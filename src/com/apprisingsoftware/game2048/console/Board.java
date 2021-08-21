package com.apprisingsoftware.game2048.console;

import static com.apprisingsoftware.game2048.console.BoardStats.ROW_MASK;
import static com.apprisingsoftware.game2048.console.BoardStats.ROW_SIZE;
import static com.apprisingsoftware.game2048.console.BoardStats.SHORT_MASK;
import static com.apprisingsoftware.game2048.console.BoardStats.TILE_MASK;
import static com.apprisingsoftware.game2048.console.BoardStats.TILE_SIZE;
import static com.apprisingsoftware.game2048.console.BoardStats.length;
import static com.apprisingsoftware.game2048.console.BoardStats.orderToRow;
import static com.apprisingsoftware.game2048.console.BoardStats.rowToIndex;
import static com.apprisingsoftware.game2048.console.BoardStats.rowToOrder;
import static com.apprisingsoftware.game2048.console.BoardStats.size;
import java.util.Arrays;
import java.util.Random;

public class Board {

	// Lookup tables
	private static short[] leftTable;
	private static short[] rightTable;
	private static boolean[] leftMoveable;
	private static boolean[] rightMoveable;
	private static boolean[] moveable;
	private static int[] leftScore;
	private static int[] rightScore;
	static { generateTables(); }

	// Instance variables
	private static final Random random = new Random();
	private long tiles;
	private boolean valid;
	private int score;

	// Constructors
	public Board() {
		tiles = 0;
		// Board is automatically initialized as all zeroes.
		for (int i=0; i<2; i++) {
			addRandomTile();
		}
		valid = true;
		score = 0;
	}
	public Board(Board board) {
		tiles = board.tiles;
		// The long is not a pointer, so we do not need to clone it.
		valid = board.isValid();
		score = board.getScore();
	}

