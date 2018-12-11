package cs1302.arcade;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * This class produces the majority of the GUI elements for the
 * Tetris game.
 *
 * @author Hunter Halloran
 * @author Calvin Childress
 */
public class TetrisGame
{
	Tetris tetris;
	Stage stage;
	HBox hBox;
	VBox gameTiles, infoTiles;
	boolean isGameOver = false;

	Image[] textures = {
			getImageFromFile("textures/grey.png"),
			getImageFromFile("textures/cyan.png"),
			getImageFromFile("textures/blue.png"),
			getImageFromFile("textures/orange.png"),
			getImageFromFile("textures/yellow.png"),
			getImageFromFile("textures/green.png"),
			getImageFromFile("textures/magenta.png"),
			getImageFromFile("textures/red.png"),
			getImageFromFile("textures/background.png"),
			getImageFromFile("textures/gameover.png")
	};

	/**
	 * This constructor creates the main panel for the game.
	 *
	 * @param stage the stage passed from the main arcade app
	 *              to put the game into
	 */
	TetrisGame(Stage stage)
	{
		this.stage = stage;
		EventHandler<ActionEvent> update = (e) -> render();

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Tutorial!");
		alert.setHeaderText("How to Play:");
		alert.setContentText("Left and Right: Move the piece back and forth!\n" +
				"Up: Rotate the piece clockwise!\n" +
				"Down: Speed the piece up!\n" +
				"Z: Rotate the piece counterclockwise!");

		alert.showAndWait();

		hBox = new HBox();
		Scene scene = new Scene(hBox);
		stage.setScene(scene);
		gameTiles = new VBox();
		gameTiles.getChildren().addAll(setupPlayField());
		tetris = new Tetris(stage);
		infoTiles = generateInfo();
		hBox.getChildren().addAll(gameTiles, infoTiles);
		stage.show();
		stage.requestFocus();
		render();
		stage.addEventFilter(ActionEvent.ACTION, update);
	}

	/**
	 * Returns an Image object from a given file name.
	 *
	 * @param fileName file to get the image from
	 * @return new Image object
	 */
	Image getImageFromFile(String fileName)
	{
		Image returnImage;
		try
		{
			returnImage = new Image(new FileInputStream(fileName), 32, 32, true, false);

		} catch (FileNotFoundException e)
		{
			returnImage = null;
			System.out.println("error");
		}
		return returnImage;
	}

	/**
	 * Generates the right-hand information portion of the display.
	 *
	 * @return populated VBox of information
	 */
	VBox generateInfo()
	{
		VBox vBox = new VBox();

		vBox.getChildren().addAll(generateFullRow(textures[8]),
				generateFullRow(textures[8]),
				generateLabel("Next"),
				generateFullRow(textures[8]),
				displayPieceFullWidth(tetris.getNextFall()),
				generateFullRow(textures[8]),
				generateLevelAndScore());

		return vBox;
	}

	/**
	 * Generates a formatted HBox with borders and text,
	 * with predefined font settings.
	 *
	 * @param text the text to be put into a label
	 * @return populated HBox
	 */
	private HBox generateLabel(String text)
	{
		return generateLabel(text, "Courier", 25);
	}

	/**
	 * Generates a formatted HBox with borders and text.
	 *
	 * @param text     the text to be put into a label
	 * @param font     the font family to use
	 * @param fontSize the font size to use
	 * @return populated HBox
	 */
	private HBox generateLabel(String text, String font, int fontSize)
	{
		Label nextLabel = new Label(text);
		nextLabel.setFont(Font.font(font, fontSize));
		nextLabel.setAlignment(Pos.CENTER);
		nextLabel.setPrefSize(128, 32);

		HBox h = new HBox();
		h.getChildren().addAll(generateInfoBorder(1, 3),
				nextLabel,
				generateInfoBorder(1, 3));

		return h;
	}

	/**
	 * Generates a formatted VBox with all score based elements
	 * of the program.
	 *
	 * @return populated VBox
	 */
	private VBox generateLevelAndScore()
	{
		VBox vBox = new VBox();

		vBox.getChildren().addAll(generateLabel("Score"),
				generateLabel("0", "Courier", 20),
				generateFullRow(textures[8]),
				generateLabel("Level"),
				generateLabel("0", "Courier", 20),
				generateFullRow(textures[8]),
				generateLabel("Lines"),
				generateLabel("0", "Courier", 20),
				generateInfoBorder(5, 10));

		return vBox;
	}

	/**
	 * Generates a display of a given Tetromino with a width
	 * of ten units.
	 *
	 * @param t the Tetromino to use
	 * @return populated HBox
	 */
	HBox displayPieceFullWidth(Tetromino t)
	{
		HBox h = new HBox();
		h.getChildren().add(generateInfoBorder(2, 2));
		h.getChildren().add(displayPiece(t));
		h.getChildren().add(generateInfoBorder(2, 2));

		return h;
	}

	/**
	 * Generates a display of a given Tetromino.
	 *
	 * @param t the Tetromino to use
	 * @return populated VBox
	 */
	VBox displayPiece(Tetromino t)
	{
		VBox retBox = new VBox();

		for (int x = 0; x < 2; x++)
		{
			retBox.getChildren().add(generateRow(textures[0], 6));
		}

		if (t == null)
		{
			return retBox;
		}

		return buildShapes(t, retBox);
	}

