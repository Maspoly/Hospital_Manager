package br.edu.ufersa.hospital_manager.model.entities;

public class Report {
    private int total;
    private int scheduled;
    private int completed;
    private int canceled;

    public Report(int total, int scheduled, int completed, int canceled) {
        this.total = total;
        this.scheduled = scheduled;
        this.completed = completed;
        this.canceled = canceled;
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
        return "Total: " + total +
                "\nScheduled: " + scheduled +
                "\nCompleted: " + completed +
                "\nCanceled: " + canceled;
    }
}
