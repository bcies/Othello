package othello_main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Othello {

	public static void main(String args[]) {
		double timePerMove = 5.0;
		Board board = new Board();
		List<Integer>turnPlayouts = new ArrayList<Integer>();
		Player player = new Player(timePerMove);
		Scanner in = new Scanner(System.in);
		StringTokenizer token;
		while (!board.gameOver()) {
			String command = in.nextLine();
			if (command.contains("play ")) {
				token = new StringTokenizer(command, " ");
				token.nextToken();
				if (!board.play(board.stringToIndex(token.nextToken()))) {
					System.out.println("Invalid move.\n");
				}
			} else if (command.contains("showboard")) {
				System.out.println(board);
				System.out.println("\n"
						+ board.colorToString(board.getColorToPlay())
						+ "'s turn:");
			} else if (command.contains("genmove")) {
				int playerMove;
				if(command.contains("showtree")) {
					playerMove = player.getBestMove(board, true);
				} else {					
					playerMove = player.getBestMove(board, false);
				}
				int turnPlayout = player.getPlayouts();
				turnPlayouts.add(turnPlayout);
				board.play(playerMove);
				System.out.println("Player played at " + Board.indexToString(playerMove));
				System.out.println("Player did " + turnPlayout + " playouts in " + timePerMove + " seconds.");
			} else if (command.contains("quit")) {
				System.exit(0);
			} else {
				System.out.println("Invalid command.");
			}
		}
	}

}
