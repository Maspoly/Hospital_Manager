package br.edu.ufersa.hospital_manager.controllers;

import br.edu.ufersa.hospital_manager.model.DAO.*;
import br.edu.ufersa.hospital_manager.model.entities.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CadastroConsultaController {

    // ── Campos do formulário ──────────────────────────────────────────────────
    @FXML private ComboBox<Patient> fldPaciente;
    @FXML private ComboBox<Doctor>  fldMedico;
    @FXML private DatePicker        fldData;
    @FXML private ComboBox<String>  fldHora;
    @FXML private ComboBox<String>  fldStatus;
    @FXML private Label             lblErro;

    // ── Labels do usuário logado (sidebar) ───────────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;

    // Acesso direto aos DAOs:
    // - ConsultationService.scheduleConsultation() bloqueia datas passadas,
    //   mas aqui queremos salvar com qualquer status (inclusive COMPLETED/CANCELED).
    // - DoctorService e PatientService não expõem listAll(), então usamos os DAOs.
    private final ConsultationDAO consultationDAO = new ConsultationDAO();
    private final DoctorDAO       doctorDAO       = new DoctorDAO();
    private final PatientDAO      patientDAO      = new PatientDAO();

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        carregarPacientes();
        carregarMedicos();
        carregarHorarios();
        carregarStatus();
        fldData.setValue(LocalDate.now());
    }

    private void carregarPacientes() {
        try {
            List<Patient> lista = patientDAO.listAll();
            fldPaciente.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar pacientes: " + e.getMessage());
        }

        fldPaciente.setConverter(new StringConverter<>() {
            @Override public String toString(Patient p) {
                return p == null ? "" : p.getName() + " — " + p.getCPF();
            }
            @Override public Patient fromString(String s) { return null; }
        });
    }

    private void carregarMedicos() {
        try {
            List<Doctor> lista = doctorDAO.listAll();
            fldMedico.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar médicos: " + e.getMessage());
        }

        fldMedico.setConverter(new StringConverter<>() {
            @Override public String toString(Doctor d) {
                return d == null ? "" : d.getName() + " — " + d.getCouncilCode();
            }
            @Override public Doctor fromString(String s) { return null; }
        });
    }

    private void carregarHorarios() {
        List<String> horarios = new ArrayList<>();
        LocalTime hora = LocalTime.of(7, 0);
        LocalTime fim  = LocalTime.of(19, 30);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        while (!hora.isAfter(fim)) {
            horarios.add(hora.format(fmt));
            hora = hora.plusMinutes(30);
        }
        fldHora.setItems(FXCollections.observableArrayList(horarios));
    }

    private void carregarStatus() {
        fldStatus.setItems(FXCollections.observableArrayList(
                "SCHEDULED", "COMPLETED", "CANCELED"
        ));
        fldStatus.setValue("SCHEDULED"); // padrão para novas consultas
    }

    // ── Ação: Salvar ──────────────────────────────────────────────────────────
    @FXML
    private void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
            LocalTime hora = LocalTime.parse(
                    fldHora.getValue(), DateTimeFormatter.ofPattern("HH:mm")
            );
            LocalDateTime dateTime = LocalDateTime.of(fldData.getValue(), hora);

            // Consultation(Patient, Doctor, LocalDateTime, String) — construtor exato da entidade
            Consultation consulta = new Consultation(
                    fldPaciente.getValue(),
                    fldMedico.getValue(),
                    dateTime,
                    fldStatus.getValue()
            );
            consultationDAO.create(consulta); // persiste e seta o ID gerado

            NavigationHelper.showInfo("Sucesso", "Consulta agendada com sucesso!");
            NavigationHelper.goTo((javafx.scene.Node) event.getSource(), "consultas.fxml");

        } catch (RuntimeException e) {
            // Captura validações da entidade Consultation (status inválido, etc.)
            mostrarErro(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro no banco de dados: " + e.getMessage());
        }
    }

    // ── Ação: Cancelar ────────────────────────────────────────────────────────
    @FXML
    private void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((javafx.scene.Node) event.getSource(), "consultas.fxml");
    }

    // ── Validação ─────────────────────────────────────────────────────────────
    private boolean validar() {
        List<String> erros = new ArrayList<>();

        if (fldPaciente.getValue() == null) erros.add("Selecione um paciente.");
        if (fldMedico.getValue() == null)   erros.add("Selecione um médico.");
        if (fldData.getValue() == null)     erros.add("Selecione a data da consulta.");
        if (fldHora.getValue() == null)     erros.add("Selecione o horário da consulta.");
        if (fldStatus.getValue() == null)   erros.add("Selecione o status da consulta.");

        // Não permite data passada quando o status for SCHEDULED
        if (fldData.getValue() != null
                && fldData.getValue().isBefore(LocalDate.now())
                && "SCHEDULED".equals(fldStatus.getValue())) {
            erros.add("Não é possível agendar uma consulta para uma data passada.");
        }

        if (!erros.isEmpty()) {
            mostrarErro(String.join("\n", erros));
            return false;
        }
        ocultarErro();
        return true;
    }

    // ── Helpers de feedback ───────────────────────────────────────────────────
    private void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }

    private void ocultarErro() {
        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }

    // ── Navegação da sidebar ──────────────────────────────────────────────────
    @FXML private void onDashboard(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "dashboard.fxml"); }
    @FXML private void onMedicos(ActionEvent e)    { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "medicos.fxml"); }
    @FXML private void onPacientes(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "pacientes.fxml"); }
    @FXML private void onConsultas(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "consultas.fxml"); }
    @FXML private void onBusca(ActionEvent e)      { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "busca.fxml"); }
    @FXML private void onRelatorios(ActionEvent e) { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "relatorios.fxml"); }
}
