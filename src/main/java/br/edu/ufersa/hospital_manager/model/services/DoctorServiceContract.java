package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Doctor;

public interface DoctorServiceContract extends FindServices<Doctor> {
    void registerDoctor(Doctor doctor) throws SQLException;

    void removeDoctor(Doctor doctor) throws SQLException;

    void updateDoctor(Doctor doctor) throws SQLException;

    ArrayList<Doctor> listAll() throws SQLException;

    Doctor findByCouncilCode(String councilCode) throws SQLException;
}