package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.DAO.ConsultationDAO;
import br.edu.ufersa.hospital_manager.model.DAO.MedicalRecordDAO;
import br.edu.ufersa.hospital_manager.model.DAO.PatientDAO;
import br.edu.ufersa.hospital_manager.model.exceptions.DuplicateEntryException;
import br.edu.ufersa.hospital_manager.model.exceptions.EntityNotFoundException;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class PatientService implements FindServices<Patient> {
    private PatientDAO patientDAO;
    private ConsultationService consultationService;

    public PatientService() {
        this.patientDAO = new PatientDAO();
        this.consultationService = new ConsultationService();
    }

    // ─── Registration ─────────────────────────────────────────────────────────

    // Registers a new patient.
    // CPF must be unique.
    public void registerPatient(Patient patient) throws SQLException {
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }
        if (patientDAO.readByCPF(patient.getCPF()) != null) {
            throw new DuplicateEntryException("CPF", patient.getCPF());
        }

        patientDAO.create(patient);
    }

    // ─── Update And Removal ───────────────────────────────────────────────────

    // Updates patient information only if the patient already exists.
    public void updatePatient(Patient patient) throws SQLException {
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }
        if (patientDAO.readById(patient.getId()) == null) {
            throw new EntityNotFoundException("Paciente", String.valueOf(patient.getId()));
        }
        patientDAO.update(patient);

    }

    // Permanently removes a patient from the database.
    public void removePatient(Patient patient) throws SQLException {
        ConsultationDAO consultationDAO = new ConsultationDAO();
        MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();

        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }
        if (patientDAO.readById(patient.getId()) == null) {
            throw new EntityNotFoundException("Paciente", String.valueOf(patient.getId()));
        }

        consultationDAO.detachPatient(patient);
        medicalRecordDAO.detachPatient(patient);
        patientDAO.delete(patient);

    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    @Override
    public Patient findById(long id) throws SQLException {
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
        return patientDAO.readById(id);
    }

    @Override
    public Patient findByName(String name) throws SQLException {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Name cannot be null or empty.");
        }
        return patientDAO.readByName(name);

    }

    @Override
    public Patient findByCPF(String cpf) throws SQLException {
        if (cpf == null || cpf.isBlank()) {
            throw new RuntimeException("CPF cannot be null or empty.");
        }
        return patientDAO.readByCPF(cpf);
    }

    public ArrayList<Patient> listAll() throws SQLException {
        return patientDAO.listAll();
    }
    
    // ─── Consultations ────────────────────────────────────────────────────────

    // Patient requests a new consultation.
    // Scheduling rules are handled by ConsultationService.
    public void requestConsultation(Consultation consultation) throws SQLException {
        consultationService.scheduleConsultation(consultation);
    }

    // Updates a consultation through ConsultationService.
    public void updateConsultation(Consultation consultation) throws SQLException {
        consultationService.updateConsultation(consultation);
    }

    // Cancels a consultation without removing it from the database.
    public void cancelConsultation(Consultation consultation) throws SQLException {
        consultationService.cancelConsultation(consultation);
    }

    // Lists all consultations related to a patient.
    public ArrayList<Consultation> consultationHistory(Patient patient) throws SQLException {
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }
        if (patient.getId() <= 0) {
            throw new EntityNotFoundException("Paciente", String.valueOf(patient.getId()));
        }
        return consultationService.findByPatient(patient);
    }
}