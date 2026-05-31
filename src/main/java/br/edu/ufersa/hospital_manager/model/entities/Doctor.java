package br.edu.ufersa.hospital_manager.model.entities;

// Doctor class inherits from Person
public class Doctor extends Person {
    private float consultationValue;
    private String councilCode;

    // Constructor: initializes the doctor with basic data
    public Doctor(String name, String cpf, String address, float consultationValue, String councilCode) throws RuntimeException {
        super(name, cpf, address);
        setConsultationValue(consultationValue); // set consultation value
        setCouncilCode(councilCode); // validate and set council code
    }

    // Constructor: initializes the doctor with default consultation value
    public Doctor(String name, String cpf, String address, String councilCode) throws RuntimeException{
        super(name, cpf, address);
        setConsultationValue(100.0f); // set default consultation value
        setCouncilCode(councilCode); // validate and set council code
    
    }

    // Getter for consultation value
    public float getConsultationValue() {
        return this.consultationValue;
    }

    // Setter with validation (cannot be negative)
    public void setConsultationValue(float consultationValue) throws RuntimeException {
        if (consultationValue < 0) {
            System.out.println("Consultation value cannot be negative.");
            throw new RuntimeException("Consultation value cannot be negative.");
        } else {
            this.consultationValue = consultationValue;
        }
    }

    // Getter for council code
    public String getCouncilCode() {
        return this.councilCode;
    }

    // Setter with validation (must have 6 digits)
    public void setCouncilCode(String councilCode) throws RuntimeException {
        if (!councilCode.matches("\\d{6}")) {
            System.out.println("Council code must contain exactly 6 numeric digits.");
            throw new RuntimeException("Council code must contain exactly 6 numeric digits.");
        } else {
            this.councilCode = councilCode;
        }
    }
}

