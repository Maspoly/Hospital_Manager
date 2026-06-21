package br.edu.ufersa.hospital_manager.model.services;

public enum ServiceRole {
    MANAGER("Gerente"),
    DOCTOR("Médico"),
    PATIENT("Paciente");

    private final String displayName;

    ServiceRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}