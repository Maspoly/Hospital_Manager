package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.DAO.MedicalRecordDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class MedicalRecordService {
    private MedicalRecordDAO medicalRecordDAO;

    public MedicalRecordService() {
        this.medicalRecordDAO = new MedicalRecordDAO();
    }

    // Update medical record
    public void updateMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecordDAO.readById(medicalRecord.getId()) == null) {
            throw new RuntimeException("Medical record not found.");
        }

        if (medicalRecord != null) {
            medicalRecordDAO.update(medicalRecord);
        }
    }

    // Find medical record by ID
    public MedicalRecord findById(long id) throws SQLException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
        return medicalRecordDAO.readById(id);
    }

    // Register new medical record
    public void registerMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }

        if (medicalRecordDAO.readById(medicalRecord.getId()) != null) {
            throw new RuntimeException("A medical record with this ID already exists.");
        }
        medicalRecordDAO.create(medicalRecord);
    }
    
    // Remove medical record
    public void removeMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }
        if (medicalRecordDAO.readById(medicalRecord.getId()) == null) {
            throw new RuntimeException("Medical record not found.");
        }

        medicalRecordDAO.delete(medicalRecord);

    }

    public MedicalRecord findByPatient(Patient patient) throws SQLException {
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }
        // Verify if the patient has a valid ID before querying the database
        if (patient.getId() <= 0) {
            throw new RuntimeException("Patient not found.");
        }

        return medicalRecordDAO.readByPatient(patient);
    }

    public ArrayList<MedicalRecord> findByDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }
        // Verify if the doctor has a valid ID before querying the database
        if (doctor.getId() <= 0) {
            throw new RuntimeException("Doctor not found.");
        }
        return medicalRecordDAO.readByDoctor(doctor);
    }

    public MedicalRecord findByDate(LocalDate date) throws SQLException {
        if (date == null) {
            throw new RuntimeException("Date cannot be null.");
        }
        return medicalRecordDAO.readByDate(date); 
    }
}
