package david.geko.geko;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class ControllerStart implements Initializable {
    @FXML
    private Button singlePlayerButton;
    @FXML
    private Button hostGameButton;
    @FXML
    private Button joinGameButton;
    @FXML
    private VBox vBox;

    @FXML
    protected void onSinglePlayerButtonClick() throws IOException {
        Parent root = FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("Views/view_choose_character.fxml")));
        Scene scene = new Scene(root, 1240, 720);

        Stage stage = (Stage) singlePlayerButton.getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    protected void onHostGameButtonClick() {
    }

    @FXML
    protected void onJoinGameButtonClick() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vBox.setBackground(new Background(new BackgroundImage(
                new Image(Objects.requireNonNull(getClass().getResource("Images/background.jfif")).toExternalForm()),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT, new BackgroundSize(1240, 720, false, false, false, false))));
    }
}