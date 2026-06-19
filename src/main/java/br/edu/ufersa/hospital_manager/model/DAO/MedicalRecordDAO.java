package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.time.LocalDateTime;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.util.Connector;

public class MedicalRecordDAO implements BaseDAO<MedicalRecord> {
    
    private Connection connection;

    public MedicalRecordDAO() {
        this.connection = Connector.getConnection();
    }
    
    public static final String INSERT_SQL = "INSERT INTO medical_records (date, observation, patient_id, doctor_id) VALUES (?, ?, ?, ?)";
    public static final String DELETE_SQL = "DELETE FROM medical_records WHERE id = ?;";
    public static final String UPDATE_SQL = "UPDATE medical_records SET date = ?, observation = ?, patient_id = ?, doctor_id = ? WHERE id = ?;";
    public static final String SELECT_ALL_SQL = "SELECT * FROM medical_records;";
    public static final String SELECT_BY_ID_SQL = "SELECT * FROM medical_records WHERE id = ?;";

    @Override
    public void create(MedicalRecord entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setDate(1, java.sql.Date.valueOf(entity.getDate()));
        ps.setString(2, entity.getObservation());
        ps.setLong(3, entity.getPatient().getId());
        ps.setLong(4, entity.getDoctor().getId());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            entity.setId(rs.getLong(1));
        }
    }

    @Override
    public void delete(MedicalRecord entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
        ps.setLong(1, entity.getId());
        ps.executeUpdate();

    }

    @Override
    public ArrayList<MedicalRecord> listAll() throws SQLException {
        Statement ps = connection.createStatement();
        ResultSet rs = ps.executeQuery(SELECT_ALL_SQL);

        ArrayList<MedicalRecord> medicalRecords = new ArrayList<>();

        while (rs.next()) {
            DoctorDAO doctorDAO = new DoctorDAO();
            PatientDAO patientDAO = new PatientDAO();
            Doctor doctor = doctorDAO.readById(rs.getLong("doctor_id"));
            Patient patient = patientDAO.readById(rs.getLong("patient_id"));

            MedicalRecord medicalRecord = new MedicalRecord(
                    rs.getString("observation"),
                    doctor,
                    patient
            );
            medicalRecord.setId(rs.getLong("id"));
            medicalRecord.setDate(rs.getDate("date").toLocalDate());
            medicalRecords.add(medicalRecord);
        }

        return medicalRecords;
    }

    @Override
    public MedicalRecord readById(long id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            DoctorDAO doctorDAO = new DoctorDAO();
            PatientDAO patientDAO = new PatientDAO();
            Doctor doctor = doctorDAO.readById(rs.getLong("doctor_id"));
            Patient patient = patientDAO.readById(rs.getLong("patient_id"));

            MedicalRecord medicalRecord = new MedicalRecord(
                    rs.getString("observation"),
                    doctor,
                    patient
            );
            medicalRecord.setId(rs.getLong("id"));
            medicalRecord.setDate(rs.getDate("date").toLocalDate());
            return medicalRecord;
        }

        throw new SQLException("Medical record with ID " + id + " not found.");
    }

    @Override
    public void update(MedicalRecord entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
        ps.setDate(1, java.sql.Date.valueOf(entity.getDate()));
        ps.setString(2, entity.getObservation());
        ps.setLong(3, entity.getPatient().getId());
        ps.setLong(4, entity.getDoctor().getId());
        ps.setLong(5, entity.getId());
        ps.executeUpdate();

    }

    public MedicalRecord readByDate(LocalDateTime date) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM medical_records WHERE date = ?;");
        ps.setDate(1, java.sql.Date.valueOf(date.toLocalDate()));
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            DoctorDAO doctorDAO = new DoctorDAO();
            PatientDAO patientDAO = new PatientDAO();
            Doctor doctor = doctorDAO.readById(rs.getLong("doctor_id"));
            Patient patient = patientDAO.readById(rs.getLong("patient_id"));

            MedicalRecord medicalRecord = new MedicalRecord(
                    rs.getString("observation"),
                    doctor,
                    patient
            );
            medicalRecord.setId(rs.getLong("id"));
            medicalRecord.setDate(rs.getDate("date").toLocalDate());
            return medicalRecord;

        } else {
            throw new SQLException("Medical record with date " + date + " not found.");
        }
    }

    public MedicalRecord readByPatient(Patient oPatient) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM medical_records WHERE patient_id = ?;");
        ps.setLong(1, oPatient.getId());
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            DoctorDAO doctorDAO = new DoctorDAO();
            PatientDAO patientDAO = new PatientDAO();
            Doctor doctor = doctorDAO.readById(rs.getLong("doctor_id"));
            Patient patient = patientDAO.readById(rs.getLong("patient_id"));

            MedicalRecord medicalRecord = new MedicalRecord(
                    rs.getString("observation"),
                    doctor,
                    patient
            );
            medicalRecord.setId(rs.getLong("id"));
            medicalRecord.setDate(rs.getDate("date").toLocalDate());
            return medicalRecord;
        }

        throw new SQLException("Medical record for patient not found.");
    }

    public ArrayList<MedicalRecord> readByDoctor(Doctor oDoctor) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM medical_records WHERE doctor_id = ?;");
        ps.setLong(1, oDoctor.getId());
        ResultSet rs = ps.executeQuery();

        ArrayList<MedicalRecord> medicalRecords = new ArrayList<>();

        while (rs.next()) {
            DoctorDAO doctorDAO = new DoctorDAO();
            PatientDAO patientDAO = new PatientDAO();
            Doctor doctor = doctorDAO.readById(rs.getLong("doctor_id"));
            Patient patient = patientDAO.readById(rs.getLong("patient_id"));

            MedicalRecord medicalRecord = new MedicalRecord(
                    rs.getString("observation"),
                    doctor,
                    patient
            );
            medicalRecord.setId(rs.getLong("id"));
            medicalRecord.setDate(rs.getDate("date").toLocalDate());
            medicalRecords.add(medicalRecord);
        }

        return medicalRecords;
    }
    
}
