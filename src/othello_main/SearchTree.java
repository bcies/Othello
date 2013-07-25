package othello_main;

import java.util.ArrayList;

public class SearchTree {

	ArrayList<SearchNode> children;
	private int totalPlayouts;
	
	public SearchTree() {
		children = new ArrayList<SearchNode>();
		totalPlayouts = 0;
	}
	
	public void createRootNodes(Board board) {
		for(int i : board.getLegalMoves()) {
			children.add(new SearchNode(i, board.getColorToPlay()));
		}
	}
	
	public void expandTree(Board board) {
		Board tempBoard = new Board();
		tempBoard.copyBoard(board);
		double bestScore = 0;
		int bestIndex = 0;
		double UCBScore;
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).getPlayouts() != 0) {
				if (children.get(i).isExhausted()) {
					UCBScore = -2;
				} else if (children.get(i).isFinalNode()) {
					UCBScore = 0;
				} else {
					UCBScore = UCBSearchValue(totalPlayouts, tempBoard, i);
				}
			} else {
				UCBScore = 0.45 + Math.random() * 0.1;
			}
			if (UCBScore > bestScore) {
				bestScore = UCBScore;
				bestIndex = i;
			}
		}

		if (children.get(bestIndex).getPlayouts() == 0) {
			children.get(bestIndex).playout(tempBoard);
			totalPlayouts++;
		} else {
			children.get(bestIndex).traverseNode(tempBoard);
		}
	}
	
	public ArrayList<SearchNode> getNodes() {
		return children;
	}
	
	
	
	@Override
	public String toString() {
		String s = "";
		for (SearchNode node : children) {
			if (node.getPlayouts() != 0) {
				s += node.toString(0);
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
