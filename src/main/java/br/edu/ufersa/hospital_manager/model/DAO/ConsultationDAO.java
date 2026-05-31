package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.util.Connector;

public class ConsultationDAO implements BaseDAO<Consultation> {

    private Connection connection;

    public ConsultationDAO() {
        this.connection = Connector.getConnection();
    }

    public static final String INSERT_SQL = "INSERT INTO consultation (patient_id, doctor_id, date, status, medical_record_id) VALUES (?, ?, ?, ?, ?);";
    public static final String DELETE_SQL = "DELETE FROM consultation WHERE id = ?;";
    public static final String UPDATE_SQL = "UPDATE consultation SET patient_id = ?, doctor_id = ?, date = ?, status = ?, medical_record_id = ? WHERE id = ?;";
    public static final String SELECT_ALL_SQL = "SELECT * FROM consultation;";
    public static final String SELECT_BY_ID_SQL = "SELECT * FROM consultation WHERE id = ?;";
    public static final String SELECT_BY_PATIENT_SQL = "SELECT * FROM consultation WHERE patient_id = ?;";
    public static final String SELECT_BY_DOCTOR_SQL = "SELECT * FROM consultation WHERE doctor_id = ?;";
    public static final String SELECT_BY_STATUS_SQL = "SELECT * FROM consultation WHERE status = ?;";
    public static final String SELECT_BY_DATE_SQL = "SELECT * FROM consultation WHERE date = ?;";

    @Override
    public void create(Consultation entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setLong(1, entity.getPatient().getId());
        ps.setLong(2, entity.getDoctor().getId());
        ps.setDate(3, java.sql.Date.valueOf(entity.getDate()));
        ps.setString(4, entity.getStatus());

        // medical_record_id is null until consultation is completed
        if (entity.getMedicalRecord() != null) {
            ps.setLong(5, entity.getMedicalRecord().getId());
        } else {
            ps.setNull(5, java.sql.Types.BIGINT);
        }

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            entity.setId(rs.getLong(1)); // set the generated ID back to the entity
        }
    }

    @Override
    public void delete(Consultation entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
        ps.setLong(1, entity.getId());
        ps.executeUpdate();
    }

    @Override
    public void update(Consultation entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
        ps.setLong(1, entity.getPatient().getId());
        ps.setLong(2, entity.getDoctor().getId());
        ps.setDate(3, java.sql.Date.valueOf(entity.getDate()));
        ps.setString(4, entity.getStatus());

        if (entity.getMedicalRecord() != null) {
            ps.setLong(5, entity.getMedicalRecord().getId());
        } else {
            ps.setNull(5, java.sql.Types.BIGINT);
        }

        ps.setLong(6, entity.getId());
        ps.executeUpdate();
    }

    @Override
    public ArrayList<Consultation> listAll() throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(SELECT_ALL_SQL);
        return buildList(rs);
    }

    @Override
    public Consultation readById(long id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return buildConsultation(rs);
        }

        throw new SQLException("Consultation with ID " + id + " not found.");
    }

    public ArrayList<Consultation> readByPatient(Patient patient) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_PATIENT_SQL);
        ps.setLong(1, patient.getId());
        ResultSet rs = ps.executeQuery();
        return buildList(rs);
    }

    public ArrayList<Consultation> readByDoctor(Doctor doctor) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_DOCTOR_SQL);
        ps.setLong(1, doctor.getId());
        ResultSet rs = ps.executeQuery();
        return buildList(rs);
    }

    public ArrayList<Consultation> readByStatus(String status) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_STATUS_SQL);
        ps.setString(1, status.toUpperCase());
        ResultSet rs = ps.executeQuery();
        return buildList(rs);
    }

    public ArrayList<Consultation> readByDate(LocalDate date) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_DATE_SQL);
        ps.setDate(1, java.sql.Date.valueOf(date));
        ResultSet rs = ps.executeQuery();
        return buildList(rs);
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    /**
     * Builds a single Consultation from the current ResultSet row.
     */
    private Consultation buildConsultation(ResultSet rs) throws SQLException {
        DoctorDAO doctorDAO = new DoctorDAO();
        PatientDAO patientDAO = new PatientDAO();

        Doctor doctor = doctorDAO.readById(rs.getLong("doctor_id"));
        Patient patient = patientDAO.readById(rs.getLong("patient_id"));

        Consultation consultation = new Consultation(
                patient,
                doctor,
                rs.getDate("date").toLocalDate(),
                rs.getString("status")
        );

        consultation.setId(rs.getLong("id"));

        // medical_record_id may be null
        long medicalRecordId = rs.getLong("medical_record_id");
        if (!rs.wasNull()) {
            MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
            MedicalRecord medicalRecord = medicalRecordDAO.readById(medicalRecordId);
            consultation.setMedicalRecord(medicalRecord);
        }

        return consultation;
    }

    /**
     * Iterates over a ResultSet and builds an ArrayList of Consultations.
     */
    private ArrayList<Consultation> buildList(ResultSet rs) throws SQLException {
        ArrayList<Consultation> consultations = new ArrayList<>();
        while (rs.next()) {
            consultations.add(buildConsultation(rs));
        }
        return consultations;
    }
}