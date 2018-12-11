package cs1302.arcade;

public class Tetromino
{
	enum Tetrominos {
		I,J,L,O,S,T,Z
	}

	private Tetrominos pieceType;
	private int rotation = 0;
	int xPos = 0;
	int yPos = 0;
	byte[][] tetromino;

	void setPieceType(Tetrominos t) {
		pieceType = t;
	}

	Tetrominos getPieceType() {
		return pieceType;
	}

	void intRotation() {
		rotation = (rotation + 1) % 4;
	}

	void decRotation() {
		rotation = (rotation + 3) % 4;
	}

	int getRotation() {
		return rotation;
	}

	byte[][] rotateTetromino(int direction)
	{
		byte[][] returnByte = new byte[tetromino[0].length][];
		for (int b = 0; b < returnByte.length; b++)
		{
			returnByte[b] = new byte[tetromino.length];
		}
		if (direction == Tetris.RIGHT)
		{
			for (int y = 0; y < tetromino.length; y++)
			{
				for (int x = 0; x < tetromino[0].length; x++)
				{
					returnByte[x][tetromino.length - y - 1] = tetromino[y][x];
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

	final int[] IXOffsets = {2, -2, 1, -1};
	final int[] IYOffsets = {-1, 2, -2, 1};

	final int[] JLSTZXOffsets = {1, -1, 0, 0};
	final int[] JLSTZYOffsets = {0, 1, -1, 0};

	Position getOffset(int direction)
	{
		switch (this.pieceType)
		{
			case I:
				return getOffset(direction, IXOffsets, IYOffsets);
			case O:
				return new Position(0, 0);
			default:
				return getOffset(direction, JLSTZXOffsets, JLSTZYOffsets);
		}
	}

	Position getOffset(int direction, int[] x, int[] y)
	{
		if (direction == Tetris.RIGHT)
		{
			return new Position(x[rotation], y[rotation]);
		} else
		{
			int rot = (rotation + 15) % 4;
			return new Position(-x[rot], -y[rot]);
		}
	}

	Position getRotLocation(int direction)
	{
		Position retPos = getOffset(direction);
		retPos.setX(retPos.getX() + xPos);
		retPos.setY(retPos.getY() + yPos);
		return retPos;
	}

	byte[][] getBaseTetromino()
	{
		switch (this.pieceType)
		{
			case I:
				return new byte[][]{
						{1, 1, 1, 1}};
			case J:
				return new byte[][]{
						{2, 0, 0},
						{2, 2, 2}};
			case L:
				return new byte[][]{
						{0, 0, 3},
						{3, 3, 3}};
			case O:
				return new byte[][]{
						{4, 4},
						{4, 4}};
			case S:
				return new byte[][]{
						{0, 5, 5},
						{5, 5, 0}};
			case T:
				return new byte[][]{
						{0, 6, 0},
						{6, 6, 6}};
			case Z:
				return new byte[][]{
						{7, 7, 0},
						{0, 7, 7}};
			default:
				return null;

		}
	}
}
