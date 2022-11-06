package david.geko.geko;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;


public class Main extends Application {
    static Game game;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Views/view_start.fxml")));
        Scene scene = new Scene(root, 1240, 720);

        stage.setTitle("Geko {shoot-shield-load}");
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("Images/icon.png"))));
        stage.setScene(scene);
        stage.show();

        game = new Game();
        game.myPlayer = new Player();

        game.addPlayers(game.myPlayer);
        game.addPlayers(new Player());
        game.addPlayers(new Player());
    }

    public static void main(String[] args) {
        launch();
    }
}
// https://stackoverflow.com/questions/16748030/difference-between-arrays-aslistarray-and-new-arraylistintegerarrays-aslist
//        Integer[] arr = {1,2,3,4,5};
//        java.util.List<Integer> l = java.util.Arrays.asList(arr);
//        java.util.Collections.shuffle(l);
//        for (int i = 0; i <arr.length ; i++) {
//            System.out.print(arr[i] + " ");
//        }
//        System.out.println();
//        System.out.println(l);