package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Manager;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.ManagerServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import br.edu.ufersa.hospital_manager.util.PasswordUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PerfilGerenteController {

    // ── Campos do formulário ──────────────────────────────────────────────────
    @FXML private TextField fldNome;
    @FXML private TextField fldCpf;

    @FXML private PasswordField fldSenhaAtual;
    @FXML private PasswordField fldSenhaNova;
    @FXML private PasswordField fldSenhaNovaConfirmar;

    @FXML private TextField fldRua;
    @FXML private TextField fldNumero;
    @FXML private TextField fldBairro;
    @FXML private TextField fldCidade;
    @FXML private TextField fldEstado;


    @FXML private Label lblErro;

    // ── Labels do usuário logado (sidebar) ───────────────────────────────────
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;

    private Manager gerenteLogado;
    private final ManagerServiceProxy managerService = new ManagerServiceProxy();

    @FXML
    public void initialize() {
        carregarGerenteLogado();
        preencherDados();
    }

    private void carregarGerenteLogado() {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        if (usuario instanceof Manager && role == ServiceRole.MANAGER) {
            gerenteLogado = (Manager) usuario;
            lblUserName.setText(gerenteLogado.getName());
            lblUserRole.setText(role.getDisplayName());
        } else {
            NavigationHelper.showError("Usuário não encontrado.");
        }
    }

    private void preencherDados() {
        if (gerenteLogado == null) return;

        fldNome.setText(gerenteLogado.getName());
        fldCpf.setText(formatarCpf(gerenteLogado.getCPF()));

        Address addr = gerenteLogado.getAddress();
        if (addr != null) {
            fldRua.setText(addr.getStreet() != null ? addr.getStreet() : "");
            fldNumero.setText(addr.getNumber() != null ? addr.getNumber() : "");
            fldBairro.setText(addr.getNeighborhood() != null ? addr.getNeighborhood() : "");
            fldCidade.setText(addr.getCity() != null ? addr.getCity() : "");
            fldEstado.setText(addr.getState() != null ? addr.getState() : "");
        }

    }

    @FXML
    private void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
            // Atualiza endereço
            Address endereco = gerenteLogado.getAddress();
            if (endereco == null) {
                endereco = new Address(
                    fldRua.getText().trim(),
                    fldNumero.getText().trim(),
                    fldBairro.getText().trim(),
                    fldCidade.getText().trim(),
                    fldEstado.getText().trim()
                );
                gerenteLogado.setAddress(endereco);
            } else {
                endereco.setStreet(fldRua.getText().trim());
                endereco.setNumber(fldNumero.getText().trim());
                endereco.setNeighborhood(fldBairro.getText().trim());
                endereco.setCity(fldCidade.getText().trim());
                endereco.setState(fldEstado.getText().trim());
            }

            // Atualiza nome
            gerenteLogado.setName(fldNome.getText().trim());

            // Atualiza senha se fornecida
            String senhaNova = fldSenhaNova.getText().trim();
            if (!senhaNova.isEmpty()) {
                // Verifica senha atual
                String senhaAtual = fldSenhaAtual.getText().trim();
                if (senhaAtual.isEmpty()) {
                    mostrarErro("Digite sua senha atual para alterá-la.");
                    return;
                }

                if (!PasswordUtils.matches(senhaAtual, gerenteLogado.getPasswordHash())) {
                    mostrarErro("Senha atual incorreta.");
                    return;
                }

                // Verifica se as senhas novas coincidem
                String senhaNovaConfirmar = fldSenhaNovaConfirmar.getText().trim();
                if (!senhaNova.equals(senhaNovaConfirmar)) {
                    mostrarErro("As senhas novas não coincidem.");
                    return;
                }

                gerenteLogado.setPassword(senhaNova);
            }

            managerService.updateManager(gerenteLogado);

            // Atualiza labels
            lblUserName.setText(gerenteLogado.getName());

            NavigationHelper.showInfo("Sucesso", "Dados atualizados com sucesso!");
            NavigationHelper.goTo((Node) event.getSource(), "Dashboard.fxml");

        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro no banco de dados: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "Dashboard.fxml");
    }

    private boolean validar() {
        List<String> erros = new ArrayList<>();

        if (fldNome.getText().isBlank()) erros.add("Nome completo é obrigatório.");
        if (fldRua.getText().isBlank()) erros.add("Rua é obrigatória.");
        if (fldNumero.getText().isBlank()) erros.add("Número é obrigatório.");
        if (fldBairro.getText().isBlank()) erros.add("Bairro é obrigatório.");
        if (fldCidade.getText().isBlank()) erros.add("Cidade é obrigatória.");
        if (fldEstado.getText().isBlank()) erros.add("Estado é obrigatório.");

        // Validação de senha
        String senhaNova = fldSenhaNova.getText().trim();
        String senhaNovaConfirmar = fldSenhaNovaConfirmar.getText().trim();
        String senhaAtual = fldSenhaAtual.getText().trim();

        if (!senhaNova.isEmpty() || !senhaNovaConfirmar.isEmpty() || !senhaAtual.isEmpty()) {
            if (senhaAtual.isEmpty()) {
                erros.add("Digite sua senha atual para alterá-la.");
            }
            if (senhaNova.isEmpty()) {
                erros.add("Digite a nova senha.");
            }
            if (senhaNovaConfirmar.isEmpty()) {
                erros.add("Confirme a nova senha.");
            }
            if (!senhaNova.equals(senhaNovaConfirmar)) {
                erros.add("As senhas novas não coincidem.");
            }

        }

        if (!erros.isEmpty()) {
            mostrarErro(String.join("\n", erros));
            return false;
        }
        ocultarErro();
        return true;
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf == null ? "" : cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9, 11);
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

    // ===================== NAVEGAÇÃO =====================

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