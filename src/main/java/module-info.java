module hospital.manager {
    requires javafx.controls;
    requires javafx.fxml;
<<<<<<< HEAD
=======
    requires transitive javafx.graphics;
>>>>>>> 96ad7c6 (Linked screens to data base)
    requires java.sql;

    opens br.edu.ufersa.hospital_manager to javafx.fxml;
    opens br.edu.ufersa.hospital_manager.controllers to javafx.fxml;
    opens br.edu.ufersa.hospital_manager.model.entities to javafx.fxml;
    exports br.edu.ufersa.hospital_manager;
}
