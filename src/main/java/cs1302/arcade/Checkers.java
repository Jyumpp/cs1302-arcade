
package cs1302.arcade;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
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
    private boolean turn = true;
    private int scoreRed = 0;
    private Text scoreKeepRed;
    private int scoreBlack = 0;
    private Text scoreKeepBlack;
    private Text colorsTurn;

    public Checkers(Stage stage) {
        System.out.println("test");
        board(stage);
    } //Checkers

    //test to see if a move is possible
    public CheckersMove testMove(CheckersPiece piece, int newX, int newY) {
        int x0 = toBoard(piece.getOldX());
        int y0 = toBoard(piece.getOldY());


        if ((newX + newY) % 2 == 0 || board[newX][newY].hasPiece()) {
            return new CheckersMove(CheckersMoveType.NONE);
        } //if
        if (Math.abs(newX - x0) == 1 && (newY - y0) == piece.getType().direction) {
            return new CheckersMove(CheckersMoveType.MOVE);
        } //if
        else if (Math.abs(newX -x0) == 2 && (newY - y0) == piece.getType().direction * 2) {
            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;

            if (board[x1][y1].hasPiece() && board[x1][y1].getPiece().getType() != piece.getType()) {
                return new CheckersMove(CheckersMoveType.JUMP, board[x1][y1].getPiece());
            } //inner if
        } //else if
        return new CheckersMove(CheckersMoveType.NONE);
    } //testMove

    //shows the move placement of the checker piece
    public int toBoard(double placement) {
        return (int) (placement + 100 / 2) / 100;
    } //toBoard

    public CheckersPiece placePiece(CheckersType type, int x, int y) {
        CheckersPiece piece = new CheckersPiece(type, x, y);

        piece.setOnMouseReleased(e -> {

            CheckersMove move;
            int newX = toBoard(piece.getLayoutX());
            int newY = toBoard(piece.getLayoutY());

            if (newX < 0 || newY < 0 || newX >= 8 || newY >= 8) {
                move = new CheckersMove(CheckersMoveType.NONE);
            } //if
            else if (piece.getType() == CheckersType.BLACK && turn) {
                move = testMove(piece, newX, newY);
            } //else if
            else if (piece.getType() == CheckersType.RED && !turn) {
                move = testMove(piece, newX, newY);
            } //else if
            else {
                move = new CheckersMove(CheckersMoveType.NONE);
            } //else

            int x0 = toBoard(piece.getOldX());
            int y0 = toBoard(piece.getOldY());

            switch (move.getMove()) {
                case NONE:
                    piece.cancel(); //cancels the turn if there is no move
                    break;
                case MOVE:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);
                    turn = !turn; //changes turn to the other player
                    if (turn) {
                        colorsTurn.setText("Blacks Turn");
                    } //if
                    else {
                        colorsTurn.setText("Reds Turn");
                    } //else
                    break;
                case JUMP:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null);
                    board[newX][newY].setPiece(piece);

                    CheckersPiece opponent = move.getPiece();
                    board[toBoard(opponent.getOldX())][toBoard(opponent.getOldY())].setPiece(null);
                    checkers.getChildren().remove(opponent); //removes the opponents piece
                    turn = !turn; //changed turn to the other player
                    //if the piece is black, then increase red score
                    if (opponent.getType() == CheckersType.BLACK) {
                        scoreRed++;
                        scoreKeepRed.setText("Red Score: " + scoreRed);
                        colorsTurn.setText("Blacks Turn!");
                    } //if
                    else {
                        scoreBlack++;
                        scoreKeepBlack.setText("Black Score: " + scoreBlack);
                        colorsTurn.setText("Reds Turn!");
                    } //else
                    if (scoreBlack == 12) {
                        Stage stage = new Stage();
                        VBox vbox = new VBox();
                        vbox.setStyle("-fx-alignment: center");
                        Button button = new Button("Main Menu");
                        button.setOnAction(event -> {
                            stage.close();
                            //should return out of the game
                        });
                        vbox.getChildren().addAll(new Text("Black has won!"), button);
                        Scene scene = new Scene(vbox);
                        stage.setScene(scene);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.show();
                    } //if
                    if (scoreRed == 12) {
                        Stage stage = new Stage();
                        VBox vbox = new VBox();
                        vbox.setStyle("-fx-alignment: center");
                        Button button = new Button("Main Menu");
                        button.setOnAction(event -> {
                            stage.close();
                            //should return out of the game
                        });
                        vbox.getChildren().addAll(new Text("Red has won!"), button);
                        Scene scene = new Scene(vbox);
                        stage.setScene(scene);
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.show();
                    } //if
                    //if the piece is red, then increase black score
                    //if one player reaches 12 points, then display a win message for that player
                    break;
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

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CheckersSquare square = new CheckersSquare((i + j) % 2 == 0, j, i);
                board[j][i] = square;
                squares.getChildren().add(square);
                CheckersPiece piece = null;
                if ((i + j) % 2 != 0 && i <= 2) {
                    piece = placePiece(CheckersType.RED, j, i);
                } //if
                if ((i + j) % 2 != 0 && i >= 5) {
                    piece = placePiece(CheckersType.BLACK, j, i);
                } //if
                if (piece != null) {
                    square.setPiece(piece);
                    checkers.getChildren().add(piece);
                } //if
            } //for j
        } //for i
        scoreKeepRed = new Text("Red score: " + scoreRed);
        scoreKeepBlack = new Text("Black score: " + scoreBlack);
        HBox h1 = new HBox();
        h1.getChildren().add(scoreKeepRed);
        HBox h2 = new HBox();
        h2.getChildren().add(scoreKeepBlack);
        colorsTurn = new Text("Blacks Turn");
        ToolBar toolBar = new ToolBar(h1, colorsTurn, h2);
        toolBar.setStyle("-fx-spacing: 267px");

        vbox.getChildren().addAll(toolBar, root);

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
