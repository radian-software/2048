package com.apprisingsoftware.game2048.graphical;

import java.util.Random;

public class Solver {
	
	private static Random random = new Random();
	
	// AI selection function
	
	public static Button advise(Board board) {
		return recursiveDecision(board, 4, 1.05).button;
	}
	
	public static ButtonScore recursiveDecision(Board board, int recursion, double weight) {
		if (recursion == 0) return new ButtonScore(Button.NONE, board.heuristic());
		Button[] directions = Button.compass;
		double max = Double.NEGATIVE_INFINITY; // Best score found so far that will lead quickly to a desirable state.
		Button best = Button.NONE; // Button corresponding to this score. This is only used at the highest recursion level.
		double currentScore = board.heuristic();
		for (Button direction : directions) {
			if (!board.isMoveable(new Pos(direction))) continue;
			Board copy = new Board(board);
			copy.moveTiles(direction, false);
			double recursiveScore = recursiveDecision(copy, recursion-1, weight).score;
			// Make the score depend on each step but make higher (earlier) recursion levels count for more.
			// Moving to a desirable state faster is better.
			double score = currentScore * Math.pow(weight, recursion) + recursiveScore;
			if (score > max) {
				max = score;
				best = direction;
			}
		}
		return new ButtonScore(best, max);
	}
	
	// Single-turn algorithm based on a weighted score of various factors.
	// Heuristic factors taken from http://stackoverflow.com/a/22362818/3538165.
	
	public static Button adviseEvaluation(Board board) {
		Button[] directions = Button.compass;
		boolean[] validDirections = {false, false, false, false};
		int numDirections = 0;
		for (int i=0; i<4; i++) {
			if (board.isMoveable(new Pos(directions[i]))) {
				validDirections[i] = true;
				numDirections += 1;
			}
		}
		
		double max = Double.MIN_VALUE;
		int best = -1;
		for (int i=0; i<4; i++) {
			if (validDirections[i]) {
				double score = (128 + board.getNumberOfEmptySpaces()*128 +
						board.getAdjacentTileScore() + board.getLogTileScore() +
						numDirections*256 + board.getAlignedTiles()*2);
				if (score > max) {
					max = score;
					best = i;
				}
			}
		}
		return directions[best];
	}
	
	// Recursive to try to order the tiles' values so as to increase towards one corner.
	// Does not take into account new tiles.
	// My idea.
	
	public static Button adviseOrdering(Board board, int recursion) {
		Button[] directions = Button.compass;
		int maxOrdering = Integer.MIN_VALUE;
		int best = -1;
		for (int i=0; i<4; i++) {
			if (board.isMoveable(new Pos(directions[i]))) {
				Board copy = new Board(board);
				copy.moveTiles(directions[i], false);
				int currentOrdering = getOrdering(copy, 0);
				int finalOrdering = getOrdering(copy, recursion-1);
				int ordering = currentOrdering*finalOrdering;
				if (ordering > maxOrdering) {
					maxOrdering = ordering;
					best = i;
				}
			}
		}
		return directions[best];
	}
	
	private static int getOrdering(Board board, int recursion) {
		if (recursion <= 0) return board.getOrderingScore();
		Button[] directions = Button.compass;
		int maxOrdering = Integer.MIN_VALUE;
		for (int i=0; i<4; i++) {
			if (board.isMoveable(new Pos(directions[i]))) {
				Board copy = new Board(board);
				copy.moveTiles(directions[i], false);
				int currentOrdering = getOrdering(copy, 0);
				int finalOrdering = getOrdering(copy, recursion-1);
				maxOrdering = Math.min(maxOrdering, currentOrdering*finalOrdering);
			}
		}
		return maxOrdering;
	}
	
	// Recursive to find moves that minimize tiles on board. Does not take into account new tiles.
	// My idea.
	
	public static Button adviseLeastTiles(Board board, int recursion) {
		Button[] directions = Button.compass;
		double leastTiles = Double.MAX_VALUE;
		int best = -1;
		for (int i=0; i<4; i++) {
			if (board.isMoveable(new Pos(directions[i]))) {
				Board copy = new Board(board);
				copy.moveTiles(directions[i], false);
				double currentTiles = getLeastTiles(copy, 0);
				double finalTiles = getLeastTiles(copy, recursion-1);
				double numTiles = currentTiles*finalTiles;
				if (numTiles < leastTiles) {
					leastTiles = numTiles;
					best = i;
				}
			}
		}
		return directions[best];
	}
	
	private static double getLeastTiles(Board board, int recursion) {
		if (recursion <= 0) return board.getScoreOnBoard();
		Button[] directions = Button.compass;
		double leastTiles = Double.MAX_VALUE;
		for (int i=0; i<4; i++) {
			if (board.isMoveable(new Pos(directions[i]))) {
				Board copy = new Board(board);
				copy.moveTiles(directions[i], false);
				double currentTiles = getLeastTiles(copy, 0);
				double finalTiles = getLeastTiles(copy, recursion-1);
				leastTiles = Math.min(leastTiles, currentTiles*finalTiles);
			}
		}
		return leastTiles;
	}
	
	// Recursive to find moves that maximize score. Does not take into account new tiles.
	// My idea.
	
	public static Button adviseMaxScore(Board board, int recursion) {
		Button[] directions = Button.compass;
		int maxJoins = -1;
		int best = -1;
		for (int i=0; i<4; i++) {
			if (board.isMoveable(new Pos(directions[i]))) {
				Board copy = new Board(board);
				copy.moveTiles(directions[i], false);
				copy.checkValidity();
				int numJoins;
				if (copy.isValid()) {
					numJoins = copy.getScore() + getMaxScore(copy, recursion-1);
					if (numJoins > maxJoins) {
						maxJoins = numJoins;
						best = i;
					}
				}
				else {
					numJoins = -9001;
				}
			}
		}
		if (best == -1) best = random.nextInt(4);
		return directions[best];
	}
	
	private static int getMaxScore(Board board, int recursion) {
		if (recursion <= 0) return 0;
		Button[] directions = Button.compass;
		int maxScore = 0;
		for (int i=0; i<4; i++) {
			if (board.isMoveable(new Pos(directions[i]))) {
				Board copy = new Board(board);
				copy.moveTiles(directions[i], false);
				maxScore = Math.max(maxScore, copy.getScore() + getMaxScore(copy, recursion-1));
			}
		}
		return maxScore;
	}
	
	// What does this look like?
	
	public static Button adviseRandom() {
		Button[] buttons = {Button.LEFT, Button.RIGHT, Button.UP, Button.DOWN};
		return buttons[new Random().nextInt(4)];
	}
	
}
