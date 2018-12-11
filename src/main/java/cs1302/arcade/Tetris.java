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

/**
 * This class produces the backend behavior of the Tetris game
 *
 * @author Hunter Halloran
 * @author Calvin Childress
 */
public class Tetris
{
	Stage stage;
	private byte[][] stationaryStage = new byte[10][21];
	private byte[][] fallingStage = new byte[10][21];
	private Tetromino currentFalling, nextFall;
	private long freeTime = 1000;
	private int score = 0, level = 0, linesCleared = 0;
	private boolean dropSpeedUp = false, gameOver = false;
	static final int LEFT = -1, RIGHT = 1;

	Event e = new ActionEvent();

	private LongProperty dropTimer = new SimpleLongProperty(0);

	//X components of the spin tests for all non-I Tetrominos
	private int[][] clockwiseXSpinTests = {
			{0, -1, -1, 0, -1},
			{0, 1, 1, 0, 1},
			{0, 1, 1, 0, 1},
			{0, -1, -1, 0, -1}
	};
	//Y components of the spin tests for all non-I Tetrominos
	private int[][] clockwiseYSpinTests = {
			{0, 0, 1, -2, -2},
			{0, 0, -1, 2, 2},
			{0, 0, 1, -2, -2},
			{0, 0, -1, 2, 2}
	};
	//X components of the spin tests for I Tetrominos
	private int[][] clockwiseIXSpinTests = {
			{0, -2, 1, -2, 1},
			{0, -1, 2, -1, 2},
			{0, 2, -1, 2, -1},
			{0, 1, -2, 1, -2}
	};
	//Y components of the spin tests for I Tetrominos
	private int[][] clockwiseIYSpinTests = {
			{0, 0, 0, -1, 2},
			{0, 0, 0, 2, -1},
			{0, 0, 0, 1, -2},
			{0, 0, 0, -2, 1}
	};

	//Same as above, but in the other rotation direction
	private int[][] counterClockwiseXSpinTests = new int[4][5];
	private int[][] counterClockwiseYSpinTests = new int[4][5];
	private int[][] counterClockwiseIXSpinTests = new int[4][5];
	private int[][] counterClockwiseIYSpinTests = new int[4][5];

