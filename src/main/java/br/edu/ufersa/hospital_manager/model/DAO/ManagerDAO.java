package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Manager;
import br.edu.ufersa.hospital_manager.util.Connector;

public class ManagerDAO implements BaseDAO<Manager> {

    
    private Connection connection;

    public ManagerDAO() {
        this.connection = Connector.getConnection();
    }
    
    public static final String INSERT_SQL = "INSERT INTO manager (name, cpf, adress) VALUES (?, ?, ?);";
    public static final String  DELETE_SQL = "DELETE FROM manager WHERE id = ?;";
    public static final String  UPDATE_SQL = "UPDATE manager SET name = ?, cpf = ?, adress = ? WHERE id = ?;";
    public static final String  SELECT_ALL_SQL = "SELECT * FROM manager;";
    public static final String  SELECT_BY_CPF_SQL = "SELECT * FROM manager WHERE cpf = ?;";
    public static final String  SELECT_BY_ID_SQL = "SELECT * FROM manager WHERE id = ?;";
    public static final String  SELECT_BY_NAME_SQL = "SELECT * FROM manager WHERE name = ?;";
    
    
    @Override
    public void create(Manager entity) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setString(1, entity.getName());
        ps.setString(2, entity.getCPF());
        ps.setString(3, entity.getAddress());
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

        while (rs.next()) {
            Manager manager = new Manager(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    rs.getString("adress")
            );

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
        ps.setString(3, entity.getAddress());
        ps.setLong(4, entity.getId());

        ps.executeUpdate();
    }

    @Override
    public Manager readById(long id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL);

        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Manager manager = new Manager(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    rs.getString("adress")
            );

            manager.setId(rs.getLong("id"));
            return manager;
        }

        throw new SQLException("Manager with ID " + id + " not found.");
    }

    public Manager readByCPF(String cpf) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_CPF_SQL);

        ps.setString(1, cpf);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Manager manager = new Manager(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    rs.getString("adress")
            );

            manager.setId(rs.getLong("id"));
            return manager;
        }

        throw new SQLException("Manager with CPF " + cpf + " not found.");
    }

    public Manager readByName(String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_NAME_SQL);

        ps.setString(1, name);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Manager manager = new Manager(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    rs.getString("adress")
            );

            manager.setId(rs.getLong("id"));
            return manager;
        }

        throw new SQLException("Manager with name " + name + " not found.");
    }
}
