package br.edu.ufersa.HospitalManager.model.entities;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
// Doctor class inherits from Person
public class Doctor extends Person {
    // Public attribute for consultation value
    public float consultationValue;
    // Private attributes
    private String councilCode;
    private Patient patient;
    private ArrayList<Consultation> consultations = new ArrayList<>();
    private ArrayList<MedicalRecord> medicalRecords = new ArrayList<>();
    private ArrayList<String> reports = new ArrayList<>();

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

    // Constructor: initializes the doctor with default consultation value
    public Doctor(String name, String cpf, String address, String councilCode) {
        super(name, cpf, address);
        setConsultationValue(100.0f); // set default consultation value
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
            System.out.println("Consultation value cannot be negative.");
            return;
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
            System.out.println("Council code must contain exactly 6 numeric digits.");
            return;
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
            System.out.println("Patient cannot be null.");
            return;
        } else {
            this.patient = patient;
        }
    }

    // Getter for consultations array
    public void getConsultations() {
        for (Consultation c : consultations) {
            if (c != null) {
                System.out.println("Consultation: " + c.getDate() + " - " + c.getStatus());
            }
        }
    }

    // Setter with validation (cannot be null)
    public void setConsultations(Consultation[] consultations){
        if (consultations == null) {
            System.out.println("Consultations cannot be null.");
            return;
        } 
        this.consultations.addAll(Arrays.asList(consultations)); // Take all the queries from the array and put them into the ArrayList.
    }

    // Getter for medical records array
    public void getMedicalRecords() {
        for (MedicalRecord c : medicalRecords) {
            if (c != null) {
                System.out.println("Medical Record: " + c.getDate() + " - " + c.getObservation());
            }
        }
    }

    // Setter with validation
    public void setMedicalRecords(MedicalRecord[] medicalRecords){
        if (medicalRecords == null) {
            System.out.println("Medical records cannot be null.");
            return;
        } 
        this.medicalRecords.addAll(Arrays.asList(medicalRecords)); // Take all the queries from the array and put them into the ArrayList.
        }
    

    // Getter for reports
    public void getReports() {
        for (String c : reports) {
            if (c != null) {
                System.out.println("Report: " + c);
            }
        }
    }

    // Setter with validation (cannot be empty)
    public void setReports(String[] reports){
        if (reports == null) {
            System.out.println("Reports cannot be null.");
            return;
        } 
        this.reports.addAll(Arrays.asList(reports)); // Take all the queries from the array and put them into the ArrayList.
        }

    // Method to register a medical record for a patient
    public void registerMedicalRecord(Patient patient, MedicalRecord medicalRecord) {
        if (patient == null || medicalRecord == null) {
            System.out.println("Patient and medical record cannot be null.");
            return;
        } else {
            patient.setMedicalRecord(medicalRecord); // associates record to patient
        }
    }

    // Method to edit an existing medical record
    public MedicalRecord editMedicalRecord(MedicalRecord medicalRecord, String newObs) {
        if (medicalRecord == null || newObs == null || newObs.trim().isEmpty()) {
            System.out.println("Invalid data.");
            return null;
        }

        medicalRecord.setObservation(newObs); // update observation
        return medicalRecord;
    }

    // Method to edit doctor's personal data
    public void editPersonalData(String name, String cpf, String address, int consultationValue, String councilCode) {

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
            System.out.println("Medical record cannot be null.");
            return;
        }

        // Search and remove
        for (MedicalRecord c : medicalRecords) {
            if (c != null && c.equals(medicalRecord)) {
                medicalRecords.remove(c); // remove the record from the list
                System.out.println("Medical record deleted successfully.");
                return;
        } else{
                System.out.println("Medical record not found.");
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
                        return;
                    case "completed":
                        completed++;
                        return;
                    case "canceled":
                        canceled++;
                        return;
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

