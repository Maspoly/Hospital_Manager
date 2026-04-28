package br.edu.ufersa.HospitalManager.model.entities;

import java.util.ArrayList;

public class Hospital {

    private String name;
    private ArrayList<Doctor> doctors = new ArrayList<>();
    private ArrayList<Patient> patients = new ArrayList<>();
    private ArrayList<Manager> managers = new ArrayList<>();
    private ArrayList<Consultation> consultations = new ArrayList<>();

    // Constructor
    public Hospital(String name, Manager manager) {
        setName(name);
        registerManager(manager);
    }

    // Getters and Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Hospital name cannot be empty.");
        } else {
            this.name = name;
        }
    }

    //USER METHODS------------------------------------------------------
    //register a new patient in the hospital
    public void registerPatient(Patient patient) {
        if (patient == null) {
            System.out.println("Patient cannot be null.");
            return;
        }
        patients.add(patient);
        System.out.println("Patient registered successfully.");
    }
    //register a new manager in the hospital
    public void registerManager(Manager manager) {
        if (manager == null) {
            System.out.println("Manager cannot be null.");
            return;
        }
        managers.add(manager);
        System.out.println("Manager registered successfully.");
    }
    //register a new consultation in the hospital
    public void registerConsultation(Consultation consultation) {
        if (consultation == null) {
            System.out.println("Consultation cannot be null.");
            return;
        }
        consultations.add(consultation);
        System.out.println("Consultation registered successfully.");
    }
    //remove a patient from the hospital
    public void removePatient(Patient patient) {
        if (patient == null) {
            System.out.println("Patient cannot be null.");
            return;
        }
        patients.remove(patient);
        System.out.println("Patient removed successfully.");
    }
    //remove a manager from the hospital
    public void removeManager(Manager manager) {
        if (manager == null) {
            System.out.println("Manager cannot be null.");
            return;
        }
        managers.remove(manager);
        System.out.println("Manager removed successfully.");
    }
    //remove a consultation from the hospital
    public void removeConsultation(Consultation consultation) {
        if (consultation == null) {
            System.out.println("Consultation cannot be null.");
            return;
        }
        consultations.remove(consultation);
        System.out.println("Consultation removed successfully.");
    }

    //MANAGER METHODS------------------------------------------------------
    //register a new doctor in the hospital
    public void registerDoctor(String name, String cpf, String address, String counsil_code, Manager manager) {

        // Validations
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        if (cpf == null || !cpf.matches("\\d{11}")) {
            System.out.println("CPF must contain exactly 11 numeric digits");
            return;
        }
        if (address == null || address.trim().isEmpty()) {
            System.out.println("Address cannot be empty.");
            return;
        }
        if (counsil_code == null || !String.valueOf(counsil_code).matches("\\d+")) {
            System.out.println("Council code must contain only numeric digits");
            return;
        }

        //new doctor to be added
        Doctor doctor = new Doctor(name, cpf, address, counsil_code);

        //check if the manager is registered in the hospital
        boolean managerFound = false;
        for (Manager m : managers) {
            if (m.getCPF().equals(manager.getCPF())) {
                managerFound = true;
                //register the doctor in the hospital
                doctors.add(doctor);
                System.out.println("Doctor registered successfully.");
                break;
            }
        }
        //if the manager is not found, print an error message
        if (!managerFound) {
            System.out.println("Manager not found. Doctor registration failed.");
        }
    }

    //edit a doctor information
    public void editDoctor(Doctor doctor, String name, String cpf, String address, String counsil_code, Manager manager) {
        // Validations
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        if (cpf == null || !cpf.matches("\\d{11}")) {
            System.out.println("CPF must contain exactly 11 numeric digits");
            return;
        }
        if (address == null || address.trim().isEmpty()) {
            System.out.println("Address cannot be empty.");
            return;
        }
        if (counsil_code == null || !String.valueOf(counsil_code).matches("\\d+")) {
            System.out.println("Council code must contain only numeric digits");
            return;
        }

        //check if the manager is registered in the hospital
        boolean managerFound = false;
        for (Manager m : managers) {
            if (m.getCPF().equals(manager.getCPF())) {
                managerFound = true;
                //edit the doctor information
                doctor.setName(name);
                doctor.setCPF(cpf);
                doctor.setAddress(address);
                doctor.setCouncilCode(String.valueOf(counsil_code));
                System.out.println("Doctor information edited successfully.");
                break;
            }
        }
        //if the manager is not found, print an error message
        if (!managerFound) {
            System.out.println("Manager not found. Doctor editing failed.");
        }
    }

    //remove a doctor from the hospital
    public void removeDoctor(Doctor doctor, Manager manager) {
        // Validations
        if (doctor == null) {
            System.out.println("Doctor cannot be null.");
            return;
        }
        //check if the manager is registered in the hospital
        boolean managerFound = false;
        for (Manager m : managers) {
            if (m.getCPF().equals(manager.getCPF())) {
                managerFound = true;
                //remove the doctor from the hospital
                doctors.remove(doctor);
                System.out.println("Doctor removed successfully.");
                break;
            }
        }
        //if the manager is not found, print an error message
        if (!managerFound) {
            System.out.println("Manager not found. Doctor removal failed.");
        }
    }

    //SEARCH METHODS------------------------------------------------------
    //search for a doctor by counsil code
    public Doctor searchDoctorByCounsilCode(String counsil_code) {
        // Validations
        if (counsil_code == null || !String.valueOf(counsil_code).matches("\\d+")) {
            System.out.println("Council code must contain only numeric digits");
            return null;
        }
        for (Doctor d : doctors) {
            //doctor found
            if (d.getCouncilCode().equals(counsil_code)) {
                System.out.println("Doctor found.");
                return d;
            }
        }
        //doctor not found
        System.out.println("Doctor not found.");
        return null; 
    }

    //search for a doctor by name
    public Doctor searchDoctorByName(String name) {
        // Validations
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return null;
        }
        for (Doctor d : doctors) {
            if (d.getName().equals(name)) {
                //doctor found
                System.out.println("Doctor found.");
                return d;
            }
        }
        //doctor not found
        System.out.println("Doctor not found.");
        return null;
    }

    //search for a patient by cpf
    public Patient searchPatientByCPF(String cpf) {
        // Validations
        if (cpf == null || !cpf.matches("\\d{11}")) {
            System.out.println("CPF must contain exactly 11 numeric digits");
            return null;
        }
        for (Patient p : patients) {
            if (p.getCPF().equals(cpf)) {
                //patient found
                System.out.println("Patient found.");
                return p;
            }
        }
        //patient not found
        System.out.println("Patient not found.");
        return null;
    }

    //search for a patient by name
    public Patient searchPatientByName(String name) {
        // Validations
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return null;
        }
        for (Patient p : patients) {
            if (p.getName().equals(name)) {
                //patient found
                System.out.println("Patient found.");
                return p;
            }
        }
        //patient not found
        System.out.println("Patient not found.");
        return null;
    }

    //search for a manager by cpf
    public Manager searchManagerByCPF(String cpf) {
        // Validations
        if (cpf == null || !cpf.matches("\\d{11}")) {
            System.out.println("CPF must contain exactly 11 numeric digits");
            return null;
        }
        for (Manager m : managers) {
            if (m.getCPF().equals(cpf)) {
                //manager found
                System.out.println("Manager found.");
                return m;
            }
        }
        //manager not found
        System.out.println("Manager not found.");
        return null;
    }

    //search for a manager by name
    public Manager searchManagerByName(String name) {
        // Validations
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return null;
        }
        for (Manager m : managers) {
            if (m.getName().equals(name)) {
                //manager found
                System.out.println("Manager found.");
                return m;
            }
        }
        //manager not found
        System.out.println("Manager not found.");
        return null;
    }
}
