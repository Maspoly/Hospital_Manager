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
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.PatientServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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

    // ── Componentes de busca ──────────────────────────────────
    @FXML private ComboBox<String> cmbTipoBusca;
    @FXML private TextField txtBusca;
    @FXML private ListView<Object> lstResultados;

    // ── Labels do usuário logado ──────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblVisualizarPerfil;

    // ── Botões de navegação ───────────────────────────────────
    @FXML private Button btnDashboard;
    @FXML private Button btnMedicos;
    @FXML private Button btnPacientes;
    @FXML private Button btnGerentes;
    @FXML private Button btnConsultas;
    @FXML private Button btnBusca;
    @FXML private Button btnRelatorios;

    private final ConsultationServiceProxy consultationService = new ConsultationServiceProxy();
    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();
    private final PatientServiceProxy patientService = new PatientServiceProxy();
    
    private final ObservableList<Consultation> consultas = FXCollections.observableArrayList();
    private final ObservableList<Object> resultados = FXCollections.observableArrayList();
    private Object itemSelecionado;
    private boolean usingDatabase = true;

    @FXML
    public void initialize() {
        carregarDadosUsuario();
        configurarLinkPerfil();
        configurarColunas();
        configurarCombos();
        configurarListaResultados();
        carregarDados();
    }

    /**
     * Preenche os dados do usuário logado na sidebar.
     */
    private void carregarDadosUsuario() {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        String nomeUsuario = usuario != null ? usuario.getName() : "Administrador";
        String cargoUsuario = role != null ? role.getDisplayName() : "Gerente";

        if (lblUserName != null) {
            lblUserName.setText(nomeUsuario);
        }
        if (lblUserRole != null) {
            lblUserRole.setText(cargoUsuario);
        }
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

    private void configurarCombos() {
        cmbTipoBusca.setItems(FXCollections.observableArrayList(
                "Todos",
                "Médicos",
                "Pacientes"
        ));
        cmbTipoBusca.setValue("Todos");
        cmbTipoBusca.setPromptText("Selecione o tipo");
    }

    private void configurarListaResultados() {
        lstResultados.setItems(resultados);
        lstResultados.setPlaceholder(new Label("Nenhum resultado encontrado."));
        lstResultados.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                if (item instanceof Doctor) {
                    Doctor doctor = (Doctor) item;
                    setText("👨‍⚕️ " + doctor.getName() + " - CRM " + doctor.getCouncilCode());
                } else if (item instanceof Patient) {
                    Patient patient = (Patient) item;
                    setText("👤 " + patient.getName() + " - CPF " + formatarCpf(patient.getCPF()));
                } else {
                    setText(item.toString());
                }
            }
        });

        lstResultados.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            itemSelecionado = newVal;
        });
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

    // ===================== BUSCA DE MÉDICOS E PACIENTES =====================

    @FXML
    private void onBuscarMedicoPaciente(ActionEvent event) {
        String termo = txtBusca.getText() == null ? "" : txtBusca.getText().trim().toLowerCase();
        String tipo = cmbTipoBusca.getValue();

        resultados.clear();

        if (tipo == null) {
            return;
        }

        if (tipo.equals("Todos") || tipo.equals("Médicos")) {
            buscarMedicos(termo);
        }

        if (tipo.equals("Todos") || tipo.equals("Pacientes")) {
            buscarPacientes(termo);
        }

        if (resultados.isEmpty()) {
            NavigationHelper.showInfo("Busca", "Nenhum resultado encontrado para \"" + txtBusca.getText() + "\".");
        }
    }

    private void buscarMedicos(String termo) {
        try {
            List<Doctor> medicos = doctorService.listAll();
            for (Doctor doctor : medicos) {
                if (termo.isEmpty() ||
                    doctor.getName().toLowerCase().contains(termo) ||
                    doctor.getCPF().contains(termo) ||
                    doctor.getCouncilCode().contains(termo)) {
                    resultados.add(doctor);
                }
            }
        } catch (SQLException e) {
            // Ignora erro
        }
    }

    private void buscarPacientes(String termo) {
        try {
            List<Patient> pacientes = patientService.listAll();
            for (Patient patient : pacientes) {
                if (termo.isEmpty() ||
                    patient.getName().toLowerCase().contains(termo) ||
                    patient.getCPF().contains(termo)) {
                    resultados.add(patient);
                }
            }
        } catch (SQLException e) {
            // Ignora erro
        }
    }

    @FXML
    private void onVerDetalhes(ActionEvent event) {
        if (itemSelecionado == null) {
            NavigationHelper.showInfo("Aviso", "Selecione um item na lista para ver os detalhes.");
            return;
        }

        if (itemSelecionado instanceof Doctor) {
            Doctor doctor = (Doctor) itemSelecionado;
            Address addr = doctor.getAddress();
            String detalhes = "Médico\n\n" +
                    "Nome: Dr. " + doctor.getName() + "\n" +
                    "CPF: " + formatarCpf(doctor.getCPF()) + "\n" +
                    "CRM: " + doctor.getCouncilCode() + "\n" +
                    "Valor da consulta: R$ " + String.format("%.2f", doctor.getConsultationValue()).replace(".", ",") + "\n" +
                    "Endereço: " + addr.getStreet() + ", " + addr.getNumber() + " - " + addr.getCity() + "/" + addr.getState();
            NavigationHelper.showInfo("Detalhes do Médico", detalhes);
        } else if (itemSelecionado instanceof Patient) {
            Patient patient = (Patient) itemSelecionado;
            Address addr = patient.getAddress();
            String detalhes = "Paciente\n\n" +
                    "Nome: " + patient.getName() + "\n" +
                    "CPF: " + formatarCpf(patient.getCPF()) + "\n" +
                    "Endereço: " + addr.getStreet() + ", " + addr.getNumber() + " - " + addr.getCity() + "/" + addr.getState();
            NavigationHelper.showInfo("Detalhes do Paciente", detalhes);
        }
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf == null ? "" : cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    // ===================== CRUD DE CONSULTAS =====================

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

    // ===================== NAVEGAÇÃO ENTRE TELAS =====================

    @FXML
    public void goDashboard(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "Dashboard.fxml");
    }

    @FXML
    public void goMedicos(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medicos.fxml");
    }

    @FXML
    public void goPacientes(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "pacientes.fxml");
    }

    @FXML
    public void goGerentes(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "gerentes.fxml");
    }

    @FXML
    public void goConsultas(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "consultas.fxml");
    }

    @FXML
    public void goBusca(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "busca.fxml");
    }

    @FXML
    public void goRelatorios(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "relatorios.fxml");
    }

    @FXML
    public void onAgendarConsulta(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "CadastroConsulta.fxml");
    }

    @FXML
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
    }
}