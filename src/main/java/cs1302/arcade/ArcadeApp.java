
package cs1302.arcade;

import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.stage.Modality;

/**
 * Main application class, that contains the main menu and launches each individual
 * game when the respective button is pressed.
 *
 * @author Calvin Childress
 * @author Hunter Halloran
 * */
public class ArcadeApp extends Application {

    public VBox vbox = null;

    @Override
    public void start(Stage stage) {

        this.vbox = new VBox();// main container

        menu();
        Scene scene = new Scene(vbox, 640, 480);
        stage.setTitle("Hunter and Calvin's Arcade!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        // the group must request input focus to receive key events
        // @see https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html#requestFocus--
        vbox.requestFocus();

    } // start

    public static void main(String[] args) {
        try {
            Application.launch(args);
        } catch (UnsupportedOperationException e) {
            System.out.println(e);
            System.err.println("If this is a DISPLAY problem, then your X server connection");
            System.err.println("has likely timed out. This can generally be fixed by logging");
            System.err.println("out and logging back in.");
            System.exit(1);
        } // try
    } // main

    public void menu() {

        Button tetris = new Button("Tetris");
        Button checkers = new Button("Checkers");
        vbox.getChildren().addAll(tetris, checkers);

        tetris.setOnAction(e -> {
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("TETRIS!");
            stage.initModality(Modality.APPLICATION_MODAL);

            TetrisGame t = new TetrisGame(stage);

        });
        checkers.setOnAction(e -> {
            VBox v = new VBox();
            Stage stage = new Stage();
            Scene scene = new Scene(v);
            stage.setResizable(false);
            stage.setTitle("CHECKERS!");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            Checkers c = new Checkers(stage);

        });

    } //menu

} // ArcadeApp

