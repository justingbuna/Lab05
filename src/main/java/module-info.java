module lab.flotavehicular {
    // Módulos de JavaFX incluidos en el proyecto
    requires javafx.controls;
    requires javafx.fxml;

    // Abre el paquete del controller para que JavaFX (FXML) acceda por reflexión
    opens lab.flotavehicular.controller to javafx.fxml;

    // Paquetes que exponemos al exterior
    exports lab.flotavehicular;
    exports lab.flotavehicular.model;
}
