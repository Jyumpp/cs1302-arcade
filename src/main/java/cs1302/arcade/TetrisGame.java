package cs1302.arcade;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TetrisGame
{
	Tetris tetris;
	VBox scene;

	Image[] textures = {
			getImageFromFile("textures/grey.png"),
			getImageFromFile("textures/cyan.png"),
			getImageFromFile("textures/blue.png"),
			getImageFromFile("textures/orange.png"),
			getImageFromFile("textures/yellow.png"),
			getImageFromFile("textures/green.png"),
			getImageFromFile("textures/magenta.png"),
			getImageFromFile("textures/red.png")
	};

	TetrisGame(Stage stage)
	{
		EventHandler<ActionEvent> update = (e) -> render();

		this.scene = new VBox();
		Scene scene = new Scene(this.scene);
		stage.setScene(scene);

		this.scene.getChildren().addAll(setupPlayField());
		stage.show();
		tetris = new Tetris(stage);
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

	HBox generatePlayFieldRow()
	{
		HBox retBox = new HBox();
		for (int i = 0; i < 10; i++)
		{
			ImageView imageView = new ImageView(textures[0]);
			retBox.getChildren().add(imageView);
		}
		return retBox;
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
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 20; y++)
			{
				((ImageView) ((HBox) scene.getChildren().get(y)).getChildren().get(x)).setImage(
						textures[tetris.getFallingStage()[x][y] | tetris.getStationaryStage()[x][y]]
				);
			}
		}
	}
}
