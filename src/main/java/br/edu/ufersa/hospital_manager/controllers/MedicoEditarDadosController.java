package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Doctor;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.DoctorServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.model.services.ServiceRoleContext;
import br.edu.ufersa.hospital_manager.util.PasswordUtils;
import br.edu.ufersa.hospital_manager.util.ProxyFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class MedicoEditarDadosController {

    // ── Labels do usuário logado (sidebar) ───────────────────────────────────
    @FXML private Label lblIniciais;
    @FXML private Label lblNomeMedico;
    @FXML private Label lblCrmMedico;

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

    @FXML private TextField fldCrm;
    @FXML private TextField fldValorConsulta;

    @FXML private Label lblErro;

    private Doctor medicoLogado;
    private final DoctorServiceProxy doctorService = (DoctorServiceProxy) ProxyFactory.createProxy("DOCTOR");

    @FXML
    public void initialize() {
        carregarMedicoLogado();
        preencherDados();
    }

    private void carregarMedicoLogado() {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        if (usuario instanceof Doctor && role == ServiceRole.DOCTOR) {
            medicoLogado = (Doctor) usuario;
            String nome = medicoLogado.getName();
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
            lblIniciais.setText(iniciais.length() > 0 ? iniciais.toString() : "D");
            lblNomeMedico.setText("Dr. " + nome);
            lblCrmMedico.setText("CRM-" + medicoLogado.getCouncilCode());
        } else {
            NavigationHelper.showError("Usuário não encontrado.");
            lblIniciais.setText("D");
            lblNomeMedico.setText("Dr. Médico");
            lblCrmMedico.setText("CRM-000000");
        }
    }

    private void preencherDados() {
        if (medicoLogado == null) return;

        fldNome.setText(medicoLogado.getName());
        fldCpf.setText(formatarCpf(medicoLogado.getCPF()));
        fldCrm.setText("CRM " + medicoLogado.getCouncilCode() + "/" + medicoLogado.getAddress().getState());

        Address addr = medicoLogado.getAddress();
        if (addr != null) {
            fldRua.setText(addr.getStreet() != null ? addr.getStreet() : "");
            fldNumero.setText(addr.getNumber() != null ? addr.getNumber() : "");
            fldBairro.setText(addr.getNeighborhood() != null ? addr.getNeighborhood() : "");
            fldCidade.setText(addr.getCity() != null ? addr.getCity() : "");
            fldEstado.setText(addr.getState() != null ? addr.getState() : "");
        }

        fldValorConsulta.setText(String.valueOf(medicoLogado.getConsultationValue()).replace(".", ","));
    }

    @FXML
    private void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
            // Atualiza endereço
            Address endereco = medicoLogado.getAddress();
            if (endereco == null) {
                endereco = new Address(
                    fldRua.getText().trim(),
                    fldNumero.getText().trim(),
                    fldBairro.getText().trim(),
                    fldCidade.getText().trim(),
                    fldEstado.getText().trim()
                );
                medicoLogado.setAddress(endereco);
            } else {
                endereco.setStreet(fldRua.getText().trim());
                endereco.setNumber(fldNumero.getText().trim());
                endereco.setNeighborhood(fldBairro.getText().trim());
                endereco.setCity(fldCidade.getText().trim());
                endereco.setState(fldEstado.getText().trim());
            }

            // Atualiza nome
            medicoLogado.setName(fldNome.getText().trim());

            // Atualiza senha se fornecida
            String senhaNova = fldSenhaNova.getText().trim();
            if (!senhaNova.isEmpty()) {
                // Verifica senha atual
                String senhaAtual = fldSenhaAtual.getText().trim();
                if (senhaAtual.isEmpty()) {
                    mostrarErro("Digite sua senha atual para alterá-la.");
                    return;
                }

                if (!PasswordUtils.matches(senhaAtual, medicoLogado.getPasswordHash())) {
                    mostrarErro("Senha atual incorreta.");
                    return;
                }

                // Verifica se as senhas novas coincidem
                String senhaNovaConfirmar = fldSenhaNovaConfirmar.getText().trim();
                if (!senhaNova.equals(senhaNovaConfirmar)) {
                    mostrarErro("As senhas novas não coincidem.");
                    return;
                }

                medicoLogado.setPassword(senhaNova);
            }

            float valor = Float.parseFloat(fldValorConsulta.getText().trim().replace(",", "."));
            medicoLogado.setConsultationValue(valor);

            doctorService.updateDoctor(medicoLogado);

            NavigationHelper.showInfo("Sucesso", "Dados atualizados com sucesso!");
            NavigationHelper.goTo((Node) event.getSource(), "medico_pacientes.fxml", "medico.css");

        } catch (NumberFormatException e) {
            mostrarErro("Valor da consulta inválido. Use o formato: 250,00");
        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro no banco de dados: " + e.getMessage());
        }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "medico_pacientes.fxml", "medico.css");
    }

    private boolean validar() {
        List<String> erros = new ArrayList<>();

        if (fldNome.getText().isBlank()) erros.add("Nome completo é obrigatório.");
        if (fldRua.getText().isBlank()) erros.add("Rua é obrigatória.");
        if (fldNumero.getText().isBlank()) erros.add("Número é obrigatório.");
        if (fldBairro.getText().isBlank()) erros.add("Bairro é obrigatório.");
        if (fldCidade.getText().isBlank()) erros.add("Cidade é obrigatória.");
        if (fldEstado.getText().isBlank()) erros.add("Estado é obrigatório.");
        if (fldValorConsulta.getText().isBlank()) erros.add("Valor da consulta é obrigatório.");

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

        try {
            Float.parseFloat(fldValorConsulta.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            erros.add("Valor da consulta inválido.");
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
}