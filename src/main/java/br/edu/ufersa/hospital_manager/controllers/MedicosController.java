package br.edu.ufersa.hospital_manager.controllers;

<<<<<<< HEAD
=======
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
>>>>>>> 96ad7c6 (Linked screens to data base)
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
<<<<<<< HEAD
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
=======
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
>>>>>>> 96ad7c6 (Linked screens to data base)


public class MedicosController {
// BOTOES DE NAVEGAÇÃO E AÇÕES  
    @FXML
    private Button btnNovoMedico;

    // ── Botões de navegação ───────────────────────────────────
    @FXML private Button btnDashboard;
    @FXML private Button btnMedicos;
    @FXML private Button btnPacientes;
    @FXML private Button btnConsultas;
    @FXML private Button btnBusca;
    @FXML private Button btnRelatorios;


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

<<<<<<< HEAD
    @FXML
    public void initialize() {
        configurarColunas();
        carregarDadosMock();
=======
    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();
    private final ObservableList<Doctor> medicos = FXCollections.observableArrayList();
    private boolean usingDatabase = true;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
>>>>>>> 96ad7c6 (Linked screens to data base)
    }

    @FXML
    private void onDashboard() {
        setNavAtivo(btnDashboard);
        navegarPara("/br/edu/ufersa/hospital_manager/views/Dashboard.fxml");
    }

    @FXML
    private void onMedicos() {
        setNavAtivo(btnMedicos);
        // Já estamos no Medicos — nenhuma ação extra necessária
    }

    @FXML
    private void onPacientes() {
        setNavAtivo(btnPacientes);
        navegarPara("/br/edu/ufersa/hospital_manager/views/pacientes.fxml");
    }

    @FXML
    private void onConsultas() {
        setNavAtivo(btnConsultas);
        navegarPara("/br/edu/ufersa/hospital_manager/views/consultas.fxml");
    }

    @FXML
    private void onBusca() {
        setNavAtivo(btnBusca);
        navegarPara("/br/edu/ufersa/hospital_manager/views/busca.fxml");
    }

    @FXML
    private void onRelatorios() {
        setNavAtivo(btnRelatorios);
        navegarPara("/br/edu/ufersa/hospital_manager/views/relatorios.fxml");
    }


    private void configurarColunas() {

        // Coluna NOME: nome em destaque + endereço como subtítulo (igual ao Figma)
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

<<<<<<< HEAD
    private void carregarDadosMock() {
        ObservableList<Doctor> dados = FXCollections.observableArrayList();

        Address endereco1 = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        Doctor doctor1 = new Doctor("Luiz Silva", "12345678900", endereco1, 250.0f, "123456");

        dados.add(doctor1);

        tableMedicos.setItems(dados);
=======
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
>>>>>>> 96ad7c6 (Linked screens to data base)
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
<<<<<<< HEAD
        // TODO: abrir formulário de edição conectado ao DoctorDAO/DoctorServices
        NavigationHelper.showInfo("Editar Médico", "Edição de \"" + doctor.getName() + "\" em construção.");
=======
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
>>>>>>> 96ad7c6 (Linked screens to data base)
    }

    private void onExcluirMedico(Doctor doctor) {
        boolean confirmado = NavigationHelper.confirm(
                "Excluir Médico",
                "Tem certeza que deseja excluir \"" + doctor.getName() + "\"?"
        );
        if (confirmado) {
<<<<<<< HEAD
            // TODO: remover via DoctorDAO/DoctorServices e recarregar a tabela
            tableMedicos.getItems().remove(doctor);
        }
    }

=======
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

>>>>>>> 96ad7c6 (Linked screens to data base)
    @FXML
    public void onNovoMedico(ActionEvent event) {
        // TODO: abrir formulário de cadastro conectado ao DoctorDAO/DoctorServices
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "CadastroMedico.fxml");
    }

    // ===================== NAVEGAÇÃO ENTRE TELAS =====================

