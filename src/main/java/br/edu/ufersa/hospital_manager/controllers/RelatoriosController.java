package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.entities.Report;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ReportServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
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

    // ── Labels do usuário logado ──────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblVisualizarPerfil;

    // ── Botões de navegação ───────────────────────────────────
    @FXML private Button btnDashboard;
    @FXML private Button btnMedicos;
    @FXML private Button btnPacientes;
    @FXML private Button btnGerentes;
    @FXML private Button btnConsultas;
    @FXML private Button btnBusca;
    @FXML private Button btnRelatorios;

    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();
    private final ReportServiceProxy reportService = new ReportServiceProxy();
    
    // Dados mock para fallback
    private final List<Consultation> consultasMock = new ArrayList<>();
    private final List<Doctor> medicosMock = new ArrayList<>();

    @FXML
    public void initialize() {
        carregarDadosUsuario();
        configurarLinkPerfil();
        carregarDadosMock();
        carregarMedicos();
        configurarPeriodoPadrao();
    }

    /**
     * Preenche os dados do usuário logado na sidebar.
     */
    private void carregarDadosUsuario() {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        String nomeUsuario = usuario != null ? usuario.getName() : "Administrador";
        String cargoUsuario = role != null ? role.getDisplayName() : "Gerente";

        lblUserName.setText(nomeUsuario);
        lblUserRole.setText(cargoUsuario);
    }

    private void configurarLinkPerfil() {
        if (lblVisualizarPerfil != null) {
            lblVisualizarPerfil.setStyle("-fx-cursor: hand; -fx-text-fill: #60a5fa; -fx-underline: true;");
            lblVisualizarPerfil.setOnMouseClicked(this::onVisualizarPerfil);
        }
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

    private void carregarDadosMock() {
        // Médico mock
        Address enderecoMedico = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        Doctor doctor = new Doctor("Luiz Silva", "12345678900", enderecoMedico, 250.0f, "123456");
        medicosMock.add(doctor);

        // Paciente mock
        Address enderecoPaciente = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
        Patient patient = new Patient("Maria Santos", "11122233344", enderecoPaciente);

        // Consultas mock
        consultasMock.add(new Consultation(patient, doctor, LocalDateTime.now().minusDays(5), "COMPLETED"));
        consultasMock.add(new Consultation(patient, doctor, LocalDateTime.now().minusDays(3), "COMPLETED"));
        consultasMock.add(new Consultation(patient, doctor, LocalDateTime.now().plusDays(2), "SCHEDULED"));
        consultasMock.add(new Consultation(patient, doctor, LocalDateTime.now().minusDays(10), "CANCELED"));
    }

    private void carregarMedicos() {
        cmbMedico.getItems().clear();
        try {
            List<Doctor> medicos = doctorService.listAll();
            if (medicos != null && !medicos.isEmpty()) {
                cmbMedico.getItems().addAll(medicos);
            } else {
                cmbMedico.getItems().addAll(medicosMock);
            }
        } catch (SQLException exception) {
            cmbMedico.getItems().addAll(medicosMock);
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
        
        if (!cmbMedico.getItems().isEmpty()) {
            cmbMedico.setValue(cmbMedico.getItems().get(0));
        }
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
            // Fallback: gerar relatório com dados mock
            gerarRelatorioMock(medico, inicio, fim);
        }
    }

    private void gerarRelatorioMock(Doctor medico, LocalDate inicio, LocalDate fim) {
        int total = 0, agendadas = 0, concluidas = 0, canceladas = 0;
        
        for (Consultation c : consultasMock) {
            LocalDate dataConsulta = c.getDateTime().toLocalDate();
            if (!dataConsulta.isBefore(inicio) && !dataConsulta.isAfter(fim)) {
                total++;
                switch (c.getStatus()) {
                    case "SCHEDULED": agendadas++; break;
                    case "COMPLETED": concluidas++; break;
                    case "CANCELED": canceladas++; break;
                }
            }
        }

        Report report = new Report(medico, inicio.atStartOfDay(), fim.atTime(23, 59, 59), LocalDateTime.now(), total, agendadas, concluidas, canceladas);
        mostrarRelatorio(medico, report);
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
        lblTotalConsultas.setText("0");
        lblAgendadas.setText("0");
        lblConcluidas.setText("0");
        lblCanceladas.setText("0");

        Label texto = new Label(mensagem);
        texto.getStyleClass().add("medico-empty-state-title");
        boxRelatorios.getChildren().add(texto);
    }

    // ===================== NAVEGAÇÃO ENTRE TELAS =====================

    @FXML
    public void goDashboard(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "Dashboard.fxml");
    }

    @FXML
    public void goMedicos(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medicos.fxml");
    }

    @FXML
    public void goPacientes(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "pacientes.fxml");
    }

    @FXML
    public void goGerentes(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "gerentes.fxml");
    }

    @FXML
    public void goConsultas(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "consultas.fxml");
    }

    @FXML
    public void goBusca(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "busca.fxml");
    }

    @FXML
    public void goRelatorios(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "relatorios.fxml");
    }

    @FXML
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
    }
}