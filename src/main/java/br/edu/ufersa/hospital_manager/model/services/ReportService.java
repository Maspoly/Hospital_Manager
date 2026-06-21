package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.DAO.ConsultationDAO;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Report;

public class ReportService {
    private ConsultationDAO consultationDAO;

    public ReportService() {
        this.consultationDAO = new ConsultationDAO();
    }

    // ─── Report Generation ────────────────────────────────────────────────────

    // Generates a report of a doctor's consultations within a given period.
    // Counts scheduled, completed and canceled consultations.
    public Report generateDoctorReport(
            Doctor doctor,
            LocalDateTime start,
            LocalDateTime end
    ) throws SQLException {

        if (doctor == null) {
            throw new RuntimeException("Doctor cannot be null.");
        }

        if (start == null || end == null) {
            throw new RuntimeException("Start and end dates cannot be null.");
        }

        if (start.isAfter(end)) {
            throw new RuntimeException("Start date cannot be after end date.");
        }

        ArrayList<Consultation> consultations =
                consultationDAO.readByDoctor(doctor);

        int total = 0;
        int scheduled = 0;
        int completed = 0;
        int canceled = 0;

        for (Consultation c : consultations) {
            if (c != null &&
                    (c.getDateTime().isEqual(start) || c.getDateTime().isAfter(start)) &&
                    (c.getDateTime().isEqual(end) || c.getDateTime().isBefore(end))) {

                total++;

                switch (c.getStatus()) {
                    case "SCHEDULED":
                        scheduled++;
                        break;

                    case "COMPLETED":
                        completed++;
                        break;

                    case "CANCELED":
                        canceled++;
                        break;
                }
            }
        }

        return new Report(doctor, start, end, LocalDateTime.now(), total, scheduled, completed, canceled);
    }

    public ArrayList<Consultation> listAllConsultations() throws SQLException {
        return consultationDAO.listAll();
    }
}