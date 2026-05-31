package br.edu.ufersa.hospital_manager.model.entities;
import java.time.LocalDate;

public class MedicalRecord {
    private long id;
    
    private LocalDate date;
    private String observation;
    private Doctor doctor; // reference to the doctor who created the record
    private Patient patient; // reference to the patient associated with the record
    
    
    // Constructor
    public MedicalRecord(String observation, Doctor doctor, Patient patient) {
        setDate();
        setObservation(observation);
        setDoctor(doctor);
        setPatient(patient);
        id = 0; // default value, should be set by the database when inserted
    }
    
    // Getters and Setters for id
    public long getId() {
        return id;
    }

    public void setId(long id) throws RuntimeException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
        this.id = id;
    }

    // Getters and Setters for doctor
    public Doctor getDoctor() {
        return doctor;
    }
    
    public void setDoctor(Doctor doctor) throws RuntimeException {
        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }
        this.doctor = doctor;
    }
    
    // Getters and Setters for patient
    public void setPatient(Patient patient) throws RuntimeException {
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }
        this.patient = patient;
    }
    public Patient getPatient() {
        return patient;
    }

    // Getters and Setters for date
    public LocalDate getDate() {
        return this.date;
    }

    public void setDate() throws RuntimeException {
        if (this.date != null) {
            throw new RuntimeException("Date is already set and cannot be changed.");
        }
        this.date = LocalDate.now(); // sets creation date
    }

    public void setDate(LocalDate date) throws RuntimeException {
        if (this.date != null) {
            throw new RuntimeException("Date is already set and cannot be changed.");
        }
        this.date = date;
    }


    // Getters and Setters for observation
    public String getObservation() {
        return this.observation;
    }

    public void setObservation(String observation) throws RuntimeException {
        if (observation == null || observation.trim().isEmpty()) {
            throw new RuntimeException("Medical record observation cannot be empty.");
        } else {
            this.observation = observation;
        }
    }
}