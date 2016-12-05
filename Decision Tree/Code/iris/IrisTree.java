package iris;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

import db.dbConnection;
import java.io.IOException;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class IrisTree {

	static int leafCount = 0;
	static int ruleNumber = 0;
	static int treeHeight = 0;
	static int compareIndex = 0;

	static String tableName = "TrainIrisData";
	static String testTableName = "TestIrisData";

	static List<Double>[] avgColValues;
	static List<List<String>> trainingExampleListMain;

	double totalE, totalI;
	double accuracy, entropy;
	double[] pi, pE, informationGain;
	double columnCount, columnsToCompare, distinctColumnCount;
	double trainingDataSetRowCount, validationDataSetRowCount;
	double class1Count = 0.0, class2Count = 0.0, class3Count = 0.0;

	String nodeName, classNameYN;
	String columnName, columnValue;
	String[] classValueYN, distinctColumnValues, nodeValues;

	List<Double> informationGainList;
	Map<String, Double> bestAttribute;
	Map<Integer, String> attributeIndex;
	Map<String, Double> bestAttributeTemp;
	Map<Double, String> informationGainMap;
	List<String> columnNames, columnNamesTemp, nodeValuesList;
	Map<String, String> columnPairNames = new HashMap<String, String>();

	dbConnection db;
	List<Rule>[] rules;
	List<String>[] ruleList;
	List<String> visited = new LinkedList<String>();
	List<String> treeAttributes = new LinkedList<String>();

	public IrisTree() throws ClassNotFoundException, SQLException, IOException {

		db = new dbConnection();
		db.createTableIris(tableName);
		createTable(tableName);

		columnNames = db.columnNames(tableName);
		columnCount = columnNames.size();

		double trainingDataProbability = 0.7;
		int rowCount = (int) db.rowCount(tableName);
		trainingDataSetRowCount = Math.round(rowCount * trainingDataProbability);
		validationDataSetRowCount = Math.round(rowCount * (1 - trainingDataProbability));

		classNameYN = db.columnNames(tableName).get((int) (4));
		classValueYN = db.distinctColumnValuesMethod(classNameYN, tableName);

		columnNames.remove(classNameYN);
		columnNamesTemp = columnNames;

		totalE = db.rowCount(tableName);
		nodeValuesList = new ArrayList<String>();
		bestAttribute = new HashMap<String, Double>();
		attributeIndex = new HashMap<Integer, String>();
		compareIndex = columnNames.size() + 1;

	}

	// DB Related

	public void formTestTable(String tName) throws SQLException, IOException, ClassNotFoundException {

		db.createTableIris(testTableName);
		createTable(testTableName);

		columnNames = db.columnNames(tName);
		columnNames.remove(classNameYN);
		List<List<String>> trainingExampleListTemp = db.trainingExamplesTrainingSet(tName);

		for (Entry<String, String> cpm : columnPairNames.entrySet()) {

			int i = 0;
			String newColumnName = cpm.getKey();
			String[] s2 = cpm.getValue().split(" ");
			String columnUnderConsideration = s2[0];
			double avgValueUnderConsideration = Double.parseDouble((s2[2]));

			for (Entry<Integer, String> entry : attributeIndex.entrySet())
				if (entry.getValue().equals(newColumnName)) {
					i = entry.getKey();
					break;
				}

			db.addColumn(newColumnName, tName);

			for (int k = 0; k < trainingExampleListTemp.size() - 1; k++) {
				double colValue = Double.parseDouble(trainingExampleListTemp.get(k).get(i));
				if (colValue > avgValueUnderConsideration)
					db.updateColumnYes(avgValueUnderConsideration, columnUnderConsideration, newColumnName, tName);
				else
					db.updateColumnNo(avgValueUnderConsideration, columnUnderConsideration, newColumnName, tName);
			}

		}

		for (int i = 0; i < columnNames.size(); i++)
			db.dropColumn(columnNames.get(i), tName);

		db.reorderColumn(tName);

	}

	public void createTable(String tName) throws IOException, SQLException {

		String inputData;
		FileInputStream fstream;
		BufferedReader bufferedReader;
		String trainingFileIris = null;

		if (tName.equals(tableName))
			trainingFileIris = "TrainIrisData";
		else if (tName.equals(testTableName))
			trainingFileIris = "TestIrisData";

		fstream = new FileInputStream(System.getProperty("user.dir") + "\\resource\\" + trainingFileIris);
		bufferedReader = new BufferedReader(new InputStreamReader(fstream));

		while ((bufferedReader.readLine()) != null) {
			inputData = bufferedReader.readLine().trim();
			String[] rowValues = inputData.split(" ");
			db.insertRow(rowValues, tName);
		}

		bufferedReader.close();

	}

	@SuppressWarnings("unused")
	public void addColumns(List<Double>[] avgColValuesTest, String tName) throws SQLException, ClassNotFoundException {

		String newColumnName, actualNewColumnName;

		for (int i = 0; i < columnNames.size(); i++) {

			String columnUnderConsideration = columnNamesTemp.get(i);
			Map<String, String> columNamesContinousData = new HashMap<String, String>();
			List<List<String>> trainingExampleListTemp = db.orderBy(columnUnderConsideration, tName);

			for (int j = 0; j < avgColValuesTest[i].size(); j++) {

				double avgValueUnderConsideration = avgColValuesTest[i].get(j);
				newColumnName = columnUnderConsideration + "_" + i + j;
				actualNewColumnName = columnUnderConsideration + " > " + avgValueUnderConsideration;
				columNamesContinousData.put(newColumnName, actualNewColumnName);
				db.addColumn(newColumnName, tName);

				for (int k = 0; k < trainingExampleListTemp.size() - 1; k++) {
					double colValue = Double.parseDouble(trainingExampleListTemp.get(k).get(i));
					if (colValue > avgValueUnderConsideration)
						db.updateColumnYes(avgValueUnderConsideration, columnUnderConsideration, newColumnName, tName);
					else
						db.updateColumnNo(avgValueUnderConsideration, columnUnderConsideration, newColumnName, tName);
				}
			}

			List<List<String>> trainingExampleListNew = db.trainingExamples(tName);
			Map<String, Double> selectedAttributePair = calculateInformationAttributeGain(trainingExampleListNew,
					tName);
			String selectedColumn = selectedAttributePair.entrySet().iterator().next().getKey();
			columnPairNames.put(selectedColumn, columNamesContinousData.get(selectedColumn));
			List<String> newColumnNames = db.columnNames(tName);
			columNamesContinousData.clear();
			selectedAttributePair.clear();

			for (int q = compareIndex; q < newColumnNames.size(); q++)
				if (!newColumnNames.get(q).equals(selectedColumn))
					db.dropColumn(newColumnNames.get(q), tName);

			compareIndex++;
		}
		for (int i = 0; i < columnNames.size(); i++)
			db.dropColumn(columnNames.get(i), tName);

		db.reorderColumn(tName);
		columnNames = db.columnNames(tName);
		columnNames.remove(classNameYN);
		columnNamesTemp = columnNames;
		compareIndex = 0;

	}

	@SuppressWarnings("unchecked")
	public List<Double>[] findAverageColumnValues(String tName) throws SQLException, ClassNotFoundException {

		List<Double>[] avgValues = new ArrayList[columnNames.size()];
		List<String> attributeNames = db.columnNames(tName);
		int tableWidth = attributeNames.size();

		for (int i = 0; i < columnNames.size(); i++) {

			avgValues[i] = new ArrayList<Double>();
			String columnUnderConsideration = columnNamesTemp.get(i);
			List<List<String>> trainingExampleListTemp = db.orderBy(columnUnderConsideration, tName);

			for (int k = 0; k < trainingExampleListMain.size() - 1; k++) {

				String rowValue1 = trainingExampleListTemp.get(k).get(tableWidth - 1);
				String rowValue2 = trainingExampleListTemp.get(k + 1).get(tableWidth - 1);

				if (rowValue1.equals(rowValue2))
					continue;
				else {

					double temp1 = Double.parseDouble(trainingExampleListTemp.get(k).get(i));
					double temp2 = Double.parseDouble(trainingExampleListTemp.get(k + 1).get(i));
					double avg = (temp1 + temp2) / 2;

					if (avgValues[i].contains(avg))
						continue;

					avgValues[i].add(avg);
					Collections.sort(avgValues[i]);
				}
			}
		}
		return avgValues;
	}

	// Leaf Node Related

	public String mostCommonValue(List<List<String>> trainingExampleListTemp)
			throws SQLException, ClassNotFoundException {

		int size = trainingExampleListTemp.size();
		double class1Count = 0.0, class2Count = 0.0, class3Count = 0.0;

		for (int i = 0; i < size; i++)
			if (trainingExampleListTemp.get(i).contains(classValueYN[0]))
				class1Count++;
			else if (trainingExampleListTemp.get(i).contains(classValueYN[1]))
				class2Count++;
			else if (trainingExampleListTemp.get(i).contains(classValueYN[2]))
				class3Count++;

		if (class1Count > class2Count && class1Count > class3Count)
			return classValueYN[0];
		else if (class2Count > class3Count && class2Count > class1Count)
			return classValueYN[1];
		else
			return classValueYN[2];

	}

	public String checkForLeafNode(List<List<String>> allClassValueSameCheck) throws SQLException {

		int total = 0;
		class1Count = 0.0;
		class2Count = 0.0;
		class3Count = 0.0;
		int columnCount = 0;
		String leafValueCheck = null;
		int size = allClassValueSameCheck.size();

		if (size != 0)
			columnCount = allClassValueSameCheck.get(0).size();

		for (int i = 0; i < allClassValueSameCheck.size(); i++)
			if (allClassValueSameCheck.get(i).get(columnCount - 1).equals(classValueYN[0]))
				class1Count += 1;
			else if (allClassValueSameCheck.get(i).get(columnCount - 1).equals(classValueYN[1]))
				class2Count += 1;
			else if (allClassValueSameCheck.get(i).get(columnCount - 1).equals(classValueYN[2]))
				class3Count += 1;

		total = (int) (class1Count + class2Count + class3Count);

		if (size == total)
			if (class2Count == 0 && class3Count == 0)
				leafValueCheck = classValueYN[0];
			else if (class1Count == 0 && class3Count == 0)
				leafValueCheck = classValueYN[1];
			else if (class1Count == 0 && class2Count == 0)
				leafValueCheck = classValueYN[2];

		return leafValueCheck;

	}

	public Node addLeafNode(String leafValueTemp, boolean attributeEmpty) throws ClassNotFoundException, SQLException {

		Node leaf = new Node();

		if (leafValueTemp == null)
			if (class1Count > class2Count && class1Count > class3Count)
				leaf.nodeName = classValueYN[0];
			else if (class2Count > class3Count && class2Count > class1Count)
				leaf.nodeName = classValueYN[1];
			else
				leaf.nodeName = classValueYN[2];

		if (leafValueTemp != null)
			leaf.nodeName = leafValueTemp;

		return leaf;
	}

	// Entropy & Information Gain

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

		columnCount = db.columnNames(tableName).size() - compareIndex;
		informationGainMap = new HashMap<Double, String>();
		informationGainList = new ArrayList<Double>();
		informationGain = new double[(int) columnCount];
		Arrays.fill(informationGain, calculateEntropy(tableName));

		List<String> colNam = db.columnNames(tableName);

		int i = 0;
		for (int a = compareIndex; a < colNam.size(); a++)
			if ((nodeValuesList.contains(colNam.get(a))) || colNam.get(a).contains(classNameYN))
				continue;
			else {
				for (; i < informationGain.length;) {

					int k = 0, m = 0;
					columnName = colNam.get(a);
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

					for (k = 0; k < pi.length; k = k + 3) {

						double temp1 = (pi[k] + pi[k + 1] + pi[k + 2]) / totalE;
						double temp2 = pi[k] / (pi[k] + pi[k + 1] + pi[k + 2])
								* (Math.log10(pi[k] / (pi[k] + pi[k + 1] + pi[k + 2])) / Math.log10(2));
						double temp3 = pi[k + 1] / (pi[k] + pi[k + 1] + pi[k + 2])
								* (Math.log10(pi[k + 1] / (pi[k] + pi[k + 1] + pi[k + 2])) / Math.log10(2));
						double temp4 = pi[k + 2] / (pi[k] + pi[k + 1] + pi[k + 2])
								* (Math.log10(pi[k + 2] / (pi[k] + pi[k + 1] + pi[k + 2])) / Math.log10(2));

						if (Double.isNaN(temp2))
							temp2 = 0;
						if (Double.isNaN(temp3))
							temp3 = 0;
						if (Double.isNaN(temp3))
							temp4 = 0;

						double temp5 = -1 * temp1 * (temp2 + temp3 + temp4);
						informationGain[i] -= temp5;

						if (Double.isNaN(informationGain[i]))
							informationGain[i] = 0;
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
		if (columnNamesTemp.contains(nodeName)) {
			columnNamesTemp.remove(nodeName);
			nodeValuesList.add(nodeName);
		}

		bestAttribute.put(nodeName, informationGainList.get(0));
		return bestAttribute;

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
			Node newLeaf = new Node();
			newLeaf = addLeafNode(leafValue, attributesIsEmpty);
			newLeaf.children = new Node[0];
			return newLeaf;
		}

		bestAttributeTemp = calculateInformationAttributeGain(trainingExampleList, tableName);
		node.branchNames = db.distinctColumnValuesMethod(nodeName, tableName);
		node.informationGain = bestAttributeTemp.get(nodeName);
		int nodeBranchesLength = node.branchNames.length;
		node.nodeName = columnPairNames.get(nodeName);
		node.children = new Node[nodeBranchesLength];
		node.nodeName = nodeName;

		for (int s = 0; s < nodeBranchesLength; s++) {

			// check if trainingExamples is empty

			boolean exampleListEmpty = false;

			if (trainingExampleList.isEmpty()) {
				Node leafNode = new Node();
				exampleListEmpty = true;
				leafNode.nodeName = mostCommonValue(trainingExampleListTemp);
				leafNode = addLeafNode(leafValue, attributesIsEmpty);
				node.children[s] = leafNode;
				node.children[s].children = new Node[0];
			} else {

				List<List<String>> trainingExampleNewToCmpare = db.newTrainingExamples(node.branchNames[s], nodeName,
						tableName);

				for (int k = 0; k < trainingExampleList.size(); k++)
					for (int l = 0; l < trainingExampleNewToCmpare.size(); l++)
						if ((trainingExampleList.get(k).equals(trainingExampleNewToCmpare.get(l))))
							trainingExampleListTemp.add(trainingExampleList.get(k));

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
				System.out.print(columnPairNames.get(ruleList[i].get(j)) + " = ");
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
						|| node.children[i].nodeName.contains(classValueYN[1])
						|| node.children[i].nodeName.contains(classValueYN[2]))
					System.out.print(columnPairNames.get(node.nodeName) + " = " + node.branchNames[i] + " : "
							+ node.children[i].nodeName + "\n");
				else
					System.out.println(columnPairNames.get(node.nodeName) + " = " + node.branchNames[i]);

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

	public double testAccuracy(String tname) throws SQLException {

		int correct = 0;
		boolean correctPrediction = false;
		int columnCount = db.columnNames(tname).size();
		int instanceCount = (int) db.rowCount(tname);
		List<List<String>> trainingExampleList = db.trainingExamples(tname);

		for (int i = 0; i < instanceCount; i++) {

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

		accuracy = correct / instanceCount * 100;
		return accuracy;

	}

	// Main Function

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		Node treeNode = new Node();
		IrisTree iris = new IrisTree();
		dbConnection db = new dbConnection();

		trainingExampleListMain = db.trainingExamples(tableName);
		avgColValues = iris.findAverageColumnValues(tableName);
		iris.addColumns(avgColValues, tableName);

		List<String> attributeNames = db.columnNames(tableName);
		trainingExampleListMain = db.trainingExamples(tableName);
		treeNode = iris.ID3Algorithm(trainingExampleListMain, attributeNames);

		System.out.println("Tree Structure\n");
		treeHeight = iris.heightOfTree(treeNode);
		iris.printTree(treeNode, null);
		iris.numberOfLeaves(treeNode);

		System.out.println("\nRule Set\n");
		iris.ruleList = new ArrayList[leafCount];
		iris.rules = new ArrayList[leafCount];
		iris.buildRuleSet(treeNode);
		iris.printRules();

		iris.ruleConversion();
		iris.initializeAttributeIndex();
		double accuracy = iris.testAccuracy(tableName);
		System.out.println("\nTrainig Accuracy : " + accuracy + "%");

		iris.formTestTable(testTableName);
		accuracy = iris.testAccuracy(testTableName);
		System.out.println("\nTest Accuracy : " + accuracy + "%");

		db.deleteTable(tableName);
		db.deleteTable(testTableName);

	}

}
