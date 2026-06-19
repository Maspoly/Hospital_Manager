package br.edu.ufersa.hospital_manager.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import br.edu.ufersa.hospital_manager.model.entities.*;

public class RelatoriosController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private ComboBox<String> cmbMedico;

    @FXML
    private DatePicker dateInicio;

    @FXML
    private DatePicker dateFim;

    @FXML
    private Label lblTotalConsultas;

    @FXML
    private Label lblAgendadas;

    @FXML
    private Label lblConcluidas;

    @FXML
    private Label lblCanceladas;

    @FXML
    private Label lblConsultasEncontradas;

    @FXML
    private VBox boxConsultasEncontradas;

    // Dados mock — futuramente substituídos por ConsultationDAO/ConsultationServices/ReportService
    private final List<Consultation> consultasMock = new ArrayList<>();
    private Doctor medicoMock;

    @FXML
    public void initialize() {
        carregarDadosMock();
        cmbMedico.getItems().add("Dr. Luiz Silva - CRM " + medicoMock.getCouncilCode());
    }

    private void carregarDadosMock() {
        Address enderecoMedico = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        medicoMock = new Doctor("Luiz Silva", "12345678900", enderecoMedico, 250.0f, "123456");

        Address enderecoPaciente = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
        Patient paciente = new Patient("Maria Santos", "11122233344", enderecoPaciente);

        consultasMock.add(new Consultation(paciente, medicoMock, LocalDateTime.now().minusDays(5), "COMPLETED"));
        consultasMock.add(new Consultation(paciente, medicoMock, LocalDateTime.now().plusDays(3), "SCHEDULED"));
    }

    @FXML
    public void onGerarRelatorio(ActionEvent event) {
        LocalDate inicio = dateInicio.getValue();
        LocalDate fim = dateFim.getValue();

        List<Consultation> filtradas = new ArrayList<>();
        for (Consultation c : consultasMock) {
            boolean dentroDoIntervalo = (inicio == null || !c.getDateTime().isBefore(inicio.atStartOfDay()))
                    && (fim == null || !c.getDateTime().isAfter(fim.atStartOfDay()));
            if (dentroDoIntervalo) {
                filtradas.add(c);
            }
        }

        int agendadas = 0, concluidas = 0, canceladas = 0;
        for (Consultation c : filtradas) {
            switch (c.getStatus()) {
                case "SCHEDULED": agendadas++; break;
                case "COMPLETED": concluidas++; break;
                case "CANCELED": canceladas++; break;
                default: break;
            }
        }

        lblTotalConsultas.setText(String.valueOf(filtradas.size()));
        lblAgendadas.setText(String.valueOf(agendadas));
        lblConcluidas.setText(String.valueOf(concluidas));
        lblCanceladas.setText(String.valueOf(canceladas));

        lblConsultasEncontradas.setText("Consultas Encontradas (" + filtradas.size() + ")");

        boxConsultasEncontradas.getChildren().clear();
        if (filtradas.isEmpty()) {
            Label vazio = new Label("Nenhuma consulta encontrada para os filtros selecionados.");
            vazio.getStyleClass().add("empty-state-title");
            boxConsultasEncontradas.getChildren().add(vazio);
        } else {
            for (Consultation c : filtradas) {
                Label titulo = new Label(c.getPatient().getName() + " — Dr. " + c.getDoctor().getName());
                titulo.getStyleClass().add("cell-title");
                Label subtitulo = new Label(c.getDateTime().format(FORMATO_DATA) + " · " + c.getStatus());
                subtitulo.getStyleClass().add("cell-subtitle");
                boxConsultasEncontradas.getChildren().add(new VBox(2, titulo, subtitulo));
            }
        }
    }

    // ===================== NAVEGAÇÃO ENTRE TELAS =====================

    @FXML
    public void goDashboard(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "dashboard.fxml");
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
