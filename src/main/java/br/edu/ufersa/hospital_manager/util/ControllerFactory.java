package br.edu.ufersa.hospital_manager.util;

public class ControllerFactory {
    public static Object createController(Class<?> controllerClass) {
        try {
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create controller: " + controllerClass.getName(), e);
        }
    }
}
