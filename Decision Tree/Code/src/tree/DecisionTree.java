package tree;

import java.util.Scanner;
import java.io.IOException;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

import id3.ID3;
import table.Table;
import support.Test;
import support.Node;
import support.Rule;
import support.Print;
import db.dbConnection;

public class DecisionTree {

	// Select Dataset

	public int selectDataset() {

		Scanner sc = new Scanner(System.in);
		System.out.println("Select Dataset \n1. Iris \n2. Tennis\n");
		int option = sc.nextInt();
		sc.close();

		return option;

	}

	// DB Related Function

	public void createTable(int option, String trainingFile, String tableName)
			throws IOException, SQLException, ClassNotFoundException {

		String inputData;
		FileInputStream fstream;
		BufferedReader bufferedReader;

		dbConnection db = new dbConnection();

		if (option == 1)
			db.createTableIris(tableName);
		else
			db.createTableTennis(tableName);

		fstream = new FileInputStream(System.getProperty("user.dir") + "\\resource\\" + trainingFile);
		bufferedReader = new BufferedReader(new InputStreamReader(fstream));

		while ((bufferedReader.readLine()) != null) {
			inputData = bufferedReader.readLine().trim();
			String[] rowValues = inputData.split(" ");
			db.insertRow(rowValues, tableName);
		}

		bufferedReader.close();
	}

	// Main Function

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		String tableName;
		String trainingFile;

		Node treeNode = new Node();
		dbConnection db = new dbConnection();
		DecisionTree decisionTree = new DecisionTree();

		// Select DataSet

		int option = decisionTree.selectDataset();

		if (option == 1) {
			tableName = "Iris";
			trainingFile = "IrisData";
		} else if (option == 2) {
			tableName = "Tennis";
			trainingFile = "TennisData";
		} else {
			System.out.println("Enter correct option, and Try again");
			return;
		}

		// Create Table and Insert Data
		decisionTree.createTable(option, trainingFile, tableName);

		// Convert Table To Facilitate Out ID3 Algorithm

		Table table = new Table(tableName);
		table.findAverageColumnValues(tableName);
		Map<String, String> columnPairNames = table.addColumns(tableName);

		// Initialize ID3 Parameters

		Map<Integer, String> indexAttributePair = table.makeIndexAttributePair(tableName);
		Map<String, Integer> attributeIndexPair = table.makeAttributeIndexPair(tableName);
		List<List<String>> dataSample = db.trainingExamples(tableName);
		List<String> attributeNames = db.columnNames(tableName);
		// Collections.shuffle(dataSample);

		int compareIndex = attributeNames.size();
		String className = attributeNames.get(compareIndex - 1);
		String[] classValues = db.distinctColumnValuesMethod(className, tableName);
		attributeNames.remove(className);

		// Initialize Dataset Parameters

		double trainingDataProbability = 0.6;
		int totalDataCount = dataSample.size();
		double trainingDataCount = Math.round(totalDataCount * trainingDataProbability);
		double testDataCount = Math.round(totalDataCount * 0.5 * (1 - trainingDataProbability));
		double validationDataCount = Math.round(totalDataCount * 0.5 * (1 - trainingDataProbability));

		List<List<String>> trainingData = table.makeData(dataSample, 0, (int) trainingDataCount);
		List<List<String>> testData = table.makeData(dataSample, (int) (trainingDataCount + validationDataCount),
				(int) totalDataCount);
		List<List<String>> validationData = table.makeData(dataSample, (int) trainingDataCount,
				(int) (totalDataCount - testDataCount));

		// ID3 Algorithm

		ID3 id3 = new ID3(compareIndex, tableName, className, trainingData);
		treeNode = id3.ID3Algorithm(trainingData, attributeNames, attributeIndexPair);

		// Initialize Rules Related Parameters

		Rule rule = new Rule();
		Test test = new Test();
		List<String> classValuesList = Arrays.asList(classValues);
		Print print = new Print(columnPairNames, classValuesList, treeNode);

		int leafCount = print.numberOfLeaves(treeNode, 0);
		List<Rule>[] rules = new ArrayList[leafCount];
		List<String>[] ruleList = new ArrayList[leafCount];

		// Print Decision Tree Structure

		System.out.println("\nTree Structure\n");
		print.printTree(treeNode, null);

		// Build Rules of Decision Tree

		ruleList = rule.buildRuleSet(ruleList, treeNode);
		rules = rule.ruleConversion(rules, ruleList, leafCount);

		// Print Rules of Decision Tree
		System.out.println("\nRule Set\n");
		print.printRules(rules);

		// Test Accuracy of Decision Tree

		double accuracy = test.testAccuracy(indexAttributePair, rules, validationData, tableName);
		System.out.println("\nPre Pruning : Validation Set Accuracy : " + accuracy + "%");

		accuracy = test.testAccuracy(indexAttributePair, rules, testData, tableName);
		System.out.println("\nPre Pruning : Test Set Accuracy : " + accuracy + "%");

		// Prune Decision Tree
		rules = test.pruneTree(indexAttributePair, rules, accuracy, validationData, tableName);
		System.out.println("\nPruning Rule Set\n");
		print.printRules(rules);

		accuracy = test.testAccuracy(indexAttributePair, rules, validationData, tableName);
		System.out.println("\nPost Pruning : Validation Set Accuracy : " + accuracy + "%");

		accuracy = test.testAccuracy(indexAttributePair, rules, testData, tableName);
		System.out.println("\nPost Pruning : Test Set Accuracy : " + accuracy + "%");

		// Delete Table Created

		db.deleteTable(tableName);

	}

}
