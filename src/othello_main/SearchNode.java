package othello_main;

import java.util.ArrayList;
import java.util.List;

public class SearchNode {
	
	private List<SearchNode> children;
	private int move;
	private int color;
	private int playouts;
	private double lastWin;
	private double winRate;
	private boolean finalNode;
	private boolean exhausted;
	
	public SearchNode(int move, int color) {
		this.move = move;
		this.color = color;
		finalNode = false;
		exhausted = false;
		playouts = 0;
		winRate = 0.0;
		lastWin = 0;
		children = new ArrayList<SearchNode>();
	}
	
	public void createChildren(Board board) {
		for(int i : board.getLegalMoves()) {
			children.add(new SearchNode(i, board.getColorToPlay()));
		}
	}
	
	public int getColor() {
		return color;
	}
	
	public double getLastWin() {
		return lastWin;
	}
	
	public int getMove() {
		return move;
	}
	
	public int getPlayouts() {
		return playouts;
	}
	
	public double getWinRate() {
		return winRate;
	}
	
	public boolean isExhausted() {
		return exhausted;
	}
	
	public boolean isFinalNode() {
		return finalNode;
	}

	public int playout(Board board) {
		int randMove;
		int formerPlayouts = playouts;
		playouts += 1;
		board.play(move);
		int winner;
		winner = board.getWinner();
		if (board.gameOver()) {
			finalNode = true;
			if (winner == color) {
				winRate = 1.0;
			}
			if (winner == -1) {
				// If the result is a tie.
				winRate = 0.65;
			}
			return winner;
		}
		while (!board.gameOver()) {
			randMove = (int) (Math.random() * Board.BOARD_AREA);
			board.play(randMove);
			winner = board.getWinner();
		}
		if (winner == color) {
			winRate = (formerPlayouts * winRate + 1.0) / (playouts * 1.0);
			lastWin = 1.0;
		} else if (winner == -1) {
			// If the result is a tie.
			winRate = (formerPlayouts * winRate + 0.65) / (playouts * 1.0);
			lastWin = 0.65;
		} else {
			winRate = (formerPlayouts * winRate + 0.0) / (playouts * 1.0);
			lastWin = 0.0;
		}
		return winner;
	}
	
	public int playoutLegal(Board board) {
		//TODO
		return -1;
	}
	
	public void setFinalNode(boolean value) {
		finalNode = value;
	}
	
	public void setPlayouts(int number) {
		playouts = number;
	}
	
	public void setWinRate(double value) {
		winRate = value;
	}
	
	public int traverseNode(Board board) {
		board.play(this.move);
		double bestScore = -1;
		int bestIndex = -1;
		int win;
		double UCBScore;
		if (this.playouts <= 1) {
			createChildren(board);
		}
		if (children.size() == 0) {
			this.exhausted = true;
			return -2;
		}
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).isExhausted()) {
				UCBScore = -2;
			} else if (children.get(i).getPlayouts() != 0) {
				if (children.get(i).isFinalNode()) {
					UCBScore = 0.0;
				} else {
					UCBScore = UCBSearchValue(playouts, board, i);
				}
			} else {
				UCBScore = 0.45 + Math.random() * 0.1;
			}
			if (UCBScore > bestScore) {
				bestScore = UCBScore;
				bestIndex = i;
			}
		}
		if (bestIndex == -1) {
			this.exhausted = true;
			return -2;
		}

		if (children.get(bestIndex).getPlayouts() == 0) {
			win = children.get(bestIndex).playout(board);
			if (this.color == win) {
				winRate = (playouts * winRate + 1.0) / (playouts + 1.0);
				lastWin = 1.0;
			} else if (win == -1) {
				winRate = (playouts * winRate + 0.65) / (playouts + 1.0);
				lastWin = 0.5;
			} else {
				winRate = (playouts * winRate + 0.0) / (playouts + 1.0);
				lastWin = 0.0;
			}
			this.playouts++;
			return win;
		} else {
			win = children.get(bestIndex).traverseNode(board);
			if (win == -2) {
				win = this.traverseNode(board);
			}
			if (this.color == win) {
				winRate = (playouts * winRate + 1.0) / (playouts + 1.0);
				lastWin = 1.0;
			} else if (win == -1) {
				winRate = (playouts * winRate + 0.65) / (playouts + 1.0);
				lastWin = 0.5;
			} else {
				winRate = (playouts * winRate + 0.0) / (playouts + 1.0);
				lastWin = 0.0;
			}
			this.playouts++;
			return win;
		}
	}
	
	public String toString() {
		return toString(0);
	}
	
	public String toString(int height) {
		String s = "";
		for (int i = 0; i < height; i++) {
			s += "   ";
		}
		s += "Move: " + Board.indexToString(this.getMove()) + " Color: "
				+ Board.colorToString(this.getColor()) + " Playouts: "
				+ this.getPlayouts() + " Winrate: " + this.getWinRate();
		if (finalNode) {
			s += " Final Node.";
		}
		s += "\n";
		if (this.children.size() != 0) {
			for (SearchNode child : children) {
				if (child.getPlayouts() != 0) {
					s += child.toString(height + 1);
				}
			}
		}
		return s;
	}
	
	protected double UCBSearchValue(int totalPlayouts,
			Board board, int childIdx) {
		// The variable names here are chosen for consistency with the tech
		// report
		double barX = children.get(childIdx).getWinRate();
		double logParentRunCount = Math.log(totalPlayouts);
		// In the paper, term1 is the mean of the SQUARES of the rewards; since
		// all rewards are 0 or 1 here, this is equivalent to the mean of the
		// rewards, i.e., the win rate.
		double term1 = barX;
		double term2 = -(barX * barX);
		double term3 = Math.sqrt(2 * logParentRunCount / children.get(childIdx).getPlayouts());
		double v = term1 + term2 + term3; // This equation is above Eq. 1
		assert v >= 0 : "Negative variability in UCT for move "
				+ children.get(childIdx).getMove() + ":\nNode: " + childIdx + "\nterm1: " + term1
				+ "\nterm2: " + term2 + "\nterm3: " + term3
				+ "\nPlayer's board:\n" + board;
		double factor1 = logParentRunCount / children.get(childIdx).getPlayouts();
		double factor2 = Math.min(0.25, v);
		double uncertainty = 0.4 * Math.sqrt(factor1 * factor2);
		return uncertainty + barX;
	}
}
