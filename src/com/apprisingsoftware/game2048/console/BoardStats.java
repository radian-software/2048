package com.apprisingsoftware.game2048.console;

public class BoardStats {

	// General board statistics
	public static final int size = 4; // Side length of board
	protected static final int largestTile = 15; // Corresponds to 32,768
	protected static final int length = (int)Math.pow(largestTile+1, size); // Number of possible rows = ROW_MASK + 1
	// Bit shift constants
	protected static final long HALF_BYTE_MASK = 0xf;
	protected static final long BYTE_MASK = 0xff;
	protected static final long SHORT_MASK = 0xffff;
	protected static final long INT_MASK = 0xffffffff;
	protected static final long LONG_MASK = 0xffffffffffffffffL; // = -1
	protected static final int HALF_BYTE_SIZE = 4;
	protected static final int BYTE_SIZE = 8;
	protected static final int SHORT_SIZE = 16;
	protected static final int INT_SIZE = 32;
	protected static final int LONG_SIZE = 64;

	// 2 tiles = 1 byte [8 bits]
	// 1 row = 1 short [16 bits]
	// 1 board = 1 long [64 bits]
	protected static final long TILE_MASK = HALF_BYTE_MASK;
	protected static final long ROW_MASK = SHORT_MASK;
	protected static final long BOARD_MASK = LONG_MASK;
	protected static final int TILE_SIZE = HALF_BYTE_SIZE;
	protected static final int ROW_SIZE = SHORT_SIZE;
	protected static final int BOARD_SIZE = LONG_SIZE;

	protected static short orderToRow(byte[] order) {
		int num = 0;
		for (int i=size-1; i>-1; i--) {
			num = (num << TILE_SIZE) | order[i]; // Shifting to the left and adding four bits as a byte 0b0000abcd.
		}
		return (short)num; // Nabbing the last sixteen bits as a short.
	}
	protected static byte[] rowToOrder(short num) {
		int snum = num;
		byte[] order = new byte[size];
		for (int i=0; i<size; i++) {
			order[i] = (byte)(snum & TILE_MASK); // Nabbing the last four bits as a byte 0b0000abcd.
			snum = snum >> TILE_SIZE; // Shifting to the right.
		}
		return order;
	}
	protected static int rowToIndex(short row) {
		return row < 0 ? Math.abs(row) + Short.MAX_VALUE : row;
	}
}
