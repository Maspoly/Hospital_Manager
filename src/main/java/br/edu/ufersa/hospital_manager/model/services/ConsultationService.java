package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.DAO.ConsultationDAO;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class ConsultationService {
    private ConsultationDAO consultationDAO;

    public ConsultationService() {
        this.consultationDAO = new ConsultationDAO();
    }

    // ─── Scheduling ───────────────────────────────────────────────────────────

    // Creates a new consultation.
    // A doctor cannot have two consultations at the same date and time.
    public void scheduleConsultation(Consultation consultation) throws SQLException {
        if (consultation == null) {
            throw new RuntimeException("Consultation cannot be null.");
        }

        if (consultation.getPatient() == null || consultation.getDoctor() == null) {
            throw new RuntimeException("Consultation requires a patient and a doctor at creation time.");
        }

        if (consultationDAO.readByDoctorAndDateTime(consultation.getDoctor(), consultation.getDateTime()) != null) {
            throw new RuntimeException("Doctor already has a consultation at this time.");
        }

        consultation.setStatus("SCHEDULED");
        consultationDAO.create(consultation);
    }

    public void createConsultation(Consultation consultation) throws SQLException {
        if (consultation == null) {
            throw new RuntimeException("Consultation cannot be null.");
        }

        if (consultation.getPatient() == null || consultation.getDoctor() == null) {
            throw new RuntimeException("Consultation requires a patient and a doctor at creation time.");
        }

        if (consultation.getStatus() == null || consultation.getStatus().isBlank()) {
            consultation.setStatus("SCHEDULED");
        }

        if (consultationDAO.readByDoctorAndDateTime(consultation.getDoctor(), consultation.getDateTime()) != null) {
            throw new RuntimeException("Doctor already has a consultation at this time.");
        }

        consultationDAO.create(consultation);
    }

    // Cancels a consultation without removing it from the database.
    // The consultation status becomes CANCELED.
    public void cancelConsultation(Consultation consultation) throws SQLException {
        if (consultation == null) {
            throw new RuntimeException("Consultation cannot be null.");
        }

        if (consultationDAO.readById(consultation.getId()) == null) {
            throw new RuntimeException("Consultation not found.");
        }

        consultation.setStatus("CANCELED");
        consultationDAO.update(consultation);
    }

    // Marks a consultation as completed.
    // The consultation remains stored in the database.
    public void completeConsultation(Consultation consultation) throws SQLException {
        if (consultation == null) {
            throw new RuntimeException("Consultation cannot be null.");
        }

        if (consultationDAO.readById(consultation.getId()) == null) {
            throw new RuntimeException("Consultation not found.");
        }

        consultation.setStatus("COMPLETED");
        consultationDAO.update(consultation);
    }

    // ─── Update And Removal ───────────────────────────────────────────────────

    // Updates consultation information.
    public void updateConsultation(Consultation consultation) throws SQLException {
        if (consultation == null) {
            throw new RuntimeException("Consultation cannot be null.");
        }

        if (consultationDAO.readById(consultation.getId()) == null) {
            throw new RuntimeException("Consultation not found.");
        }

        consultationDAO.update(consultation);
    }

    // Permanently removes a consultation from the database.
    // Intended for administrative corrections.
    public void removeConsultation(Consultation consultation) throws SQLException {
        if (consultation == null) {
            throw new RuntimeException("Consultation cannot be null.");
        }

        if (consultationDAO.readById(consultation.getId()) == null) {
            throw new RuntimeException("Consultation not found.");
        }

        consultationDAO.delete(consultation);
    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    public ArrayList<Consultation> findByDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }
        if (doctor.getId() <= 0) {
            throw new RuntimeException("Doctor not found.");
        }

        return consultationDAO.readByDoctor(doctor);
    }

    public ArrayList<Consultation> findByPatient(Patient patient) throws SQLException {
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }
        if (patient.getId() <= 0) {
            throw new RuntimeException("Patient not found.");
        }

        return consultationDAO.readByPatient(patient);
    }

    public ArrayList<Consultation> findByDateTime(LocalDateTime dateTime) throws SQLException {
        if (dateTime == null) {
            throw new RuntimeException("Date time cannot be null.");
        }

        return consultationDAO.readByDateTime(dateTime);
    }

    public Consultation findById(long id) throws SQLException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }

        return consultationDAO.readById(id);
    }

    public ArrayList<Consultation> listAll() throws SQLException {
        return consultationDAO.listAll();
    }
}