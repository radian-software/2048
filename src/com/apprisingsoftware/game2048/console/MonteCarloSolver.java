package com.apprisingsoftware.game2048.console;

import java.util.Random;

public class MonteCarloSolver implements Solver {

	private int gamesPerMove;

	private static final Random random = new Random();

	public MonteCarloSolver(int gamesPerMove) {
		this.gamesPerMove = gamesPerMove;
	}

	@Override public Button getBestDirection(Board board) {
		Button[] compass = Button.compass;
		int games = gamesPerMove / compass.length;
		double maxScore = Double.MIN_VALUE;
		int maxIndex = -1;
		for (int i=0; i<4; i++) {
			if (!board.canMove(compass[i])) continue;

			Board option = new Board(board);
			option.move(compass[i]);
			int sum = 0;
			for (int j=0; j<games; j++) {
				sum += getFinalScore(option);
			}
			double score = (double)sum / games;
			if (score > maxScore) {
				maxScore = score;
				maxIndex = i;
			}
		}
		if (maxIndex == -1)
			return Button.NONE;
		return compass[maxIndex];
	}

	@Override public ButtonScore getWorstLocation(Board board) {
		Pos[] spaces = board.getEmptyLocations();
		int games = gamesPerMove / (spaces.length*2);
		double minScore = Double.MAX_VALUE;
		int maxIndex = -1;
		boolean maxFour = false;
		for (int i=0; i<spaces.length; i++) {
			Board option = new Board(board);
			option.setTile((byte)1, spaces[i]);
			int sum = 0;
			for (int j=0; j<games; j++) {
				sum += getFinalScore(option);
			}
			double score = (double)sum / games;
			if (score < minScore) {
				minScore = score;
				maxIndex = i;
				maxFour = false;
			}
			option.setTile((byte)2, spaces[i]);
			sum = 0;
			for (int j=0; j<games; j++) {
				sum += getFinalScore(option);
			}
			score = (double)sum / games;
			if (score < minScore) {
				minScore = score;
				maxIndex = i;
				maxFour = true;
			}
		}
		if (maxIndex == -1)
			return null;
		return new ButtonScore(spaces[maxIndex], maxFour, (int)minScore);
	}

	public int getFinalScore(Board board) {
		Board game = new Board(board);
		Button[] compass = Button.compass;
		while (game.isValid()) {
			Button direction = compass[random.nextInt(4)];
			if (game.canMove(direction)) {
				game.move(direction);
				game.addRandomTile();
				game.testValidity();
			}
		}
		return game.getScore();
	}

}
