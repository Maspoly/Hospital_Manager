package br.edu.ufersa.hospital_manager.controllers;

import br.edu.ufersa.hospital_manager.model.DAO.*;
import br.edu.ufersa.hospital_manager.model.entities.*;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CadastroPacienteController {

    // ── Campos do formulário ──────────────────────────────────────────────────
    @FXML private TextField        fldNome;
    @FXML private TextField        fldCpf;
    @FXML private DatePicker       fldDataNascimento;
    @FXML private ComboBox<String> fldSexo;
    @FXML private ComboBox<String> fldEstadoCivil;
    @FXML private TextField        fldTelefone;
    @FXML private TextField        fldEmail;

    // Endereço separado em campos para montar o objeto Address corretamente
    @FXML private TextField        fldRua;
    @FXML private TextField        fldNumero;
    @FXML private TextField        fldBairro;
    @FXML private TextField        fldCidade;
    @FXML private TextField        fldEstado;
    @FXML private TextField        fldCep;

    @FXML private ComboBox<String> fldTipoSanguineo;
    @FXML private ComboBox<String> fldConvenio;
    @FXML private TextArea         fldAlergias;
    @FXML private TextArea         fldObservacoes;
    @FXML private Label            lblErro;

    // ── Labels do usuário logado (sidebar) ───────────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;

    // Acesso direto ao DAO pois PatientService.registerPatient() exige
    // um Patient já construído, mas o Address precisa existir no banco antes.
    private final PatientDAO patientDAO = new PatientDAO();
    private final AddressDAO addressDAO = new AddressDAO();

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        fldSexo.setItems(FXCollections.observableArrayList(
                "Masculino", "Feminino", "Outro", "Prefiro não informar"
        ));

        fldEstadoCivil.setItems(FXCollections.observableArrayList(
                "Solteiro(a)", "Casado(a)", "Divorciado(a)", "Viúvo(a)", "União estável"
        ));

        fldTipoSanguineo.setItems(FXCollections.observableArrayList(
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        ));

        fldConvenio.setItems(FXCollections.observableArrayList(
                "Particular", "Unimed", "Bradesco Saúde", "Amil",
                "SulAmérica", "Hapvida", "NotreDame Intermédica", "Outro"
        ));
    }

    // ── Ação: Salvar ──────────────────────────────────────────────────────────
    @FXML
    private void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
            // 1. Persiste o endereço e obtém o ID gerado pelo banco
            Address endereco = new Address(
                    fldRua.getText().trim(),
                    fldNumero.getText().trim(),
                    fldBairro.getText().trim(),
                    fldCidade.getText().trim(),
                    fldEstado.getText().trim()
            );
            addressDAO.create(endereco);

            // 2. Remove pontuação do CPF (entidade espera exatamente 11 dígitos)
            String cpfLimpo = fldCpf.getText().trim().replaceAll("[^0-9]", "");

            // 3. Cria e persiste o paciente
            Patient paciente = new Patient(
                    fldNome.getText().trim(),
                    cpfLimpo,
                    endereco
            );
            patientDAO.create(paciente);

            NavigationHelper.showInfo("Sucesso", "Paciente cadastrado com sucesso!");
            NavigationHelper.goTo((javafx.scene.Node) event.getSource(), "pacientes.fxml");

        } catch (RuntimeException e) {
            // Captura validações das entidades (CPF inválido, nome vazio, etc.)
            mostrarErro(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro no banco de dados: " + e.getMessage());
        }
    }

    // ── Ação: Cancelar ────────────────────────────────────────────────────────
    @FXML
    private void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((javafx.scene.Node) event.getSource(), "pacientes.fxml");
    }

    // ── Validação ─────────────────────────────────────────────────────────────
    private boolean validar() {
        List<String> erros = new ArrayList<>();

        if (fldNome.getText().isBlank())         erros.add("Nome completo é obrigatório.");
        if (fldCpf.getText().isBlank())          erros.add("CPF é obrigatório.");
        if (fldDataNascimento.getValue() == null) erros.add("Data de nascimento é obrigatória.");
        if (fldSexo.getValue() == null)          erros.add("Sexo é obrigatório.");
        if (fldTelefone.getText().isBlank())     erros.add("Telefone é obrigatório.");
        if (fldRua.getText().isBlank())          erros.add("Rua é obrigatória.");
        if (fldNumero.getText().isBlank())       erros.add("Número é obrigatório.");
        if (fldBairro.getText().isBlank())       erros.add("Bairro é obrigatório.");
        if (fldCidade.getText().isBlank())       erros.add("Cidade é obrigatória.");
        if (fldEstado.getText().isBlank())       erros.add("Estado é obrigatório.");

        // Valida CPF: deve ter 11 dígitos após limpeza
        String cpfLimpo = fldCpf.getText().replaceAll("[^0-9]", "");
        if (!fldCpf.getText().isBlank() && cpfLimpo.length() != 11)
            erros.add("CPF deve conter 11 dígitos numéricos.");

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

    // ── Navegação da sidebar ──────────────────────────────────────────────────
    @FXML private void onDashboard(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "dashboard.fxml"); }
    @FXML private void onMedicos(ActionEvent e)    { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "medicos.fxml"); }
    @FXML private void onPacientes(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "pacientes.fxml"); }
    @FXML private void onConsultas(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "consultas.fxml"); }
    @FXML private void onBusca(ActionEvent e)      { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "busca.fxml"); }
    @FXML private void onRelatorios(ActionEvent e) { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "relatorios.fxml"); }
}