	/**
	 * This constructor initializes a new Tetris game.
	 *
	 * @param stage the stage to receive KeyEvents from or
	 *              to send update events to
	 */
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
				dropSpeedUp = true; //ignore timings if player requests faster speed
				freeTime = 50;
			}
		};

		EventHandler<KeyEvent> speedDown = event ->
		{
			if (event.getCode() == KeyCode.DOWN)
			{
				dropSpeedUp = false; //resume normal timings
				freeTime = getDifficultyTime();
			}
		};

		stage.addEventFilter(KeyEvent.KEY_PRESSED, keyboardEvent);
		stage.addEventFilter(KeyEvent.KEY_PRESSED, speedUp);
		stage.addEventFilter(KeyEvent.KEY_RELEASED, speedDown);
	}

	/**
	 * Returns the stationaryStage byte array.
	 *
	 * @return 2D byte array
	 */
	public byte[][] getStationaryStage()
	{
		return stationaryStage;
	}

	/**
	 * Returns the fallingStage byte array.
	 *
	 * @return 2D byte array
	 */
	public byte[][] getFallingStage()
	{
		return fallingStage;
	}

	/**
	 * Returns the number of lines cleared.
	 *
	 * @return number of lines cleared
	 */
	public int getLinesCleared()
	{
		return linesCleared;
	}

	/**
	 * Returns the current level number.
	 *
	 * @return the current level
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * Returns the current score.
	 *
	 * @return current score
	 */
	public int getScore()
	{
		return score;
	}

	/**
	 * Returns true if game is finished.
	 *
	 * @return game state
	 */
	public boolean isGameOver()
	{
		return gameOver;
	}

	/**
	 * Returns the next Tetromino to fall.
	 *
	 * @return Tetromino object
	 */
	public Tetromino getNextFall()
	{
		return nextFall;
	}

	/**
	 * Initializes variable values.
	 */
	private void init()
	{
		//Spin Tests repeat over clockwise and counterclockwise
		counterClockwiseXSpinTests[0] = clockwiseXSpinTests[1];
		counterClockwiseXSpinTests[1] = clockwiseXSpinTests[0];
		counterClockwiseXSpinTests[2] = clockwiseXSpinTests[3];
		counterClockwiseXSpinTests[3] = clockwiseXSpinTests[2];
		counterClockwiseYSpinTests[0] = clockwiseYSpinTests[1];
		counterClockwiseYSpinTests[1] = clockwiseYSpinTests[0];
		counterClockwiseYSpinTests[2] = clockwiseYSpinTests[3];
		counterClockwiseYSpinTests[3] = clockwiseYSpinTests[2];

		//I Spin Tests for counterclockwise motion is the negative of clockwise
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

	/**
	 * Prepares the next Tetromino to fall while setting the new current falling.
	 */
	private void setupNextFall()
	{
		currentFalling = nextFall; //replace falling piece
		nextFall = randomTetromino(); //randomize the next piece
		nextFall.tetromino = nextFall.getBaseTetromino();
		currentFalling.yPos = 0; //set at center top of screen
		currentFalling.xPos = 4;
		clearStage(fallingStage); //clear the falling stage
		placeFalling(); //place the falling Tetromino on the falling stage
		tryFall(); //attempt to make the piece fall
	}

	/**
	 * Returns a random Tetromino object
	 *
	 * @return random Tetromino object
	 */
	private Tetromino randomTetromino()
	{
		Tetromino retTet = new Tetromino();
		Random r = new Random();

		//get a random Tetromino of any shape
		retTet.setPieceType(Tetromino.Tetrominos.values()[r.nextInt(7)]);

		return retTet;
	}

	/**
	 * Clears all values of a byte array to 0.
	 *
	 * @param stage 2D byte array to clear
	 */
	private void clearStage(byte[][] stage)
	{
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 21; y++)
			{
				stage[x][y] = 0;
			}
		}
	}

	/**
	 * Sends an event to the stage to trigger the GUI to update.
	 */
	private void displayStage()
	{
		stage.fireEvent(e);
	}

	/**
	 * Attempts to cause the current falling piece to fall one unit.
	 */
	private void tryFall()
	{
		displayStage();

		if (canFall())
		{
			//cause the piece to fall if it can
			clearStage(fallingStage);
			currentFalling.yPos++;
			placeFalling();
			dropTimer.set(freeTime);
			new Thread(dropOnTimeZero).start();
		} else
		{
			//try to set the piece if it can't fall
			dropTimer.set(getDifficultyTime());
			new Thread(setOnTimeZero).start();
		}

		displayStage();
	}

	/**
	 * Attempts to move the current falling piece one unit in a given direction.
	 *
	 * @param direction the direction to move (left or right)
	 */
	private void tryMove(int direction)
	{
		if (canMove(direction))
		{
			//move the piece if it can move
			clearStage(fallingStage);
			currentFalling.xPos += direction;
			placeFalling();
			dropTimer.set(getDifficultyTime());
		}
		displayStage();
	}

	/**
	 * Attempts to rotate the current falling piece in a given direction.
	 *
	 * @param direction the direction to rotate
	 */
	private void tryRotate(int direction)
	{
		byte[][] tryTurn = currentFalling.rotateTetromino(direction);
		Position rotPos = currentFalling.getRotLocation(direction);

		//it turns out the official Tetris SRS rotation system is complicated :P
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

	/**
	 * Evaluates all spin tests to properly implement the Tetris "SRS" method of rotation.
	 *
	 * @param tryTurn   a byte array of the turned Tetromino
	 * @param rotPos    the position which the top-left of the Tetromino should be
	 * @param direction the direction to rotate
	 * @param xPos      the proper X spin test
	 * @param yPos      the proper Y spin test
	 */
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
						if (++count == 4) //if it gets to 4 it passed all tests
						{
							set(tryTurn, at, direction);
							return;
						}
					} else break;
				} catch (Exception e)
				{
					break; //if the array is out of bounds it probably shouldn't turn there
				}
			}
		}
	}

	/**
	 * Sets the values of a rotated Tetromino.
	 *
	 * @param tryTurn   the byte array of a turned Tetromino
	 * @param p         the position to put the Tetromino at
	 * @param direction the direction the Tetromino was rotated
	 */
	private void set(byte[][] tryTurn, Position p, int direction)
	{
		if (direction == RIGHT)
		{
			currentFalling.incRotation();
		} else if (direction == LEFT)
		{
			currentFalling.decRotation();
		}
		currentFalling.tetromino = tryTurn;
		currentFalling.xPos = p.getX();
		currentFalling.yPos = p.getY();
	}

	/**
	 * Places a the current falling tetromino onto the falling stage.
	 */
	private void placeFalling()
	{
		for (int y = 0; y < currentFalling.tetromino.length; y++)
		{
			for (int x = 0; x < currentFalling.tetromino[0].length; x++)
			{
				if (stationaryStage[currentFalling.xPos + x][currentFalling.yPos + y] != 0 &&
						currentFalling.tetromino[y][x] != 0)
				{
					//if initial space is occupied, game over!
					gameOver = true;
					displayStage();
					return;
				}
				//place 2D array at a given offset to the full stage
				fallingStage[currentFalling.xPos + x][currentFalling.yPos + y]
						= currentFalling.tetromino[y][x];
			}
		}
	}

	/**
	 * New Thread to drop the Tetromino one unit per amount of time.
	 */
	private Runnable dropOnTimeZero = new Thread(() ->
	{
		long timer = dropTimer.get();
		while (timer > 0)
		{
			if (dropSpeedUp) //ignore timings if player wants to go faster
			{
				timer = freeTime;
				dropSpeedUp = false;
			}
			try
			{
				Thread.sleep(1);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			dropTimer.set(--timer);
		}
		//fall again after the time has run out
		Platform.runLater(this::tryFall);
	});

	/**
	 * New Thread to set the Tetromino in place in an amount of time.
	 */
	private Runnable setOnTimeZero = new Thread(() ->
	{
		dropTimer.set(getDifficultyTime());
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
		if (!canFall()) //sometimes if a piece is moved it could fall after it couldn't
		{
			freeTime = getDifficultyTime();
			fallToStat();
			Platform.runLater(() ->
			{
				displayStage();
				setupNextFall();
			});
		} else
		{
			new Thread(dropOnTimeZero).start(); //try to fall again if it can
		}
	});

	/**
	 * Checks whether the current falling Tetromino can fall more.
	 *
	 * @return true if can still fall
	 */
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

	/**
	 * Checks whether the current falling Tetromino can move in a direction.
	 *
	 * @param direction the direction of movement
	 * @return true if can move
	 */
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

	/**
	 * Returns the positions to check movement with.
	 *
	 * @return array of positions
	 */
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

	/**
	 * Returns positions to check movement with for a given byte array and initial position.
	 *
	 * @param tetromino the 2D byte array of the Tetromino
	 * @param p         the position of the top-right of the Tetromino byte array
	 * @return array of positions
	 */
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

	/**
	 * Sets the current falling stage into the stationary stage.
	 */
	private void fallToStat()
	{
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 21; y++)
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

	/**
	 * Checks to see if any lines should be cleared and calls to update scores.
	 */
	private void checkClear()
	{
		int linesCleared = 0, lineOn = 20;
		byte[][] newStat = new byte[10][21];
		clearStage(newStat);
		for (int y = 20; y >= 0; y--)
		{
			int count = 0;
			for (int x = 0; x < 10; x++)
			{
				if (stationaryStage[x][y] != 0)
				{
					if (++count == 10) //full line let be deleted
					{
						linesCleared++;
					}
				} else
				{
					for (int i = 0; i < 10; i++) //save lines that aren't complete
					{
						newStat[i][lineOn] = stationaryStage[i][y];
					}
					lineOn--;
					break;
				}
			}
		}
		stationaryStage = newStat;
		updateScore(linesCleared);
	}

	/**
	 * Updates the score based on number of lines cleared.
	 *
	 * @param linesCleared number of lines cleared.
	 */
	private void updateScore(int linesCleared)
	{
		this.linesCleared += linesCleared;
		int n = level + 1;
		switch (linesCleared) //more lines cleared at once, more points!
		{
			case 4:
				score += n * 1200;
			case 3:
				score += n * 300;
			case 2:
				score += n * 100;
			case 1:
				score += n * 40;
		}
		level = this.linesCleared / 10;
	}

	/**
	 * Returns the amount of time per drop at a given level.
	 *
	 * @return amount of milliseconds per drop
	 */
	private long getDifficultyTime()
	{
		//reduce drop time by 3/4 every level
		long t = 1000;
		for (int i = 0; i < level; i++)
		{
			t *= 0.75;
		}
		return t;
	}
}
