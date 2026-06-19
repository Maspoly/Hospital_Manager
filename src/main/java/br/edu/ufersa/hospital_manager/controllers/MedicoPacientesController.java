package br.edu.ufersa.hospital_manager.controllers;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.services.MedicalRecordService;

public class MedicoPacientesController {

    @FXML
    private Label lblIniciais;

    @FXML
    private Label lblNomeMedico;

    @FXML
    private Label lblCrmMedico;

    @FXML
    private Label lblTotalPacientes;

    @FXML
    private Label lblTotalProntuarios;

    @FXML
    private Label lblProntuariosSelecionados;

    @FXML
    private ComboBox<Patient> cmbPaciente;

    @FXML
    private VBox boxProntuarios;

    // Dados mock
    private final List<Patient> pacientesMock = new ArrayList<>();
    private final MedicalRecordService medicalRecordService = new MedicalRecordService();

    @FXML
    public void initialize() {
        configurarDadosMedico();
        carregarDadosMock();
        configurarComboBox();
        atualizarEstatisticas();
        mostrarEstadoVazio();
    }

    private void configurarDadosMedico() {
        lblIniciais.setText("J");
        lblNomeMedico.setText("Dr. João Lourenço");
        lblCrmMedico.setText("CRM-12345");
    }

    private void carregarDadosMock() {
        Address endereco1 = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
        pacientesMock.add(new Patient("Maria Santos", "11122233344", endereco1));

        Address endereco2 = new Address("Av. Central", "200", "Centro", "Mossoró", "RN");
        pacientesMock.add(new Patient("João Oliveira", "55566677788", endereco2));
    }

    private void configurarComboBox() {
        cmbPaciente.getItems().addAll(pacientesMock);
        cmbPaciente.setConverter(new javafx.util.StringConverter<Patient>() {
            @Override
            public String toString(Patient patient) {
                return patient == null ? "" : patient.getName();
            }

            @Override
            public Patient fromString(String string) {
                return null;
            }
        });
        cmbPaciente.setPromptText("Selecione um paciente para ver seus prontuários");
        cmbPaciente.valueProperty().addListener((obs, oldVal, newVal) -> onPacienteSelecionado(newVal));
    }

    private void atualizarEstatisticas() {
        lblTotalPacientes.setText(String.valueOf(pacientesMock.size()));

        int totalProntuarios = 0;
        for (Patient p : pacientesMock) {
            try {
                MedicalRecord record = medicalRecordService.findByPatient(p);
                if (record != null) {
                    totalProntuarios++;
                }
            } catch (Exception e) {
                // Paciente sem prontuário
            }
        }
        lblTotalProntuarios.setText(String.valueOf(totalProntuarios));
        lblProntuariosSelecionados.setText("0");
    }

    private void onPacienteSelecionado(Patient patient) {
        boxProntuarios.getChildren().clear();

        if (patient == null) {
            mostrarEstadoVazio();
            lblProntuariosSelecionados.setText("0");
            return;
        }

        try {
            MedicalRecord record = medicalRecordService.findByPatient(patient);
            
            if (record == null) {
                Label vazio = new Label("Nenhum prontuário cadastrado para " + patient.getName() + ".");
                vazio.getStyleClass().add("medico-empty-state-title");
                boxProntuarios.getChildren().add(vazio);
                boxProntuarios.setAlignment(javafx.geometry.Pos.CENTER);
                lblProntuariosSelecionados.setText("0");
            } else {
                Label titulo = new Label("Prontuário de " + patient.getName());
                titulo.getStyleClass().add("medico-panel-title");
                
                Label data = new Label("Data: " + record.getDate().toString());
                data.getStyleClass().add("medico-patient-detail");
                
                Label observacao = new Label("Observação: " + record.getObservation());
                observacao.getStyleClass().add("medico-patient-detail");
                observacao.setWrapText(true);
                
                boxProntuarios.getChildren().addAll(titulo, data, observacao);
                boxProntuarios.setAlignment(javafx.geometry.Pos.TOP_LEFT);
                lblProntuariosSelecionados.setText("1");
            }
        } catch (Exception e) {
            Label erro = new Label("Erro ao carregar prontuário: " + e.getMessage());
            erro.getStyleClass().add("medico-empty-state-title");
            boxProntuarios.getChildren().add(erro);
            boxProntuarios.setAlignment(javafx.geometry.Pos.CENTER);
            lblProntuariosSelecionados.setText("0");
        }
    }

    private void mostrarEstadoVazio() {
        boxProntuarios.getChildren().clear();
        boxProntuarios.setAlignment(javafx.geometry.Pos.CENTER);
        boxProntuarios.setPadding(new Insets(40, 20, 40, 20));

        StackPane icone = new StackPane();
        Circle circulo = new Circle(26);
        circulo.getStyleClass().add("medico-empty-state-icon-circle");
        Label glyph = new Label("👤");
        glyph.setStyle("-fx-font-size: 20px; -fx-text-fill: #c1c5cc;");
        icone.getChildren().addAll(circulo, glyph);

        Label texto = new Label("Selecione um paciente acima para visualizar seus prontuários");
        texto.getStyleClass().add("medico-empty-state-title");

        boxProntuarios.getChildren().addAll(icone, texto);
    }

    // ===================== NAVEGAÇÃO ENTRE TELAS =====================

    @FXML
    public void goMeusPacientes(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_pacientes.fxml", "medico.css");
    }

    @FXML
    public void goCadastrarProntuario(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_cadastrar_prontuario.fxml", "medico.css");
    }

    @FXML
    public void goEditarDados(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_editar_pacientes.fxml", "medico.css");
    }

    @FXML
    public void goRelatorios(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_relatorios.fxml", "medico.css");
    }

    @FXML
    public void onSair(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "login_medico.fxml", "medico.css");
    }
}