package othello_main;

import java.util.ArrayList;

public class Player {
	
	private double timePerMove;
	private int turnPlayouts;
	
	public Player(double timePerMove) {
		this.timePerMove = timePerMove;
		turnPlayouts = 0;
	}
	
	public int getBestMove(Board board, boolean showTree) {
		turnPlayouts = 0;
		SearchTree tree = new SearchTree();
		tree.createRootNodes(board);
		long currentTime = System.nanoTime();
		long finishTime = (long) (timePerMove * 1000000000) + currentTime;
		while (currentTime < finishTime) {
			tree.expandTree(board);
			turnPlayouts++;
			currentTime = System.nanoTime();
		}
		ArrayList<SearchNode> nodes = tree.getNodes();
		int bestNodeIndex = 0;
		for (int i = 1; i < nodes.size(); i++) {
			if (nodes.get(i).getWinRate() > nodes.get(bestNodeIndex)
					.getWinRate()) {
				bestNodeIndex = i;
			}
		}
		if (showTree) {
			System.out.println(tree);
		}
		return nodes.get(bestNodeIndex).getMove();
	}
	
	public int getPlayouts() {
		return turnPlayouts;
	}
	
	public boolean setTimePerMove(double time) {
		if(time <= 0) {
			return false;
		}
		timePerMove = time;
		return true;
	}
}
