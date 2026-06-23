package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.ufersa.hospital_manager.model.entities.Address;
import br.edu.ufersa.hospital_manager.model.entities.Patient;
import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.PatientServiceProxy;
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
import javafx.scene.input.MouseEvent;

public class PacienteEditarDadosController {

    @FXML
    private Label lblIniciais;

    @FXML
    private Label lblNomePaciente;

    @FXML
    private Label lblCpfPaciente;

    @FXML
    private TextField fldNome;

    @FXML
    private TextField fldCpf;

    @FXML
    private PasswordField fldSenhaAtual;

    @FXML
    private PasswordField fldSenhaNova;

    @FXML
    private PasswordField fldSenhaNovaConfirmar;

    @FXML private Label lblVisualizarPerfil;

    @FXML
    private TextField fldRua;

    @FXML
    private TextField fldNumero;

    @FXML
    private TextField fldBairro;

    @FXML
    private TextField fldCidade;

    @FXML
    private TextField fldEstado;

    @FXML
    private Label lblErro;

    private Patient pacienteLogado;

    private final PatientServiceProxy patientService = (PatientServiceProxy) ProxyFactory.createProxy("PATIENT");

    @FXML
    public void initialize() {
        carregarPacienteLogado();
        preencherDados();
    }

    @FXML
    private void onVisualizarPerfil(MouseEvent event) {
        Person usuario = ServiceRoleContext.getCurrentUser();
        ServiceRole role = ServiceRoleContext.getCurrentRole();

        if (usuario == null || role == null) {
            NavigationHelper.showError("Usuário não encontrado.");
            return;
        }

        switch (role) {
            case MANAGER:
                NavigationHelper.goTo(lblVisualizarPerfil, "perfil_gerente.fxml");
                break;
            case DOCTOR:
                NavigationHelper.goTo(lblVisualizarPerfil, "medico_editar_dados.fxml", "medico.css");
                break;
            case PATIENT:
                NavigationHelper.goTo(lblVisualizarPerfil, "paciente_editar_dados.fxml", "paciente.css");
                break;
            default:
                NavigationHelper.showError("Perfil não encontrado.");
                break;
        }
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

    private void preencherDados() {
        if (pacienteLogado == null) return;

        fldNome.setText(pacienteLogado.getName());
        fldCpf.setText(formatarCpf(pacienteLogado.getCPF()));

        Address addr = pacienteLogado.getAddress();
        fldRua.setText(addr.getStreet() != null ? addr.getStreet() : "");
        fldNumero.setText(addr.getNumber() != null ? addr.getNumber() : "");
        fldBairro.setText(addr.getNeighborhood() != null ? addr.getNeighborhood() : "");
        fldCidade.setText(addr.getCity() != null ? addr.getCity() : "");
        fldEstado.setText(addr.getState() != null ? addr.getState() : "");
    }

    @FXML
    public void onSalvar(ActionEvent event) {
        if (!validar()) return;

        try {
            // Atualiza endereço
            Address endereco = pacienteLogado.getAddress();
            endereco.setStreet(fldRua.getText().trim());
            endereco.setNumber(fldNumero.getText().trim());
            endereco.setNeighborhood(fldBairro.getText().trim());
            endereco.setCity(fldCidade.getText().trim());
            endereco.setState(fldEstado.getText().trim());

            // Atualiza paciente
            pacienteLogado.setName(fldNome.getText().trim());

            // Atualiza senha se fornecida
            String senhaNova = fldSenhaNova.getText().trim();
            if (!senhaNova.isEmpty()) {
                // Verifica senha atual
                String senhaAtual = fldSenhaAtual.getText().trim();
                if (senhaAtual.isEmpty()) {
                    mostrarErro("Digite sua senha atual para alterá-la.");
                    return;
                }

                if (!PasswordUtils.matches(senhaAtual, pacienteLogado.getPasswordHash())) {
                    mostrarErro("Senha atual incorreta.");
                    return;
                }

                // Verifica se as senhas novas coincidem
                String senhaNovaConfirmar = fldSenhaNovaConfirmar.getText().trim();
                if (!senhaNova.equals(senhaNovaConfirmar)) {
                    mostrarErro("As senhas novas não coincidem.");
                    return;
                }

                pacienteLogado.setPassword(senhaNova);
            }

            patientService.updatePatient(pacienteLogado);

            NavigationHelper.showInfo("Sucesso", "Dados atualizados com sucesso!");

            // Atualiza os labels da sidebar após salvar
            atualizarDadosPaciente();

            NavigationHelper.goTo((Node) event.getSource(), "paciente_dashboard.fxml", "paciente.css");

        } catch (RuntimeException e) {
            mostrarErro(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro no banco de dados: " + e.getMessage());
        }
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        NavigationHelper.goTo((Node) event.getSource(), "paciente_dashboard.fxml", "paciente.css");
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