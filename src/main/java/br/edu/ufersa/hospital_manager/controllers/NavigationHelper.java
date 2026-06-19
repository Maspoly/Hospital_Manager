package br.edu.ufersa.hospital_manager.controllers;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class NavigationHelper {

    private static final String VIEWS_PATH = "/br/edu/ufersa/hospital_manager/views/";
    private static final String CSS_BASE_PATH = "/br/edu/ufersa/hospital_manager/css/";

    private NavigationHelper() {
    }

    // Navegação a partir de um Node
    public static void goTo(Node fromNode, String fxmlFileName) {
        goTo(fromNode, fxmlFileName, "style.css");
    }

    public static void goTo(Node fromNode, String fxmlFileName, String cssFileName) {
        try {
            Stage stage = (Stage) fromNode.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    NavigationHelper.class.getResource(VIEWS_PATH + fxmlFileName)
            
            );
            Parent root = loader.load();
            stage.setFullScreen(true);
            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            scene.getStylesheets().add(
                    NavigationHelper.class.getResource(CSS_BASE_PATH + cssFileName).toExternalForm()
            );

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Não foi possível abrir a tela solicitada.\n" + e.getMessage());
        }
    }

    // Navegação a partir de um StackPane (para LoginMedicoController)
    public static void goTo(StackPane rootPane, String fxmlFileName) {
        goTo(rootPane, fxmlFileName, "style.css");
    }

    public static void goTo(StackPane rootPane, String fxmlFileName, String cssFileName) {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    NavigationHelper.class.getResource(VIEWS_PATH + fxmlFileName)
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            scene.getStylesheets().add(
                    NavigationHelper.class.getResource(CSS_BASE_PATH + cssFileName).toExternalForm()
            );

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Não foi possível abrir a tela solicitada.\n" + e.getMessage());
        }
    }

    public static void showInfo(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(b -> b.getButtonData().isDefaultButton()).isPresent();
    }
}