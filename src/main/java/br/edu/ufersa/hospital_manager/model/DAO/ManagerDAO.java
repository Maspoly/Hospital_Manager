package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Manager;
import br.edu.ufersa.hospital_manager.util.Connector;

public class ManagerDAO implements BaseDAO<Manager> {

    
    private Connection connection;

    public ManagerDAO() {
        this.connection = Connector.getConnection();
    }
    
    public static final String INSERT_SQL = "INSERT INTO manager (name, cpf, address_id) VALUES (?, ?, ?);";
    public static final String  DELETE_SQL = "DELETE FROM manager WHERE id = ?;";
    public static final String  UPDATE_SQL = "UPDATE manager SET name = ?, cpf = ?, address_id = ? WHERE id = ?;";
    public static final String  SELECT_ALL_SQL = "SELECT * FROM manager;";
    public static final String  SELECT_BY_CPF_SQL = "SELECT * FROM manager WHERE cpf = ?;";
    public static final String  SELECT_BY_ID_SQL = "SELECT * FROM manager WHERE id = ?;";
    public static final String  SELECT_BY_NAME_SQL = "SELECT * FROM manager WHERE name = ?;";
    
    
    @Override
    public void create(Manager entity) throws SQLException {
        AddressDAO addressDAO = new AddressDAO();

        if (entity.getAddress().getId() <= 0) {
            addressDAO.create(entity.getAddress());
        }

        PreparedStatement ps = connection.prepareStatement(
                INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS
        );

        ps.setString(1, entity.getName());
        ps.setString(2, entity.getCPF());
        ps.setLong(3, entity.getAddress().getId());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if (rs.next()) {
            entity.setId(rs.getLong(1)); // set the generated ID back to the entity
        }
    }

    @Override
    public void delete(Manager entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(DELETE_SQL);

        ps.setLong(1, entity.getId());

        ps.executeUpdate();
    }

    @Override
    public ArrayList<Manager> listAll() throws SQLException {
        Statement ps = connection.createStatement();

        ResultSet rs = ps.executeQuery(SELECT_ALL_SQL);

        ArrayList<Manager> managers = new ArrayList<>();

        AddressDAO addressDAO = new AddressDAO();

        while (rs.next()) {
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Manager manager = new Manager(rs.getString("name"), rs.getString("cpf"), address);

            manager.setId(rs.getLong("id"));
            managers.add(manager);
        }

        return managers;
    }

    @Override
    public void update(Manager entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);

        ps.setString(1, entity.getName());
        ps.setString(2, entity.getCPF());
        ps.setLong(3, entity.getAddress().getId());
        ps.setLong(4, entity.getId());

        ps.executeUpdate();
    }

    @Override
    public Manager readById(long id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL);

        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Manager manager = new Manager(rs.getString("name"), rs.getString("cpf"), address);

            manager.setId(rs.getLong("id"));
            return manager;
        }

        return null;
    }

    public Manager readByCPF(String cpf) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_CPF_SQL);

        ps.setString(1, cpf);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Manager manager = new Manager(rs.getString("name"), rs.getString("cpf"), address);

            manager.setId(rs.getLong("id"));
            return manager;
        }

        return null;
    }

    public Manager readByName(String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_NAME_SQL);

        ps.setString(1, name);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Manager manager = new Manager(rs.getString("name"), rs.getString("cpf"), address);

            manager.setId(rs.getLong("id"));
            return manager;
        }

        return null;
    }
}