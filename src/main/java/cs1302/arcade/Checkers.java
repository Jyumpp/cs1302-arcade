
package cs1302.arcade;
//import statements below
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;

/**
 * Main class which contains all the logic for the game, such as moving pieces, scoring,
 * and gameplay. Calls on helper classes to update the board and pieces when necessary
 *
 * @author Calvin Childress
 * @author Hunter Halloran
 * */
public class Checkers {

    Stage stage;
    //created 8x8 array that will be the game board
    private CheckersSquare[][] board = new CheckersSquare[8][8];
    private Group squares = new Group();
    private Group checkers = new Group();
    private boolean turn = true;
    private int scoreRed = 0;
    private Text scoreKeepRed;
    private int scoreBlack = 0;
    private Text scoreKeepBlack;
    private Text colorsTurn;
    private boolean king = false;

    /**
     * Default constructor that passes the stage from ArcadeApp
     * */
    public Checkers(Stage stage) {
        board(stage);
    } //Checkers

    /**
     * Tests to see if a move is valid and handles the outcome by moving
     * the piece accordingly
     * @param piece piece being moved
     * @param newX the x coord where the piece is moving to
     * @param newY the y coord where the piece is moving to
     * */
    private CheckersMove testMove(CheckersPiece piece, int newX, int newY) {
        int x0 = toBoard(piece.getOldX()); //assigns oldX to this variable
        int y0 = toBoard(piece.getOldY()); //assigns oldY to this variable


        //KING------------
//        if(king || piece.getType() == CheckersType.KING){
//            System.out.println("INSIDE KING");
//            piece = placePiece(CheckersType.KING, x0, y0);
//            if ((newX + newY) % 2 == 0 || board[newX][newY].hasPiece()) {
//                return new CheckersMove(CheckersMoveType.NONE);
//            } //if
//            if (Math.abs(newX - x0) == 1){
//                return new CheckersMove(CheckersMoveType.MOVE);
//            }
//            if(Math.abs(newX - x0) == 2){
//                int x1 = x0 + (newX - x0) / 2;
//
//                int y1 = y0 + (newY - y0) / 2;
//
//                if (board[x1][y1].hasPiece() &&
//                          board[x1][y1].getPiece().getType() != piece.getType()) {
//                    return new CheckersMove(CheckersMoveType.JUMP, board[x1][y1].getPiece());
//                } //inner if
//            }
//            king = false;
//            return new CheckersMove(CheckersMoveType.NONE);
//        } //king


        //if the move is not diagonal, then no move
        if ((newX + newY) % 2 == 0 || board[newX][newY].hasPiece()) {
            return new CheckersMove(CheckersMoveType.NONE);
        } //if
        //if the move is diagonal and in the right direction, then move
        if (Math.abs(newX - x0) == 1 && (newY - y0) == piece.getType().direction) {
            return new CheckersMove(CheckersMoveType.MOVE);
        } //if
        //if it is diagonal by two spaces, and in the right direction, then check for jump
        else if (Math.abs(newX -x0) == 2 && (newY - y0) == piece.getType().direction * 2) {
            int x1 = x0 + (newX - x0) / 2;
            int y1 = y0 + (newY - y0) / 2;
            //if the spot being jumped is the other color, then valid jump move
            if (board[x1][y1].hasPiece() &&
                    board[x1][y1].getPiece().getType() != piece.getType()) {
                return new CheckersMove(CheckersMoveType.JUMP, board[x1][y1].getPiece());
            } //inner if
        } //else if
        return new CheckersMove(CheckersMoveType.NONE);
    } //testMove

    /**
     * Shows the move placement of the piece
     * @param placement where the piece is currently
     * */
    private int toBoard(double placement) {
        return (int) (placement + 50 / 2) / 50; //proportional to our selected scale
    } //toBoard

