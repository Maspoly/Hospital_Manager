package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.services.AddressServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.PatientServiceProxy;
import br.edu.ufersa.hospital_manager.util.ProxyFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class CadastroUsuarioController {

    // ── Campos do formulário ──────────────────────────────────────────────────
    @FXML private TextField fldNome;
    @FXML private TextField fldCpf;
    
    // Endereço
    @FXML private TextField fldRua;
    @FXML private TextField fldNumero;
    @FXML private TextField fldBairro;
    @FXML private TextField fldCidade;
    @FXML private TextField fldEstado;
    
    @FXML private PasswordField fldSenha;
    @FXML private PasswordField fldConfirmarSenha;
    @FXML private Label lblErro;

    private final PatientServiceProxy patientService = (PatientServiceProxy) ProxyFactory.createProxy("PATIENT");
    private final AddressServiceProxy addressService = (AddressServiceProxy) ProxyFactory.createProxy("ADDRESS");

    // ── Inicialização ─────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        // Configurações iniciais, se necessário
    }

    // ── Ação: Salvar ──────────────────────────────────────────────────────────
    @FXML
    private void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
            // 1. Persiste o endereço
            Address endereco = new Address(
                    fldRua.getText().trim(),
                    fldNumero.getText().trim(),
                    fldBairro.getText().trim(),
                    fldCidade.getText().trim(),
                    fldEstado.getText().trim()
            );
            addressService.create(endereco);

            // 2. Remove pontuação do CPF
            String cpfLimpo = fldCpf.getText().trim().replaceAll("[^0-9]", "");

            // 3. Cria e persiste o paciente
            Patient paciente = new Patient(
                    fldNome.getText().trim(),
                    cpfLimpo,
                    endereco,
                    fldSenha.getText().trim()
            );
            patientService.registerPatient(paciente);

            NavigationHelper.showInfo("Sucesso", "Paciente cadastrado com sucesso! Faça login para acessar o sistema.");
            NavigationHelper.goTo((Node) event.getSource(), "login.fxml");

        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro no banco de dados: " + e.getMessage());
        }
    }

    // ── Ação: Cancelar ────────────────────────────────────────────────────────
    @FXML
    private void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
    }

    // ── Voltar para o login ──────────────────────────────────────────────────
    @FXML
    private void onVoltarLogin(MouseEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "login.fxml");
    }

    // ── Validação ─────────────────────────────────────────────────────────────
    private boolean validar() {
        List<String> erros = new ArrayList<>();

        // Validação dos campos obrigatórios
        if (fldNome.getText().isBlank()) {
            erros.add("Nome completo é obrigatório.");
        }

        if (fldCpf.getText().isBlank()) {
            erros.add("CPF é obrigatório.");
        } else {
            String cpfLimpo = fldCpf.getText().trim().replaceAll("[^0-9]", "");
            if (cpfLimpo.length() != 11) {
                erros.add("CPF deve conter 11 dígitos numéricos.");
            }
        }

        if (fldSenha.getText().isBlank()) {
            erros.add("Senha é obrigatória.");
        } 

        if (fldConfirmarSenha.getText().isBlank()) {
            erros.add("Confirme a senha.");
        } else if (!fldSenha.getText().equals(fldConfirmarSenha.getText())) {
            erros.add("As senhas não conferem.");
        }

        // Validação do endereço
        if (fldRua.getText().isBlank()) {
            erros.add("Rua é obrigatória.");
        }
        if (fldNumero.getText().isBlank()) {
            erros.add("Número é obrigatório.");
        }
        if (fldBairro.getText().isBlank()) {
            erros.add("Bairro é obrigatório.");
        }
        if (fldCidade.getText().isBlank()) {
            erros.add("Cidade é obrigatória.");
        }
        if (fldEstado.getText().isBlank()) {
            erros.add("Estado é obrigatório.");
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
}