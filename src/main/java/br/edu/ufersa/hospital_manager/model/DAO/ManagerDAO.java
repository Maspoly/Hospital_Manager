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
    }
<<<<<<< HEAD
    
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
        ps.setString(3, String.valueOf(entity.getAddress()));
=======

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = Connector.getConnection();
        }

        if (connection == null) {
            throw new SQLException("Database connection is not available.");
        }

        return connection;
    }

    public static final String INSERT_SQL = "INSERT INTO manager (name, cpf, password, address_id) VALUES (?, ?, ?, ?);";
    public static final String DELETE_SQL = "DELETE FROM manager WHERE id = ?;";
    public static final String UPDATE_SQL = "UPDATE manager SET name = ?, cpf = ?, password = ?, address_id = ? WHERE id = ?;";
    public static final String SELECT_ALL_SQL = "SELECT * FROM manager;";
    public static final String SELECT_BY_CPF_SQL = "SELECT * FROM manager WHERE cpf = ?;";
    public static final String SELECT_BY_ID_SQL = "SELECT * FROM manager WHERE id = ?;";
    public static final String SELECT_BY_NAME_SQL = "SELECT * FROM manager WHERE name = ?;";

    @Override
    public void create(Manager entity) throws SQLException {
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

>>>>>>> 96ad7c6 (Linked screens to data base)
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            entity.setId(rs.getLong(1));
        }
    }

    @Override
    public void delete(Manager entity) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(DELETE_SQL);

        ps.setLong(1, entity.getId());

        ps.executeUpdate();
    }

    @Override
    public ArrayList<Manager> listAll() throws SQLException {
        Statement ps = getConnection().createStatement();

        ResultSet rs = ps.executeQuery(SELECT_ALL_SQL);

        ArrayList<Manager> managers = new ArrayList<>();

        while (rs.next()) {
<<<<<<< HEAD
            Manager manager = new Manager(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    new AddressDAO().readById(rs.getLong("address_id"))
            );
=======
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Manager manager = new Manager(rs.getString("name"), rs.getString("cpf"), address, rs.getString("password"), true);
>>>>>>> 96ad7c6 (Linked screens to data base)

            manager.setId(rs.getLong("id"));
            managers.add(manager);
        }

        return managers;
    }

    @Override
    public void update(Manager entity) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(UPDATE_SQL);

        ps.setString(1, entity.getName());
        ps.setString(2, entity.getCPF());
<<<<<<< HEAD
        ps.setString(3, String.valueOf(entity.getAddress()));
        ps.setLong(4, entity.getId());
=======
        ps.setString(3, entity.getPasswordHash());
        ps.setLong(4, entity.getAddress().getId());
        ps.setLong(5, entity.getId());
>>>>>>> 96ad7c6 (Linked screens to data base)

        ps.executeUpdate();
    }

    @Override
    public Manager readById(long id) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_ID_SQL);

        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
<<<<<<< HEAD
            Manager manager = new Manager(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    new AddressDAO().readById(rs.getLong("address_id"))
            );
=======
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Manager manager = new Manager(rs.getString("name"), rs.getString("cpf"), address, rs.getString("password"), true);
>>>>>>> 96ad7c6 (Linked screens to data base)

            manager.setId(rs.getLong("id"));
            return manager;
        }

        throw new SQLException("Manager with ID " + id + " not found.");
    }

    public Manager readByCPF(String cpf) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_CPF_SQL);

        ps.setString(1, cpf);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
<<<<<<< HEAD
            Manager manager = new Manager(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    new AddressDAO().readById(rs.getLong("address_id"))
            );
=======
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Manager manager = new Manager(rs.getString("name"), rs.getString("cpf"), address, rs.getString("password"), true);
>>>>>>> 96ad7c6 (Linked screens to data base)

            manager.setId(rs.getLong("id"));
            return manager;
        }

        throw new SQLException("Manager with CPF " + cpf + " not found.");
    }

    public Manager readByName(String name) throws SQLException {
        PreparedStatement ps = getConnection().prepareStatement(SELECT_BY_NAME_SQL);

        ps.setString(1, name);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
<<<<<<< HEAD
            Manager manager = new Manager(
                    rs.getString("name"),
                    rs.getString("cpf"),
                    new AddressDAO().readById(rs.getLong("address_id"))
            );
=======
            AddressDAO addressDAO = new AddressDAO();
            Address address = addressDAO.readById(rs.getLong("address_id"));

            Manager manager = new Manager(rs.getString("name"), rs.getString("cpf"), address, rs.getString("password"), true);
>>>>>>> 96ad7c6 (Linked screens to data base)

            manager.setId(rs.getLong("id"));
            return manager;
        }

        throw new SQLException("Manager with name " + name + " not found.");
    }
}
