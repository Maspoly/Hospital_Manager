module hospital.manager {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires mysql.connector.j;

    exports br.edu.ufersa.hospital_manager;

    opens br.edu.ufersa.hospital_manager.controllers to javafx.fxml;
}