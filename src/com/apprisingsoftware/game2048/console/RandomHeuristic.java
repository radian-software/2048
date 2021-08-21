package com.apprisingsoftware.game2048.console;

import java.util.Random;

public class RandomHeuristic extends AbstractHeuristic {
	
	private static final Random random = new Random();
	
	@Override public int score(Board board) {
		return random.nextInt();
	}
	
}
