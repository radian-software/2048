package com.apprisingsoftware.game2048.console;

public class GameManager {

	private Board board;
	private Solver solver;
	private boolean evil;

	public GameManager(Solver solver) {
		board = new Board();
		this.solver = solver;
		evil = false;
	}

	public void moveBoard(Button direction) {
		if (direction == Button.LEFT || direction == Button.RIGHT ||
				direction == Button.UP || direction == Button.DOWN) {
			if (board.canMove(direction)) {
				board.move(direction);
				if (evil) {
					ButtonScore advice = solver.getWorstLocation(board);
					board.setTile((byte)(advice.four ? 2 : 1), advice.pos);
				}
				else {
					board.addRandomTile();
				}
				board.testValidity();
			}
		}
		else if (direction == Button.NONE) {
			System.out.println("NONE!");
			board.testValidity();
			System.out.println("Board is " + (board.isValid() ? "valid." : "invalid."));
		}
	}

	public void setEvil(boolean evil) {
		this.evil = evil;
	}

	@Override public String toString() { return board.toString(); }
	public Board getBoard() { return board; }
	public boolean getEvil() { return evil; }

}