package br.edu.ufersa.hospital_manager;

<<<<<<< HEAD
=======
import br.edu.ufersa.hospital_manager.model.services.DefaultDataBootstrap;
>>>>>>> 96ad7c6 (Linked screens to data base)
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
<<<<<<< HEAD
=======
import javafx.scene.control.Alert;
>>>>>>> 96ad7c6 (Linked screens to data base)
import javafx.stage.Stage;

public class MainTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
<<<<<<< HEAD
=======
        try {
            DefaultDataBootstrap.ensureTestAccounts();
        } catch (Exception exception) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Banco indisponível");
            alert.setHeaderText("Não foi possível criar os dados de teste.");
            alert.setContentText("A tela será aberta normalmente, mas login e cadastro dependem do banco estar disponível: " + exception.getMessage());
            alert.showAndWait();
        }

>>>>>>> 96ad7c6 (Linked screens to data base)
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
<<<<<<< HEAD
        // ativa fullscreen
        stage.setFullScreen(true);
=======
>>>>>>> 96ad7c6 (Linked screens to data base)
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
