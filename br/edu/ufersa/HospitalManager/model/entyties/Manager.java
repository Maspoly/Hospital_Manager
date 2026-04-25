package br.edu.ufersa.HospitalManager.model.entyties;

public class Manager {
    private String name;
    private String cpf;
    private String address;

    //getters
    public String getAddress() {
        return address;
    }
    public String getCPF() {
        return cpf;
    }
    public String getName() {
        return name;
    }

    //setters
    public void setAddress(String address) {
        this.address = address;
    }
    public void setCPF(String cpf) {
        this.cpf = cpf;
    }
    public void setName(String name) {
        this.name = name;
    }
}
