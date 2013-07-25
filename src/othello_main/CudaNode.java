package othello_main;

import static jcuda.driver.JCudaDriver.cuCtxCreate;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuDeviceGet;
import static jcuda.driver.JCudaDriver.cuInit;
import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuMemAlloc;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoH;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoD;
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import static jcuda.driver.JCudaDriver.cuModuleLoad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import jcuda.driver.CUmodule;
import jcuda.driver.JCudaDriver;
import jcuda.runtime.JCuda;

public class CudaNode extends SearchNode {

	/** Holds the playout function for leaf parallelism */
	protected static CUfunction function;

	/** Holds the playout function for multileaf parallelism */
	protected static CUfunction functionMultiLeaf;

	/**
	 * constructer for CudaNode
	 * 
	 * @param move
	 * @param color
	 */
	public CudaNode(int move, int color) {
		super(move, color);
	}

	/**
	 * create all of the legal children of the node
	 */
	public void createChildren(Board board) {
		for (int i : board.getLegalMoves()) {
			children.add(new CudaNode(i, board.getColorToPlay()));
		}
	}

	/**
	 * Does a number of random playouts using leaf parallelism
	 */
	public double playout(Board board, int blocks, int threads) {
		int blocksxthreads = blocks * threads;
		int formerPlayouts = playouts;
		playouts += blocksxthreads;
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

		// Get random numbers
		// Generate all open moves
		List<Integer> openMoves = new ArrayList<Integer>();
		List<Integer> newOpenMoves = new ArrayList<Integer>();
		for (int i = 0; i < Board.BOARD_AREA; i++) {
			if (board.getPoint(i) == Board.VACANT) {
				openMoves.add(i);
			}
		}

		// shuffle them
		int size = openMoves.size();
		int[] rand = new int[blocks * size];
		for (int j = 0; j < blocks; j++) {
			for (int i = 0; i < size; i++) {
				int randNumber = (int) (Math.random() * openMoves.size());
				newOpenMoves.add(openMoves.get(randNumber));
				rand[j * size + i] = openMoves.remove(randNumber);
			}
			openMoves.addAll(newOpenMoves);
			newOpenMoves.clear();
		}

		// Allocate the device input data, and copy the
		// host input data to the device
		CUdeviceptr d_rand = new CUdeviceptr();
		cuMemAlloc(d_rand, Sizeof.INT * rand.length);
		cuMemcpyHtoD(d_rand, Pointer.to(rand), Sizeof.INT * rand.length);

		int randNum[] = { rand.length };
		CUdeviceptr d_randNum = new CUdeviceptr();
		cuMemAlloc(d_randNum, Sizeof.INT);
		cuMemcpyHtoD(d_randNum, Pointer.to(randNum), Sizeof.INT);

		CUdeviceptr d_board = new CUdeviceptr();
		cuMemAlloc(d_board, Sizeof.INT * Board.BOARD_AREA);
		cuMemcpyHtoD(d_board, Pointer.to(board.getBoard()), Sizeof.INT
				* Board.BOARD_AREA);

		int boardWidth[] = { Board.BOARD_WIDTH };
		CUdeviceptr d_boardWidth = new CUdeviceptr();
		cuMemAlloc(d_boardWidth, Sizeof.INT);
		cuMemcpyHtoD(d_boardWidth, Pointer.to(boardWidth), Sizeof.INT);

		int color[] = { board.getColorToPlay() };
		CUdeviceptr d_color = new CUdeviceptr();
		cuMemAlloc(d_color, Sizeof.INT);
		cuMemcpyHtoD(d_color, Pointer.to(color), Sizeof.INT);

		// Allocate device output memory
		float wins[] = { (float) 0.0 };
		CUdeviceptr d_wins = new CUdeviceptr();
		cuMemAlloc(d_wins, Sizeof.FLOAT);
		cuMemcpyHtoD(d_wins, Pointer.to(wins), Sizeof.FLOAT);

		// Set up the kernel parameters: A pointer to an array
		// of pointers which point to the actual values.
		Pointer kernelParameters = Pointer.to(Pointer.to(d_rand),
				Pointer.to(d_randNum), Pointer.to(d_board),
				Pointer.to(d_boardWidth), Pointer.to(d_color),
				Pointer.to(d_wins));

		// Call the kernel function.
		cuLaunchKernel(function, blocks, 1, 1, threads, 1, 1, 0, null,
				kernelParameters, null);
		cuCtxSynchronize();

		cuMemcpyDtoH(Pointer.to(wins), d_wins, Sizeof.FLOAT);

		JCuda.cudaFree(d_rand);
		JCuda.cudaFree(d_randNum);
		JCuda.cudaFree(d_board);
		JCuda.cudaFree(d_boardWidth);
		JCuda.cudaFree(d_color);
		JCuda.cudaFree(d_wins);

		winRate = (formerPlayouts * winRate + wins[0]) / (playouts * 1.0);
		lastWin = wins[0] / (blocksxthreads);
		return wins[0];

	}

