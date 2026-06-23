package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class ConsultationServiceProxy implements IsServiceProxy {
    private final ConsultationService consultationService;

    public ConsultationServiceProxy(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    public void createConsultation(Consultation consultation) throws SQLException {
        ensureManagerAccess("create a consultation");
        consultationService.createConsultation(consultation);
    }

    public void scheduleConsultation(Consultation consultation) throws SQLException {
        ensureManagerAccess("schedule a consultation");
        consultationService.scheduleConsultation(consultation);
    }

    public void cancelConsultation(Consultation consultation) throws SQLException {
        ensureManagerAccess("cancel a consultation");
        consultationService.cancelConsultation(consultation);
    }

    public void completeConsultation(Consultation consultation) throws SQLException {
        ensureManagerAccess("complete a consultation");
        consultationService.completeConsultation(consultation);
    }

    public void updateConsultation(Consultation consultation) throws SQLException {
        ensureManagerAccess("update a consultation");
        consultationService.updateConsultation(consultation);
    }

    public void removeConsultation(Consultation consultation) throws SQLException {
        consultationService.removeConsultation(consultation);
    }

    public Consultation findById(long id) throws SQLException {
        return consultationService.findById(id);
    }

    public ArrayList<Consultation> findByDoctor(Doctor doctor) throws SQLException {
        return consultationService.findByDoctor(doctor);
    }

    public ArrayList<Consultation> findByPatient(Patient patient) throws SQLException {
        return consultationService.findByPatient(patient);
    }

    public ArrayList<Consultation> findByDateTime(LocalDateTime dateTime) throws SQLException {
        return consultationService.findByDateTime(dateTime);
    }

    public ArrayList<Consultation> listAll() throws SQLException {
        return consultationService.listAll();
    }

    private void ensureManagerAccess(String action) {
        if ((ServiceRoleContext.getCurrentRole() != ServiceRole.MANAGER) && (ServiceRoleContext.getCurrentRole() != ServiceRole.PATIENT)) {
            throw new RuntimeException("You are not authorized to " + action + ".");
        }
    }
}