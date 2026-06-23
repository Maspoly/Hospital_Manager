package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.MedicalRecord;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.MedicalRecordServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;

public class MedicoCadastrarProntuarioController implements DadosRecebivel {

    @FXML
    private Label lblIniciais;

    @FXML
    private Label lblNomeMedico;

    @FXML
    private Label lblCrmMedico;

    @FXML
    private DatePicker dateConsulta;

    @FXML
    private TextField txtBuscarPaciente;

    @FXML
    private ListView<Patient> lstPacientes;

    @FXML
    private TextArea txtObservacoes;

    @FXML
    private Label lblContador;

    @FXML private Label lblVisualizarPerfil;
    @FXML
    private Label lblPacienteSelecionado;

    private final MedicalRecordServiceProxy medicalRecordService = new MedicalRecordServiceProxy();
    private final ConsultationServiceProxy consultationService = new ConsultationServiceProxy();
    private final ObservableList<Patient> pacientesDisponiveis = FXCollections.observableArrayList();
    private final FilteredList<Patient> pacientesFiltrados = new FilteredList<>(pacientesDisponiveis, patient -> true);
    private Doctor medicoLogado;
    private Patient pacienteSelecionado;

    // Dados recebidos via NavigationHelper
    private Patient pacientePreSelecionado;

    @FXML
    public void initialize() {
        configurarDadosMedico();
        carregarDados();
        configurarListaPacientes();
        configurarBuscaPaciente();
        configurarContadorCaracteres();
        dateConsulta.setValue(LocalDate.now());

        // Se veio um paciente pré-selecionado, seleciona ele
        if (pacientePreSelecionado != null) {
            selecionarPaciente(pacientePreSelecionado);
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
    @Override
    public void receberDados(String key, Object value) {
        if ("pacienteSelecionado".equals(key) && value instanceof Patient) {
            this.pacientePreSelecionado = (Patient) value;
        }
    }

    private void configurarDadosMedico() {
        if (ServiceRoleContext.getCurrentUser() instanceof Doctor) {
            medicoLogado = (Doctor) ServiceRoleContext.getCurrentUser();
            lblIniciais.setText(extrairIniciais(medicoLogado.getName()));
            lblNomeMedico.setText("Dr. " + medicoLogado.getName());
            lblCrmMedico.setText("CRM-" + medicoLogado.getCouncilCode());
            return;
        }

        medicoLogado = null;
        lblIniciais.setText("D");
        lblNomeMedico.setText("Dr. Médico");
        lblCrmMedico.setText("CRM-000000");
    }

    private void carregarDados() {
        pacientesDisponiveis.clear();

        if (medicoLogado == null) {
            return;
        }

        try {
            Map<Long, Patient> pacientesUnicos = new LinkedHashMap<>();
            ArrayList<Consultation> consultas = consultationService.findByDoctor(medicoLogado);

            for (Consultation consultation : consultas) {
                if (consultation.getPatient() != null) {
                    pacientesUnicos.putIfAbsent(consultation.getPatient().getId(), consultation.getPatient());
                }
            }

            ArrayList<Patient> pacientesOrdenados = new ArrayList<>(pacientesUnicos.values());
            pacientesOrdenados.sort(Comparator.comparing(Patient::getName, String.CASE_INSENSITIVE_ORDER));
            pacientesDisponiveis.setAll(pacientesOrdenados);
        } catch (Exception exception) {
            pacientesDisponiveis.clear();
        }
    }

    private void configurarListaPacientes() {
        lstPacientes.setItems(pacientesFiltrados);
        lstPacientes.setPlaceholder(new Label("Nenhum paciente encontrado."));
        lstPacientes.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Patient patient, boolean empty) {
                super.updateItem(patient, empty);

                if (empty || patient == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label nome = new Label(patient.getName());
                nome.getStyleClass().add("medico-patient-cell-name");

                Label cpf = new Label("CPF: " + patient.getCPF());
                cpf.getStyleClass().add("medico-patient-cell-detail");

                VBox conteudo = new VBox(2, nome, cpf);
                conteudo.getStyleClass().add("medico-patient-cell");

                setText(null);
                setGraphic(conteudo);
            }
        });

        lstPacientes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            pacienteSelecionado = newVal;
            lblPacienteSelecionado.setText(newVal == null ? "Nenhum paciente selecionado" : newVal.getName());
        });

        lblPacienteSelecionado.setText("Nenhum paciente selecionado");
    }

    private void configurarBuscaPaciente() {
        txtBuscarPaciente.textProperty().addListener((obs, oldVal, newVal) -> {
            String termo = newVal == null ? "" : newVal.trim().toLowerCase(Locale.ROOT);
            String termoCpf = termo.replaceAll("\\D", "");

            pacientesFiltrados.setPredicate(patient -> {
                if (termo.isBlank()) {
                    return true;
                }

                boolean nomeOk = patient.getName() != null && patient.getName().toLowerCase(Locale.ROOT).contains(termo);
                boolean cpfOk = !termoCpf.isBlank() && patient.getCPF() != null && patient.getCPF().contains(termoCpf);
                return nomeOk || cpfOk;
            });

            if (pacienteSelecionado != null && !pacientesFiltrados.contains(pacienteSelecionado)) {
                lstPacientes.getSelectionModel().clearSelection();
            }
        });
    }

    private void configurarContadorCaracteres() {
        txtObservacoes.textProperty().addListener((obs, oldVal, newVal) ->
                lblContador.setText(newVal.length() + " caracteres")
        );
    }

    private void selecionarPaciente(Patient patient) {
        // Procura o paciente na lista disponível
        for (Patient p : pacientesDisponiveis) {
            if (p.getId() == patient.getId()) {
                lstPacientes.getSelectionModel().select(p);
                lstPacientes.scrollTo(p);
                pacienteSelecionado = p;
                lblPacienteSelecionado.setText(p.getName());
                return;
            }
        }

        // Se não encontrou, adiciona e seleciona
        pacientesDisponiveis.add(patient);
        lstPacientes.getSelectionModel().select(patient);
        pacienteSelecionado = patient;
        lblPacienteSelecionado.setText(patient.getName());
    }

    @FXML
    public void onSalvarProntuario(ActionEvent event) {
        Patient paciente = pacienteSelecionado;
        String observacoes = txtObservacoes.getText();

        if (paciente == null) {
            NavigationHelper.showError("Selecione um paciente na lista lateral antes de salvar.");
            return;
        }
        if (observacoes == null || observacoes.trim().isEmpty()) {
            NavigationHelper.showError("As observações médicas são obrigatórias.");
            return;
        }

        try {
            // Verifica se paciente já tem prontuário
            try {
                MedicalRecord existing = medicalRecordService.findByPatient(paciente);
                if (existing != null) {
                    NavigationHelper.showError("Este paciente já possui um prontuário. Edite o prontuário existente.");
                    return;
                }
            } catch (Exception e) {
                // Paciente não tem prontuário, pode criar
            }

            MedicalRecord record = new MedicalRecord(observacoes, medicoLogado, paciente);
            medicalRecordService.registerMedicalRecord(record);

            NavigationHelper.showInfo("Prontuário Salvo", "Prontuário de \"" + paciente.getName() + "\" registrado com sucesso.");
            NavigationHelper.goTo((Node) event.getSource(), "medico_pacientes.fxml", "medico.css");
        } catch (Exception e) {
            NavigationHelper.showError("Erro ao salvar prontuário: " + e.getMessage());
        }
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_pacientes.fxml", "medico.css");
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