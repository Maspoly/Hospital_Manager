package br.edu.ufersa.hospital_manager.model.services;

<<<<<<< Updated upstream
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

=======
>>>>>>> Stashed changes
import br.edu.ufersa.hospital_manager.model.DAO.MedicalRecordDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

<<<<<<< Updated upstream
public class MedicalRecordService {
    private MedicalRecordDAO medicalRecordDAO;
=======
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class MedicalRecordService {

    private final MedicalRecordDAO medicalRecordDAO;
>>>>>>> Stashed changes

    public MedicalRecordService() {
        this.medicalRecordDAO = new MedicalRecordDAO();
    }

<<<<<<< Updated upstream
    // ─── Registration ─────────────────────────────────────────────────────────

    // Creates a new medical record.
    public void registerMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }

        if (medicalRecordDAO.readById(medicalRecord.getId()) != null) {
            throw new RuntimeException("A medical record with this ID already exists.");
        }
        medicalRecordDAO.create(medicalRecord);
    }

    // ─── Update And Removal ───────────────────────────────────────────────────

    // Updates an existing medical record.
    public void updateMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }
        if (medicalRecordDAO.readById(medicalRecord.getId()) == null) {
            throw new RuntimeException("Medical record not found.");
        }

        medicalRecordDAO.update(medicalRecord);
    }

    // Permanently removes a medical record from the database.
    public void removeMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) {
            throw new RuntimeException("Medical record cannot be null.");
        }
        if (medicalRecordDAO.readById(medicalRecord.getId()) == null) {
            throw new RuntimeException("Medical record not found.");
        }

        medicalRecordDAO.delete(medicalRecord);

=======
    // ─── Create ───────────────────────────────────────────────────────────────

    public MedicalRecord registerMedicalRecord(String observation, Doctor doctor, Patient patient) throws SQLException {

        // DAO lança SQLException quando NÃO encontra.
        // Se NÃO lançou = prontuário já existe hoje = bloqueamos.
        try {
            MedicalRecord existing = medicalRecordDAO.readByPatient(patient);
            if (existing.getDate().isEqual(LocalDate.now())) {
                throw new RuntimeException(
                    "Patient already has a medical record registered today (" + LocalDate.now() + ")."
                );
            }
        } catch (SQLException ignored) {
            // SQLException = não encontrou prontuário = pode criar
        }

        MedicalRecord record = new MedicalRecord(observation, doctor, patient);
        medicalRecordDAO.create(record);
        return record;
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    public void removeMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        if (medicalRecord == null) throw new RuntimeException("Medical record cannot be null.");

        if (!medicalRecord.getDate().isEqual(LocalDate.now())) {
            throw new RuntimeException("Only medical records created today can be deleted.");
        }
        medicalRecordDAO.delete(medicalRecord);
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    public MedicalRecord updateObservation(MedicalRecord medicalRecord, String newObservation) throws SQLException {
        if (medicalRecord == null) throw new RuntimeException("Medical record cannot be null.");
        if (newObservation == null || newObservation.isBlank())
            throw new RuntimeException("New observation cannot be empty.");

        if (medicalRecord.getDate().isBefore(LocalDate.now().minusDays(30))) {
            throw new RuntimeException("Medical records older than 30 days cannot be edited.");
        }

        medicalRecord.setObservation(newObservation);
        medicalRecordDAO.update(medicalRecord);
        return medicalRecord;
>>>>>>> Stashed changes
    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    public MedicalRecord findById(long id) throws SQLException {
<<<<<<< Updated upstream
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
=======
        if (id <= 0) throw new RuntimeException("ID must be a positive number.");
>>>>>>> Stashed changes
        return medicalRecordDAO.readById(id);
    }

    public MedicalRecord findByPatient(Patient patient) throws SQLException {
<<<<<<< Updated upstream
        if (patient == null) {
            throw new RuntimeException("Patient cannot be null.");
        }

        if (patient.getId() <= 0) {
            throw new RuntimeException("Patient not found.");
        }

        return medicalRecordDAO.readByPatient(patient);
    }

    public ArrayList<MedicalRecord> findByDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }

        if (doctor.getId() <= 0) {
            throw new RuntimeException("Doctor not found.");
        }
        return medicalRecordDAO.readByDoctor(doctor);
    }

    public ArrayList<MedicalRecord> findByDate(LocalDate date) throws SQLException {
        if (date == null) {
            throw new RuntimeException("Date cannot be null.");
        }
        return medicalRecordDAO.readByDate(date); 
    }
}
=======
        if (patient == null) throw new RuntimeException("Patient cannot be null.");
        return medicalRecordDAO.readByPatient(patient);
    }

    public MedicalRecord findByDate(LocalDate date) throws SQLException {
        if (date == null) throw new RuntimeException("Date cannot be null.");
        if (date.isAfter(LocalDate.now())) throw new RuntimeException("Date cannot be in the future.");
        return medicalRecordDAO.readByDate(date);
    }

    public ArrayList<MedicalRecord> findByDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) throw new RuntimeException("Doctor cannot be null.");
        ArrayList<MedicalRecord> records = medicalRecordDAO.readByDoctor(doctor);
        if (records.isEmpty()) throw new RuntimeException("No medical records found for this doctor.");
        return records;
    }

    public ArrayList<MedicalRecord> listAll() throws SQLException {
        ArrayList<MedicalRecord> records = medicalRecordDAO.listAll();
        if (records.isEmpty()) throw new RuntimeException("No medical records registered in the system.");
        return records;
    }
}
>>>>>>> Stashed changes
