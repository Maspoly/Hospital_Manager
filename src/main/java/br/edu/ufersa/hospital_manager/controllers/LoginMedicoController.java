package br.edu.ufersa.hospital_manager.controllers;

import java.sql.SQLException;

import br.edu.ufersa.hospital_manager.model.entities.Doctor;
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

    private final DoctorServiceProxy doctorService = (DoctorServiceProxy) ProxyFactory.createProxy("DOCTOR");

    @FXML
    public void onEntrarClick(ActionEvent event) {
        String codigo = txtCodigo.getText();
        String senha = txtSenha.getText();

        try {
            String codigoLimpo = codigo == null ? "" : codigo.replaceAll("[^0-9]", "");
            Doctor doctor = doctorService.findByCouncilCode(codigoLimpo);

            if (doctor != null && PasswordUtils.matches(senha, doctor.getPasswordHash())) {
                ServiceRoleContext.setCurrentUser(doctor, ServiceRole.DOCTOR);
                NavigationHelper.goTo((Node) event.getSource(), "medico_pacientes.fxml", "medico.css");
                return;
            }

            NavigationHelper.showError("Código ou senha inválidos.");
        } catch (SQLException exception) {
            NavigationHelper.showError("Erro ao autenticar médico: " + exception.getMessage());
        }
    }

    @FXML
    public void onVoltarGerenteClick() {
        NavigationHelper.goTo(rootPane, "login.fxml");
    }
}