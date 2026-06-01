package br.edu.ufersa.hospital_manager.model.entities;

import java.time.LocalDateTime;

public class Consultation {
    private long id;
    
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private String status; // "SCHEDULED", "COMPLETED" ou "CANCELED"
    
    public Consultation(Patient patient, Doctor doctor, LocalDateTime dateTime, String status) {
        id = 0; // ID will be set by the database when the consultation is registered
        setPatient(patient);
        setDoctor(doctor);
        setDateTime(dateTime);
        setStatus(status);
    }
    public long getId() {
        return id;
    }

    public void setId(long id) throws RuntimeException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
        this.id = id;
    }
    
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) throws RuntimeException {
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) throws RuntimeException {
        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }
        this.doctor = doctor;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) throws RuntimeException {
        if (dateTime == null) {
            throw new RuntimeException("Date and time cannot be null.");
        }
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) throws RuntimeException {
        String[] valid = {"SCHEDULED", "COMPLETED", "CANCELED"};
        boolean ok = false;
        if (status != null) {
            for (String s : valid) {
                if (status.equalsIgnoreCase(s)) {
                    ok = true;
                    break;
                }
            }
        }
        if (!ok) {
            throw new RuntimeException("Status must be SCHEDULED, COMPLETED or CANCELED.");
        }
        this.status = status.toUpperCase();
    }
}
