package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.util.Connector;

public class ConsultationDAO implements BaseDAO<Consultation> {
    public static final String INSERT_SQL = "INSERT INTO consultation (patient_id, doctor_id, date_time, status) VALUES (?, ?, ?, ?);";
    public static final String  DELETE_SQL = "DELETE FROM consultation WHERE id = ?;";
    public static final String  UPDATE_SQL = "UPDATE consultation SET patient_id = ?, doctor_id = ?, date_time = ?, status = ? WHERE id = ?;";
    public static final String  SELECT_ALL_SQL = "SELECT * FROM consultation;";
    public static final String  SELECT_BY_ID_SQL = "SELECT * FROM consultation WHERE id = ?;";
    public static final String  SELECT_BY_PATIENT_SQL = "SELECT * FROM consultation WHERE patient_id = ?;";
    public static final String  SELECT_BY_DOCTOR_SQL = "SELECT * FROM consultation WHERE doctor_id = ?;";
    public static final String  SELECT_BY_STATUS_SQL = "SELECT * FROM consultation WHERE status = ?;";
    public static final String  SELECT_BY_DATE_TIME_SQL = "SELECT * FROM consultation WHERE date_time = ?;";
    public static final String  SELECT_BY_PATIENT_AND_DATE_TIME_SQL = "SELECT * FROM consultation WHERE patient_id = ? AND date_time = ?;";
    public static final String  SELECT_BY_DOCTOR_AND_DATE_TIME_SQL = "SELECT * FROM consultation WHERE doctor_id = ? AND date_time = ?;";

    public ConsultationDAO() {
        ensureNullableForeignKeys();
    }

    private void ensureNullableForeignKeys() {
        try (Statement statement = Connector.getConnection().createStatement()) {
            statement.executeUpdate("ALTER TABLE consultation MODIFY doctor_id BIGINT NULL;");
            statement.executeUpdate("ALTER TABLE consultation MODIFY patient_id BIGINT NULL;");
        } catch (SQLException exception) {
            // If the database is unavailable or already compatible, keep startup resilient.
        }
    }

