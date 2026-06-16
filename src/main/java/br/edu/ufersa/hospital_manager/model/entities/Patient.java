package br.edu.ufersa.hospital_manager.model.entities;

public class Patient extends Person {

<<<<<<< Updated upstream
    public Patient(String name, String cpf, Address address) {
=======
    public Patient(String name, String cpf, Address address, MedicalRecord medicalRecord) {
>>>>>>> Stashed changes
        super(name, cpf, address);
    }

<<<<<<< Updated upstream
=======
    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public Consultation[] getConsultations() {
        return consultations;
    }

    public void setConsultations(Consultation[] consultations) {
        if (consultations == null) {
            System.out.println("Consultations array cannot be null.");
            return;
        }
        this.consultations = consultations;
    }

    public void cadastrarDados(String name, String cpf, Address address, MedicalRecord medicalRecord) {
        setName(name);
        setCPF(cpf);
        setAddress(address);
        setMedicalRecord(medicalRecord);
    }

    public void editorDados(String name, String cpf, Address address) {
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
            System.out.println("Consultation cannot be null.");
            return null;
        }
        consulta.setDate(newDate);
        consulta.setStatus(newStatus);
        return consulta;
    }

    public void excluirConsulta(Consultation[] consultasParaExcluir) {
        if (consultasParaExcluir == null || consultasParaExcluir.length == 0) {
            System.out.println("No consultations to exclude.");
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
>>>>>>> Stashed changes
}
