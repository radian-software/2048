package com.apprisingsoftware.game2048.console;

public class MonotonicitySmoothnessHeuristic extends AbstractHeuristic {

	@Override public int score(Board board) {
		int locs = board.getEmptyLocations().length;
		if (locs == 0) board.testValidity();
		if (!board.isValid()) return Integer.MIN_VALUE;
		double clustering = 0;
		for (int r=0; r<4; r++) {
			for (int c=0; c<4; c++) {
				byte tile = board.getTile(r, c);
				if (tile == 0) continue;
				int sum = 0;
				int cells = 0;
				for (Pos offset : Pos.compass) {
					int r2 = r+offset.r;
					int c2 = c+offset.c;
					try {
						sum += Math.abs(board.getTile(r2, c2) - tile);
						cells++;
					}
					catch (IndexOutOfBoundsException e) {
						//
					}
				}
				clustering += ((double)sum) / cells;
			}
		}
		int score = (int)(board.getScore() + Math.log(board.getScore()*locs - clustering));
		return Math.max(score, Math.min(board.getScore(), 1));
	}

}
