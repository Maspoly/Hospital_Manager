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
        this.doctor = doctor;
        this.patient = patient;
        id = 0; // default value, should be set by the database when inserted
    }

    public MedicalRecord(String observation, Doctor doctor, Patient patient, LocalDate date) {
        this.observation = null;
        this.doctor = doctor;
        this.patient = patient;
        this.date = date;
        setObservation(observation);
        id = 0;
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
        this.doctor = doctor;
    }
    
    // Getters and Setters for patient
    public void setPatient(Patient patient) throws RuntimeException {
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
        this.date = LocalDate.now(); // sets creation date
    }

    public void setDate(LocalDate date) throws RuntimeException {
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