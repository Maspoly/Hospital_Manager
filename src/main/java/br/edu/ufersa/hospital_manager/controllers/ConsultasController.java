package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

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

    private final ConsultationServiceProxy consultationService = new ConsultationServiceProxy();
    private final ObservableList<Consultation> consultas = FXCollections.observableArrayList();
    private boolean usingDatabase = true;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
    }

    private void configurarColunas() {

        colDataHora.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDateTime().format(FORMATO_DATA))
        );

        colPaciente.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getPatient() != null ? data.getValue().getPatient().getName() : "Paciente removido")
        );

        colMedico.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(data.getValue().getDoctor() != null ? "Dr. " + data.getValue().getDoctor().getName() : "Médico removido")
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

    private void carregarDados() {
        try {
            consultas.setAll(consultationService.listAll());
            usingDatabase = true;
        } catch (SQLException exception) {
            usingDatabase = false;
            consultas.setAll(carregarDadosMock());
        }

        tableConsultas.setItems(consultas);
    }

    private List<Consultation> carregarDadosMock() {
        List<Consultation> dados = new ArrayList<>();

        Address enderecoMedico = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        Doctor doctor = new Doctor("Luiz Silva", "12345678900", enderecoMedico, 250.0f, "123456");

        Address enderecoPaciente = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
        Patient patient = new Patient("Maria Santos", "11122233344", enderecoPaciente);

        dados.add(new Consultation(patient, doctor, java.time.LocalDateTime.now().plusDays(2), "SCHEDULED"));
        return dados;
    }

    private void onEditarConsulta(Consultation consulta) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Consulta");
        dialog.setHeaderText("Atualize data, hora e status da consulta");

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ((Button) pane.lookupButton(ButtonType.OK)).setText("Salvar consulta");

        DatePicker data = new DatePicker(consulta.getDateTime().toLocalDate());
        TextField hora = new TextField(consulta.getDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        ComboBox<String> status = new ComboBox<>(FXCollections.observableArrayList("SCHEDULED", "COMPLETED", "CANCELED"));
        status.setValue(consulta.getStatus());

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16, 24, 8, 24));
        grid.addRow(0, new Label("Data:"), data);
        grid.addRow(1, new Label("Hora:"), hora);
        grid.addRow(2, new Label("Status:"), status);
        pane.setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            try {
                LocalTime parsedTime = LocalTime.parse(hora.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
                consulta.setDateTime(LocalDateTime.of(data.getValue(), parsedTime));
                consulta.setStatus(status.getValue());

                if (usingDatabase) {
                    consultationService.updateConsultation(consulta);
                }

                tableConsultas.refresh();
                NavigationHelper.showInfo("Editar Consulta", "Consulta atualizada com sucesso.");
            } catch (RuntimeException | SQLException exception) {
                NavigationHelper.showError(exception.getMessage());
            }
        });
    }

    private void onCancelarConsulta(Consultation consulta) {
        boolean confirmado = NavigationHelper.confirm(
                "Cancelar Consulta",
                "Tem certeza que deseja cancelar essa consulta?"
        );
        if (confirmado) {
            try {
                if (usingDatabase) {
                    consultationService.cancelConsultation(consulta);
                    carregarDados();
                } else {
                    consulta.setStatus("CANCELED");
                    tableConsultas.refresh();
                }
            } catch (SQLException exception) {
                NavigationHelper.showError("Erro ao cancelar consulta: " + exception.getMessage());
            }
        }
    }

    @FXML
    public void onAgendarConsulta(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "CadastroConsulta.fxml");
    }

    // ===================== NAVEGAÇÃO ENTRE TELAS =====================

    @FXML
    public void goDashboard(ActionEvent event) {
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "Dashboard.fxml");
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
