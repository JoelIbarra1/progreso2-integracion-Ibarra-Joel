package edu.udla.integracion.progreso2.service;

import edu.udla.integracion.progreso2.model.CitaRequest;
import org.springframework.stereotype.Service;

@Service
public class CitaValidationService {
    public String validarCita(CitaRequest cita) {
        if (cita.getIdCita() == null || cita.getIdCita().trim().isEmpty())
            return "idCita obligatorio";
        if (cita.getPaciente() == null || cita.getPaciente().trim().isEmpty())
            return "paciente obligatorio";
        if (cita.getCorreo() == null || cita.getCorreo().trim().isEmpty())
            return "correo obligatorio";
        if (cita.getEspecialidad() == null || cita.getEspecialidad().trim().isEmpty())
            return "especialidad obligatoria";
        if (cita.getFechaCita() == null || cita.getFechaCita().trim().isEmpty())
            return "fechaCita obligatoria";
        if (cita.getSede() == null || cita.getSede().trim().isEmpty())
            return "sede obligatoria";
        if (cita.getValor() == null || cita.getValor() <= 0)
            return "valor debe ser mayor a 0";
        return null; // Válida
    }
}