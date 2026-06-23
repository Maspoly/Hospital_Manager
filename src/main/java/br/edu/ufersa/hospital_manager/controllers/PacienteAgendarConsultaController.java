package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class PacienteAgendarConsultaController {

    // ── Campos do formulário ──────────────────────────────────────────────────
    @FXML private ComboBox<Doctor> cmbMedico;
    @FXML private ComboBox<String> cmbEspecialidade;
    @FXML private DatePicker dateData;
    @FXML private ComboBox<String> cmbHora;
    @FXML private TextArea txtObservacoes;
    @FXML private Label lblErro;

    // ── Labels do perfil ─────────────────────────────────────────────────────
    @FXML private Label lblIniciais;
    @FXML private Label lblNomePaciente;
    @FXML private Label lblCpfPaciente;

    private final ConsultationServiceProxy consultationService = new ConsultationServiceProxy();
    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();

    private Patient pacienteLogado;
    
    // Listas para controle da busca
    private ObservableList<Doctor> allDoctors = FXCollections.observableArrayList();

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        carregarDadosPaciente();
        carregarMedicos();
        carregarHorarios();
        configurarBuscaMedicosEmTempoReal();
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
            allDoctors.setAll(medicos);
            
            // Inicialmente mostra todos os médicos
            cmbMedico.setItems(allDoctors);

            cmbMedico.setConverter(new StringConverter<>() {
                @Override
                public String toString(Doctor doctor) {
                    return doctor == null ? "" : "Dr. " + doctor.getName() + " — " + doctor.getCouncilCode();
                }
                @Override
                public Doctor fromString(String string) {
                    return null;
                }
            });

            cmbMedico.setPromptText("Digite para buscar médico...");

        } catch (SQLException e) {
            mostrarErro("Erro ao carregar médicos: " + e.getMessage());
        }
    }

    // ── CONFIGURAÇÃO DA BUSCA EM TEMPO REAL ──────────────────────────────────

    private void configurarBuscaMedicosEmTempoReal() {
        cmbMedico.setEditable(true);
        
        TextField editor = cmbMedico.getEditor();
        editor.textProperty().addListener((obs, oldVal, newVal) -> {
            // Salva o valor selecionado atual
            Doctor currentSelection = cmbMedico.getValue();
            
            // Aplica o filtro
            aplicarFiltro(newVal);
            
            // Tenta restaurar a seleção se o item ainda estiver na lista
            if (currentSelection != null && cmbMedico.getItems().contains(currentSelection)) {
                cmbMedico.setValue(currentSelection);
            } else if (cmbMedico.getItems().size() > 0) {
                // Se o item selecionado não está mais na lista, limpa a seleção
                cmbMedico.setValue(null);
            }
        });
        
        cmbMedico.setOnAction(event -> {
            Doctor selected = cmbMedico.getValue();
            if (selected != null) {
                cmbMedico.getEditor().setText("Dr. " + selected.getName() + " — " + selected.getCouncilCode());
            }
        });
    }

    private void aplicarFiltro(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            // Mostra todos os médicos
            cmbMedico.setItems(allDoctors);
            return;
        }
        
        String searchTerm = termo.trim().toLowerCase();
        
        List<Doctor> filtered = allDoctors.stream()
                .filter(d -> d.getName().toLowerCase().contains(searchTerm) ||
                            d.getCouncilCode().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        
        // Cria uma nova lista com os resultados filtrados
        ObservableList<Doctor> filteredList = FXCollections.observableArrayList(filtered);
        cmbMedico.setItems(filteredList);
        
        // Mostrar o dropdown automaticamente se houver resultados
        if (!filtered.isEmpty()) {
            cmbMedico.show();
        }
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
            Doctor medicoSelecionado = cmbMedico.getValue();
            LocalTime hora = LocalTime.parse(cmbHora.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
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

        if (cmbMedico.getValue() == null) {
            erros.add("Selecione um médico.");
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