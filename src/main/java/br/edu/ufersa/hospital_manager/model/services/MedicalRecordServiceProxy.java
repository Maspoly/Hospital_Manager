package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class MedicalRecordServiceProxy implements MedicalRecordServiceContract {
    private final MedicalRecordServiceContract medicalRecordService;

    public MedicalRecordServiceProxy() {
        this.medicalRecordService = new MedicalRecordService();
    }

    @Override
    public void registerMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        ensureDoctorAccess("register a medical record");
        medicalRecordService.registerMedicalRecord(medicalRecord);
    }

    @Override
    public void updateMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        ensureDoctorAccess("update a medical record");
        medicalRecordService.updateMedicalRecord(medicalRecord);
    }

    @Override
    public void removeMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        ensureDoctorAccess("remove a medical record");
        medicalRecordService.removeMedicalRecord(medicalRecord);
    }

    @Override
    public MedicalRecord findById(long id) throws SQLException {
        ensureDoctorAccess("access medical records");
        return medicalRecordService.findById(id);
    }

    @Override
    public MedicalRecord findByPatient(Patient patient) throws SQLException {
        return medicalRecordService.findByPatient(patient);
    }

    @Override
    public ArrayList<MedicalRecord> findByDoctor(Doctor doctor) throws SQLException {
        ensureDoctorAccess("access medical records");
        return medicalRecordService.findByDoctor(doctor);
    }

    @Override
    public ArrayList<MedicalRecord> findByDate(LocalDate date) throws SQLException {
        ensureDoctorAccess("access medical records");
        return medicalRecordService.findByDate(date);
    }

    private void ensureDoctorAccess(String action) {
        if (ServiceRoleContext.getCurrentRole() != ServiceRole.DOCTOR) {
            throw new RuntimeException("Only a doctor can " + action + ".");
        }
    }
}