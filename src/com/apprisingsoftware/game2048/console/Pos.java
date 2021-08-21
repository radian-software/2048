package com.apprisingsoftware.game2048.console;

public class Pos {
	
	static final Pos[] compass = {new Pos(-1, 0), new Pos(1, 0),
		new Pos(0, -1), new Pos(0, 1)};
	
	public int r;
	public int c;
	
	public Pos(int r, int c) {
		super();
		
		this.r = r;
		this.c = c;
	}
	
	public Pos(Button button) {
		super();
		switch (button) {
		case LEFT:
			c = -1;
			r = 0;
			break;
		case RIGHT:
			c = 1;
			r = 0;
			break;
		case UP:
			c = 0;
			r = -1;
			break;
		case DOWN:
			c = 0;
			r = 1;
			break;
		default:
			c = 0;
			r = 0;
		}
	}
	
}
