package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MedicosController {

    // ── Botões de navegação ───────────────────────────────────
    @FXML private Button btnDashboard;
    @FXML private Button btnMedicos;
    @FXML private Button btnPacientes;
    @FXML private Button btnGerentes;
    @FXML private Button btnConsultas;
    @FXML private Button btnBusca;
    @FXML private Button btnRelatorios;
    @FXML private Button btnNovoMedico;

    // ── Labels do usuário logado ──────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblVisualizarPerfil;

    @FXML
    private TableView<Doctor> tableMedicos;

    @FXML
    private TableColumn<Doctor, String> colNome;

    @FXML
    private TableColumn<Doctor, String> colCpf;

    @FXML
    private TableColumn<Doctor, String> colConselho;

    @FXML
    private TableColumn<Doctor, String> colValor;

    @FXML
    private TableColumn<Doctor, Void> colAcoes;

    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();
    private final ObservableList<Doctor> medicos = FXCollections.observableArrayList();
    private boolean usingDatabase = true;

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

        lblUserName.setText(nomeUsuario);
        lblUserRole.setText(cargoUsuario);
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

    // ===================== MÉTODOS INTERNOS =====================

    private void configurarColunas() {
        // Coluna NOME: nome em destaque + endereço como subtítulo
        colNome.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(""));
        colNome.setCellFactory(col -> new TableCell<Doctor, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                Doctor doctor = getTableRow() != null ? getTableRow().getItem() : null;
                if (empty || doctor == null) {
                    setGraphic(null);
                    return;
                }
                Label nome = new Label("Dr. " + doctor.getName());
                nome.getStyleClass().add("cell-title");

                Address addr = doctor.getAddress();
                String enderecoTexto = addr.getStreet() + ", " + addr.getNumber()
                        + " - " + addr.getCity() + "/" + addr.getState();
                Label endereco = new Label(enderecoTexto);
                endereco.getStyleClass().add("cell-subtitle");

                VBox box = new VBox(2, nome, endereco);
                setGraphic(box);
            }
        });

        colCpf.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                formatarCpf(data.getValue().getCPF())
        ));

        colConselho.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                "CRM " + data.getValue().getCouncilCode() + "/" + data.getValue().getAddress().getState()
        ));

        colValor.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                formatarMoeda(data.getValue().getConsultationValue())
        ));

        colAcoes.setCellFactory(criarFabricaDeAcoes());
    }

    private Callback<TableColumn<Doctor, Void>, TableCell<Doctor, Void>> criarFabricaDeAcoes() {
        return col -> new TableCell<Doctor, Void>() {

            private final Button btnEditar = new Button("Editar");
            private final Button btnExcluir = new Button("Excluir");
            private final HBox box = new HBox(10, btnEditar, btnExcluir);

            {
                btnEditar.getStyleClass().add("cell-link");
                btnExcluir.getStyleClass().add("btn-danger-ghost");

                btnEditar.setOnAction(e -> onEditarMedico(getTableRow().getItem()));
                btnExcluir.setOnAction(e -> onExcluirMedico(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        };
    }

    private void carregarDados() {
        try {
            medicos.setAll(doctorService.listAll());
            usingDatabase = true;
        } catch (SQLException exception) {
            usingDatabase = false;
            medicos.setAll(carregarDadosMock());
        }

        tableMedicos.setItems(medicos);
    }

    private List<Doctor> carregarDadosMock() {
        List<Doctor> dados = new ArrayList<>();
        Address endereco1 = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        dados.add(new Doctor("Luiz Silva", "12345678900", endereco1, 250.0f, "123456"));
        return dados;
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    private String formatarMoeda(float valor) {
        return String.format("R$ %.2f", valor).replace(".", ",");
    }

    private void onEditarMedico(Doctor doctor) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Médico");
        dialog.setHeaderText(null);

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ((Button) pane.lookupButton(ButtonType.OK)).setText("Salvar alterações");

        Address address = doctor.getAddress();
        TextField nome = field(doctor.getName());
        TextField cpf = field(doctor.getCPF());
        TextField rua = field(address.getStreet());
        TextField numero = field(address.getNumber());
        TextField bairro = field(address.getNeighborhood());
        TextField cidade = field(address.getCity());
        TextField estado = field(address.getState());
        TextField crm = field(doctor.getCouncilCode());
        TextField valor = field(String.valueOf(doctor.getConsultationValue()));

        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(12);
        personalGrid.setVgap(12);
        personalGrid.addRow(0, label("Nome completo"), nome);
        personalGrid.addRow(1, label("CPF"), cpf);

        GridPane addressGrid = new GridPane();
        addressGrid.setHgap(12);
        addressGrid.setVgap(12);
        addressGrid.addRow(0, label("Rua"), rua, label("Número"), numero);
        addressGrid.addRow(1, label("Bairro"), bairro, label("Cidade"), cidade);
        addressGrid.addRow(2, label("Estado"), estado);

        GridPane professionalGrid = new GridPane();
        professionalGrid.setHgap(12);
        professionalGrid.setVgap(12);
        professionalGrid.addRow(0, label("CRM / Conselho"), crm);
        professionalGrid.addRow(1, label("Valor da consulta (R$)"), valor);

        VBox sections = new VBox(14,
                section("Dados pessoais", personalGrid),
                section("Endereço", addressGrid),
                section("Dados profissionais", professionalGrid)
        );
        sections.setPadding(new Insets(2, 2, 0, 2));

        VBox header = new VBox(4,
                dialogTitle("Editar Médico"),
                dialogSubtitle("Mantenha apenas os dados realmente usados pelo sistema.")
        );

        VBox content = new VBox(18, header, sections, footer(pane));
        content.setPadding(new Insets(24));
        content.setPrefWidth(720);
        content.getStyleClass().add("edit-dialog-card");

        StackPane backdrop = new StackPane(content);
        backdrop.setPadding(new Insets(22));
        backdrop.getStyleClass().add("edit-dialog-backdrop");

        pane.setContent(backdrop);
        pane.getStylesheets().add(getClass().getResource("/br/edu/ufersa/hospital_manager/css/style.css").toExternalForm());
        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            try {
                doctor.setName(nome.getText().trim());
                doctor.setCPF(cpf.getText().trim().replaceAll("[^0-9]", ""));
                doctor.getAddress().setStreet(rua.getText().trim());
                doctor.getAddress().setNumber(numero.getText().trim());
                doctor.getAddress().setNeighborhood(bairro.getText().trim());
                doctor.getAddress().setCity(cidade.getText().trim());
                doctor.getAddress().setState(estado.getText().trim());
                doctor.setCouncilCode(crm.getText().trim().replaceAll("[^0-9]", ""));
                doctor.setConsultationValue(Float.parseFloat(valor.getText().trim().replace(",", ".")));

                if (usingDatabase) {
                    doctorService.updateDoctor(doctor);
                }

                tableMedicos.refresh();
                NavigationHelper.showInfo("Editar Médico", "Dados de \"" + doctor.getName() + "\" atualizados com sucesso.");
            } catch (RuntimeException | SQLException exception) {
                NavigationHelper.showError(exception.getMessage());
            }
        });
    }

    private void onExcluirMedico(Doctor doctor) {
        boolean confirmado = NavigationHelper.confirm(
                "Excluir Médico",
                "Tem certeza que deseja excluir \"" + doctor.getName() + "\"?"
        );
        if (confirmado) {
            try {
                if (usingDatabase) {
                    doctorService.removeDoctor(doctor);
                    carregarDados();
                } else {
                    tableMedicos.getItems().remove(doctor);
                }
            } catch (SQLException exception) {
                NavigationHelper.showError("Erro ao excluir médico: " + exception.getMessage());
            }
        }
    }

    private TextField field(String value) {
        TextField field = new TextField(value);
        field.getStyleClass().add("edit-dialog-field");
        return field;
    }

    private Label label(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("edit-dialog-field-label");
        return label;
    }

    private Label dialogTitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("edit-dialog-title");
        return label;
    }

    private Label dialogSubtitle(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("edit-dialog-subtitle");
        return label;
    }

    private VBox section(String title, GridPane grid) {
        VBox box = new VBox(12, sectionTitle(title), grid);
        box.getStyleClass().add("edit-dialog-section");
        return box;
    }

    private Label sectionTitle(String title) {
        Label label = new Label(title);
        label.getStyleClass().add("edit-dialog-section-title");
        return label;
    }

    private VBox footer(DialogPane pane) {
        Button saveButton = (Button) pane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) pane.lookupButton(ButtonType.CANCEL);
        saveButton.getStyleClass().add("btn-accent");
        cancelButton.getStyleClass().add("btn-ghost");

        HBox footer = new HBox(10, cancelButton, saveButton);
        footer.getStyleClass().add("edit-dialog-footer");
        return new VBox(footer);
    }

    @FXML
    public void onNovoMedico(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "CadastroMedico.fxml");
    }
}