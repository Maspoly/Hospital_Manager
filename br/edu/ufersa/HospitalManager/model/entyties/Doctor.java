package br.edu.ufersa.HospitalManager.model.entyties;
import java.time.LocalDate;
// Doctor class inherits from Person
public class Doctor extends Person {
    // Public attribute for consultation value
    public float consultationValue;
    // Private attributes
    private String councilCode;
    private Patient patient;
    private Consultation consultations[];
    private MedicalRecord medicalRecords[];
    private String reports[];

    // Constructor: initializes the doctor with basic data
    public Doctor(String name, String cpf, String address, float consultationValue, String councilCode) {
        super(name, cpf, address);
        setConsultationValue(consultationValue); // set consultation value
        setCouncilCode(councilCode); // validate and set council code

        // Initialize attributes
        this.patient = null;
        setConsultations(new Consultation[100]); // fixed-size array for consultations
        setMedicalRecords(new MedicalRecord[100]); // fixed-size array for medical records
        setReports(new String[100]); // fixed-size array for reports
    }

    // Getter for consultation value
    public float getConsultationValue() {
        return this.consultationValue;
    }

    // Setter with validation (cannot be negative)
    public void setConsultationValue(float consultationValue){
        if (consultationValue < 0) {
            throw new IllegalArgumentException("Consultation value cannot be negative.");
        } else {
            this.consultationValue = consultationValue;
        }
    }

    // Getter for council code
    public String getCouncilCode() {
        return this.councilCode;
    }

    // Setter with validation (must have 6 digits)
    public void setCouncilCode(String councilCode){
        if (!councilCode.matches("\\d{6}")) {
            throw new IllegalArgumentException("Must contain exactly 6 numeric digits.");
        } else {
            this.councilCode = councilCode;
        }
    }

    // Getter for patient
    public Patient getPatient() {
        return this.patient;
    }

    // Setter with validation (cannot be null)
    public void setPatient(Patient patient){
        if (patient == null) {
            throw new IllegalArgumentException("Patient cannot be null.");
        } else {
            this.patient = patient;
        }
    }

    // Getter for consultations array
    public Consultation[] getConsultations() {
        return this.consultations;
    }

    // Setter with validation (cannot be null)
    public void setConsultations(Consultation[] consultations){
        if (consultations == null) {
            throw new IllegalArgumentException("Consultations cannot be null.");
        } else {
            this.consultations = consultations;
        }
    }

    // Getter for medical records array
    public MedicalRecord[] getMedicalRecords() {
        return this.medicalRecords;
    }

    // Setter with validation
    public void setMedicalRecords(MedicalRecord[] medicalRecords){
        if (medicalRecords == null) {
            throw new IllegalArgumentException("Medical records cannot be null.");
        } else {
            this.medicalRecords = medicalRecords;
        }
    }

    // Getter for reports
    public String[] getReports() {
        return this.reports;
    }

    // Setter with validation (cannot be empty)
    public void setReports(String[] reports){
        if (reports == null || reports.length == 0) {
            throw new IllegalArgumentException("Reports cannot be empty.");
        } else {
            this.reports = reports;
        }
    }

    // Method to register a medical record for a patient
    public void registerMedicalRecord(Patient patient, MedicalRecord medicalRecord) {
        if (patient == null || medicalRecord == null) {
            throw new IllegalArgumentException("Patient and medical record cannot be null.");
        } else {
            patient.setMedicalRecord(medicalRecord); // associates record to patient
        }
    }

    // Method to edit an existing medical record
    public MedicalRecord editMedicalRecord(MedicalRecord medicalRecord, String newObs) {
        if (medicalRecord == null || newObs == null || newObs.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid data.");
        }

        medicalRecord.setObservation(newObs); // update observation
        return medicalRecord;
    }

    // Method to edit doctor's personal data
    public void editPersonalData(String name, String cpf, String address, int consultationValue, String councilCode) {

        // Validations
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF must contain exactly 11 numeric digits");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }

        // Update data
        setName(name);
        setCPF(cpf);
        setAddress(address);
        setConsultationValue(consultationValue);
        setCouncilCode(councilCode);
    }

    // Method to delete a medical record from the array
    public void deleteMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            throw new IllegalArgumentException("Medical record cannot be null.");
        }

        // Search and remove
        for (int i = 0; i < this.medicalRecords.length; i++) {
            if (this.medicalRecords[i] == medicalRecord) {
                this.medicalRecords[i] = null;
                break;
            }
        }
    }

    // Method to generate report based on date range
    public void generateReport(LocalDate start, LocalDate end) {

        int total = 0;
        int scheduled = 0;
        int completed = 0;
        int canceled = 0;

        // Iterate over consultations
        for (Consultation c : consultations) {
            if (c != null &&
                (c.getDate().isEqual(start) || c.getDate().isAfter(start)) &&
                (c.getDate().isEqual(end) || c.getDate().isBefore(end))) {

                total++;

                // Count by status
                switch (c.getStatus()) {
                    case "scheduled":
                        scheduled++;
                        break;
                    case "completed":
                        completed++;
                        break;
                    case "canceled":
                        canceled++;
                        break;
                }
            }
        }

        // Print results
        System.out.println("Total: " + total);
        System.out.println("Scheduled: " + scheduled);
        System.out.println("Completed: " + completed);
        System.out.println("Canceled: " + canceled);
    }
}

