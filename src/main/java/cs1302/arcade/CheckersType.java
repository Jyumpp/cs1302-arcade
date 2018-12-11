package cs1302.arcade;

/**
 * Class that lists the types of pieces that exist.
 *
 * @author Calvin Childress
 * @author Hunter Halloran
 **/

public enum CheckersType {
    //lists the three types of checkers (king can be either color)
    RED(1), BLACK(-1), KING(0);
    final int direction;

    /**
     * Direction helps determine that the piece cannot move backwards
     * relative to its position
     * @param direction sets the direction based from the checkers type
     */
    CheckersType(int direction) {
        this.direction = direction;
    } //CheckersPiece

} //CheckersType


