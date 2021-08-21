package com.apprisingsoftware.game2048.graphical;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class Board {

	static Color emptyTileColor = Color.WHITE;
	static Color newTileColor = Color.ORANGE;
	static Color combinedTileColor = Color.GREEN;
	static Color movedTileColor = new Color(255, 255, 150);

	private int[][] tiles;
	public Color[][] colors;
	private int size;
	private boolean valid;
	private int score;

	private Random random;

	public Board(int size) {
		super();

		this.size = size;
		tiles = new int[size][size];
		colors = new Color[size][size];
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				tiles[x][y] = 0;
				colors[x][y] = emptyTileColor;
			}
		}
		valid = true;
		score = 0;

		random = new Random();

		addRandomTile();
		addRandomTile();
	}

	public Board(Board other) {
		super();

		size = other.size;
		valid = other.valid;
		score = other.score;
		tiles = new int[size][size];
		for (int i=0; i<size; i++) {
			tiles[i] = other.tiles[i].clone();
		}
	}

	public void addRandomTile() {
		int tile;
		if (random.nextInt(10) == 0) tile = 4;
		else tile = 2;

		Pos loc = getRandomEmptyLocation();
		if (loc == null) {
			invalidateBoard();
		}
		else {
			tiles[loc.x][loc.y] = tile;
			colors[loc.x][loc.y] = newTileColor;
		}
	}

	public void checkValidity() {
		boolean checksOut = false;
		Pos[] dirs = Pos.compass;
		for (Pos dir : dirs) {
			if (isMoveable(dir)) {
				checksOut = true;
				break;
			}
		}
		if (!checksOut) {
			invalidateBoard();
		}
	}

	public void invalidateBoard() {
		valid = false;
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				colors[x][y] = emptyTileColor;
			}
		}
	}

	public boolean moveTiles(Button direction, boolean color) {
		if (!isMoveable(new Pos(direction))) {
			return false;
		}
		boolean frozen[][] = new boolean[size][size];
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				frozen[x][y] = false;
			}
		}
		switch (direction) {
		case LEFT:
			for (int x=0; x<size; x++) {
				for (int y=0; y<size; y++) {
					moveTile(x, y, new Pos(direction), frozen, color);
				}
			}
			break;
		case RIGHT:
			for (int x=size-1; x>-1; x--) {
				for (int y=0; y<size; y++) {
					moveTile(x, y, new Pos(direction), frozen, color);
				}
			}
			break;
		case UP:
			for (int y=0; y<size; y++) {
				for (int x=0; x<size; x++) {
					moveTile(x, y, new Pos(direction), frozen, color);
				}
			}
			break;
		case DOWN:
			for (int y=size-1; y>-1; y--) {
				for (int x=0; x<size; x++) {
					moveTile(x, y, new Pos(direction), frozen, color);
				}
			}
			break;
		default:
			break;
		}
		return true;
	}

	private void moveTile(int x, int y, Pos direction, boolean[][] frozen, boolean color) {
		if (tiles[x][y] == 0) return;
		int newx = x;
		int newy = y;
		String state = "none";
		while (true) {
			newx += direction.x;
			newy += direction.y;
			if (newx < 0 || newx >= size || newy < 0 || newy >= size) {
				newx -= direction.x;
				newy -= direction.y;
				state = "move";
				break;
			}
			if (tiles[newx][newy] != 0) {
				if (tiles[newx][newy] == tiles[x][y] && !frozen[newx][newy]) {
					state = "replace";
					break;
				}
				else {
					newx -= direction.x;
					newy -= direction.y;
					state = "move";
					break;
				}
			}
		}
		if (state.equals("move")) {
			if (color) colors[x][y] = emptyTileColor;
			if (newx != x || newy != y) {
				tiles[newx][newy] = tiles[x][y];
				tiles[x][y] = 0;
				if (color) colors[newx][newy] = movedTileColor;
			}
		}
		if (state.equals("replace")) {
			tiles[newx][newy] *= 2;
			tiles[x][y] = 0;
			frozen[newx][newy] = true;
			if (color) colors[x][y] = emptyTileColor;
			if (color) colors[newx][newy] = combinedTileColor;
			score += tiles[newx][newy];
		}
	}

	public boolean isMoveable(Pos direction) {
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				if (tiles[x][y] != 0) {
					int newx = x + direction.x;
					int newy = y + direction.y;
					if (newx >= 0 && newx < size && newy >= 0 && newy < size && (
							tiles[newx][newy] == 0 || tiles[newx][newy] == tiles[x][y])) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private Pos getRandomEmptyLocation() {
		ArrayList<Pos> candidates = new ArrayList<Pos>();
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				if (tiles[x][y] == 0) {
					candidates.add(new Pos(x, y));
				}
			}
		}
		if (candidates.size() == 0) return null;
		return candidates.get(random.nextInt(candidates.size()));
	}

	public int getNumberOfEmptySpaces() {
		int score = 0;
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				if (tiles[x][y] == 0)
					score += 1;
			}
		}
		return score;
	}

	public void display() {
		StringBuilder sb = new StringBuilder();
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				sb.append(tiles[y][x] + "\t");
			}
			sb.append('\n');
		}
		System.out.print(sb.toString());
	}

	public int getSize() {
		return size;
	}
	public boolean isValid() {
		return valid;
	}
	public int getScore() {
		return score;
	}
	public int getTile(Pos pos) {
		return getTile(pos.x, pos.y);
	}
	public int getTile(int x, int y) {
		return tiles[x][y];
	}

	/////////////////////////
	// Algorithmic scoring //
	/////////////////////////

	private enum Algorithm {
		score, path
	}

	public double heuristic() {
		Algorithm alg = Algorithm.path;
		switch (alg) {
		case score:
			return score;
		case path:
			double total = 0;
			int i = 0;
			double ratio = 0.85;
			int lastTile = tiles[0][0];
			int lastX = 0;
			int lastY = 0;
			for (int n=0; n<size*size; n++) {
				int y = n/size;
				int x = n%size; // This will iterate in row-major order.
				int currentTile = tiles[y][x]; // Oops, apparently x corresponds to row not column.
				// I want the ordering to go left to right first.
				if (currentTile != 0) {
					total += currentTile * Math.pow(ratio, i);
					if (lastTile < currentTile) {
						if (lastTile != 0) {
							if (!(lastX-1 >= 0 && tiles[lastX-1][lastY] == tiles[lastX][lastY])) {
								double amount = Math.pow(Math.pow(currentTile, 2) - Math.pow(lastTile, 2), 0.5);
								total -= amount * Math.pow(ratio, i);
							}
						}
						else {
							total -= Math.pow(currentTile, 2) * Math.pow(ratio, i);
						}
					}
					lastTile = currentTile;
					lastX = x;
					lastY = y;
				}
				i += 1;
			}
			return total;
		default:
			return random.nextDouble()*100;
		}
	}

	public int getNumberOfJoins() {
		int total = 0;
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				if (colors[x][y].equals(combinedTileColor)) {
					total += 1;
				}
			}
		}
		return total;
	}

	public int getNumberOfTiles() {
		int total = 0;
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				if (tiles[x][y] != 0) {
					total += 1;
				}
			}
		}
		return total;
	}

	public double getScoreOnBoard() {
		double total = 0;
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				if (tiles[x][y] != 0) {
					total += Math.log(tiles[x][y] + 1);
				}
			}
		}
		return total;
	}

	public int getOrderingScore() {
		int total = 0;
		for (int x=0; x<size-1; x++) {
			for (int y=0; y<size-1; y++) {
				if (tiles[x][y] != 0 && tiles[x][y+1] != 0) {
					int factor = (int)(Math.log((double)tiles[x][y+1]/tiles[x][y])/Math.log(2));
					if (tiles[x][y+1] > tiles[x][y]) {
						total += factor;
					}
					else {
						total += factor*2;
					}
				}
				if (tiles[x][y] != 0 && tiles[x+1][y] != 0) {
					int factor = (int)(Math.log((double)tiles[x+1][y]/tiles[x][y])/Math.log(2));
					if (tiles[x+1][y] > tiles[x][y]) {
						total += factor;
					}
					else {
						total += factor*2;
					}
				}
			}
		}
		return total;
	}

	public double getAdjacentTileScore() {
		Pos[] directions = Pos.compass;
		double score = 0;
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				for (Pos direction : directions) {
					int x2 = x+direction.x, y2 = y+direction.y;
					if (x2 >= 0 && x2 < size && y2 >= 0 && y2 < size && tiles[x2][y2] == 0) {
						score += 1.0/tiles[x][y] * 4096;
						break;
					}
				}
			}
		}
		return score;
	}

	public double getLogTileScore() {
		double score = 0;
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				if (tiles[x][y] != 0)
					score += Math.log(tiles[x][y]) * 4;
			}
		}
		return score;
	}

	public int getAlignedTiles() {
		Pos[] directions = Pos.compass;
		int score = 0;
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				for (Pos direction : directions) {
					int x2 = x+direction.x, y2 = y+direction.y;
					if (x2 >= 0 && x2 < size && y2 >= 0 && y2 < size && tiles[x2][y2] == tiles[x][y]) {
						score += 1;
					}
				}
			}
		}
		return score;
	}

}
