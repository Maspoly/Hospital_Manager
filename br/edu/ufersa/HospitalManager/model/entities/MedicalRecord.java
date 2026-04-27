package br.edu.ufersa.HospitalManager.model.entities;
import java.time.LocalDate;

public class MedicalRecord {
    private LocalDate date;
    private String observation;

    // Constructor
    public MedicalRecord(String observation) {
        setDate();
        setObservation(observation);
    }

    // Getters and Setters for date
    public LocalDate getDate() {
        return this.date;
    }

    public void setDate() {
        this.date = LocalDate.now(); // sets creation date
    }

    // Getters and Setters for observation
    public String getObservation() {
        return this.observation;
    }

    public void setObservation(String observation) {
        if (observation == null || observation.trim().isEmpty()) {
            throw new IllegalArgumentException("Medical record observation cannot be empty.");
        } else {
            this.observation = observation;
        }
    }
}