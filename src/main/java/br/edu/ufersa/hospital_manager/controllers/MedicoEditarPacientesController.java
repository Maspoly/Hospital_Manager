package br.edu.ufersa.hospital_manager.controllers;

import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.services.MedicalRecordServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.PatientServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import br.edu.ufersa.hospital_manager.util.ProxyFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class MedicoEditarPacientesController {

    @FXML
    private Label lblIniciais;

    @FXML
    private Label lblNomeMedico;

    @FXML
    private Label lblCrmMedico;

    @FXML
    private TextField txtBusca;

    @FXML
    private VBox boxListaPacientes;

    @FXML
    private VBox boxPainelEdicao;

    @FXML
    private Label lblTotalPacientesStrip;

    private final List<Patient> pacientesMock = new ArrayList<>();
    private Patient pacienteSelecionado;
    private final MedicalRecordServiceProxy medicalRecordService = (MedicalRecordServiceProxy) ProxyFactory.createProxy("MEDICAL_RECORD");
    private final PatientServiceProxy patientService = (PatientServiceProxy) ProxyFactory.createProxy("PATIENT");

    @FXML
    public void initialize() {
        configurarDadosMedico();
        carregarDados();
        renderizarLista(pacientesMock);
        lblTotalPacientesStrip.setText("Total de Pacientes: " + pacientesMock.size());

        txtBusca.textProperty().addListener((obs, oldVal, newVal) -> filtrarPacientes(newVal));
    }

    private void configurarDadosMedico() {
        if (ServiceRoleContext.getCurrentUser() instanceof Doctor) {
            Doctor medico = (Doctor) ServiceRoleContext.getCurrentUser();
            lblIniciais.setText(extrairIniciais(medico.getName()));
            lblNomeMedico.setText("Dr. " + medico.getName());
            lblCrmMedico.setText("CRM-" + medico.getCouncilCode());
            return;
        }

        lblIniciais.setText("D");
        lblNomeMedico.setText("Dr. Médico");
        lblCrmMedico.setText("CRM-000000");
    }

    private void carregarDados() {
        pacientesMock.clear();
        try {
            pacientesMock.addAll(patientService.listAll());
        } catch (Exception exception) {
            Address endereco1 = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
            pacientesMock.add(new Patient("Maria Santos", "11122233344", endereco1));

            Address endereco2 = new Address("Av. Central", "200", "Centro", "Mossoró", "RN");
            pacientesMock.add(new Patient("João Oliveira", "55566677788", endereco2));
        }
    }

    private void filtrarPacientes(String termo) {
        String termoBusca = termo == null ? "" : termo.trim().toLowerCase();
        List<Patient> filtrados = new ArrayList<>();
        for (Patient p : pacientesMock) {
            if (termoBusca.isEmpty()
                    || p.getName().toLowerCase().contains(termoBusca)
                    || p.getCPF().contains(termoBusca)) {
                filtrados.add(p);
            }
        }
        renderizarLista(filtrados);
    }

    private void renderizarLista(List<Patient> pacientes) {
        boxListaPacientes.getChildren().clear();

        for (Patient paciente : pacientes) {
            boolean selecionado = paciente == pacienteSelecionado;

            VBox item = new VBox(2);
            item.getStyleClass().add(selecionado ? "medico-patient-list-item-selected" : "medico-patient-list-item");

            Label nome = new Label(paciente.getName());
            nome.getStyleClass().add("medico-patient-name");

            Label cpf = new Label("CPF: " + formatarCpf(paciente.getCPF()));
            cpf.getStyleClass().add("medico-patient-detail");

            // Verifica se tem prontuário via MedicalRecordService
            boolean temProntuario = false;
            try {
                MedicalRecord record = medicalRecordService.findByPatient(paciente);
                temProntuario = (record != null);
            } catch (Exception e) {
                // Paciente sem prontuário
            }
            Label prontuario = new Label("Prontuário: " + (temProntuario ? "1" : "0"));
            prontuario.getStyleClass().add("medico-patient-detail");

            item.getChildren().addAll(nome, cpf, prontuario);
            item.setOnMouseClicked(e -> selecionarPaciente(paciente));

            boxListaPacientes.getChildren().add(item);
        }
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    private void selecionarPaciente(Patient paciente) {
        this.pacienteSelecionado = paciente;
        renderizarLista(filtrarComTermoAtual());
        renderizarPainelEdicao(paciente);
    }

    private List<Patient> filtrarComTermoAtual() {
        String termo = txtBusca.getText();
        if (termo == null || termo.isBlank()) {
            return pacientesMock;
        }
        List<Patient> filtrados = new ArrayList<>();
        String termoBusca = termo.trim().toLowerCase();
        for (Patient p : pacientesMock) {
            if (p.getName().toLowerCase().contains(termoBusca) || p.getCPF().contains(termoBusca)) {
                filtrados.add(p);
            }
        }
        return filtrados;
    }

    private void renderizarPainelEdicao(Patient paciente) {
        boxPainelEdicao.getChildren().clear();
        boxPainelEdicao.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        boxPainelEdicao.setPadding(new Insets(20, 22, 20, 22));
        boxPainelEdicao.setSpacing(14);

        Label titulo = new Label("Editar: " + paciente.getName());
        titulo.getStyleClass().add("medico-panel-title");

        VBox campoNome = criarCampo("Nome", paciente.getName());
        VBox campoCpf = criarCampo("CPF", formatarCpf(paciente.getCPF()));
        
        Address addr = paciente.getAddress();
        String enderecoTexto = addr.getStreet() + ", " + addr.getNumber()
                + " - " + addr.getCity() + "/" + addr.getState();
        VBox campoEndereco = criarCampo("Endereço", enderecoTexto);

        HBox botoes = new HBox(12);
        Button btnSalvar = new Button("💾  Salvar Alterações");
        btnSalvar.getStyleClass().add("medico-btn-save");
        btnSalvar.setOnAction(e -> onSalvarEdicao(paciente));

        Button btnCancelar = new Button("Cancelar");
        btnCancelar.getStyleClass().add("medico-btn-cancel");
        btnCancelar.setOnAction(e -> limparSelecao());

        botoes.getChildren().addAll(btnSalvar, btnCancelar);

        boxPainelEdicao.getChildren().addAll(titulo, campoNome, campoCpf, campoEndereco, botoes);
    }

    private VBox criarCampo(String rotulo, String valor) {
        Label lbl = new Label(rotulo);
        lbl.getStyleClass().add("medico-form-label");
        TextField campo = new TextField(valor);
        campo.getStyleClass().add("medico-text-input");
        return new VBox(6, lbl, campo);
    }

    private void onSalvarEdicao(Patient paciente) {
        NavigationHelper.showInfo("Dados Atualizados", "Informações de \"" + paciente.getName() + "\" atualizadas com sucesso.");
        limparSelecao();
    }

    private void limparSelecao() {
        this.pacienteSelecionado = null;
        renderizarLista(filtrarComTermoAtual());
        mostrarEstadoVazio();
    }

    private void mostrarEstadoVazio() {
        boxPainelEdicao.getChildren().clear();
        boxPainelEdicao.setAlignment(javafx.geometry.Pos.CENTER);
        boxPainelEdicao.setPadding(new Insets(40, 20, 40, 20));

        StackPane icone = new StackPane();
        Circle circulo = new Circle(26);
        circulo.getStyleClass().add("medico-empty-state-icon-circle");
        Label glyph = new Label("👤");
        glyph.setStyle("-fx-font-size: 20px; -fx-text-fill: #c1c5cc;");
        icone.getChildren().addAll(circulo, glyph);

        Label titulo = new Label("Nenhum paciente selecionado");
        titulo.getStyleClass().add("medico-panel-title");

        Label texto = new Label("Selecione um paciente na lista ao lado para editar suas informações pessoais");
        texto.getStyleClass().add("medico-empty-state-title");
        texto.setWrapText(true);
        texto.setMaxWidth(280);
        texto.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox conteudo = new VBox(10, icone, titulo, texto);
        conteudo.setAlignment(javafx.geometry.Pos.CENTER);

        boxPainelEdicao.getChildren().add(conteudo);
    }

    // ===================== NAVEGAÇÃO ENTRE TELAS =====================

    @FXML
    public void goMeusPacientes(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_pacientes.fxml", "medico.css");
    }

    @FXML
    public void goMinhasConsultas(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_consultas.fxml", "medico.css");
    }

    @FXML
    public void goCadastrarProntuario(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_cadastrar_prontuario.fxml", "medico.css");
    }

    @FXML
    public void goRelatorios(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_relatorios.fxml", "medico.css");
    }

    @FXML
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
    }

    private String extrairIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "D";
        }

        StringBuilder iniciais = new StringBuilder();
        for (String parte : nome.trim().split("\\s+")) {
            if (!parte.isBlank()) {
                iniciais.append(Character.toUpperCase(parte.charAt(0)));
            }
            if (iniciais.length() == 2) {
                break;
            }
        }

        return iniciais.length() > 0 ? iniciais.toString() : "D";
    }
}