package cs1302.arcade;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * This class creates the grid of the checkerboard, extends the rectangle class
 * in order to create the blocks with a usable value.
 * <p>
 *
 * @author Calvin Childress
 * @author Hunter Halloran
 * </p>
 */
//in progress 12:36am 12/4
public class CheckersSquare extends Rectangle {

    private CheckersPiece piece;

    /**
     * Default constructor to create the color and size of the squares for the
     * board
     */
    public CheckersSquare(boolean brown, int x, int y) {
        setHeight(100.0); //1/8th 0f 800
        setWidth(100.0);
        relocate(x * 100, y * 100);
        if (brown) { //if boolean is true
            setFill(Color.BROWN); //set square to brown
        } else {
            setFill(Color.WHITE); //otherwise set square to white
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
    public void setPiece() {
        this.piece = piece;
    } //setPiece

} //CheckersPiece
