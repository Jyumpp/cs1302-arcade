
package cs1302.arcade;

import javafx.application.Application;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class Checkers extends Application {

    Stage stage = null;
    Private VBox vbox = new VBox();

    //default constructor
    public Checkers() {
        stage.initModality(Modality.APPLICATION_MODAL);
        start(stage);
    } //Checkers

    public Parent board() {

        Pane root = new Pane();
        root.setPrefSize(800,800);
        vbox.getChildren()addAll();
        return vbox;

    } //board

    public void start(Stage stage) {
        this.stage = stage;
        Scene scene = new Scene(board());
        stage.setTitle("CHECKERS!");
        stage.setScene(scene);
        stage.show();
    } //start

    public Checkers(Stage stage) {

        System.out.println("test");

    } //Checkers

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
