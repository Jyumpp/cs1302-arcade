package cs1302.arcade;

/**
 * This class provides the framework for different types of Tetrominos.
 *
 * @author Hunter Halloran
 * @author Calvin Childress
 */
public class Tetromino
{
	/**
	 * The different types of Tetrominos for the game.
	 */
	enum Tetrominos
	{
		I, J, L, O, S, T, Z
	}

	private Tetrominos pieceType;
	private int rotation = 0;
	int xPos = 0;
	int yPos = 0;
	byte[][] tetromino;

	/**
	 * Sets the piece type to a given Tetromino.
	 *
	 * @param t the type to set to
	 */
	void setPieceType(Tetrominos t)
	{
		pieceType = t;
	}

	/**
	 * Returns the Tetromino type.
	 *
	 * @return Tetromino type.
	 */
	Tetrominos getPieceType()
	{
		return pieceType;
	}

	/**
	 * Increments the rotation value by one.
	 */
	void incRotation()
	{
		rotation = (rotation + 1) % 4;
	}

	/**
	 * Decrements the rotation value by one.
	 */
	void decRotation()
	{
		rotation = (rotation + 3) % 4;
	}

	/**
	 * Returns the current rotation value.
	 *
	 * @return current rotation
	 */
	int getRotation()
	{
		return rotation;
	}

	/**
	 * Returns a 2D byte array of the Tetromino rotated in a given direction.
	 *
	 * @param direction the direction of rotation
	 * @return rotated Tetromino byte array
	 */
	byte[][] rotateTetromino(int direction)
	{
		byte[][] returnByte = new byte[tetromino[0].length][];
		for (int b = 0; b < returnByte.length; b++)
		{
			//creates byte array of opposite dimensions
			returnByte[b] = new byte[tetromino.length];
		}
		if (direction == Tetris.RIGHT) //rotates the Tetromino clockwise
		{
			for (int y = 0; y < tetromino.length; y++)
			{
				for (int x = 0; x < tetromino[0].length; x++)
				{
					returnByte[x][tetromino.length - y - 1] = tetromino[y][x];
				}
			}
		} else if (direction == Tetris.LEFT) //rotates the Tetromino counterclockwise
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

	//Offsets to rotate Tetrominos around their center of rotation
	final int[] IXOffsets = {2, -2, 1, -1};
	final int[] IYOffsets = {-1, 2, -2, 1};
	final int[] JLSTZXOffsets = {1, -1, 0, 0};
	final int[] JLSTZYOffsets = {0, 1, -1, 0};

	/**
	 * Returns the position offset for a given piece and direction.
	 *
	 * @param direction the direction of rotation
	 * @return the relative position of the offset piece
	 */
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

	/**
	 * Returns the position offsets for a given piece and direction for specified offsets.
	 *
	 * @param direction the direction of rotation
	 * @param x         the specified x offset list
	 * @param y         the specified y offset list
	 * @return the relative position of the offset piece
	 */
	Position getOffset(int direction, int[] x, int[] y)
	{
		if (direction == Tetris.RIGHT)
		{
			return new Position(x[rotation], y[rotation]); //returns the normal offset
		} else
		{
			int rot = (rotation + 15) % 4; //ensures proper wrap, always within [0,3]
			return new Position(-x[rot], -y[rot]); //uses the negative of the offset
		}
	}

	/**
	 * Returns the absolute location of the rotated piece.
	 *
	 * @param direction the direction of rotation
	 * @return the absolute location of the offset piece
	 */
	Position getRotLocation(int direction)
	{
		Position retPos = getOffset(direction);
		retPos.setX(retPos.getX() + xPos);
		retPos.setY(retPos.getY() + yPos);
		return retPos;
	}

	/**
	 * Returns the base Tetromino shape.
	 *
	 * @return base Tetromino 2D byte array
	 */
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
