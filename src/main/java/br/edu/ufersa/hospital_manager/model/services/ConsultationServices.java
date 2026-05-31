package br.edu.ufersa.hospital_manager.model.services;

import br.edu.ufersa.hospital_manager.model.DAO.ConsultationDAO;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class ConsultationServices {

    private final ConsultationDAO consultationDAO;

    public ConsultationServices() {
        this.consultationDAO = new ConsultationDAO();
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    public Consultation scheduleConsultation(Patient patient, Doctor doctor, LocalDate date, String status) throws SQLException {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        if (doctor == null)  throw new RuntimeException("Doctor cannot be null.");
        if (date == null)    throw new RuntimeException("Date cannot be null.");

        // Business rule: no past consultations
        if (date.isBefore(LocalDate.now())) {
            throw new RuntimeException("Consultation date cannot be in the past.");
        }        
        Consultation consultation = new Consultation(patient, doctor, date, status);
        consultationDAO.create(consultation); // sets the generated ID back to the entity
        return consultation;
        
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    public void removeConsultation(Consultation consultation) throws SQLException {
        if (consultation == null) throw new RuntimeException("Consultation cannot be null.");

        // Business rule: only scheduled consultations can be removed
        if (!consultation.getStatus().equals("SCHEDULED")) {
            throw new RuntimeException("Only SCHEDULED consultations can be removed.");
        }

        // Business rule: cannot remove past consultations
        if (consultation.getDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Past consultations cannot be removed.");
        }
        consultationDAO.delete(consultation);
        
    }

    // ─── Update ───────────────────────────────────────────────────────────────


    public Consultation rescheduleConsultation(Consultation consultation, LocalDate newDate) throws SQLException {
        if (consultation == null) throw new RuntimeException("Consultation cannot be null.");
        if (newDate == null)      throw new RuntimeException("New date cannot be null.");

        // Business rule: only scheduled consultations can be rescheduled
        if (!consultation.getStatus().equals("SCHEDULED")) {
            throw new RuntimeException("Only SCHEDULED consultations can be rescheduled.");
        }

        // Business rule: new date cannot be in the past
        if (newDate.isBefore(LocalDate.now())) {
            throw new RuntimeException("New consultation date cannot be in the past.");
        }

        // Entity validates the date field
        consultation.setDate(newDate);
        consultationDAO.update(consultation);
        return consultation;
        
    }

    /**
     * Updates the status of a consultation.
     * Rule: COMPLETED consultations cannot change status.
     * Rule: CANCELED consultations cannot change status.
     */
    public Consultation updateStatus(Consultation consultation, String newStatus) throws SQLException {
        if (consultation == null) throw new RuntimeException("Consultation cannot be null.");
        if (newStatus == null || newStatus.isBlank()) throw new RuntimeException("Status cannot be empty.");

        // Business rule: terminal statuses cannot be changed
        if (consultation.getStatus().equals("COMPLETED") || consultation.getStatus().equals("CANCELED")) {
            throw new RuntimeException(
                "Cannot change status of a " + consultation.getStatus() + " consultation."
            );
        }

        // Entity validates the status value (SCHEDULED, COMPLETED, CANCELED)
        consultation.setStatus(newStatus);

        consultationDAO.update(consultation);
        return consultation;
        
    }

    /**
     * Attaches a medical record to a completed consultation.
     * Rule: Only COMPLETED consultations can have a medical record attached.
     * Rule: Consultation cannot already have a medical record.
     */
    public Consultation attachMedicalRecord(Consultation consultation, MedicalRecord medicalRecord) throws SQLException {
        if (consultation == null)  throw new RuntimeException("Consultation cannot be null.");
        if (medicalRecord == null) throw new RuntimeException("Medical record cannot be null.");

        // Business rule: only completed consultations get a medical record
        if (!consultation.getStatus().equals("COMPLETED")) {
            throw new RuntimeException("A medical record can only be attached to a COMPLETED consultation.");
        }

        // Business rule: cannot overwrite an existing medical record
        if (consultation.getMedicalRecord() != null) {
            throw new RuntimeException("This consultation already has a medical record attached.");
        }

        consultation.setMedicalRecord(medicalRecord);

        consultationDAO.update(consultation);
        return consultation;

    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    public Consultation findById(long id) throws SQLException {
        if (id <= 0) throw new RuntimeException("ID must be a positive number.");
        return consultationDAO.readById(id);
        
    }

    public ArrayList<Consultation> findByPatient(Patient patient) throws SQLException {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        ArrayList<Consultation> consultations = consultationDAO.readByPatient(patient);
        if (consultations.isEmpty()) throw new RuntimeException("No consultations found for this patient.");
        return consultations;
        
    }

    public ArrayList<Consultation> findByDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) throw new RuntimeException("Doctor cannot be null.");
    
        ArrayList<Consultation> consultations = consultationDAO.readByDoctor(doctor);
        if (consultations.isEmpty()) throw new RuntimeException("No consultations found for this doctor.");
        return consultations;
        
    }

    public ArrayList<Consultation> findByStatus(String status) throws SQLException {
        if (status == null || status.isBlank()) throw new RuntimeException("Status cannot be empty.");
        ArrayList<Consultation> consultations = consultationDAO.readByStatus(status);
        if (consultations.isEmpty()) throw new RuntimeException("No consultations found with status: " + status);
        return consultations;
        
    }

    public ArrayList<Consultation> findByDate(LocalDate date) throws SQLException {
        if (date == null) throw new RuntimeException("Date cannot be null.");
        ArrayList<Consultation> consultations = consultationDAO.readByDate(date);
        if (consultations.isEmpty()) throw new RuntimeException("No consultations found for date: " + date);
        return consultations;
        
    }

    public ArrayList<Consultation> listAll() throws SQLException {
        ArrayList<Consultation> consultations = consultationDAO.listAll();
        if (consultations.isEmpty()) throw new RuntimeException("No consultations registered in the system.");
        return consultations;
    }
}