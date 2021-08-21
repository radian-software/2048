package com.apprisingsoftware.game2048.console;

public class HeuristicSolver implements Solver {

	private AbstractHeuristic heuristic;
	private int recursionDepth;

	// Constructors
	public HeuristicSolver(AbstractHeuristic heuristic, int recursionDepth) {
		this.heuristic = heuristic;
		this.recursionDepth = recursionDepth;
	}

	// Interface methods
	@Override public Button getBestDirection(Board board) {
		ButtonScore advice = maximize(board, recursionDepth, true);
		return advice.button;
	}
	@Override public ButtonScore getWorstLocation(Board board) {
		ButtonScore advice = minimize(board, recursionDepth-1);
		return advice;
	}

	// ButtonScore returned contains Button and Score.
	private ButtonScore maximize(Board board, int recursion, boolean random) {
		Button[] compass = Button.compass;
		int maxScore = Integer.MIN_VALUE;
		int maxIndex = -1;
		for (int i=0; i<4; i++) {
			if (!board.canMove(compass[i])) continue;

			Board option = new Board(board);
			option.move(compass[i]);
			int score;
			if (random) score = expect(option, recursion-1).score;
			else score = minimize(option, recursion-1).score;
			if (score > maxScore) {
				maxScore = score;
				maxIndex = i;
			}
		}
		if (maxIndex == -1)
			return new ButtonScore(Button.NONE, maxScore);
		return new ButtonScore(compass[maxIndex], maxScore);
	}

	// ButtonScore returned contains Score.
	private ButtonScore expect(Board board, int recursion) {
		if (recursion == 0) return new ButtonScore(heuristic.score(board));
		int score = 0;
		Pos[] empty = board.getEmptyLocations();
		for (Pos loc : empty) {
			Board option = new Board(board);
			// With bit shifting operations, cloning the board is probably faster than changing the tile back.
			option.setTile((byte)1, loc); // Set 2 tile
			score += 9 * maximize(option, recursion, true).score;
			option.setTile((byte)2, loc); // Set 4 tile
			score += maximize(option, recursion, true).score;
		}
		if (empty.length == 0) return new ButtonScore(0);
		return new ButtonScore(score / (empty.length * 10));
	}

	// ButtonScore returned contains Location, Four, and Score.
	private ButtonScore minimize(Board board, int recursion) {
		if (recursion == 0) return new ButtonScore(heuristic.score(board));
		Pos[] empty = board.getEmptyLocations();
		int minScore = Integer.MAX_VALUE;
		Pos minPos = null;
		boolean minFour = false;
		for (Pos loc : empty) {
			Board option = new Board(board);
			// With bit shifting operations, cloning the board is probably faster than changing the tile back.
			option.setTile((byte)1, loc); // Set 2 tile
			int score = maximize(option, recursion, false).score;
			if (score < minScore) {
				minScore = score;
				minPos = loc;
				minFour = false;
			}
			option.setTile((byte)2, loc); // Set 4 tile
			score = maximize(option, recursion, false).score;
			if (score < minScore) {
				minScore = score;
				minPos = loc;
				minFour = true;
			}
		}
		if (empty.length == 0) return new ButtonScore(0);
		return new ButtonScore(minPos, minFour, minScore);
	}
}
