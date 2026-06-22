package br.edu.ufersa.hospital_manager.controllers;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ManagerServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.PatientServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DashboardController implements Initializable {

    // ── Labels de boas-vindas ──────────────────────────────────
    @FXML private Label lblWelcome;

    // ── Labels de métricas ────────────────────────────────────
    @FXML private Label lblTotalMedicos;
    @FXML private Label lblTotalPacientes;
    @FXML private Label lblTotalGerentes;
    @FXML private Label lblConsultasPendentes;

    // ── Labels do usuário logado ──────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;

    // ── Botões de navegação ───────────────────────────────────
    @FXML private Button btnDashboard;
    @FXML private Button btnMedicos;
    @FXML private Button btnPacientes;
    @FXML private Button btnGerentes;
    @FXML private Button btnConsultas;
    @FXML private Button btnBusca;
    @FXML private Button btnRelatorios;

    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();
    private final PatientServiceProxy patientService = new PatientServiceProxy();
    private final ManagerServiceProxy managerService = new ManagerServiceProxy();
    private final ConsultationServiceProxy consultationService = new ConsultationServiceProxy();

    // ─────────────────────────────────────────────────────────
    // Inicialização
    // ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carregarDadosUsuario();
        carregarMetricas();
    }

    /**
     * Preenche os dados do usuário logado na sidebar.
     */
    private void carregarDadosUsuario() {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        String nomeUsuario = usuario != null ? usuario.getName() : "Administrador";
        String cargoUsuario = role != null ? role.getDisplayName() : "Gerente";

        lblWelcome.setText("Bem-vindo, " + nomeUsuario + "!");
        lblUserName.setText(nomeUsuario);
        lblUserRole.setText(cargoUsuario);
    }

    /**
     * Carrega as métricas do dashboard.
     */
    private void carregarMetricas() {
        int totalMedicos = buscarTotalMedicos();
        int totalPacientes = buscarTotalPacientes();
        int totalGerentes = buscarTotalGerentes();
        int consultasPendentes = buscarConsultasPendentes();

        // Verifica se os Labels não são null antes de setar texto
        if (lblTotalMedicos != null) {
            lblTotalMedicos.setText(String.valueOf(totalMedicos));
        }
        if (lblTotalPacientes != null) {
            lblTotalPacientes.setText(String.valueOf(totalPacientes));
        }
        if (lblTotalGerentes != null) {
            lblTotalGerentes.setText(String.valueOf(totalGerentes));
        }
        if (lblConsultasPendentes != null) {
            lblConsultasPendentes.setText(String.valueOf(consultasPendentes));
        }
    }

    private int buscarTotalMedicos() {
        try {
            return doctorService.listAll().size();
        } catch (SQLException exception) {
            return 0;
        }
    }

    private int buscarTotalPacientes() {
        try {
            return patientService.listAll().size();
        } catch (SQLException exception) {
            return 0;
        }
    }

    private int buscarTotalGerentes() {
        try {
            return managerService.listAll().size();
        } catch (SQLException exception) {
            return 1; // Pelo menos o admin padrão
        }
    }

    private int buscarConsultasPendentes() {
        try {
            int total = 0;
            for (Consultation consultation : consultationService.listAll()) {
                if ("SCHEDULED".equals(consultation.getStatus())) {
                    total++;
                }
            }
            return total;
        } catch (SQLException exception) {
            return 0;
        }
    }

    // ─────────────────────────────────────────────────────────
    // Navegação — handlers da sidebar
    // ─────────────────────────────────────────────────────────

    @FXML
    private void onDashboard() {
        setNavAtivo(btnDashboard);
    }

    @FXML
    private void onMedicos() {
        setNavAtivo(btnMedicos);
        navegarPara("/br/edu/ufersa/hospital_manager/views/medicos.fxml");
    }

    @FXML
    private void onPacientes() {
        setNavAtivo(btnPacientes);
        navegarPara("/br/edu/ufersa/hospital_manager/views/pacientes.fxml");
    }

    @FXML
    private void onGerentes() {
        setNavAtivo(btnGerentes);
        navegarPara("/br/edu/ufersa/hospital_manager/views/gerentes.fxml");
    }

    @FXML
    private void onConsultas() {
        setNavAtivo(btnConsultas);
        navegarPara("/br/edu/ufersa/hospital_manager/views/consultas.fxml");
    }

    @FXML
    private void onBusca() {
        setNavAtivo(btnBusca);
        navegarPara("/br/edu/ufersa/hospital_manager/views/busca.fxml");
    }

    @FXML
    private void onRelatorios() {
        setNavAtivo(btnRelatorios);
        navegarPara("/br/edu/ufersa/hospital_manager/views/relatorios.fxml");
    }

    // ─────────────────────────────────────────────────────────
    // Utilitários de navegação
    // ─────────────────────────────────────────────────────────

    private void setNavAtivo(Button botaoAtivo) {
        Button[] todos = {
            btnDashboard, btnMedicos, btnPacientes,
            btnGerentes, btnConsultas, btnBusca, btnRelatorios
        };
        for (Button btn : todos) {
            if (btn != null) {
                btn.getStyleClass().remove("nav-btn-active");
            }
        }
        if (botaoAtivo != null && !botaoAtivo.getStyleClass().contains("nav-btn-active")) {
            botaoAtivo.getStyleClass().add("nav-btn-active");
        }
    }

    private void navegarPara(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}