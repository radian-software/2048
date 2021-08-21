package com.apprisingsoftware.game2048.console;

import static com.apprisingsoftware.game2048.console.BoardStats.size;

public class AdvancedEmptySpaceHeuristic extends AbstractRowColumnHeuristic {
	
	@Override public int score(byte[] row) {
		int score = 0;
		int maxValue = -1;
		int maxIndex = -1;
		int secondValue = -1;
		int secondIndex = -1;
		for (int i=0; i<size; i++) {
			if (row[i] == 0)
				score += 10;
			if (row[i] > maxValue) {
				secondValue = maxValue;
				secondIndex = maxIndex;
				maxValue = row[i];
				maxIndex = i;
			}
			else if (row[i] > secondValue) {
				secondValue = row[i];
				secondIndex = i;
			}
		}
		// Having a high tile is good unless the second highest tile is on the other side
		if (maxIndex == 0 || maxIndex == size-1) {
			if (secondIndex == 0 || secondIndex == size-1) { // This has to be on the other side of the row/column, not the same tile.
				score -= row[secondIndex]*4;
			}
			else {
				score += row[maxIndex]*4;
			}
		}
		return score;
	}
}
