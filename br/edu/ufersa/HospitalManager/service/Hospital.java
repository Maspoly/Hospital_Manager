package br.edu.ufersa.HospitalManager.service;

import java.util.ArrayList;

import br.edu.ufersa.HospitalManager.model.entyties.Consultation;
import br.edu.ufersa.HospitalManager.model.entyties.Doctor;
import br.edu.ufersa.HospitalManager.model.entyties.Manager;
import br.edu.ufersa.HospitalManager.model.entyties.Patient;

public class Hospital {

    String name;
    ArrayList<Doctor> doctors = new ArrayList<>();
    ArrayList<Patient> patients = new ArrayList<>();
    ArrayList<Manager> managers = new ArrayList<>();
    ArrayList<Consultation> consultations = new ArrayList<>();

    //MANAGER METHODS------------------------------------------------------
    //register a new doctor in the hospital
    void registerDoctor(String name, String cpf, String address, int counsil_code, Manager manager) {

        //new doctor to be added
        Doctor doctor = new Doctor();

        //check if the manager is registered in the hospital
        boolean managerFound = false;
        for (Manager m : managers) {
            if (m.getCpf().equals(manager.getCpf())) {
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
    void editDoctor(Doctor doctor, String name, String cpf, String address, int counsil_code, Manager manager) {
        //check if the manager is registered in the hospital
        boolean managerFound = false;
        for (Manager m : managers) {
            if (m.getCpf().equals(manager.getCpf())) {
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
    void removeDoctor(Doctor doctor, Manager manager) {
        //check if the manager is registered in the hospital
        boolean managerFound = false;
        for (Manager m : managers) {
            if (m.getCpf().equals(manager.getCpf())) {
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
    Doctor searchDoctorByCounsilCode(String counsil_code) {
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
    Doctor searchDoctorByName(String name) {
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
    Patient searchPatientByCPF(String cpf) {
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
    Patient searchPatientByName(String name) {
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
    Manager searchManagerByCPF(String cpf) {
        for (Manager m : managers) {
            if (m.getCpf().equals(cpf)) {
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
    Manager searchManagerByName(String name) {
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
