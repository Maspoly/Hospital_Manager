package br.edu.ufersa.HospitalManager.model.entyties;

import java.time.LocalDate;

public class Patient {
    private String name;
    private String cpf;
    private String address;
    private MedicalRecord medicalRecord;
    private Consultation[] consultations;

    public Patient(String name, String cpf, String address, MedicalRecord medicalRecord) {
        setName(name);
        setCpf(cpf);
        setAddress(address);
        setMedicalRecord(medicalRecord);
        this.consultations = new Consultation[0];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException("CPF must contain exactly 11 numeric digits.");
        }
        this.cpf = cpf;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty.");
        }
        this.address = address;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord == null) {
            throw new IllegalArgumentException("Medical record cannot be null.");
        }
        this.medicalRecord = medicalRecord;
    }

    public Consultation[] getConsultations() {
        return consultations;
    }

    public void setConsultations(Consultation[] consultations) {
        if (consultations == null) {
            throw new IllegalArgumentException("Consultations array cannot be null.");
        }
        this.consultations = consultations;
    }

    public void cadastrarDados(String name, String cpf, String address, MedicalRecord medicalRecord) {
        setName(name);
        setCpf(cpf);
        setAddress(address);
        setMedicalRecord(medicalRecord);
    }

    public void editorDados(String name, String cpf, String address) {
        setName(name);
        setCpf(cpf);
        setAddress(address);
    }

    public void excluirDados() {
        this.name = null;
        this.cpf = null;
        this.address = null;
        this.medicalRecord = null;
        this.consultations = new Consultation[0];
    }

    public Consultation cadastrarConsulta(Doctor doctor, LocalDate date, String status) {
        Consultation nova = new Consultation(this, doctor, date, status);
        Consultation[] novoArray = new Consultation[consultations.length + 1];
        for (int i = 0; i < consultations.length; i++) {
            novoArray[i] = consultations[i];
        }
        novoArray[consultations.length] = nova;
        consultations = novoArray;
        return nova;
    }

    public Consultation editorConsulta(Consultation consulta, LocalDate newDate, String newStatus) {
        if (consulta == null) {
            throw new IllegalArgumentException("Consultation cannot be null.");
        }
        consulta.setDate(newDate);
        consulta.setStatus(newStatus);
        return consulta;
    }

    public void excluirConsulta(Consultation[] consultasParaExcluir) {
        if (consultasParaExcluir == null || consultasParaExcluir.length == 0) {
            return;
        }

        // Conta quantas consultas NÃO serão excluídas
        int restantes = 0;
        for (Consultation c : consultations) {
            if (c != null && !estaNaLista(c, consultasParaExcluir)) {
                restantes++;
            }
        }

        // Cria novo array apenas com as consultas que ficam
        Consultation[] novoArray = new Consultation[restantes];
        int index = 0;
        for (Consultation c : consultations) {
            if (c != null && !estaNaLista(c, consultasParaExcluir)) {
                novoArray[index++] = c;
            }
        }

        consultations = novoArray;
    }

    // Método auxiliar: verifica se uma consulta está no array de exclusão
    private boolean estaNaLista(Consultation consulta, Consultation[] lista) {
        for (Consultation excl : lista) {
            if (excl != null && mesmaConsulta(consulta, excl)) {
                return true;
            }
        }
        return false;
    }

    // Método auxiliar: define quando duas consultas são consideradas a mesma
    private boolean mesmaConsulta(Consultation a, Consultation b) {
        return a.getPatient().getCpf().equals(b.getPatient().getCpf()) &&
               a.getDoctor().getCPF().equals(b.getDoctor().getCPF()) &&
               a.getDate().equals(b.getDate()) &&
               a.getStatus().equals(b.getStatus());
    }
}
