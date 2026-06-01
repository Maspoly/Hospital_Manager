package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.util.Connector;

public class AddressDAO {
    private Connection connection;

    public AddressDAO() {
        this.connection = Connector.getConnection();
    }

    public static final String INSERT_SQL =
            "INSERT INTO addresses (street, number, neighborhood, city, state) VALUES (?, ?, ?, ?, ?);";

    public static final String SELECT_BY_ID_SQL =
            "SELECT * FROM addresses WHERE id = ?;";

    public void create(Address address) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                INSERT_SQL,
                PreparedStatement.RETURN_GENERATED_KEYS
        );

        ps.setString(1, address.getStreet());
        ps.setString(2, address.getNumber());
        ps.setString(3, address.getNeighborhood());
        ps.setString(4, address.getCity());
        ps.setString(5, address.getState());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if (rs.next()) {
            address.setId(rs.getLong(1));
        }
    }

    public Address readById(long id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL);
        ps.setLong(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Address address = new Address(
                    rs.getString("street"),
                    rs.getString("number"),
                    rs.getString("neighborhood"),
                    rs.getString("city"),
                    rs.getString("state")
            );

            address.setId(rs.getLong("id"));
            return address;
        }

        return null;
    }
}