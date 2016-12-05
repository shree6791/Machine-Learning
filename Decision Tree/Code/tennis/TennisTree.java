package tennis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import db.dbConnection;

public class TennisTree {

	static int leafCount = 0;
	static int ruleNumber = 0;
	static int treeHeight = 0;
	static String tableName = "TrainTennisData";
	static String testTableName = "TestTennisData";
	static List<List<String>> trainingExampleListMain;

	double totalE;
	double accuracy, entropy;
	double[] pi, pE, informationGain;
	double columnCount, distinctColumnCount;
	double columnsToCompare, noCount, yesCount;
	double trainingDataSetRowCount, validationDataSetRowCount;

	String classNameYN, nodeName;
	String columnName, columnValue;
	String[] classValueYN, distinctColumnValues;

	List<String>[] ruleList;
	List<String> decisionTreeOutput;
	List<Double> informationGainList;
	Map<String, Double> bestAttribute;
	Map<Integer, String> attributeIndex;
	Map<String, Double> bestAttributeTemp;
	Map<Double, String> informationGainMap;

	dbConnection db;
	List<Rule>[] rules;
	List<String> visited = new LinkedList<String>();
	List<String> treeAttributes = new LinkedList<String>();
	List<String> columnNames, columnNamesTemp, nodeValuesList;

	// DB Related

	public void createTable(String tName) throws IOException, SQLException {

		String inputData;
		FileInputStream fstream;
		BufferedReader bufferedReader;
		String trainingFileTennis = null;

		if (tName.equals(tableName))
			trainingFileTennis = "TrainTennisData";
		else if (tName.equals(testTableName))
			trainingFileTennis = "TestTennisData";

		fstream = new FileInputStream(System.getProperty("user.dir") + "\\resource\\" + trainingFileTennis);
		bufferedReader = new BufferedReader(new InputStreamReader(fstream));

		while ((bufferedReader.readLine()) != null) {
			inputData = bufferedReader.readLine().trim();
			String[] rowValues = inputData.split(" ");
			db.insertRow(rowValues, tName);
		}

		bufferedReader.close();

	}

	@SuppressWarnings("unchecked")
	public TennisTree(String tableName) throws SQLException, ClassNotFoundException, IOException {

		db = new dbConnection();
		db.createTableTennis(tableName);
		createTable(tableName);

		columnNames = db.columnNames(tableName);
		columnCount = columnNames.size();

		classNameYN = db.columnNames(tableName).get((int) (columnCount - 1));
		classValueYN = db.distinctColumnValuesMethod(classNameYN, tableName);

		double trainingDataProbability = 0.7;
		int rowCount = (int) db.rowCount(tableName);
		trainingDataSetRowCount = Math.round(rowCount * trainingDataProbability);
		validationDataSetRowCount = Math.round(rowCount * (1 - trainingDataProbability));

		columnNames.remove(classNameYN);
		columnNamesTemp = columnNames;

		totalE = db.rowCount(tableName);
		nodeValuesList = new ArrayList<String>();
		decisionTreeOutput = new ArrayList<String>();
		bestAttribute = new HashMap<String, Double>();
		attributeIndex = new HashMap<Integer, String>();

	}

	// Entropy and Information Gain Related

	public double calculateEntropy(String tableName) throws ClassNotFoundException, SQLException {

		entropy = 0.0;
		distinctColumnValues = db.distinctColumnValuesMethod(classNameYN, tableName);
		distinctColumnCount = distinctColumnValues.length;
		pE = new double[(int) distinctColumnCount];

		for (int i = 0; i < distinctColumnCount; i++)
			pE[i] = db.count(distinctColumnValues[i], classNameYN, tableName);

		for (int i = 0; i < distinctColumnCount; i++)
			entropy += (-1 * pE[i] / totalE) * ((Math.log10(pE[i] / totalE)) / (Math.log10(2)));

		return entropy;
	}

