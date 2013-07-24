package othello_main;

import java.util.Scanner;
import java.util.StringTokenizer;

public class Othello {

	public static void main(String args[]) {
		Board board = new Board();
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
			} else if (command.contains("quit")) {
				System.exit(0);
			} else {
				System.out.println("Invalid command.");
			}
		}
	}

}
