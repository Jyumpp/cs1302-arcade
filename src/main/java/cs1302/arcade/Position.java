package cs1302.arcade;

public class Position
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