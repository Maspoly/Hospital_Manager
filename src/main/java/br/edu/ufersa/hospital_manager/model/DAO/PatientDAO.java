package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.util.Connector;

public class PatientDAO implements BaseDAO<Patient> {

    private Connection connection;

    public PatientDAO() {
        this.connection = Connector.getConnection();
    }

    public static final String INSERT_SQL = "INSERT INTO patient (name, cpf, adress, medical_record_id) VALUES (?, ?, ?, ?)";
    public static final String DELETE_SQL = "DELETE FROM patient WHERE id = ?;";
    public static final String UPDATE_SQL = "UPDATE patient SET name = ?, cpf = ?, adress = ?, medical_record_id = ? WHERE id = ?;";
    public static final String SELECT_ALL_SQL = "SELECT * FROM patient;";
    public static final String SELECT_BY_ID_SQL = "SELECT * FROM patient WHERE id = ?;";
    public static final String SELECT_BY_CPF_SQL = "SELECT * FROM patient WHERE cpf = ?;";
    public static final String SELECT_BY_NAME_SQL  = "SELECT * FROM patient WHERE name = ?;";

    @Override
    public void create(Patient entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, entity.getName());
        ps.setString(2, entity.getCPF());
        ps.setString(3, entity.getAddress());

        // medical_record_id can be null if the patient does not yet have a medical record.
        if (entity.getMedicalRecord() != null) {
            ps.setLong(4, entity.getMedicalRecord().getId());
        } else {
            ps.setNull(4, java.sql.Types.BIGINT);
        }

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            entity.setId(rs.getLong(1)); // set the generated ID back to the entity
        }
    }

    @Override
    public void delete(Patient entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
        ps.setLong(1, entity.getId());
        ps.executeUpdate();
    }

    @Override
    public void update(Patient entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
        ps.setString(1, entity.getName());
        ps.setString(2, entity.getCPF());
        ps.setString(3, entity.getAddress());

        if (entity.getMedicalRecord() != null) {
            ps.setLong(4, entity.getMedicalRecord().getId());
        } else {
            ps.setNull(4, java.sql.Types.BIGINT);
        }

        ps.setLong(5, entity.getId());
        ps.executeUpdate();
    }

    @Override
    public ArrayList<Patient> listAll() throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(SELECT_ALL_SQL);

        ArrayList<Patient> patients = new ArrayList<>();

        while (rs.next()) {
            // Busca o prontuário associado, se existir
            MedicalRecord medicalRecord = null;
            long medicalRecordId = rs.getLong("medical_record_id");
            if (!rs.wasNull()) {
                MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
                medicalRecord = medicalRecordDAO.readById(medicalRecordId);
            }

            Patient patient = new Patient(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    rs.getString("adress"),
                    medicalRecord
            );
            patient.setId(rs.getLong("id"));
            patients.add(patient);
        }

        return patients;
    }

    @Override
    public Patient readById(long id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            MedicalRecord medicalRecord = null;
            long medicalRecordId = rs.getLong("medical_record_id");
            if (!rs.wasNull()) {
                MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
                medicalRecord = medicalRecordDAO.readById(medicalRecordId);
            }

            Patient patient = new Patient(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    rs.getString("adress"),
                    medicalRecord
            );
            patient.setId(rs.getLong("id"));
            return patient;
        }

        throw new SQLException("Patient with ID " + id + " not found.");
    }

    public Patient readByCPF(String cpf) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_CPF_SQL);
        ps.setString(1, cpf);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            MedicalRecord medicalRecord = null;
            long medicalRecordId = rs.getLong("medical_record_id");
            if (!rs.wasNull()) {
                MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
                medicalRecord = medicalRecordDAO.readById(medicalRecordId);
            }

            Patient patient = new Patient(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    rs.getString("adress"),
                    medicalRecord
            );
            patient.setId(rs.getLong("id"));
            return patient;
        }

        throw new SQLException("Patient with CPF " + cpf + " not found.");
    }

    public Patient readByName(String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_NAME_SQL);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            MedicalRecord medicalRecord = null;
            long medicalRecordId = rs.getLong("medical_record_id");
            if (!rs.wasNull()) {
                MedicalRecordDAO medicalRecordDAO = new MedicalRecordDAO();
                medicalRecord = medicalRecordDAO.readById(medicalRecordId);
            }

            Patient patient = new Patient(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    rs.getString("adress"),
                    medicalRecord
            );
            patient.setId(rs.getLong("id"));
            return patient;
        }

        throw new SQLException("Patient with name " + name + " not found.");
    }
}