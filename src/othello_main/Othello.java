package othello_main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Othello {

	private static boolean autogame;
	private static long blackPAverages;
	private static long whitePAverages;

	public static void main(String args[]) {
		CudaNode.prepareGPU();
		autogame = false;
		double timePerMove = 2.0;
		Player black = new Player(timePerMove);
		black.setCuda(14, 512);
		Player white = new Player(timePerMove);
		boolean startGame = false;
		Scanner in = new Scanner(System.in);
		System.out
				.println("Run Experiment or Play Game?\n(Type experiment or game)");
		while (!startGame) {
			String command = in.nextLine();
			blackPAverages = 0;
			whitePAverages = 0;
			if (command.contains("experiment")) {
				startGame = true;
				runExperiment();
			} else if (command.contains("game")) {
				startGame = true;
				runGame(black, white);
			} else if (command.contains("cuda")) {
				startGame = true;
				cudaExperiment();
			}
		}
	}

	public static void runExperiment() {
		autogame = true;
		int totalGames;
		int blocks;
		int threads;
		double player1TimePerMove;
		double player2TimePerMove;
		boolean player1Cuda;
		boolean player2Cuda;
		String settingsDescription;

		Properties defaultProp = new Properties();
		try {
			defaultProp.load(new FileInputStream("default.properties"));
		} catch (FileNotFoundException e1) {
			System.err.println("config.properties not found.");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Properties userProp = new Properties(defaultProp);
		try {
			userProp.load(new FileInputStream("user.properties"));
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get the values from the properties file

		totalGames = Integer.parseInt(userProp.getProperty("totalgames"));
		player1TimePerMove = Double.parseDouble(userProp
				.getProperty("blktimepermove"));
		player2TimePerMove = Double.parseDouble(userProp
				.getProperty("whttimepermove"));
		player1Cuda = Boolean.parseBoolean(userProp.getProperty("blkCuda"));
		player2Cuda = Boolean.parseBoolean(userProp.getProperty("whtCuda"));
		blocks = Integer.parseInt(userProp.getProperty("blocks"));
		threads = Integer.parseInt(userProp.getProperty("threads"));
		settingsDescription = userProp.getProperty("settingsdescription");

		Player black = new Player(player1TimePerMove);
		if (player1Cuda){
			black.setCuda(blocks, threads);
		}
		Player white = new Player(player2TimePerMove);
		if (player2Cuda){
			white.setCuda(blocks, threads);
		}
		int blacksum = 0;
		int firstties = 0;
		int win;
		for (int i = 0; i < (totalGames / 2); i++) {
			System.out.println("\nFirst half: Game number " + (i + 1));
			win = runGame(black, white);
			if (win == Board.BLACK) {
				blacksum += 1;
			} else if (win == Board.VACANT) {
				firstties += 1;
			}
		}

		black = new Player(player2TimePerMove);
		if (player2Cuda){
			black.setCuda(blocks, threads);
		}
		white = new Player(player1TimePerMove);
		if (player1Cuda){
			white.setCuda(blocks, threads);
		}
		int whitesum = 0;
		int secondties = 0;
		for (int i = 0; i < (totalGames / 2); i++) {
			System.out.println("\nSecond half: Game number " + (i + 1));
			win = runGame(black, white);
			if (win == Board.WHITE) {
				whitesum += 1;
			} else if (win == Board.VACANT) {
				secondties += 1;
			}
		}

		System.out.println();
		System.out.println("Description:\n");
		System.out.println(settingsDescription + "\n");
		System.out.println("First " + (totalGames / 2) + " games:");
		System.out.println("Black won " + blacksum + " games");
		System.out.println("Ties: " + firstties);
		System.out.println("\nSecond " + (totalGames / 2) + " games:");
		System.out.println("White won " + whitesum + " games");
		System.out.println("Ties: " + secondties);

		try {
			File file = new File("results.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream outStream = new FileOutputStream(file);
			Writer out = new OutputStreamWriter(outStream);
			out.write("Description:\n" + settingsDescription + "\n");
			out.write("\nFirst " + (totalGames / 2) + " games:");
			out.write("\nBlack won " + blacksum + " games");
			out.write("\nTies: " + firstties);
			out.write("\nSecond " + (totalGames / 2) + " games:");
			out.write("\nWhite won " + whitesum + " games");
			out.write("\nTies: " + secondties);
			out.close();
		} catch (Exception e) {
		}
	}
	
	public static void cudaExperiment() {
		autogame = true;
		List<Integer> BlackWins = new ArrayList<Integer>();
		List<Integer> WhiteWins = new ArrayList<Integer>();
		List<Integer> Ties = new ArrayList<Integer>();
		List<Long> BlackAvgPlayouts = new ArrayList<Long>();
		List<Long> WhiteAvgPlayouts = new ArrayList<Long>();
		
		Player serial = new Player(8.0);
			
		for(int TPM = 1; TPM <= 8; TPM = TPM * 2) {
			blackPAverages = 0;
			whitePAverages = 0;
			Player cuda = new Player(TPM);
			int blacksum = 0;
			int whitesum = 0;
			int ties = 0;
			int win;
			for (int i = 0; i < 100; i++) {
				System.out.println("\nGame number " + (i + 1)
						+ "\nBlack Time: " + TPM + " seconds,\nWhite Time: 8 seconds.");
				win = runGame(cuda, serial);
				if (win == Board.BLACK) {
					blacksum += 1;
				} else if (win == Board.VACANT) {
					ties += 1;
				} else {
					whitesum += 1;
				}
			}
			BlackWins.add(blacksum);
			WhiteWins.add(whitesum);
			Ties.add(ties);
			BlackAvgPlayouts.add(blackPAverages / 100);
			WhiteAvgPlayouts.add(whitePAverages / 100);
		}

		System.out.println("\nBlack uses CUDA. White is a serial player.");
		System.out.println("\nAll out of 100 games\n");
		System.out.println("Black 1 second: ");
		System.out.println("Black Wins: " + BlackWins.get(0));
		System.out.println("White Wins: " + WhiteWins.get(0));
		System.out.println("Ties: " + Ties.get(0));
		System.out.println("Black Average Playouts: " + BlackAvgPlayouts.get(0));
		System.out.println("White Average Playouts: " + WhiteAvgPlayouts.get(0));
		System.out.println();
		System.out.println("Black 2 seconds: ");
		System.out.println("Black Wins: " + BlackWins.get(1));
		System.out.println("White Wins: " + WhiteWins.get(1));
		System.out.println("Ties: " + Ties.get(1));
		System.out.println("Black Average Playouts: " + BlackAvgPlayouts.get(1));
		System.out.println("White Average Playouts: " + WhiteAvgPlayouts.get(1));
		System.out.println();
		System.out.println("Black 4 seconds: ");
		System.out.println("Black Wins: " + BlackWins.get(2));
		System.out.println("White Wins: " + WhiteWins.get(2));
		System.out.println("Ties: " + Ties.get(2));
		System.out.println("Black Average Playouts: " + BlackAvgPlayouts.get(2));
		System.out.println("White Average Playouts: " + WhiteAvgPlayouts.get(2));
		System.out.println();
		System.out.println("Black 8 seconds: ");
		System.out.println("Black Wins: " + BlackWins.get(3));
		System.out.println("White Wins: " + WhiteWins.get(3));
		System.out.println("Ties: " + Ties.get(3));
		System.out.println("Black Average Playouts: " + BlackAvgPlayouts.get(3));
		System.out.println("White Average Playouts: " + WhiteAvgPlayouts.get(3));

		try {
			File file = new File("SerialResults.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream outStream = new FileOutputStream(file);
			Writer out = new OutputStreamWriter(outStream);
			out.write("Black uses CUDA. White is a serial player.");
			out.write("\nAll out of 100 games\n");
			out.write("\nBlack 1 second: ");
			out.write("\nBlack Wins: " + BlackWins.get(0));
			out.write("\nWhite Wins: " + WhiteWins.get(0));
			out.write("\nTies: " + Ties.get(0));
			out.write("\nBlack Average Playouts: " + BlackAvgPlayouts.get(0));
			out.write("\nWhite Average Playouts: " + WhiteAvgPlayouts.get(0));
			out.write("\n");
			out.write("\nBlack 2 seconds: ");
			out.write("\nBlack Wins: " + BlackWins.get(1));
			out.write("\nWhite Wins: " + WhiteWins.get(1));
			out.write("\nTies: " + Ties.get(1));
			out.write("\nBlack Average Playouts: " + BlackAvgPlayouts.get(1));
			out.write("\nWhite Average Playouts: " + WhiteAvgPlayouts.get(1));
			out.write("\n");
			out.write("\nBlack 4 seconds: ");
			out.write("\nBlack Wins: " + BlackWins.get(2));
			out.write("\nWhite Wins: " + WhiteWins.get(2));
			out.write("\nTies: " + Ties.get(2));
			out.write("\nBlack Average Playouts: " + BlackAvgPlayouts.get(2));
			out.write("\nWhite Average Playouts: " + WhiteAvgPlayouts.get(2));
			out.write("\n");
			out.write("\nBlack 8 seconds: ");
			out.write("\nBlack Wins: " + BlackWins.get(3));
			out.write("\nWhite Wins: " + WhiteWins.get(3));
			out.write("\nTies: " + Ties.get(3));
			out.write("\nBlack Average Playouts: " + BlackAvgPlayouts.get(3));
			out.write("\nWhite Average Playouts: " + WhiteAvgPlayouts.get(3));
			out.close();
		} catch (Exception e) {
		}
	}

	public static int runGame(Player black, Player white) {
		Board board = new Board();
		List<Long> blackTurnPlayouts = new ArrayList<Long>();
		List<Long> whiteTurnPlayouts = new ArrayList<Long>();
		String command;
		Scanner in = new Scanner(System.in);
		StringTokenizer token;
		while (!board.gameOver()) {
			if (!autogame) {
				command = in.nextLine();
			} else {
				command = "genmove";
			}
			if (command.contains("autogame")) {
				autogame = true;
			} else if (command.contains("play ")) {
				token = new StringTokenizer(command, " ");
				token.nextToken();
				if (!board.play(Board.stringToIndex(token.nextToken()))) {
					System.out.println("Invalid move.\n");
				}
			} else if(command.contains("settime ")) {
				token = new StringTokenizer(command, " ");
				token.nextToken();
				double time = Double.parseDouble(token.nextToken());
				if (black.setTimePerMove(time) && white.setTimePerMove(time)) {
					System.out.println("Time per move set to " + time
							+ " seconds.");
				} else {
					System.out.println("Invalid time.");
				}
			} else if (command.contains("showboard")) {
				System.out.println(board);
				System.out.println("\n"
						+ Board.colorToString(board.getColorToPlay())
						+ "'s turn:");
			} else if (command.contains("genmove")) {
				int playerMove;
				long turnPlayout;
				boolean showTree = false;
				if (command.contains("showtree")) {
					showTree = true;
				}
				long startTime = System.nanoTime();
				if (board.getColorToPlay() == Board.BLACK) {
					playerMove = black.getBestMove(board, showTree);
					turnPlayout = black.getPlayouts();
					blackTurnPlayouts.add(turnPlayout);
				} else {
					playerMove = white.getBestMove(board, showTree);
					turnPlayout = white.getPlayouts();
					whiteTurnPlayouts.add(turnPlayout);
				}
				int turnTime = (int) ((System.nanoTime() - startTime) / 1000000.0);
				board.play(playerMove);
				System.out.println("Player played at "
						+ Board.indexToString(playerMove));
				System.out.println("Player did " + turnPlayout
						+ " playouts in " + turnTime + " ms.\n");
			} else if (command.contains("quit")) {
				System.exit(0);
			} else {
				System.out.println("Invalid command.");
			}
		}
		System.out.println(board);
		int winner = board.getWinner();
		if (winner == Board.BLACK) {
			System.out.println("Black won!");
		} else if (winner == Board.WHITE) {
			System.out.println("White won!");
		} else {
			System.out.println("It's a tie!");
		}
		
		System.out.println("Black points: " + board.getScore(Board.BLACK));
		System.out.println("White points: " + board.getScore(Board.WHITE));

		double blackPlayoutSum = 0;
		double whitePlayoutSum = 0;
		for (Long playouts : blackTurnPlayouts) {
			blackPlayoutSum += playouts;
		}
		for (Long playouts : whiteTurnPlayouts) {
			whitePlayoutSum += playouts;
		}
		long blackPAverage = (long) (blackPlayoutSum / blackTurnPlayouts.size());
		long whitePAverage = (long) (whitePlayoutSum / whiteTurnPlayouts.size());
		blackPAverages += blackPAverage;
		whitePAverages += whitePAverage;
		System.out.println("Average black playouts per turn: "
				+ blackPAverage);
		System.out.println("Average white playouts per turn: "
				+ whitePAverage);
		return winner;
	}

}
