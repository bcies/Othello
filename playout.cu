extern "C" __global__ void playout(int *rands, int *numRands, int *board,
		int *boardWidth, int *colorToPlay, float *wins) {

	int l_width = *boardWidth;

	//NOTE!!!!! hardcoded!!! change if you can.....
	int tempBoard[8 * 8];
	for (int j = 0; j < l_width * l_width; j++) {
		tempBoard[j] = board[j];
	}

	int colorTP = *colorToPlay;
	int end = false;
	int black = 0;
	int white = 0;
	int count = 0;

	while (!end) {
		int n = rands[(blockIdx.x * (*numRands / blockDim.x) + count
				+ threadIdx.x) % *numRands];
		count += 1;

		//Check if n is a legal move
		//TODO


		if (tempBoard[n] == 0) {
			//play
			tempBoard[n] = colorTP;

			//capture
			int x = n % l_width;
			int y = n / l_width;
			int newX;
			int newY;
			int nextSpace;
			int captureFlag = false;
			int opposite;
			int i;
			if (colorTP == 1) {
				opposite = 2;
			} else {
				opposite = 1;
			}
			int numCapture = 0;

			//up
			if ((y < 6) && (board[x + (y + 1) * l_width] == opposite)) {
				newY = y + 2;
				numCapture += 1;
				while (newY <= 7) {
					nextSpace = board[x + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newY += 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x + (y + i) * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// UpR
			if ((x < 6) && (y < 6)
					&& (board[x + 1 + (y + 1) * l_width] == opposite)) {
				numCapture += 1;
				newX = x + 2;
				newY = y + 2;
				while ((newX <= 7) && (newY <= 7)) {
					nextSpace = board[newX + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX += 1;
					newY += 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x + i + (y + i) * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// R
			if ((x < 6) && (board[x + 1 + y * l_width] == opposite)) {
				numCapture += 1;
				newX = x + 2;
				while (newX <= 7) {
					nextSpace = board[newX + y * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX += 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x + 1 + y * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// DoR
			if ((x < 6) && (y > 1)
					&& (board[x + 1 + (y - 1) * l_width] == opposite)) {
				numCapture += 1;
				newX = x + 2;
				newY = y - 2;
				while ((newX <= 7) && (newY >= 0)) {
					nextSpace = board[newX + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX += 1;
					newY -= 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x + i + (y - i) * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// Do
			if ((y > 1) && (board[x + (y - 1) * l_width] == opposite)) {
				numCapture += 1;
				newY = y - 2;
				while (newY >= 0) {
					nextSpace = board[x + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newY -= 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x + (y - i) * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// DoL
			if ((x > 1) && (y > 1)
					&& (board[x - 1 + (y - 1) * l_width] == opposite)) {
				numCapture += 1;
				newX = x - 2;
				newY = y - 2;
				while ((newX >= 0) && (newY >= 0)) {
					nextSpace = board[newX + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX -= 1;
					newY -= 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x - i + (y - i) * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// L
			if ((x > 1) && (board[x - 1 + y * l_width] == opposite)) {
				numCapture += 1;
				newX = x - 2;
				while (newX >= 0) {
					nextSpace = board[newX + y * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX -= 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x - i + y * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// UpL
			if ((x > 1) && (y < 6)
					&& (board[x - 1 + (y + 1) * l_width] == opposite)) {
				numCapture += 1;
				newX = x - 2;
				newY = y + 2;
				while ((newX >= 0) && (newY <= 7)) {
					nextSpace = board[newX + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX -= 1;
					newY += 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x - i + (y + i) * l_width] = colorTP;
			}

			//check end
			end = true;
			black = 0;
			white = 0;
			for (i = 0; i < l_width * l_width; i++) {
				if (tempBoard[i] == 0) {
					end = false;
					break;
				} else if (tempBoard[i] == 1) {
					black += 1;
				} else {
					white += 1;
				}
			}

		}
		if (colorTP == 1){
			colorTP =2;
		} else {
			colorTP = 1;
		}
	}
	black = 0;
	white = 0;
	for (int i = 0; i < l_width *l_width; i++){
		if (tempBoard[i] == 1){
			black += 1;
		} else if (tempBoard[i] == 2) {
			white += 1;
		}
	}
	int win = 0;
	if (black > white) {
		win = 1;
	} else if (white > black) {
		win = 2;
	}

	if (win == *colorToPlay) {
		atomicAdd(wins, (float) 1.0);
	} else if (win == 0) {
		atomicAdd(wins, (float) 0.5);
	}
}
extern "C"
__global__ void legalPlayout(int *rands, int *numRands, int *board,
		int *boardWidth, int *colorToPlay, float *wins) {

	int l_width = *boardWidth;

	//NOTE!!!!! hardcoded!!! change if you can.....
	int tempBoard[8 * 8];
	for (int j = 0; j < l_width * l_width; j++) {
		tempBoard[j] = board[j];
	}

	int colorTP = *colorToPlay;
	int end = false;
	int black = 0;
	int white = 0;
	int count = 0;

	int maxCount = *numRands / gridDim.x;

	while (count < (maxCount * 2) && !end) {
		int n = rands[(blockIdx.x * maxCount + count
				+ threadIdx.x) % *numRands];
		count += 1;

		//Check if n is a legal move
		//TODO
		int x = n % l_width;
		int y = n / l_width;
		int newX;
		int newY;
		int nextSpace;
		int captureFlag = false;
		int opposite;
		int i;
		if (colorTP == 1) {
			opposite = 2;
		} else {
			opposite = 1;
		}
		int legalMove = false;

		if (tempBoard[n] == 0) {
			// Up
			if ((y < 6) && (board[x + (y + 1) * l_width] == opposite)) {
				newY = y + 2;
				while (newY <= 7) {
					nextSpace = board[x + newY * l_width];
					if (nextSpace == colorTP) {
						legalMove = true;
						break;
					} else if (nextSpace == 0) {
						break;
					}
					newY += 1;
				}
			}
			//UpR
			if (!legalMove) {
				if ((x < 6) && (y < 6)
						&& (board[x + 1 + (y + 1) * l_width] == opposite)) {
					newX = x + 2;
					newY = y + 2;
					while ((newX <= 7) && (newY <= 7)) {
						nextSpace = board[newX + newY * l_width];
						if (nextSpace == colorTP) {
							legalMove = true;
							break;
						} else if (nextSpace == 0) {
							break;
						}
						newX += 1;
						;
						newY += 1;
					}
				}
			}
			//R
			if (!legalMove) {
				if ((x < 6) && (board[x + 1 + y * l_width] == opposite)) {
					newX = x + 2;
					while (newX <= 7) {
						nextSpace = board[newX + y * l_width];
						if (nextSpace == colorTP) {
							legalMove = true;
							break;
						} else if (nextSpace == 0) {
							break;
						}
						newX += 1;
					}
				}
			}
			//DoR
			if (!legalMove) {
				if ((x < 6) && (y > 1)
						&& (board[x + 1 + (y - 1) * l_width] == opposite)) {
					newX = x + 2;
					newY = y - 2;
					while ((newX <= 7) && (newY >= 0)) {
						nextSpace = board[newX + newY * l_width];
						if (nextSpace == colorTP) {
							legalMove = true;
							break;
						} else if (nextSpace == 0) {
							break;
						}
						newX += 1;
						newY -= 1;
					}
				}
			}
			//Do
			if (!legalMove) {
				if ((y > 1) && (board[x + (y - 1) * l_width] == opposite)) {
					newY = y - 2;
					while (newY >= 0) {
						nextSpace = board[x + newY * l_width];
						if (nextSpace == colorTP) {
							legalMove = true;
							break;
						} else if (nextSpace == 0) {
							break;
						}
						newY -= 1;
					}
				}
			}
			//DoL
			if (!legalMove) {
				if ((x > 1) && (y > 1)
						&& (board[x - 1 + (y - 1) * l_width] == opposite)) {
					newX = x - 2;
					newY = y - 2;
					while ((newX >= 0) && (newY >= 0)) {
						nextSpace = board[newX + newY * l_width];
						if (nextSpace == colorTP) {
							legalMove = true;
							break;
						} else if (nextSpace == 0) {
							break;
						}
						newX -= 1;
						newY -= 1;
					}
				}
			}
			//L
			if (!legalMove) {
				if ((x > 1) && (board[x - 1 + y * l_width] == opposite)) {
					newX = x - 2;
					while (newX >= 0) {
						nextSpace = board[newX + y * l_width];
						if (nextSpace == colorTP) {
							legalMove = true;
							break;
						} else if (nextSpace == 0) {
							break;
						}
						newX -= 1;
					}
				}
			}
			//UpL
			if (!legalMove) {
				if ((x > 1) && (y < 6)
						&& (board[x - 1 + (y + 1) * l_width] == opposite)) {
					newX = x - 2;
					newY = y + 2;
					while ((newX >= 0) && (newY <= 7)) {
						nextSpace = board[newX + newY * l_width];
						if (nextSpace == colorTP) {
							legalMove = true;
							break;
						} else if (nextSpace == 0) {
							break;
						}
						newX -= 1;
						newY += 1;
					}
				}
			}
		}

		if (legalMove) {
			//play
			tempBoard[n] = colorTP;

			//capture

			int numCapture = 0;

			//up
			if ((y < 6) && (board[x + (y + 1) * l_width] == opposite)) {
				newY = y + 2;
				numCapture += 1;
				while (newY <= 7) {
					nextSpace = board[x + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newY += 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x + (y + i) * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// UpR
			if ((x < 6) && (y < 6)
					&& (board[x + 1 + (y + 1) * l_width] == opposite)) {
				numCapture += 1;
				newX = x + 2;
				newY = y + 2;
				while ((newX <= 7) && (newY <= 7)) {
					nextSpace = board[newX + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX += 1;
					newY += 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x + i + (y + i) * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// R
			if ((x < 6) && (board[x + 1 + y * l_width] == opposite)) {
				numCapture += 1;
				newX = x + 2;
				while (newX <= 7) {
					nextSpace = board[newX + y * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX += 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x + 1 + y * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// DoR
			if ((x < 6) && (y > 1)
					&& (board[x + 1 + (y - 1) * l_width] == opposite)) {
				numCapture += 1;
				newX = x + 2;
				newY = y - 2;
				while ((newX <= 7) && (newY >= 0)) {
					nextSpace = board[newX + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX += 1;
					newY -= 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x + i + (y - i) * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// Do
			if ((y > 1) && (board[x + (y - 1) * l_width] == opposite)) {
				numCapture += 1;
				newY = y - 2;
				while (newY >= 0) {
					nextSpace = board[x + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newY -= 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x + (y - i) * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// DoL
			if ((x > 1) && (y > 1)
					&& (board[x - 1 + (y - 1) * l_width] == opposite)) {
				numCapture += 1;
				newX = x - 2;
				newY = y - 2;
				while ((newX >= 0) && (newY >= 0)) {
					nextSpace = board[newX + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX -= 1;
					newY -= 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x - i + (y - i) * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// L
			if ((x > 1) && (board[x - 1 + y * l_width] == opposite)) {
				numCapture += 1;
				newX = x - 2;
				while (newX >= 0) {
					nextSpace = board[newX + y * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX -= 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x - i + y * l_width] = colorTP;
			}
			numCapture = 0;
			captureFlag = false;

			// UpL
			if ((x > 1) && (y < 6)
					&& (board[x - 1 + (y + 1) * l_width] == opposite)) {
				numCapture += 1;
				newX = x - 2;
				newY = y + 2;
				while ((newX >= 0) && (newY <= 7)) {
					nextSpace = board[newX + newY * l_width];
					if (nextSpace == colorTP) {
						captureFlag = true;
						break;
					} else if (nextSpace == 0) {
						break;
					} else {
						numCapture += 1;
					}
					newX -= 1;
					newY += 1;
				}
			}
			if (!captureFlag) {
				numCapture = 0;
			}
			for (i = 1; i < numCapture + 1; i++) {
				board[x - i + (y + i) * l_width] = colorTP;
			}

			//check end
			end = true;
			for (i = 0; i < l_width * l_width; i++) {
				if (tempBoard[i] == 0) {
					end = false;
					break;
				}
			}

		}
		if (colorTP == 1) {
			colorTP = 2;
		} else {
			colorTP = 1;
		}
	}
	black = 0;
	white = 0;
	for (int i = 0; i < l_width * l_width; i++) {
		if (tempBoard[i] == 1) {
			black += 1;
		} else if (tempBoard[i] == 2) {
			white += 1;
		}
	}
	int win = 0;
	if (black > white) {
		win = 1;
	} else if (white > black) {
		win = 2;
	}

	if (win == *colorToPlay) {
		atomicAdd(wins, (float) 1.0);
	} else if (win == 0) {
		atomicAdd(wins, (float) 0.5);
	}
}

