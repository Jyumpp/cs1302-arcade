
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
    //created 8x8 array that will be the gameboard
    private CheckersSquare[][] board = new CheckersSquare[8][8];
    private Group squares = new Group();
    private Group checkers = new Group();

    public Checkers(Stage stage) {
        System.out.println("test");
        board(stage);
    } //Checkers

    public void board(Stage stage) {

        this.stage = stage;

        VBox vbox = new VBox();
        Pane root = new Pane();
        root.setPrefSize(800, 800);
        //Button b = new Button("test");
        root.getChildren().addAll(squares, checkers);

        for(int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                CheckersSquare square = new CheckersSquare((i+j) % 2 == 0, i, j);
                board[i][j] = square;
                squares.getChildren().add(square);
            }
        }
        vbox.getChildren().addAll(root);

        Scene s = new Scene(vbox);
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
