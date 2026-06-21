package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;

import br.edu.ufersa.hospital_manager.model.DAO.AddressDAO;
import br.edu.ufersa.hospital_manager.model.entities.Address;

public class AddressService {
    private final AddressDAO addressDAO;

    public AddressService() {
        this.addressDAO = new AddressDAO();
    }

    public void create(Address address) throws SQLException {
        if (address == null) {
            throw new RuntimeException("Address cannot be null.");
        }

        addressDAO.create(address);
    }

    public Address findById(long id) throws SQLException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }

        return addressDAO.readById(id);
    }
}