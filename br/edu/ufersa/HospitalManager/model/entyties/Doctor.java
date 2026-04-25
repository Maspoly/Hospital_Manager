package br.edu.ufersa.HospitalManager.model.entyties;
import java.time.LocalDate;

public class Doctor {
    public float consultationValue;
    private int councilCode;
    private Patient patient;
    private Consultation consultations[];
    private MedicalRecord medicalRecords[];
    private String reports[];

    // Getters and Setters for consultation value
    public float getConsultationValue() {
        return this.consultationValue;
    }

    public void setConsultationValue(float consultationValue) {
        if (consultationValue < 0) {
            throw new IllegalArgumentException("Consultation value cannot be negative.");
        } else {
            this.consultationValue = consultationValue;
        }
    }

    // Getters and Setters for council code
    public int getCouncilCode() {
        return this.councilCode;
    }

    public void setCouncilCode(String councilCode) {
        if (!councilCode.matches("\\d{6}")) {
            throw new IllegalArgumentException("Must contain exactly 6 numeric digits.");
        } else {
            this.councilCode = Integer.parseInt(councilCode);
        }
    }

    // Getters and Setters for associated patient
    public Patient getPatient() {
        return this.patient;
    }

    public void setPatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null.");
        } else {
            this.patient = patient;
        }
    }

    // Getters and Setters for consultations
    public Consultation[] getConsultations() {
        return this.consultations;
    }

    public void setConsultations(Consultation[] consultations) {
        if (consultations == null) {
            throw new IllegalArgumentException("Consultations cannot be null.");
        } else {
            this.consultations = consultations;
        }
    }

    // Getters and Setters for medical records
    public MedicalRecord getMedicalRecord() {
        return this.medicalRecords;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            throw new IllegalArgumentException("Medical record cannot be null.");
        } else {
            this.medicalRecords.add(medicalRecord);
        }
    }

    // Getters and Setters for reports
    public String[] getReports() {
        return this.reports;
    }

    public void setReports(String[] reports) {
        if (reports == null || reports.length == 0) {
            throw new IllegalArgumentException("Report cannot be empty.");
        } else {
            this.reports = reports;
        }
    }

    public void registerMedicalRecord(Patient patient, MedicalRecord medicalRecord) {
        if (patient == null || medicalRecord == null) {
            throw new IllegalArgumentException("Patient and medical record cannot be null.");
        }
    }

    public MedicalRecord editMedicalRecord(MedicalRecord medicalRecord, String newObs) {
        if (medicalRecord == null || newObs == null || newObs.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid data.");
        }

        medicalRecord.setObservation(newObs);

        return medicalRecord;
    }

    public void editPersonalData(String name, String CPF, String address, int consultationValue, String councilCode) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (CPF == null || !CPF.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF must contain exactly 11 numeric digits.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
        setName(name);
        setCPF(CPF);
        setAddress(address);
        setConsultationValue(consultationValue);
        setCouncilCode(councilCode);
    }

    public void deleteMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            throw new IllegalArgumentException("Medical record cannot be null.");
        }

        for (int i = 0; i < this.medicalRecords.length; i++) {
            if (this.medicalRecords[i] == medicalRecord) {
                this.medicalRecords[i] = null;
                break;
            }
        }
    }

    public void generateReport(LocalDate start, LocalDate end) {

        int total = 0;
        int scheduled = 0;
        int completed = 0;
        int canceled = 0;

        for (Consultation c : consultations) {

            if (c != null &&
                (c.getDate().isEqual(start) || c.getDate().isAfter(start)) &&
                (c.getDate().isEqual(end) || c.getDate().isBefore(end))) {

                total++;

                switch (c.getStatus()) {
                    case "SCHEDULED":
                        scheduled++;
                        break;
                    case "COMPLETED":
                        completed++;
                        break;
                    case "CANCELED":
                        canceled++;
                        break;
                }
            }
        }

        System.out.println("Total: " + total);
        System.out.println("Scheduled: " + scheduled);
        System.out.println("Completed: " + completed);
        System.out.println("Canceled: " + canceled);
    }
}
