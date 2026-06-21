package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.Map;

import br.edu.ufersa.hospital_manager.model.entities.Person;

public interface LoginServiceContract {
    Map<ServiceRole, Person> authenticate(String cpf, String password) throws SQLException;

    void selectSession(ServiceRole role, Person user);

    void logout();

    Person getLoggedUser();

    ServiceRole getLoggedRole();
}