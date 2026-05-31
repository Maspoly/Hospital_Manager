package br.edu.ufersa.hospital_manager.model.services;

public interface FindServices<T> {
    public T findById(long id) throws Exception;
    public T findByName(String name) throws Exception;
    public T findByCPF(String cpf) throws Exception;
    
}
