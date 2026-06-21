package br.edu.ufersa.hospital_manager.model.services;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Report;

public class ReportServiceProxy {
    private final ReportService reportService;

    public ReportServiceProxy() {
        this.reportService = new ReportService();
    }

    public Report generateDoctorReport(Doctor doctor, LocalDateTime start, LocalDateTime end) throws SQLException {
        ensureReportAccess("generate a report");
        return reportService.generateDoctorReport(doctor, start, end);
    }

    public ArrayList<Consultation> listAllConsultations() throws SQLException {
        return reportService.listAllConsultations();
    }

    private void ensureReportAccess(String action) {
        ServiceRole role = ServiceRoleContext.getCurrentRole();
        if (role != ServiceRole.DOCTOR && role != ServiceRole.MANAGER) {
            throw new RuntimeException("Only a doctor or manager can " + action + ".");
        }
    }
}