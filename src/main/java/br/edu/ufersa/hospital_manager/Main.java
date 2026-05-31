package br.edu.ufersa.hospital_manager;

import br.edu.ufersa.hospital_manager.model.entities.*;
import br.edu.ufersa.hospital_manager.model.services.*;
import br.edu.ufersa.hospital_manager.util.Connector;

import java.time.LocalDate;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        System.out.println("═══════════════════════════════════════════");
        System.out.println("        HOSPITAL MANAGER - TESTS           ");
        System.out.println("═══════════════════════════════════════════\n");

        try {
            testDoctorServices();
            testManagerServices();
            testPatientServices();
            testMedicalRecordServices();
            testConsultationServices();
        } finally {
            Connector.closeConnection();
            System.out.println("\n✔ Connection closed.");
        }
    }

    // ─── Doctor ───────────────────────────────────────────────────────────────

    static void testDoctorServices() {
        System.out.println("─── DoctorServices ───────────────────────");
        DoctorServices service = new DoctorServices();
        ManagerService managerService = new ManagerService();

        // Register
        Doctor doctor = new Doctor("Dr. House", "12345678901", "221B Baker St", 350.0f, "123456");
        try {
            managerService.registerDoctor(doctor);
            System.out.println("✔ Doctor registered: " + doctor.getName() + " | ID: " + doctor.getId());
        } catch (Exception e) {
            System.out.println("✘ Error registering doctor: " + e.getMessage());
        }

        // Find by council code
        try{
            Doctor found = service.findByCouncilCode("123456");
            System.out.println("✔ Found by council code: " + found.getName());
        } catch (Exception e) {
            System.out.println("✘ " + e.getMessage());
        }

        // Find by CPF
        try {
            Doctor foundByCPF = service.findByCPF("12345678901");
            System.out.println("✔ Found by CPF: " + foundByCPF.getName());
        } catch (Exception e) {
            System.out.println("✘ " + e.getMessage());
        }

        // Find by name
        try {
            Doctor foundByName = service.findByName("Dr. House");
            System.out.println("✔ Found by name: " + foundByName.getName());
        } catch (Exception e) {
            System.out.println("✘ " + e.getMessage());
        }

        // Update
        try {
            Doctor updated = service.updateDoctor(doctor);
            System.out.println("✔ Doctor updated: " + updated.getName());
        } catch (Exception e) {
            System.out.println("✘ " + e.getMessage());
        }

        
        // List all
        ArrayList<Doctor> doctors = service.listAll();
        System.out.println("✔ Total doctors: " + doctors.size());

        // Duplicate council code (should throw)
        Doctor doctor2 = new Doctor("Dr. Duplicate", "98765432100", "Some St", 200.0f, "123456");
        try {
            managerService.registerDoctor(doctor2);
            System.out.println("✘ Should have thrown for duplicate council code.");
        } catch (RuntimeException e) {
            System.out.println("✔ Duplicate council code blocked: " + e.getMessage());
        }

        System.out.println();
    }

    // ─── Manager ──────────────────────────────────────────────────────────────

    static void testManagerServices() {
        System.out.println("─── ManagerService ───────────────────────");
        ManagerService service = new ManagerService();

        // Register manager
        Manager manager = new Manager("Alice Manager", "11122233344", "Manager St 10");
        try {
            service.registerManager(manager);
            System.out.println("✔ Manager registered: " + manager.getName() + " | ID: " + manager.getId());
        } catch (Exception e) {
            System.out.println("✘ Error registering manager: " + e.getMessage());
        }

        // Find by CPF
        try {
            Manager found = service.findByCPF("11122233344");
            System.out.println("✔ Found manager by CPF: " + found.getName());
        } catch (Exception e) {
            System.out.println("✘ " + e.getMessage());
        }

        // Update
        manager.setName("Alice Updated");
        try {
            service.updateManager(manager);
            System.out.println("✔ Manager updated: " + manager.getName());
        } catch (Exception e) {
            System.out.println("✘ " + e.getMessage());
        }

        // Register doctor via manager
        try {
            Doctor doctorByManager = new Doctor("Dr. Manager", "55566677788", "Doctor Ave 5", 200.0f, "654321");
            service.registerDoctor(doctorByManager);
            System.out.println("✔ Doctor registered via ManagerService: " + doctorByManager.getName());
        } catch (Exception e) {
            System.out.println("✘ " + e.getMessage());
        }

        System.out.println();
    }

    // ─── Patient ──────────────────────────────────────────────────────────────

    static void testPatientServices() {
        System.out.println("─── PatientServices ──────────────────────");
        PatientServices service = new PatientServices();

        // Register without medical record
        Patient patient = service.registerPatient("John Doe", "44455566677", "Patient Rd 42", null);
        System.out.println("✔ Patient registered: " + patient.getName() + " | ID: " + patient.getId());

        // Find by CPF
        Patient found = service.findByCPF("44455566677");
        System.out.println("✔ Found by CPF: " + found.getName());

        // Update
        Patient updated = service.updatePatient(patient, "John Doe Updated", "44455566677", "New Address 42");
        System.out.println("✔ Patient updated: " + updated.getName());

        // List all
        ArrayList<Patient> patients = service.listAll();
        System.out.println("✔ Total patients: " + patients.size());

        // Duplicate CPF (should throw)
        try {
            service.registerPatient("John Clone", "44455566677", "Clone St", null);
            System.out.println("✘ Should have thrown for duplicate CPF.");
        } catch (RuntimeException e) {
            System.out.println("✔ Duplicate CPF blocked: " + e.getMessage());
        }

        System.out.println();
    }

    // ─── MedicalRecord ────────────────────────────────────────────────────────

    static void testMedicalRecordServices() {
        System.out.println("─── MedicalRecordServices ────────────────");
        MedicalRecordServices service = new MedicalRecordServices();
        DoctorServices doctorService = new DoctorServices();
        PatientServices patientService = new PatientServices();

        Doctor doctor   = doctorService.findByCPF("12345678901");
        Patient patient = patientService.findByCPF("44455566677");

        // Create medical record
        MedicalRecord record = service.registerMedicalRecord("Patient has fever and headache.", doctor, patient);
        System.out.println("✔ Medical record registered | ID: " + record.getId());

        // Find by patient
        MedicalRecord foundByPatient = service.findByPatient(patient);
        System.out.println("✔ Found by patient: " + foundByPatient.getObservation());

        // Find by date
        MedicalRecord foundByDate = service.findByDate(LocalDate.now());
        System.out.println("✔ Found by date: " + foundByDate.getObservation());

        // Update observation
        MedicalRecord updated = service.updateObservation(record, "Patient has fever, headache and nausea.");
        System.out.println("✔ Observation updated: " + updated.getObservation());

        // Find by doctor
        ArrayList<MedicalRecord> byDoctor = service.findByDoctor(doctor);
        System.out.println("✔ Records for doctor: " + byDoctor.size());

        // Duplicate record today (should throw)
        try {
            service.registerMedicalRecord("Another record today.", doctor, patient);
            System.out.println("✘ Should have thrown for duplicate record today.");
        } catch (RuntimeException e) {
            System.out.println("✔ Duplicate record today blocked: " + e.getMessage());
        }

        System.out.println();
    }

    // ─── Consultation ─────────────────────────────────────────────────────────

    static void testConsultationServices() {
        System.out.println("─── ConsultationServices ─────────────────");
        ConsultationServices service = new ConsultationServices();
        DoctorServices doctorService   = new DoctorServices();
        PatientServices patientService = new PatientServices();

        Doctor doctor   = doctorService.findByCPF("12345678901");
        Patient patient = patientService.findByCPF("44455566677");

        // Schedule consultation
        LocalDate future = LocalDate.now().plusDays(3);
        Consultation consultation = service.scheduleConsultation(patient, doctor, future, "SCHEDULED");
        System.out.println("✔ Consultation scheduled | ID: " + consultation.getId() + " | Date: " + consultation.getDate());

        // Find by patient
        ArrayList<Consultation> byPatient = service.findByPatient(patient);
        System.out.println("✔ Consultations for patient: " + byPatient.size());

        // Find by doctor
        ArrayList<Consultation> byDoctor = service.findByDoctor(doctor);
        System.out.println("✔ Consultations for doctor: " + byDoctor.size());

        // Find by status
        ArrayList<Consultation> scheduled = service.findByStatus("SCHEDULED");
        System.out.println("✔ Scheduled consultations: " + scheduled.size());

        // Reschedule
        Consultation rescheduled = service.rescheduleConsultation(consultation, LocalDate.now().plusDays(7));
        System.out.println("✔ Rescheduled to: " + rescheduled.getDate());

        // Update status to COMPLETED
        Consultation completed = service.updateStatus(consultation, "COMPLETED");
        System.out.println("✔ Status updated to: " + completed.getStatus());

        // Attach medical record
        MedicalRecordServices mrService = new MedicalRecordServices();
        MedicalRecord record = mrService.findByPatient(patient);
        Consultation withRecord = service.attachMedicalRecord(consultation, record);
        System.out.println("✔ Medical record attached to consultation.");

        // Try to change status of COMPLETED (should throw)
        try {
            service.updateStatus(completed, "CANCELED");
            System.out.println("✘ Should have thrown for changing COMPLETED status.");
        } catch (RuntimeException e) {
            System.out.println("✔ Status change blocked: " + e.getMessage());
        }

        // Schedule in the past (should throw)
        try {
            service.scheduleConsultation(patient, doctor, LocalDate.now().minusDays(1), "SCHEDULED");
            System.out.println("✘ Should have thrown for past date.");
        } catch (RuntimeException e) {
            System.out.println("✔ Past date blocked: " + e.getMessage());
        }

        // List all
        ArrayList<Consultation> all = service.listAll();
        System.out.println("✔ Total consultations: " + all.size());

        System.out.println();
    }
}