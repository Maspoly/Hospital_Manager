package br.edu.ufersa.hospital_manager.model.services;

import br.edu.ufersa.hospital_manager.model.entities.Person;

public final class ServiceRoleContext {
    private static final ThreadLocal<ServiceRole> CURRENT_ROLE = new ThreadLocal<>();
    private static final ThreadLocal<Person> CURRENT_USER = new ThreadLocal<>();

    private ServiceRoleContext() {
    }

    public static void setCurrentRole(ServiceRole role) {
        if (role == null) {
            throw new RuntimeException("Current role cannot be null.");
        }
        CURRENT_ROLE.set(role);
    }

    public static void setCurrentUser(Person user, ServiceRole role) {
        if (user == null) {
            throw new RuntimeException("Current user cannot be null.");
        }
        if (role == null) {
            throw new RuntimeException("Current role cannot be null.");
        }

        CURRENT_USER.set(user);
        CURRENT_ROLE.set(role);
    }

    public static ServiceRole getCurrentRole() {
        return CURRENT_ROLE.get();
    }

    public static Person getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static void clear() {
        CURRENT_USER.remove();
        CURRENT_ROLE.remove();
    }
}