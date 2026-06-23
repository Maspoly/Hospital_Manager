package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import br.edu.ufersa.hospital_manager.util.ProxyFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class CadastroConsultaController {

    // ── Campos do formulário ──────────────────────────────────────────────────
    @FXML private ComboBox<Patient> fldPaciente;
    @FXML private ComboBox<Doctor>  fldMedico;
    @FXML private DatePicker        fldData;
    @FXML private ComboBox<String>  fldHora;
    @FXML private ComboBox<String>  fldStatus;
    @FXML private Label             lblErro;

    // ── Componentes de busca ──────────────────────────────────────────────────
    @FXML private ComboBox<String> cmbTipoBusca;
    @FXML private TextField txtBusca;
    @FXML private ListView<Object> lstResultados;
    @FXML private Button btnBuscar;
    @FXML private Button btnVerDetalhes;
    @FXML private Button btnSelecionar;
    @FXML private Button btnSair;  // NOVO: botão Sair

    // ── Labels do usuário logado (sidebar) ───────────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblIniciais;
    @FXML private Label lblVisualizarPerfil;

    private final ConsultationServiceProxy consultationService = (ConsultationServiceProxy) ProxyFactory.createProxy("CONSULTATION");
    private final DoctorServiceProxy doctorService = (DoctorServiceProxy) ProxyFactory.createProxy("DOCTOR");
    private final PatientServiceProxy patientService = (PatientServiceProxy) ProxyFactory.createProxy("PATIENT");

    private final ObservableList<Object> resultados = FXCollections.observableArrayList();
    private Object itemSelecionado;

    // Listas completas para filtro
    private ObservableList<Patient> allPatients = FXCollections.observableArrayList();
    private ObservableList<Doctor> allDoctors = FXCollections.observableArrayList();

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        carregarDadosUsuario();
        configurarLinkPerfil();
        configurarBotoes();  // NOVO: método unificado para configuração de botões
        carregarPacientes();
        carregarMedicos();
        carregarHorarios();
        carregarStatus();
        configurarCombos();
        configurarListaResultados();
        configurarBuscaPacientesCombo();
        configurarBuscaMedicosCombo();
        fldData.setValue(LocalDate.now());
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
        if (lblIniciais != null) {
            lblIniciais.setText(extrairIniciais(nomeUsuario));
        }
    }

    private void configurarLinkPerfil() {
        if (lblVisualizarPerfil != null) {
            lblVisualizarPerfil.setStyle("-fx-cursor: hand; -fx-text-fill: #60a5fa; -fx-underline: true;");
            lblVisualizarPerfil.setOnMouseClicked(this::onVisualizarPerfil);
        }
    }

    // ── Configuração unificada de botões ─────────────────────────────────────
    private void configurarBotoes() {
        // Botão Buscar
        if (btnBuscar != null) {
            btnBuscar.setOnAction(this::onBuscarMedicoPaciente);
        }
        // Botão Ver Detalhes
        if (btnVerDetalhes != null) {
            btnVerDetalhes.setOnAction(this::onVerDetalhes);
        }
        // Botão Selecionar
        if (btnSelecionar != null) {
            btnSelecionar.setOnAction(this::onSelecionar);
        }
        // Botão Sair (configuração programática)
        if (btnSair != null) {
            btnSair.setOnAction(this::onSair);
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

    // ── Configuração de busca nos ComboBox (em tempo real) ──────────────────

    private void configurarBuscaPacientesCombo() {
        fldPaciente.setEditable(true);
        fldPaciente.setConverter(new StringConverter<>() {
            @Override public String toString(Patient p) {
                return p == null ? "" : p.getName() + " — " + p.getCPF();
            }
            @Override public Patient fromString(String s) { return null; }
        });
        
        TextField editor = fldPaciente.getEditor();
        editor.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarPacientesCombo(newVal);
        });
        
        fldPaciente.setOnAction(event -> {
            Patient selected = fldPaciente.getValue();
            if (selected != null) {
                fldPaciente.getEditor().setText(selected.getName() + " — " + selected.getCPF());
            }
        });
    }

    private void configurarBuscaMedicosCombo() {
        fldMedico.setEditable(true);
        fldMedico.setConverter(new StringConverter<>() {
            @Override public String toString(Doctor d) {
                return d == null ? "" : d.getName() + " — " + d.getCouncilCode();
            }
            @Override public Doctor fromString(String s) { return null; }
        });
        
        TextField editor = fldMedico.getEditor();
        editor.textProperty().addListener((obs, oldVal, newVal) -> {
            filtrarMedicosCombo(newVal);
        });
        
        fldMedico.setOnAction(event -> {
            Doctor selected = fldMedico.getValue();
            if (selected != null) {
                fldMedico.getEditor().setText(selected.getName() + " — " + selected.getCouncilCode());
            }
        });
    }

    private void filtrarPacientesCombo(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            fldPaciente.setItems(allPatients);
            return;
        }
        
        String searchTerm = termo.trim().toLowerCase();
        List<Patient> filtered = allPatients.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchTerm) ||
                            p.getCPF().contains(searchTerm))
                .collect(Collectors.toList());
        
        fldPaciente.setItems(FXCollections.observableArrayList(filtered));
        if (!filtered.isEmpty()) {
            fldPaciente.show();
        }
    }

    private void filtrarMedicosCombo(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            fldMedico.setItems(allDoctors);
            return;
        }
        
        String searchTerm = termo.trim().toLowerCase();
        List<Doctor> filtered = allDoctors.stream()
                .filter(d -> d.getName().toLowerCase().contains(searchTerm) ||
                            d.getCPF().contains(searchTerm) ||
                            d.getCouncilCode().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        
        fldMedico.setItems(FXCollections.observableArrayList(filtered));
        if (!filtered.isEmpty()) {
            fldMedico.show();
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

                VBox conteudo = new VBox(2);
                if (item instanceof Doctor) {
                    Doctor doctor = (Doctor) item;
                    Label nome = new Label("👨‍⚕️ " + doctor.getName());
                    nome.getStyleClass().add("medico-patient-cell-name");
                    Label detalhe = new Label("CRM: " + doctor.getCouncilCode());
                    detalhe.getStyleClass().add("medico-patient-cell-detail");
                    conteudo.getChildren().addAll(nome, detalhe);
                } else if (item instanceof Patient) {
                    Patient patient = (Patient) item;
                    Label nome = new Label("👤 " + patient.getName());
                    nome.getStyleClass().add("medico-patient-cell-name");
                    Label detalhe = new Label("CPF: " + formatarCpf(patient.getCPF()));
                    detalhe.getStyleClass().add("medico-patient-cell-detail");
                    conteudo.getChildren().addAll(nome, detalhe);
                } else {
                    setText(item.toString());
                    return;
                }
                conteudo.getStyleClass().add("medico-patient-cell");
                setText(null);
                setGraphic(conteudo);
            }
        });

        lstResultados.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            itemSelecionado = newVal;
        });
    }

    // ── Busca de Médicos e Pacientes ─────────────────────────────────────────

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
                    doctor.getCouncilCode().toLowerCase().contains(termo)) {
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

    @FXML
    private void onSelecionar(ActionEvent event) {
        if (itemSelecionado == null) {
            NavigationHelper.showInfo("Aviso", "Selecione um item na lista para selecionar.");
            return;
        }

        if (itemSelecionado instanceof Doctor) {
            Doctor doctor = (Doctor) itemSelecionado;
            fldMedico.setValue(doctor);
            fldMedico.getEditor().setText(doctor.getName() + " — " + doctor.getCouncilCode());
            NavigationHelper.showInfo("Selecionado", "Médico \"" + doctor.getName() + "\" selecionado com sucesso!");
        } else if (itemSelecionado instanceof Patient) {
            Patient patient = (Patient) itemSelecionado;
            fldPaciente.setValue(patient);
            fldPaciente.getEditor().setText(patient.getName() + " — " + patient.getCPF());
            NavigationHelper.showInfo("Selecionado", "Paciente \"" + patient.getName() + "\" selecionado com sucesso!");
        }
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf == null ? "" : cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    // ── Carregamento de dados ──────────────────────────────────────────────────

    private void carregarPacientes() {
        try {
            allPatients = FXCollections.observableArrayList(patientService.listAll());
            fldPaciente.setItems(allPatients);
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar pacientes: " + e.getMessage());
        }
    }

    private void carregarMedicos() {
        try {
            allDoctors = FXCollections.observableArrayList(doctorService.listAll());
            fldMedico.setItems(allDoctors);
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar médicos: " + e.getMessage());
        }
    }

    private void carregarHorarios() {
        List<String> horarios = new ArrayList<>();
        LocalTime hora = LocalTime.of(7, 0);
        LocalTime fim  = LocalTime.of(19, 30);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        while (!hora.isAfter(fim)) {
            horarios.add(hora.format(fmt));
            hora = hora.plusMinutes(30);
        }
        fldHora.setItems(FXCollections.observableArrayList(horarios));
        fldHora.setPromptText("Selecione o horário...");
    }

    private void carregarStatus() {
        fldStatus.setItems(FXCollections.observableArrayList(
                "SCHEDULED", "COMPLETED", "CANCELED"
        ));
        fldStatus.setValue("SCHEDULED");
        fldStatus.setPromptText("Selecione o status...");
    }

    // ── Ação: Salvar ──────────────────────────────────────────────────────────
    @FXML
    private void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
            LocalTime hora = LocalTime.parse(
                    fldHora.getValue(), DateTimeFormatter.ofPattern("HH:mm")
            );
            LocalDateTime dateTime = LocalDateTime.of(fldData.getValue(), hora);

            Consultation consulta = new Consultation(
                    fldPaciente.getValue(),
                    fldMedico.getValue(),
                    dateTime,
                    fldStatus.getValue()
            );
            consultationService.createConsultation(consulta);

            NavigationHelper.showInfo("Sucesso", "Consulta agendada com sucesso!");
            NavigationHelper.goTo((Node) event.getSource(), "consultas.fxml");

        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro no banco de dados: " + e.getMessage());
        }
    }

    // ── Ação: Cancelar ────────────────────────────────────────────────────────
    @FXML
    private void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "consultas.fxml");
    }

    // ── Validação ─────────────────────────────────────────────────────────────
    private boolean validar() {
        List<String> erros = new ArrayList<>();

        if (fldPaciente.getValue() == null) erros.add("Selecione um paciente.");
        if (fldMedico.getValue() == null)   erros.add("Selecione um médico.");
        if (fldData.getValue() == null)     erros.add("Selecione a data da consulta.");
        if (fldHora.getValue() == null)     erros.add("Selecione o horário da consulta.");
        if (fldStatus.getValue() == null)   erros.add("Selecione o status da consulta.");

        if (fldData.getValue() != null
                && fldData.getValue().isBefore(LocalDate.now())
                && "SCHEDULED".equals(fldStatus.getValue())) {
            erros.add("Não é possível agendar uma consulta para uma data passada.");
        }

        if (!erros.isEmpty()) {
            mostrarErro(String.join("\n", erros));
            return false;
        }
        ocultarErro();
        return true;
    }

    // ── Helpers de feedback ───────────────────────────────────────────────────
    private void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }

    private void ocultarErro() {
        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }

    private String extrairIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "A";
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

        return iniciais.length() > 0 ? iniciais.toString() : "A";
    }

    // ── Navegação da sidebar ──────────────────────────────────────────────────
    @FXML 
    private void onDashboard(ActionEvent e) { 
        NavigationHelper.goTo((Node) e.getSource(), "Dashboard.fxml"); 
    }
    
    @FXML 
    private void onMedicos(ActionEvent e) { 
        NavigationHelper.goTo((Node) e.getSource(), "medicos.fxml"); 
    }
    
    @FXML 
    private void onPacientes(ActionEvent e) { 
        NavigationHelper.goTo((Node) e.getSource(), "pacientes.fxml"); 
    }
    
    @FXML 
    private void onConsultas(ActionEvent e) { 
        NavigationHelper.goTo((Node) e.getSource(), "consultas.fxml"); 
    }
    
    @FXML 
    private void onBusca(ActionEvent e) { 
        NavigationHelper.goTo((Node) e.getSource(), "busca.fxml"); 
    }
    
    @FXML 
    private void onRelatorios(ActionEvent e) { 
        NavigationHelper.goTo((Node) e.getSource(), "relatorios.fxml"); 
    }
    
    // Método onSair agora é chamado programaticamente, mas mantemos a anotação @FXML para segurança
    @FXML
    private void onSair(ActionEvent e) { 
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) e.getSource(), "login.fxml"); 
    }
}