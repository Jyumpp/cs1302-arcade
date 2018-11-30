package cs1302.arcade;

public class Tetris
{

	private byte[][] stationaryStage = new byte[10][20];
	private byte[][] fallingStage = new byte[10][20];
	private Tetromino currentFalling;

	private enum Tetromino
	{
		I, J, L, O, S, T, Z;

		int rotation = 0;
		int xPos = 0;
		int yPos = 0;
		byte[][] tetromino;

		byte[][] getTetrominos()
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
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 20; y++)
			{
				stationaryStage[x][y] = 0;
				fallingStage[x][y] = 0;
			}
		}
	}

	private boolean tryFall()
	{


		return false;
	}

	private void fallToStat()
	{

	}
}
