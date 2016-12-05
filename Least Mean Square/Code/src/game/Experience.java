package game;

import java.io.IOException;
import java.util.Scanner;

public class Experience {

	// select experience (teacher and no-teacher modes)

	// Select Who Should Start the Game

	@SuppressWarnings("resource")
	public int selectMode() {

		System.out.println("Select Mode \n 1. Teacher Mode \n 2. No Teacher Mode \n");
		Scanner sc = new Scanner(System.in);
		return sc.nextInt();

	}

	// Display Knowledge (Weights) Gained after Training

	public void displayWeights(double[] weight) {

		int size = weight.length;
		System.out.println("\nKnowledge Gained After Training \n");

		for (int i = 0; i < size; i++)
			System.out.println("weight[" + i + "] = " + weight[i]);

	}

	// Train the Computer to Play Tic Tac Toe

	public double[] startTraining(int mode) throws IOException {

		Learner learn = new Learner();
		learn.initializeWeight();

		if (mode == 1)
			learn.learnWithTeacher();
		else if (mode == 2)
			learn.learnWithoutTeacher();
		else
			return null;

		return learn.weight;

	}

}
