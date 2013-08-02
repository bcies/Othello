package othello_main;

import java.util.ArrayList;
import java.util.List;

public class Board {

	public static final int BOARD_WIDTH = 8;

	public static final int BOARD_AREA = BOARD_WIDTH * BOARD_WIDTH;

	public static final int VACANT = 0;

	public static final int BLACK = 1;

	public static final int WHITE = 2;

	private int[] board;
	private List<Integer> legalMoves;
	private int colorToPlay;
	private boolean gameOver;

	public Board() {
		board = new int[BOARD_AREA];
		colorToPlay = VACANT;
		legalMoves = new ArrayList<Integer>();
		initializeBoard();
		nextTurn();
		gameOver = false;
	}
	
	protected void capture(int index) {
		int x = index % BOARD_WIDTH;
		int y = index / BOARD_WIDTH;
		int newX;
		int newY;
		int nextSpace;
		boolean captureFlag = false;
		int opposite = opposite(colorToPlay);
		List<Integer> capturedStones = new ArrayList<Integer>();
		// Up

		if ((y < 6) && (board[pointToIndex(x, y + 1)] == opposite)) {
			newY = y + 2;
			capturedStones.add(pointToIndex(x, y + 1));
			while (newY <= 7) {
				nextSpace = board[pointToIndex(x, newY)];
				if (nextSpace == colorToPlay) {
					captureFlag = true;
					break;
				} else if (nextSpace == VACANT) {
					break;
				} else {
					capturedStones.add(pointToIndex(x, newY));
				}
				newY++;
			}
		}
		if(!captureFlag) {
			capturedStones.clear();
		}
		for(int i : capturedStones) {
			board[i] = colorToPlay;
		}
		capturedStones.clear();
		captureFlag = false;
		
		// UpR

		if ((x < 6) && (y < 6)
				&& (board[pointToIndex(x + 1, y + 1)] == opposite)) {
			capturedStones.add(pointToIndex(x + 1, y + 1));
			newX = x + 2;
			newY = y + 2;
			while ((newX <= 7) && (newY <= 7)) {
				nextSpace = board[pointToIndex(newX, newY)];
				if (nextSpace == colorToPlay) {
					captureFlag = true;
					break;
				} else if (nextSpace == VACANT) {
					break;
				} else {
					capturedStones.add(pointToIndex(newX, newY));
				}
				newX++;
				newY++;
			}
		}
		if(!captureFlag) {
			capturedStones.clear();
		}
		for(int i : capturedStones) {
			board[i] = colorToPlay;
		}
		capturedStones.clear();
		captureFlag = false;
		
		// R

		if ((x < 6) && (board[pointToIndex(x + 1, y)] == opposite)) {
			capturedStones.add(pointToIndex(x + 1, y));
			newX = x + 2;
			while (newX <= 7) {
				nextSpace = board[pointToIndex(newX, y)];
				if (nextSpace == colorToPlay) {
					captureFlag = true;
					break;
				} else if (nextSpace == VACANT) {
					break;
				} else {
					capturedStones.add(pointToIndex(newX, y));
				}
				newX++;
			}
		}
		if(!captureFlag) {
			capturedStones.clear();
		}
		for(int i : capturedStones) {
			board[i] = colorToPlay;
		}
		capturedStones.clear();
		captureFlag = false;

		// DoR

		if ((x < 6) && (y > 1)
				&& (board[pointToIndex(x + 1, y - 1)] == opposite)) {
			capturedStones.add(pointToIndex(x + 1, y - 1));
			newX = x + 2;
			newY = y - 2;
			while ((newX <= 7) && (newY >= 0)) {
				nextSpace = board[pointToIndex(newX, newY)];
				if (nextSpace == colorToPlay) {
					captureFlag = true;
					break;
				} else if (nextSpace == VACANT) {
					break;
				} else {
					capturedStones.add(pointToIndex(newX, newY));
				}
				newX++;
				newY--;
			}
		}
		if(!captureFlag) {
			capturedStones.clear();
		}
		for(int i : capturedStones) {
			board[i] = colorToPlay;
		}
		capturedStones.clear();
		captureFlag = false;

		// Do

		if ((y > 1) && (board[pointToIndex(x, y - 1)] == opposite)) {
			capturedStones.add(pointToIndex(x, y - 1));
			newY = y - 2;
			while (newY >= 0) {
				nextSpace = board[pointToIndex(x, newY)];
				if (nextSpace == colorToPlay) {
					captureFlag = true;
					break;
				} else if (nextSpace == VACANT) {
					break;
				} else {
					capturedStones.add(pointToIndex(x, newY));
				}
				newY--;
			}
		}
		if(!captureFlag) {
			capturedStones.clear();
		}
		for(int i : capturedStones) {
			board[i] = colorToPlay;
		}
		capturedStones.clear();
		captureFlag = false;

		// DoL

		if ((x > 1) && (y > 1)
				&& (board[pointToIndex(x - 1, y - 1)] == opposite)) {
			capturedStones.add(pointToIndex(x - 1, y - 1));
			newX = x - 2;
			newY = y - 2;
			while ((newX >= 0) && (newY >= 0)) {
				nextSpace = board[pointToIndex(newX, newY)];
				if (nextSpace == colorToPlay) {
					captureFlag = true;
					break;
				} else if (nextSpace == VACANT) {
					break;
				} else {
					capturedStones.add(pointToIndex(newX, newY));
				}
				newX--;
				newY--;
			}
		}
		if(!captureFlag) {
			capturedStones.clear();
		}
		for(int i : capturedStones) {
			board[i] = colorToPlay;
		}
		capturedStones.clear();
		captureFlag = false;

		// L

		if ((x > 1) && (board[pointToIndex(x - 1, y)] == opposite)) {
			capturedStones.add(pointToIndex(x - 1, y));
			newX = x - 2;
			while (newX >= 0) {
				nextSpace = board[pointToIndex(newX, y)];
				if (nextSpace == colorToPlay) {
					captureFlag = true;
					break;
				} else if (nextSpace == VACANT) {
					break;
				} else {
					capturedStones.add(pointToIndex(newX, y));
				}
				newX--;
			}
		}
		if(!captureFlag) {
			capturedStones.clear();
		}
		for(int i : capturedStones) {
			board[i] = colorToPlay;
		}
		capturedStones.clear();
		captureFlag = false;

		// UpL

		if ((x > 1) && (y < 6)
				&& (board[pointToIndex(x - 1, y + 1)] == opposite)) {
			capturedStones.add(pointToIndex(x - 1, y + 1));
			newX = x - 2;
			newY = y + 2;
			while ((newX >= 0) && (newY <= 7)) {
				nextSpace = board[pointToIndex(newX, newY)];
				if (nextSpace == colorToPlay) {
					captureFlag = true;
					break;
				} else if (nextSpace == VACANT) {
					break;
				} else {
					capturedStones.add(pointToIndex(newX, newY));
				}
				newX--;
				newY++;
			}
		}
		if(!captureFlag) {
			capturedStones.clear();
		}
		for(int i : capturedStones) {
			board[i] = colorToPlay;
		}
	}

