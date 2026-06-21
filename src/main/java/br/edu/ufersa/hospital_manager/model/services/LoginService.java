package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import br.edu.ufersa.hospital_manager.model.DAO.DoctorDAO;
import br.edu.ufersa.hospital_manager.model.DAO.ManagerDAO;
import br.edu.ufersa.hospital_manager.model.DAO.PatientDAO;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Manager;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.util.PasswordUtils;

public class LoginService implements LoginServiceContract {
    private final ManagerDAO managerDAO;
    private final DoctorDAO doctorDAO;
    private final PatientDAO patientDAO;

    public LoginService() {
        this.managerDAO = new ManagerDAO();
        this.doctorDAO = new DoctorDAO();
        this.patientDAO = new PatientDAO();
    }

    @Override
    public Map<ServiceRole, Person> authenticate(String cpf, String password) throws SQLException {
        if (cpf == null || cpf.isBlank()) {
            throw new RuntimeException("CPF cannot be null or empty.");
        }

        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password cannot be null or empty.");
        }

        String normalizedCpf = cpf.replaceAll("[^0-9]", "");
        if (normalizedCpf.isBlank()) {
            throw new RuntimeException("CPF cannot be empty.");
        }

        LinkedHashMap<ServiceRole, Person> matchedUsers = new LinkedHashMap<>();

        Manager manager = managerDAO.readByCPF(normalizedCpf);
        if (manager != null && PasswordUtils.matches(password, manager.getPasswordHash())) {
            matchedUsers.put(ServiceRole.MANAGER, manager);
        }

        Doctor doctor = doctorDAO.readByCPF(normalizedCpf);
        if (doctor != null && PasswordUtils.matches(password, doctor.getPasswordHash())) {
            matchedUsers.put(ServiceRole.DOCTOR, doctor);
        }

        Patient patient = patientDAO.readByCPF(normalizedCpf);
        if (patient != null && PasswordUtils.matches(password, patient.getPasswordHash())) {
            matchedUsers.put(ServiceRole.PATIENT, patient);
        }

        if (matchedUsers.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        return matchedUsers;
    }

    @Override
    public void selectSession(ServiceRole role, Person user) {
        ServiceRoleContext.setCurrentUser(user, role);
    }

    @Override
    public void logout() {
        ServiceRoleContext.clear();
    }

    @Override
    public Person getLoggedUser() {
        return ServiceRoleContext.getCurrentUser();
    }

    @Override
    public ServiceRole getLoggedRole() {
        return ServiceRoleContext.getCurrentRole();
    }
}