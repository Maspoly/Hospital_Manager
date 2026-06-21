package br.edu.ufersa.hospital_manager.model.services;

import java.time.LocalDateTime;
import br.edu.ufersa.hospital_manager.model.DAO.ConsultationDAO;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

import java.sql.SQLException;
import java.util.ArrayList;

public class ConsultationService {

    private final ConsultationDAO consultationDAO;

    public ConsultationService() {
        this.consultationDAO = new ConsultationDAO();
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    public Consultation scheduleConsultation(Patient patient, Doctor doctor, LocalDateTime date, String status) throws SQLException {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        if (doctor == null)  throw new RuntimeException("Doctor cannot be null.");
        if (date == null)    throw new RuntimeException("Date cannot be null.");

<<<<<<< HEAD
        // Business rule: no past consultations
        if (date.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Consultation date cannot be in the past.");
        }        
        Consultation consultation = new Consultation(patient, doctor, date, status);
        consultationDAO.create(consultation); // sets the generated ID back to the entity
        return consultation;
        
    }

    // ─── Delete ───────────────────────────────────────────────────────────────
=======
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

        consultationDAO.create(consultation);
    }

    // Cancels a consultation without removing it from the database.
    // The consultation status becomes CANCELED.
    public void cancelConsultation(Consultation consultation) throws SQLException {
        if (consultation == null) {
            throw new RuntimeException("Consultation cannot be null.");
        }
>>>>>>> 96ad7c6 (Linked screens to data base)

    public void removeConsultation(Consultation consultation) throws SQLException {
        if (consultation == null) throw new RuntimeException("Consultation cannot be null.");

        // Business rule: only scheduled consultations can be removed
        if (!consultation.getStatus().equals("SCHEDULED")) {
            throw new RuntimeException("Only SCHEDULED consultations can be removed.");
        }

        // Business rule: cannot remove past consultations
        if (consultation.getDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Past consultations cannot be removed.");
        }
        consultationDAO.delete(consultation);
        
    }

    // ─── Update ───────────────────────────────────────────────────────────────


    public Consultation rescheduleConsultation(Consultation consultation, LocalDateTime newDate) throws SQLException {
        if (consultation == null) throw new RuntimeException("Consultation cannot be null.");
        if (newDate == null)      throw new RuntimeException("New date cannot be null.");

        // Business rule: only scheduled consultations can be rescheduled
        if (!consultation.getStatus().equals("SCHEDULED")) {
            throw new RuntimeException("Only SCHEDULED consultations can be rescheduled.");
        }

        // Business rule: new date cannot be in the past
        if (newDate.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("New consultation date cannot be in the past.");
        }

        // Entity validates the date field
        consultation.setDateTime(newDate);
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

    public ArrayList<Consultation> findByDate(LocalDateTime date) throws SQLException {
        if (date == null) throw new RuntimeException("Date cannot be null.");
        ArrayList<Consultation> consultations = consultationDAO.readByDateTime(date);
        if (consultations.isEmpty()) throw new RuntimeException("No consultations found for date: " + date);
        return consultations;
        
    }

    public ArrayList<Consultation> listAll() throws SQLException {
        ArrayList<Consultation> consultations = consultationDAO.listAll();
        if (consultations.isEmpty()) throw new RuntimeException("No consultations registered in the system.");
        return consultations;
    }

    public ArrayList<Consultation> listAll() throws SQLException {
        return consultationDAO.listAll();
    }
}