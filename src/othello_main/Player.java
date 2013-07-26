package othello_main;

import java.util.ArrayList;

public class Player {
	
	private double timePerMove;
	private long turnPlayouts;
	private int blocks;
	private int threads;
	private boolean legal;
	
	public Player(double timePerMove, boolean legal) {
		this.timePerMove = timePerMove;
		turnPlayouts = 0;
		blocks = 0;
		threads = 0;
		this.legal = legal;
	}
	
	public int getBestMove(Board board, boolean showTree) {
		int blocksxthreads = blocks * threads;
		turnPlayouts = 0;
		SearchTree tree = new SearchTree();
		long currentTime = System.nanoTime();
		long finishTime = (long) (timePerMove * 1000000000) + currentTime;
		
		if (blocks == 0){
			tree = new SearchTree();
			tree.createRootNodes(board);
			while (currentTime < finishTime) {
				tree.expandTree(board);
				turnPlayouts++;
				currentTime = System.nanoTime();
			}
		} else {
			CudaTree ctree = new CudaTree();
			ctree.createRootNodes(board);
			while (currentTime < finishTime){
				ctree.expandTree(board, blocks, threads, legal);
				turnPlayouts += blocksxthreads;
				currentTime = System.nanoTime();
			}
			tree = ctree;
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
	
	public long getPlayouts() {
		return turnPlayouts;
	}
	
	public void setCuda(int blocks, int threads){
		this.blocks = blocks;
		this.threads = threads;
	}
	
	public boolean setTimePerMove(double time) {
		if(time <= 0) {
			return false;
		}
		timePerMove = time;
		return true;
	}
}
