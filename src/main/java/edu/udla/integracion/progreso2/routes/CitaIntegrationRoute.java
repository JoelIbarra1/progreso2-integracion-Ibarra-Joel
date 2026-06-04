package edu.udla.integracion.progreso2.routes;

import edu.udla.integracion.progreso2.model.CitaRequest;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CitaIntegrationRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Manejo global de excepciones para errores de ruteo
        onException(Exception.class)
                .handled(true)
                .log("Error inesperado en ruta: ${exception.message}")
                .setHeader("motivo", simple("${exception.message}"))
                .to("direct:errorControlado");

        // Ruta principal
        from("direct:procesarCita")
                .log("Iniciando integración para cita: ${body.idCita}")
                .multicast().parallelProcessing()
                .to("direct:facturacion")
                .to("direct:eventos")
                .to("direct:auditoria");

        // RF2: Point-to-Point a Facturación
        from("direct:facturacion")
                .process(exchange -> {
                    CitaRequest cita = exchange.getIn().getBody(CitaRequest.class);
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("idCita", cita.getIdCita());
                    msg.put("paciente", cita.getPaciente());
                    msg.put("especialidad", cita.getEspecialidad());
                    msg.put("valor", cita.getValor());
                    msg.put("tipoMensaje", "COMANDO_FACTURAR_CITA");
                    exchange.getIn().setBody(msg);
                })
                .marshal().json(JsonLibrary.Jackson)
                .to("spring-rabbitmq:amq.direct?queues=billing.queue&routingKey=billing.queue");

        // RF3: Publish/Subscribe a Notificaciones y Analítica
        from("direct:eventos")
                .process(exchange -> {
                    CitaRequest cita = exchange.getIn().getBody(CitaRequest.class);
                    Map<String, Object> evento = new HashMap<>();
                    evento.put("idCita", cita.getIdCita());
                    evento.put("paciente", cita.getPaciente());
                    evento.put("correo", cita.getCorreo());
                    evento.put("especialidad", cita.getEspecialidad());
                    evento.put("fechaCita", cita.getFechaCita());
                    evento.put("sede", cita.getSede());
                    evento.put("tipoEvento", "CITA_CONFIRMADA");
                    exchange.getIn().setBody(evento);
                })
                .marshal().json(JsonLibrary.Jackson)
                .to("spring-rabbitmq:appointments.events?exchangeType=fanout");

        // RF4: Generación de archivo CSV para Auditoría
        from("direct:auditoria")
                .process(exchange -> {
                    CitaRequest cita = exchange.getIn().getBody(CitaRequest.class);
                    String csvLine = String.format("%s,%s,%s,%s,%s,%s,%.2f\n",
                            cita.getIdCita(), cita.getPaciente(), cita.getCorreo(),
                            cita.getEspecialidad(), cita.getFechaCita(), cita.getSede(), cita.getValor());
                    exchange.getIn().setBody(csvLine);
                })
                .to("file:data/outbox?fileName=auditoria-citas.csv&fileExist=Append");

        // RF5: Manejo básico de errores controlados
        from("direct:errorControlado")
                .setBody(simple("${date:now:yyyy-MM-dd HH:mm:ss} | Motivo: ${header.motivo} | Payload: ${body}\n"))
                .to("file:data/errors?fileName=citas-rechazadas.log&fileExist=Append")
                .log("Error registrado en archivo de log.");
    }
}