	/**
	 * Prepares the GPU functions
	 */
	public static void prepareGPU() {
		// Note the following CUDA code came from
		// http://www.jcuda.org/samples/samples.html
		// Enable exceptions and omit all subsequent error checks
		JCudaDriver.setExceptionsEnabled(true);

		// Create the PTX file by calling the NVCC
		String ptxFileName = "";
		try {
			ptxFileName = preparePtxFile("playout.cu");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Initialize the driver and create a context for the first device.
		cuInit(0);
		CUdevice device = new CUdevice();
		cuDeviceGet(device, 0);
		CUcontext context = new CUcontext();
		cuCtxCreate(context, 0, device);

		// Load the ptx file.
		CUmodule module = new CUmodule();
		cuModuleLoad(module, ptxFileName);

		// Obtain a function pointer to the "playout" function.
		function = new CUfunction();
		cuModuleGetFunction(function, module, "playout");

		// // Obtain a function pointer to the "playoutMultiLeaf" function.
		// functionMultiLeaf = new CUfunction();
		// cuModuleGetFunction(functionMultiLeaf, module, "playoutMultiLeaf");

	}

	/**
	 * The extension of the given file name is replaced with "ptx". If the file
	 * with the resulting name does not exist, it is compiled from the given
	 * file using NVCC. The name of the PTX file is returned.
	 * 
	 * source: http://www.jcuda.org/samples/samples.html
	 * 
	 * @param cuFileName
	 *            The name of the .CU file
	 * @return The name of the PTX file
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	private static String preparePtxFile(String cuFileName) throws IOException {
		int endIndex = cuFileName.lastIndexOf('.');
		if (endIndex == -1) {
			endIndex = cuFileName.length() - 1;
		}
		String ptxFileName = cuFileName.substring(0, endIndex + 1) + "ptx";
		File ptxFile = new File(ptxFileName);
		if (ptxFile.exists()) {
			return ptxFileName;
		}

		File cuFile = new File(cuFileName);
		if (!cuFile.exists()) {
			throw new IOException("Input file not found: " + cuFileName);
		}
		String modelString = "-m" + System.getProperty("sun.arch.data.model");
		String command = "nvcc -arch=sm_20 -v " + modelString + " -ptx "
				+ cuFile.getPath() + " -o " + ptxFileName;

		System.out.println("Executing\n" + command);
		Process process = Runtime.getRuntime().exec(command);

		String errorMessage = new String(toByteArray(process.getErrorStream()));
		String outputMessage = new String(toByteArray(process.getInputStream()));
		int exitValue = 0;
		try {
			exitValue = process.waitFor();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Interrupted while waiting for nvcc output",
					e);
		}

		if (exitValue != 0) {
			System.out.println("nvcc process exitValue " + exitValue);
			System.out.println("errorMessage:\n" + errorMessage);
			System.out.println("outputMessage:\n" + outputMessage);
			throw new IOException("Could not create .ptx file: " + errorMessage);
		}

		System.out.println("Finished creating PTX file");
		return ptxFileName;
	}

	/**
	 * Fully reads the given InputStream and returns it as a byte array
	 * 
	 * source: http://www.jcuda.org/samples/samples.html
	 * 
	 * @param inputStream
	 *            The input stream to read
	 * @return The byte array containing the data from the input stream
	 * @throws IOException
	 *             If an I/O error occurs
	 */
	private static byte[] toByteArray(InputStream inputStream)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buffer[] = new byte[8192];
		while (true) {
			int read = inputStream.read(buffer);
			if (read == -1) {
				break;
			}
			baos.write(buffer, 0, read);
		}
		return baos.toByteArray();
	}

	/**
	 * traverses a node to find a playout
	 * 
	 * @param board
	 * @param blocks
	 * @param threads
	 * @return the number of wins for the upper node
	 */
	public double traverseNode(Board board, int blocks, int threads) {
		board.play(this.move);
		double bestScore = -1;
		int bestIndex = -1;
		double wins;
		double UCBScore;
		int blocksxthreads = blocks * threads;
		if (this.playouts <= blocksxthreads) {
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
			CudaNode node = (CudaNode) children.get(bestIndex);
			wins = node.playout(board, blocks, threads);
			int formerPlayouts = playouts;
			playouts += blocksxthreads;
			winRate = (formerPlayouts * winRate + wins) / (playouts);
			lastWin = wins / blocksxthreads;
			return blocksxthreads - wins;
		} else {
			CudaNode node = (CudaNode) children.get(bestIndex);
			wins = node.traverseNode(board, blocks, threads);
			if (wins == -2) {
				wins = this.traverseNode(board, blocks, threads);
			}
			int formerPlayouts = playouts;
			playouts += blocksxthreads;
			winRate = (formerPlayouts * winRate + wins) / (playouts);
			lastWin = wins / blocksxthreads;
			return blocksxthreads - wins;
		}

	}

}
