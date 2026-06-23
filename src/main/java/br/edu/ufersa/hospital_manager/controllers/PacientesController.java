package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.MedicalRecordServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.PatientServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import br.edu.ufersa.hospital_manager.util.ProxyFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class PacientesController {

    private final PatientServiceProxy patientService = (PatientServiceProxy) ProxyFactory.createProxy("PATIENT");
    private final MedicalRecordServiceProxy medicalRecordService = (MedicalRecordServiceProxy) ProxyFactory.createProxy("MEDICAL_RECORD");
    private final ObservableList<Patient> pacientes = FXCollections.observableArrayList();
    private boolean usingDatabase = true;

    @FXML
    private TableView<Patient> tablePacientes;

    @FXML
    private TableColumn<Patient, String> colNome;

    @FXML
    private TableColumn<Patient, String> colCpf;

    @FXML
    private TableColumn<Patient, String> colEndereco;

    @FXML
    private TableColumn<Patient, Void> colProntuarios;

    @FXML
    private TableColumn<Patient, Void> colAcoes;

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

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
        carregarDadosUsuario();
        configurarLinkPerfil();
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

    /**
     * Navega para a tela de perfil do usuário logado
     */
    @FXML
    private void onVisualizarPerfil(MouseEvent event) {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        if (usuario == null || role == null) {
            NavigationHelper.showError("Usuário não encontrado.");
            return;
        }

        // Usa o próprio label como referência para navegação
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

    private void configurarColunas() {

        colNome.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName())
        );

        colCpf.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(formatarCpf(data.getValue().getCPF()))
        );

        colEndereco.setCellValueFactory(data -> {
            Address addr = data.getValue().getAddress();
            String texto = addr.getStreet() + ", " + addr.getNumber()
                    + " - " + addr.getCity() + "/" + addr.getState();
            return new javafx.beans.property.SimpleStringProperty(texto);
        });

        // Coluna de prontuários: link clicável "N prontuários"
        colProntuarios.setCellFactory(col -> new TableCell<Patient, Void>() {
            private final Label link = new Label();

            {
                link.getStyleClass().add("cell-link");
                link.setOnMouseClicked(e -> onVerProntuarios(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                Patient patient = getTableRow() != null ? getTableRow().getItem() : null;
                if (empty || patient == null) {
                    setGraphic(null);
                    return;
                }
                
                try {
                    MedicalRecord record = medicalRecordService.findByPatient(patient);
                    int quantidade = record != null ? 1 : 0;
                    link.setText(quantidade + " prontuário" + (quantidade == 1 ? "" : "s"));
                } catch (Exception e) {
                    link.setText("0 prontuários");
                }
                setGraphic(link);
            }
        });

        colAcoes.setCellFactory(col -> new TableCell<Patient, Void>() {

            private final Button btnEditar = new Button("Editar");
            private final Button btnExcluir = new Button("Excluir");
            private final HBox box = new HBox(10, btnEditar, btnExcluir);

            {
                btnEditar.getStyleClass().add("cell-link");
                btnExcluir.getStyleClass().add("btn-danger-ghost");

                btnEditar.setOnAction(e -> onEditarPaciente(getTableRow().getItem()));
                btnExcluir.setOnAction(e -> onExcluirPaciente(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void carregarDados() {
        try {
            pacientes.setAll(patientService.listAll());
            usingDatabase = true;
        } catch (SQLException exception) {
            usingDatabase = false;
            pacientes.setAll(criarPacientesMock());
        }

        tablePacientes.setItems(pacientes);
    }

    private List<Patient> criarPacientesMock() {
        List<Patient> dados = new ArrayList<>();

        Address endereco1 = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
        dados.add(new Patient("Maria Santos", "11122233344", endereco1));

        Address endereco2 = new Address("Av. Central", "200", "Centro", "Mossoró", "RN");
        dados.add(new Patient("João Oliveira", "55566677788", endereco2));

        return dados;
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    private void onVerProntuarios(Patient patient) {
        try {
            MedicalRecord record = medicalRecordService.findByPatient(patient);
            if (record == null) {
                NavigationHelper.showInfo("Prontuários", "Nenhum prontuário encontrado para \"" + patient.getName() + "\".");
                return;
            }

            NavigationHelper.showInfo(
                    "Prontuários",
                    "Paciente: " + patient.getName() + "\n" +
                            "Data: " + record.getDate() + "\n" +
                            "Observação: " + record.getObservation()
            );
        } catch (SQLException exception) {
            NavigationHelper.showError("Erro ao carregar prontuários: " + exception.getMessage());
        }
    }

    private void onEditarPaciente(Patient patient) {
        if (patient == null) return;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Paciente");
        dialog.setHeaderText("Atualize os dados de " + patient.getName());

        ButtonType salvar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvar, ButtonType.CANCEL);

        // Campos do formulário
        TextField nomeField = new TextField(patient.getName());
        nomeField.setPromptText("Nome completo");

        TextField cpfField = new TextField(patient.getCPF());
        cpfField.setPromptText("CPF");
        cpfField.setEditable(false); // CPF não pode ser alterado

        // Layout do GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        GridPane.setHgrow(nomeField, Priority.ALWAYS);

        grid.add(new Label("CPF:"), 0, 1);
        grid.add(cpfField, 1, 1);
        GridPane.setHgrow(cpfField, Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);

        // Estilizar botões
        Button btnOk = (Button) dialog.getDialogPane().lookupButton(salvar);
        btnOk.getStyleClass().add("btn-accent");
        
        Button btnCancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        btnCancel.getStyleClass().add("btn-ghost");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != salvar) {
            return;
        }

        try {
            String novoNome = nomeField.getText().trim();
            if (novoNome.isEmpty()) {
                NavigationHelper.showError("O nome não pode estar vazio.");
                return;
            }

            patient.setName(novoNome);

            if (usingDatabase) {
                patientService.updatePatient(patient);
            }

            tablePacientes.refresh();
            NavigationHelper.showInfo("Editar Paciente", "Dados de \"" + patient.getName() + "\" atualizados com sucesso.");
        } catch (RuntimeException | SQLException exception) {
            NavigationHelper.showError(exception.getMessage());
        }
    }

    private void onExcluirPaciente(Patient patient) {
        boolean confirmado = NavigationHelper.confirm(
                "Excluir Paciente",
                "Tem certeza que deseja excluir \"" + patient.getName() + "\"?"
        );
        if (confirmado) {
            try {
                if (usingDatabase) {
                    patientService.removePatient(patient);
                    carregarDados();
                } else {
                    tablePacientes.getItems().remove(patient);
                }
            } catch (SQLException exception) {
                NavigationHelper.showError("Erro ao excluir paciente: " + exception.getMessage());
            }
        }
    }

    @FXML
    public void onNovoPaciente(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "CadastroPaciente.fxml");
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
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
    }
}