package com.apprisingsoftware.game2048.console;

public enum Button {
	NONE	("NONE"),
	LEFT	("LEFT"),
	RIGHT	("RIGHT"),
	UP		("UP"),
	DOWN	("DOWN"),
	R		("R"),
	H		("H"),
	A		("A"),
	P		("P"),
	T		("T");

	static final Button[] compass = {LEFT, RIGHT, UP, DOWN};

	public final String name;

	Button(String name) {
		this.name = name;
	}
}