package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;

import br.edu.ufersa.hospital_manager.model.DAO.DoctorDAO;
import br.edu.ufersa.hospital_manager.model.DAO.ManagerDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Manager;

public class ManagerService implements FindServices<Manager> {
    private ManagerDAO managerDAO;

    public ManagerService() {
        this.managerDAO = new ManagerDAO();
    }

    public void registerDoctor(Doctor doctor) throws SQLException {
        DoctorDAO doctorDAO = new DoctorDAO();

        // DAO lança SQLException quando NÃO encontra.
        // Se NÃO lançou = já existe = bloqueamos.
        try {
            doctorDAO.readByCPF(doctor.getCPF());
            throw new RuntimeException("A doctor with this CPF already exists.");
        } catch (SQLException ignored) { /* não encontrou = pode continuar */ }

        try {
            doctorDAO.readByCouncilCode(doctor.getCouncilCode());
            throw new RuntimeException("A doctor with this council code already exists.");
        } catch (SQLException ignored) { /* não encontrou = pode continuar */ }

        doctorDAO.create(doctor);
    }

    public void removeDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) throw new RuntimeException("Doctor cannot be null.");
        DoctorDAO doctorDAO = new DoctorDAO();
        doctorDAO.delete(doctor);
    }

    public void registerManager(Manager manager) throws SQLException {
        try {
            managerDAO.readByCPF(manager.getCPF());
            throw new RuntimeException("A manager with this CPF already exists.");
        } catch (SQLException ignored) { /* não encontrou = pode continuar */ }

        managerDAO.create(manager);
    }

    public void removeManager(Manager manager) throws SQLException {
        if (manager == null) throw new RuntimeException("Manager cannot be null.");
        managerDAO.delete(manager);
    }

    public void updateManager(Manager manager) throws SQLException {
        if (manager == null) throw new RuntimeException("Manager cannot be null.");
        // readById lança SQLException se não encontrar
        try {
            managerDAO.readById(manager.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Manager not found.");
        }
        managerDAO.update(manager);
    }

    @Override
    public Manager findByCPF(String cpf) throws SQLException {
        if (cpf == null || cpf.isEmpty())
            throw new RuntimeException("CPF cannot be null or empty.");
        return managerDAO.readByCPF(cpf); // lança SQLException se não encontrar
    }

    @Override
    public Manager findById(long id) throws SQLException {
        if (id <= 0) throw new RuntimeException("ID must be a positive number.");
        return managerDAO.readById(id);
    }

    @Override
    public Manager findByName(String name) throws SQLException {
        if (name == null || name.isEmpty())
            throw new RuntimeException("Name cannot be null or empty.");
        return managerDAO.readByName(name);
    }
}
