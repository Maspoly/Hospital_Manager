package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import br.edu.ufersa.hospital_manager.model.DAO.MedicalRecordDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class MedicalRecordService {
    private MedicalRecordDAO medicalRecordDAO;

    public MedicalRecordService() {
        this.medicalRecordDAO = new MedicalRecordDAO();
    }

    // ─── Registration ─────────────────────────────────────────────────────────

    // Creates a new medical record.
    public void registerMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }
        
        if (medicalRecordDAO.readById(medicalRecord.getId()) != null) {
            throw new RuntimeException("A medical record with this ID already exists.");
        }
        medicalRecordDAO.create(medicalRecord);
    }

    // ─── Update And Removal ───────────────────────────────────────────────────

    // Updates an existing medical record.
    public void updateMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }
        if (medicalRecordDAO.readById(medicalRecord.getId()) == null) {
            throw new RuntimeException("Medical record not found.");
        }

        medicalRecordDAO.update(medicalRecord);
    }

    // Permanently removes a medical record from the database.
    public void removeMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }
        if (medicalRecordDAO.readById(medicalRecord.getId()) == null) {
            throw new RuntimeException("Medical record not found.");
        }

        medicalRecordDAO.delete(medicalRecord);

    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    public MedicalRecord findById(long id) throws SQLException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
        return medicalRecordDAO.readById(id);
    }

    public MedicalRecord findByPatient(Patient patient) throws SQLException {
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }

        if (patient.getId() <= 0) {
            throw new RuntimeException("Patient not found.");
        }

        return medicalRecordDAO.readByPatient(patient);
    }

    public ArrayList<MedicalRecord> findByDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }

        if (doctor.getId() <= 0) {
            throw new RuntimeException("Doctor not found.");
        }
        return medicalRecordDAO.readByDoctor(doctor);
    }

    public MedicalRecord findByDate(LocalDateTime date) throws SQLException {
        if (date == null) {
            throw new RuntimeException("Date cannot be null.");
        }
        return medicalRecordDAO.readByDate(date);
        
    }
    public MedicalRecord updateObservation(
        MedicalRecord record,
        String observation)
        throws SQLException {

    record.setObservation(observation);

    medicalRecordDAO.update(record);

    return record;
}
}