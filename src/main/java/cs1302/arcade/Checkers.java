
package cs1302.arcade;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Checkers /*extends Application*/ {

    Stage stage;
    private int board[][];

    //default constructor
//    public Checkers() {
//        stage.initModality(Modality.APPLICATION_MODAL);
//        start(stage);
//    } //Checkers



//    public void start(Stage stage) {
//        this.stage = stage;
//        Scene scene = new Scene(board());
//        stage.setTitle("CHECKERS!");
//        stage.setScene(scene);
//        stage.show();
//    } //start

    public Checkers(Stage stage) {
        System.out.println("test");
        board(stage);
    } //Checkers

    public void board(Stage stage) {

        this.stage = stage;

        VBox vbox = new VBox();
        Pane root = new Pane();
        root.setPrefSize(800,800);
        Button b = new Button("test");
        vbox.getChildren().addAll();
        Circle circle = new Circle(100.0, 100.0, 25.0,  );
        circle.setCenterX(100.0);
        circle.setCenterY(100.0);
        circle.setRadius(25.0);
        root.getChildren().addAll(circle);

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
