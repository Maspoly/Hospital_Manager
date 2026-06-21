package br.edu.ufersa.hospital_manager.controllers;

import java.time.format.DateTimeFormatter;
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
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.MedicalRecordServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class MedicoPacientesController {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private Label lblIniciais;

    @FXML
    private Label lblNomeMedico;

    @FXML
    private Label lblCrmMedico;

    @FXML
    private Label lblTotalPacientes;

    @FXML
    private Label lblTotalProntuarios;

    @FXML
    private Label lblProntuariosSelecionados;

    @FXML
    private TextField txtBuscarPaciente;

    @FXML
    private ListView<Patient> lstPacientes;

    @FXML
    private Label lblPacienteSelecionado;

    @FXML
    private VBox boxProntuarios;

    // Dados mock
    private final ObservableList<Patient> pacientesDisponiveis = FXCollections.observableArrayList();
    private final FilteredList<Patient> pacientesFiltrados = new FilteredList<>(pacientesDisponiveis, patient -> true);
    private final MedicalRecordServiceProxy medicalRecordService = new MedicalRecordServiceProxy();
    private Patient pacienteSelecionado;

    @FXML
    public void initialize() {
        configurarDadosMedico();
        carregarDados();
        configurarListaPacientes();
        configurarBuscaPaciente();
        atualizarEstatisticas();
        mostrarEstadoVazio();
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
        pacientesDisponiveis.clear();
        if (!(ServiceRoleContext.getCurrentUser() instanceof Doctor)) {
            return;
        }

        Doctor medico = (Doctor) ServiceRoleContext.getCurrentUser();
        Map<Long, Patient> pacientesUnicos = new LinkedHashMap<>();

        try {
            ConsultationServiceProxy consultationService = new ConsultationServiceProxy();
            for (Consultation consultation : consultationService.findByDoctor(medico)) {
                if (consultaAtiva(consultation) && consultation.getPatient() != null) {
                    pacientesUnicos.putIfAbsent(consultation.getPatient().getId(), consultation.getPatient());
                }
            }
        } catch (Exception exception) {
            // leave empty when consultations cannot be loaded
        }

        List<Patient> pacientesOrdenados = new ArrayList<>(pacientesUnicos.values());
        pacientesOrdenados.sort(Comparator.comparing(Patient::getName, String.CASE_INSENSITIVE_ORDER));
        pacientesDisponiveis.setAll(pacientesOrdenados);
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

                Label cpf = new Label("CPF: " + formatarCpf(patient.getCPF()));
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
            onPacienteSelecionado(newVal);
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
                pacienteSelecionado = null;
                lblPacienteSelecionado.setText("Nenhum paciente selecionado");
                mostrarEstadoVazio();
            }
        });
    }

    private void atualizarEstatisticas() {
        lblTotalPacientes.setText(String.valueOf(pacientesDisponiveis.size()));

        int totalProntuarios = 0;
        for (Patient p : pacientesDisponiveis) {
            try {
                MedicalRecord record = medicalRecordService.findByPatient(p);
                if (record != null) {
                    totalProntuarios++;
                }
            } catch (Exception e) {
                // Paciente sem prontuário
            }
        }
        lblTotalProntuarios.setText(String.valueOf(totalProntuarios));
        lblProntuariosSelecionados.setText("0");
    }

    private void onPacienteSelecionado(Patient patient) {
        boxProntuarios.getChildren().clear();

        if (patient == null) {
            mostrarEstadoVazio();
            lblProntuariosSelecionados.setText("0");
            return;
        }

        try {
            MedicalRecord record = medicalRecordService.findByPatient(patient);
            
            if (record == null) {
                Label vazio = new Label("Nenhum prontuário cadastrado para " + patient.getName() + ".");
                vazio.getStyleClass().add("medico-empty-state-title");
                boxProntuarios.getChildren().add(vazio);
                boxProntuarios.setAlignment(javafx.geometry.Pos.CENTER);
                lblProntuariosSelecionados.setText("0");
            } else {
                Label titulo = new Label("Prontuário de " + patient.getName());
                titulo.getStyleClass().add("medico-panel-title");
                
                Label editor = new Label("Editado por: " + nomeDoEditor(record));
                editor.getStyleClass().add("medico-record-editor");

                Label data = new Label("Última edição: " + formatarData(record.getDate()));
                data.getStyleClass().add("medico-record-date");

                Label observacaoTitulo = new Label("Observação clínica");
                observacaoTitulo.getStyleClass().add("medico-record-section-title");

                Label observacao = new Label(record.getObservation());
                observacao.getStyleClass().add("medico-record-observation");
                observacao.setWrapText(true);

                VBox card = new VBox(8, titulo, editor, data, observacaoTitulo, observacao);
                card.getStyleClass().add("medico-record-card");
                card.setPadding(new Insets(18, 18, 18, 18));

                boxProntuarios.getChildren().add(card);
                boxProntuarios.setAlignment(javafx.geometry.Pos.TOP_LEFT);
                lblProntuariosSelecionados.setText("1");
            }
        } catch (Exception e) {
            Label erro = new Label("Erro ao carregar prontuário: " + e.getMessage());
            erro.getStyleClass().add("medico-empty-state-title");
            boxProntuarios.getChildren().add(erro);
            boxProntuarios.setAlignment(javafx.geometry.Pos.CENTER);
            lblProntuariosSelecionados.setText("0");
        }
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf == null ? "" : cpf;
        }

        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    private void mostrarEstadoVazio() {
        boxProntuarios.getChildren().clear();
        boxProntuarios.setAlignment(javafx.geometry.Pos.CENTER);
        boxProntuarios.setPadding(new Insets(40, 20, 40, 20));

        StackPane icone = new StackPane();
        Circle circulo = new Circle(26);
        circulo.getStyleClass().add("medico-empty-state-icon-circle");
        Label glyph = new Label("👤");
        glyph.setStyle("-fx-font-size: 20px; -fx-text-fill: #c1c5cc;");
        icone.getChildren().addAll(circulo, glyph);

        Label texto = new Label("Selecione um paciente acima para visualizar seus prontuários");
        texto.getStyleClass().add("medico-empty-state-title");

        boxProntuarios.getChildren().addAll(icone, texto);
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

    private String formatarData(java.time.LocalDate date) {
        if (date == null) {
            return "--/--/----";
        }

        return date.format(FORMATO_DATA);
    }

    private boolean consultaAtiva(Consultation consultation) {
        if (consultation == null || consultation.getStatus() == null) {
            return false;
        }

        return "SCHEDULED".equalsIgnoreCase(consultation.getStatus());
    }

    private String nomeDoEditor(MedicalRecord record) {
        if (record == null || record.getDoctor() == null || record.getDoctor().getName() == null || record.getDoctor().getName().isBlank()) {
            return "Médico não informado";
        }

        return "Dr. " + record.getDoctor().getName();
    }
}