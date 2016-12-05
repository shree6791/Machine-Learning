package game;

import java.io.IOException;

public class Player {

	// input knowledge and board, output a move

	Learner learn;
	int attributeCount;

	int[] attribute;
	int maxBoardStates;

	double boardQuality;
	double bestBoardQuality;
	String finalChosenBoardState;

	public Player() throws IOException {

		learn = new Learner();
		attributeCount = learn.attributeCount;

		attribute = new int[attributeCount + 1];
		maxBoardStates = learn.maximumBoardStates;

		boardQuality = 0;
		bestBoardQuality = Double.NEGATIVE_INFINITY;

	}

	// Select Best Move For Given Board State

	public String selectBestMove(String currentBoardState, char currentMove, double[] weight) throws IOException {

		for (int i = 0; i < maxBoardStates; i++) {

			boardQuality = 0;
			boolean validBoardPosition = learn.isBoardPositionUsed(currentBoardState, i);

			if (!validBoardPosition)
				continue;

			char characterToReplace = currentBoardState.charAt(i);
			String chosenBoardState = currentBoardState.replace(characterToReplace, currentMove);

			attribute = learn.evaluateAttributeCount(chosenBoardState, attribute);

			// Find Board Quality for Current Move
			for (int j = 0; j <= attributeCount; j++)
				boardQuality += weight[j] * attribute[j];

			if (boardQuality > bestBoardQuality) {
				bestBoardQuality = boardQuality;
				finalChosenBoardState = chosenBoardState;
			}

		}

		return finalChosenBoardState;

	}

	// Main Function

	public static void main(String args[]) throws IOException {

		// For Test Purposes

		Player player = new Player();
		Experience exp = new Experience();

		int mode = exp.selectMode();
		double[] weight = exp.startTraining(mode);

		String curBoard = "012OX5X7O";
		String newBoard = player.selectBestMove(curBoard, 'O', weight);
		System.out.println(newBoard);

	}

}
