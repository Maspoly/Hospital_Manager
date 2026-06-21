package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public interface MedicalRecordServiceContract {
    void registerMedicalRecord(MedicalRecord medicalRecord) throws SQLException;

    void updateMedicalRecord(MedicalRecord medicalRecord) throws SQLException;

    void removeMedicalRecord(MedicalRecord medicalRecord) throws SQLException;

    MedicalRecord findById(long id) throws SQLException;

    MedicalRecord findByPatient(Patient patient) throws SQLException;

    ArrayList<MedicalRecord> findByDoctor(Doctor doctor) throws SQLException;

    ArrayList<MedicalRecord> findByDate(LocalDate date) throws SQLException;
}