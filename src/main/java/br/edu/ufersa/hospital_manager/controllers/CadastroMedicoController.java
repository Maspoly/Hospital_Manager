package br.edu.ufersa.hospital_manager.controllers;

<<<<<<< HEAD
import br.edu.ufersa.hospital_manager.model.DAO.*;
import br.edu.ufersa.hospital_manager.model.entities.*;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

=======
>>>>>>> 96ad7c6 (Linked screens to data base)
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
=======
import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.services.AddressServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

>>>>>>> 96ad7c6 (Linked screens to data base)
public class CadastroMedicoController {

    // ── Campos do formulário ──────────────────────────────────────────────────
    @FXML private TextField        fldNome;
    @FXML private TextField        fldCpf;
<<<<<<< HEAD
    @FXML private DatePicker       fldDataNascimento;
    @FXML private TextField        fldTelefone;
    @FXML private TextField        fldEmail;
=======
    @FXML private PasswordField    fldSenha;
>>>>>>> 96ad7c6 (Linked screens to data base)

    // Endereço separado em campos para montar o objeto Address corretamente
    @FXML private TextField        fldRua;
    @FXML private TextField        fldNumero;
    @FXML private TextField        fldBairro;
    @FXML private TextField        fldCidade;
    @FXML private TextField        fldEstado;

    @FXML private TextField        fldCrm;
<<<<<<< HEAD
    @FXML private ComboBox<String> fldEspecialidade;
    @FXML private TextField        fldValorConsulta;
    @FXML private ComboBox<String> fldTurno;
=======
    @FXML private TextField        fldValorConsulta;
>>>>>>> 96ad7c6 (Linked screens to data base)
    @FXML private Label            lblErro;

    // ── Labels do usuário logado (sidebar) ───────────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;

<<<<<<< HEAD
    // Acesso direto ao DAO pois DoctorService não expõe registerDoctor()
    private final DoctorDAO  doctorDAO  = new DoctorDAO();
    private final AddressDAO addressDAO = new AddressDAO();
=======
    private final DoctorServiceProxy doctorService = new DoctorServiceProxy();
    private final AddressServiceProxy addressService = new AddressServiceProxy();
>>>>>>> 96ad7c6 (Linked screens to data base)

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
<<<<<<< HEAD
        fldEspecialidade.setItems(FXCollections.observableArrayList(
                "Clínica Geral", "Cardiologia", "Dermatologia",
                "Ginecologia", "Neurologia", "Ortopedia",
                "Pediatria", "Psiquiatria", "Urologia", "Outra"
        ));

        fldTurno.setItems(FXCollections.observableArrayList(
                "Manhã", "Tarde", "Noite", "Integral"
        ));
=======
>>>>>>> 96ad7c6 (Linked screens to data base)
    }

    // ── Ação: Salvar ──────────────────────────────────────────────────────────
    @FXML
    private void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
<<<<<<< HEAD
            // 1. Monta e persiste o endereço primeiro (necessário para obter o ID gerado)
=======
>>>>>>> 96ad7c6 (Linked screens to data base)
            Address endereco = new Address(
                    fldRua.getText().trim(),
                    fldNumero.getText().trim(),
                    fldBairro.getText().trim(),
                    fldCidade.getText().trim(),
                    fldEstado.getText().trim()
            );
<<<<<<< HEAD
            addressDAO.create(endereco);

            // 2. Remove pontuação do CPF (banco espera exatamente 11 dígitos)
            String cpfLimpo = fldCpf.getText().trim().replaceAll("[^0-9]", "");

            // 3. Extrai somente os 6 dígitos do CRM (ex.: "CRM 123456/RN" → "123456")
=======
            addressService.create(endereco);

            String cpfLimpo = fldCpf.getText().trim().replaceAll("[^0-9]", "");
