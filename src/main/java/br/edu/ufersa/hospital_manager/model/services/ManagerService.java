package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.DAO.ManagerDAO;
import br.edu.ufersa.hospital_manager.model.entities.Manager;

public class ManagerService implements ManagerServiceContract {
    private ManagerDAO managerDAO;

    public ManagerService() {
        this.managerDAO = new ManagerDAO();
    }

    // ─── Manager Management ───────────────────────────────────────────────────

    // Registers a new manager.
    // CPF must be unique.
    @Override
    public void registerManager(Manager manager) throws SQLException {
        if (manager == null) {
            throw new RuntimeException("Manager cannot be null.");
        }

        if (managerDAO.readByCPF(manager.getCPF()) != null) {
            throw new RuntimeException("A manager with this CPF already exists.");
        }

        managerDAO.create(manager);
    }

    // Removes a manager from the system.
    @Override
    public void removeManager(Manager manager) throws SQLException {
        if (manager == null) {
            throw new RuntimeException("Manager cannot be null.");
        }

        if (managerDAO.readById(manager.getId()) == null) {
            throw new RuntimeException("Manager not found.");
        }

        managerDAO.delete(manager);
    }

    // Updates manager information.
    @Override
    public void updateManager(Manager manager) throws SQLException {
        if (manager == null) {
            throw new RuntimeException("Manager cannot be null.");
        }

        if (managerDAO.readById(manager.getId()) == null) {
            throw new RuntimeException("Manager not found.");
        }

        managerDAO.update(manager);
    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    @Override
    public Manager findByCPF(String cpf) throws SQLException {
        if (cpf == null || cpf.isBlank()) {
            throw new RuntimeException("CPF cannot be null or empty.");
        }
        return managerDAO.readByCPF(cpf);
    }

    @Override
    public Manager findById(long id) throws SQLException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
        return managerDAO.readById(id);
    }

    @Override
    public Manager findByName(String name) throws SQLException {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Name cannot be null or empty.");
        }
        return managerDAO.readByName(name);
    }

    @Override
    public ArrayList<Manager> listAll() throws SQLException {
        return managerDAO.listAll();
    }
}