package com.apprisingsoftware.game2048.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Applet {

	private static final String separator = "+------------------------+";

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
		String input;
		Solver solver;
		System.out.println("Would you like to solve using a [heuristic] or [monte carlo] methods?");
		System.out.print(">>> ");
		input = reader.readLine();
		switch (input.charAt(0)) {
		case 'h':
			System.out.println("Which heuristic would you like to use?");
			System.out.println("[random] Completely random!");
			System.out.println("[score] Based only on score.");
			System.out.println("[empty] Based on amount of empty space, with a bonus\n        if the highest-value tiles are on the edges.");
			System.out.println("[adv-empty] The same as above, except with a penalty\n            instead of a bonus if the highest-value and second-highest-value\n            tiles are on opposite sides of a row or column.");
			System.out.println("[monotonic] Maximizes smoothness and monotonicity, as per\n         http://stackoverflow.com/a/22389702.");
			System.out.print(">>> ");
			input = reader.readLine();
			AbstractHeuristic heuristic;
			switch (input.charAt(0)) {
			case 'r': heuristic = new RandomHeuristic(); break;
			case 's': heuristic = new ScoreHeuristic(); break;
			case 'e': heuristic = new EmptySpaceHeuristic(); break;
			case 'a': heuristic = new AdvancedEmptySpaceHeuristic(); break;
			case 'm': heuristic = new MonotonicitySmoothnessHeuristic(); break;
			default:
				System.out.println("That's not a valid heuristic!");
				System.exit(1);
				return;
			}
			System.out.print("Enter recursion depth: ");
			int recursionDepth = Integer.parseInt(reader.readLine());
			solver = new HeuristicSolver(heuristic, recursionDepth);
			break;
		case 'm':
			System.out.print("Enter games per move (recommended ~500, time is O(n)): ");
			int gamesPerMove = Integer.parseInt(reader.readLine());
			solver = new MonteCarloSolver(gamesPerMove);
			break;
		default:
			System.out.println("That's not a valid input!");
			System.exit(1);
			return;
		}
		GameManager manager = new GameManager(solver);
		while (true) {
			System.out.printf("%s%n%s%s%n", separator, manager, separator);
			System.out.print("Enter direction: ");
			input = reader.readLine();
			if (input.equals("autosolve") || input.equals("autoplay")) {
				while (manager.getBoard().isValid()) {
					manager.moveBoard(solver.getBestDirection(manager.getBoard()));
					System.out.printf("%s%n%s", separator, manager);
				}
				System.out.printf("%s%nGame over! Press enter to try again.", separator);
				reader.readLine();
				manager = new GameManager(solver);
			}
			else if (input.length() >= 7 && input.substring(0, 7).equals("profile")) {
				try {
					int trials = Integer.parseInt(input.substring(8));
					Integer[] scores = new Integer[trials];
					Integer[] largest = new Integer[trials];
					for (int trial=0; trial<trials; trial++) {
						manager = new GameManager(solver);
						while (manager.getBoard().isValid()) {
							manager.moveBoard(solver.getBestDirection(manager.getBoard()));
						}
						scores[trial] = manager.getBoard().getScore();
						largest[trial] = manager.getBoard().getLargestTile();
					}
					manager = new GameManager(solver);
					System.out.println("Ran " + trials + " games.");
					for (int tile=0; tile<16; tile++) {
						int count = 0;
						for (int e : largest)
							if (e == (int)Math.pow(2, tile)) count++;
						if (count > 0) {
							System.out.println(Math.pow(2, tile) + " tile achieved in " + count + " games.");
						}
					}
					List<Integer> list = Arrays.asList(scores);
					System.out.println("Lowest score was " + Collections.min(list));
					System.out.println("Highest score was " + Collections.max(list));
					int sum = 0;
					for (Integer e : scores)
						sum += e;
					System.out.println("Average score was " + (double)sum / scores.length);
				}
				catch (StringIndexOutOfBoundsException e) {
					System.out.println("Invalid command. Profile must be used with a number, i.e. 'profile 10'.");
				}
			}
			else if (input.equals("evil")) {
				if (manager.getEvil()) {
					manager.setEvil(false);
					System.out.println("2048 is no longer evil.");
				}
				else {
					manager.setEvil(true);
					System.out.println("2048 is now evil.");
				}
			}
			else if (input.length() == 0) {
				System.out.println("Invalid keycode entered. Please use one of [wasd, h, autoplay, profile, evil].");
			}
			else {
				char direction = input.charAt(0);
				switch (direction) {
				case 'a':
					manager.moveBoard(Button.LEFT);
					break;
				case 'd':
					manager.moveBoard(Button.RIGHT);
					break;
				case 'w':
					manager.moveBoard(Button.UP);
					break;
				case 's':
					manager.moveBoard(Button.DOWN);
					break;
				case 'h':
					manager.moveBoard(solver.getBestDirection(manager.getBoard()));
					break;
				default:
					System.out.println("Invalid keycode entered. Please use one of [wasd, h, autoplay, evil].");
				}
				if (!manager.getBoard().isValid()) {
					System.out.printf("%s%n%s%s%nGame over! Press enter to try again.", separator, manager, separator);
					reader.readLine();
					manager = new GameManager(solver);
				}
			}
		}
	}
}
