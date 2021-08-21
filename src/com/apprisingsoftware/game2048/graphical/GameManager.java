package com.apprisingsoftware.game2048.graphical;

import java.awt.Frame;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class GameManager {

	private Board board;
	private Frame app;
	private boolean autoplaying;
	private boolean profiling;

	public GameManager(int size, Frame app) {
		super();
		this.app = app;
		board = new Board(size);
		autoplaying = false;
	}

	public void playGame(Button input) {
		if (profiling) return;
		autoplaying = false;
		switch (input) {
		case H: {
			Button hint = Solver.advise(board);
			inputButton(hint);
			break;
		}
		case P:
			Solver.advise(board);
			break;
		case A: {
			int response = JOptionPane.showConfirmDialog(app,
					"Would you like to enable autoplay?",
					"Confirm Autoplay",
					JOptionPane.YES_NO_OPTION);
			if (response == 0) {
				autoplaying = true;
			}
			break;
		}
		case R: {
			int response = JOptionPane.showConfirmDialog(app,
					"Would you like to reset the game?",
					"Confirm Reset",
					JOptionPane.YES_NO_OPTION);
			if (response == 0) {
				board = new Board(board.getSize());
			}
			break;
		}
		case T: {
			int response = Integer.parseInt((String)JOptionPane.showInputDialog(app,
					"Run how many tests?",
					"Run Profiling Tests",
					JOptionPane.PLAIN_MESSAGE,
					null, null, 10));
			if (response >= 1) {
				profiling = true;
				Board oldBoard = board;
				int[] scores = new int[response];
				for (int i=0; i<response; i++) {
					board = new Board(oldBoard.getSize());
					while (board.isValid()) {
						Button hint = Solver.advise(board);
						inputButton(hint);
					}
					scores[i] = board.getScore();
				}
				int sum = 0;
				for (int score : scores) sum += score;
				JOptionPane.showMessageDialog(app,
						"The average score was: " + sum / response);
				board = oldBoard;
				profiling = false;
				app.repaint();
			}
		}
		default:
			if (board.isValid()) {
				inputButton(input);
			}
		}
	}

	private void inputButton(Button input) {
		boolean success;
		if (Arrays.asList(Button.compass).contains(input)) {
			success = board.moveTiles(input, true);
		}
		else {
			success = false;
		}
		if (success) {
			for (int i=0; i<1; i++) {
				board.addRandomTile();
				board.checkValidity();
			}
		}
	}

	public void doAutoplayIfEnabled() {
		if (autoplaying) {
			Button hint = Solver.advise(board);
			inputButton(hint);
			app.repaint();
		}
	}

	public Board getBoard() {
		return board;
	}

}
