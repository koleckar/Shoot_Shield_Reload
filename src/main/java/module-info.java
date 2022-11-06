module david.geko.geko {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires com.almasb.fxgl.all;

    opens david.geko.geko to javafx.fxml;
    exports david.geko.geko;
}