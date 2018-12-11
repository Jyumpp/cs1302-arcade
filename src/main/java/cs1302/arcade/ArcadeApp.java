
package cs1302.arcade;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.stage.Modality;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Main application class, that contains the main menu and launches each individual
 * game when the respective button is pressed.
 *
 * @author Calvin Childress
 * @author Hunter Halloran
 * */
public class ArcadeApp extends Application {

    public HBox hbox = null;

    @Override
    public void start(Stage stage) {

        this.hbox = new HBox();// main container

        menu();
        Scene scene = new Scene(hbox);
        stage.setTitle("Hunter and Calvin's Arcade!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        // the group must request input focus to receive key events
        // @see https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html#requestFocus--
        hbox.requestFocus();

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

	/**
	 * Returns an Image object from a given file name.
	 *
	 * @param fileName file to get the image from
	 * @return new Image object
	 */
	Image getImageFromFile(String fileName)
	{
		javafx.scene.image.Image returnImage;
		try
		{
			returnImage = new javafx.scene.image.Image(new FileInputStream(fileName), 150, 150, false, false);

		} catch (FileNotFoundException e)
		{
			returnImage = null;
			System.out.println("error");
		}
		return returnImage;
	}

    public void menu() {

        Button tetris = new Button();
		Image tetLogo = getImageFromFile("textures/tetrislogo.jpeg");
		tetris.setGraphic(new ImageView(tetLogo));
        Button checkers = new Button();
        Image checkLogo = getImageFromFile("textures/checkerslogo.jpeg");
        checkers.setGraphic(new ImageView(checkLogo));
        hbox.getChildren().addAll(tetris, checkers);

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

