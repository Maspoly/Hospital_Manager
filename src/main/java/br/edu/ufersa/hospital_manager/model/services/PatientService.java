package br.edu.ufersa.hospital_manager.model.services;

import br.edu.ufersa.hospital_manager.model.DAO.*;
import br.edu.ufersa.hospital_manager.model.entities.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

<<<<<<< HEAD
=======
import br.edu.ufersa.hospital_manager.model.DAO.ConsultationDAO;
import br.edu.ufersa.hospital_manager.model.DAO.MedicalRecordDAO;
import br.edu.ufersa.hospital_manager.model.DAO.PatientDAO;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

>>>>>>> 96ad7c6 (Linked screens to data base)
public class PatientService implements FindServices<Patient> {

    private final PatientDAO patientDAO;
    private final MedicalRecordDAO medicalRecordDAO;

    public PatientService() {
        this.patientDAO = new PatientDAO();
        this.medicalRecordDAO = new MedicalRecordDAO();
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

<<<<<<< HEAD
    public void removePatient(long id) throws SQLException {
        Patient patient = findById(id);
=======
    // Updates patient information only if the patient already exists.
    public void updatePatient(Patient patient) throws SQLException {
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }
        if (patientDAO.readById(patient.getId()) == null) {
            throw new RuntimeException("Patient not found.");
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
            throw new RuntimeException("Patient not found.");
        }

        consultationDAO.detachPatient(patient);
        medicalRecordDAO.detachPatient(patient);
>>>>>>> 96ad7c6 (Linked screens to data base)
        patientDAO.delete(patient);
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    public Patient updatePatient(Patient patient) throws SQLException {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        patientDAO.update(patient);
        return patient;
    }

    public void assignMedicalRecord(Patient patient, MedicalRecord medicalRecord) throws SQLException {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        if (medicalRecord == null) throw new RuntimeException("Medical record cannot be null.");
    

        if (medicalRecordDAO.readByPatient(patient) != null) {
            throw new RuntimeException("Patient already has an active medical record.");
        } else{
            medicalRecord.setPatient(patient);
            medicalRecordDAO.create(medicalRecord);
            patientDAO.update(patient);
        }
        
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
<<<<<<< HEAD
        ArrayList<Patient> patients = patientDAO.listAll();
        if (patients.isEmpty()) throw new RuntimeException("No patients registered in the system.");
        return patients;
    }

=======
        return patientDAO.listAll();
    }
    
>>>>>>> 96ad7c6 (Linked screens to data base)
    // ─── Consultations ────────────────────────────────────────────────────────

    public Consultation scheduleConsultation(Patient patient, Doctor doctor, LocalDateTime date, String status) {
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        if (doctor == null)  throw new RuntimeException("Doctor cannot be null.");
        if (date == null)    throw new RuntimeException("Date cannot be null.");
        if (date.isBefore(LocalDateTime.now())) throw new RuntimeException("Consultation date cannot be in the past.");
        Consultation consultation = new Consultation(patient, doctor, date, status);
        return consultation;
    }

    public Consultation updateConsultation(Patient patient, Consultation consultation,
                                            LocalDateTime newDate, String newStatus) {
        ConsultationService consultationService = new ConsultationService();

        if (patient == null)      throw new RuntimeException("Patient cannot be null.");
        if (consultation == null) throw new RuntimeException("Consultation cannot be null.");
        if (newDate == null)      throw new RuntimeException("New date cannot be null.");

        if (consultation.getDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Past consultations cannot be edited.");
        }
        try{
             return consultationService.rescheduleConsultation(consultation, newDate);
        }
        catch (SQLException e) {
            throw new IllegalArgumentException("Failed to reschedule consultation: " + e.getMessage());
        }
    }

    public void removeConsultations(Patient patient, Consultation[] consultations) {
        
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        if (consultations == null || consultations.length == 0)
            throw new RuntimeException("No consultations provided for removal.");
        
        for (Consultation c : consultations) {
            if (c != null && c.getDateTime().isBefore(LocalDateTime.now())) {
                throw new RuntimeException(
                    "Cannot remove past consultation scheduled for " + c.getDateTime() + "."
                );
            }
            if (c.getPatient() == patient){
                try {
                    new ConsultationService().removeConsultation(c);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to remove consultation: " + e.getMessage());
                }
            } else {
                throw new RuntimeException("Consultation does not belong to the specified patient.");
            }
        }
    }
}
