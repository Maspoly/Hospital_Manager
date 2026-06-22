package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Manager;
import br.edu.ufersa.hospital_manager.model.services.ManagerServiceProxy;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class GerentesController {

    // ── Botões de navegação ───────────────────────────────────
    @FXML private Button btnDashboard;
    @FXML private Button btnMedicos;
    @FXML private Button btnPacientes;
    @FXML private Button btnGerentes;
    @FXML private Button btnConsultas;
    @FXML private Button btnBusca;
    @FXML private Button btnRelatorios;

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

    @FXML
    private Label lblUserName;

    @FXML
    private Label lblUserRole;

    private final ManagerServiceProxy managerService = new ManagerServiceProxy();
    private final ObservableList<Manager> gerentes = FXCollections.observableArrayList();
    private boolean usingDatabase = true;

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDados();
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

    private void onEditarGerente(Manager manager) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Gerente");
        dialog.setHeaderText("Atualize os dados de " + manager.getName());

        ButtonType salvar = new ButtonType("Salvar", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(salvar, ButtonType.CANCEL);

        // Cria os campos do formulário
        TextField nomeField = new TextField(manager.getName());
        TextField cpfField = new TextField(manager.getCPF());

        Address address = manager.getAddress();
        TextField ruaField = new TextField(address.getStreet() != null ? address.getStreet() : "");
        TextField numeroField = new TextField(address.getNumber() != null ? address.getNumber() : "");
        TextField bairroField = new TextField(address.getNeighborhood() != null ? address.getNeighborhood() : "");
        TextField cidadeField = new TextField(address.getCity() != null ? address.getCity() : "");
        TextField estadoField = new TextField(address.getState() != null ? address.getState() : "");

        // Cria o GridPane
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(16, 24, 8, 24));

        // Adiciona os campos ao GridPane
        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        GridPane.setColumnSpan(nomeField, 3);

        grid.add(new Label("CPF:"), 0, 1);
        grid.add(cpfField, 1, 1);

        grid.add(new Label("Rua:"), 0, 2);
        grid.add(ruaField, 1, 2);
        grid.add(new Label("Número:"), 2, 2);
        grid.add(numeroField, 3, 2);

        grid.add(new Label("Bairro:"), 0, 3);
        grid.add(bairroField, 1, 3);
        grid.add(new Label("Cidade:"), 2, 3);
        grid.add(cidadeField, 3, 3);

        grid.add(new Label("Estado:"), 0, 4);
        grid.add(estadoField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            try {
                manager.setName(nomeField.getText().trim());
                manager.setCPF(cpfField.getText().trim().replaceAll("[^0-9]", ""));

                manager.getAddress().setStreet(ruaField.getText().trim());
                manager.getAddress().setNumber(numeroField.getText().trim());
                manager.getAddress().setNeighborhood(bairroField.getText().trim());
                manager.getAddress().setCity(cidadeField.getText().trim());
                manager.getAddress().setState(estadoField.getText().trim());

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

    @FXML
    public void onNovoGerente(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "CadastroGerente.fxml");
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