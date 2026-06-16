package br.edu.ufersa.hospital_manager;

public class Main {
    public static void main(String[] args) {
<<<<<<< Updated upstream
        System.out.println("Hello world!");
=======

        System.out.println("═══════════════════════════════════════════");
        System.out.println("        HOSPITAL MANAGER - TESTS           ");
        System.out.println("═══════════════════════════════════════════\n");

        try {
            testManagerServices();
            testDoctorServices();
            testPatientServices();
            testMedicalRecordServices();
            testConsultationServices();
        } finally {
            Connector.closeConnection();
            System.out.println("\nConnection closed.");
        }
    }

    // ─── Manager ──────────────────────────────────────────────────────────────

    static void testManagerServices() {
        System.out.println("─── ManagerService ───────────────────────");
        ManagerService service = new ManagerService();

        // Register
        Manager manager = new Manager("Alice Manager", "11122233344", new Address("Manager" ,"10" ,"St","",""));
        try {
            service.registerManager(manager);
            System.out.println("Manager registered: " + manager.getName() + " | ID: " + manager.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Find by CPF
        try {
            Manager found = service.findByCPF("11122233344");
            System.out.println("Found by CPF: " + found.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Find by name
        try {
            Manager found = service.findByName("Alice Manager");
            System.out.println("Found by name: " + found.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Update
        manager.setName("Alice Updated");
        try {
            service.updateManager(manager);
            System.out.println("Manager updated: " + manager.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Duplicate CPF (should throw)
        try {
            service.registerManager(new Manager("Alice Clone", "11122233344", new Address("Clone", "","St","","")));
            System.out.println("Should have thrown for duplicate CPF.");
        } catch (Exception e) {
            System.out.println("Duplicate CPF blocked: " + e.getMessage());
        }

        System.out.println();
    }

    // ─── Doctor ───────────────────────────────────────────────────────────────

    static void testDoctorServices() {
        System.out.println("─── DoctorServices ───────────────────────");
        ManagerService managerService = new ManagerService();
        DoctorService service = new DoctorService();

        // Register via ManagerService (regra de negócio do projeto)
        Doctor doctor = new Doctor("Dr. House", "12345678901", new Address("Baker",  "221B", "St","",""), 350.0f, "123456");
        try {
            managerService.registerDoctor(doctor);
            System.out.println("Doctor registered: " + doctor.getName() + " | ID: " + doctor.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Find by council code
        try {
            Doctor found = service.findByCouncilCode("123456");
            System.out.println("Found by council code: " + found.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Find by CPF
        try {
            Doctor found = service.findByCPF("12345678901");
            System.out.println("Found by CPF: " + found.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Find by name
        try {
            Doctor found = service.findByName("Dr. House");
            System.out.println("Found by name: " + found.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Update — busca o objeto atualizado do banco antes de editar
        try {
            Doctor toUpdate = service.findByCPF("12345678901");
            service.updateDoctor(new Doctor("Dr. House Jr.", "12345678901", new Address("Baker",  "221B", "St","",""), 400.0f, "123456"));
            System.out.println("Doctor updated: " + toUpdate.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // List all
        try {
            ArrayList<Doctor> doctors = service.listAll();
            System.out.println("Total doctors: " + doctors.size());
        } catch (Exception e) {
            System.out.println( e.getMessage());
        }

        // Duplicate council code (should throw)
        try {
            managerService.registerDoctor(new Doctor("Dr. Duplicate", "98765432100", new Address("Some",  "", "St","",""), 200.0f, "123456"));
            System.out.println("Should have thrown for duplicate council code.");
        } catch (Exception e) {
            System.out.println("Duplicate council code blocked: " + e.getMessage());
        }

        System.out.println();
    }

    // ─── Patient ──────────────────────────────────────────────────────────────

    static void testPatientServices() {
        System.out.println("─── PatientServices ──────────────────────");
        PatientService service = new PatientService();

        // Register without medical record
        try {
            Patient patient = service.registerPatient(new Patient("John Doe", "44455566677", new Address("Patient",  "42", "Rd","",""), null));
            System.out.println("Patient registered: " + patient.getName() + " | ID: " + patient.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Find by CPF
        try {
            Patient found = service.findByCPF("44455566677");
            System.out.println("Found by CPF: " + found.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Find by name
        try {
            Patient found = service.findByName("John Doe");
            System.out.println("Found by name: " + found.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Update
        try {
            Patient toUpdate = service.findByCPF("44455566677");
            service.updatePatient(new Patient("John Doe Updated", "44455566677", new Address("New Address",  "42", "St","",""), null));
            System.out.println("Patient updated: " + toUpdate.getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // List all
        try {
            ArrayList<Patient> patients = service.listAll();
            System.out.println("Total patients: " + patients.size());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Duplicate CPF (should throw)
        try {
            service.registerPatient(new Patient("John Clone", "44455566677", new Address("Clone",  "", "St","",""), null));
            System.out.println("Should have thrown for duplicate CPF.");
        } catch (Exception e) {
            System.out.println("Duplicate CPF blocked: " + e.getMessage());
        }

        System.out.println();
    }

    // ─── MedicalRecord ────────────────────────────────────────────────────────

    static void testMedicalRecordServices() {
        System.out.println("─── MedicalRecordServices ────────────────");
        MedicalRecordService service = new MedicalRecordService();
        DoctorService  doctorService  = new DoctorService();
        PatientService patientService = new PatientService();

        // Busca fora do try para ficar acessível no resto do método
        Doctor doctor = null;
        Patient patient = null;

        try {
            doctor  = doctorService.findByCPF("12345678901");
            patient = patientService.findByCPF("44455566677");
            System.out.println("Doctor and patient loaded.");
        } catch (Exception e) {
            System.out.println("Could not load doctor/patient: " + e.getMessage());
            return; // sem doctor/patient os testes seguintes falhariam
        }

        // Create
        MedicalRecord record = null;
        try {
            record = service.registerMedicalRecord("Patient has fever and headache.", doctor, patient);
            System.out.println("Medical record registered | ID: " + record.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        // Find by patient
        try {
            MedicalRecord found = service.findByPatient(patient);
            System.out.println("Found by patient: " + found.getObservation());
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }

        // Find by date
        try {
            MedicalRecord found = service.findByDate(LocalDate.now());
            System.out.println("Found by date: " + found.getObservation());
        } catch (Exception e) {
            System.out.println("" + e.getMessage());
        }

        // Update observation
        try {
            MedicalRecord updated = service.updateObservation(record, "Patient has fever, headache and nausea.");
            System.out.println("Observation updated: " + updated.getObservation());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Find by doctor
        try {
            ArrayList<MedicalRecord> byDoctor = service.findByDoctor(doctor);
            System.out.println("Records for doctor: " + byDoctor.size());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Duplicate today (should throw)
        try {
            service.registerMedicalRecord("Another record today.", doctor, patient);
            System.out.println("Should have thrown for duplicate record today.");
        } catch (Exception e) {
            System.out.println("Duplicate record today blocked: " + e.getMessage());
        }

        System.out.println();
    }

    // ─── Consultation ─────────────────────────────────────────────────────────

    static void testConsultationServices() {
        System.out.println("─── ConsultationServices ─────────────────");
        ConsultationService service        = new ConsultationService();
        MedicalRecordService mrService     = new MedicalRecordService();
        DoctorService  doctorService       = new DoctorService();
        PatientService patientService      = new PatientService();

        Doctor doctor = null;
        Patient patient = null;

        try {
            doctor  = doctorService.findByCPF("12345678901");
            patient = patientService.findByCPF("44455566677");
            System.out.println("Doctor and patient loaded.");
        } catch (Exception e) {
            System.out.println("Could not load doctor/patient: " + e.getMessage());
            return;
        }

        // Schedule
        Consultation consultation = null;
        try {
            consultation = service.scheduleConsultation(patient, doctor, LocalDate.now().plusDays(3), "SCHEDULED");
            System.out.println("Consultation scheduled | ID: " + consultation.getId() + " | Date: " + consultation.getDate());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        // Find by patient
        try {
            ArrayList<Consultation> found = service.findByPatient(patient);
            System.out.println("Consultations for patient: " + found.size());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Find by doctor
        try {
            ArrayList<Consultation> found = service.findByDoctor(doctor);
            System.out.println("Consultations for doctor: " + found.size());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Find by status
        try {
            ArrayList<Consultation> found = service.findByStatus("SCHEDULED");
            System.out.println("Scheduled consultations: " + found.size());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Reschedule
        try {
            Consultation rescheduled = service.rescheduleConsultation(consultation, LocalDate.now().plusDays(7));
            System.out.println("Rescheduled to: " + rescheduled.getDate());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Update status to COMPLETED
        try {
            Consultation completed = service.updateStatus(consultation, "COMPLETED");
            System.out.println("Status updated to: " + completed.getStatus());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Attach medical record
        try {
            MedicalRecord record = mrService.findByPatient(patient);
            service.attachMedicalRecord(consultation, record);
            System.out.println("Medical record attached to consultation.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Change status of COMPLETED (should throw)
        try {
            service.updateStatus(consultation, "CANCELED");
            System.out.println("Should have thrown for changing COMPLETED status.");
        } catch (Exception e) {
            System.out.println("Status change blocked: " + e.getMessage());
        }

        // Past date (should throw)
        try {
            service.scheduleConsultation(patient, doctor, LocalDate.now().minusDays(1), "SCHEDULED");
            System.out.println("Should have thrown for past date.");
        } catch (Exception e) {
            System.out.println("Past date blocked: " + e.getMessage());
        }

        // List all
        try {
            ArrayList<Consultation> all = service.listAll();
            System.out.println("Total consultations: " + all.size());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println();
>>>>>>> Stashed changes
    }
}