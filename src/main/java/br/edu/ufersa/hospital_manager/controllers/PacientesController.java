package br.edu.ufersa.hospital_manager.controllers;

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

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Patient;

public class PacientesController {

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

    @FXML
    public void initialize() {
        configurarColunas();
        carregarDadosMock();
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
            /* 
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                Patient patient = getTableRow() != null ? getTableRow().getItem() : null;
                if (empty || patient == null) {
                    setGraphic(null);
                    return;
                }
                int quantidade = patient.getMedicalRecord() != null ? 1 : 0;
                link.setText(quantidade + " prontuário" + (quantidade == 1 ? "" : "s"));
                setGraphic(link);
            }
                */
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

    private void carregarDadosMock() {
        ObservableList<Patient> dados = FXCollections.observableArrayList();

        Address endereco1 = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
        Patient paciente1 = new Patient("Maria Santos", "11122233344", endereco1);

        Address endereco2 = new Address("Av. Central", "200", "Centro", "Mossoró", "RN");
        Patient paciente2 = new Patient("João Oliveira", "55566677788", endereco2);

        dados.add(paciente1);
        dados.add(paciente2);

        tablePacientes.setItems(dados);
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    private void onVerProntuarios(Patient patient) {
        // TODO: navegar para tela de prontuários do paciente, conectado ao MedicalRecordDAO/Services
        NavigationHelper.showInfo("Prontuários", "Prontuários de \"" + patient.getName() + "\" em construção.");
    }

    private void onEditarPaciente(Patient patient) {
        // TODO: abrir formulário de edição conectado ao PatientDAO/PatientServices
        NavigationHelper.showInfo("Editar Paciente", "Edição de \"" + patient.getName() + "\" em construção.");
    }

    private void onExcluirPaciente(Patient patient) {
        boolean confirmado = NavigationHelper.confirm(
                "Excluir Paciente",
                "Tem certeza que deseja excluir \"" + patient.getName() + "\"?"
        );
        if (confirmado) {
            // TODO: remover via PatientDAO/PatientServices e recarregar a tabela
            tablePacientes.getItems().remove(patient);
        }
    }

    @FXML
    public void onNovoPaciente(ActionEvent event) {
        
        NavigationHelper.goTo(((javafx.scene.Node) event.getSource()), "CadastroPaciente.fxml");
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
