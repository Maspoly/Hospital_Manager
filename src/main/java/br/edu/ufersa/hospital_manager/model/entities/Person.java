package br.edu.ufersa.hospital_manager.model.entities;

import br.edu.ufersa.hospital_manager.util.PasswordUtils;

public abstract class Person {
    private long id; // unique identifier for database purposes
    private String name;
    private String cpf;
    private Address address;
    private String passwordHash;

    // basic constructor
    public Person(String name, String cpf, Address address) throws RuntimeException {
        this(name, cpf, address, null, false);
    }

    public Person(String name, String cpf, Address address, String password) throws RuntimeException {
        this(name, cpf, address, password, false);
    }

    public Person(String name, String cpf, Address address, String password, boolean passwordIsHash) throws RuntimeException {
        id = 0; // default value, should be set by database or service layer
        setName(name);
        setCPF(cpf);
        setAddress(address);
        setPasswordInternal(password, passwordIsHash);
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

    public void setAddress(Address address) throws RuntimeException {
        if (address == null) {
            throw new RuntimeException("Address cannot be null.");
        }
        this.address = address;
    }

    public void setPassword(String password) throws RuntimeException {
        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password cannot be empty.");
        }
        this.passwordHash = PasswordUtils.hash(password);
    }

    public void setPasswordHash(String passwordHash) throws RuntimeException {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new RuntimeException("Password cannot be empty.");
        }
        this.passwordHash = passwordHash;
    }

    private void setPasswordInternal(String password, boolean passwordIsHash) {
        if (password == null || password.isBlank()) {
            this.passwordHash = null;
            return;
        }

        if (passwordIsHash) {
            setPasswordHash(password);
        } else {
            setPassword(password);
        }
    }

    // getters
    public String getName() {
        return name;
    }

    public String getCPF() {
        return cpf;
    }

    public Address getAddress() {
        return this.address;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public long getId() {
        return id;
    }
}
