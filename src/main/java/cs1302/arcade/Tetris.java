package cs1302.arcade;

import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;

public class Tetris
{

	private byte[][] stationaryStage = new byte[10][20];
	private byte[][] fallingStage = new byte[10][20];
	private Tetromino currentFalling;
	private long freeTime = 1000;

	private LongProperty dropTimer = new SimpleLongProperty(0);

	Tetris(Stage stage) {
		init();
	}

	private enum Tetromino
	{
		I, J, L, O, S, T, Z;

		int rotation = 0;
		int xPos = 0;
		int yPos = 0;
		byte[][] tetromino;

		byte[][] getBaseTetromino()
		{
			switch (this.name())
			{
				case "I":
					return new byte[][]{
							{1},
							{1},
							{1},
							{1}};
				case "J":
					return new byte[][]{
							{0, 2},
							{0, 2},
							{2, 2}};
				case "L":
					return new byte[][]{
							{3, 0},
							{3, 0},
							{3, 3}};
				case "O":
					return new byte[][]{
							{4, 4},
							{4, 4}};
				case "S":
					return new byte[][]{
							{0, 5, 5},
							{5, 5, 0}};
				case "T":
					return new byte[][]{
							{0, 6, 0},
							{6, 6, 6}};
				case "Z":
					return new byte[][]{
							{7, 7, 0},
							{0, 7, 7}};
				default:
					return null;

			}
		}
	}

	public void init()
	{
		clearStage(stationaryStage);
		clearStage(fallingStage);
		currentFalling = Tetromino.Z;
		currentFalling.tetromino = currentFalling.getBaseTetromino();
		currentFalling.yPos = 0;
		currentFalling.xPos = 4;
		placeFalling();
		tryFall();
	}

	private void clearStage(byte[][] stage)
	{
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 20; y++)
			{
				stage[x][y] = 0;
			}
		}
	}

	private class Position
	{
		private int xPos = 0, yPos = 0;

		Position(int x, int y)
		{
			xPos = x;
			yPos = y;
		}

		void setX(int x)
		{
			xPos = x;
		}

		void setY(int y)
		{
			yPos = y;
		}

		int getX()
		{
			return xPos;
		}

		int getY()
		{
			return yPos;
		}
	}

	private void tryFall()
	{
		for(int x = 0; x < 20; x++) {
			for(int y = 0; y < 10; y++) {
				System.out.print(fallingStage[y][x] + " ");
			}
			System.out.println();
		}

		if (canFall())
		{
			clearStage(fallingStage);
			currentFalling.yPos++;
			placeFalling();
			dropTimer.set(freeTime);
			new Thread(dropOnTimeZero).start();
		} else {
			//dropTimer.set(freeTime);
			//dropOnTimeZero.start();
		}
	}

	private void placeFalling()
	{
		for (int y = 0; y < currentFalling.tetromino.length; y++)
		{
			for (int x = 0; x < currentFalling.tetromino[0].length; x++)
			{
				fallingStage[currentFalling.xPos + x][currentFalling.yPos + y]
						= currentFalling.tetromino[y][x];
			}
		}
	}

	Runnable dropOnTimeZero = new Thread(()->{
		while (dropTimer.get() > 0){
			//System.out.println(dropTimer.get());
			try{
				Thread.sleep(1);
			}catch(Exception e){
				e.printStackTrace();
			}
			dropTimer.set(dropTimer.get() - 1);
		}

		Platform.runLater(this::tryFall);
	});

	private boolean canFall()
	{
		Position[] checkUnder = new Position[4];
		int count = 0;
		for (int i = 0; i < currentFalling.tetromino.length; i++)
		{
			for (int j = 0; j < currentFalling.tetromino[0].length; j++)
			{
				if (currentFalling.tetromino[i][j] != 0)
				{
					checkUnder[count++] = new Position(j + currentFalling.xPos,
							i + currentFalling.yPos);
				}
			}
		}

		for (Position p : checkUnder)
		{
			try
			{
				if (stationaryStage[p.xPos][p.yPos + 1] != 0)
				{
					return false;
				}
			} catch (Exception e)
			{
				return false;
			}
		}

		return true;
	}

	private void fallToStat()
	{
		for(int x = 0; x < 10; x++) {
			for (int y = 0; y < 20; y++) {
				if(fallingStage[x][y] != 0) {
					stationaryStage[x][y] = fallingStage[x][y];
				}
			}
		}
	}
}
