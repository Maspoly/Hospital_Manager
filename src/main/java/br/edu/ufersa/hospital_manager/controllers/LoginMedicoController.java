package br.edu.ufersa.hospital_manager.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class LoginMedicoController {

    @FXML
    private StackPane rootPane;

    @FXML
    private TextField txtCodigo;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Label lblVoltarGerente;

    // Credenciais de teste
    private static final String MEDICO_CODIGO = "CRM-12345";
    private static final String MEDICO_SENHA = "medico123";

    @FXML
    public void onEntrarClick(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String senha = txtSenha.getText();

        if (codigo != null && codigo.equals(MEDICO_CODIGO) && senha != null && senha.equals(MEDICO_SENHA)) {
            NavigationHelper.goTo((Node) event.getSource(), "medico_pacientes.fxml", "medico.css");
        } else {
            NavigationHelper.showError("Código ou senha inválidos.");
        }
    }

    @FXML
    public void onVoltarGerenteClick() {
        NavigationHelper.goTo(rootPane, "login.fxml");
    }
}