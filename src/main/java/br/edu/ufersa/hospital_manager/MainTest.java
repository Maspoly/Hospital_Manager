package br.edu.ufersa.hospital_manager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        /*
                FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/br/edu/ufersa/hospital_manager/views/Dashboard.fxml"
                )
            );

            Scene scene = new Scene(loader.load());

            stage.setTitle("Hospital Manager");
            stage.setScene(scene);
            stage.show();
        }
    */
        
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/br/edu/ufersa/hospital_manager/views/login.fxml")
        );
        Parent root = loader.load();
        // ativa fullscreen
        stage.setFullScreen(true);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/br/edu/ufersa/hospital_manager/css/style.css").toExternalForm()
        );

        stage.setTitle("Clínica Dr. Luiz - Sistema de Gerenciamento Médico");
        stage.setScene(scene);
        stage.show();
    }
    
        
        
    public static void main(String[] args) {
        launch(args);
    }
}
