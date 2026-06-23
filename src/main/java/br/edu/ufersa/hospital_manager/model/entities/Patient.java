package br.edu.ufersa.hospital_manager.model.entities;

public class Patient extends Person {

    public Patient(String name, String cpf, Address address) throws RuntimeException {
        super(name, cpf, address);
    }

    public Patient(String name, String cpf, Address address, String password) throws RuntimeException {
        super(name, cpf, address, password);
    }

    public Patient(String name, String cpf, Address address, String password, boolean passwordIsHash) throws RuntimeException {
        super(name, cpf, address, password, passwordIsHash);
    }

}
