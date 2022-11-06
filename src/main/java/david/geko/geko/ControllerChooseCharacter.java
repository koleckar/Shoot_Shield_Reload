package david.geko.geko;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class ControllerChooseCharacter implements Initializable {
    @FXML
    private ImageView imageViewPutin;
    @FXML
    private ImageView imageViewBiden;
    @FXML
    private ImageView imageViewZeman;
    @FXML
    private Button startGameButton;
    @FXML
    private VBox vBox2;

    private ImageView lastChosenImageView;
    MediaPlayer player;

    //todo what to do in multiplayer?
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageViewBiden.setOnMouseClicked(this::onCharacterChosenImageViewClick);
        imageViewZeman.setOnMouseClicked(this::onCharacterChosenImageViewClick);
        imageViewPutin.setOnMouseClicked(this::onCharacterChosenImageViewClick);

        Media media = new Media(
                Objects.requireNonNull(getClass().getResource("Music/choose_character.mp3")).toString());
        player = new MediaPlayer(media);
        player.setOnEndOfMedia(() -> player.seek(Duration.ZERO)); // loop
        player.setVolume(0.1);
        player.play();

        vBox2.setBackground(new Background(new BackgroundImage(
                new Image(Objects.requireNonNull(getClass().getResource("Images/background.jfif")).toExternalForm()),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(1240, 720, false, false, false, false))));

        characters = new ArrayList<>();
        characters.add(imageViewBiden);
        characters.add(imageViewPutin);
        characters.add(imageViewZeman);
    }

    private String getCharacterNameFromImageViewName(String source) {
        return source.replace("imageView", "").toLowerCase();
    }

    protected void onCharacterChosenImageViewClick(MouseEvent event) {
        String source = event.getPickResult().getIntersectedNode().getId();
        source = getCharacterNameFromImageViewName(source);
        AudioClip sound;
        if (isCharacterTaken(source)) {
            cannotChooseCharacter(source);
            sound = new AudioClip(
                    Objects.requireNonNull(this.getClass().getResource("Music/nope.mp3")).toExternalForm());
        } else {
            updateMyPlayerCharacter(source);
            updateChosenCharacterView(event);
            sound = new AudioClip(
                    Objects.requireNonNull(this.getClass().getResource("Music/character_click.mp3")).toExternalForm());
            sound.setVolume(0.1);

        }
        sound.play();
    }


    private void updateChosenCharacterView(MouseEvent event) {
        ImageView clickedImageView = (ImageView) event.getPickResult().getIntersectedNode();
        if (lastChosenImageView != null) {
            lastChosenImageView.setEffect(new GaussianBlur());
        }
        clickedImageView.setEffect(new Glow());
        lastChosenImageView = clickedImageView;
        animateChoice(clickedImageView);
    }

    ArrayList<ImageView> characters;

    private void animateChoice(ImageView imageView) {
        ScaleTransition upscaleTransition = new ScaleTransition(Duration.millis(500));
        upscaleTransition.setNode(imageView);
        upscaleTransition.setByX(0.3);
        upscaleTransition.setByY(0.3);
        upscaleTransition.play();
    }

    private void updateMyPlayerCharacter(String source) {
        if (Main.game.isMultiplayer) {
            //TODO: send info if multiplayer to otherPlayers.
        }
        Main.game.myPlayer.setCharacterName(source);
    }

    private void cannotChooseCharacter(String character) {
    }

    private boolean isCharacterTaken(String character) {
        for (Player player : Main.game.players) {
            if (!player.equals(Main.game.myPlayer)
                    && player.getCharacterName() != null
                    && player.getCharacterName().equals(character)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void onStartGameButtonClick() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Views/view_game.fxml")));
        Scene scene = new Scene(root, 1240, 720);
        Stage stage = (Stage) startGameButton.getScene().getWindow();
        stage.setScene(scene);

        player.stop();
    }


}
