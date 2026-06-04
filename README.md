# Integración de Sistemas - Progreso 2
**Nombre del estudiante:** Joel Ibarra

## Descripción
Solución de integración para "Salud360" que automatiza el flujo de registro de citas médicas enviando datos a facturación (RabbitMQ Point-to-Point), notificaciones/analítica (RabbitMQ Publish/Subscribe) y auditoría (Archivo CSV) orquestado con Apache Camel.

## Tecnologías Utilizadas
- Java 17 / Spring Boot 3
- Apache Camel 4
- RabbitMQ (Docker)
- Maven

## Instrucciones de ejecución
1. Levantar RabbitMQ: `docker-compose up -d`
2. Ejecutar la aplicación: `mvn spring-boot:run`

## Endpoint Disponible
`POST http://localhost:8080/api/citas`
**Content-Type:** application/json

## Ejemplo Request Válido
```json
{
  "idCita": "CITA-1001",
  "paciente": "Ana Torres",
  "correo": "ana.torres@email.com",
  "especialidad": "Cardiología",
  "fechaCita": "2026-06-15",
  "sede": "Centro Norte",
  "valor": 45.50
}
```
## Ejemplo Request Invalido
```json
{
  "idCita": "",
  "paciente": "Ana Torres",
  "valor": -10
}