	public Map<String, Double> calculateInformationAttributeGain(List<List<String>> trainingExampleList,
			String tableName) throws ClassNotFoundException, SQLException {

		dbConnection db = new dbConnection();

		columnCount = columnNames.size();
		columnsToCompare = columnNames.size();
		informationGainList = new ArrayList<Double>();
		informationGainMap = new HashMap<Double, String>();
		informationGain = new double[(int) columnsToCompare];
		Arrays.fill(informationGain, calculateEntropy(tableName));

		int i = 0;

		for (int a = 0; a < columnCount; a++)

			if ((nodeValuesList.contains(columnNames.get(a))))
				continue;
			else {

				for (; i < informationGain.length;) {

					int k = 0, m = 0;
					columnName = columnNames.get(a);
					int classValueLength = classValueYN.length;
					distinctColumnValues = db.distinctColumnValuesMethod(columnName, tableName);
					distinctColumnCount = distinctColumnValues.length;

					pi = new double[(int) (classValueLength * distinctColumnCount)];

					for (; k < pi.length; m++)
						for (int l = 0; l < classValueYN.length; l++, k++)
							for (int x = 0; x < trainingExampleList.size(); x++)
								if ((trainingExampleList.get(x).contains(distinctColumnValues[m]))
										&& ((trainingExampleList.get(x).contains(classValueYN[l]))))
									pi[k]++;

					for (k = 0; k < pi.length; k = k + 2) {

						double temp1 = (pi[k] + pi[k + 1]) / totalE;
						double temp2 = pi[k] / (pi[k] + pi[k + 1])
								* (Math.log10(pi[k] / (pi[k] + pi[k + 1])) / Math.log10(2));
						double temp3 = pi[k + 1] / (pi[k] + pi[k + 1])
								* (Math.log10(pi[k + 1] / (pi[k] + pi[k + 1])) / Math.log10(2));

						if (Double.isNaN(temp2))
							temp2 = 0;
						if (Double.isNaN(temp3))
							temp3 = 0;

						double temp4 = -1 * temp1 * (temp2 + temp3);
						informationGain[i] -= temp4;

					}

					informationGainList.add(informationGain[i]);
					informationGainMap.put(informationGain[i], columnName);
					i++;
					break;
				}
			}

		Arrays.fill(pi, 0.0);
		Collections.sort(informationGainList);
		Collections.reverse(informationGainList);
		nodeName = informationGainMap.get(informationGainList.get(0));
		columnNamesTemp.remove(nodeName);
		nodeValuesList.add(nodeName);

		bestAttribute.put(nodeName, informationGainList.get(0));
		return bestAttribute;

	}

	// Leaf Node Related

	public String mostCommonValue(List<List<String>> trainingExampleListTemp)
			throws SQLException, ClassNotFoundException {

		double count1 = 0.0, count2 = 0.0;
		int size = trainingExampleListTemp.size();

		for (int i = 0; i < size; i++)
			if (trainingExampleListTemp.get(i).contains(classValueYN[0]))
				count1++;
			else if (trainingExampleListTemp.get(i).contains(classValueYN[1]))
				count2++;

		if (count1 > count2)
			return classValueYN[0];
		else
			return classValueYN[1];

	}

	public String checkForLeafNode(List<List<String>> allClassValueSameCheck) throws SQLException {

		int total = 0;
		noCount = yesCount = 0.0;
		String leafValueCheck = null;
		int size = allClassValueSameCheck.size();
		int columnCount = allClassValueSameCheck.get(0).size();

		for (int i = 0; i < allClassValueSameCheck.size(); i++)
			if (allClassValueSameCheck.get(i).get(columnCount - 1).equals(classValueYN[0]))
				noCount += 1;
			else if (allClassValueSameCheck.get(i).get(columnCount - 1).equals(classValueYN[1]))
				yesCount += 1;

		total = (int) (yesCount + noCount);

		if (size == total)
			if (yesCount == 0)
				leafValueCheck = classValueYN[0];
			else if (noCount == 0)
				leafValueCheck = classValueYN[1];

		return leafValueCheck;

	}

	public Node addLeafNode(String leafValueTemp, boolean attributeEmpty) throws ClassNotFoundException, SQLException {

		Node leaf = new Node();

		if (leafValueTemp == null)
			if (noCount > yesCount)
				leaf.nodeName = classValueYN[0];
			else
				leaf.nodeName = classValueYN[1];
		else
			leaf.nodeName = leafValueTemp;

		return leaf;

	}

	// ID3 Algorithm

