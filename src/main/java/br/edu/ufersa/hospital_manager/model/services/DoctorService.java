package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.DAO.ConsultationDAO;
import br.edu.ufersa.hospital_manager.model.DAO.DoctorDAO;
import br.edu.ufersa.hospital_manager.model.exceptions.DuplicateEntryException;
import br.edu.ufersa.hospital_manager.model.exceptions.EntityNotFoundException;
import br.edu.ufersa.hospital_manager.model.DAO.MedicalRecordDAO;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;

public class DoctorService implements DoctorServiceContract {
    private DoctorDAO doctorDAO;

    public DoctorService() {
        this.doctorDAO = new DoctorDAO();
    }

    // ─── Doctor Management ───────────────────────────────────────────────────

    // Registers a new doctor.
    @Override
    public void registerDoctor(Doctor doctor) throws SQLException {
        DoctorDAO doctorDAO = new DoctorDAO();

        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }

        if (doctorDAO.readByCPF(doctor.getCPF()) != null) {
            throw new DuplicateEntryException("CPF", doctor.getCPF());
        }

        if (doctorDAO.readByCouncilCode(doctor.getCouncilCode()) != null) {
            throw new DuplicateEntryException("Código de Conselho", doctor.getCouncilCode());
        }

        doctorDAO.create(doctor);
    }

    // Removes a doctor from the system.
    @Override
    public void removeDoctor(Doctor doctor) throws SQLException {
        DoctorDAO doctorDAO = new DoctorDAO();
        ConsultationDAO consultationDAO = new ConsultationDAO();
        MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }

        if (doctorDAO.readById(doctor.getId()) == null) {
            throw new EntityNotFoundException("Médico", String.valueOf(doctor.getId()));
        }

        ArrayList<Consultation> consultations = consultationDAO.readByDoctor(doctor);
        if (!consultations.isEmpty()) {
            consultationDAO.detachDoctor(doctor);
        }

        medicalRecordDAO.detachDoctor(doctor);

        doctorDAO.delete(doctor);
    }


    // ─── Update ───────────────────────────────────────────────────────────────

    // Updates doctor's information only if the doctor already exists.
    @Override
    public void updateDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }
        if (doctorDAO.readById(doctor.getId()) == null) {
            throw new EntityNotFoundException("Médico", String.valueOf(doctor.getId()));
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

    @Override
    public Doctor findByCouncilCode(String councilCode) throws SQLException {
        if (councilCode == null || councilCode.isBlank()) {
            throw new RuntimeException("Council code cannot be null or empty.");
        }
        return doctorDAO.readByCouncilCode(councilCode);
    }

    @Override
    public ArrayList<Doctor> listAll() throws SQLException {
        return doctorDAO.listAll();
    }

}