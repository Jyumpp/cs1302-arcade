package cs1302.arcade;

/**
 * This class provides a way to store a 2D location as a single object.
 *
 * @author Hunter Halloran
 * @author Calvin Childress
 */
public class Position
{
	private int xPos, yPos;

	/**
	 * Creates a new Position at a given X and Y.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 */
	Position(int x, int y)
	{
		xPos = x;
		yPos = y;
	}

	/**
	 * Sets the X coordinate to a given value.
	 *
	 * @param x value to set X to
	 */
	void setX(int x)
	{
		xPos = x;
	}

	/**
	 * Sets the Y coordinate to a given value.
	 *
	 * @param y value to set Y to
	 */
	void setY(int y)
	{
		yPos = y;
	}

	/**
	 * Returns the X coordinate of the Position
	 *
	 * @return the X coordinate
	 */
	int getX()
	{
		return xPos;
	}

	/**
	 * Returns the Y coordinate of the Position
	 *
	 * @return the Y coordinate
	 */
	int getY()
	{
		return yPos;
	}
}