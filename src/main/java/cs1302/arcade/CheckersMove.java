package cs1302.arcade;

public class CheckersMove {

    private CheckersMoveType move;
    private CheckersPiece piece;

    public CheckersPiece getPiece(){
        return piece;
    } //getPiece

    public CheckersMoveType getMove(){
        return move;
    } //getMove

    public CheckersMove(CheckersMoveType type){
        this(type, null);
    }

    //checks the result of the move
    public CheckersMove(CheckersMoveType type, CheckersPiece piece){
        this.move = type;
        this.piece = piece;
    } //CheckersMove

} //CheckersMove
