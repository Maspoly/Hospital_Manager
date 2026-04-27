package br.edu.ufersa.HospitalManager.model.entities;

public class Person {
    private String name;
    private String cpf;
    private String address;

    // basic constructor
    public Person(String name, String cpf, String address) {
        setName(name);
        setCPF(cpf);
        setAddress(address);
    }

    // setters with validation
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name;
    }

    public void setCPF(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF must contain exactly 11 numeric digits.");
        }
        this.cpf = cpf;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
        this.address = address;
    }

    // getters
    public String getName() {
        return name;
    }

    public String getCPF() {
        return cpf;
    }

    public String getAddress() {
        return address;
    }
}