	/**
	 * Builds the actual shape of a given Tetromino.
	 *
	 * @param t      the Tetromino to use
	 * @param retBox the VBox to apply the shape to
	 * @return populated VBox
	 */
	private VBox buildShapes(Tetromino t, VBox retBox)
	{
		int offset = t.getPieceType().name().equals("O") ? 2 : 1;
		for (int y = 0; y < t.getBaseTetromino().length; y++)
		{
			for (int x = 0; x < t.getBaseTetromino()[0].length; x++)
			{
				((ImageView) ((HBox) retBox.getChildren().get(y)).getChildren().get(x + offset))
						.setImage(textures[t.getBaseTetromino()[y][x]]);
			}
		}
		return retBox;
	}

	/**
	 * Generates a border of a given height and width with
	 * texture #8.
	 *
	 * @param h the height
	 * @param w the width
	 * @return populated VBox as a border
	 */
	VBox generateInfoBorder(int h, int w)
	{
		VBox retBox = new VBox();
		for (int x = 0; x < h; x++)
		{
			retBox.getChildren().add(generateInfoRow(w));
		}
		return retBox;
	}

	/**
	 * Generates a row of given width with texture #8.
	 *
	 * @param w the width
	 * @return populated HBox of textures
	 */
	HBox generateInfoRow(int w)
	{
		HBox retBox = new HBox();
		for (int x = 0; x < w; x++)
		{
			retBox.getChildren().add(new ImageView(textures[8]));
		}
		return retBox;
	}

	/**
	 * Generates a row of 10 units with texture #0.
	 *
	 * @return populated HBox of textures
	 */
	HBox generatePlayFieldRow()
	{
		return generateFullRow(textures[0]);
	}

	/**
	 * Generates a row of a given width and given texture.
	 *
	 * @param i the texture to use
	 * @param w the width
	 * @return populated HBox of textures
	 */
	HBox generateRow(Image i, int w)
	{
		HBox retBox = new HBox();
		for (int j = 0; j < w; j++)
		{
			ImageView imageView = new ImageView(i);
			retBox.getChildren().add(imageView);
		}
		return retBox;
	}

	/**
	 * Generates a row of 10 units for a given texture.
	 *
	 * @param i the texture to use
	 * @return populated HBox of textures
	 */
	HBox generateFullRow(Image i)
	{
		return generateRow(i, 10);
	}

	/**
	 * Generates an array of HBoxes for each row and column of the play area.
	 *
	 * @return an HBox array of texture #0.
	 */
	HBox[] setupPlayField()
	{
		HBox[] retBoxes = new HBox[20];
		for (int i = 0; i < 20; i++)
		{
			retBoxes[i] = generatePlayFieldRow();
		}
		return retBoxes;
	}

	/**
	 * Updates the play area and information area with proper details
	 * from the Tetris object.
	 */
	private void render()
	{
		if (!isGameOver) //only run once after a game over
		{
			isGameOver = tetris.isGameOver();
			for (int x = 0; x < 10; x++) //texture of falling or stationary stages
			{
				for (int y = 1; y < 21; y++)
				{
					((ImageView) ((HBox) gameTiles.getChildren().get(y - 1)).getChildren().get(x))
							.setImage(textures[tetris.getFallingStage()[x][y] |
									tetris.getStationaryStage()[x][y]]);
				}
			}
			((HBox) infoTiles.getChildren().get(4)).getChildren() //set next piece display
					.set(1, displayPiece(tetris.getNextFall()));
			setScoreText(0, tetris.getScore()); //update score boxes
			setScoreText(1, tetris.getLevel());
			setScoreText(2, tetris.getLinesCleared());

			if (isGameOver) //makes sure this only runs once
			{
				AnimationTimer animationTimer = new GameOver();
				animationTimer.start();
			}
		}
	}

	/**
	 * Method indicating behavior upon Game Over animation.
	 *
	 * @param y the height of the animation
	 */
	private void gameOver(int y)
	{

		for (int x = 0; x < 10; x++) //set all non #0 textures to texture #9
		{
			if ((tetris.getFallingStage()[x][y] | tetris.getStationaryStage()[x][y]) != 0)
				((ImageView) ((HBox) gameTiles.getChildren().get(y - 1)).getChildren().get(x))
						.setImage(textures[9]);
		}

	}

	/**
	 * Describes the behavior upon Game Over.
	 */
	private class GameOver extends AnimationTimer
	{
		int y = 10;

		/**
		 * This method needs to be overridden by extending classes. It is going to
		 * be called in every frame while the {@code AnimationTimer} is active.
		 *
		 * @param now The timestamp of the current frame given in nanoseconds. This
		 *            value will be the same for all {@code AnimationTimers} called
		 *            during one frame.
		 */
		@Override
		public void handle(long now)
		{
			gameOver(y++ / 10);
			if (y >= 210)
			{
				stop();
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Game Over :(");
				alert.setHeaderText("Game Over! Good Job!");
				alert.setContentText("You Reached Level " + tetris.getLevel() +",\n" +
						"Cleared " + tetris.getLinesCleared() + " Lines,\n" +
						"And Scored " + tetris.getScore() + " Points!");

				alert.show();
				alert.setOnCloseRequest((i)->stage.close());
			}
		}
	}

	/**
	 * Sets scores in the proper display box.
	 *
	 * @param box   which box to update
	 * @param score what to update the box with
	 */
	private void setScoreText(int box, int score)
	{
		//It's messy but it's just a lot of boxes inside other boxes :/
		((Label) ((HBox) ((VBox) infoTiles.getChildren().get(6)).getChildren().get(box * 3 + 1))
				.getChildren().get(1)).setText(String.valueOf(score));
	}
}
