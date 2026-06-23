package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class PacienteAgendarConsultaController {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    // ── Campos do formulário ──────────────────────────────────────────────────
    @FXML private Label lblMedicoSelecionado;
    @FXML private DatePicker dateData;
    @FXML private ComboBox<String> cmbHora;
    @FXML private TextArea txtObservacoes;
    @FXML private Label lblErro;

    // ── Componentes de busca ──────────────────────────────────────────────────
    @FXML private TextField txtBuscarMedico;
    @FXML private ListView<Doctor> lstMedicos;

    // ── Labels do perfil ─────────────────────────────────────────────────────
    @FXML private Label lblIniciais;
    @FXML private Label lblNomePaciente;
    @FXML private Label lblCpfPaciente;

    private final ConsultationServiceProxy consultationService = new ConsultationServiceProxy();
    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();

    private Patient pacienteLogado;
    private Doctor medicoSelecionado = null;
    
    // Listas de médicos
    private final ObservableList<Doctor> medicosDisponiveis = FXCollections.observableArrayList();
    private final FilteredList<Doctor> medicosFiltrados = new FilteredList<>(medicosDisponiveis, doctor -> true);

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        carregarDadosPaciente();
        carregarMedicos();
        carregarHorarios();
        configurarListaMedicos();
        configurarBuscaMedico();
        dateData.setValue(LocalDate.now().plusDays(1));
    }

    private void carregarDadosPaciente() {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        if (usuario instanceof Patient && role == ServiceRole.PATIENT) {
            pacienteLogado = (Patient) usuario;
            lblIniciais.setText(extrairIniciais(pacienteLogado.getName()));
            lblNomePaciente.setText(pacienteLogado.getName());
            lblCpfPaciente.setText("CPF: " + formatarCpf(pacienteLogado.getCPF()));
        } else {
            lblIniciais.setText("P");
            lblNomePaciente.setText("Paciente Teste");
            lblCpfPaciente.setText("CPF: 000.000.000-00");
        }
    }

    private void carregarMedicos() {
        try {
            List<Doctor> medicos = doctorService.listAll();
            medicosDisponiveis.setAll(medicos);
            lstMedicos.setItems(medicosFiltrados);
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar médicos: " + e.getMessage());
        }
    }

    private void configurarListaMedicos() {
        lstMedicos.setPlaceholder(new Label("Nenhum médico encontrado."));
        lstMedicos.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Doctor doctor, boolean empty) {
                super.updateItem(doctor, empty);

                if (empty || doctor == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label nome = new Label("Dr. " + doctor.getName());
                nome.getStyleClass().add("paciente-patient-cell-name");

                Label crm = new Label("CRM: " + doctor.getCouncilCode());
                crm.getStyleClass().add("paciente-patient-cell-detail");

                VBox conteudo = new VBox(2, nome, crm);
                conteudo.getStyleClass().add("paciente-patient-cell");

                setText(null);
                setGraphic(conteudo);
            }
        });

        // Quando clicar em um médico, seleciona ele
        lstMedicos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                medicoSelecionado = newVal;
                lblMedicoSelecionado.setText("Dr. " + newVal.getName() + " — " + newVal.getCouncilCode());
                lblMedicoSelecionado.setStyle("-fx-text-fill: #1d4ed8; -fx-font-weight: bold;");
            }
        });
    }

    private void configurarBuscaMedico() {
        txtBuscarMedico.textProperty().addListener((obs, oldVal, newVal) -> {
            String termo = newVal == null ? "" : newVal.trim().toLowerCase(Locale.ROOT);
            
            medicosFiltrados.setPredicate(doctor -> {
                if (termo.isBlank()) {
                    return true;
                }
                
                boolean nomeOk = doctor.getName() != null && 
                               doctor.getName().toLowerCase(Locale.ROOT).contains(termo);
                boolean crmOk = doctor.getCouncilCode() != null && 
                              doctor.getCouncilCode().toLowerCase(Locale.ROOT).contains(termo);
                
                return nomeOk || crmOk;
            });
        });
    }

    private void carregarHorarios() {
        List<String> horarios = new ArrayList<>();
        LocalTime hora = LocalTime.of(7, 0);
        LocalTime fim = LocalTime.of(19, 30);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        while (!hora.isAfter(fim)) {
            horarios.add(hora.format(fmt));
            hora = hora.plusMinutes(30);
        }
        cmbHora.setItems(FXCollections.observableArrayList(horarios));
        cmbHora.setPromptText("Selecione...");
    }

    // ── Ação: Salvar ──────────────────────────────────────────────────────────
    @FXML
    private void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
            LocalTime hora = LocalTime.parse(cmbHora.getValue(), FORMATO_HORA);
            LocalDateTime dataHora = LocalDateTime.of(dateData.getValue(), hora);
            
            Consultation consulta = new Consultation(
                    pacienteLogado,
                    medicoSelecionado,
                    dataHora,
                    "SCHEDULED"
            );

            consultationService.createConsultation(consulta);

            NavigationHelper.showInfo("Sucesso", "Consulta agendada com sucesso!");
            NavigationHelper.goTo((Node) event.getSource(), "paciente_consultas.fxml", "paciente.css");

        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro no banco de dados: " + e.getMessage());
        }
    }

    // ── Ação: Cancelar ────────────────────────────────────────────────────────
    @FXML
    private void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_consultas.fxml", "paciente.css");
    }

    // ── Validação ─────────────────────────────────────────────────────────────
    private boolean validar() {
        List<String> erros = new ArrayList<>();

        if (medicoSelecionado == null) {
            erros.add("Selecione um médico na lista lateral.");
        }
        if (dateData.getValue() == null) {
            erros.add("Selecione a data da consulta.");
        }
        if (cmbHora.getValue() == null) {
            erros.add("Selecione o horário da consulta.");
        }

        if (dateData.getValue() != null && dateData.getValue().isBefore(LocalDate.now())) {
            erros.add("Não é possível agendar uma consulta para uma data passada.");
        }

        if (!erros.isEmpty()) {
            mostrarErro(String.join("\n", erros));
            return false;
        }
        ocultarErro();
        return true;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        lblErro.setVisible(true);
        lblErro.setManaged(true);
    }

    private void ocultarErro() {
        lblErro.setVisible(false);
        lblErro.setManaged(false);
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf == null ? "" : cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
    }

    private String extrairIniciais(String nome) {
        if (nome == null || nome.isBlank()) {
            return "P";
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
        return iniciais.length() > 0 ? iniciais.toString() : "P";
    }

    // ── Navegação da sidebar ──────────────────────────────────────────────────
    @FXML private void goDashboard(ActionEvent e) {
        NavigationHelper.goTo((Node) e.getSource(), "paciente_dashboard.fxml", "paciente.css");
    }

    @FXML private void goProntuarios(ActionEvent e) {
        NavigationHelper.goTo((Node) e.getSource(), "paciente_prontuarios.fxml", "paciente.css");
    }

    @FXML private void goConsultas(ActionEvent e) {
        NavigationHelper.goTo((Node) e.getSource(), "paciente_consultas.fxml", "paciente.css");
    }

    @FXML private void goEditarDados(ActionEvent e) {
        NavigationHelper.goTo((Node) e.getSource(), "paciente_editar_dados.fxml", "paciente.css");
    }

    @FXML private void onSair(ActionEvent e) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) e.getSource(), "login.fxml");
    }
}