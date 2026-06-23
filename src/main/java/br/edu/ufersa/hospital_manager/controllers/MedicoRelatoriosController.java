package br.edu.ufersa.hospital_manager.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.entities.Report;
import br.edu.ufersa.hospital_manager.model.services.ReportServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import br.edu.ufersa.hospital_manager.util.ProxyFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class MedicoRelatoriosController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private DatePicker dateInicio;
    @FXML private DatePicker dateFim;

    @FXML private Label lblTotalConsultas;
    @FXML private Label lblAgendadas;
    @FXML private Label lblConcluidas;
    @FXML private Label lblCanceladas;
    @FXML private Label lblRelatoriosEncontrados;
    @FXML private Label lblVisualizarPerfil;

    @FXML private VBox boxRelatorios;

    @FXML private Label lblIniciais;
    @FXML private Label lblNomeMedico;
    @FXML private Label lblCrmMedico;

    private Doctor medicoLogado;
    private final ReportServiceProxy reportService = (ReportServiceProxy) ProxyFactory.createProxy("REPORT");

    @FXML
    public void initialize() {
        configurarDadosMedico();
        configurarPeriodoPadrao();
        atualizarRelatorio();
    }

    private void configurarPeriodoPadrao() {
        LocalDate hoje = LocalDate.now();
        dateInicio.setValue(hoje.withDayOfMonth(1));
        dateFim.setValue(hoje);
    }

    @FXML
    private void onPeriodoAlterado() {
        atualizarRelatorio();
    }

    private void configurarDadosMedico() {
        if (ServiceRoleContext.getCurrentUser() instanceof Doctor) {
            medicoLogado = (Doctor) ServiceRoleContext.getCurrentUser();
            lblIniciais.setText(extrairIniciais(medicoLogado.getName()));
            lblNomeMedico.setText("Dr. " + medicoLogado.getName());
            lblCrmMedico.setText("CRM-" + medicoLogado.getCouncilCode());
            return;
        }

        medicoLogado = null;
        lblIniciais.setText("D");
        lblNomeMedico.setText("Dr. Médico");
        lblCrmMedico.setText("CRM-000000");
    }
    @FXML
    private void onVisualizarPerfil(MouseEvent event) {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        if (usuario == null || role == null) {
            NavigationHelper.showError("Usuário não encontrado.");
            return;
        }

        switch (role) {
            case MANAGER:
                NavigationHelper.goTo(lblVisualizarPerfil, "perfil_gerente.fxml");
                break;
            case DOCTOR:
                NavigationHelper.goTo(lblVisualizarPerfil, "medico_editar_dados.fxml", "medico.css");
                break;
            case PATIENT:
                NavigationHelper.goTo(lblVisualizarPerfil, "paciente_editar_dados.fxml", "paciente.css");
                break;
            default:
                NavigationHelper.showError("Perfil não encontrado.");
                break;
        }
    }
    @FXML
    private void onGerarRelatorio(ActionEvent event) {
        atualizarRelatorio();
    }

    private void atualizarRelatorio() {
        LocalDate inicio = dateInicio.getValue();
        LocalDate fim = dateFim.getValue();

        if (medicoLogado == null || inicio == null || fim == null) {
            mostrarEstadoVazio("Selecione um período válido para gerar o relatório.");
            return;
        }

        if (fim.isBefore(inicio)) {
            mostrarEstadoVazio("A data final não pode ser anterior à data inicial.");
            return;
        }

        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime fimDateTime = fim.atTime(23, 59, 59);

        try {
            Report report = reportService.generateDoctorReport(medicoLogado, inicioDateTime, fimDateTime);
            mostrarRelatorio(report);
        } catch (Exception exception) {
            mostrarEstadoVazio(exception.getMessage());
        }
    }

    private void mostrarRelatorio(Report report) {
        boxRelatorios.getChildren().clear();
        boxRelatorios.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        lblTotalConsultas.setText(String.valueOf(report.getTotal()));
        lblAgendadas.setText(String.valueOf(report.getScheduled()));
        lblConcluidas.setText(String.valueOf(report.getCompleted()));
        lblCanceladas.setText(String.valueOf(report.getCanceled()));

        lblRelatoriosEncontrados.setText("Relatório do período selecionado");

        VBox card = new VBox(6);
        card.getStyleClass().add("medico-report-item");

        Label titulo = new Label("Relatório gerado em " + formatarDataHora(report.getGeneratedAt()));
        titulo.getStyleClass().add("medico-patient-name");

        Label periodo = new Label("Período: " + formatarData(report.getPeriodStart()) + " até " + formatarData(report.getPeriodEnd()));
        periodo.getStyleClass().add("medico-patient-detail");

        Label resumo = new Label("Total: " + report.getTotal()
                + " | Agendadas: " + report.getScheduled()
                + " | Concluídas: " + report.getCompleted()
                + " | Canceladas: " + report.getCanceled());
        resumo.getStyleClass().add("medico-patient-detail");

        card.getChildren().addAll(titulo, periodo, resumo);
        boxRelatorios.getChildren().add(card);
    }

    private void mostrarEstadoVazio(String mensagem) {
        boxRelatorios.getChildren().clear();
        boxRelatorios.setAlignment(javafx.geometry.Pos.CENTER);

        Label texto = new Label(mensagem);
        texto.getStyleClass().add("medico-empty-state-title");
        boxRelatorios.getChildren().add(texto);
        lblRelatoriosEncontrados.setText("Relatório atual");
    }

    private String formatarData(LocalDateTime data) {
        return data == null ? "-" : data.format(FORMATO_DATA);
    }

    private String formatarDataHora(LocalDateTime data) {
        return data == null ? "-" : data.format(FORMATO_DATA_HORA);
    }

    @FXML
    public void goMeusPacientes(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "medico_pacientes.fxml", "medico.css");
    }

    @FXML
    public void goMinhasConsultas(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "medico_consultas.fxml", "medico.css");
    }

    @FXML
    public void goCadastrarProntuario(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "medico_cadastrar_prontuario.fxml", "medico.css");
    }

    @FXML
    public void goRelatorios(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "medico_relatorios.fxml", "medico.css");
    }

    @FXML
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "login.fxml");
    }

    private String extrairIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "D";
        }

        StringBuilder iniciais = new StringBuilder();
        for (String parte : nome.trim().split("\\s+")) {
            if (!parte.isBlank()) {
                iniciais.append(Character.toUpperCase(parte.charAt(0)));
            }
            if (iniciais.length() == 2) {
                break;
            }
        }

        return iniciais.length() > 0 ? iniciais.toString() : "D";
    }
}