package com.apprisingsoftware.game2048.console;

import static com.apprisingsoftware.game2048.console.BoardStats.size;

public class EmptySpaceHeuristic extends AbstractRowColumnHeuristic {
	
	@Override public int score(byte[] row) {
		int score = 0;
		int maxValue = -1;
		int maxIndex = -1;
		for (int i=0; i<size; i++) {
			if (row[i] == 0)
				score += 5;
			if (row[i] > maxValue) {
				maxValue = row[i];
				maxIndex = i;
			}
		}
		if (maxIndex == 0 || maxIndex == size-1) {
			score += 10;
		}
		return score;
	}	
}
