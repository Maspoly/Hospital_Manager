package br.edu.ufersa.hospital_manager.util;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Manager;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class PersonFactory {
    
    // ── 1. For managers and patients (basic) ─────────────────────────────────
    public static Patient createPatient(String name, String cpf, Address address) {
        return new Patient(name, cpf, address);
    }
    
    public static Manager createManager(String name, String cpf, Address address) {
        return new Manager(name, cpf, address);
    }

    // ── 2. For managers and patients (with password) ─────────────────────────────────
    public static Patient createPatient(String name, String cpf, Address address, String password) {
        return new Patient(name, cpf, address, password);
    }
    
    public static Manager createManager(String name, String cpf, Address address, String password) {
        return new Manager(name, cpf, address, password);
    }

    // ── 3. For reading from the database (with hashed password) ──────────────────────────────
    public static Patient createPatient(String name, String cpf, Address address, String password, boolean passwordIsHash) {
        return new Patient(name, cpf, address, password, passwordIsHash);
    }
    
    public static Manager createManager(String name, String cpf, Address address, String password, boolean passwordIsHash) {
        return new Manager(name, cpf, address, password, passwordIsHash);
    }

    // ── 3. EXCLUSIVE DOCTORS: (Without Hash Flag and Password) ─────
    public static Doctor createDoctor(String name, String cpf, Address address, float consultationValue, String councilCode) {
        // Call the constructor of Doctor that does not require the passwordIsHash flag and password
        return new Doctor(name, cpf, address, consultationValue, councilCode);
    }

    // ── 4. EXCLUSIVE DOCTORS: Controller (Without Hash Flag) ─────
    public static Doctor createDoctor(String name, String cpf, Address address, String password, float consultationValue, String councilCode) {
        // Call the constructor of Doctor that does not require the passwordIsHash flag
        return new Doctor(name, cpf, address, password, consultationValue, councilCode);
    }

    // ── 4. EXCLUSIVE DOCTORS: DAO (With Hash Flag) ──────────
    public static Doctor createDoctor(String name, String cpf, Address address, String password, boolean passwordIsHash, float consultationValue, String councilCode) {
        // Call the constructor of Doctor that accepts the passwordIsHash flag
        return new Doctor(name, cpf, address, password, passwordIsHash, consultationValue, councilCode);
    }
}