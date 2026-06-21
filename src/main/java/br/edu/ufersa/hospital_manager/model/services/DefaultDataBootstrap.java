package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;

import br.edu.ufersa.hospital_manager.model.DAO.ManagerDAO;
import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Manager;
import br.edu.ufersa.hospital_manager.util.Connector;

public final class DefaultDataBootstrap {
    private static final String DEFAULT_CPF = "00000000000";
    private static final String DEFAULT_PASSWORD = "admin123";

    private DefaultDataBootstrap() {
    }

    public static void ensureTestAccounts() throws SQLException {
        if (!Connector.isAvailable()) {
            return;
        }

        ensureDefaultManager();
    }

    private static void ensureDefaultManager() throws SQLException {
        ManagerDAO managerDAO = new ManagerDAO();
        if (managerDAO.readByCPF(DEFAULT_CPF) != null) {
            return;
        }

        Address address = new Address("Av. Central", "100", "Centro", "Mossoró", "RN");
        Manager defaultManager = new Manager("Administrador", DEFAULT_CPF, address, DEFAULT_PASSWORD);
        managerDAO.create(defaultManager);
    }

}