	public Node ID3Algorithm(List<List<String>> trainingExampleList, List<String> attributeNames)
			throws SQLException, ClassNotFoundException {

		Node node = new Node();
		String leafValue = null;
		boolean attributesIsEmpty = false;
		bestAttributeTemp = new HashMap<String, Double>();
		List<List<String>> trainingExampleListTemp = new ArrayList<List<String>>();

		// check if trainingExamples are all positive or negative, Attributes to
		// compare are empty, add leaf Node, if it exists

		leafValue = checkForLeafNode(trainingExampleList);

		if (attributeNames.isEmpty())
			attributesIsEmpty = true;

		if (leafValue != null || attributesIsEmpty == true) {

			Node newLeaf = addLeafNode(leafValue, attributesIsEmpty);
			newLeaf.children = new Node[0];
			return newLeaf;

		}

		bestAttributeTemp = calculateInformationAttributeGain(trainingExampleList, tableName);
		node.branchNames = db.distinctColumnValuesMethod(nodeName, tableName);
		node.informationGain = bestAttributeTemp.get(nodeName);
		int nodeBranchesLength = node.branchNames.length;
		node.children = new Node[nodeBranchesLength];
		node.nodeName = nodeName;

		for (int s = 0; s < nodeBranchesLength; s++) {

			// check if trainingExamples is empty

			boolean exampleListEmpty = false;

			if (trainingExampleList.isEmpty()) {

				Node nodeLeaf = new Node();
				nodeLeaf = addLeafNode(leafValue, attributesIsEmpty);
				nodeLeaf.nodeName = mostCommonValue(trainingExampleListTemp);
				node.children[s] = nodeLeaf;
				node.children[s].children = new Node[0];
				exampleListEmpty = true;

			} else {

				// drop values not needed for next iteration
				boolean addRow = true;
				List<List<String>> trainingExampleNewToCmpare = db.newTrainingExamples(node.branchNames[s],
						node.nodeName, tableName);

				for (int k = 0; k < trainingExampleNewToCmpare.size(); k++) {
					for (int l = 0; l < trainingExampleList.size(); l++) {
						if ((trainingExampleList.get(l).equals(trainingExampleNewToCmpare.get(k)))) {
							trainingExampleListTemp.add(trainingExampleList.get(l));
							break;
						} else
							addRow = false;

					}
					if (addRow = true)
						continue;
				}

				attributeNames = columnNamesTemp;
				node.children[s] = ID3Algorithm(trainingExampleListTemp, attributeNames);
			}
			trainingExampleListTemp.clear();
		}
		trainingExampleListTemp = trainingExampleListMain;
		return node;
	}

	// Print Functions

	public void printRules() {

		int size = ruleList.length;

		for (int i = 0; i < size; i++) {
			int end = ruleList[i].size() - 1;
			for (int j = 0; j < end - 1; j = j + 2) {
				System.out.print(ruleList[i].get(j) + " = ");
				System.out.print(ruleList[i].get(j + 1));
				if (j + 2 != end)
					System.out.print(" ^ ");
				else
					System.out.print(" => ");
			}
			System.out.println(ruleList[i].get(end));
		}

	}

	public void printTree(Node node, Node parent) {

		// Pre-Order Traversal

		for (int i = 0; i < node.children.length; i++)
			if (node.children[i] != null) {

				int height = heightOfTree(parent);
				int level = (treeHeight - height + 1) % treeHeight;

				if (parent == null)
					level = 0;

				for (int j = 0; j < level; j++)
					System.out.print("| ");

				if (node.children[i].nodeName.contains(classValueYN[0])
						|| node.children[i].nodeName.contains(classValueYN[1]))
					System.out.print(
							node.nodeName + " = " + node.branchNames[i] + " : " + node.children[i].nodeName + "\n");
				else
					System.out.println(node.nodeName + " = " + node.branchNames[i]);

				printTree(node.children[i], node);

			}
	}

	// Rule Functions

	public void ruleConversion() {

		for (int i = 0; i < leafCount; i++) {

			Rule r = new Rule();
			rules[i] = new ArrayList<>();
			int end = ruleList[i].size() - 1;

			for (int j = 0; j < end; j = j + 2)
				r.preCondition.put(ruleList[i].get(j), ruleList[i].get(j + 1));

			r.postCondition = ruleList[i].get(end);
			rules[i].add(r);

		}
	}

