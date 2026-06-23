package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class PatientServiceProxy {
    private final PatientService patientService;

    public PatientServiceProxy() {
        this.patientService = new PatientService();
    }

    public void registerPatient(Patient patient) throws SQLException {
        ensureManagerAccess("register a patient");
        patientService.registerPatient(patient);
    }

    public void updatePatient(Patient patient) throws SQLException {
        ensureManagerAccess("update a patient");
        patientService.updatePatient(patient);
    }

    public void removePatient(Patient patient) throws SQLException {
        ensureManagerAccess("remove a patient");
        patientService.removePatient(patient);
    }

    public Patient findById(long id) throws SQLException {
        return patientService.findById(id);
    }

    public Patient findByName(String name) throws SQLException {
        return patientService.findByName(name);
    }

    public Patient findByCPF(String cpf) throws SQLException {
        return patientService.findByCPF(cpf);
    }

    public ArrayList<Patient> listAll() throws SQLException {
        return patientService.listAll();
    }

    public void requestConsultation(Consultation consultation) throws SQLException {
        ensureManagerAccess("request a consultation");
        patientService.requestConsultation(consultation);
    }

    public void updateConsultation(Consultation consultation) throws SQLException {
        ensureManagerAccess("update a consultation");
        patientService.updateConsultation(consultation);
    }

    public void cancelConsultation(Consultation consultation) throws SQLException {
        ensureManagerAccess("cancel a consultation");
        patientService.cancelConsultation(consultation);
    }

    public ArrayList<Consultation> consultationHistory(Patient patient) throws SQLException {
        return patientService.consultationHistory(patient);
    }

    private void ensureManagerAccess(String action) {
        if (ServiceRoleContext.getCurrentRole() != ServiceRole.MANAGER && ServiceRoleContext.getCurrentRole() != ServiceRole.PATIENT) {
            throw new RuntimeException("Only a manager or patient can " + action + ".");
        }
    }
}