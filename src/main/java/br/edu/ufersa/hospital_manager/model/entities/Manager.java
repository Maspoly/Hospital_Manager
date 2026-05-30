package br.edu.ufersa.hospital_manager.model.entities;

public class Manager extends Person {
    // basic constructor
    public Manager(String name, String cpf, String address) throws RuntimeException {
        super(name, cpf, address);
    }
}