	public void buildRuleSet(Node node) {

		if (node.branchNames == null) {

			visited.add(node.nodeName);
			ruleList[ruleNumber] = new ArrayList<>();
			ruleList[ruleNumber].addAll(visited);
			visited.remove(node.nodeName);
			ruleNumber++;

		}

		for (int i = 0; i < node.children.length; i++) {
			visited.add(node.nodeName);
			visited.add(node.branchNames[i]);
			buildRuleSet(node.children[i]);
			visited.remove(node.nodeName);
			visited.remove(node.branchNames[i]);
		}

	}

	// Helper Functions

	public int heightOfTree(Node node) {

		if (node == null)
			return -1;
		else {
			int ht = 0;
			for (int i = 0; i < node.children.length - 1; i++)
				ht = 1 + Math.max(heightOfTree(node.children[i]), ht);
			return ht;
		}

	}

	public int numberOfLeaves(Node node) {

		if (node.branchNames == null)
			leafCount++;
		else
			for (int i = 0; i < node.children.length; i++)
				numberOfLeaves(node.children[i]);

		return leafCount;

	}

	public void treeAttributes(Node node) {

		if (node.branchNames == null)
			return;

		for (int i = 0; i < node.children.length; i++) {
			if (!treeAttributes.contains(node.nodeName))
				treeAttributes.add(node.nodeName);
			treeAttributes(node.children[i]);
		}

	}

	// Test Functions

	public void initializeAttributeIndex() throws SQLException {

		columnNames = db.columnNames(tableName);
		for (int i = 0; i < columnNames.size(); i++)
			attributeIndex.put(i, columnNames.get(i));

	}

	public double testAccuracy(String tName) throws SQLException {

		int correct = 0;
		boolean correctPrediction = false;
		int columnCount = db.columnNames(tName).size();
		int trainingExamplesCount = (int) db.rowCount(tName);
		List<List<String>> trainingExampleList = db.trainingExamples(tName);

		for (int i = 0; i < trainingExamplesCount; i++) {

			for (int j = 0; j < ruleList.length; j++) {

				for (int k = 0; k < columnCount; k++) {

					String currentColumn = attributeIndex.get(k);
					String currentColumnValue = trainingExampleList.get(i).get(k);
					String ruleValue = rules[j].get(j).preCondition.get(currentColumn);

					if (k == columnCount - 1) {
						ruleValue = rules[j].get(j).postCondition;
						if (ruleValue.equals(currentColumnValue))
							correctPrediction = true;
					} else if (ruleValue == null)
						correctPrediction = true;
					else if (ruleValue.equals(currentColumnValue))
						correctPrediction = true;
					else
						break;

				}

				if (correctPrediction == true) {
					correct++;
					break;
				}
			}
		}

		accuracy = correct / trainingExamplesCount * 100;
		return accuracy;

	}

	// Main Function

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		Node treeNode = new Node();
		dbConnection db = new dbConnection();
		TennisTree tennis = new TennisTree(tableName);
		List<String> attributeNames = db.columnNames(tableName);
		trainingExampleListMain = db.trainingExamples(tableName);
		treeNode = tennis.ID3Algorithm(trainingExampleListMain, attributeNames);

		System.out.println("Tree Structure\n");
		treeHeight = tennis.heightOfTree(treeNode);
		tennis.printTree(treeNode, null);

		tennis.numberOfLeaves(treeNode);
		tennis.ruleList = new ArrayList[leafCount];
		tennis.rules = new ArrayList[leafCount];

		System.out.println("\nRule Set\n");
		tennis.buildRuleSet(treeNode);
		tennis.printRules();

		tennis.ruleConversion();
		tennis.initializeAttributeIndex();
		double accuracy = tennis.testAccuracy(tableName);
		System.out.println("\nTraining Accuracy : " + accuracy + "%");

		db.createTableTennis(testTableName);
		tennis.createTable(testTableName);
		accuracy = tennis.testAccuracy(testTableName);
		System.out.println("\nTest Accuracy : " + accuracy + "%");

		db.deleteTable(tableName);
		db.deleteTable(testTableName);

	}

}
