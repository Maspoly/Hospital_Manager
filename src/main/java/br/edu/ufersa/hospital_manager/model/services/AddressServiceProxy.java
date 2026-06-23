package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;

import br.edu.ufersa.hospital_manager.model.entities.Address;

public class AddressServiceProxy implements IsServiceProxy {
    private final AddressService addressService;

    public AddressServiceProxy(AddressService addressService) {
        this.addressService = addressService;
    }

    public void create(Address address) throws SQLException {
        addressService.create(address);
    }

    public Address findById(long id) throws SQLException {
        return addressService.findById(id);
    }
}