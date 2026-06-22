package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Manager;

public class ManagerServiceProxy implements ManagerServiceContract {
    private final ManagerServiceContract managerService;

    public ManagerServiceProxy() {
        this.managerService = new ManagerService();
    }

    @Override
    public void registerManager(Manager manager) throws SQLException {
        ensureManagerAccess("register a manager");
        managerService.registerManager(manager);
    }

    @Override
    public void removeManager(Manager manager) throws SQLException {
        ensureManagerAccess("remove a manager");
        managerService.removeManager(manager);
    }

    @Override
    public void updateManager(Manager manager) throws SQLException {
        ensureManagerAccess("update a manager");
        managerService.updateManager(manager);
    }

    @Override
    public Manager findById(long id) throws SQLException {
        return managerService.findById(id);
    }

    @Override
    public Manager findByName(String name) throws SQLException {
        return managerService.findByName(name);
    }

    @Override
    public Manager findByCPF(String cpf) throws SQLException {
        return managerService.findByCPF(cpf);
    }

    @Override
    public ArrayList<Manager> listAll() throws SQLException {
        return managerService.listAll();
    }

    private void ensureManagerAccess(String action) {
        if (ServiceRoleContext.getCurrentRole() != ServiceRole.MANAGER) {
            throw new RuntimeException("Only a manager can " + action + ".");
        }
    }
}