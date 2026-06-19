package br.edu.ufersa.hospital_manager.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

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
        String nomeUsuario = "Administrador"; // Recuperar da sessão
        String cargoUsuario = "Gerente";      // Recuperar da sessão

        lblWelcome.setText("Bem-vindo, " + nomeUsuario + "!");
        lblUserName.setText(nomeUsuario);
        lblUserRole.setText(cargoUsuario);
    }

    /**
     * Carrega as métricas do dashboard.
     * Substitua pelas chamadas reais ao serviço/repositório.
     */
    private void carregarMetricas() {
        // TODO: injetar serviços e buscar dados do banco
        int totalMedicos          = buscarTotalMedicos();
        int totalPacientes        = buscarTotalPacientes();
        int consultasHoje         = buscarConsultasHoje();
        int consultasPendentes    = buscarConsultasPendentes();

        lblTotalMedicos.setText(String.valueOf(totalMedicos));
        lblTotalPacientes.setText(String.valueOf(totalPacientes));
        lblConsultasHoje.setText(String.valueOf(consultasHoje));
        lblConsultasPendentes.setText(String.valueOf(consultasPendentes));
    }

    // ─────────────────────────────────────────────────────────
    // Stubs de serviço (substitua pela integração real)
    // ─────────────────────────────────────────────────────────

    private int buscarTotalMedicos() {
        // Exemplo: return medicoService.contarTodos();
        return 1;
    }

    private int buscarTotalPacientes() {
        // Exemplo: return pacienteService.contarTodos();
        return 2;
    }

    private int buscarConsultasHoje() {
        // Exemplo: return consultaService.contarHoje(LocalDate.now());
        return 0;
    }

    private int buscarConsultasPendentes() {
        // Exemplo: return consultaService.contarPendentes();
        return 0;
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