	protected void checkLegalMoves(int color) {
		legalMoves.clear();
		for (int i = 0; i < BOARD_AREA; i++) {
			if ((board[i] == VACANT) && isLegalMove(i, color)) {
				legalMoves.add(i);
			}
		}
	}

	public static String colorToString(int color) {
		if (color == BLACK) {
			return "Black";
		} else if (color == WHITE) {
			return "White";
		} else {
			return "Vacant";
		}
	}

	public void copyBoard(Board fromBoard) {
		this.colorToPlay = fromBoard.getColorToPlay();
		int[] fromBoardBoard = fromBoard.getBoard();
		for (int i = 0; i < BOARD_AREA; i++) {
			this.board[i] = fromBoardBoard[i];
		}
	}

	public boolean gameOver() {
		return gameOver;
	}

	public int[] getBoard() {
		return board;
	}

	public int getColorToPlay() {
		return colorToPlay;
	}

	public List<Integer> getLegalMoves() {
		return legalMoves;
	}

	public int getScore(int color) {
		int score = 0;
		for (int i : board) {
			if (i == color) {
				score++;
			}
		}
		return score;
	}
	
	public int getPoint(int index){
		return board[index];
	}
	
	public int getWinner(int komi) {
		int blackScore = getScore(BLACK)+komi;
		int whiteScore = getScore(WHITE);
		if(blackScore > whiteScore) {
			return BLACK;
		} else if (whiteScore > blackScore) {
			return WHITE;
		}
		return -1;
	}

	public void initializeBoard() {
		play(pointToIndex(3, 3), WHITE);
		play(pointToIndex(4, 4), WHITE);
		play(pointToIndex(3, 4), BLACK);
		play(pointToIndex(4, 3), BLACK);
	}

