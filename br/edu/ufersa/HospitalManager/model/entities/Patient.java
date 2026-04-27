package br.edu.ufersa.HospitalManager.model.entities;

import java.time.LocalDate;

public class Patient extends Person {
    private MedicalRecord medicalRecord;
    private Consultation[] consultations;

    public Patient(String name, String cpf, String address, MedicalRecord medicalRecord) {
        super(name, cpf, address);
        setMedicalRecord(medicalRecord);
        this.consultations = new Consultation[0];
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
        setCPF(cpf);
        setAddress(address);
        setMedicalRecord(medicalRecord);
    }

    public void editorDados(String name, String cpf, String address) {
        setName(name);
        setCPF(cpf);
        setAddress(address);
    }

    public void excluirDados() {
        setName(null);
        setCPF(null);
        setAddress(null);
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
        return a.getPatient().getCPF().equals(b.getPatient().getCPF()) &&
               a.getDoctor().getCPF().equals(b.getDoctor().getCPF()) &&
               a.getDate().equals(b.getDate()) &&
               a.getStatus().equals(b.getStatus());
    }
}
