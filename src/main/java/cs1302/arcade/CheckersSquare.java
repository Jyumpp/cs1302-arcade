package cs1302.arcade;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This class creates the grid of the checkerboard, and contains the
 * logic for getting and setting the respective pieces
 * Extends Rectangle
 *
 * @author Calvin Childress
 * @author Hunter Halloran
 */
//in progress 12:36am 12/4
public class CheckersSquare extends Rectangle {

    private CheckersPiece piece;

    /**
     * Default constructor to create the color and size of the squares for the
     * board
     * @param dark boolean for if the previous square was dark, needed for alternating
     * @param x x coord
     * @param y y coord
     */
    public CheckersSquare(boolean dark, int x, int y) {
        setHeight(50.0); //found to be a good size that scaled well on multiple devices
        setWidth(50.0);
        relocate(x * 50, y * 50);
        if (dark) { //if the previous square was light, make the next dark
            setFill(Color.MAROON.darker()); //set square to brown
        } else {
            setFill(Color.BEIGE); //otherwise set square to beige
        }
    } //CheckersSquare

    /**
     * Checks square for piece
     */
    public boolean hasPiece() {
        return piece != null;
    } //hasPiece

    /**
     * Gets piece from the square
     */
    public CheckersPiece getPiece() {
        return piece;
    } //getPiece

    /**
     * Sets piece to a square
     */
    public void setPiece(CheckersPiece piece) {
        this.piece = piece;
    } //setPiece

} //CheckersPiece
