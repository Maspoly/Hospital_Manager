package br.edu.ufersa.hospital_manager.model.DAO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface BaseDAO<T> {
    public void create(T entity) throws SQLException;
    public T readById(long id) throws SQLException;
    public void update(T entity) throws SQLException;
    public void delete(T entity) throws SQLException;
    public ArrayList<T> listAll() throws SQLException;

}