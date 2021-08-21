package com.apprisingsoftware.game2048.console;

public class ScoreHeuristic extends AbstractHeuristic {

	@Override public int score(Board board) {
		return board.getScore();
	}

}
