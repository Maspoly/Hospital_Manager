package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;

public interface FindServices<T> {
    public T findById(long id) throws SQLException;
    public T findByName(String name) throws SQLException;
    public T findByCPF(String cpf) throws SQLException;
    
}
