package br.edu.ufersa.hospital_manager.model.entities;

public class Manager extends Person {
    // basic constructor
    public Manager(String name, String cpf, Address address) throws RuntimeException {
        super(name, cpf, address);
    }

    public Manager(String name, String cpf, Address address, String password) throws RuntimeException {
        super(name, cpf, address, password);
    }

    public Manager(String name, String cpf, Address address, String password, boolean passwordIsHash) throws RuntimeException {
        super(name, cpf, address, password, passwordIsHash);
    }
}
