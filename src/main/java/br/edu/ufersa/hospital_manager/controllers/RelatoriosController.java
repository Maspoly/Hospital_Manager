package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Report;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ReportServiceProxy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RelatoriosController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private ComboBox<Doctor> cmbMedico;
    @FXML private DatePicker dateInicio;
    @FXML private DatePicker dateFim;
    @FXML private Label lblTotalConsultas;
    @FXML private Label lblAgendadas;
    @FXML private Label lblConcluidas;
    @FXML private Label lblCanceladas;
    @FXML private Label lblRelatoriosEncontrados;
    @FXML private VBox boxRelatorios;

    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();
    private final ReportServiceProxy reportService = new ReportServiceProxy();

    @FXML
    public void initialize() {
        carregarMedicos();
        configurarPeriodoPadrao();
    }

    private void carregarMedicos() {
        cmbMedico.getItems().clear();
        try {
            cmbMedico.getItems().addAll(doctorService.listAll());
        } catch (SQLException exception) {
            mostrarEstadoVazio("Não foi possível carregar os médicos.");
        }

        cmbMedico.setConverter(new javafx.util.StringConverter<Doctor>() {
            @Override
            public String toString(Doctor doctor) {
                if (doctor == null) {
                    return "";
                }
                return "Dr. " + doctor.getName() + " - CRM " + doctor.getCouncilCode();
            }

            @Override
            public Doctor fromString(String string) {
                return null;
            }
        });
        cmbMedico.setPromptText("Selecione um médico");
    }

    private void configurarPeriodoPadrao() {
        LocalDate hoje = LocalDate.now();
        dateInicio.setValue(hoje.withDayOfMonth(1));
        dateFim.setValue(hoje);
    }

    @FXML
    private void onGerarRelatorio(ActionEvent event) {
        gerarRelatorio();
    }

    private void gerarRelatorio() {
        Doctor medico = cmbMedico.getValue();
        LocalDate inicio = dateInicio.getValue();
        LocalDate fim = dateFim.getValue();

        if (medico == null) {
            mostrarEstadoVazio("Selecione um médico para gerar o relatório.");
            return;
        }

        if (inicio == null || fim == null) {
            mostrarEstadoVazio("Selecione um período válido.");
            return;
        }

        if (fim.isBefore(inicio)) {
            mostrarEstadoVazio("A data final não pode ser anterior à inicial.");
            return;
        }

        try {
            Report report = reportService.generateDoctorReport(
                    medico,
                    inicio.atStartOfDay(),
                    fim.atTime(23, 59, 59));
            mostrarRelatorio(medico, report);
        } catch (Exception exception) {
            mostrarEstadoVazio(exception.getMessage());
        }
    }

    private void mostrarRelatorio(Doctor medico, Report report) {
        boxRelatorios.getChildren().clear();
        boxRelatorios.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        lblTotalConsultas.setText(String.valueOf(report.getTotal()));
        lblAgendadas.setText(String.valueOf(report.getScheduled()));
        lblConcluidas.setText(String.valueOf(report.getCompleted()));
        lblCanceladas.setText(String.valueOf(report.getCanceled()));
        lblRelatoriosEncontrados.setText("Relatório de " + medico.getName());

        VBox card = new VBox(6);
        card.getStyleClass().add("medico-report-item");

        Label titulo = new Label("Dr. " + medico.getName() + " - CRM " + medico.getCouncilCode());
        titulo.getStyleClass().add("medico-patient-name");

        Label periodo = new Label("Período: " + report.getPeriodStart().format(FORMATO_DATA)
                + " até " + report.getPeriodEnd().format(FORMATO_DATA));
        periodo.getStyleClass().add("medico-patient-detail");

        Label geradoEm = new Label("Gerado em: " + report.getGeneratedAt().format(FORMATO_DATA_HORA));
        geradoEm.getStyleClass().add("medico-patient-detail");

        Label resumo = new Label("Total: " + report.getTotal()
                + " | Agendadas: " + report.getScheduled()
                + " | Concluídas: " + report.getCompleted()
                + " | Canceladas: " + report.getCanceled());
        resumo.getStyleClass().add("medico-patient-detail");

        card.getChildren().addAll(titulo, periodo, geradoEm, resumo);
        boxRelatorios.getChildren().add(card);
    }

    private void mostrarEstadoVazio(String mensagem) {
        boxRelatorios.getChildren().clear();
        boxRelatorios.setAlignment(javafx.geometry.Pos.CENTER);

        lblRelatoriosEncontrados.setText("Relatório de consultas");

        Label texto = new Label(mensagem);
        texto.getStyleClass().add("medico-empty-state-title");
        boxRelatorios.getChildren().add(texto);
    }

    // ===================== NAVEGAÇÃO ENTRE TELAS =====================

    @FXML
    public void goDashboard(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "Dashboard.fxml");
    }

    @FXML
    public void goMedicos(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "medicos.fxml");
    }

    @FXML
    public void goPacientes(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "pacientes.fxml");
    }

    @FXML
    public void goConsultas(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "consultas.fxml");
    }

    @FXML
    public void goBusca(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "busca.fxml");
    }

    @FXML
    public void goRelatorios(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "relatorios.fxml");
    }
}