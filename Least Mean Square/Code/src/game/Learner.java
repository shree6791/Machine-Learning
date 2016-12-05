package game;

import java.util.List;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Learner {

	// input experience, output knowledge (weight values)

	double[] weight;

	int attributeCount = 6;
	int maximumBoardStates = 9;
	double learningFactor = 0.01;
	int numberOfTrainingGames = 20;

	Random random;
	String initialBoardState;

	List<String>[] games;
	List<String> playerBoardStates;

	// Initialize Variables

	public void initializeWeight() {

		double maximum = 0.5;
		double minimum = -0.5;

		for (int i = 0; i <= attributeCount; i++)
			weight[i] = Math.random() * (maximum - minimum) + minimum;

	}

	@SuppressWarnings("unchecked")
	public Learner() throws IOException {

		games = new ArrayList[findNumberOfGames()];
		playerBoardStates = new ArrayList<String>();

		random = new Random();
		initialBoardState = "012345678";
		weight = new double[attributeCount + 1];

	}

	// File I/O Functions

	public int findNumberOfGames() throws IOException {

		FileInputStream fstream;
		numberOfTrainingGames = 0;
		BufferedReader bufferedReader;

		String teacherModeFile = "TeacherModeGames";

		fstream = new FileInputStream(System.getProperty("user.dir") + "\\resource\\" + teacherModeFile);
		bufferedReader = new BufferedReader(new InputStreamReader(fstream));

		while ((bufferedReader.readLine()) != null)
			numberOfTrainingGames++;

		bufferedReader.close();
		return numberOfTrainingGames / 2;

	}

	public void readGamesFromFile() throws IOException {

		int gameNumber = 0;
		FileInputStream fstream;
		BufferedReader bufferedReader;
		String teacherModeFile = "TeacherModeGames";

		fstream = new FileInputStream(System.getProperty("user.dir") + "\\resource\\" + teacherModeFile);
		bufferedReader = new BufferedReader(new InputStreamReader(fstream));

		while ((bufferedReader.readLine()) != null) {

			String boardVaues = bufferedReader.readLine().trim();
			games[gameNumber] = new ArrayList<String>();
			games[gameNumber].addAll(Arrays.asList(boardVaues.split(" ")));
			gameNumber++;

		}

		bufferedReader.close();
	}

	// No Teacher Mode Function

	@SuppressWarnings("unchecked")
	public void learnWithoutTeacher() {

		int win = 0;
		String currentBoard = null;
		games = new ArrayList[numberOfTrainingGames];

		for (int i = 0; i < numberOfTrainingGames; i++) {

			char currentMove = selectTurn();
			games[i] = new ArrayList<String>();

			for (int j = 0; j < maximumBoardStates; j++) {

				if (currentMove == 'X') {

					addBoardState(currentMove, j, i);
					currentBoard = games[i].get(j);
					win = evaluateWinCount(currentBoard, currentMove);

					if (win > 0)
						break;
					else
						currentMove = 'O';

				} else if (currentMove == 'O') {

					addBoardState(currentMove, j, i);
					currentBoard = games[i].get(j);
					win = evaluateWinCount(currentBoard, currentMove);

					if (win > 0)
						break;
					else
						currentMove = 'X';
				}

			}
		}

		// Find Quality of Games Used in Training

		for (int i = 0; i < numberOfTrainingGames; i++)
			evaluateGame(games[i]);

	}

	// Teacher Mode Function

	public void learnWithTeacher() throws IOException {

		readGamesFromFile();
		int size = games.length;

		// Find Quality of Games Used in Training
		for (int i = 0; i < size; i++)
			evaluateGame(games[i]);

	}

	// Board Evaluation Functions

	public void evaluateGame(List<String> boardStates) {

		playerBoardStates.clear();
		int boardStateCount = boardStates.size();
		String initialBoardState = boardStates.get(0);
		String lastBoardState = boardStates.get(boardStateCount - 1);

		int playerWinCount = evaluateWinCount(lastBoardState, 'X');
		int computerWinCount = evaluateWinCount(lastBoardState, 'O');
		int single_X_Count = evaluateSingleCount(initialBoardState, 'X');
		int single_O_Count = evaluateSingleCount(initialBoardState, 'O');

		if (single_X_Count == 1)
			for (int i = 0; i < boardStateCount; i = i + 2)
				playerBoardStates.add(boardStates.get(i));
		else if (single_O_Count == 1)
			for (int i = 1; i < boardStateCount; i = i + 2)
				playerBoardStates.add(boardStates.get(i));

		if (playerWinCount > 0)
			updateWeight(playerBoardStates, 100);
		else if (computerWinCount > 0)
			updateWeight(playerBoardStates, -100);
		else
			updateWeight(playerBoardStates, 0);

	}

	public int evaluateWinCount(String boardInputValuesString, char symbol) {

		// Calculate Features 5 and 6 : 3 Adjacent X or O

		int count = 0;

		if (boardInputValuesString.charAt(0) == symbol && boardInputValuesString.charAt(1) == symbol
				&& boardInputValuesString.charAt(2) == symbol)
			count++;

		if (boardInputValuesString.charAt(3) == symbol && boardInputValuesString.charAt(4) == symbol
				&& boardInputValuesString.charAt(5) == symbol)
			count++;

		if (boardInputValuesString.charAt(6) == symbol && boardInputValuesString.charAt(7) == symbol
				&& boardInputValuesString.charAt(8) == symbol)
			count++;

		if (boardInputValuesString.charAt(0) == symbol && boardInputValuesString.charAt(3) == symbol
				&& boardInputValuesString.charAt(6) == symbol)
			count++;

		if (boardInputValuesString.charAt(1) == symbol && boardInputValuesString.charAt(4) == symbol
				&& boardInputValuesString.charAt(7) == symbol)
			count++;

		if (boardInputValuesString.charAt(2) == symbol && boardInputValuesString.charAt(5) == symbol
				&& boardInputValuesString.charAt(8) == symbol)
			count++;

		if (boardInputValuesString.charAt(0) == symbol && boardInputValuesString.charAt(4) == symbol
				&& boardInputValuesString.charAt(8) == symbol)
			count++;

		if (boardInputValuesString.charAt(2) == symbol && boardInputValuesString.charAt(4) == symbol
				&& boardInputValuesString.charAt(6) == symbol)
			count++;

		return count;

	}

	public int evaluateTwoCount(String boardInputValuesString, char symbol) {

		// Calculate Features 3 and 4 : 2 Adjacent X or O

		int count = 0;

		if ((boardInputValuesString.charAt(0) == symbol && boardInputValuesString.charAt(1) == symbol)
				|| (boardInputValuesString.charAt(1) == symbol && boardInputValuesString.charAt(2) == symbol)
				|| (boardInputValuesString.charAt(0) == symbol && boardInputValuesString.charAt(2) == symbol))
			count++;

		if ((boardInputValuesString.charAt(3) == symbol && boardInputValuesString.charAt(4) == symbol)
				|| (boardInputValuesString.charAt(4) == symbol && boardInputValuesString.charAt(5) == symbol)
				|| (boardInputValuesString.charAt(3) == symbol && boardInputValuesString.charAt(5) == symbol))
			count++;

		if ((boardInputValuesString.charAt(6) == symbol && boardInputValuesString.charAt(7) == symbol)
				|| (boardInputValuesString.charAt(7) == symbol && boardInputValuesString.charAt(8) == symbol)
				|| (boardInputValuesString.charAt(6) == symbol && boardInputValuesString.charAt(8) == symbol))
			count++;

		if ((boardInputValuesString.charAt(0) == symbol && boardInputValuesString.charAt(3) == symbol)
				|| (boardInputValuesString.charAt(3) == symbol && boardInputValuesString.charAt(6) == symbol)
				|| (boardInputValuesString.charAt(0) == symbol && boardInputValuesString.charAt(6) == symbol))
			count++;

		if ((boardInputValuesString.charAt(1) == symbol && boardInputValuesString.charAt(4) == symbol)
				|| (boardInputValuesString.charAt(4) == symbol && boardInputValuesString.charAt(7) == symbol)
				|| (boardInputValuesString.charAt(1) == symbol && boardInputValuesString.charAt(7) == symbol))
			count++;

		if ((boardInputValuesString.charAt(2) == symbol && boardInputValuesString.charAt(5) == symbol)
				|| (boardInputValuesString.charAt(5) == symbol && boardInputValuesString.charAt(8) == symbol)
				|| (boardInputValuesString.charAt(2) == symbol && boardInputValuesString.charAt(8) == symbol))
			count++;

		if ((boardInputValuesString.charAt(0) == symbol && boardInputValuesString.charAt(4) == symbol)
				|| (boardInputValuesString.charAt(4) == symbol && boardInputValuesString.charAt(8) == symbol)
				|| (boardInputValuesString.charAt(0) == symbol && boardInputValuesString.charAt(8) == symbol))
			count++;

		if ((boardInputValuesString.charAt(2) == symbol && boardInputValuesString.charAt(4) == symbol)
				|| (boardInputValuesString.charAt(4) == symbol && boardInputValuesString.charAt(6) == symbol)
				|| (boardInputValuesString.charAt(2) == symbol && boardInputValuesString.charAt(6) == symbol))
			count++;

		return count;

	}

	public int evaluateSingleCount(String boardInputValuesString, char symbol) {

		// Calculate Features 1 and 2 : Count of X or O on Board

		int count = 0;

		for (int i = 0; i < boardInputValuesString.length(); i++)
			if (boardInputValuesString.charAt(i) == symbol)
				count++;

		return count;

	}

	public int[] evaluateAttributeCount(String currentBoardState, int[] attribute) {

		attribute[0] = 1;
		attribute[1] = evaluateSingleCount(currentBoardState, 'X');
		attribute[2] = evaluateSingleCount(currentBoardState, 'O');
		attribute[3] = evaluateTwoCount(currentBoardState, 'X');
		attribute[4] = evaluateTwoCount(currentBoardState, 'O');
		attribute[5] = evaluateWinCount(currentBoardState, 'X');
		attribute[6] = evaluateWinCount(currentBoardState, 'O');

		return attribute;

	}

	// Knowledge Update Function

	public void updateWeight(List<String> playerBoardStates, double vSuccessor) {

		double vpredicted = 0;
		int size = playerBoardStates.size() - 1;
		int[] attribute = new int[attributeCount + 1];

		for (int i = size; i > -1; i--) {

			attribute = evaluateAttributeCount(playerBoardStates.get(i), attribute);

			for (int j = 0; j <= attributeCount; j++)
				vpredicted += weight[j] * attribute[j];

			// LMS Algorithm

			for (int j = 0; j <= attributeCount; j++)
				weight[j] += learningFactor * (vSuccessor - vpredicted) * attribute[j];

			vSuccessor = vpredicted;

		}

	}

	// Helper Functions

	public char selectTurn() {

		char[] turn = { 'X', 'O' };
		int index = random.nextInt(turn.length);
		return turn[index];

	}

	public int selectRandomBoardPosition() {
		return random.nextInt(maximumBoardStates);
	}

	public boolean isEmpty(List<String> boardStates) {
		return boardStates.size() == 0;
	}

	public boolean isBoardPositionUsed(String boardState, int position) {

		char character = boardState.charAt(position);
		return Character.isDigit(character);

	}

	public void addBoardState(char currentMove, int boardNumber, int gameNumber) {

		boolean validBoardPosition = false;
		String previousBoardState = initialBoardState;
		int boardPosition = selectRandomBoardPosition();

		if (!isEmpty(games[gameNumber])) {
			while (!validBoardPosition) {
				boardPosition = selectRandomBoardPosition();
				previousBoardState = games[gameNumber].get(boardNumber - 1);
				validBoardPosition = isBoardPositionUsed(previousBoardState, boardPosition);
			}
		}

		char characterToReplace = previousBoardState.charAt(boardPosition);
		String currentBoardState = previousBoardState.replace(characterToReplace, currentMove);

		games[gameNumber].add(boardNumber, currentBoardState);

	}

}
