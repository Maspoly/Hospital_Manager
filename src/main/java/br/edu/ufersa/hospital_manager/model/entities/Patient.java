package br.edu.ufersa.hospital_manager.model.entities;

public class Patient extends Person {

    public Patient(String name, String cpf, Address address) {
        super(name, cpf, address);
    }

<<<<<<< HEAD
}
=======
    public Patient(String name, String cpf, Address address, String password) {
        super(name, cpf, address, password);
    }

    public Patient(String name, String cpf, Address address, String password, boolean passwordIsHash) {
        super(name, cpf, address, password, passwordIsHash);
    }

}
>>>>>>> 96ad7c6 (Linked screens to data base)
