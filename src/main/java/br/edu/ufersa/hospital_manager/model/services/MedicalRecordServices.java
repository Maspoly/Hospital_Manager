package br.edu.ufersa.hospital_manager.model.services;

import br.edu.ufersa.hospital_manager.model.DAO.MedicalRecordDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;


public class MedicalRecordServices {

    private final MedicalRecordDAO medicalRecordDAO;

    public MedicalRecordServices() {
        this.medicalRecordDAO = new MedicalRecordDAO();
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    /**
     * Registers a new medical record.
     * Rule: A patient cannot have two medical records on the same day.
     */
    public MedicalRecord registerMedicalRecord(String observation, Doctor doctor, Patient patient) throws SQLException {
        // Business rule: no duplicate record for the same patient on the same day
        MedicalRecord existing = medicalRecordDAO.readByPatient(patient);
        if (existing.getDate().isEqual(LocalDate.now())) {
            throw new RuntimeException(
                "Patient already has a medical record registered today (" + LocalDate.now() + ")."
            );
        }  
        // Entity validates: observation not empty, doctor/patient not null, date set automatically
        MedicalRecord record = new MedicalRecord(observation, doctor, patient);
        medicalRecordDAO.create(record); // sets the generated ID back to the entity
        return record;
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    /**
     * Removes a medical record.
     * Rule: Only records created today can be deleted.
     */
    public void removeMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) throw new RuntimeException("Medical record cannot be null.");

        // Business rule: only today's records can be deleted
        if (!medicalRecord.getDate().isEqual(LocalDate.now())) {
            throw new RuntimeException("Only medical records created today can be deleted.");
        }
        medicalRecordDAO.delete(medicalRecord);
        
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    /**
     * Updates the observation of a medical record.
     * Rule: Records older than 30 days cannot be edited.
     */
    public MedicalRecord updateObservation(MedicalRecord medicalRecord, String newObservation) throws SQLException {
        if (medicalRecord == null) throw new RuntimeException("Medical record cannot be null.");
        if (newObservation == null || newObservation.isBlank())
            throw new RuntimeException("New observation cannot be empty.");

        // Business rule: records older than 30 days are immutable
        if (medicalRecord.getDate().isBefore(LocalDate.now().minusDays(30))) {
            throw new RuntimeException("Medical records older than 30 days cannot be edited.");
        }

        // Entity validates observation format (not empty)
        medicalRecord.setObservation(newObservation);

        medicalRecordDAO.update(medicalRecord);
        return medicalRecord;

    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    public MedicalRecord findById(long id) throws SQLException {
        if (id <= 0) throw new RuntimeException("ID must be a positive number.");
        return medicalRecordDAO.readById(id);
    }

    public MedicalRecord findByPatient(Patient patient) throws SQLException {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        return medicalRecordDAO.readByPatient(patient);
    }

    public MedicalRecord findByDate(LocalDate date) throws SQLException {
        if (date == null) throw new RuntimeException("Date cannot be null.");
        if (date.isAfter(LocalDate.now())) throw new RuntimeException("Date cannot be in the future.");
        return medicalRecordDAO.readByDate(date);
    }

    public ArrayList<MedicalRecord> findByDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) throw new RuntimeException("Doctor cannot be null.");
            ArrayList<MedicalRecord> records = medicalRecordDAO.readByDoctor(doctor);
            if (records.isEmpty()) throw new RuntimeException("No medical records found for this doctor.");
            return records;
    }

    public ArrayList<MedicalRecord> listAll() throws SQLException {
        ArrayList<MedicalRecord> records = medicalRecordDAO.listAll();
        if (records.isEmpty()) throw new RuntimeException("No medical records registered in the system.");
        return records;
    }
}