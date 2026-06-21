package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.util.Connector;

public class DoctorDAO implements BaseDAO<Doctor> {

    private Connection connection;

    public DoctorDAO() {
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

    public static final String INSERT_SQL = "INSERT INTO doctor (name, cpf, password, address_id, consultation_value, council_code) VALUES (?, ?, ?, ?, ?, ?);";
    public static final String DELETE_SQL = "DELETE FROM doctor WHERE id = ?;";
    public static final String UPDATE_SQL = "UPDATE doctor SET name = ?, cpf = ?, password = ?, address_id = ?, consultation_value = ?, council_code = ? WHERE id = ?;";
    public static final String SELECT_ALL_SQL = "SELECT * FROM doctor;";
    public static final String SELECT_BY_CPF_SQL = "SELECT * FROM doctor WHERE cpf = ?;";
    public static final String SELECT_BY_ID_SQL = "SELECT * FROM doctor WHERE id = ?;";
    public static final String SELECT_BY_NAME_SQL = "SELECT * FROM doctor WHERE LOWER(name) LIKE ?;";
    public static final String SELECT_BY_COUNCIL_CODE_SQL = "SELECT * FROM doctor WHERE council_code = ?;";

    @Override
    public void create(Doctor entity) throws SQLException {
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
        ps.setFloat(5, entity.getConsultationValue());
        ps.setString(6, entity.getCouncilCode());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if (rs.next()) {
            entity.setId(rs.getLong(1));
        }
    }

    @Override
    public void delete(Doctor entity) throws SQLException {
        long addressId = entity.getAddress().getId();
        PreparedStatement ps = getConnection().prepareStatement(DELETE_SQL);
        ps.setLong(1, entity.getId());
        ps.executeUpdate();
        ps = getConnection().prepareStatement("DELETE FROM addresses WHERE id = ?;");
        ps.setLong(1, addressId);
        ps.executeUpdate();
    }

    @Override
    public ArrayList<Doctor> listAll() throws SQLException {
        Statement ps = getConnection().createStatement();
        ResultSet rs = ps.executeQuery(SELECT_ALL_SQL);

        ArrayList<Doctor> doctors = new ArrayList<>();
        AddressDAO addressDAO = new AddressDAO();

        while (rs.next()) {
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Doctor doctor = new Doctor(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    address,
                    rs.getString("password"),
                    true,
                    rs.getFloat("consultation_value"),
                    rs.getString("council_code")
            );

            doctor.setId(rs.getLong("id"));
            doctors.add(doctor);
        }

        return doctors;
    }

    @Override
    public void update(Doctor entity) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(UPDATE_SQL);

        ps.setString(1, entity.getName());
        ps.setString(2, entity.getCPF());
        ps.setString(3, entity.getPasswordHash());
        ps.setLong(4, entity.getAddress().getId());
        ps.setFloat(5, entity.getConsultationValue());
        ps.setString(6, entity.getCouncilCode());
        ps.setLong(7, entity.getId());

        ps.executeUpdate();
    }

    @Override
    public Doctor readById(long id) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_ID_SQL);

        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Doctor doctor = new Doctor(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    address,
                    rs.getString("password"),
                    true,
                    rs.getFloat("consultation_value"),
                    rs.getString("council_code")
            );

            doctor.setId(rs.getLong("id"));
            return doctor;
        }

        return null;
    }

    public Doctor readByCPF(String cpf) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_CPF_SQL);

        ps.setString(1, cpf);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Doctor doctor = new Doctor(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    address,
                    rs.getString("password"),
                    true,
                    rs.getFloat("consultation_value"),
                    rs.getString("council_code")
            );

            doctor.setId(rs.getLong("id"));
            return doctor;
        }

        return null;
    }

    public Doctor readByName(String name) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_NAME_SQL);

        ps.setString(1, "%" + name.toLowerCase() + "%");

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Doctor doctor = new Doctor(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    address,
                    rs.getString("password"),
                    true,
                    rs.getFloat("consultation_value"),
                    rs.getString("council_code")
            );

            doctor.setId(rs.getLong("id"));
            return doctor;
        }

        return null;
    }

    public Doctor readByCouncilCode(String councilCode) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_COUNCIL_CODE_SQL);

        ps.setString(1, councilCode);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Doctor doctor = new Doctor(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    address,
                    rs.getString("password"),
                    true,
                    rs.getFloat("consultation_value"),
                    rs.getString("council_code")
            );

            doctor.setId(rs.getLong("id"));
            return doctor;
        }

        return null;
    }
}