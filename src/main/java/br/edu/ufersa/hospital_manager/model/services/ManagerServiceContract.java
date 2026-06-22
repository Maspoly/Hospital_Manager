package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Manager;

public interface ManagerServiceContract extends FindServices<Manager> {
    void registerManager(Manager manager) throws SQLException;

    void removeManager(Manager manager) throws SQLException;

    void updateManager(Manager manager) throws SQLException;

    ArrayList<Manager> listAll() throws SQLException;
}