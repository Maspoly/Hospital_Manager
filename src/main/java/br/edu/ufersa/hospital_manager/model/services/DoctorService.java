package br.edu.ufersa.hospital_manager.model.services;

<<<<<<< Updated upstream
import java.sql.SQLException;

import br.edu.ufersa.hospital_manager.model.DAO.DoctorDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;

public class DoctorService implements FindServices<Doctor> {
    private DoctorDAO doctorDAO;
=======
import br.edu.ufersa.hospital_manager.model.DAO.DoctorDAO;
import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;

import java.sql.SQLException;
import java.util.ArrayList;

public class DoctorService implements FindServices<Doctor> {

    private final DoctorDAO doctorDAO;
>>>>>>> Stashed changes

    public DoctorService() {
        this.doctorDAO = new DoctorDAO();
    }

    // ─── Update ───────────────────────────────────────────────────────────────

<<<<<<< Updated upstream
    // Updates doctor's information only if the doctor already exists.
    public void updateDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }
        if (doctorDAO.readById(doctor.getId()) == null) {
            throw new RuntimeException("Doctor not found.");
        }
        doctorDAO.update(doctor);
=======
    public Doctor updateDoctor(Doctor doctor) throws SQLException {
        if (doctor == null) throw new RuntimeException("Doctor cannot be null.");
        doctorDAO.update(doctor);
        return doctor;
>>>>>>> Stashed changes
    }

    // ─── Searches ─────────────────────────────────────────────────────────────

    @Override
    public Doctor findById(long id) throws SQLException {
<<<<<<< Updated upstream
        if (id <= 0) {
            throw new RuntimeException("ID must be a positive number.");
        }
=======
        // readById lança SQLException se não encontrar — deixamos propagar ao Main
>>>>>>> Stashed changes
        return doctorDAO.readById(id);
    }

    @Override
<<<<<<< Updated upstream
    public Doctor findByName(String name) throws SQLException {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Name cannot be null or empty.");
        }
        return doctorDAO.readByName(name);
    }

    @Override
    public Doctor findByCPF(String cpf) throws SQLException {
        if (cpf == null || cpf.isBlank()) {
            throw new RuntimeException("CPF cannot be null or empty.");
        }
        return doctorDAO.readByCPF(cpf);
    }

    // ─── Medical Records ──────────────────────────────────────────────────────

    // Doctor writes a new medical record through MedicalRecordService.
    public void registerMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        MedicalRecordService medicalRecordService = new MedicalRecordService();
        medicalRecordService.registerMedicalRecord(medicalRecord);
    }

    // Doctor updates an existing medical record through MedicalRecordService.
    public void updateMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        MedicalRecordService medicalRecordService = new MedicalRecordService();
        medicalRecordService.updateMedicalRecord(medicalRecord);
    }

    // Doctor removes a medical record through MedicalRecordService.
    public void deleteMedicalRecord(MedicalRecord medicalRecord) throws SQLException {
        MedicalRecordService medicalRecordService = new MedicalRecordService();
        medicalRecordService.removeMedicalRecord(medicalRecord);
    }
}
=======
    public Doctor findByCPF(String cpf) throws SQLException {
        return doctorDAO.readByCPF(cpf);
    }

    public Doctor findByCouncilCode(String councilCode) throws SQLException {
        if (councilCode == null || councilCode.isBlank())
            throw new RuntimeException("CRM cannot be empty.");
        return doctorDAO.readByCouncilCode(councilCode);
    }

    @Override
    public Doctor findByName(String name) throws SQLException {
        return doctorDAO.readByName(name);
    }

    public ArrayList<Doctor> listAll() throws SQLException {
        ArrayList<Doctor> doctors = doctorDAO.listAll();
        if (doctors.isEmpty())
            throw new RuntimeException("No doctors found in the system.");
        return doctors;
    }

    // ─── Edit personal data ───────────────────────────────────────────────────

    public void editPersonalData(Doctor doctor, String name, String cpf,
                                  Address address, int consultationValue,
                                  String councilCode) {

        if (name == null || name.trim().isEmpty())
            throw new RuntimeException("Name cannot be empty.");
        if (cpf == null || !cpf.matches("\\d{11}"))
            throw new RuntimeException("CPF must contain exactly 11 numeric digits.");
        if (address == null)
            throw new RuntimeException("Address cannot be null.");
        if (address.getStreet() == null || address.getStreet().isBlank())
            throw new RuntimeException("Street cannot be empty.");
        if (address.getCity() == null || address.getCity().isBlank())
            throw new RuntimeException("City cannot be empty.");
        if (councilCode == null || councilCode.trim().isEmpty())
            throw new RuntimeException("Council code cannot be empty.");

        doctor.setName(name);
        doctor.setCPF(cpf);
        doctor.setAddress(address);
        doctor.setConsultationValue(consultationValue);
        doctor.setCouncilCode(councilCode);
    }

}
>>>>>>> Stashed changes
