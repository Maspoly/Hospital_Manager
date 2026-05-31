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

    public void updateDoctor(Doctor doctor) throws SQLException {
        if (doctorDAO.readById(doctor.getId()) == null) {
            throw new RuntimeException("Doctor not found.");
        }

        if (doctor != null) {
            doctorDAO.update(doctor);
        }
    }

    @Override
    public Doctor findById(long id) throws Exception {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
        return doctorDAO.readById(id);
    }

    @Override
    public Doctor findByName(String name) throws Exception {
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Name cannot be null or empty.");
        }
        return doctorDAO.readByName(name);
    }

    @Override
    public Doctor findByCPF(String cpf) throws Exception {
        if (cpf == null || cpf.isEmpty()) {
            throw new RuntimeException("CPF cannot be null or empty.");
        }
        return doctorDAO.readByCPF(cpf);
    }

    public void registerMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        MedicalRecordService medicalRecordService = new MedicalRecordService();
        medicalRecordService.registerMedicalRecord(medicalRecord);
    }

    public void updateMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        MedicalRecordService medicalRecordService = new MedicalRecordService();
        medicalRecordService.updateMedicalRecord(medicalRecord);
    }

    public void deleteMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        MedicalRecordService medicalRecordService = new MedicalRecordService();
        medicalRecordService.removeMedicalRecord(medicalRecord);
    }
}