    @FXML
    public void goDashboard(ActionEvent event) {
<<<<<<< HEAD
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "dashboard.fxml");
=======
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "Dashboard.fxml");
>>>>>>> 96ad7c6 (Linked screens to data base)
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
<<<<<<< HEAD
=======

    @FXML
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "login.fxml");
    }
>>>>>>> 96ad7c6 (Linked screens to data base)
        // ─────────────────────────────────────────────────────────
    // Utilitários de navegação
    // ─────────────────────────────────────────────────────────

    /**
     * Marca o botão selecionado como ativo e remove o estilo dos demais.
     */
    private void setNavAtivo(Button botaoAtivo) {
        Button[] todos = {
            btnDashboard, btnMedicos, btnPacientes,
            btnConsultas, btnBusca, btnRelatorios
        };
        for (Button btn : todos) {
            btn.getStyleClass().remove("nav-btn-active");
        }
        if (!botaoAtivo.getStyleClass().contains("nav-btn-active")) {
            botaoAtivo.getStyleClass().add("nav-btn-active");
        }
    }

    /**
     * Carrega outro FXML na área central.
     * Adapte conforme a arquitetura de navegação do seu projeto
     * (ex.: injetar um controlador-raiz, usar um ScreenManager, etc.).
     *
     * @param fxmlPath caminho relativo ao classpath do arquivo FXML
     */
    private void navegarPara(String fxmlPath) {
        try {
            // Exemplo de navegação com troca de cena:
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.getScene().setRoot(root);

            System.out.println("Navegando para: " + fxmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
<<<<<<< HEAD
        @FXML
        private void onNovoMedico() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Cadastrar novo médico");
        dialog.setHeaderText(null);

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Estiliza o botão OK
        Button btnOk = (Button) pane.lookupButton(ButtonType.OK);
        btnOk.setText("Salvar médico");
        btnOk.getStyleClass().add("btn-accent");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16, 24, 8, 24));

        TextField fNome        = new TextField(); fNome.setPromptText("Dr. João da Silva");
        TextField fCpf         = new TextField(); fCpf.setPromptText("000.000.000-00");
        TextField fCrm         = new TextField(); fCrm.setPromptText("CRM 00000/UF");
        TextField fEspecialidade = new TextField(); fEspecialidade.setPromptText("Clínica Geral");
        TextField fValor       = new TextField(); fValor.setPromptText("0,00");
        TextField fEndereco    = new TextField(); fEndereco.setPromptText("Av. Principal, 100 - Cidade/UF");
        TextField fTelefone    = new TextField(); fTelefone.setPromptText("(00) 90000-0000");
        TextField fEmail       = new TextField(); fEmail.setPromptText("medico@clinica.com");

        // Aplica a classe CSS dos campos existentes
        for (TextField f : List.of(fNome,fCpf,fCrm,fEspecialidade,fValor,fEndereco,fTelefone,fEmail))
            f.getStyleClass().add("text-input");

        grid.addRow(0, label("Nome completo"), fNome);
        GridPane.setColumnSpan(fNome, 3);
        grid.addRow(1, label("CPF"), fCpf, label("CRM / Conselho"), fCrm);
        grid.addRow(2, label("Especialidade"), fEspecialidade, label("Valor consulta (R$)"), fValor);
        grid.addRow(3, label("Endereço"), fEndereco);
        GridPane.setColumnSpan(fEndereco, 3);
        grid.addRow(4, label("Telefone"), fTelefone, label("E-mail"), fEmail);

        pane.setContent(grid);
        pane.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String endereço = fEndereco.getText();
                String[] partes = endereço.split(",");
                String rua = partes.length > 0 ? partes[0].trim() : "";
                String numero = partes.length > 1 ? partes[1].trim() : "";
                Doctor m = new Doctor(fNome.getText(), fCpf.getText(), new Address(rua, numero,null,null,null), Float.parseFloat(fValor.getText().replace(",", ".")), fCrm.getText());
            }
                });
            }

            private Label label(String texto) {
                Label l = new Label(texto);
                l.getStyleClass().add("form-label");
                return l;
            }
=======
>>>>>>> 96ad7c6 (Linked screens to data base)
}
