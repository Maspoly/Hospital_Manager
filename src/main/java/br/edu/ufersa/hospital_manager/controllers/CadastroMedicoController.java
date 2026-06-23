package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.services.AddressServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.util.ProxyFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CadastroMedicoController {

    // ── Campos do formulário ──────────────────────────────────────────────────
    @FXML private TextField        fldNome;
    @FXML private TextField        fldCpf;
    @FXML private PasswordField    fldSenha;

    // Endereço separado em campos para montar o objeto Address corretamente
    @FXML private TextField        fldRua;
    @FXML private TextField        fldNumero;
    @FXML private TextField        fldBairro;
    @FXML private TextField        fldCidade;
    @FXML private TextField        fldEstado;

    @FXML private TextField        fldCrm;
    @FXML private TextField        fldValorConsulta;
    @FXML private Label            lblErro;

    // ── Labels do usuário logado (sidebar) ───────────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;

    private final DoctorServiceProxy doctorService = (DoctorServiceProxy) ProxyFactory.createProxy("DOCTOR");
    private final AddressServiceProxy addressService = (AddressServiceProxy) ProxyFactory.createProxy("ADDRESS");

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
    }

    // ── Ação: Salvar ──────────────────────────────────────────────────────────
    @FXML
    private void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
            Address endereco = new Address(
                    fldRua.getText().trim(),
                    fldNumero.getText().trim(),
                    fldBairro.getText().trim(),
                    fldCidade.getText().trim(),
                    fldEstado.getText().trim()
            );
            addressService.create(endereco);

            String cpfLimpo = fldCpf.getText().trim().replaceAll("[^0-9]", "");
            String crmLimpo = fldCrm.getText().trim().replaceAll("[^0-9]", "");

            float valorConsulta = Float.parseFloat(
                    fldValorConsulta.getText().trim().replace(",", ".")
            );

            Doctor medico = new Doctor(
                    fldNome.getText().trim(),
                    cpfLimpo,
                    endereco,
                    fldSenha.getText().trim(),
                    valorConsulta,
                    crmLimpo
            );
            doctorService.registerDoctor(medico);

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
        if (fldValorConsulta.getText().isBlank()) erros.add("Valor da consulta é obrigatório.");
        if (fldSenha.getText().isBlank())         erros.add("Senha é obrigatória.");
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
    @FXML private void onDashboard(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "Dashboard.fxml"); }
    @FXML private void onMedicos(ActionEvent e)    { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "medicos.fxml"); }
    @FXML private void onPacientes(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "pacientes.fxml"); }
    @FXML private void onConsultas(ActionEvent e)  { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "consultas.fxml"); }
    @FXML private void onBusca(ActionEvent e)      { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "busca.fxml"); }
    @FXML private void onRelatorios(ActionEvent e) { NavigationHelper.goTo((javafx.scene.Node) e.getSource(), "relatorios.fxml"); }
}
