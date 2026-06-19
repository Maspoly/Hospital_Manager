package br.edu.ufersa.hospital_manager.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {
    @FXML private Button btnEntrar;

    @FXML
    private StackPane rootPane;

    @FXML
    private TextField ID_campoText;

    @FXML
    private PasswordField ID_Password;

    @FXML
    private Label lblAcessoMedicos;

    // Credenciais de teste (ajustar para usar ManagerService/DoctorServices futuramente)
    private static final String ADMIN_USER = "a";
    private static final String ADMIN_PASS = "a";

    @FXML
    public void onEntrarClick() {
        String usuario = ID_campoText.getText();
        String senha = ID_Password.getText();

        if (usuario.equals(ADMIN_USER) && senha.equals(ADMIN_PASS)) {
            // TODO: trocar para a tela do dashboard
            NavigationHelper.goTo(btnEntrar, "dashboard.fxml");
            
            showAlert(AlertType.INFORMATION, "Login", "Bem-vindo, " + usuario + "!");
        } else {
            showAlert(AlertType.ERROR, "Erro", "Usuário ou senha inválidos.");
        }
    }

    @FXML
    public void onCadastrarClick() {
        // TODO: navegar para a tela de cadastro
        NavigationHelper.goTo(btnEntrar, "cadastro.fxml");
    }

    @FXML
    public void onAcessoMedicosClick() {
        // TODO: navegar para a tela de login de médicos
        NavigationHelper.goTo(btnEntrar, "login_medico.fxml");
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Carrega outro FXML na área central.
     * Adapte conforme a arquitetura de navegação do seu projeto
     * (ex.: injetar um controlador-raiz, usar um ScreenManager, etc.).
     *
     * @param fxmlPath caminho relativo ao classpath do arquivo FXML
     */
    private void navegarPara(String fxmlPath) {
        try {
            // Exemplo de navegação com troca de cena:
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnEntrar.getScene().getWindow();
            stage.getScene().setRoot(root);

            System.out.println("Navegando para: " + fxmlPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
