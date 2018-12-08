package cs1302.arcade;

/**
 * Class that lists the types of pieces that exist. (Maybe make in CheckerPiece.java?)
 **/
//should be done 12:36am 12/4
public enum CheckersType {

    RED(1), BLACK(-1);
    final int direction;

    CheckersType(int direction) {
        this.direction = direction;
    } //CheckersPiece

} //CheckersType