    /**
     * Places the piece in the new square if valid move, and removes a piece if jumped.
     * Contains switch for each scenario.
     * @param type type of piece
     * @param x x coord
     * @param y y coord
     * */
    private CheckersPiece placePiece(CheckersType type, int x, int y) {
        CheckersPiece piece = new CheckersPiece(type, x, y);
        piece.setOnMouseReleased(e -> {

            CheckersMove move;
            int newX = toBoard(piece.getLayoutX()); //gets new placement
            int newY = toBoard(piece.getLayoutY());
            int y0 = toBoard(piece.getOldY()); //gets old placement of y
            int x0 = toBoard(piece.getOldX()); //gets old placement of x

           
            // KING--------
//            if((newY == 7 && piece.getType() == CheckersType.RED) ||
//                newY == 0 && piece.getType() == CheckersType.BLACK){
//                king = true;
//                //CheckersPiece p1 = null;
//                //p1 = placePiece(CheckersType.KING, newX, newY);
//                testMove(piece, newX, newY);
//            }


            //if the move is out of bounds
            if (newX < 0 || newY < 0 || newX >= 8 || newY >= 8) {
                move = new CheckersMove(CheckersMoveType.NONE);
            } //if
            //if the piece is black and its their turn
            else if (piece.getType() == CheckersType.BLACK && turn) {
                move = testMove(piece, newX, newY);
            } //else if
            //if the piece is red and its not black's turn
            else if (piece.getType() == CheckersType.RED && !turn) {
                move = testMove(piece, newX, newY);
            } //else if
            //otherwise no move
            else {
                move = new CheckersMove(CheckersMoveType.NONE);
            } //else

            switch (move.getMove()) {
                case NONE:
                    piece.cancel(); //cancels the turn if there is no move
                    break;
                case MOVE:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null); //remove piece from previous spot
                    board[newX][newY].setPiece(piece); //sets new piece
                    turn = !turn; //changes turn to the other player
                    if (turn) { //black's turn
                        colorsTurn.setText("Black's Turn");
                        colorsTurn.setFill(Color.BLACK);
                    } //if
                    else { //red's turn
                        colorsTurn.setText("Red's Turn");
                        colorsTurn.setFill(Color.RED);
                    } //else
                    break;
                case JUMP:
                    piece.move(newX, newY);
                    board[x0][y0].setPiece(null); //remove piece from previous spot
                    board[newX][newY].setPiece(piece); //sets new piece

                    CheckersPiece opponent = move.getPiece(); //gets the opponent piece
                    board[toBoard(opponent.getOldX())]
                            [toBoard(opponent.getOldY())].setPiece(null);
                    checkers.getChildren().remove(opponent); //removes the opponents piece
                    turn = !turn; //changed turn to the other player
                    //if the piece is black, then increase red score
                    if (opponent.getType() == CheckersType.BLACK) {
                        scoreRed++;
                        scoreKeepRed.setText("Red Score: " + scoreRed);
                        colorsTurn.setText("Black's Turn");
                        colorsTurn.setFill(Color.BLACK);
                    } //if
                    else { //if captured piece is red, then increase black score
                        scoreBlack++;
                        scoreKeepBlack.setText("Black Score: " + scoreBlack);
                        colorsTurn.setText("Red's Turn!");
                        colorsTurn.setFill(Color.RED);
                    } //else
                    if (scoreBlack == 12) { //if black wins, display win message
                        String winner = "Black";
                        win(winner);
                    } //if
                    if (scoreRed == 12) { //if red wins, display win message
                        String winner = "Red";
                        win(winner);
                    } //if
                    break;
            } //switch
        });
        return piece;
    } //CheckersPiece

    /**
     * Displays the win message for the deserving opponent
     * @param winner string of the winning color
     * */
    private void win(String winner){
        Stage stage = new Stage();
        VBox vbox = new VBox();
        vbox.setStyle("-fx-alignment: center");
        Button button = new Button("Main Menu");
        button.setOnAction(event -> {
            stage.close(); //go to main menu
            this.stage.close();
        });
        vbox.getChildren().addAll(new Text(winner + " has won!"), button);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    /**
     * Creates the GUI board that will host the game. Builds the board and pieces
     * @param stage needed to update the stage window of the Checkers App
     * */
    private void board(Stage stage) {

        this.stage = stage;

        VBox vbox = new VBox();
        Pane root = new Pane();
        root.setPrefSize(400, 400); //size found best to fit
        root.getChildren().addAll(squares, checkers);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                CheckersSquare square = new CheckersSquare((i + j) % 2 == 0, j, i);
                board[j][i] = square;
                squares.getChildren().add(square);
                CheckersPiece piece = null;
                if ((i + j) % 2 != 0 && i <= 2) { //alternates pieces on every other square
                    piece = placePiece(CheckersType.RED, j, i);
                } //if
                if ((i + j) % 2 != 0 && i >= 5) {
                    piece = placePiece(CheckersType.BLACK, j, i);
                } //if
                if (piece != null) { //for all pieces, add them to the respective squares
                    square.setPiece(piece);
                    checkers.getChildren().add(piece);
                } //if
            } //for j
        } //for i
        info(vbox, root); //calls on the info method
    } //board

    /**
     * Provides the header and footer windows that contain the score, turn,
     * and instructions for how the game is played
     * @param vbox vbox that will hold the scoring and turn info
     * @param root pane to be added to the vbox
     * */
    private void info(VBox vbox, Pane root){
        scoreKeepRed = new Text("Red score: " + scoreRed);
        scoreKeepBlack = new Text("Black score: " + scoreBlack);
        HBox h1 = new HBox();
        h1.getChildren().add(scoreKeepRed);
        HBox h2 = new HBox();
        h2.getChildren().add(scoreKeepBlack);
        colorsTurn = new Text("Black's Turn");
        ToolBar toolBar = new ToolBar(h1, colorsTurn, h2);
        toolBar.setStyle("-fx-spacing: 73px");

        Text rules = new Text("           Click and drag on the piece to the desired square");
        rules.setTextAlignment(TextAlignment.CENTER);
        HBox h3 = new HBox();
        h3.getChildren().add(rules);
        ToolBar rule = new ToolBar(h3);
        vbox.getChildren().addAll(toolBar, root, rule);

        Scene s = new Scene(vbox);
        stage.setScene(s);
    }
    /**
     * Main method which launches the Application
     * */
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
/**
 * This class contains the move information of an attempted move and the
 * piece information for that move.
 *
 * @author Calvin Childress
 * @author Hunter Halloran
 * */
class CheckersMove {

    private CheckersMoveType move;
    private CheckersPiece piece;

    /**
     * Gets piece
     * @return piece that is being moved
     * */
    public CheckersPiece getPiece(){
        return piece;
    } //getPiece
    /**
     * Gets move
     * @return move type that is being attempted
     * */
    public CheckersMoveType getMove(){
        return move;
    } //getMove
    /**
     * Constructor that starts the type of move, and sets piece to null
     * @param type what move is being attempted
     * */
    public CheckersMove(CheckersMoveType type){
        this(type, null);
    }
    /**
     * Overloaded constructor, checks the result of the move
     * @param type what move is being attempted
     * @param piece what piece is being moved
     * */
    public CheckersMove(CheckersMoveType type, CheckersPiece piece){
        this.move = type;
        this.piece = piece;
    } //CheckersMove

} //CheckersMove
