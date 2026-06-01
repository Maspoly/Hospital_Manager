package br.edu.ufersa.hospital_manager.model.services;

import br.edu.ufersa.hospital_manager.model.DAO.PatientDAO;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class PatientServices implements FindServices<Patient> {

    private final PatientDAO patientDAO;

    public PatientServices() {
        this.patientDAO = new PatientDAO();
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    public Patient registerPatient(Patient patient) throws SQLException {
        // DAO lança SQLException quando NÃO encontra.
        // Se NÃO lançou = CPF já existe = bloqueamos.
        try {
            patientDAO.readByCPF(patient.getCPF());
            throw new RuntimeException("There is already a patient registered with CPF: " + patient.getCPF());
        } catch (SQLException ignored) {
            // não encontrou = CPF livre = pode continuar
        }

        patientDAO.create(patient);
        return patient;
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    public void removePatient(long id) throws SQLException {
        Patient patient = findById(id);
        patientDAO.delete(patient);
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    public Patient updatePatient(Patient patient) throws SQLException {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        patientDAO.update(patient);
        return patient;
    }

    public Patient assignMedicalRecord(Patient patient, MedicalRecord medicalRecord) throws SQLException {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        if (medicalRecord == null) throw new RuntimeException("Medical record cannot be null.");

        if (patient.getMedicalRecord() != null) {
            throw new RuntimeException("Patient already has an active medical record.");
        }

        patient.setMedicalRecord(medicalRecord);
        patientDAO.update(patient);
        return patient;
    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    @Override
    public Patient findById(long id) throws SQLException {
        if (id <= 0) throw new RuntimeException("ID must be a positive number.");
        return patientDAO.readById(id);
    }

    @Override
    public Patient findByCPF(String cpf) throws SQLException {
        if (cpf == null || cpf.isBlank()) throw new RuntimeException("CPF cannot be empty.");
        return patientDAO.readByCPF(cpf);
    }

    @Override
    public Patient findByName(String name) throws SQLException {
        if (name == null || name.isBlank()) throw new RuntimeException("Name cannot be empty.");
        return patientDAO.readByName(name);
    }

    public ArrayList<Patient> listAll() throws SQLException {
        ArrayList<Patient> patients = patientDAO.listAll();
        if (patients.isEmpty()) throw new RuntimeException("No patients registered in the system.");
        return patients;
    }

    // ─── Consultations ────────────────────────────────────────────────────────

    public Consultation scheduleConsultation(Patient patient, Doctor doctor, LocalDate date, String status) {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        if (doctor == null)  throw new RuntimeException("Doctor cannot be null.");
        if (date == null)    throw new RuntimeException("Date cannot be null.");
        if (date.isBefore(LocalDate.now())) throw new RuntimeException("Consultation date cannot be in the past.");

        return patient.cadastrarConsulta(doctor, date, status);
    }

    public Consultation updateConsultation(Patient patient, Consultation consultation,
                                            LocalDate newDate, String newStatus) {
        if (patient == null)      throw new RuntimeException("Patient cannot be null.");
        if (consultation == null) throw new RuntimeException("Consultation cannot be null.");
        if (newDate == null)      throw new RuntimeException("New date cannot be null.");

        if (consultation.getDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Past consultations cannot be edited.");
        }

        return patient.editorConsulta(consultation, newDate, newStatus);
    }

    public void removeConsultations(Patient patient, Consultation[] consultations) {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        if (consultations == null || consultations.length == 0)
            throw new RuntimeException("No consultations provided for removal.");

        for (Consultation c : consultations) {
            if (c != null && c.getDate().isBefore(LocalDate.now())) {
                throw new RuntimeException(
                    "Cannot remove past consultation scheduled for " + c.getDate() + "."
                );
            }
        }

        patient.excluirConsulta(consultations);
    }
}
