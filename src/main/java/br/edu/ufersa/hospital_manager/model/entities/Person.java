package br.edu.ufersa.hospital_manager.model.entities;

public abstract class Person {
    private long id; // unique identifier for database purposes
    private String name;
    private String cpf;
    private String address;

    // basic constructor
    public Person(String name, String cpf, String address) throws RuntimeException {
        id = 0; // default value, should be set by database or service layer
        setName(name);
        setCPF(cpf);
        setAddress(address);
    }

    public void setId(long id) throws RuntimeException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
        this.id = id;
    }

    // setters with validation
    public void setName(String name) throws RuntimeException {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Name cannot be empty.");
        }
        this.name = name;
    }

    public void setCPF(String cpf) throws RuntimeException {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new RuntimeException("CPF must contain exactly 11 numeric digits.");
        }
        this.cpf = cpf;
    }

    public void setAddress(String address) throws RuntimeException {
        if (address == null || address.trim().isEmpty()) {
            throw new RuntimeException("Address cannot be empty.");
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

    public long getId() {
        return id;
    }
}
