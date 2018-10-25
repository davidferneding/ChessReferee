package Main;

import ChessField.ChessFieldHelper;
import ChessField.ChessFieldViewRepresentation;
import Pieces.Color;
import Player.DummyPlayer;
import Player.Player;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/***
 * Icons: material.io, free to use (Licence: Apache license version 2.0)
 * Style: bitbucket.org/agix-material-fx/materialfx-material-design-for-javafx, free to use (Licence: MIT)
 */
public class MainActivity extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        newWindowAction(primaryStage);
    }

    public void newWindowAction(Stage stage) {
        Scene scene = new Scene(new ApplicationViewHolder(this), 800, 800);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());

        stage.setTitle("ChessReferee by David Ferneding");
        stage.setScene(scene);
        stage.show();
    }

    public ChessFieldHelper newGameAction(ChessFieldViewRepresentation viewRepresentation, ApplicationViewHolder viewHolder, Player playerBlack, Player playerWhite) {
        //TODO: load Player.Player type
        ChessFieldHelper referee = new ChessFieldHelper(
                viewRepresentation,
                playerBlack,
                playerWhite,
                viewHolder);

        referee.startPlaying();

        viewHolder.onGameStartPause();

        return referee;
    }

    public void pauseGameAction(ChessFieldHelper referee, ApplicationViewHolder viewHolder) {
        if (referee != null) {
            referee.pauseGame();
            viewHolder.onGameStartPause();
        } else {
            System.out.println("referee is null");
        }
    }

    public void quitAction() {
        Platform.exit();
    }
}
