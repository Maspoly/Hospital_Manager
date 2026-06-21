package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.Map;

import br.edu.ufersa.hospital_manager.model.entities.Person;

public class LoginServiceProxy implements LoginServiceContract {
    private final LoginServiceContract loginService;

    public LoginServiceProxy() {
        this.loginService = new LoginService();
    }

    @Override
    public Map<ServiceRole, Person> authenticate(String cpf, String password) throws SQLException {
        return loginService.authenticate(cpf, password);
    }

    @Override
    public void selectSession(ServiceRole role, Person user) {
        loginService.selectSession(role, user);
    }

    @Override
    public void logout() {
        loginService.logout();
    }

    @Override
    public Person getLoggedUser() {
        return loginService.getLoggedUser();
    }

    @Override
    public ServiceRole getLoggedRole() {
        return loginService.getLoggedRole();
    }
}