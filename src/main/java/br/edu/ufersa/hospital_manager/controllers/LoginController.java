package br.edu.ufersa.hospital_manager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class LoginController {

    @FXML
    private StackPane rootPane;

    @FXML
    private TextField ID_campoText;

    @FXML
    private PasswordField ID_Password;

    @FXML
    private Label lblAcessoMedicos;

    // Credenciais de teste (ajustar para usar ManagerService/DoctorServices futuramente)
    private static final String ADMIN_USER = "Administrador";
    private static final String ADMIN_PASS = "admin123";

    @FXML
    public void onEntrarClick() {
        String usuario = ID_campoText.getText();
        String senha = ID_Password.getText();

        if (usuario.equals(ADMIN_USER) && senha.equals(ADMIN_PASS)) {
            // TODO: trocar para a tela do dashboard
            showAlert(AlertType.INFORMATION, "Login", "Bem-vindo, " + usuario + "!");
        } else {
            showAlert(AlertType.ERROR, "Erro", "Usuário ou senha inválidos.");
        }
    }

    @FXML
    public void onCadastrarClick() {
        // TODO: navegar para a tela de cadastro
        showAlert(AlertType.INFORMATION, "Cadastrar", "Tela de cadastro em construção.");
    }

    @FXML
    public void onAcessoMedicosClick() {
        // TODO: navegar para a tela de login de médicos
        showAlert(AlertType.INFORMATION, "Acesso para Médicos", "Tela de acesso para médicos em construção.");
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
