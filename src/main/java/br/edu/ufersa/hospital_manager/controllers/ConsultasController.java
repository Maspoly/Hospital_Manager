package br.edu.ufersa.hospital_manager.controllers;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class ConsultasController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private TableView<Consultation> tableConsultas;

    @FXML
    private TableColumn<Consultation, String> colDataHora;

    @FXML
    private TableColumn<Consultation, String> colPaciente;

    @FXML
    private TableColumn<Consultation, String> colMedico;

    @FXML
    private TableColumn<Consultation, String> colStatus;

    @FXML
    private TableColumn<Consultation, Void> colAcoes;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDadosMock();
    }

    private void configurarColunas() {

        colDataHora.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDateTime().format(FORMATO_DATA))
        );

        colPaciente.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPatient().getName())
        );

        colMedico.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty("Dr. " + data.getValue().getDoctor().getName())
        );

        colStatus.setCellFactory(col -> new TableCell<Consultation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                Consultation consulta = getTableRow() != null ? getTableRow().getItem() : null;
                if (empty || consulta == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(traduzirStatus(consulta.getStatus()));
                badge.getStyleClass().addAll("badge", estiloBadge(consulta.getStatus()));
                setGraphic(new StackPane(badge));
            }
        });

        colAcoes.setCellFactory(col -> new TableCell<Consultation, Void>() {

            private final Button btnEditar = new Button("Editar");
            private final Button btnCancelar = new Button("Cancelar");
            private final HBox box = new HBox(10, btnEditar, btnCancelar);

            {
                btnEditar.getStyleClass().add("cell-link");
                btnCancelar.getStyleClass().add("btn-danger-ghost");

                btnEditar.setOnAction(e -> onEditarConsulta(getTableRow().getItem()));
                btnCancelar.setOnAction(e -> onCancelarConsulta(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private String traduzirStatus(String status) {
        switch (status) {
            case "SCHEDULED": return "Agendada";
            case "COMPLETED": return "Concluída";
            case "CANCELED": return "Cancelada";
            default: return status;
        }
    }

    private String estiloBadge(String status) {
        switch (status) {
            case "SCHEDULED": return "badge-scheduled";
            case "COMPLETED": return "badge-completed";
            case "CANCELED": return "badge-canceled";
            default: return "badge-scheduled";
        }
    }

    private void carregarDadosMock() {
        ObservableList<Consultation> dados = FXCollections.observableArrayList();

        Address enderecoMedico = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        Doctor doctor = new Doctor("Luiz Silva", "12345678900", enderecoMedico, 250.0f, "123456");

        Address enderecoPaciente = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
        Patient patient = new Patient("Maria Santos", "11122233344", enderecoPaciente);

        Consultation consulta1 = new Consultation(patient, doctor, java.time.LocalDateTime.now().plusDays(2), "SCHEDULED");

        dados.add(consulta1);

        tableConsultas.setItems(dados);
    }

    private void onEditarConsulta(Consultation consulta) {
        // TODO: abrir formulário de edição conectado ao ConsultationDAO/ConsultationServices
        NavigationHelper.showInfo("Editar Consulta", "Edição da consulta em construção.");
    }

    private void onCancelarConsulta(Consultation consulta) {
        boolean confirmado = NavigationHelper.confirm(
                "Cancelar Consulta",
                "Tem certeza que deseja cancelar essa consulta?"
        );
        if (confirmado) {
            // TODO: atualizar status via ConsultationDAO/ConsultationServices
            tableConsultas.getItems().remove(consulta);
        }
    }

    @FXML
    public void onAgendarConsulta(ActionEvent event) {
        // TODO: abrir formulário de agendamento conectado ao ConsultationDAO/ConsultationServices
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "CadastroConsulta.fxml");
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
