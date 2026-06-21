package br.edu.ufersa.hospital_manager.model.entities;

import java.time.LocalDateTime;

public class Report {
    private Long id;
    private Doctor doctor;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private LocalDateTime generatedAt;
    private int total;
    private int scheduled;
    private int completed;
    private int canceled;

    public Report(int total, int scheduled, int completed, int canceled) {
        this(null, null, null, null, total, scheduled, completed, canceled);
    }

    public Report(Doctor doctor,
                  LocalDateTime periodStart,
                  LocalDateTime periodEnd,
                  LocalDateTime generatedAt,
                  int total,
                  int scheduled,
                  int completed,
                  int canceled) {
        this.doctor = doctor;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.generatedAt = generatedAt;
        this.total = total;
        this.scheduled = scheduled;
        this.completed = completed;
        this.canceled = canceled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public int getTotal() {
        return total;
    }

    public int getScheduled() {
        return scheduled;
    }

    public int getCompleted() {
        return completed;
    }

    public int getCanceled() {
        return canceled;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (doctor != null) {
            builder.append("Doctor: ").append(doctor.getName()).append('\n');
        }

        if (periodStart != null && periodEnd != null) {
            builder.append("Period: ").append(periodStart).append(" -> ").append(periodEnd).append('\n');
        }

        if (generatedAt != null) {
            builder.append("Generated at: ").append(generatedAt).append('\n');
        }

        builder.append("Total: ").append(total)
                .append('\n').append("Scheduled: ").append(scheduled)
                .append('\n').append("Completed: ").append(completed)
                .append('\n').append("Canceled: ").append(canceled);

        return builder.toString();
    }
}
