package cs1302.arcade;

import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Random;

public class Tetris
{
	Stage stage;
	private byte[][] stationaryStage = new byte[10][20];
	private byte[][] fallingStage = new byte[10][20];
	private Tetromino currentFalling, nextFall;
	private long freeTime = 1000, difficultyTime = 1000;
	static final int LEFT = -1, RIGHT = 1;

	Event e = new ActionEvent();

	private LongProperty dropTimer = new SimpleLongProperty(0);

	private int[][] clockwiseXSpinTests = {
			{0, -1, -1, 0, -1},
			{0, 1, 1, 0, 1},
			{0, 1, 1, 0, 1},
			{0, -1, -1, 0, -1}
	};
	private int[][] clockwiseYSpinTests = {
			{0, 0, 1, -2, -2},
			{0, 0, -1, 2, 2},
			{0, 0, 1, -2, -2},
			{0, 0, -1, 2, 2}
	};
	private int[][] clockwiseIXSpinTests = {
			{0, -2, 1, -2, 1},
			{0, -1, 2, -1, 2},
			{0, 2, -1, 2, -1},
			{0, 1, -2, 1, -2}
	};
	private int[][] clockwiseIYSpinTests = {
			{0, 0, 0, -1, 2},
			{0, 0, 0, 2, -1},
			{0, 0, 0, 1, -2},
			{0, 0, 0, -2, 1}
	};

	private int[][] counterClockwiseXSpinTests = new int[4][5];
	private int[][] counterClockwiseYSpinTests = new int[4][5];
	private int[][] counterClockwiseIXSpinTests = new int[4][5];
	private int[][] counterClockwiseIYSpinTests = new int[4][5];

	Tetris(Stage stage)
	{
		this.stage = stage;
		init();
		EventHandler<KeyEvent> keyboardEvent = event ->
		{
			switch (event.getCode())
			{
				case LEFT:
					tryMove(LEFT);
					break;
				case RIGHT:
					tryMove(RIGHT);
					break;
				case UP:
					tryRotate(RIGHT);
					break;
				case Z:
					tryRotate(LEFT);
					break;
			}
		};

		EventHandler<KeyEvent> speedUp = event ->
		{
			if (event.getCode() == KeyCode.DOWN)
			{
				freeTime = 50;
			}
		};

		EventHandler<KeyEvent> speedDown = event ->
		{
			if (event.getCode() == KeyCode.DOWN)
			{
				freeTime = difficultyTime;
			}
		};

		stage.addEventFilter(KeyEvent.KEY_PRESSED, keyboardEvent);
		stage.addEventFilter(KeyEvent.KEY_PRESSED, speedUp);
		stage.addEventFilter(KeyEvent.KEY_RELEASED, speedDown);
	}

	public byte[][] getStationaryStage()
	{
		return stationaryStage;
	}

	public byte[][] getFallingStage()
	{
		return fallingStage;
	}

	private void init()
	{
		counterClockwiseXSpinTests[0] = clockwiseXSpinTests[1];
		counterClockwiseXSpinTests[1] = clockwiseXSpinTests[0];
		counterClockwiseXSpinTests[2] = clockwiseXSpinTests[3];
		counterClockwiseXSpinTests[3] = clockwiseXSpinTests[2];

		counterClockwiseYSpinTests[0] = clockwiseYSpinTests[1];
		counterClockwiseYSpinTests[1] = clockwiseYSpinTests[0];
		counterClockwiseYSpinTests[2] = clockwiseYSpinTests[3];
		counterClockwiseYSpinTests[3] = clockwiseYSpinTests[2];

		for (int x = 0; x < 4; x++)
		{
			for (int y = 0; y < 5; y++)
			{
				counterClockwiseIXSpinTests[x][y] = -clockwiseIXSpinTests[x][y];
				counterClockwiseIYSpinTests[x][y] = -clockwiseIYSpinTests[x][y];
			}
		}
		currentFalling = new Tetromino();
		nextFall = new Tetromino();
		clearStage(stationaryStage);
		clearStage(fallingStage);
		nextFall = randomTetromino();
		nextFall.tetromino = nextFall.getBaseTetromino();
		setupNextFall();
	}

	private void setupNextFall()
	{
		currentFalling = nextFall;
		nextFall = randomTetromino();
		nextFall.tetromino = nextFall.getBaseTetromino();
		currentFalling.yPos = 0;
		currentFalling.xPos = 4;
		clearStage(fallingStage);
		placeFalling();
		tryFall();
	}

