package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Doctor;

public class DoctorDAO implements BaseDAO<Doctor> {
    
    
    private Connection connection;
    
    public static final String INSERT_SQL = "INSERT INTO Doctor (name, cpf, adress, consultation_value, council_code) VALUES (?, ?, ?, ?, ?);";
    public static final String  DELETE_SQL = "DELETE FROM Doctor WHERE id = ?;";
    public static final String  UPDATE_SQL = "UPDATE Doctor SET name = ?, cpf = ?, adress = ?, consultation_value = ?, council_code = ? WHERE id = ?;";
    public static final String  SELECT_ALL_SQL = "SELECT * FROM Doctor;";
    public static final String  SELECT_BY_CPF_SQL = "SELECT * FROM Doctor WHERE cpf = ?;";
    public static final String  SELECT_BY_ID_SQL = "SELECT * FROM Doctor WHERE id = ?;";
    public static final String  SELECT_BY_NAME_SQL = "SELECT * FROM Doctor WHERE name = ?;";
    public static final String  SELECT_BY_COURCIL_CODE_SQL = "SELECT * FROM Doctor WHERE council_code = ?;";
    
    
    @Override
    public void create(Doctor entity) throws SQLException {
        try{
            PreparedStatement ps = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getCPF());
            ps.setString(3, entity.getAddress());
            ps.setFloat(4, entity.getConsultationValue());
            ps.setString(5, entity.getCouncilCode());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getLong(1)); // set the generated ID back to the entity
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        
    }
    
    @Override
    public void delete(long id) throws SQLException {
        try{
            PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
            ps.setLong(1, id);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        
    }
    
    @Override
    public ArrayList<Doctor> listAll() throws SQLException {
        try{
            Statement ps = connection.createStatement();
            ResultSet rs = ps.executeQuery(SELECT_ALL_SQL);
            ArrayList<Doctor> doctors = new ArrayList<>();
            while (rs.next()) {
                Doctor doctor = new Doctor(rs.getString("name"), rs.getString("cpf"), rs.getString("adress"), rs.getFloat("consultation_value"), rs.getString("council_code"));
                doctor.setId(rs.getLong("id"));
                doctors.add(doctor);
            }
            return doctors;
        }catch (SQLException e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public void update(Doctor entity) throws SQLException {
        try{
            PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
            ps.setString(1, entity.getName());
            ps.setString(2, entity.getCPF());
            ps.setString(3, entity.getAddress());
            ps.setFloat(4, entity.getConsultationValue());
            ps.setString(5, entity.getCouncilCode());
            ps.setLong(6, entity.getId());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        
    }
    @Override
    public Doctor readById(long id) throws SQLException {
        try{
            PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Doctor doctor = new Doctor(rs.getString("name"), rs.getString("cpf"), rs.getString("adress"), rs.getFloat("consultation_value"), rs.getString("council_code"));
                doctor.setId(rs.getLong("id"));
                return doctor;
            }
            return null; // not found
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    
    public Doctor readByCPF(String cpf) throws SQLException {
        try{
            PreparedStatement ps = connection.prepareStatement(SELECT_BY_CPF_SQL);
            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Doctor doctor = new Doctor(rs.getString("name"), rs.getString("cpf"), rs.getString("adress"), rs.getFloat("consultation_value"), rs.getString("council_code"));
                doctor.setId(rs.getLong("id"));
                return doctor;
            }
            return null; // not found
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    
    
    public Doctor readByName(String name) throws SQLException {
        try{
            PreparedStatement ps = connection.prepareStatement(SELECT_BY_NAME_SQL);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Doctor doctor = new Doctor(rs.getString("name"), rs.getString("cpf"), rs.getString("adress"), rs.getFloat("consultation_value"), rs.getString("council_code"));
                doctor.setId(rs.getLong("id"));
                return doctor;
            }
            return null; // not found
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Doctor readByCouncilCode(String councilCode) throws SQLException {
        try{
            PreparedStatement ps = connection.prepareStatement(SELECT_BY_COURCIL_CODE_SQL);
            ps.setString(1, councilCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Doctor doctor = new Doctor(rs.getString("name"), rs.getString("cpf"), rs.getString("adress"), rs.getFloat("consultation_value"), rs.getString("council_code"));
                doctor.setId(rs.getLong("id"));
                return doctor;
            }
            return null; // not found
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
