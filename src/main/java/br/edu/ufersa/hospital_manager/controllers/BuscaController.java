package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class BuscaController {

    private static final DateTimeFormatter DATA_HORA_FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private ComboBox<String> cmbBuscarPor;

    @FXML
    private ComboBox<String> cmbCriterio;

    @FXML
    private TextField txtTermo;

    @FXML
    private Label lblResultados;

    @FXML
    private VBox boxResultados;

    @FXML
    private Label lblUserName;

    @FXML
    private Label lblUserRole;

    @FXML
    private Label lblVisualizarPerfil;

    private final PatientServiceProxy patientService = (PatientServiceProxy) ProxyFactory.createProxy("PATIENT");
    private final DoctorServiceProxy doctorService = (DoctorServiceProxy) ProxyFactory.createProxy("DOCTOR");
    private final ConsultationServiceProxy consultationService = (ConsultationServiceProxy) ProxyFactory.createProxy("CONSULTATION");

    // Dados mock para simular a busca
    private final List<Patient> pacientesMock = new ArrayList<>();
    private final List<Doctor> medicosMock = new ArrayList<>();

    @FXML
    public void initialize() {
        carregarDadosUsuario();
        cmbBuscarPor.getItems().setAll("Paciente", "Médico", "Consulta");
        cmbBuscarPor.setValue("Paciente");

        cmbBuscarPor.valueProperty().addListener((obs, oldVal, newVal) -> atualizarCriterios(newVal));
        cmbCriterio.valueProperty().addListener((obs, oldVal, newVal) -> atualizarTermoPlaceholder());

        atualizarCriterios(cmbBuscarPor.getValue());
        configurarLinkPerfil();

        carregarDadosMock();
    }

    /**
     * Preenche os dados do usuário logado na sidebar.
     */
    private void carregarDadosUsuario() {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        String nomeUsuario = usuario != null ? usuario.getName() : "Administrador";
        String cargoUsuario = role != null ? role.getDisplayName() : "Gerente";

        lblUserName.setText(nomeUsuario);
        lblUserRole.setText(cargoUsuario);
    }

    private void configurarLinkPerfil() {
        if (lblVisualizarPerfil != null) {
            lblVisualizarPerfil.setStyle("-fx-cursor: hand; -fx-text-fill: #60a5fa; -fx-underline: true;");
            lblVisualizarPerfil.setOnMouseClicked(this::onVisualizarPerfil);
        }
    }

    @FXML
    private void onVisualizarPerfil(MouseEvent event) {
        // Usa o próprio label como referência para navegação
        NavigationHelper.goTo(lblVisualizarPerfil, "perfil_gerente.fxml");
    }

    private void atualizarCriterios(String buscarPor) {
        cmbCriterio.getItems().clear();
        if ("Consulta".equals(buscarPor)) {
            cmbCriterio.getItems().addAll("Paciente", "Médico", "Data e hora");
        } else if ("Médico".equals(buscarPor)) {
            cmbCriterio.getItems().addAll("Nome", "CPF", "Código Conselho");
        } else {
            cmbCriterio.getItems().addAll("Nome", "CPF");
        }
        cmbCriterio.setValue(cmbCriterio.getItems().get(0));
        atualizarTermoPlaceholder();
    }

    private void atualizarTermoPlaceholder() {
        String criterio = cmbCriterio.getValue();
        if (criterio == null) {
            txtTermo.setPromptText("Digite para buscar...");
            return;
        }

        switch (criterio) {
            case "CPF" -> txtTermo.setPromptText("00000000000 ou 000.000.000-00");
            case "Código Conselho" -> txtTermo.setPromptText("123456");
            case "Data e hora" -> txtTermo.setPromptText("dd/MM/aaaa HH:mm");
            case "Paciente" -> txtTermo.setPromptText("Nome do paciente");
            case "Médico" -> txtTermo.setPromptText("Nome do médico");
            default -> txtTermo.setPromptText("Digite para buscar...");
        }
    }

    private void carregarDadosMock() {
        Address endereco1 = new Address("Rua das Flores", "50", "Centro", "Mossoró", "RN");
        pacientesMock.add(new Patient("Maria Santos", "11122233344", endereco1));

        Address endereco2 = new Address("Av. Central", "200", "Centro", "Mossoró", "RN");
        pacientesMock.add(new Patient("João Oliveira", "55566677788", endereco2));

        Address enderecoMedico = new Address("Av. Principal", "100", "Centro", "Mossoró", "RN");
        medicosMock.add(new Doctor("Luiz Silva", "12345678900", enderecoMedico, 250.0f, "123456"));
    }

    @FXML
    public void onBuscar(ActionEvent event) {
        String termo = txtTermo.getText() == null ? "" : txtTermo.getText().trim().toLowerCase();
        String buscarPor = cmbBuscarPor.getValue();
        String criterio = cmbCriterio.getValue();

        boxResultados.getChildren().clear();
        List<SearchResultItem> results = buscar(buscarPor, criterio, termo);

        lblResultados.setText("Resultados da Busca (" + results.size() + ")");

        if (results.isEmpty()) {
            Label vazio = new Label("Nenhum resultado encontrado para os filtros selecionados.");
            vazio.getStyleClass().add("empty-state-title");
            boxResultados.getChildren().add(vazio);
            return;
        }

        for (SearchResultItem result : results) {
            boxResultados.getChildren().add(criarCartaoResultado(result));
        }
    }

    private List<SearchResultItem> buscar(String buscarPor, String criterio, String termo) {
        List<SearchResultItem> resultados = new ArrayList<>();

        if (buscarPor == null || criterio == null) {
            return resultados;
        }

        if ("Paciente".equals(buscarPor)) {
            adicionarResultadoPaciente(resultados, criterio, termo);
        } else if ("Médico".equals(buscarPor)) {
            adicionarResultadoMedico(resultados, criterio, termo);
        } else if ("Consulta".equals(buscarPor)) {
            adicionarResultadoConsulta(resultados, criterio, termo);
        }

        return resultados;
    }

    private void adicionarResultadoPaciente(List<SearchResultItem> resultados, String criterio, String termo) {
        try {
            if ("Nome".equals(criterio)) {
                Patient patient = buscarPacientePorNomeOuFallback(termo);
                if (patient != null) {
                    resultados.add(criarPacienteResultado(patient));
                }
                return;
            }

            if ("CPF".equals(criterio)) {
                Patient patient = patientService.findByCPF(normalizarCpf(termo));
                if (patient != null) {
                    resultados.add(criarPacienteResultado(patient));
                }
            }
        } catch (Exception exception) {
            // fallback handled below
        }

        for (Patient patient : carregarPacientes()) {
            boolean matches = "Nome".equals(criterio) && patient.getName().toLowerCase().contains(termo)
                    || "CPF".equals(criterio) && normalizarCpf(patient.getCPF()).contains(normalizarCpf(termo));
            if (matches) {
                resultados.add(criarPacienteResultado(patient));
            }
        }
    }

    private void adicionarResultadoMedico(List<SearchResultItem> resultados, String criterio, String termo) {
        try {
            if ("Nome".equals(criterio)) {
                Doctor doctor = buscarMedicoPorNomeOuFallback(termo);
                if (doctor != null) {
                    resultados.add(criarMedicoResultado(doctor));
                }
                return;
            }

            if ("CPF".equals(criterio)) {
                Doctor doctor = doctorService.findByCPF(normalizarCpf(termo));
                if (doctor != null) {
                    resultados.add(criarMedicoResultado(doctor));
                }
                return;
            }

            if ("Código Conselho".equals(criterio)) {
                Doctor doctor = buscarMedicoPorConselhoOuFallback(termo);
                if (doctor != null) {
                    resultados.add(criarMedicoResultado(doctor));
                }
            }
        } catch (Exception exception) {
            // fallback handled below
        }

        for (Doctor doctor : carregarMedicos()) {
            boolean matches = "Nome".equals(criterio) && doctor.getName().toLowerCase().contains(termo)
                    || "CPF".equals(criterio) && normalizarCpf(doctor.getCPF()).contains(normalizarCpf(termo))
                    || "Código Conselho".equals(criterio) && doctor.getCouncilCode().contains(termo.replaceAll("[^0-9]", ""));
            if (matches) {
                resultados.add(criarMedicoResultado(doctor));
            }
        }
    }

    private void adicionarResultadoConsulta(List<SearchResultItem> resultados, String criterio, String termo) {
        try {
            if ("Paciente".equals(criterio)) {
                Patient patient = buscarPacientePorNomeOuFallback(termo);
                if (patient != null) {
                    for (Consultation consultation : consultationService.findByPatient(patient)) {
                        resultados.add(criarConsultaResultado(consultation));
                    }
                }
                return;
            }

            if ("Médico".equals(criterio)) {
                Doctor doctor = buscarMedicoPorNomeOuFallback(termo);
                if (doctor != null) {
                    for (Consultation consultation : consultationService.findByDoctor(doctor)) {
                        resultados.add(criarConsultaResultado(consultation));
                    }
                }
                return;
            }

            if ("Data e hora".equals(criterio)) {
                LocalDateTime dateTime = LocalDateTime.parse(txtTermo.getText().trim(), DATA_HORA_FORMATO);
                for (Consultation consultation : consultationService.findByDateTime(dateTime)) {
                    resultados.add(criarConsultaResultado(consultation));
                }
                return;
            }
        } catch (DateTimeParseException exception) {
            NavigationHelper.showError("Use o formato dd/MM/aaaa HH:mm para buscar por data e hora.");
            return;
        } catch (Exception exception) {
            // fallback handled below
        }

        for (Consultation consultation : carregarConsultas()) {
            boolean matches = "Paciente".equals(criterio) && consultation.getPatient().getName().toLowerCase().contains(termo)
                    || "Médico".equals(criterio) && consultation.getDoctor().getName().toLowerCase().contains(termo)
                    || "Data e hora".equals(criterio) && consultation.getDateTime().format(DATA_HORA_FORMATO).toLowerCase().contains(termo);
            if (matches) {
                resultados.add(criarConsultaResultado(consultation));
            }
        }
    }

    private Patient buscarPacientePorNomeOuFallback(String termo) {
        try {
            return patientService.findByName(termo);
        } catch (Exception exception) {
            for (Patient patient : carregarPacientes()) {
                if (patient.getName().toLowerCase().contains(termo)) {
                    return patient;
                }
            }
            return null;
        }
    }

    private Doctor buscarMedicoPorNomeOuFallback(String termo) {
        try {
            return doctorService.findByName(termo);
        } catch (Exception exception) {
            for (Doctor doctor : carregarMedicos()) {
                if (doctor.getName().toLowerCase().contains(termo)) {
                    return doctor;
                }
            }
            return null;
        }
    }

    private Doctor buscarMedicoPorConselhoOuFallback(String termo) {
        String normalized = termo == null ? "" : termo.trim().replaceAll("[^0-9]", "");
        try {
            return doctorService.findByCouncilCode(normalized);
        } catch (Exception exception) {
            for (Doctor doctor : carregarMedicos()) {
                if (doctor.getCouncilCode().contains(normalized)) {
                    return doctor;
                }
            }
            return null;
        }
    }

    private List<Patient> carregarPacientes() {
        try {
            return patientService.listAll();
        } catch (SQLException exception) {
            return pacientesMock;
        }
    }

    private List<Doctor> carregarMedicos() {
        try {
            return doctorService.listAll();
        } catch (SQLException exception) {
            return medicosMock;
        }
    }

    private List<Consultation> carregarConsultas() {
        try {
            return consultationService.listAll();
        } catch (SQLException exception) {
            return new ArrayList<>();
        }
    }

    private SearchResultItem criarPacienteResultado(Patient patient) {
        String subtitle = "CPF " + formatarCpf(patient.getCPF()) + " · " + formatarEndereco(patient.getAddress());
        String detail = "Paciente\n\nNome: " + patient.getName() + "\nCPF: " + formatarCpf(patient.getCPF())
                + "\nEndereço: " + formatarEndereco(patient.getAddress());
        return new SearchResultItem("Paciente", patient.getName(), subtitle, detail, "Abrir pacientes", () -> NavigationHelper.goTo(txtTermo, "pacientes.fxml"));
    }

    private SearchResultItem criarMedicoResultado(Doctor doctor) {
        String subtitle = "CRM " + doctor.getCouncilCode() + " · " + formatarEndereco(doctor.getAddress());
        String detail = "Médico\n\nNome: Dr. " + doctor.getName() + "\nCPF: " + formatarCpf(doctor.getCPF())
                + "\nCRM: " + doctor.getCouncilCode() + "\nValor da consulta: " + formatarMoeda(doctor.getConsultationValue())
                + "\nEndereço: " + formatarEndereco(doctor.getAddress());
        return new SearchResultItem("Médico", "Dr. " + doctor.getName(), subtitle, detail, "Abrir médicos", () -> NavigationHelper.goTo(txtTermo, "medicos.fxml"));
    }

    private SearchResultItem criarConsultaResultado(Consultation consultation) {
        String dataTexto = consultation.getDateTime().format(DATA_HORA_FORMATO);
        String pacienteNome = consultation.getPatient() != null ? consultation.getPatient().getName() : "Paciente removido";
        String medicoNome = consultation.getDoctor() != null ? "Dr. " + consultation.getDoctor().getName() : "Médico removido";
        String subtitle = pacienteNome + " / " + medicoNome + " · " + traduzirStatus(consultation.getStatus());
        String detail = "Consulta\n\nPaciente: " + pacienteNome
            + "\nMédico: " + medicoNome
                + "\nData e hora: " + dataTexto
                + "\nStatus: " + traduzirStatus(consultation.getStatus());
        return new SearchResultItem("Consulta", dataTexto, subtitle, detail, "Abrir consultas", () -> NavigationHelper.goTo(txtTermo, "consultas.fxml"));
    }

    private VBox criarCartaoResultado(SearchResultItem result) {
        Label tituloLbl = new Label(result.title);
        tituloLbl.getStyleClass().add("search-result-card-title");

        Label linhaPrincipal = new Label(result.mainText);
        linhaPrincipal.getStyleClass().add("cell-title");

        Label subtituloLbl = new Label(result.subtitle);
        subtituloLbl.getStyleClass().add("cell-subtitle");
        subtituloLbl.setWrapText(true);

        Label actionLbl = new Label("Clique para ver detalhes e abrir a tela relacionada");
        actionLbl.getStyleClass().add("search-result-card-action");

        VBox content = new VBox(2, tituloLbl, linhaPrincipal, subtituloLbl, actionLbl);
        content.setAlignment(Pos.CENTER_LEFT);

        Button card = new Button();
        card.setGraphic(content);
        card.setMaxWidth(Double.MAX_VALUE);
        card.getStyleClass().add("search-result-card");
        card.setOnAction(event -> showResultDetails(result));

        VBox wrapper = new VBox(card);
        wrapper.setPadding(new Insets(0));
        return wrapper;
    }

    private void showResultDetails(SearchResultItem result) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(result.title);
        dialog.setHeaderText(null);

        ButtonType openButton = new ButtonType(result.openLabel, ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(openButton, ButtonType.CANCEL);

        Label title = new Label(result.mainText);
        title.getStyleClass().add("edit-dialog-title");

        Label detail = new Label(result.detail);
        detail.getStyleClass().add("edit-dialog-subtitle");
        detail.setWrapText(true);

        VBox body = new VBox(12, title, detail);
        body.setPadding(new Insets(16, 20, 8, 20));
        dialog.getDialogPane().setContent(body);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/br/edu/ufersa/hospital_manager/css/style.css").toExternalForm());

        Button open = (Button) dialog.getDialogPane().lookupButton(openButton);
        open.getStyleClass().add("btn-accent");
        Button cancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancel.getStyleClass().add("btn-ghost");

        dialog.showAndWait().ifPresent(resultType -> {
            if (resultType == openButton) {
                result.openAction.run();
            }
        });
    }

    private String normalizarCpf(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
    }

    private String formatarCpf(String cpf) {
        String digits = normalizarCpf(cpf);
        if (digits.length() != 11) {
            return cpf;
        }
        return digits.substring(0, 3) + "." + digits.substring(3, 6) + "."
                + digits.substring(6, 9) + "-" + digits.substring(9, 11);
    }

    private String formatarMoeda(float valor) {
        return String.format("R$ %.2f", valor).replace(".", ",");
    }

    private String formatarEndereco(Address address) {
        if (address == null) {
            return "Endereço não informado";
        }
        return address.getStreet() + ", " + address.getNumber() + " - " + address.getCity() + "/" + address.getState();
    }

    private String traduzirStatus(String status) {
        if (status == null) {
            return "Sem status";
        }
        return switch (status.toUpperCase()) {
            case "SCHEDULED" -> "Agendada";
            case "COMPLETED" -> "Concluída";
            case "CANCELED" -> "Cancelada";
            default -> status;
        };
    }

    private static class SearchResultItem {
        private final String title;
        private final String mainText;
        private final String subtitle;
        private final String detail;
        private final String openLabel;
        private final Runnable openAction;

        private SearchResultItem(String title, String mainText, String subtitle, String detail, String openLabel, Runnable openAction) {
            this.title = title;
            this.mainText = mainText;
            this.subtitle = subtitle;
            this.detail = detail;
            this.openLabel = openLabel;
            this.openAction = openAction;
        }
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