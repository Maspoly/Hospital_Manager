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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
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
    public void onNovoMedico(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "CadastroMedico.fxml");
    }

    // ===================== MÉTODOS INTERNOS =====================

    private void configurarColunas() {
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

    // ===================== EDIÇÃO COMPLETA =====================

    private void onEditarMedico(Doctor doctor) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Médico");
        dialog.setHeaderText(null);

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ((Button) pane.lookupButton(ButtonType.OK)).setText("Salvar alterações");

        // ===== DADOS PESSOAIS =====
        TextField nome = new TextField(doctor.getName());
        nome.setPromptText("Nome completo");
        nome.getStyleClass().add("edit-dialog-field");

        TextField cpf = new TextField(doctor.getCPF());
        cpf.setPromptText("CPF (apenas números)");
        cpf.getStyleClass().add("edit-dialog-field");

        // ===== ENDEREÇO =====
        Address address = doctor.getAddress();
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

        // ===== DADOS PROFISSIONAIS =====
        TextField crm = new TextField(doctor.getCouncilCode());
        crm.setPromptText("CRM (apenas números)");
        crm.getStyleClass().add("edit-dialog-field");

        TextField valor = new TextField(String.valueOf(doctor.getConsultationValue()));
        valor.setPromptText("Valor da consulta (ex: 150.00)");
        valor.getStyleClass().add("edit-dialog-field");

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

        GridPane professionalGrid = new GridPane();
        professionalGrid.setHgap(12);
        professionalGrid.setVgap(12);
        professionalGrid.addRow(0, label("CRM / Conselho *"), crm);
        professionalGrid.addRow(1, label("Valor da consulta (R$) *"), valor);
        GridPane.setColumnSpan(crm, 3);
        GridPane.setColumnSpan(valor, 3);

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
                section("Dados profissionais", professionalGrid),
                section("Alterar senha (opcional)", senhaGrid)
        );
        sections.setPadding(new Insets(2, 2, 0, 2));

        VBox header = new VBox(4,
                dialogTitle("Editar Médico"),
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
                if (crm.getText().trim().isEmpty()) {
                    NavigationHelper.showError("O CRM é obrigatório.");
                    return;
                }
                if (valor.getText().trim().isEmpty()) {
                    NavigationHelper.showError("O valor da consulta é obrigatório.");
                    return;
                }

                // Atualizar dados
                doctor.setName(nome.getText().trim());
                doctor.setCPF(cpf.getText().trim().replaceAll("[^0-9]", ""));
                doctor.getAddress().setStreet(rua.getText().trim());
                doctor.getAddress().setNumber(numero.getText().trim());
                doctor.getAddress().setNeighborhood(bairro.getText().trim());
                doctor.getAddress().setCity(cidade.getText().trim());
                doctor.getAddress().setState(estado.getText().trim().toUpperCase());
                doctor.setCouncilCode(crm.getText().trim().replaceAll("[^0-9]", ""));
                doctor.setConsultationValue(Float.parseFloat(valor.getText().trim().replace(",", ".")));

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
                    doctor.setPassword(senha);
                }

                if (usingDatabase) {
                    doctorService.updateDoctor(doctor);
                }

                tableMedicos.refresh();
                NavigationHelper.showInfo("Editar Médico", "Dados de \"" + doctor.getName() + "\" atualizados com sucesso.");
            } catch (NumberFormatException e) {
                NavigationHelper.showError("Valor da consulta inválido. Use formato: 150.00");
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