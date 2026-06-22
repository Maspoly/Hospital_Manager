package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.PatientServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;

public class PacienteConsultasController {

    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private Label lblIniciais;

    @FXML
    private Label lblNomePaciente;

    @FXML
    private Label lblCpfPaciente;

    @FXML
    private TableView<Consultation> tableConsultas;

    @FXML
    private TableColumn<Consultation, String> colDataHora;

    @FXML
    private TableColumn<Consultation, String> colMedico;

    @FXML
    private TableColumn<Consultation, String> colStatus;

    @FXML
    private TableColumn<Consultation, Void> colAcoes;

    private Patient pacienteLogado;

    private final PatientServiceProxy patientService = new PatientServiceProxy();
    private final ConsultationServiceProxy consultationService = new ConsultationServiceProxy();

    private final ObservableList<Consultation> consultasObservable = FXCollections.observableArrayList();
    private boolean usingDatabase = true;

    @FXML
    public void initialize() {
        carregarPacienteLogado();
        configurarColunas();
        carregarConsultas();
    }

    /**
     * Carrega o paciente logado a partir do ServiceRoleContext
     */
    private void carregarPacienteLogado() {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        if (usuario instanceof Patient && role == ServiceRole.PATIENT) {
            pacienteLogado = (Patient) usuario;
            atualizarDadosPaciente();
        } else {
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

    private void configurarColunas() {
        colDataHora.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDateTime().format(FORMATO_DATA_HORA)
                )
        );

        colMedico.setCellValueFactory(data -> {
            String nomeMedico = data.getValue().getDoctor() != null 
                    ? "Dr. " + data.getValue().getDoctor().getName() 
                    : "Médico removido";
            return new javafx.beans.property.SimpleStringProperty(nomeMedico);
        });

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
                badge.getStyleClass().addAll("paciente-badge", estiloBadge(consulta.getStatus()));
                setGraphic(new StackPane(badge));
            }
        });

        colAcoes.setCellFactory(col -> new TableCell<Consultation, Void>() {

            private final Button btnCancelar = new Button("Cancelar");
            private final HBox box = new HBox(10, btnCancelar);

            {
                btnCancelar.getStyleClass().add("btn-danger-ghost");
                btnCancelar.setOnAction(e -> onCancelarConsulta(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                Consultation consulta = getTableRow() != null ? getTableRow().getItem() : null;
                if (empty || consulta == null) {
                    setGraphic(null);
                    return;
                }
                // Só mostra botão de cancelar se estiver agendada
                if ("SCHEDULED".equals(consulta.getStatus())) {
                    setGraphic(box);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    private void carregarConsultas() {
        if (pacienteLogado == null) {
            tableConsultas.setItems(FXCollections.observableArrayList());
            return;
        }

        try {
            consultasObservable.setAll(consultationService.findByPatient(pacienteLogado));
            usingDatabase = true;
        } catch (Exception e) {
            usingDatabase = false;
            consultasObservable.clear();
        }

        tableConsultas.setItems(consultasObservable);
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
            case "SCHEDULED": return "paciente-badge-scheduled";
            case "COMPLETED": return "paciente-badge-completed";
            case "CANCELED": return "paciente-badge-canceled";
            default: return "paciente-badge-scheduled";
        }
    }

    private void onCancelarConsulta(Consultation consulta) {
        if (consulta == null) return;

        String nomeMedico = consulta.getDoctor() != null 
                ? "Dr. " + consulta.getDoctor().getName() 
                : "médico não informado";

        boolean confirmado = NavigationHelper.confirm(
                "Cancelar Consulta",
                "Tem certeza que deseja cancelar a consulta com " + nomeMedico + "?"
        );

        if (confirmado) {
            try {
                if (usingDatabase) {
                    consultationService.cancelConsultation(consulta);
                    carregarConsultas();
                } else {
                    consulta.setStatus("CANCELED");
                    tableConsultas.refresh();
                }
                NavigationHelper.showInfo("Sucesso", "Consulta cancelada com sucesso!");
            } catch (Exception e) {
                NavigationHelper.showError("Erro ao cancelar consulta: " + e.getMessage());
            }
        }
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
    public void onAgendarConsulta(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_agendar_consulta.fxml", "paciente.css");
    }

    @FXML
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
    }
}