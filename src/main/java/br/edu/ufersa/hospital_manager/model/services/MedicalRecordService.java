package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import br.edu.ufersa.hospital_manager.model.DAO.MedicalRecordDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class MedicalRecordService implements MedicalRecordServiceContract {
    private MedicalRecordDAO medicalRecordDAO;

    public MedicalRecordService() {
        this.medicalRecordDAO = new MedicalRecordDAO();
    }

    // ─── Registration ─────────────────────────────────────────────────────────

    // Creates a new medical record.
    @Override
    public void registerMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }
<<<<<<< HEAD
        
=======

        if (ServiceRoleContext.getCurrentUser() instanceof Doctor) {
            medicalRecord.setDoctor((Doctor) ServiceRoleContext.getCurrentUser());
        }

>>>>>>> 96ad7c6 (Linked screens to data base)
        if (medicalRecordDAO.readById(medicalRecord.getId()) != null) {
            throw new RuntimeException("A medical record with this ID already exists.");
        }
        medicalRecordDAO.create(medicalRecord);
    }

    // ─── Update And Removal ───────────────────────────────────────────────────

    // Updates an existing medical record.
    @Override
    public void updateMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }
        if (medicalRecordDAO.readById(medicalRecord.getId()) == null) {
            throw new RuntimeException("Medical record not found.");
        }

        if (ServiceRoleContext.getCurrentUser() instanceof Doctor) {
            medicalRecord.setDoctor((Doctor) ServiceRoleContext.getCurrentUser());
        }

        medicalRecord.setDate();

        medicalRecordDAO.update(medicalRecord);
    }

    // Permanently removes a medical record from the database.
    @Override
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
    @Override
    public MedicalRecord findById(long id) throws SQLException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
        return medicalRecordDAO.readById(id);
    }
    @Override
    public MedicalRecord findByPatient(Patient patient) throws SQLException {
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }

        if (patient.getId() <= 0) {
            throw new RuntimeException("Patient not found.");
        }

        return medicalRecordDAO.readByPatient(patient);
    }
    @Override
    public ArrayList<MedicalRecord> findByDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }

        if (doctor.getId() <= 0) {
            throw new RuntimeException("Doctor not found.");
        }
        return medicalRecordDAO.readByDoctor(doctor);
    }
<<<<<<< HEAD

    public MedicalRecord findByDate(LocalDateTime date) throws SQLException {
=======
    @Override
    public ArrayList<MedicalRecord> findByDate(LocalDate date) throws SQLException {
>>>>>>> 96ad7c6 (Linked screens to data base)
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