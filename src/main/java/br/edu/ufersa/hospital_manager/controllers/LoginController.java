package br.edu.ufersa.hospital_manager.controllers;

<<<<<<< HEAD
import javafx.fxml.FXML;
=======
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.LoginServiceContract;
import br.edu.ufersa.hospital_manager.model.services.LoginServiceProxy;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
>>>>>>> 96ad7c6 (Linked screens to data base)
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
<<<<<<< HEAD
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
=======
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
>>>>>>> 96ad7c6 (Linked screens to data base)

public class LoginController {
    @FXML private Button btnEntrar;

    @FXML
    private StackPane rootPane;

    @FXML
<<<<<<< HEAD
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

=======
    private TextField txtCpf;

    @FXML
    private PasswordField txtSenha;

    private final LoginServiceContract loginService = new LoginServiceProxy();

    @FXML
    public void onEntrarClick() {
        String cpf = txtCpf.getText();
        String senha = txtSenha.getText();

        try {
            Map<ServiceRole, Person> matchedUsers = loginService.authenticate(cpf, senha);

            if (matchedUsers.size() == 1) {
                ServiceRole selectedRole = matchedUsers.keySet().iterator().next();
                loginService.selectSession(selectedRole, matchedUsers.get(selectedRole));
                navegarParaPapel(selectedRole);
                return;
            }

            Optional<ServiceRole> selectedRole = showRoleSelector(new ArrayList<>(matchedUsers.keySet()));
            if (selectedRole.isEmpty()) {
                return;
            }

            loginService.selectSession(selectedRole.get(), matchedUsers.get(selectedRole.get()));
            navegarParaPapel(selectedRole.get());
        } catch (SQLException exception) {
            showAlert(AlertType.ERROR, "Erro", "Falha ao autenticar: " + exception.getMessage());
        } catch (RuntimeException exception) {
            showAlert(AlertType.ERROR, "Erro", exception.getMessage());
        }
    }

>>>>>>> 96ad7c6 (Linked screens to data base)
    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
<<<<<<< HEAD
    
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
=======

    private Optional<ServiceRole> showRoleSelector(List<ServiceRole> roles) {
        if (roles.isEmpty()) {
            return Optional.empty();
        }

        AtomicReference<ServiceRole> selectedRole = new AtomicReference<>();

        Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.WINDOW_MODAL);
        if (rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
            dialog.initOwner(rootPane.getScene().getWindow());
        }

        Label title = new Label("Escolha sua função");
        title.getStyleClass().add("role-dialog-title");

        Label subtitle = new Label("Este CPF e senha têm mais de um perfil. Selecione como deseja entrar no sistema.");
        subtitle.getStyleClass().add("role-dialog-subtitle");
        subtitle.setWrapText(true);

        VBox cards = new VBox(12);
        cards.setAlignment(Pos.CENTER);

        for (ServiceRole role : roles) {
            Button selectButton = new Button(role.getDisplayName());
            selectButton.getStyleClass().add("role-dialog-button");
            selectButton.setMaxWidth(Double.MAX_VALUE);
            selectButton.setOnAction(event -> {
                selectedRole.set(role);
                dialog.close();
            });

            Label description = new Label(describeRole(role));
            description.getStyleClass().add("role-dialog-description");
            description.setWrapText(true);

            VBox card = new VBox(6, selectButton, description);
            card.getStyleClass().add("role-dialog-card");
            card.setFillWidth(true);
            cards.getChildren().add(card);
        }

        Button cancelButton = new Button("Cancelar");
        cancelButton.getStyleClass().add("btn-ghost");
        cancelButton.setOnAction(event -> dialog.close());

        VBox content = new VBox(18, title, subtitle, cards, cancelButton);
        content.getStyleClass().add("role-dialog-content");
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(28));
        content.setMaxWidth(420);

        StackPane backdrop = new StackPane(content);
        backdrop.getStyleClass().add("role-dialog-backdrop");
        backdrop.setPadding(new Insets(24));

        Scene scene = new Scene(backdrop);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/br/edu/ufersa/hospital_manager/css/style.css").toExternalForm());

        dialog.setScene(scene);
        dialog.showAndWait();

        return Optional.ofNullable(selectedRole.get());
    }

    private String describeRole(ServiceRole role) {
        return switch (role) {
            case MANAGER -> "Acesso ao painel administrativo, cadastros e relatórios da clínica.";
            case DOCTOR -> "Entrada nas telas e rotinas de atendimento médico.";
            case PATIENT -> "Acesso ao painel do paciente e suas informações.";
        };
    }

    private void navegarParaPapel(ServiceRole role) {
        if (role == ServiceRole.DOCTOR) {
            NavigationHelper.goTo(btnEntrar, "medico_pacientes.fxml", "medico.css");
            return;
        }

        NavigationHelper.goTo(btnEntrar, "Dashboard.fxml");
>>>>>>> 96ad7c6 (Linked screens to data base)
    }
}