>>>>>>> 96ad7c6 (Linked screens to data base)
            String crmLimpo = fldCrm.getText().trim().replaceAll("[^0-9]", "");

            float valorConsulta = Float.parseFloat(
                    fldValorConsulta.getText().trim().replace(",", ".")
            );

<<<<<<< HEAD
            // 4. Cria o médico e persiste no banco
=======
>>>>>>> 96ad7c6 (Linked screens to data base)
            Doctor medico = new Doctor(
                    fldNome.getText().trim(),
                    cpfLimpo,
                    endereco,
<<<<<<< HEAD
                    valorConsulta,
                    crmLimpo
            );
            doctorDAO.create(medico);
=======
                    fldSenha.getText().trim(),
                    valorConsulta,
                    crmLimpo
            );
            doctorService.registerDoctor(medico);
>>>>>>> 96ad7c6 (Linked screens to data base)

            NavigationHelper.showInfo("Sucesso", "Médico cadastrado com sucesso!");
            NavigationHelper.goTo((javafx.scene.Node) event.getSource(), "medicos.fxml");

        } catch (NumberFormatException e) {
            mostrarErro("Valor da consulta inválido. Use o formato: 250,00");
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro no banco de dados: " + e.getMessage());
        }
    }

    // ── Ação: Cancelar ────────────────────────────────────────────────────────
    @FXML
    private void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((javafx.scene.Node) event.getSource(), "medicos.fxml");
    }

    // ── Validação ─────────────────────────────────────────────────────────────
    private boolean validar() {
        List<String> erros = new ArrayList<>();

        if (fldNome.getText().isBlank())          erros.add("Nome completo é obrigatório.");
        if (fldCpf.getText().isBlank())           erros.add("CPF é obrigatório.");
        if (fldCrm.getText().isBlank())           erros.add("CRM / Código Conselho é obrigatório.");
<<<<<<< HEAD
        if (fldEspecialidade.getValue() == null)  erros.add("Selecione a especialidade.");
        if (fldValorConsulta.getText().isBlank()) erros.add("Valor da consulta é obrigatório.");
=======
        if (fldValorConsulta.getText().isBlank()) erros.add("Valor da consulta é obrigatório.");
        if (fldSenha.getText().isBlank())         erros.add("Senha é obrigatória.");
>>>>>>> 96ad7c6 (Linked screens to data base)
        if (fldRua.getText().isBlank())           erros.add("Rua é obrigatória.");
        if (fldNumero.getText().isBlank())        erros.add("Número é obrigatório.");
        if (fldBairro.getText().isBlank())        erros.add("Bairro é obrigatório.");
        if (fldCidade.getText().isBlank())        erros.add("Cidade é obrigatória.");
        if (fldEstado.getText().isBlank())        erros.add("Estado é obrigatório.");

        String cpfLimpo = fldCpf.getText().replaceAll("[^0-9]", "");
        if (!fldCpf.getText().isBlank() && cpfLimpo.length() != 11)
            erros.add("CPF deve conter 11 dígitos numéricos.");

        String crmLimpo = fldCrm.getText().replaceAll("[^0-9]", "");
        if (!fldCrm.getText().isBlank() && crmLimpo.length() != 6)
            erros.add("CRM deve conter exatamente 6 dígitos numéricos (ex.: CRM 123456/RN).");

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
<<<<<<< HEAD
    @FXML private void onDashboard(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "dashboard.fxml"); }
=======
    @FXML private void onDashboard(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "Dashboard.fxml"); }
>>>>>>> 96ad7c6 (Linked screens to data base)
    @FXML private void onMedicos(ActionEvent e)    { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "medicos.fxml"); }
    @FXML private void onPacientes(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "pacientes.fxml"); }
    @FXML private void onConsultas(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "consultas.fxml"); }
    @FXML private void onBusca(ActionEvent e)      { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "busca.fxml"); }
    @FXML private void onRelatorios(ActionEvent e) { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "relatorios.fxml"); }
}
