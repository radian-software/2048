package com.apprisingsoftware.game2048.console;

public interface Solver {

	public Button getBestDirection(Board board);
	public ButtonScore getWorstLocation(Board board);

}
