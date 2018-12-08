package cs1302.arcade;

import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Random;


public class Tetris
{
	private class Position
	{
		private float xPos = 0, yPos = 0;

		Position(float x, float y)
		{
			xPos = x;
			yPos = y;
		}

		void setX(float x)
		{
			xPos = x;
		}

		void setY(float y)
		{
			yPos = y;
		}

		float getX()
		{
			return xPos;
		}

		float getY()
		{
			return yPos;
		}
	}

	private byte[][] stationaryStage = new byte[10][20];
	private byte[][] fallingStage = new byte[10][20];
	private Tetromino currentFalling;
	private long freeTime = 1000;
	private static final int LEFT = -1, RIGHT = 1;

	private LongProperty dropTimer = new SimpleLongProperty(0);

	private Position[][] clockwiseSpinTests = {
			{new Position(0, 0),
					new Position(-1, 0),
					new Position(-1, 1),
					new Position(0, -2),
					new Position(-1, -2)},
			{new Position(0, 0),
					new Position(1, 0),
					new Position(1, -1),
					new Position(0, 2),
					new Position(1, 2)},
			{new Position(0, 0),
					new Position(1, 0),
					new Position(1, 1),
					new Position(0, -2),
					new Position(1, -2)},
			{new Position(0, 0),
					new Position(-1, 0),
					new Position(-1, -1),
					new Position(0, 2),
					new Position(-1, 2)}};
	private Position[][] counterClockwiseSpinTests = new Position[4][5];


	EventHandler<KeyEvent> keyboardEvent = new EventHandler<KeyEvent>()
	{
		@Override
		public void handle(KeyEvent event)
		{
			switch (event.getCode())
			{
				case LEFT:
					tryMove(LEFT);
					break;
				case RIGHT:
					tryMove(RIGHT);
					break;
				default:
					break;
			}
		}
	};

	Tetris(Stage stage)
	{
		init();
		stage.addEventFilter(KeyEvent.KEY_PRESSED, keyboardEvent);
	}

	private enum Tetromino
	{
		I, J, L, O, S, T, Z;

		int rotation = 0;
		int xPos = 0;
		int yPos = 0;
		float rotOffset;
		byte[][] tetromino;

		byte[][] rotateTetromino(int direction)
		{
			byte[][] returnByte = new byte[tetromino[0].length][];
			for (int b = 0; b < returnByte.length; b++)
			{
				returnByte[b] = new byte[tetromino.length];
			}
			if (direction == Tetris.LEFT)
			{
				for (int y = 0; y < tetromino.length; y++)
				{
					for (int x = 0; x < tetromino[0].length; x++)
					{
						returnByte[x][y] = tetromino[y][x];
					}
				}
			} else
			{
				for (int y = 0; y < tetromino.length; y++)
				{
					for (int x = tetromino[0].length; x > 0; x--)
					{
						returnByte[returnByte.length - x][y] = tetromino[y][x - 1];
					}
				}
			}
			return returnByte;
		}

