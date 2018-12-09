package cs1302.arcade;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

//in progress 12:36am 12/4
public class CheckersPiece extends StackPane {

    public CheckersType type;
    double oldX, oldY;
    double newX, newY;

    public CheckersPiece(CheckersType type, int x, int y) {

        this.type = type;
        move(x, y);
        Circle circle = new Circle(25);
        if (type == CheckersType.RED) {
            circle.setFill(Color.RED.desaturate());
        } else {
            circle.setFill(Color.BLACK.desaturate());
        } //if
        circle.setTranslateX(25.0); //places in the center of squares
        circle.setTranslateY(25.0);

        getChildren().add(circle);
        //when the user clicks on desired square, those values get passes to newX/newY
        setOnMousePressed(e -> {
            newX = e.getSceneX();
            newY = e.getSceneY();
        });
        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - newX + oldX, e.getSceneY() - newY + oldY);
        });

    } //CheckersPiece

    public void move(int x, int y) {
        oldX = x * 100;
        oldY = y * 100;
        relocate(oldX, oldY); //inherited from the Node class
    } //move

    public CheckersType getType() {
        return type;
    } //getType

    public double getOldX() {
        return oldX;
    } //getOldX

    public double getOldY() {
        return oldY;
    } //getOldY

    public void cancel() {
        relocate(oldX, oldY);
    } //cancel

} //CheckersPiece

