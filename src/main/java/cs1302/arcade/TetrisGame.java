package cs1302.arcade;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TetrisGame
{
	Tetris tetris;
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

	TetrisGame(Stage stage)
	{
		EventHandler<ActionEvent> update = (e) -> render();

		hBox = new HBox();
		Scene scene = new Scene(hBox);
		stage.setScene(scene);
		gameTiles = new VBox();
		gameTiles.getChildren().addAll(setupPlayField());
		tetris = new Tetris(stage);
		infoTiles = generateInfo();
		hBox.getChildren().addAll(gameTiles, infoTiles);
		stage.show();
		render();
		stage.addEventFilter(ActionEvent.ACTION, update);
	}

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

	private HBox generateLabel(String text)
	{
		return generateLabel(text, "Courier", 25);
	}

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

	HBox displayPieceFullWidth(Tetromino t)
	{
		HBox h = new HBox();
		h.getChildren().add(generateInfoBorder(2, 2));
		h.getChildren().add(displayPiece(t));
		h.getChildren().add(generateInfoBorder(2, 2));

		return h;
	}

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

	VBox generateInfoBorder(int h, int w)
	{
		VBox retBox = new VBox();
		for (int x = 0; x < h; x++)
		{
			retBox.getChildren().add(generateInfoRow(w));
		}
		return retBox;
	}

	HBox generateInfoRow(int w)
	{
		HBox retBox = new HBox();
		for (int x = 0; x < w; x++)
		{
			retBox.getChildren().add(new ImageView(textures[8]));
		}
		return retBox;
	}

	HBox generatePlayFieldRow()
	{
		return generateFullRow(textures[0]);
	}

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

	HBox generateFullRow(Image i)
	{
		return generateRow(i, 10);
	}

	HBox[] setupPlayField()
	{
		HBox[] retBoxes = new HBox[20];
		for (int i = 0; i < 20; i++)
		{
			retBoxes[i] = generatePlayFieldRow();
		}
		return retBoxes;
	}

	private void render()
	{
		if (!isGameOver)
		{
			isGameOver = tetris.isGameOver();
			for (int x = 0; x < 10; x++)
			{
				for (int y = 1; y < 21; y++)
				{
					((ImageView) ((HBox) gameTiles.getChildren().get(y - 1)).getChildren().get(x))
							.setImage(textures[tetris.getFallingStage()[x][y] |
									tetris.getStationaryStage()[x][y]]
							);
				}
			}

			((HBox) infoTiles.getChildren().get(4)).getChildren()
					.set(1, displayPiece(tetris.getNextFall()));
			setScoreText(0, tetris.getScore());
			setScoreText(1, tetris.getLevel());
			setScoreText(2, tetris.getLinesCleared());

			if (isGameOver)
			{
				AnimationTimer animationTimer = new GameOver();
				animationTimer.start();
			}
		}
	}

	private void gameOver(int y)
	{

		for (int x = 0; x < 10; x++)
		{
			if ((tetris.getFallingStage()[x][y] | tetris.getStationaryStage()[x][y]) != 0)
				((ImageView) ((HBox) gameTiles.getChildren().get(y - 1)).getChildren().get(x))
						.setImage(textures[9]);
		}

	}

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
			}
		}
	}

	private void setScoreText(int box, int score)
	{
		((Label) ((HBox) ((VBox) infoTiles.getChildren().get(6)).getChildren().get(box * 3 + 1))
				.getChildren().get(1)).setText(String.valueOf(score));
	}
}
