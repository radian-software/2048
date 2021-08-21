package com.apprisingsoftware.game2048.console;

import static com.apprisingsoftware.game2048.console.BoardStats.length;
import static com.apprisingsoftware.game2048.console.BoardStats.rowToIndex;
import static com.apprisingsoftware.game2048.console.BoardStats.rowToOrder;
import static com.apprisingsoftware.game2048.console.BoardStats.size;

public abstract class AbstractRowColumnHeuristic extends AbstractHeuristic {

	static int[] heuristic;
	{ generateHeuristicTable(); }

	@Override public int score(Board board) {
		int score = 0;
		for (int r=0; r<size; r++) {
			score += score(board.getRow(r));
		}
		for (int c=0; c<size; c++) {
			score += score(board.getCol(c));
		}
		return score;
	}
	public abstract int score(byte[] row);

	public synchronized void generateHeuristicTable() {
		if (heuristic == null) {
			heuristic = new int[length];
			for (int i=0; i<length; i++) {
				short r = (short)i;
				heuristic[rowToIndex(r)] = score(rowToOrder(r));
			}
		}
	}
	public static int score(short row) {
		return heuristic[rowToIndex(row)];
	}

}
