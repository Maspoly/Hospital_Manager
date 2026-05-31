package br.edu.ufersa.hospital_manager.model.services;

import br.edu.ufersa.hospital_manager.model.DAO.DoctorDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import java.sql.SQLException;

import java.util.ArrayList;

public class DoctorServices implements FindServices<Doctor> {

    private final DoctorDAO doctorDAO;

    // Recebe a Connection do Controller/Main, igual ao padrão que o projeto já usa
    public DoctorServices() {
        this.doctorDAO = new DoctorDAO();
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    /**
     * Updates doctor's information.
     * Rule: If the CRM changes, verify uniqueness.
     */
    public Doctor updateDoctor(Doctor doctor) throws SQLException {
            if (doctor == null) throw new RuntimeException("Doctor cannot be null.");
            doctorDAO.update(doctor);
            return doctor;        
    }

    // ─── Searches ───────────────────────────────────────────────────────────────
@Override
    public Doctor findById(long id) throws SQLException {
            Doctor doctor = doctorDAO.readById(id);
            if (doctor == null) throw new RuntimeException("Doctor with ID " + id + " not found.");
            return doctor;            
    }
@Override
    public Doctor findByCPF(String cpf) throws SQLException {
            Doctor doctor = doctorDAO.readByCPF(cpf);
            if (doctor == null) throw new RuntimeException("Doctor with CPF " + cpf + " not found.");
            return doctor;
        
    }
    public Doctor findByCouncilCode(String councilCode) throws SQLException {
            if (councilCode == null || councilCode.isBlank())
                throw new RuntimeException("CRM cannot be empty.");
            
            Doctor doctor = doctorDAO.readByCouncilCode(councilCode);
            if (doctor == null) throw new RuntimeException("CRM " + councilCode + " not found.");
            return doctor;
        
    }
    
@Override
    public Doctor findByName(String name) throws SQLException {
            Doctor doctor = doctorDAO.readByName(name);
            if (doctor == null) throw new RuntimeException("Doctor '" + name + "' not found.");
            return doctor;

    }

    public ArrayList<Doctor> listAll() throws SQLException {
        if (doctorDAO.listAll().isEmpty()) {
            throw new RuntimeException("No doctors found in the system.");
        }
            return doctorDAO.listAll();
        
    }

    // Method to edit doctor's personal data
    public void editPersonalData(Doctor doctor, String name, String cpf, String address, int consultationValue, String councilCode) throws RuntimeException {

        // Validations
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name cannot be empty.");
        }
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new RuntimeException("CPF must contain exactly 11 numeric digits");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new RuntimeException("Address cannot be empty.");
        }
        if (councilCode == null || councilCode.trim().isEmpty()) {
            throw new RuntimeException("Council code cannot be empty.");
        }

        // Update data
        doctor.setName(name);
        doctor.setCPF(cpf);
        doctor.setAddress(address);
        doctor.setConsultationValue(consultationValue);
        doctor.setCouncilCode(councilCode);
    }
}