	private Tetromino randomTetromino()
	{
		Tetromino retTet = new Tetromino();
		Random r = new Random();
		retTet.setPieceType(Tetromino.Tetrominos.values()[r.nextInt(7)]);

		return retTet;
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
		stage.fireEvent(e);
	}

	private void tryFall()
	{
		displayStage();

		if (canFall())
		{
			clearStage(fallingStage);
			currentFalling.yPos++;
			placeFalling();
			dropTimer.set(freeTime);
			new Thread(dropOnTimeZero).start();
		} else
		{
			dropTimer.set(difficultyTime);
			new Thread(setOnTimeZero).start();
		}

		displayStage();
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
		Position rotPos = currentFalling.getRotLocation(direction);

		int[][] spinXTest = (currentFalling.getPieceType() == Tetromino.Tetrominos.I) ?
				(direction == RIGHT ? clockwiseIXSpinTests : counterClockwiseIXSpinTests) :
				(direction == RIGHT ? clockwiseXSpinTests : counterClockwiseXSpinTests);
		int[][] spinYTest = (currentFalling.getPieceType() == Tetromino.Tetrominos.I) ?
				(direction == RIGHT ? clockwiseIYSpinTests : counterClockwiseIYSpinTests) :
				(direction == RIGHT ? clockwiseYSpinTests : counterClockwiseYSpinTests);

		evaluateSpinTests(tryTurn, rotPos, direction,
				spinXTest[currentFalling.getRotation()], spinYTest[currentFalling.getRotation()]);
		clearStage(fallingStage);
		placeFalling();
		displayStage();
	}

	private void evaluateSpinTests(byte[][] tryTurn, Position rotPos,
								   int direction, int[] xPos, int[] yPos)
	{
		for (int i = 0; i < xPos.length; i++)
		{
			Position at = new Position(rotPos.getX() + xPos[i],
					rotPos.getY() + yPos[i]);
			Position[] checkAt = getCheckPositions(tryTurn, at);
			int count = 0;
			for (Position c : checkAt)
			{
				try
				{
					if (stationaryStage[c.getX()][c.getY()] == 0)
					{
						if (++count == 4)
						{
							set(tryTurn, at, direction);
							return;
						}
					} else break;
				} catch (Exception e)
				{
					break;
				}
			}
		}
	}

	private void set(byte[][] tryTurn, Position p, int direction)
	{
		if(direction == RIGHT) {
			currentFalling.intRotation();
		} else if (direction == LEFT){
			currentFalling.decRotation();
		}
		currentFalling.tetromino = tryTurn;
		currentFalling.xPos = p.getX();
		currentFalling.yPos = p.getY();
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

	private Runnable dropOnTimeZero = new Thread(() ->
	{
		long timer = dropTimer.get();
		while (timer > 0)
		{
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

	private Runnable setOnTimeZero = new Thread(() ->
	{
		while (dropTimer.get() > 0)
		{
			try
			{
				Thread.sleep(1);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			dropTimer.set(dropTimer.get() - 1);
		}
		if (!canFall())
		{
			freeTime = difficultyTime;
			fallToStat();
			displayStage();
			setupNextFall();
		} else
		{
			new Thread(dropOnTimeZero).start();
		}
	});

	private boolean canFall()
	{
		Position[] checkUnder = getCheckPositions();

		for (Position p : checkUnder)
		{
			try
			{
				if (stationaryStage[p.getX()][p.getY() + 1] != 0)
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
				if (stationaryStage[p.getX() + direction][p.getY()] != 0)
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
		checkClear();
	}

	private void checkClear()
	{
		int linesCleared = 0, lineOn = 19;
		byte[][] newStat = new byte[10][20];
		clearStage(newStat);
		for (int y = 19; y >= 0; y--)
		{
			int count = 0;
			for (int x = 0; x < 10; x++)
			{
				if (stationaryStage[x][y] != 0)
				{
					if (++count == 10)
					{
						linesCleared++;
					}
				} else
				{
					for (int i = 0; i < 10; i++)
					{
						newStat[i][lineOn] = stationaryStage[i][y];
					}
					lineOn--;
					break;
				}
			}
		}
		stationaryStage = newStat;
	}
}
