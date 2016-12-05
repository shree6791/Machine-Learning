package game;

import java.io.IOException;

public class Game {

	// input a move, output win, lose, tie, nothing (not end of game)

	Player play;
	Learner learn;

	double playerWins;
	double computerWins;

	int testGamesCount;
	int maximumBoardStates;

	public void boardSetup() {

		System.out.println("Board index position are as below : " + "\n");
		System.out.println(" 0 | 1 | 2 " + "\n" + " 3 | 4 | 5 " + "\n" + " 6 | 7 | 8 " + "\n");
		System.out.println("Player uses X ");
		System.out.println("Computer uses O " + "\n");

	}

	public Game() throws IOException {

		play = new Player();
		learn = new Learner();

		playerWins = 0;
		computerWins = 0;
		testGamesCount = 5;
		maximumBoardStates = learn.maximumBoardStates;

	}

	// Play Game Using Knowledge Gained During Training
	
	public void playGame(double[] weight) throws IOException {

		int win = 0;
		char currentMove = learn.selectTurn();
		String currentBoardState = learn.initialBoardState;

		for (int j = 0; j < maximumBoardStates; j++) {

			if (currentMove == 'X') {

				currentBoardState = play.selectBestMove(currentBoardState, currentMove, weight);
				win = learn.evaluateWinCount(currentBoardState, currentMove);

				if (win > 0) {
					playerWins++;
					break;
				} else
					currentMove = 'O';

			} else if (currentMove == 'O') {

				currentBoardState = play.selectBestMove(currentBoardState, currentMove, weight);
				win = learn.evaluateWinCount(currentBoardState, currentMove);

				if (win > 0) {
					computerWins++;
					break;
				} else
					currentMove = 'X';
			}
		}

	}

	// Evaluate Performance of LMS Algorithm Implemented
	
	public void performanceEvaluation(double[] weight) throws IOException {

		for (int i = 0; i < testGamesCount; i++)
			playGame(weight);

		double winPercent = playerWins / testGamesCount * 100;
		double lossPercent = computerWins / testGamesCount * 100;
		double drawPercent = (100 - winPercent - lossPercent);

		System.out.println("Player Win Percentage : " + winPercent);
		System.out.println("Computer Win Percentage : " + lossPercent);
		System.out.println(" Draw Percentage : " + drawPercent);

	}

	// Main Function
	
	public static void main(String[] args) throws IOException {

		Game game = new Game();
		Experience exp = new Experience();

		int mode = exp.selectMode();
		double[] weight = exp.startTraining(mode);

		if (weight == null) {
			System.out.println("Try again, with right option");
			return;
		}

		// Display Board Setup
		game.boardSetup();

		// Performance Evaluation
		game.performanceEvaluation(weight);

		// Display Weights Used for Performance Evaluation
		exp.displayWeights(weight);

	}

}
