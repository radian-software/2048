package com.apprisingsoftware.game2048.graphical;

public class Pos {

	static final Pos[] compass = {new Pos(-1, 0), new Pos(1, 0),
		new Pos(0, -1), new Pos(0, 1)};

	public int x;
	public int y;

	public Pos(int x, int y) {
		super();

		this.x = x;
		this.y = y;
	}

	public Pos(Button button) {
		super();
		switch (button) {
		case LEFT:
			x = -1;
			y = 0;
			break;
		case RIGHT:
			x = 1;
			y = 0;
			break;
		case UP:
			x = 0;
			y = -1;
			break;
		case DOWN:
			x = 0;
			y = 1;
			break;
		default:
			x = 0;
			y = 0;
		}
	}

}
