package br.edu.ufersa.hospital_manager.controllers;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class BuscaController {

    @FXML
    private ComboBox<String> cmbBuscarPor;

    @FXML
    private ComboBox<String> cmbCriterio;

    @FXML
    private TextField txtTermo;

    @FXML
    private Label lblResultados;

    @FXML
    private VBox boxResultados;

    // Dados mock para simular a busca (futuramente vêm de PatientServices/DoctorServices)
    private final List<Patient> pacientesMock = new ArrayList<>();
    private final List<Doctor> medicosMock = new ArrayList<>();

    @FXML
    public void initialize() {
        cmbBuscarPor.getItems().addAll("Paciente", "Médico", "Consulta");
        cmbBuscarPor.setValue("Paciente");

        cmbCriterio.getItems().addAll("Nome", "CPF");
        cmbCriterio.setValue("Nome");

        cmbBuscarPor.valueProperty().addListener((obs, oldVal, newVal) -> atualizarCriterios(newVal));

        carregarDadosMock();
    }

    private void atualizarCriterios(String buscarPor) {
        cmbCriterio.getItems().clear();
        if ("Consulta".equals(buscarPor)) {
            cmbCriterio.getItems().addAll("Data", "Status");
        } else {
            cmbCriterio.getItems().addAll("Nome", "CPF");
        }
        cmbCriterio.setValue(cmbCriterio.getItems().get(0));
    }

    private void carregarDadosMock() {
        Address endereco1 = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
        pacientesMock.add(new Patient("Maria Santos", "11122233344", endereco1));

        Address endereco2 = new Address("Av. Central", "200", "Centro", "Mossoró", "RN");
        pacientesMock.add(new Patient("João Oliveira", "55566677788", endereco2));

        Address enderecoMedico = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        medicosMock.add(new Doctor("Luiz Silva", "12345678900", enderecoMedico, 250.0f, "123456"));
    }

    @FXML
    public void onBuscar(ActionEvent event) {
        String termo = txtTermo.getText() == null ? "" : txtTermo.getText().trim().toLowerCase();
        String buscarPor = cmbBuscarPor.getValue();

        boxResultados.getChildren().clear();
        int totalEncontrados = 0;

        if ("Paciente".equals(buscarPor)) {
            for (Patient p : pacientesMock) {
                if (termo.isEmpty() || p.getName().toLowerCase().contains(termo) || p.getCPF().contains(termo)) {
                    boxResultados.getChildren().add(criarLinhaResultado(p.getName(), "Paciente · CPF " + p.getCPF()));
                    totalEncontrados++;
                }
            }
        } else if ("Médico".equals(buscarPor)) {
            for (Doctor d : medicosMock) {
                if (termo.isEmpty() || d.getName().toLowerCase().contains(termo) || d.getCPF().contains(termo)) {
                    boxResultados.getChildren().add(criarLinhaResultado("Dr. " + d.getName(), "CRM " + d.getCouncilCode()));
                    totalEncontrados++;
                }
            }
        } else {
            // TODO: busca de consultas conectada ao ConsultationDAO/ConsultationServices
        }

        lblResultados.setText("Resultados da Busca (" + totalEncontrados + ")");

        if (totalEncontrados == 0) {
            Label vazio = new Label("Nenhum resultado encontrado para os filtros selecionados.");
            vazio.getStyleClass().add("empty-state-title");
            boxResultados.getChildren().add(vazio);
        }
    }

    private VBox criarLinhaResultado(String titulo, String subtitulo) {
        Label tituloLbl = new Label(titulo);
        tituloLbl.getStyleClass().add("cell-title");
        Label subtituloLbl = new Label(subtitulo);
        subtituloLbl.getStyleClass().add("cell-subtitle");
        return new VBox(2, tituloLbl, subtituloLbl);
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
