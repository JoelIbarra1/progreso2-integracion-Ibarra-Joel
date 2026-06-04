package edu.udla.integracion.progreso2.controller;

import edu.udla.integracion.progreso2.model.CitaRequest;
import edu.udla.integracion.progreso2.service.CitaValidationService;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CitaController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CitaValidationService validationService;

    @PostMapping("/citas")
    public ResponseEntity<?> registrarCita(@RequestBody CitaRequest cita) {
        String error = validationService.validarCita(cita);

        if (error != null) {
            // Enviar a la ruta de errores en caso de fallo (RF5)
            producerTemplate.sendBodyAndHeader("direct:errorControlado", cita.toString(), "motivo", error);
            Map<String, String> respuestaError = new HashMap<>();
            respuestaError.put("error", error);
            return ResponseEntity.badRequest().body(respuestaError);
        }

        // Iniciar flujo de integración (RF1)
        producerTemplate.sendBody("direct:procesarCita", cita);

        Map<String, String> respuestaExitosa = new HashMap<>();
        respuestaExitosa.put("mensaje", "Cita registrada y en proceso de integración");
        return ResponseEntity.ok(respuestaExitosa);
    }
}