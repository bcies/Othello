package othello_main;

import java.util.ArrayList;

public class CudaTree extends SearchTree{
	
	public CudaTree() {
		children = new ArrayList<SearchNode>();
		totalPlayouts = 0;
	}
	
	public void createRootNodes(Board board) {
		for(int i : board.getLegalMoves()) {
			children.add(new CudaNode(i, board.getColorToPlay()));
		}
	}
	
	public void expandTree(Board board, int blocks, int threads) {
		Board tempBoard = new Board();
		tempBoard.copyBoard(board);
		double bestScore = 0;
		int bestIndex = 0;
		double UCBScore;
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).isExhausted()) {
				UCBScore = -2;
			} else if (children.get(i).getPlayouts() != 0) {
				if (children.get(i).isFinalNode()) {
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
			CudaNode node = (CudaNode) children.get(bestIndex);
			node.playout(tempBoard, blocks, threads);
			totalPlayouts += blocks * threads;
		} else {
			CudaNode node = (CudaNode) children.get(bestIndex);
			node.traverseNode(tempBoard, blocks, threads);
		}
	}
}
