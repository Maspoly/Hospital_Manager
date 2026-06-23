package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.MedicalRecordServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.PatientServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import br.edu.ufersa.hospital_manager.util.ProxyFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class PacienteProntuariosController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private Label lblIniciais;

    @FXML
    private Label lblNomePaciente;

    @FXML
    private Label lblCpfPaciente;

    @FXML private Label lblVisualizarPerfil;
    @FXML
    private VBox boxProntuarios;

    private Patient pacienteLogado;

    private final PatientServiceProxy patientService = (PatientServiceProxy) ProxyFactory.createProxy("PATIENT");
    private final MedicalRecordServiceProxy medicalRecordService = (MedicalRecordServiceProxy) ProxyFactory.createProxy("MEDICAL_RECORD");

    @FXML
    public void initialize() {
        carregarPacienteLogado();
        carregarProntuarios();
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

    private void carregarProntuarios() {
        boxProntuarios.getChildren().clear();
        boxProntuarios.setAlignment(javafx.geometry.Pos.CENTER);

        if (pacienteLogado == null) {
            mostrarEstadoVazio("Nenhum paciente logado.");
            return;
        }

        try {
            MedicalRecord record = medicalRecordService.findByPatient(pacienteLogado);

            if (record == null) {
                mostrarEstadoVazio("Nenhum prontuário encontrado para " + pacienteLogado.getName() + ".");
                return;
            }

            boxProntuarios.setAlignment(javafx.geometry.Pos.TOP_LEFT);

            VBox item = new VBox(4);
            item.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 8; -fx-padding: 16 18 16 18; -fx-border-color: #e5e7eb; -fx-border-radius: 8; -fx-border-width: 1;");

            String dataFormatada = record.getDate() != null ? record.getDate().format(FORMATO_DATA) : "Data não informada";
            Label data = new Label("Data: " + dataFormatada);
            data.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 12px;");

            String nomeMedico = record.getDoctor() != null ? "Dr. " + record.getDoctor().getName() : "Médico não informado";
            Label medico = new Label("Médico: " + nomeMedico);
            medico.setStyle("-fx-text-fill: #1f2937; -fx-font-size: 13.5px; -fx-font-weight: 600;");

            Label observacao = new Label("Observação: " + record.getObservation());
            observacao.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 13px;");
            observacao.setWrapText(true);

            item.getChildren().addAll(data, medico, observacao);
            boxProntuarios.getChildren().add(item);

        } catch (Exception e) {
            mostrarEstadoVazio("Erro ao carregar prontuário: " + e.getMessage());
        }
    }

    private void mostrarEstadoVazio(String mensagem) {
        boxProntuarios.getChildren().clear();
        boxProntuarios.setAlignment(javafx.geometry.Pos.CENTER);
        boxProntuarios.setPadding(new Insets(40, 20, 40, 20));

        Label texto = new Label(mensagem);
        texto.getStyleClass().add("paciente-empty-state-title");
        boxProntuarios.getChildren().add(texto);
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
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
    }
}