    @Override
    public void create(Consultation entity) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
        if (entity.getPatient() == null) {
            ps.setNull(1, java.sql.Types.BIGINT);
        } else {
            ps.setLong(1, entity.getPatient().getId());
        }
        if (entity.getDoctor() == null) {
            ps.setNull(2, java.sql.Types.BIGINT);
        } else {
            ps.setLong(2, entity.getDoctor().getId());
        }
        ps.setObject(3, entity.getDateTime());
        ps.setString(4, entity.getStatus());
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            entity.setId(rs.getLong(1)); // set the generated ID back to the entity
        }
    }

    @Override
    public void delete(Consultation entity) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement(DELETE_SQL);

        ps.setLong(1, entity.getId());

        ps.executeUpdate();
    }

    @Override
    public ArrayList<Consultation> listAll() throws SQLException {
        Statement ps = Connector.getConnection().createStatement();
        ResultSet rs = ps.executeQuery(SELECT_ALL_SQL);

        ArrayList<Consultation> consultations = new ArrayList<>();

        DoctorDAO doctorDAO = new DoctorDAO();
        PatientDAO patientDAO = new PatientDAO();
        while (rs.next()) {
            Doctor doctor = readDoctorIfPresent(doctorDAO, rs, "doctor_id");
            Patient patient = readPatientIfPresent(patientDAO, rs, "patient_id");

            Consultation consultation = new Consultation(patient, doctor, rs.getObject("date_time", LocalDateTime.class), rs.getString("status"));
            consultation.setId(rs.getLong("id"));
            consultations.add(consultation);
        }

        return consultations;
    }

    @Override
    public Consultation readById(long id) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement(SELECT_BY_ID_SQL);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            DoctorDAO doctorDAO = new DoctorDAO();
            PatientDAO patientDAO = new PatientDAO();
            Doctor doctor = readDoctorIfPresent(doctorDAO, rs, "doctor_id");
            Patient patient = readPatientIfPresent(patientDAO, rs, "patient_id");

            Consultation consultation = new Consultation(patient, doctor, rs.getObject("date_time", LocalDateTime.class), rs.getString("status"));
            consultation.setId(rs.getLong("id"));
            return consultation;
        }

        return null;
    }

    @Override
    public void update(Consultation entity) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement(UPDATE_SQL);
        if (entity.getPatient() == null) {
            ps.setNull(1, java.sql.Types.BIGINT);
        } else {
            ps.setLong(1, entity.getPatient().getId());
        }
        if (entity.getDoctor() == null) {
            ps.setNull(2, java.sql.Types.BIGINT);
        } else {
            ps.setLong(2, entity.getDoctor().getId());
        }
        ps.setObject(3, entity.getDateTime());
        ps.setString(4, entity.getStatus());
        ps.setLong(5, entity.getId());
        ps.executeUpdate();

    }

    public void detachDoctor(Doctor doctor) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement("UPDATE consultation SET doctor_id = NULL WHERE doctor_id = ?;");
        ps.setLong(1, doctor.getId());
        ps.executeUpdate();
    }

    public void detachPatient(Patient patient) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement("UPDATE consultation SET patient_id = NULL WHERE patient_id = ?;");
        ps.setLong(1, patient.getId());
        ps.executeUpdate();
    }

    public ArrayList<Consultation> readByPatient(Patient oPatient) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement(SELECT_BY_PATIENT_SQL);
        ps.setLong(1, oPatient.getId());
        ResultSet rs = ps.executeQuery();

        ArrayList<Consultation> consultations = new ArrayList<>();

        DoctorDAO doctorDAO = new DoctorDAO();
        PatientDAO patientDAO = new PatientDAO();
        while (rs.next()) {
            Doctor doctor = doctorDAO.readById(rs.getLong("doctor_id"));
            Patient patient = patientDAO.readById(rs.getLong("patient_id"));

            Consultation consultation = new Consultation(patient, doctor, rs.getObject("date_time", LocalDateTime.class), rs.getString("status"));
            consultation.setId(rs.getLong("id"));
            consultations.add(consultation);
        }

        return consultations;
    }

    public ArrayList<Consultation> readByDoctor(Doctor oDoctor) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement(SELECT_BY_DOCTOR_SQL);
        ps.setLong(1, oDoctor.getId());
        ResultSet rs = ps.executeQuery();

        ArrayList<Consultation> consultations = new ArrayList<>();

        DoctorDAO doctorDAO = new DoctorDAO();
        PatientDAO patientDAO = new PatientDAO();
        while (rs.next()) {
            Doctor doctor = readDoctorIfPresent(doctorDAO, rs, "doctor_id");
            Patient patient = readPatientIfPresent(patientDAO, rs, "patient_id");

            Consultation consultation = new Consultation(patient, doctor, rs.getObject("date_time", LocalDateTime.class), rs.getString("status"));
            consultation.setId(rs.getLong("id"));
            consultations.add(consultation);
        }

        return consultations;
    }

    public ArrayList<Consultation> readByStatus(String status) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement(SELECT_BY_STATUS_SQL);
        ps.setString(1, status);
        ResultSet rs = ps.executeQuery();

        ArrayList<Consultation> consultations = new ArrayList<>();

        DoctorDAO doctorDAO = new DoctorDAO();
        PatientDAO patientDAO = new PatientDAO();
        while (rs.next()) {
            Doctor doctor = readDoctorIfPresent(doctorDAO, rs, "doctor_id");
            Patient patient = readPatientIfPresent(patientDAO, rs, "patient_id");

            Consultation consultation = new Consultation(patient, doctor, rs.getObject("date_time", LocalDateTime.class), rs.getString("status"));
            consultation.setId(rs.getLong("id"));
            consultations.add(consultation);
        }

        return consultations;
    }

    public ArrayList<Consultation> readByDateTime(LocalDateTime dateTime) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement(SELECT_BY_DATE_TIME_SQL);
        ps.setObject(1, dateTime);
        ResultSet rs = ps.executeQuery();

        ArrayList<Consultation> consultations = new ArrayList<>();

        DoctorDAO doctorDAO = new DoctorDAO();
        PatientDAO patientDAO = new PatientDAO();
        while (rs.next()) {
            Doctor doctor = doctorDAO.readById(rs.getLong("doctor_id"));
            Patient patient = patientDAO.readById(rs.getLong("patient_id"));

            Consultation consultation = new Consultation(patient, doctor, rs.getObject("date_time", LocalDateTime.class), rs.getString("status"));
            consultation.setId(rs.getLong("id"));
            consultations.add(consultation);
        }

        return consultations;
    }

    public Consultation readByPatientAndDateTime(Patient oPatient, LocalDateTime dateTime) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement(SELECT_BY_PATIENT_AND_DATE_TIME_SQL);
        ps.setLong(1, oPatient.getId());
        ps.setObject(2, dateTime);
        ResultSet rs = ps.executeQuery();

        DoctorDAO doctorDAO = new DoctorDAO();
        PatientDAO patientDAO = new PatientDAO();
        if (rs.next()) {
            Doctor doctor = readDoctorIfPresent(doctorDAO, rs, "doctor_id");
            Patient patient = readPatientIfPresent(patientDAO, rs, "patient_id");

            Consultation consultation = new Consultation(patient, doctor, rs.getObject("date_time", LocalDateTime.class), rs.getString("status"));
            consultation.setId(rs.getLong("id"));
            return consultation;
        }

        return null;
    }

    public Consultation readByDoctorAndDateTime(Doctor oDoctor, LocalDateTime dateTime) throws SQLException {
        PreparedStatement ps = Connector.getConnection().prepareStatement(SELECT_BY_DOCTOR_AND_DATE_TIME_SQL);
        ps.setLong(1, oDoctor.getId());
        ps.setObject(2, dateTime);
        ResultSet rs = ps.executeQuery();

        DoctorDAO doctorDAO = new DoctorDAO();
        PatientDAO patientDAO = new PatientDAO();
        if (rs.next()) {
            Doctor doctor = readDoctorIfPresent(doctorDAO, rs, "doctor_id");
            Patient patient = readPatientIfPresent(patientDAO, rs, "patient_id");

            Consultation consultation = new Consultation(patient, doctor, rs.getObject("date_time", LocalDateTime.class), rs.getString("status"));
            consultation.setId(rs.getLong("id"));
            return consultation;
        }

        return null;
    }

    private Doctor readDoctorIfPresent(DoctorDAO doctorDAO, ResultSet rs, String column) throws SQLException {
        long id = rs.getLong(column);
        if (rs.wasNull()) {
            return null;
        }
        return doctorDAO.readById(id);
    }

    private Patient readPatientIfPresent(PatientDAO patientDAO, ResultSet rs, String column) throws SQLException {
        long id = rs.getLong(column);
        if (rs.wasNull()) {
            return null;
        }
        return patientDAO.readById(id);
    }
    
}
