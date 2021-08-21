package com.apprisingsoftware.game2048.console;

public class ButtonScore {

	public Button button;
	public int score;
	public Pos pos;
	public boolean four;

	// For expect
	public ButtonScore(int score) {
		this.score = score;
	}
	// For maximize
	public ButtonScore(Button button, int score) {
		this.button = button;
		this.score = score;
	}
	// For minimize
	public ButtonScore(Pos pos, boolean four, int score) {
		this.pos = pos;
		this.four = four;
		this.score = score;
	}

}
