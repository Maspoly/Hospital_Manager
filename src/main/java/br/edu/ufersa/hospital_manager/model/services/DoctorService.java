package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;

import br.edu.ufersa.hospital_manager.model.DAO.DoctorDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;

public class DoctorService implements FindServices<Doctor> {
    private DoctorDAO doctorDAO;

    public DoctorService() {
        this.doctorDAO = new DoctorDAO();
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    // Updates doctor's information only if the doctor already exists.
    public void updateDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }
        if (doctorDAO.readById(doctor.getId()) == null) {
            throw new RuntimeException("Doctor not found.");
        }
        doctorDAO.update(doctor);
    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    @Override
    public Doctor findById(long id) throws SQLException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
        return doctorDAO.readById(id);
    }

    @Override
    public Doctor findByName(String name) throws SQLException {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Name cannot be null or empty.");
        }
        return doctorDAO.readByName(name);
    }

    @Override
    public Doctor findByCPF(String cpf) throws SQLException {
        if (cpf == null || cpf.isBlank()) {
            throw new RuntimeException("CPF cannot be null or empty.");
        }
        return doctorDAO.readByCPF(cpf);
    }

    // ─── Medical Records ──────────────────────────────────────────────────────

    // Doctor writes a new medical record through MedicalRecordService.
    public void registerMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        MedicalRecordService medicalRecordService = new MedicalRecordService();
        medicalRecordService.registerMedicalRecord(medicalRecord);
    }

    // Doctor updates an existing medical record through MedicalRecordService.
    public void updateMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        MedicalRecordService medicalRecordService = new MedicalRecordService();
        medicalRecordService.updateMedicalRecord(medicalRecord);
    }

    // Doctor removes a medical record through MedicalRecordService.
    public void deleteMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        MedicalRecordService medicalRecordService = new MedicalRecordService();
        medicalRecordService.removeMedicalRecord(medicalRecord);
    }
}