		byte[][] getBaseTetromino()
		{
			switch (this.name())
			{
				case "I":
					rotOffset = 0.5f;
					return new byte[][]{
							{1, 1, 1, 1}};
				case "J":
					rotOffset = 1f;
					return new byte[][]{
							{2, 0, 0},
							{2, 2, 2}};
				case "L":
					rotOffset = 1f;
					return new byte[][]{
							{0, 0, 3},
							{3, 3, 3}};
				case "O":
					rotOffset = 0;
					return new byte[][]{
							{4, 4},
							{4, 4}};
				case "S":
					rotOffset = 1f;
					return new byte[][]{
							{0, 5, 5},
							{5, 5, 0}};
				case "T":
					rotOffset = 1f;
					return new byte[][]{
							{0, 6, 0},
							{6, 6, 6}};
				case "Z":
					rotOffset = 1f;
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
		counterClockwiseSpinTests[0] = clockwiseSpinTests[1];
		counterClockwiseSpinTests[1] = clockwiseSpinTests[0];
		counterClockwiseSpinTests[2] = clockwiseSpinTests[3];
		counterClockwiseSpinTests[3] = clockwiseSpinTests[2];

		clearStage(stationaryStage);
		clearStage(fallingStage);
		currentFalling = randomTetromino();
		currentFalling.tetromino = currentFalling.getBaseTetromino();
		currentFalling.tetromino = currentFalling.rotateTetromino(LEFT);
		currentFalling.yPos = 0;
		currentFalling.xPos = 4;
		placeFalling();
		tryFall();
	}

	private Tetromino randomTetromino()
	{
		Random r = new Random();
		return Tetromino.values()[r.nextInt(7)];
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

	private void displayStage()
	{
		for (int x = 0; x < 20; x++)
		{
			for (int y = 0; y < 10; y++)
			{
				System.out.print(fallingStage[y][x] + " ");
			}
			System.out.println();
		}
	}

	private void tryFall()
	{
		displayStage();

		dropTimer.set(freeTime);

		if (canFall())
		{
			clearStage(fallingStage);
			currentFalling.yPos++;
			placeFalling();
			new Thread(dropOnTimeZero).start();
		} else
		{
			new Thread(setOnTimeZero).start();
		}
	}

	private void tryMove(int direction)
	{
		if (canMove(direction))
		{
			clearStage(fallingStage);
			currentFalling.xPos += direction;
			placeFalling();
			dropTimer.set(freeTime);
		}
		displayStage();
	}

	private void tryRotate(int direction)
	{
		byte[][] tryTurn = currentFalling.rotateTetromino(direction);

		Position oldCenterLocation = new Position(
				currentFalling.xPos + currentFalling.tetromino[0].length / 2f,
				currentFalling.yPos + currentFalling.tetromino.length / 2f);

		for (int y = 0; y < tryTurn.length; y++)
		{
			for (int x = 0; x < tryTurn[0].length; x++)
			{


				/*fallingStage[currentFalling.xPos + x][currentFalling.yPos + y]
						= currentFalling.tetromino[y][x];*/
			}
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

	Runnable dropOnTimeZero = new Thread(() ->
	{
		long timer = dropTimer.get();
		while (timer > 0)
		{
			//System.out.println(dropTimer.get());
			try
			{
				Thread.sleep(1);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			dropTimer.set(--timer);
		}

		Platform.runLater(this::tryFall);
	});

	Runnable setOnTimeZero = new Thread(() ->
	{
		while (dropTimer.get() > 0)
		{
			//System.out.println(dropTimer.get());
			try
			{
				Thread.sleep(1);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			dropTimer.set(dropTimer.get() - 1);
		}

		Platform.runLater(this::fallToStat);
		System.out.println("Set!");
	});

	private boolean canFall()
	{
		Position[] checkUnder = getCheckPositions();

		for (Position p : checkUnder)
		{
			try
			{
				if (stationaryStage[(int) p.xPos][(int) p.yPos + 1] != 0)
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

	private boolean canMove(int direction)
	{
		Position[] checkBy = getCheckPositions();

		for (Position p : checkBy)
		{
			try
			{
				if (stationaryStage[(int) p.getX() + direction][(int) p.getY()] != 0)
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

	private Position[] getCheckPositions()
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
		return checkUnder;
	}

	private Position[] getCheckPositions(byte[][] tetromino, Position p)
	{
		Position[] checkUnder = new Position[4];
		int count = 0;
		for (int i = 0; i < tetromino.length; i++)
		{
			for (int j = 0; j < tetromino[0].length; j++)
			{
				if (tetromino[i][j] != 0)
				{
					checkUnder[count++] = new Position(j + p.getX(),
							i + p.getY());
				}
			}
		}
		return checkUnder;
	}


	private void fallToStat()
	{
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 20; y++)
			{
				if (fallingStage[x][y] != 0)
				{
					stationaryStage[x][y] = fallingStage[x][y];
				}
			}
		}
		clearStage(fallingStage);
	}
}
