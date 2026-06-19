package br.edu.ufersa.hospital_manager.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import br.edu.ufersa.hospital_manager.model.entities.*;
import br.edu.ufersa.hospital_manager.model.services.*;


public class MedicoCadastrarProntuarioController {

    @FXML
    private Label lblIniciais;

    @FXML
    private Label lblNomeMedico;

    @FXML
    private Label lblCrmMedico;

    @FXML
    private ComboBox<Patient> cmbPaciente;

    @FXML
    private DatePicker dateConsulta;

    @FXML
    private TextArea txtObservacoes;

    @FXML
    private Label lblContador;

    private final List<Patient> pacientesMock = new ArrayList<>();
    private final MedicalRecordService medicalRecordService = new MedicalRecordService();
    private Doctor medicoLogado;

    @FXML
    public void initialize() {
        configurarDadosMedico();
        carregarDadosMock();
        configurarComboBox();
        configurarContadorCaracteres();
        dateConsulta.setValue(LocalDate.now());
    }

    private void configurarDadosMedico() {
        // TODO: carregar dados do médico logado via DoctorService
        lblIniciais.setText("J");
        lblNomeMedico.setText("Dr. João Lourenço");
        lblCrmMedico.setText("CRM-12345");
        
        // Cria médico mock para uso no prontuário
        Address enderecoMedico = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        medicoLogado = new Doctor("João Lourenço", "12345678900", enderecoMedico, 250.0f, "123456");
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
        cmbPaciente.setPromptText("Selecione um paciente");
    }

    private void configurarContadorCaracteres() {
        txtObservacoes.textProperty().addListener((obs, oldVal, newVal) ->
                lblContador.setText(newVal.length() + " caracteres")
        );
    }

    @FXML
    public void onSalvarProntuario(ActionEvent event) {
        Patient paciente = cmbPaciente.getValue();
        String observacoes = txtObservacoes.getText();

        if (paciente == null) {
            NavigationHelper.showError("Selecione um paciente antes de salvar.");
            return;
        }
        if (observacoes == null || observacoes.trim().isEmpty()) {
            NavigationHelper.showError("As observações médicas são obrigatórias.");
            return;
        }

        try {
            // Verifica se paciente já tem prontuário
            try {
                MedicalRecord existing = medicalRecordService.findByPatient(paciente);
                if (existing != null) {
                    NavigationHelper.showError("Este paciente já possui um prontuário. Edite o prontuário existente.");
                    return;
                }
            } catch (Exception e) {
                // Paciente não tem prontuário, pode criar
            }

            // Cria novo prontuário
            MedicalRecord record = new MedicalRecord(observacoes, medicoLogado, paciente);
            medicalRecordService.registerMedicalRecord(record);

            NavigationHelper.showInfo("Prontuário Salvo", "Prontuário de \"" + paciente.getName() + "\" registrado com sucesso.");
            limparFormulario();
        } catch (Exception e) {
            NavigationHelper.showError("Erro ao salvar prontuário: " + e.getMessage());
        }
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        limparFormulario();
    }

    private void limparFormulario() {
        cmbPaciente.setValue(null);
        dateConsulta.setValue(LocalDate.now());
        txtObservacoes.clear();
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