package br.edu.ufersa.hospital_manager.model.services;

import br.edu.ufersa.hospital_manager.model.DAO.DoctorDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;

import java.sql.SQLException;
import java.util.ArrayList;

public class DoctorServices implements FindServices<Doctor> {

    private final DoctorDAO doctorDAO;

    public DoctorServices() {
        this.doctorDAO = new DoctorDAO();
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    public Doctor updateDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) throw new RuntimeException("Doctor cannot be null.");
        doctorDAO.update(doctor);
        return doctor;
    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    @Override
    public Doctor findById(long id) throws SQLException {
        // readById lança SQLException se não encontrar — deixamos propagar ao Main
        return doctorDAO.readById(id);
    }

    @Override
    public Doctor findByCPF(String cpf) throws SQLException {
        return doctorDAO.readByCPF(cpf);
    }

    public Doctor findByCouncilCode(String councilCode) throws SQLException {
        if (councilCode == null || councilCode.isBlank())
            throw new RuntimeException("CRM cannot be empty.");
        return doctorDAO.readByCouncilCode(councilCode);
    }

    @Override
    public Doctor findByName(String name) throws SQLException {
        return doctorDAO.readByName(name);
    }

    public ArrayList<Doctor> listAll() throws SQLException {
        ArrayList<Doctor> doctors = doctorDAO.listAll();
        if (doctors.isEmpty())
            throw new RuntimeException("No doctors found in the system.");
        return doctors;
    }

    // ─── Edit personal data ───────────────────────────────────────────────────

    public void editPersonalData(Doctor doctor, String name, String cpf,
                                  String address, int consultationValue,
                                  String councilCode) {
        if (name == null || name.trim().isEmpty())
            throw new RuntimeException("Name cannot be empty.");
        if (cpf == null || !cpf.matches("\\d{11}"))
            throw new RuntimeException("CPF must contain exactly 11 numeric digits.");
        if (address == null || address.trim().isEmpty())
            throw new RuntimeException("Address cannot be empty.");
        if (councilCode == null || councilCode.trim().isEmpty())
            throw new RuntimeException("Council code cannot be empty.");

        doctor.setName(name);
        doctor.setCPF(cpf);
        doctor.setAddress(address);
        doctor.setConsultationValue(consultationValue);
        doctor.setCouncilCode(councilCode);
    }
}
