package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import br.edu.ufersa.hospital_manager.model.entities.*;
import br.edu.ufersa.hospital_manager.model.services.*;


public class PacienteDashboardController {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private Label lblIniciais;

    @FXML
    private Label lblNomePaciente;

    @FXML
    private Label lblCpfPaciente;

    @FXML
    private Label lblTotalConsultas;

    @FXML
    private Label lblConsultasAgendadas;

    @FXML
    private Label lblTotalProntuarios;

    @FXML
    private VBox boxProximasConsultas;

    private Patient pacienteLogado;

    private final PatientServiceProxy patientService = new PatientServiceProxy();
    private final ConsultationServiceProxy consultationService = new ConsultationServiceProxy();
    private final MedicalRecordServiceProxy medicalRecordService = new MedicalRecordServiceProxy();

    @FXML
    public void initialize() {
        carregarPacienteLogado();
        atualizarDashboard();
        carregarProximasConsultas();
    }

    /**
     * Carrega o paciente logado a partir do ServiceRoleContext
     */
    private void carregarPacienteLogado() {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        // Verifica se o usuário logado é um Paciente
        if (usuario instanceof Patient && role == ServiceRole.PATIENT) {
            pacienteLogado = (Patient) usuario;
            atualizarDadosPaciente();
        } else {
            // Fallback: tenta buscar pelo CPF mock (apenas para teste)
            try {
                pacienteLogado = patientService.findByCPF("11122233344");
                if (pacienteLogado != null) {
                    atualizarDadosPaciente();
                } else {
                    mostrarDadosVazios();
                }
            } catch (SQLException e) {
                mostrarDadosVazios();
            }
        }
    }

    private void atualizarDadosPaciente() {
        if (pacienteLogado == null) {
            mostrarDadosVazios();
            return;
        }

        String nome = pacienteLogado.getName();
        String[] partes = nome.split(" ");
        StringBuilder iniciais = new StringBuilder();
        for (String parte : partes) {
            if (!parte.isEmpty()) {
                iniciais.append(Character.toUpperCase(parte.charAt(0)));
            }
            if (iniciais.length() >= 2) {
                break;
            }
        }

        lblIniciais.setText(iniciais.length() > 0 ? iniciais.toString() : "P");
        lblNomePaciente.setText(nome);
        lblCpfPaciente.setText("CPF: " + formatarCpf(pacienteLogado.getCPF()));
    }

    private void mostrarDadosVazios() {
        lblIniciais.setText("P");
        lblNomePaciente.setText("Paciente");
        lblCpfPaciente.setText("CPF: ---");
    }

    private void atualizarDashboard() {
        if (pacienteLogado == null) {
            lblTotalConsultas.setText("0");
            lblConsultasAgendadas.setText("0");
            lblTotalProntuarios.setText("0");
            return;
        }

        try {
            // Busca consultas do paciente
            List<Consultation> consultas = consultationService.findByPatient(pacienteLogado);

            int total = consultas.size();
            int agendadas = 0;

            for (Consultation c : consultas) {
                if ("SCHEDULED".equals(c.getStatus())) {
                    agendadas++;
                }
            }

            lblTotalConsultas.setText(String.valueOf(total));
            lblConsultasAgendadas.setText(String.valueOf(agendadas));

            // Busca prontuário do paciente
            try {
                MedicalRecord record = medicalRecordService.findByPatient(pacienteLogado);
                lblTotalProntuarios.setText(record != null ? "1" : "0");
            } catch (Exception e) {
                lblTotalProntuarios.setText("0");
            }

        } catch (Exception e) {
            lblTotalConsultas.setText("0");
            lblConsultasAgendadas.setText("0");
            lblTotalProntuarios.setText("0");
        }
    }

    private void carregarProximasConsultas() {
        boxProximasConsultas.getChildren().clear();
        boxProximasConsultas.setAlignment(javafx.geometry.Pos.CENTER);

        if (pacienteLogado == null) {
            mostrarEstadoVazio("Nenhum paciente logado.");
            return;
        }

        try {
            List<Consultation> consultas = consultationService.findByPatient(pacienteLogado);
            List<Consultation> proximas = new ArrayList<>();

            for (Consultation c : consultas) {
                if ("SCHEDULED".equals(c.getStatus()) && c.getDateTime().isAfter(LocalDateTime.now())) {
                    proximas.add(c);
                }
            }

            if (proximas.isEmpty()) {
                mostrarEstadoVazio("Nenhuma consulta agendada.");
                return;
            }

            boxProximasConsultas.setAlignment(javafx.geometry.Pos.TOP_LEFT);

            for (Consultation c : proximas) {
                VBox item = criarItemConsulta(c);
                boxProximasConsultas.getChildren().add(item);
            }

        } catch (Exception e) {
            mostrarEstadoVazio("Erro ao carregar consultas: " + e.getMessage());
        }
    }

    private VBox criarItemConsulta(Consultation c) {
        VBox item = new VBox(2);
        item.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-padding: 12 16 12 16; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-border-width: 1;");

        String nomeMedico = c.getDoctor() != null ? "Dr. " + c.getDoctor().getName() : "Médico não informado";
        Label medico = new Label(nomeMedico);
        medico.getStyleClass().add("paciente-patient-name");
        medico.setStyle("-fx-text-fill: #1f2937; -fx-font-size: 13.5px; -fx-font-weight: 600;");

        Label dataHora = new Label(c.getDateTime().format(FORMATO_DATA_HORA));
        dataHora.getStyleClass().add("paciente-patient-detail");
        dataHora.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

        Label status = new Label("Agendada");
        status.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 11px; -fx-font-weight: 600;");

        item.getChildren().addAll(medico, dataHora, status);
        return item;
    }

    private void mostrarEstadoVazio(String mensagem) {
        boxProximasConsultas.getChildren().clear();
        boxProximasConsultas.setAlignment(javafx.geometry.Pos.CENTER);
        boxProximasConsultas.setPadding(new Insets(40, 20, 40, 20));

        Label texto = new Label(mensagem);
        texto.getStyleClass().add("paciente-empty-state-title");
        boxProximasConsultas.getChildren().add(texto);
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf == null ? "" : cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    // ===================== NAVEGAÇÃO =====================

    @FXML
    public void goDashboard(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_dashboard.fxml", "paciente.css");
    }

    @FXML
    public void goProntuarios(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_prontuarios.fxml", "paciente.css");
    }

    @FXML
    public void goConsultas(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_consultas.fxml", "paciente.css");
    }

    @FXML
    public void goEditarDados(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_editar_dados.fxml", "paciente.css");
    }

    @FXML
    public void goAgendarConsulta(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_agendar_consulta.fxml", "paciente.css");
    }

    @FXML
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
    }
}