package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.util.Connector;

public class PatientDAO implements BaseDAO<Patient> {

    public static final String INSERT_SQL = "INSERT INTO patient (name, cpf, password, address_id) VALUES (?, ?, ?, ?);";
    public static final String DELETE_SQL = "DELETE FROM patient WHERE id = ?;";
    public static final String UPDATE_SQL = "UPDATE patient SET name = ?, cpf = ?, password = ?, address_id = ? WHERE id = ?;";
    public static final String SELECT_ALL_SQL = "SELECT * FROM patient;";
    public static final String SELECT_BY_CPF_SQL = "SELECT * FROM patient WHERE cpf = ?;";
    public static final String SELECT_BY_ID_SQL = "SELECT * FROM patient WHERE id = ?;";
    public static final String SELECT_BY_NAME_SQL = "SELECT * FROM patient WHERE LOWER(name) LIKE ?;";

    private Connection connection;

    public PatientDAO() {
    }

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = Connector.getConnection();
        }

        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        return connection;
    }

    @Override
    public Patient readById(long id) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_ID_SQL);

        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Patient patient = new Patient(rs.getString("name"), rs.getString("cpf"), address, rs.getString("password"), true);

            patient.setId(rs.getLong("id"));
            return patient;
        }

        return null;
    }

    @Override
    public void update(Patient entity) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(UPDATE_SQL);

        ps.setString(1, entity.getName());
        ps.setString(2, entity.getCPF());
        ps.setString(3, entity.getPasswordHash());
        ps.setLong(4, entity.getAddress().getId());
        ps.setLong(5, entity.getId());

        ps.executeUpdate();
    }

    @Override
    public void create(Patient entity) throws SQLException {
        AddressDAO addressDAO = new AddressDAO();

        if (entity.getAddress().getId() <= 0) {
            addressDAO.create(entity.getAddress());
        }

        PreparedStatement ps = getConnection().prepareStatement(
                INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS
        );

        ps.setString(1, entity.getName());
        ps.setString(2, entity.getCPF());
        ps.setString(3, entity.getPasswordHash());
        ps.setLong(4, entity.getAddress().getId());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if (rs.next()) {
            entity.setId(rs.getLong(1));
        }
    }

    @Override
    public void delete(Patient entity) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(DELETE_SQL);

        ps.setLong(1, entity.getId());

        ps.executeUpdate();
    }

    @Override
    public ArrayList<Patient> listAll() throws SQLException {
        Statement ps = getConnection().createStatement();

        ResultSet rs = ps.executeQuery(SELECT_ALL_SQL);

        ArrayList<Patient> patients = new ArrayList<>();

        AddressDAO addressDAO = new AddressDAO();

        while (rs.next()) {
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Patient patient = new Patient(rs.getString("name"), rs.getString("cpf"), address, rs.getString("password"), true);

            patient.setId(rs.getLong("id"));
            patients.add(patient);
        }

        return patients;
    }

    public Patient readByCPF(String cpf) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_CPF_SQL);

        ps.setString(1, cpf);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Patient patient = new Patient(rs.getString("name"), rs.getString("cpf"), address, rs.getString("password"), true);

            patient.setId(rs.getLong("id"));
            return patient;
        }

        return null;
    }

    public Patient readByName(String name) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_NAME_SQL);

        ps.setString(1, "%" + name.toLowerCase() + "%");

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Patient patient = new Patient(rs.getString("name"), rs.getString("cpf"), address, rs.getString("password"));

            patient.setId(rs.getLong("id"));
            return patient;
        }

        return null;
    }

}