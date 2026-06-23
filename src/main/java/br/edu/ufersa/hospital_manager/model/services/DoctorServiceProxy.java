package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Doctor;

public class DoctorServiceProxy implements DoctorServiceContract, IsServiceProxy {
    private final DoctorServiceContract doctorService;

    public DoctorServiceProxy(DoctorServiceContract doctorService) {
        this.doctorService = doctorService;
    }

    @Override
    public void registerDoctor(Doctor doctor) throws SQLException {
        ensureManagerAccess("register a doctor");
        doctorService.registerDoctor(doctor);
    }

    @Override
    public void removeDoctor(Doctor doctor) throws SQLException {
        ensureManagerAccess("remove a doctor");
        doctorService.removeDoctor(doctor);
    }

    @Override
    public void updateDoctor(Doctor doctor) throws SQLException {
        doctorService.updateDoctor(doctor);
    }

    @Override
    public Doctor findById(long id) throws SQLException {
        return doctorService.findById(id);
    }

    @Override
    public Doctor findByName(String name) throws SQLException {
        return doctorService.findByName(name);
    }

    @Override
    public Doctor findByCPF(String cpf) throws SQLException {
        return doctorService.findByCPF(cpf);
    }

    @Override
    public Doctor findByCouncilCode(String councilCode) throws SQLException {
        return doctorService.findByCouncilCode(councilCode);
    }

    @Override
    public ArrayList<Doctor> listAll() throws SQLException {
        return doctorService.listAll();
    }

    private void ensureManagerAccess(String action) {
        if (ServiceRoleContext.getCurrentRole() != ServiceRole.MANAGER) {
            throw new RuntimeException("Only a manager can " + action + ".");
        }
    }

    private void ensureDoctorAccess(String action) {
        if (ServiceRoleContext.getCurrentRole() != ServiceRole.DOCTOR && ServiceRoleContext.getCurrentRole() != ServiceRole.MANAGER) {
            throw new RuntimeException("You are not authorized to " + action + ".");
        }
    }
}