package cs1302.arcade;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This class makes the red, black and king pieces, and contains the
 * logic for the coordinates when the mouse is pressed on them. It
 * also deals with the scaling and proportions
 * Extends StackPane to make the objects stack-able
 *
 * @author Calvin Childress
 * @author Hunter Halloran
 * */

public class CheckersPiece extends StackPane {

    public CheckersType type;
    double oldX, oldY;
    double newX, newY;

    /**
     * Creates the pieces (color, size, translation)
     * @param type type of piece (BLACK, RED, KING)
     * @param x x coord of piece
     * @param y y coord of piece
     * */
    public CheckersPiece(CheckersType type, int x, int y) {

        this.type = type;
        move(x, y);
        Circle circle = new Circle(20);
        if (type == CheckersType.RED) {
            circle.setFill(Color.RED.desaturate()); //sets color to red
        }
        else if (type == CheckersType.BLACK) {
            circle.setFill(Color.BLACK.desaturate()); //sets color to red
        } //if
        else if (type == CheckersType.KING){
            circle.setFill(Color.GOLD); //sets color to r o y a l t y
        } //if
        circle.setTranslateX(5.0); //places in the center of squares
        circle.setTranslateY(5.0); //proportional to scale

        getChildren().add(circle);
        //when the user clicks on desired square, those values get passes to newX/newY
        setOnMousePressed(e -> {
            newX = e.getSceneX(); //gets x axis of where the mouse was pressed
            newY = e.getSceneY(); //gets y axis of where the mouse was pressed
        });
        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - newX + oldX, e.getSceneY() - newY + oldY);
        }); //relocates the circle to the new x and y coords.

    } //CheckersPiece
    /**
     * Moves the piece from the old square to the new one
     * @param x x coord of piece
     * @param y y coord of piece
     * */
    public void move(int x, int y) {
        oldX = x * 50; //needed this scale so that it could find the proper square
        oldY = y * 50;
        relocate(oldX, oldY); //inherited from the Node class
    } //move
    /**
     * Gets Type of circle
     * */
    public CheckersType getType() {
        return type;
    } //getType
    /**
     * Gets the old x coordinate
     * */
    public double getOldX() {
        return oldX;
    } //getOldX
    /**
     * Gets the old y coordinate
     * */
    public double getOldY() {
        return oldY;
    } //getOldY
    /**
     * This method can cancel a move when it is invalid, and puts the circle back
     * in its starting position
     * */
    public void cancel() {
        relocate(oldX, oldY);
    } //cancel

} //CheckersPiece

