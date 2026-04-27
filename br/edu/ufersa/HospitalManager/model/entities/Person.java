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
            System.out.println("Name cannot be empty.");
            return;
        }
        this.name = name;
    }

    public void setCPF(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            System.out.println("CPF must contain exactly 11 numeric digits.");
            return;
        }
        this.cpf = cpf;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            System.out.println("Address cannot be empty.");
            return;
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
