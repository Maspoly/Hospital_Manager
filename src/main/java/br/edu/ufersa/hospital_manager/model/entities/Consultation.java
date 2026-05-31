package br.edu.ufersa.hospital_manager.model.entities;

import java.time.LocalDate;

public class Consultation {
    private long id;
    private Patient patient;
    private Doctor doctor;
    private LocalDate date;
    private String status; // "SCHEDULED", "COMPLETED" ou "CANCELED"
    private MedicalRecord medicalRecord; // reference to the medical record created from this consultation

    public Consultation(Patient patient, Doctor doctor, LocalDate date, String status) {
        setPatient(patient);
        setDoctor(doctor);
        setDate(date);
        setStatus(status);
        this.medicalRecord = null; // medical record will be created after consultation is completed
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        if (patient == null) {
            System.out.println("Patient cannot be null.");
            return;
        }
        this.patient = patient;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) throws RuntimeException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }
        this.medicalRecord = medicalRecord;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        if (doctor == null) {
            System.out.println("Doctor cannot be null.");
            return;
        }
        this.doctor = doctor;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            System.out.println("Date cannot be null.");
            return;
        }
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
            System.out.println("Status must be SCHEDULED, COMPLETED or CANCELED.");
            return;
        }
        this.status = status.toUpperCase();
    }
}
