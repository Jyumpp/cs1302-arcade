
package cs1302.arcade;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Checkers {

    Stage stage;
    private int board[][];

    public Checkers(Stage stage) {
        System.out.println("test");
        board(stage);
    } //Checkers

    public void board(Stage stage) {

        this.stage = stage;

        VBox vbox = new VBox();
        Pane root = new Pane();
        root.setPrefSize(800, 800);
        Button b = new Button("test");
        vbox.getChildren().addAll();

        Scene s = new Scene(root);
        stage.setScene(s);


    } //board

    public static void main(String[] args) {
        try {
            Application.launch(args);
        } catch (UnsupportedOperationException e) {
            System.out.println(e);
            System.err.println("X server error, log out and log back in");
            System.exit(1);
        } //try
    } //main

} //Checkers
