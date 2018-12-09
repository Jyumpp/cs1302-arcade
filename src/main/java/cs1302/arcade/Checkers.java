
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
    boolean turn = true;

    public Checkers(Stage stage) {
        System.out.println("test");
        board(stage);
    } //Checkers

    //test to see if a move is possible
    public CheckersMove testMove(CheckersPiece piece, int x, int y){
        int x1 = toBoard(piece.getOldX());
        int y1 = toBoard(piece.getOldY());


        if((x+y) % 2 == 0 || board[x][y].hasPiece()){
            return new CheckersMove(CheckersMoveType.NONE);
        } //if
        if(Math.abs(x - x1) == 1 && (y-y1) == piece.getType().direction){
            return new CheckersMove(CheckersMoveType.MOVE);
        } //if
        else if(Math.abs(x - x1) == 2 && (y-y1) == piece.getType().direction * 2){
            int a = x1 + (x - x1) / 2;
            int b = y1 + (y - y1) / 2;

            if(board[a][b].hasPiece() && board[a][b].getPiece().getType() != piece.getType()){
                return new CheckersMove(CheckersMoveType.JUMP, board[a][b].getPiece());
            } //inner if
        } //else if
        return new CheckersMove(CheckersMoveType.NONE);
    } //testMove

    //shows the move placement of the checker piece
    public int toBoard(double placement){
        return (int)(placement + 100 / 2) / 100;
    } //toBoard

    public CheckersPiece placePiece(CheckersType type, int x, int y){
        CheckersPiece piece = new CheckersPiece(type, x, y);

        piece.setOnMouseReleased(e -> {
            int x1 = toBoard(piece.getLayoutX());
            int y1 = toBoard(piece.getLayoutY());

            CheckersMove move;

            if(x1 >= 8 || x1 < 0 || y1 >= 8 || y1 < 0){
                move = new CheckersMove(CheckersMoveType.NONE);
            } //if
            else if(piece.getType() == CheckersType.BLACK && turn){
                move = testMove(piece, x1, y1);
            } //else if
            else if(piece.getType() == CheckersType.RED && !turn){
                move = testMove(piece, x1, y1);
            } //else if
            else{
                move = new CheckersMove(CheckersMoveType.NONE);
            } //else

            int a = toBoard(piece.getOldX());
            int b = toBoard(piece.getOldY());

            switch (move.getMove()) {
                case NONE:
                    piece.cancel(); //cancels the turn if there is no move
                    break;
                case MOVE:
                    piece.move(x1, y1);
                    board[a][b].setPiece(null);
                    board[x1][y1].setPiece(piece);
                    turn = !turn; //changes turn to the other player
                    if(turn){
                        //set label to black player
                    } //if
                    else {
                        //set label to red player
                    } //else
                    break;
                case JUMP:
                    turn = !turn; //changed turn to the other player
                    piece.move(x1, y1);
                    board[a][b].setPiece(null);
                    board[x1][y1].setPiece(piece);

                    CheckersPiece opponent = move.getPiece();
                    board[toBoard(opponent.getOldX())][toBoard(opponent.getOldY())].setPiece(null);
                    piece.getChildren().remove(opponent); //removes the opponents piece

                    //if the piece is black, then increase red score
                    //if the piece is red, then increase black score
                    //if one player reaches 12 points, then display a win message for that player
            } //switch


        });
        return piece;
    } //CheckersPiece

    public void board(Stage stage) {

        this.stage = stage;

        VBox vbox = new VBox();
        Pane root = new Pane();
        root.setPrefSize(800, 800);
        root.getChildren().addAll(squares, checkers);

        for(int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                CheckersPiece piece = null;

                CheckersSquare square = new CheckersSquare((i+j) % 2 == 0, i, j);
                board[i][j] = square;
                squares.getChildren().add(square);

                if((i+j) % 2 != 0 && i <= 2){
                    piece = placePiece(CheckersType.RED, i, j);
                } //if
                if((i+j) % 2 != 0 && i >= 5){
                    piece = placePiece(CheckersType.BLACK, i, j);
                } //if
                if (piece != null){
                    square.setPiece(piece);
                    checkers.getChildren().add(piece);
                }
            } //for j
        } //for i
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
