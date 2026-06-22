package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import br.edu.ufersa.hospital_manager.model.entities.Consultation;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.ConsultationServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.PatientServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;

public class PacienteAgendarConsultaController {

    @FXML
    private StackPane rootPane;

    @FXML
    private Label lblIniciais;

    @FXML
    private Label lblNomePaciente;

    @FXML
    private Label lblCpfPaciente;

    @FXML
    private ComboBox<Doctor> cmbMedico;

    @FXML
    private ComboBox<String> cmbEspecialidade;

    @FXML
    private DatePicker dateData;

    @FXML
    private ComboBox<String> cmbHora;

    @FXML
    private TextArea txtObservacoes;

    @FXML
    private Label lblErro;

    private Patient pacienteLogado;

    private final PatientServiceProxy patientService = new PatientServiceProxy();
    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();
    private final ConsultationServiceProxy consultationService = new ConsultationServiceProxy();

    @FXML
    public void initialize() {
        carregarPacienteLogado();
        carregarMedicos();
        carregarHorarios();
        carregarEspecialidades();
        dateData.setValue(LocalDate.now().plusDays(1));
    }

    /**
     * Carrega o paciente logado a partir do ServiceRoleContext
     */
    private void carregarPacienteLogado() {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        if (usuario instanceof Patient && role == ServiceRole.PATIENT) {
            pacienteLogado = (Patient) usuario;
            atualizarDadosPaciente();
        } else {
            // Fallback: tenta buscar pelo CPF mock (apenas para teste)
            try {
                pacienteLogado = patientService.findByCPF("11122233344");
                if (pacienteLogado != null) {
                    atualizarDadosPaciente();
                } else {
                    mostrarDadosVazios();
                }
            } catch (SQLException e) {
                mostrarDadosVazios();
            }
        }
    }

    private void atualizarDadosPaciente() {
        if (pacienteLogado == null) {
            mostrarDadosVazios();
            return;
        }

        String nome = pacienteLogado.getName();
        String[] partes = nome.split(" ");
        StringBuilder iniciais = new StringBuilder();
        for (String parte : partes) {
            if (!parte.isEmpty()) {
                iniciais.append(Character.toUpperCase(parte.charAt(0)));
            }
            if (iniciais.length() >= 2) {
                break;
            }
        }

        lblIniciais.setText(iniciais.length() > 0 ? iniciais.toString() : "P");
        lblNomePaciente.setText(nome);
        lblCpfPaciente.setText("CPF: " + formatarCpf(pacienteLogado.getCPF()));
    }

    private void mostrarDadosVazios() {
        lblIniciais.setText("P");
        lblNomePaciente.setText("Paciente");
        lblCpfPaciente.setText("CPF: ---");
    }

    private void carregarEspecialidades() {
        cmbEspecialidade.setItems(FXCollections.observableArrayList(
                "Clínica Geral", "Cardiologia", "Dermatologia",
                "Ginecologia", "Neurologia", "Ortopedia",
                "Pediatria", "Psiquiatria", "Urologia"
        ));
    }

    private void carregarMedicos() {
        try {
            List<Doctor> medicos = doctorService.listAll();
            if (medicos != null && !medicos.isEmpty()) {
                cmbMedico.setItems(FXCollections.observableArrayList(medicos));
            }
        } catch (Exception e) {
            // Lista vazia
        }

        cmbMedico.setConverter(new StringConverter<Doctor>() {
            @Override
            public String toString(Doctor doctor) {
                return doctor == null ? "" : "Dr. " + doctor.getName() + " - CRM " + doctor.getCouncilCode();
            }

            @Override
            public Doctor fromString(String string) {
                return null;
            }
        });
        cmbMedico.setPromptText("Selecione um médico");
    }

    private void carregarHorarios() {
        List<String> horarios = new ArrayList<>();
        LocalTime hora = LocalTime.of(7, 0);
        LocalTime fim = LocalTime.of(19, 0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        while (!hora.isAfter(fim)) {
            horarios.add(hora.format(fmt));
            hora = hora.plusMinutes(30);
        }
        cmbHora.setItems(FXCollections.observableArrayList(horarios));
        cmbHora.setPromptText("Selecione um horário");
    }

    @FXML
    public void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
            Doctor medico = cmbMedico.getValue();
            LocalDate data = dateData.getValue();
            LocalTime hora = LocalTime.parse(cmbHora.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalDateTime dateTime = LocalDateTime.of(data, hora);

            Consultation consulta = new Consultation(
                    pacienteLogado,
                    medico,
                    dateTime,
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

    @FXML
    public void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_consultas.fxml", "paciente.css");
    }

    private boolean validar() {
        List<String> erros = new ArrayList<>();

        if (cmbMedico.getValue() == null) {
            erros.add("Selecione um médico.");
        }
        if (dateData.getValue() == null) {
            erros.add("Selecione a data da consulta.");
        }
        if (dateData.getValue() != null && dateData.getValue().isBefore(LocalDate.now())) {
            erros.add("A data da consulta não pode ser no passado.");
        }
        if (cmbHora.getValue() == null) {
            erros.add("Selecione o horário da consulta.");
        }

        if (!erros.isEmpty()) {
            mostrarErro(String.join("\n", erros));
            return false;
        }
        ocultarErro();
        return true;
    }

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

    // ===================== NAVEGAÇÃO =====================

    @FXML
    public void goDashboard(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_dashboard.fxml", "paciente.css");
    }

    @FXML
    public void goProntuarios(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_prontuarios.fxml", "paciente.css");
    }

    @FXML
    public void goConsultas(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_consultas.fxml", "paciente.css");
    }

    @FXML
    public void goEditarDados(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_editar_dados.fxml", "paciente.css");
    }

    @FXML
    public void onSair(ActionEvent event) {
        ServiceRoleContext.clear();
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
    }
}