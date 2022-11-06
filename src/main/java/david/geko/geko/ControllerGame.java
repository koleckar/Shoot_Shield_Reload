package david.geko.geko;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class ControllerGame implements Initializable {
    @FXML
    Text textTimer;
    @FXML
    private ImageView imageViewPlayer1;
    @FXML
    private ImageView imageViewPlayer2;
    @FXML
    private ImageView imageViewPlayer3;
    @FXML
    private ImageView bullet1to2; // why cannot be static?z something to do with DI
    @FXML
    private ImageView bullet1to3;
    @FXML
    private ImageView bullet3to1;
    @FXML
    private ImageView bullet3to2;
    @FXML
    private ImageView bullet2to1;
    @FXML
    private ImageView bullet2to3;
    @FXML
    private Pane pane;
    @FXML
    Text countdownText;
    @FXML
    VBox player1HeartsVBox;
    @FXML
    VBox player2HeartsVBox;
    @FXML
    VBox player3HeartsVBox;
    @FXML
    Line arrow1;
    @FXML
    Line arrowhead12;
    @FXML
    Line arrowhead11;
    @FXML
    Line arrow2;
    @FXML
    Line arrowhead21;
    @FXML
    Line arrowhead22;
    @FXML
    Rectangle countdownBlanket;
    @FXML
    Rectangle player1Hurt;
    @FXML
    Rectangle player2Hurt;
    @FXML
    Rectangle player3Hurt;
    @FXML
    ImageView imageViewReload;
    @FXML
    ImageView imageViewShieldPlayer1;
    @FXML
    ImageView imageViewShieldPlayer2;
    @FXML
    ImageView imageViewShieldPlayer3;
    @FXML
    ImageView imageViewLoadedPlayer1;
    @FXML
    ImageView imageViewLoadedPlayer2;
    @FXML
    ImageView imageViewLoadedPlayer3;

    @FXML
    ImageView imageViewLetsGo;


    private record Pair(int x, int y) {
    }

    private class TranslationValues {
        static HashMap<Pair, Pair> values = new HashMap<>();

        static {
            values.put(new Pair(1, 2), new Pair(400, -100));
            values.put(new Pair(1, 3), new Pair(400, 200));

            values.put(new Pair(2, 1), new Pair(-500, 0));
            values.put(new Pair(2, 3), new Pair(0, 300));

            values.put(new Pair(3, 1), new Pair(-400, -200));
            values.put(new Pair(3, 2), new Pair(0, -300));
        }
    }

    private boolean shootButtonSelected;
    private HashMap<Pair, ImageView> bulletsDictionary;

    MediaPlayer player;

    Timer timer;
    TimerTask timerTaskCountdown;
    TimerTask timerTaskRound;

    final int COUNTDOWN_TIME = 3;
    final int ONE_ROUND_TIME = 10;

    int roundTimeLeft = ONE_ROUND_TIME;
    int countdownTimeLeft = COUNTDOWN_TIME;


    private void updateTimeUI() {
        textTimer.setText(String.valueOf(roundTimeLeft));
    }

    private void createCountdownTimerTask() {
        timerTaskCountdown = new TimerTask() {
            @Override
            public void run() {
                countdownText.setText(String.valueOf(countdownTimeLeft));
                animateScaleTransition(countdownText, 2, 2, 1500);
                if (countdownTimeLeft == 0) {
                    countdownText.setVisible(false);
                    countdownBlanket.setVisible(false);
                    playAudioClip("beep_longer");
                    this.cancel();
                } else {
                    countdownTimeLeft--;
                    playAudioClip("beep_short");
                }
            }
        };
    }

    final private Object LOCK = new Object();

    private void createOneRoundTimerTask() {
        timerTaskRound = new TimerTask() {
            @Override
            public void run() {
                System.out.println("round running");
                updateTimeUI();
                animateScaleTransition(textTimer, 0.1, 0.1, 1500);

                if (roundTimeLeft == 0) {
                    try {
                        synchronized (LOCK) {
                            LOCK.notify();
                            System.out.println("round notifying");
                        }
                        Thread.sleep(100);
                        synchronized (LOCK) {
                            System.out.println("round waiting");
                            LOCK.wait();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    roundTimeLeft--;
                }
            }
        };
    }

    private boolean gameLoopCondition = true;

    class OnRoundEndThread implements Runnable {
        @Override
        public void run() {
            while (gameLoopCondition) {
                System.out.println("End running");

                if (roundTimeLeft == 0) {
                    updateGameState();

                    performEndOfRoundAnimations();
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                roundTimeLeft = ONE_ROUND_TIME;

                try {
                    synchronized (LOCK) {
                        LOCK.notify();
                        System.out.println("End notifying");
                    }
                    Thread.sleep(200);
                    synchronized (LOCK) {
                        System.out.println("End waiting");
                        LOCK.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void updateGameState() {
        if (Main.game.isMultiplayer) {
            //update via sockets
        } else {
            // make bots
            Main.game.botMakesAction(1);

            Main.game.botMakesAction(2);

            Main.game.performAllActions();
        }
    }


    // round timer
    // actions are reactive - each action updates Game/Player structures.
    // round-end triggers end-of-round actions = game updates, (socket actions), animations
    // next round -> reset timer
    // OR game over
    // have instad end of round task
    protected void gameLoop() {
        timer = new Timer();
        createOneRoundTimerTask();
        createCountdownTimerTask();
        Thread gameLoopThread = new Thread(new OnRoundEndThread());

        timer.schedule(timerTaskCountdown, 0, 1500);
        timer.schedule(timerTaskRound, 4500, 1500);
        gameLoopThread.start();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeGameUI();

        playMediaPlayer();

        initializeBulletsDictionary();

        bindArrowLines();

        gameLoop();
    }

    private void initializeGameUI() {
        countdownBlanket.setVisible(true);
        imageViewReload.setEffect(new Shadow());
        imageViewShieldPlayer1.setVisible(false);
        imageViewShieldPlayer2.setVisible(false);
        imageViewShieldPlayer3.setVisible(false);


        pane.setBackground(new Background(new BackgroundImage(
                new Image(Objects.requireNonNull(getClass().getResource("Images/background2.png")).toExternalForm()),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(1240, 720, false, false, false, false))));

        String characterName = Main.game.myPlayer.getCharacterName();
        ArrayList<String> names = new ArrayList<>(Arrays.asList("zeman", "putin", "biden"));
        names.remove(characterName);
        imageViewPlayer1.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("Images/" + characterName + ".png"))));
        imageViewPlayer2.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("Images/" + names.remove(0) + ".png"))));
        imageViewPlayer3.setImage(new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("Images/" + names.remove(0) + ".png"))));
    }

    private void resetRoundUI() {
        //todo:

    }

    private void performEndOfRoundAnimations() {
        //todo:
        animateFadeInFadeOutTransition(imageViewLetsGo, 1);

    }

    // TODO: can user change its mind after clicking any of action buttons? or smrdi ti ass
    //  reload a stit to udela rovnou, reload se ti zastinuje kdyz uz mas nabito, ty mrdko
    //  shoot udela to ze ti jenom zacne blikat ta vybrana sipka a streli to az na konci kola, aby to kdyztak
    //   ukazalo stit proti tvy strele i s efektem odrazeni kulky
    public void onShootButtonClicked(ActionEvent event) {
        if (!shootButtonSelected) {
            shootButtonSelected = true;
            arrow1.setVisible(true);
            arrow2.setVisible(true);

            animateFadeTransition(arrow1, 1);
            animateFadeTransition(arrow2, 1);
        }
    }

    public void onShieldButtonClicked(ActionEvent event) {
        if (shootButtonSelected) {
            shootButtonSelected = false;

            animateFadeTransition(arrow1, 0);
            animateFadeTransition(arrow2, 0);
            arrow1.setVisible(false);
            arrow2.setVisible(false);
        }
        animateShield(0);

        playAudioClip("shield");

    }

    public void onReloadButtonClicked(ActionEvent event) {
        if (shootButtonSelected) {
            shootButtonSelected = false;
            animateFadeTransition(arrow1, 0);
            animateFadeTransition(arrow2, 0);
            arrow1.setVisible(false);
            arrow2.setVisible(false);
        }
        playAudioClip("reload");
    }

    public void onPlayer2ButtonClicked(ActionEvent event) {
        if (shootButtonSelected) {
            animateShoot(1, 2);
            animateLooseHeart(2);
            animateLoadedFade(1, 0);
        }
    }

    public void onPlayer3ButtonClicked(ActionEvent event) {
        animateShoot(1, 3);
        animateLooseHeart(3);
    }


    private void animateShoot(int shooterID, int targetID) {
        TranslateTransition translate = new TranslateTransition();

        ImageView bullet = bulletsDictionary.get(new Pair(shooterID, targetID));
        Pair values = TranslationValues.values.get(new Pair(shooterID, targetID));

        bullet.setVisible(true);
        translate.setNode(bullet);
        translate.setByX(values.x);
        translate.setByY(values.y);

        translate.setDuration(new Duration(1000));
        translate.setOnFinished((event) -> {
            bullet.setVisible(false);
            bullet.setX(bullet.getX() - values.x);
            bullet.setY(bullet.getY() - values.y);
        });
        translate.play();

        playAudioClip("gunshot");
    }

    private void animateLooseHeart(int playerID) {
        VBox vbox;
        Rectangle hurt;

        switch (playerID) {
            case 1 -> {
                vbox = player1HeartsVBox;
                hurt = player1Hurt;
            }
            case 2 -> {
                vbox = player2HeartsVBox;
                hurt = player2Hurt;
            }
            case 3 -> {
                vbox = player3HeartsVBox;
                hurt = player3Hurt;
            }
            default -> throw new RuntimeException("illegal playerID");
        }

        int lives = Main.game.players.get(playerID - 1).getLives();

        vbox.getChildren().get(lives - 1).setVisible(false);

        animateFadeInFadeOutTransition(hurt, 0.4);

        playAudioClip("hurt");
    }

    private void animateShield(int playerID) {
        // todo: shield and shoot should be connected. shield reflecting the bullet.

        ImageView imageView = switch (playerID) {
            case 0 -> imageViewShieldPlayer1;
            case 1 -> imageViewShieldPlayer2;
            case 2 -> imageViewShieldPlayer3;
            default -> throw new RuntimeException("illegal playerID");
        };

        // TODO: move to upscale transition.
        imageView.setVisible(true);
        ScaleTransition upscaleTransition = new ScaleTransition(Duration.millis(500));
        upscaleTransition.setNode(imageView);
        upscaleTransition.setByX(0.3);
        upscaleTransition.setByY(0.3);
        upscaleTransition.setCycleCount(2);
        upscaleTransition.setAutoReverse(true);
        upscaleTransition.play();
    }

    public void animateLoadedFade(int playerID, double fadeValue) {
        ImageView imageView = switch (playerID) {
            case 0 -> imageViewLoadedPlayer1;
            case 1 -> imageViewLoadedPlayer2;
            case 2 -> imageViewLoadedPlayer3;
            default -> throw new RuntimeException("illegal playerID");
        };

        animateFadeTransition(imageView, fadeValue);

        playAudioClip("vanish");
    }

    private void animateFadeTransition(Node node, double fadeValue) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(500));
        fadeTransition.setNode(node);
        fadeTransition.setByValue(fadeValue);
        fadeTransition.play();
    }

    private void animateFadeInFadeOutTransition(Node node, double fadeValue) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000));
        fadeTransition.setNode(node);
        fadeTransition.setByValue(fadeValue);
        fadeTransition.setCycleCount(2);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }

    private void animateScaleTransition(Node node, double byX, double byY, double duration) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration / 2));
        scaleTransition.setNode(node);
        scaleTransition.setByX(byX);
        scaleTransition.setByY(byY);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    private void playAudioClip(String audioClipName) {
        AudioClip audioClip = new AudioClip(
                Objects.requireNonNull(getClass().getResource("Music/" + audioClipName + ".mp3")).toExternalForm());
        audioClip.setVolume(0.2);
        audioClip.play();
    }

    private void playMediaPlayer() {
        player = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource("Music/medieval.mp3")).toString()));
        player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
        player.setVolume(0.3);
        player.play();
    }


    private void bindArrowLines() {
        arrowhead11.visibleProperty().bind(arrow1.visibleProperty());
        arrowhead12.visibleProperty().bind(arrow1.visibleProperty());
        arrowhead21.visibleProperty().bind(arrow2.visibleProperty());
        arrowhead22.visibleProperty().bind(arrow2.visibleProperty());

        arrowhead11.opacityProperty().bind(arrow1.opacityProperty());
        arrowhead12.opacityProperty().bind(arrow1.opacityProperty());
        arrowhead21.opacityProperty().bind(arrow2.opacityProperty());
        arrowhead22.opacityProperty().bind(arrow2.opacityProperty());

        // cannot animate stroke width :(
    }

    private void initializeBulletsDictionary() {
        bulletsDictionary = new HashMap<>();
        bulletsDictionary.put(new Pair(1, 2), bullet1to2);
        bulletsDictionary.put(new Pair(1, 3), bullet1to3);
        bulletsDictionary.put(new Pair(2, 1), bullet2to1);
        bulletsDictionary.put(new Pair(2, 3), bullet2to3);
        bulletsDictionary.put(new Pair(3, 1), bullet3to1);
        bulletsDictionary.put(new Pair(3, 2), bullet3to2);
    }


}