	// Public game methods (moving tiles)
	public boolean canMove(Button direction) {
		switch (direction) {
		case LEFT:
			return canMoveLeft();
		case RIGHT:
			return canMoveRight();
		case UP:
			return canMoveUp();
		case DOWN:
			return canMoveDown();
		default:
			throw new IllegalArgumentException();
		}
	}
	public boolean canMove() {
		return canMoveHorizontally() || canMoveVertically();
	}
	public void move(Button direction) {
		switch (direction) {
		case LEFT:
			moveLeft();
			break;
		case RIGHT:
			moveRight();
			break;
		case UP:
			moveUp();
			break;
		case DOWN:
			moveDown();
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
	// Public game methods (adding tiles)
	public void addRandomTile() {
		byte tile = (byte)(random.nextDouble() < 0.9 ? 1 : 2);
		Pos location = getRandomEmptyLocation();
		if (location != null) {
			setTile(tile, location);
		}
		else
			valid = false;
	}

	// Private game methods (moving tiles)
	private boolean canMoveHorizontally() {
		for (int r=0; r<size; r++) {
			if (canMove(getRow(r)))
				return true;
		}
		return false;
	}
	private boolean canMoveVertically() {
		for (int c=0; c<size; c++) {
			if (canMove(getCol(c)))
				return true;
		}
		return false;
	}
	private boolean canMoveLeft() {
		for (int r=0; r<size; r++) {
			if (canMoveLeft(getRow(r)))
				return true;
		}
		return false;
	}
	private boolean canMoveRight() {
		for (int r=0; r<size; r++) {
			if (canMoveRight(getRow(r)))
				return true;
		}
		return false;
	}
	private boolean canMoveUp() {
		for (int c=0; c<size; c++) {
			if (canMoveLeft(getCol(c)))
				return true;
		}
		return false;
	}
	private boolean canMoveDown() {
		for (int c=0; c<size; c++) {
			if (canMoveRight(getCol(c)))
				return true;
		}
		return false;
	}
	private void moveLeft() {
		for (int r=0; r<size; r++) {
			short row = getRow(r);
			setRow(moveLeft(row), r);
			score += leftScore(row);
		}
	}
	private void moveRight() {
		for (int r=0; r<size; r++) {
			short row = getRow(r);
			setRow(moveRight(row), r);
			score += rightScore(row);
		}
	}
	private void moveUp() {
		for (int c=0; c<size; c++) {
			short col = getCol(c);
			setCol(moveLeft(col), c);
			score += leftScore(col);
		}
	}
	private void moveDown() {
		for (int c=0; c<size; c++) {
			short col = getCol(c);
			setCol(moveRight(col), c);
			score += rightScore(col);
		}
	}
	// Private game methods (adding tiles)
	private Pos getRandomEmptyLocation() {
		Pos[] empty = getEmptyLocations();
		if (empty.length == 0) return null;
		return empty[random.nextInt(empty.length)];
	}
	protected Pos[] getEmptyLocations() {
		Pos[] locs = new Pos[size*size];
		int candidates = 0;
		for (int r=0; r<size; r++) {
			for (int c=0; c<size; c++) {
				if (getTile(r, c) == 0) {
					locs[candidates] = new Pos(r, c);
					candidates += 1;
				}
			}
		}
		if (candidates == 0) return new Pos[0];
		return Arrays.copyOf(locs, candidates);
	}
	public void testValidity() {
		valid = canMove();
	}

	// Board operations
	protected short getRow(int r) {
		return (short)((tiles >> r*ROW_SIZE) & ROW_MASK); // Shifting to the right and nabbing the last sixteen bits as a short.
	};
	protected short getCol(int c) {
		int num = 0;
		for (int r=size-1; r>-1; r--) {
			num = (num << TILE_SIZE) | getTile(r, c); // Shifting to the left and adding four bits as a byte 0b0000abcd.
		}
		return (short)num;
	}
	protected byte getTile(int r, int c) {
		return (byte)((tiles >> (ROW_SIZE*r+TILE_SIZE*c)) & TILE_MASK); // Shifting to the right and nabbing the last four bits as a byte 0b0000abcd.
	};
	private void setRow(short row, int r) {
		int index = ROW_SIZE*r;
		tiles = (tiles & ~(ROW_MASK << index)) | ((row & SHORT_MASK) << index); // Replacing sixteen bits as a long 0b[...48 zeroes...][16-bit row].
	}
	private void setCol(short col, int c) {
		byte[] order = rowToOrder(col);
		for (int r=0; r<4; r++) {
			setTile(order[r], new Pos(r, c));
		}
	}
	protected void setTile(byte tile, Pos location) {
		int index = ROW_SIZE*location.r + TILE_SIZE*location.c;
		tiles = (tiles & ~(TILE_MASK << index)) | (((long)tile) << index); // Replacing four bits as a long 0b[...60 zeroes...]abcd.
	}

	// Row operations
	private static boolean canMoveLeft(short row) {
		return leftMoveable[rowToIndex(row)];
	}
	private static boolean canMoveRight(short row) {
		return rightMoveable[rowToIndex(row)];
	}
	private static boolean canMove(short row) {
		return moveable[rowToIndex(row)];
	}
	private static short moveLeft(short row) {
		return leftTable[rowToIndex(row)];
	}
	private static short moveRight(short row) {
		return rightTable[rowToIndex(row)];
	}
	private static int leftScore(short row) {
		return leftScore[rowToIndex(row)];
	}
	private static int rightScore(short row) {
		return rightScore[rowToIndex(row)];
	}

	// Lookup table generation
	private static void generateTables() {
		leftTable = new short[length];
		rightTable = new short[length];
		leftMoveable = new boolean[length];
		rightMoveable = new boolean[length];
		moveable = new boolean[length];
		leftScore = new int[length];
		rightScore = new int[length];
		for (int i=Short.MIN_VALUE; i<=Short.MAX_VALUE; i++) {
			short r = (short)i;
			leftTable[rowToIndex(r)] = orderToRow(moveLeft(rowToOrder(r)));
			rightTable[rowToIndex(r)] = orderToRow(moveRight(rowToOrder(r)));
			leftMoveable[rowToIndex(r)] = leftTable[rowToIndex(r)] != r;
			rightMoveable[rowToIndex(r)] = rightTable[rowToIndex(r)] != r;
			moveable[rowToIndex(r)] = leftMoveable[rowToIndex(r)] || rightMoveable[rowToIndex(r)];
			leftScore[rowToIndex(r)] = scoreLeft(rowToOrder(r));
			rightScore[rowToIndex(r)] = scoreRight(rowToOrder(r));
		}
	}
	private static byte[] moveLeft(byte[] row) {
		boolean[] frozen = new boolean[size];
		for (int i=0; i<size; i++) frozen[i] = false;
		for (int i=1; i<size; i++) {
			if (row[i] == 0) continue;
			for (int j=i-1; j>-1; j--) {
				if (frozen[j] || row[j] != 0 && row[j] != row[i]) {
					// Also readable as frozen[j] || row[j] == 0 && j == 0 || row[j] != 0 && row[j] != row[i].
					if (j != i-1) {
						row[j+1] = row[i];
						row[i] = 0;
					}
					break;
				}
				if (row[j] != 0 && row[j] < 15) { // Sorry, but four bits ain't a lot of storage space!
					row[j] = (byte)(row[i] + 1);
					row[i] = 0;
					frozen[j] = true;
					break;
				}
				if (row[j] == 0 && j == 0) {
					row[j] = row[i];
					row[i] = 0;
				}
			}
		}
		return row;
	}
	private static byte[] moveRight(byte[] row) {
		boolean[] frozen = new boolean[size];
		for (int i=0; i<size; i++) frozen[i] = false;
		for (int i=size-2; i>-1; i--) {
			if (row[i] == 0) continue;
			for (int j=i+1; j<size; j++) {
				if (frozen[j] || row[j] != 0 && row[j] != row[i]) {
					// Also readable as frozen[j] || row[j] == 0 && j == 0 || row[j] != 0 && row[j] != row[i].
					if (j != i+1) {
						row[j-1] = row[i];
						row[i] = 0;
					}
					break;
				}
				if (row[j] != 0 && row[j] < 15) { // Sorry, but four bits ain't a lot of storage space!
					row[j] = (byte)(row[i] + 1);
					row[i] = 0;
					frozen[j] = true;
					break;
				}
				if (row[j] == 0 && j == size-1) {
					row[j] = row[i];
					row[i] = 0;
				}
			}
		}
		return row;
	}
	private static int scoreLeft(byte[] row) {
		int inc = 0;
		boolean[] frozen = new boolean[size];
		for (int i=0; i<size; i++) frozen[i] = false;
		for (int i=1; i<size; i++) {
			if (row[i] == 0) continue;
			for (int j=i-1; j>-1; j--) {
				if (frozen[j] || row[j] != 0 && row[j] != row[i]) {
					// Also readable as frozen[j] || row[j] == 0 && j == 0 || row[j] != 0 && row[j] != row[i].
					if (j != i-1) {
						row[j+1] = row[i];
						row[i] = 0;
					}
					break;
				}
				if (row[j] != 0 && row[j] < 15) { // Sorry, but four bits ain't a lot of storage space!
					row[j] = (byte)(row[i] + 1);
					row[i] = 0;
					frozen[j] = true;
					inc += Math.pow(2, row[j]);
					break;
				}
				if (row[j] == 0 && j == 0) {
					row[j] = row[i];
					row[i] = 0;
				}
			}
		}
		return inc;
	}
	private static int scoreRight(byte[] row) {
		int inc = 0;
		boolean[] frozen = new boolean[size];
		for (int i=0; i<size; i++) frozen[i] = false;
		for (int i=size-2; i>-1; i--) {
			if (row[i] == 0) continue;
			for (int j=i+1; j<size; j++) {
				if (frozen[j] || row[j] != 0 && row[j] != row[i]) {
					// Also readable as frozen[j] || row[j] == 0 && j == 0 || row[j] != 0 && row[j] != row[i].
					if (j != i+1) {
						row[j-1] = row[i];
						row[i] = 0;
					}
					break;
				}
				if (row[j] != 0 && row[j] < 15) { // Sorry, but four bits ain't a lot of storage space!
					row[j] = (byte)(row[i] + 1);
					row[i] = 0;
					frozen[j] = true;
					inc += Math.pow(2, row[j]);
					break;
				}
				if (row[j] == 0 && j == size-1) {
					row[j] = row[i];
					row[i] = 0;
				}
			}
		}
		return inc;
	}

	// Field access
	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int r=0; r<size; r++) {
			sb.append("|");
			for (int c=0; c<size; c++) {
				String num = getTile(r, c) == 0 ? "." : String.valueOf((int)Math.pow(2, getTile(r, c)));
				for (int i=0; i<5-num.length(); i++) {
					sb.append(" ");
				}
				sb.append(num);
				sb.append(" ");
			}
			sb.append("| ");
			if (r == 2) sb.append(getScore());
			sb.append("\n");
		}
		return sb.toString();
	}
	public boolean isValid() { return valid; }
	public int getScore() { return score; }
	public int getEstimatedScore() {
		double score = 0;
		for (int r=0; r<size; r++) {
			for (int c=0; c<size; c++) {
				int tile = getTile(r, c);
				if (tile >= 2) {
					score += (tile - 13.0/11.0) * Math.pow(2, tile);
				}
			}
		}
		return (int)Math.round(score);
	}
	public int getLargestTile() {
		int largest = -1;
		for (int r=0; r<size; r++) {
			for (int c=0; c<size; c++) {
				int tile = getTile(r, c);
				if (tile > largest) {
					largest = tile;
				}
			}
		}
		return (int)Math.pow(2, largest);
	}
}
