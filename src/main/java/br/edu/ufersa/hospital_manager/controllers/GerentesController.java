package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Manager;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.ManagerServiceProxy;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GerentesController {

    // ── Botões de navegação ───────────────────────────────────
    @FXML private Button btnDashboard;
    @FXML private Button btnMedicos;
    @FXML private Button btnPacientes;
    @FXML private Button btnGerentes;
    @FXML private Button btnConsultas;
    @FXML private Button btnBusca;
    @FXML private Button btnRelatorios;

    // ── Labels do usuário logado ──────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblVisualizarPerfil;

    @FXML
    private TableView<Manager> tableGerentes;

    @FXML
    private TableColumn<Manager, String> colNome;

    @FXML
    private TableColumn<Manager, String> colCpf;

    @FXML
    private TableColumn<Manager, String> colEndereco;

    @FXML
    private TableColumn<Manager, Void> colAcoes;

    private final ManagerServiceProxy managerService = new ManagerServiceProxy();
    private final ObservableList<Manager> gerentes = FXCollections.observableArrayList();
    private boolean usingDatabase = true;

    @FXML
    public void initialize() {
        carregarDadosUsuario();
        configurarLinkPerfil();
        configurarColunas();
        carregarDados();
    }

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

    @FXML
    public void onNovoGerente(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "CadastroGerente.fxml");
    }

    // ===================== MÉTODOS INTERNOS =====================

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

        colAcoes.setCellFactory(col -> new TableCell<Manager, Void>() {

            private final Button btnEditar = new Button("Editar");
            private final Button btnExcluir = new Button("Excluir");
            private final HBox box = new HBox(10, btnEditar, btnExcluir);

            {
                btnEditar.getStyleClass().add("cell-link");
                btnExcluir.getStyleClass().add("btn-danger-ghost");

                btnEditar.setOnAction(e -> onEditarGerente(getTableRow().getItem()));
                btnExcluir.setOnAction(e -> onExcluirGerente(getTableRow().getItem()));
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
            gerentes.setAll(managerService.listAll());
            usingDatabase = true;
        } catch (SQLException exception) {
            usingDatabase = false;
            gerentes.setAll(carregarDadosMock());
        }

        tableGerentes.setItems(gerentes);
    }

    private List<Manager> carregarDadosMock() {
        List<Manager> dados = new ArrayList<>();
        Address endereco1 = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        dados.add(new Manager("Administrador", "00000000000", endereco1));
        return dados;
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    // ===================== EDIÇÃO COMPLETA =====================

    private void onEditarGerente(Manager manager) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Gerente");
        dialog.setHeaderText(null);

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ((Button) pane.lookupButton(ButtonType.OK)).setText("Salvar alterações");

        // ===== DADOS PESSOAIS =====
        TextField nome = new TextField(manager.getName());
        nome.setPromptText("Nome completo");
        nome.getStyleClass().add("edit-dialog-field");

        TextField cpf = new TextField(manager.getCPF());
        cpf.setPromptText("CPF (apenas números)");
        cpf.getStyleClass().add("edit-dialog-field");

        // ===== ENDEREÇO =====
        Address address = manager.getAddress();
        TextField rua = new TextField(address.getStreet());
        rua.setPromptText("Rua");
        rua.getStyleClass().add("edit-dialog-field");

        TextField numero = new TextField(address.getNumber());
        numero.setPromptText("Número");
        numero.getStyleClass().add("edit-dialog-field");

        TextField bairro = new TextField(address.getNeighborhood());
        bairro.setPromptText("Bairro");
        bairro.getStyleClass().add("edit-dialog-field");

        TextField cidade = new TextField(address.getCity());
        cidade.setPromptText("Cidade");
        cidade.getStyleClass().add("edit-dialog-field");

        TextField estado = new TextField(address.getState());
        estado.setPromptText("UF (ex: RN)");
        estado.getStyleClass().add("edit-dialog-field");

        // ===== SENHA =====
        PasswordField novaSenha = new PasswordField();
        novaSenha.setPromptText("Digite a nova senha (opcional)");
        novaSenha.getStyleClass().add("edit-dialog-field");

        PasswordField confirmarSenha = new PasswordField();
        confirmarSenha.setPromptText("Confirme a nova senha");
        confirmarSenha.getStyleClass().add("edit-dialog-field");

        Label lblSenhaErro = new Label();
        lblSenhaErro.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 11px;");
        lblSenhaErro.setVisible(false);
        lblSenhaErro.setManaged(false);

        // ===== GRIDS =====
        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(12);
        personalGrid.setVgap(12);
        personalGrid.addRow(0, label("Nome completo *"), nome);
        personalGrid.addRow(1, label("CPF *"), cpf);
        GridPane.setColumnSpan(nome, 3);
        GridPane.setColumnSpan(cpf, 3);

        GridPane addressGrid = new GridPane();
        addressGrid.setHgap(12);
        addressGrid.setVgap(12);
        addressGrid.addRow(0, label("Rua *"), rua, label("Número *"), numero);
        addressGrid.addRow(1, label("Bairro *"), bairro, label("Cidade *"), cidade);
        addressGrid.addRow(2, label("Estado *"), estado);
        GridPane.setColumnSpan(estado, 3);

        GridPane senhaGrid = new GridPane();
        senhaGrid.setHgap(12);
        senhaGrid.setVgap(8);
        senhaGrid.addRow(0, label("Nova senha (opcional)"), novaSenha);
        senhaGrid.addRow(1, label("Confirmar senha"), confirmarSenha);
        senhaGrid.addRow(2, lblSenhaErro);
        GridPane.setColumnSpan(novaSenha, 3);
        GridPane.setColumnSpan(confirmarSenha, 3);
        GridPane.setColumnSpan(lblSenhaErro, 3);

        // ===== VALIDAÇÕES EM TEMPO REAL =====
        novaSenha.textProperty().addListener((obs, oldVal, newVal) -> {
            validarSenha(novaSenha, confirmarSenha, lblSenhaErro);
        });

        confirmarSenha.textProperty().addListener((obs, oldVal, newVal) -> {
            validarSenha(novaSenha, confirmarSenha, lblSenhaErro);
        });

        // ===== LAYOUT =====
        VBox sections = new VBox(14,
                section("Dados pessoais", personalGrid),
                section("Endereço", addressGrid),
                section("Alterar senha (opcional)", senhaGrid)
        );
        sections.setPadding(new Insets(2, 2, 0, 2));

        VBox header = new VBox(4,
                dialogTitle("Editar Gerente"),
                dialogSubtitle("Preencha todos os campos obrigatórios (*) para atualizar os dados.")
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
                // Validar campos obrigatórios
                if (nome.getText().trim().isEmpty()) {
                    NavigationHelper.showError("O nome completo é obrigatório.");
                    return;
                }
                if (cpf.getText().trim().isEmpty()) {
                    NavigationHelper.showError("O CPF é obrigatório.");
                    return;
                }
                if (rua.getText().trim().isEmpty()) {
                    NavigationHelper.showError("A rua é obrigatória.");
                    return;
                }
                if (numero.getText().trim().isEmpty()) {
                    NavigationHelper.showError("O número é obrigatório.");
                    return;
                }
                if (bairro.getText().trim().isEmpty()) {
                    NavigationHelper.showError("O bairro é obrigatório.");
                    return;
                }
                if (cidade.getText().trim().isEmpty()) {
                    NavigationHelper.showError("A cidade é obrigatória.");
                    return;
                }
                if (estado.getText().trim().isEmpty()) {
                    NavigationHelper.showError("O estado é obrigatório.");
                    return;
                }

                // Atualizar dados
                manager.setName(nome.getText().trim());
                manager.setCPF(cpf.getText().trim().replaceAll("[^0-9]", ""));
                manager.getAddress().setStreet(rua.getText().trim());
                manager.getAddress().setNumber(numero.getText().trim());
                manager.getAddress().setNeighborhood(bairro.getText().trim());
                manager.getAddress().setCity(cidade.getText().trim());
                manager.getAddress().setState(estado.getText().trim().toUpperCase());

                // Verifica se a senha foi preenchida
                String senha = novaSenha.getText().trim();
                if (!senha.isEmpty()) {
                    if (senha.length() < 6) {
                        NavigationHelper.showError("A senha deve ter no mínimo 6 caracteres.");
                        return;
                    }
                    if (!senha.equals(confirmarSenha.getText().trim())) {
                        NavigationHelper.showError("As senhas não conferem.");
                        return;
                    }
                    manager.setPassword(senha);
                }

                if (usingDatabase) {
                    managerService.updateManager(manager);
                }

                tableGerentes.refresh();
                NavigationHelper.showInfo("Editar Gerente", "Dados de \"" + manager.getName() + "\" atualizados com sucesso.");
            } catch (RuntimeException | SQLException exception) {
                NavigationHelper.showError(exception.getMessage());
            }
        });
    }

    private void validarSenha(PasswordField senha, PasswordField confirmar, Label lblErro) {
        String s = senha.getText().trim();
        String c = confirmar.getText().trim();

        if (s.isEmpty() && c.isEmpty()) {
            lblErro.setVisible(false);
            lblErro.setManaged(false);
            return;
        }

        if (s.length() < 6 && !s.isEmpty()) {
            lblErro.setText("A senha deve ter no mínimo 6 caracteres.");
            lblErro.setVisible(true);
            lblErro.setManaged(true);
            return;
        }

        if (!s.equals(c) && !s.isEmpty()) {
            lblErro.setText("As senhas não conferem.");
            lblErro.setVisible(true);
            lblErro.setManaged(true);
            return;
        }

        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }

    private void onExcluirGerente(Manager manager) {
        boolean confirmado = NavigationHelper.confirm(
                "Excluir Gerente",
                "Tem certeza que deseja excluir \"" + manager.getName() + "\"?"
        );
        if (confirmado) {
            try {
                if (usingDatabase) {
                    managerService.removeManager(manager);
                    carregarDados();
                } else {
                    tableGerentes.getItems().remove(manager);
                }
                NavigationHelper.showInfo("Sucesso", "Gerente removido com sucesso!");
            } catch (SQLException exception) {
                NavigationHelper.showError("Erro ao excluir gerente: " + exception.getMessage());
            }
        }
    }

    // ===================== HELPERS DO DIÁLOGO =====================

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
}