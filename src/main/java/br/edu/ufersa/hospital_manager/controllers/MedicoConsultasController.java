package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import br.edu.ufersa.hospital_manager.util.ProxyFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class MedicoConsultasController {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Label lblIniciais;
    @FXML private Label lblNomeMedico;
    @FXML private Label lblCrmMedico;
    @FXML private Label lblVisualizarPerfil;
    @FXML private Label lblTotalConsultas;
    @FXML private Label lblConsultasEncontradas;
    @FXML private VBox boxConsultas;

    private final ConsultationServiceProxy consultationService = (ConsultationServiceProxy) ProxyFactory.createProxy("CONSULTATION");
    private Doctor medicoLogado;

    @FXML
    public void initialize() {
        configurarDadosMedico();
        configurarLinkPerfil();
        carregarConsultas();
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

    private void carregarConsultas() {
        boxConsultas.getChildren().clear();

        if (medicoLogado == null) {
            mostrarEstadoVazio("Nenhum médico logado foi encontrado.");
            return;
        }

        try {
            ArrayList<Consultation> consultas = consultationService.findByDoctor(medicoLogado);
            lblTotalConsultas.setText(String.valueOf(consultas.size()));
            lblConsultasEncontradas.setText("Minhas consultas (" + consultas.size() + ")");

            if (consultas.isEmpty()) {
                mostrarEstadoVazio("Você ainda não possui consultas cadastradas.");
                return;
            }

            for (Consultation consultation : consultas) {
                boxConsultas.getChildren().add(criarCard(consultation));
            }
        } catch (SQLException exception) {
            mostrarEstadoVazio("Não foi possível carregar suas consultas.");
        }
    }

    private VBox criarCard(Consultation consultation) {
        VBox card = new VBox(6);
        card.getStyleClass().add("medico-consultation-card");
        card.setMaxWidth(Double.MAX_VALUE);

        String pacienteNome = consultation.getPatient() != null ? consultation.getPatient().getName() : "Paciente removido";
        Label paciente = new Label(pacienteNome);
        paciente.getStyleClass().add("medico-consultation-title");

        Label data = new Label("Data: " + consultation.getDateTime().format(FORMATO_DATA_HORA));
        data.getStyleClass().add("medico-consultation-detail");

        Label status = new Label("Status: " + traduzirStatus(consultation.getStatus()));
        status.getStyleClass().add("medico-consultation-detail");

        card.getChildren().addAll(paciente, data, status);
        return card;
    }

    private void mostrarEstadoVazio(String mensagem) {
        boxConsultas.getChildren().clear();
        boxConsultas.setAlignment(javafx.geometry.Pos.CENTER);
        lblConsultasEncontradas.setText("Minhas consultas");
        lblTotalConsultas.setText("0");

        Label texto = new Label(mensagem);
        texto.getStyleClass().add("medico-empty-state-title");
        boxConsultas.getChildren().add(texto);
    }

    private String traduzirStatus(String status) {
        switch (status) {
            case "SCHEDULED": return "Agendada";
            case "COMPLETED": return "Concluída";
            case "CANCELED": return "Cancelada";
            default: return status;
        }
    }

    @FXML
    public void goMeusPacientes(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_pacientes.fxml", "medico.css");
    }

    @FXML
    public void goMinhasConsultas(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_consultas.fxml", "medico.css");
    }

    @FXML
    public void goCadastrarProntuario(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_cadastrar_prontuario.fxml", "medico.css");
    }

    @FXML
    public void goRelatorios(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_relatorios.fxml", "medico.css");
    }

    @FXML
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
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