	public boolean isLegalMove(int index, int color) {
		int x = index % BOARD_WIDTH;
		int y = index / BOARD_WIDTH;
		int newX;
		int newY;
		int nextSpace;
		int opposite = opposite(color);
		// Up

		if ((y < 6) && (board[pointToIndex(x, y + 1)] == opposite)) {
			newY = y + 2;
			while (newY <= 7) {
				nextSpace = board[pointToIndex(x, newY)];
				if (nextSpace == color) {
					return true;
				} else if (nextSpace == VACANT) {
					break;
				}
				newY++;
			}
		}
		// UpR

		if ((x < 6) && (y < 6)
				&& (board[pointToIndex(x + 1, y + 1)] == opposite)) {
			newX = x + 2;
			newY = y + 2;
			while ((newX <= 7) && (newY <= 7)) {
				nextSpace = board[pointToIndex(newX, newY)];
				if (nextSpace == color) {
					return true;
				} else if (nextSpace == VACANT) {
					break;
				}
				newX++;
				newY++;
			}
		}

		// R

		if ((x < 6) && (board[pointToIndex(x + 1, y)] == opposite)) {
			newX = x + 2;
			while (newX <= 7) {
				nextSpace = board[pointToIndex(newX, y)];
				if (nextSpace == color) {
					return true;
				} else if (nextSpace == VACANT) {
					break;
				}
				newX++;
			}
		}

		// DoR

		if ((x < 6) && (y > 1)
				&& (board[pointToIndex(x + 1, y - 1)] == opposite)) {
			newX = x + 2;
			newY = y - 2;
			while ((newX <= 7) && (newY >= 0)) {
				nextSpace = board[pointToIndex(newX, newY)];
				if (nextSpace == color) {
					return true;
				} else if (nextSpace == VACANT) {
					break;
				}
				newX++;
				newY--;
			}
		}

		// Do

		if ((y > 1) && (board[pointToIndex(x, y - 1)] == opposite)) {
			newY = y - 2;
			while (newY >= 0) {
				nextSpace = board[pointToIndex(x, newY)];
				if (nextSpace == color) {
					return true;
				} else if (nextSpace == VACANT) {
					break;
				}
				newY--;
			}
		}

		// DoL

		if ((x > 1) && (y > 1)
				&& (board[pointToIndex(x - 1, y - 1)] == opposite)) {
			newX = x - 2;
			newY = y - 2;
			while ((newX >= 0) && (newY >= 0)) {
				nextSpace = board[pointToIndex(newX, newY)];
				if (nextSpace == color) {
					return true;
				} else if (nextSpace == VACANT) {
					break;
				}
				newX--;
				newY--;
			}
		}

		// L

		if ((x > 1) && (board[pointToIndex(x - 1, y)] == opposite)) {
			newX = x - 2;
			while (newX >= 0) {
				nextSpace = board[pointToIndex(newX, y)];
				if (nextSpace == color) {
					return true;
				} else if (nextSpace == VACANT) {
					break;
				}
				newX--;
			}
		}

		// UpL

		if ((x > 1) && (y < 6)
				&& (board[pointToIndex(x - 1, y + 1)] == opposite)) {
			newX = x - 2;
			newY = y + 2;
			while ((newX >= 0) && (newY <= 7)) {
				nextSpace = board[pointToIndex(newX, newY)];
				if (nextSpace == color) {
					return true;
				} else if (nextSpace == VACANT) {
					break;
				}
				newX--;
				newY++;
			}
		}
		return false;
	}

	public static String indexToString(int index) {
		int row = (index / BOARD_WIDTH) + 1;
		int column = (index % BOARD_WIDTH);
		String string = "";
		string += (char) (column + 'a');
		string += row;
		return string;
	}

	/**
	 * Switches the turn to the opposite color and checks for the legal moves of
	 * that next player.
	 * 
	 * @return True if there are no more legal moves for the next player.
	 */
	public boolean nextTurn() {
		if (colorToPlay == BLACK) {
			colorToPlay = WHITE;
		} else {
			colorToPlay = BLACK;
		}
		checkLegalMoves(colorToPlay);
		return legalMoves.isEmpty();
	}

	public int opposite(int color) {
		if (color == BLACK) {
			return WHITE;
		} else if (color == WHITE) {
			return BLACK;
		} else
			return VACANT;
	}

	public boolean play(int index) {
		return play(index, colorToPlay);
	}
	
	public boolean playoutMove(int index) {
		if(board[index] != VACANT) {
			return false;
		}
		board[index] = colorToPlay;
		capture(index);
		if (colorToPlay == BLACK) {
			colorToPlay = WHITE;
		} else {
			colorToPlay = BLACK;
		}
		return true;
	}

	public boolean play(int index, int color) {
		if (color == colorToPlay) {
			if (legalMoves.contains(index)) {
				board[index] = color;
				capture(index);
				if (nextTurn()) {
					if (nextTurn()) {
						gameOver = true;
					}
				}
				return true;
			} else {
				return false;
			}
		}
		board[index] = color;
		return true;

	}

	public static int pointToIndex(int x, int y) {
		return x + y * BOARD_WIDTH;
	}

	public static int stringToIndex(String string) {
		int column;
		if (string.charAt(0) < 96) {
			column = string.charAt(0) - 'A';
		} else {
			column = string.charAt(0) - 'a';
		}
		string = string.substring(1);
		int row = Integer.parseInt(string) - 1;
		int index = row * BOARD_WIDTH + column;
		return index;
	}

	public String toString() {
		String s = "";
		for (int y = BOARD_WIDTH - 1; y >= 0; y--) {
			s += (y + 1) + " ";
			for (int x = 0; x < BOARD_WIDTH; x++) {
				if (board[pointToIndex(x, y)] == VACANT) {
					s += ". ";
				} else if (board[pointToIndex(x, y)] == BLACK) {
					s += "# ";
				} else {
					s += "O ";
				}
			}
			s += "\n";
		}
		s += "  A B C D E F G H";
		return s;
	}

}
