package br.edu.ufersa.hospital_manager.controllers;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
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

/**
 * Controller do Dashboard — Clínica Dr. Luiz
 *
 * Responsabilidades:
 *  - Inicializar os dados de resumo (médicos, pacientes, consultas).
 *  - Controlar a navegação entre as seções via sidebar.
 *  - Atualizar a saudação do usuário logado.
 */
public class DashboardController implements Initializable {

    // ── Labels de boas-vindas ──────────────────────────────────
    @FXML private Label lblWelcome;

    // ── Labels de métricas ────────────────────────────────────
    @FXML private Label lblTotalMedicos;
    @FXML private Label lblTotalPacientes;
    @FXML private Label lblConsultasHoje;
    @FXML private Label lblConsultasPendentes;

    // ── Labels do usuário logado ──────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;

    // ── Botões de navegação ───────────────────────────────────
    @FXML private Button btnDashboard;
    @FXML private Button btnMedicos;
    @FXML private Button btnPacientes;
    @FXML private Button btnConsultas;
    @FXML private Button btnBusca;
    @FXML private Button btnRelatorios;

    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();
    private final PatientServiceProxy patientService = new PatientServiceProxy();
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
     * Em produção, substitua pelos dados da sessão/autenticação.
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
     * Substitua pelas chamadas reais ao serviço/repositório.
     */
    private void carregarMetricas() {
        int totalMedicos          = buscarTotalMedicos();
        int totalPacientes        = buscarTotalPacientes();
        int consultasHoje         = buscarConsultasHoje();
        int consultasPendentes    = buscarConsultasPendentes();

        lblTotalMedicos.setText(String.valueOf(totalMedicos));
        lblTotalPacientes.setText(String.valueOf(totalPacientes));
        lblConsultasHoje.setText(String.valueOf(consultasHoje));
        lblConsultasPendentes.setText(String.valueOf(consultasPendentes));
    }

    private int buscarTotalMedicos() {
        try {
            return doctorService.listAll().size();
        } catch (SQLException exception) {
            return 1;
        }
    }

    private int buscarTotalPacientes() {
        try {
            return patientService.listAll().size();
        } catch (SQLException exception) {
            return 2;
        }
    }

    private int buscarConsultasHoje() {
        try {
            int total = 0;
            for (Consultation consultation : consultationService.listAll()) {
                if (consultation.getDateTime().toLocalDate().equals(LocalDate.now())) {
                    total++;
                }
            }
            return total;
        } catch (SQLException exception) {
            return 0;
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
        // Já estamos no dashboard — nenhuma ação extra necessária
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

    /**
     * Marca o botão selecionado como ativo e remove o estilo dos demais.
     */
    private void setNavAtivo(Button botaoAtivo) {
        Button[] todos = {
            btnDashboard, btnMedicos, btnPacientes,
            btnConsultas, btnBusca, btnRelatorios
        };
        for (Button btn : todos) {
            btn.getStyleClass().remove("nav-btn-active");
        }
        if (!botaoAtivo.getStyleClass().contains("nav-btn-active")) {
            botaoAtivo.getStyleClass().add("nav-btn-active");
        }
    }

    /**
     * Carrega outro FXML na área central.
     * Adapte conforme a arquitetura de navegação do seu projeto
     * (ex.: injetar um controlador-raiz, usar um ScreenManager, etc.).
     *
     * @param fxmlPath caminho relativo ao classpath do arquivo FXML
     */
    private void navegarPara(String fxmlPath) {
        try {
            // Exemplo de navegação com troca de cena:
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.getScene().setRoot(root);

            System.out.println("Navegando para: " + fxmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
