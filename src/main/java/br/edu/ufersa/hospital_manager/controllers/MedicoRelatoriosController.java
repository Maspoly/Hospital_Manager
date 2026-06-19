package br.edu.ufersa.hospital_manager.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import br.edu.ufersa.hospital_manager.model.entities.*;

public class MedicoRelatoriosController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Campos do formulário ────────────────────────────────────────────────
    @FXML private DatePicker dateInicio;
    @FXML private DatePicker dateFim;

    // ── Labels de estatísticas ──────────────────────────────────────────────
    @FXML private Label lblTotalConsultas;
    @FXML private Label lblAgendadas;
    @FXML private Label lblConcluidas;
    @FXML private Label lblCanceladas;
    @FXML private Label lblConsultasEncontradas;

    // ── Container para listar consultas ─────────────────────────────────────
    @FXML private VBox boxConsultas;

    // ── Labels do médico logado (sidebar) ──────────────────────────────────
    @FXML private Label lblIniciais;
    @FXML private Label lblNomeMedico;
    @FXML private Label lblCrmMedico;

    // ── Dados mock ──────────────────────────────────────────────────────────
    private final List<Consultation> consultasMock = new ArrayList<>();
    private Doctor medicoMock;

    @FXML
    public void initialize() {
        carregarDadosMock();
        configurarDadosMedico();
        mostrarEstadoVazio();
    }

    private void carregarDadosMock() {
        // Cria médico mock
        Address enderecoMedico = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        medicoMock = new Doctor("João Lourenço", "12345678900", enderecoMedico, 250.0f, "123456");

        // Cria pacientes mock
        Address enderecoPaciente1 = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
        Patient paciente1 = new Patient("Maria Santos", "11122233344", enderecoPaciente1);

        Address enderecoPaciente2 = new Address("Av. Central", "200", "Centro", "Mossoró", "RN");
        Patient paciente2 = new Patient("João Oliveira", "55566677788", enderecoPaciente2);

        // Cria consultas mock
        consultasMock.add(new Consultation(paciente1, medicoMock, 
            java.time.LocalDateTime.now().minusDays(5), "COMPLETED"));
        consultasMock.add(new Consultation(paciente2, medicoMock, 
            java.time.LocalDateTime.now().minusDays(3), "COMPLETED"));
        consultasMock.add(new Consultation(paciente1, medicoMock, 
            java.time.LocalDateTime.now().plusDays(2), "SCHEDULED"));
        consultasMock.add(new Consultation(paciente2, medicoMock, 
            java.time.LocalDateTime.now().plusDays(5), "SCHEDULED"));
        consultasMock.add(new Consultation(paciente1, medicoMock, 
            java.time.LocalDateTime.now().minusDays(10), "CANCELED"));
        consultasMock.add(new Consultation(paciente2, medicoMock, 
            java.time.LocalDateTime.now().plusDays(7), "SCHEDULED"));
    }

    private void configurarDadosMedico() {
        // TODO: carregar dados do médico logado via DoctorService/DoctorDAO
        lblIniciais.setText("J");
        lblNomeMedico.setText("Dr. João Lourenço");
        lblCrmMedico.setText("CRM-12345");
    }

    private void mostrarEstadoVazio() {
        boxConsultas.getChildren().clear();
        boxConsultas.setAlignment(javafx.geometry.Pos.CENTER);

        // Cria ícone
        javafx.scene.layout.StackPane icone = new javafx.scene.layout.StackPane();
        javafx.scene.shape.Circle circulo = new javafx.scene.shape.Circle(26);
        circulo.getStyleClass().add("medico-empty-state-icon-circle");
        Label glyph = new Label("📋");
        glyph.setStyle("-fx-font-size: 20px; -fx-text-fill: #c1c5cc;");
        icone.getChildren().addAll(circulo, glyph);

        Label texto = new Label("Selecione um período acima e clique em 'Gerar Relatório'");
        texto.getStyleClass().add("medico-empty-state-title");

        boxConsultas.getChildren().addAll(icone, texto);
    }

    @FXML
    private void onGerarRelatorio(ActionEvent event) {
        LocalDate inicio = dateInicio.getValue();
        LocalDate fim = dateFim.getValue();

        // Filtra consultas pelo período
        List<Consultation> filtradas = new ArrayList<>();
        for (Consultation c : consultasMock) {
            LocalDate dataConsulta = c.getDateTime().toLocalDate();
            boolean dentroDoIntervalo = (inicio == null || !dataConsulta.isBefore(inicio))
                    && (fim == null || !dataConsulta.isAfter(fim));
            if (dentroDoIntervalo) {
                filtradas.add(c);
            }
        }

        // Conta por status
        int agendadas = 0, concluidas = 0, canceladas = 0;
        for (Consultation c : filtradas) {
            switch (c.getStatus()) {
                case "SCHEDULED": agendadas++; break;
                case "COMPLETED": concluidas++; break;
                case "CANCELED": canceladas++; break;
                default: break;
            }
        }

        // Atualiza estatísticas
        lblTotalConsultas.setText(String.valueOf(filtradas.size()));
        lblAgendadas.setText(String.valueOf(agendadas));
        lblConcluidas.setText(String.valueOf(concluidas));
        lblCanceladas.setText(String.valueOf(canceladas));
        lblConsultasEncontradas.setText("Consultas Encontradas (" + filtradas.size() + ")");

        // Renderiza lista
        boxConsultas.getChildren().clear();
        boxConsultas.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        if (filtradas.isEmpty()) {
            Label vazio = new Label("Nenhuma consulta encontrada para o período selecionado.");
            vazio.getStyleClass().add("medico-empty-state-title");
            boxConsultas.getChildren().add(vazio);
            boxConsultas.setAlignment(javafx.geometry.Pos.CENTER);
            return;
        }

        for (Consultation c : filtradas) {
            VBox item = new VBox(2);
            item.getStyleClass().add("medico-consultation-item");

            Label titulo = new Label(c.getPatient().getName());
            titulo.getStyleClass().add("medico-patient-name");

            String statusLabel = traduzirStatus(c.getStatus());
            String dataHora = c.getDateTime().format(FORMATO_DATA_HORA);
            Label subtitulo = new Label(dataHora + " · " + statusLabel);
            subtitulo.getStyleClass().add("medico-patient-detail");
            subtitulo.setStyle("-fx-text-fill: " + corStatus(c.getStatus()) + ";");

            item.getChildren().addAll(titulo, subtitulo);
            boxConsultas.getChildren().add(item);
        }
    }

    private String traduzirStatus(String status) {
        switch (status) {
            case "SCHEDULED": return "Agendada";
            case "COMPLETED": return "Concluída";
            case "CANCELED": return "Cancelada";
            default: return status;
        }
    }

    private String corStatus(String status) {
        switch (status) {
            case "SCHEDULED": return "#2563eb";
            case "COMPLETED": return "#059669";
            case "CANCELED": return "#dc2626";
            default: return "#6b7280";
        }
    }

    // ===================== NAVEGAÇÃO ENTRE TELAS =====================

    @FXML
    public void goMeusPacientes(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "medico_pacientes.fxml", "medico.css");
    }

    @FXML
    public void goCadastrarProntuario(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "medico_cadastrar_prontuario.fxml", "medico.css");
    }

    @FXML
    public void goEditarDados(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "medico_editar_pacientes.fxml", "medico.css");
    }

    @FXML
    public void goRelatorios(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "medico_relatorios.fxml", "medico.css");
    }

    @FXML
    public void onSair(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "login_medico.fxml", "medico.css");
    }
}