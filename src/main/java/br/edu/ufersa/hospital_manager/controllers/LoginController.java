package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import br.edu.ufersa.hospital_manager.model.entities.Person;
import br.edu.ufersa.hospital_manager.model.services.LoginServiceContract;
import br.edu.ufersa.hospital_manager.model.services.ServiceRole;
import br.edu.ufersa.hospital_manager.util.ProxyFactory;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController {
    @FXML private Button btnEntrar;
    @FXML private Button btnCadastrarUsuario;

    @FXML
    private StackPane rootPane;

    @FXML
    private TextField txtCpf;

    @FXML
    private PasswordField txtSenha;

    private final LoginServiceContract loginService = (LoginServiceContract) ProxyFactory.createProxy("LOGIN");

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

    /**
     * Ação do botão "Cadastrar novo usuário"
     * Redireciona para a tela de cadastro de paciente
     */
    @FXML
    public void onCadastrarUsuarioClick() {
        NavigationHelper.goTo(btnCadastrarUsuario, "cadastro_usuario.fxml");
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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
        
        if (role == ServiceRole.PATIENT) {
            NavigationHelper.goTo(btnEntrar, "paciente_dashboard.fxml", "paciente.css");
            return;
        }

        // MANAGER ou fallback
        NavigationHelper.goTo(btnEntrar, "Dashboard.fxml");
    }
}