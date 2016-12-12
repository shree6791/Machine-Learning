package id3;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.SQLException;

import support.Node;
import db.dbConnection;
import best.BestAttribute;

public class ID3 {

	String tableName;
	String[] classValues;

	dbConnection db;
	BestAttribute best;

	public ID3(int compareIndex, String tableName, String className, List<List<String>> trainingDataMain)
			throws ClassNotFoundException, SQLException {

		this.tableName = tableName;

		db = new dbConnection();
		classValues = db.distinctColumnValuesMethod(className, tableName);
		best = new BestAttribute(compareIndex, className, trainingDataMain);

	}

	// Leaf Node Related Functions

	public Node mostCommonValue(List<List<String>> trainingData, String[] classValues)
			throws SQLException, ClassNotFoundException {

		Node leafNode = new Node();

		int maxIndex = 0;
		int distinctClassCount = classValues.length;
		int classIndex = trainingData.get(0).size() - 1;
		double[] classCount = new double[distinctClassCount];
		classCount = classDistribution(classIndex, classCount, trainingData);

		for (int i = 0; i < distinctClassCount - 1; i++)
			if (classCount[i] < classCount[i + 1])
				maxIndex = i + 1;

		leafNode.nodeName = classValues[maxIndex];

		return leafNode;

	}

	public Node checkForLeafNode(List<List<String>> trainingData, String[] classValues) throws SQLException {

		Node leafNode = new Node();

		int count = 0;
		int leafPositiion = 0;
		boolean leafExists = false;
		int distinctClassCount = classValues.length;
		int classIndex = trainingData.get(0).size() - 1;
		double[] classCount = new double[distinctClassCount];
		classCount = classDistribution(classIndex, classCount, trainingData);

		// Check Presence of Distinct Class Value in Data Set

		for (int i = 0; i < distinctClassCount; i++) {

			count = 0;
			for (int j = 0; j < distinctClassCount; j++)
				if (classCount[j] == 0 && i != j)
					count++;

			if (count == distinctClassCount - 1) {
				leafExists = true;
				leafPositiion = i;
				break;
			}
		}

		if (leafExists) {
			leafNode.nodeName = classValues[leafPositiion];
			return leafNode;
		} else
			return null;

	}

	public double[] classDistribution(int classIndex, double[] classCount, List<List<String>> trainingData) {

		int distinctClassCount = classCount.length;

		for (int i = 0; i < distinctClassCount; i++)
			for (int j = 0; j < trainingData.size(); j++)
				if (trainingData.get(j).get(classIndex).equals(classValues[i]))
					classCount[i]++;

		return classCount;

	}

	// ID3 Algorithm

	public Node ID3Algorithm(List<List<String>> trainingData, List<String> attributeNames,
			Map<String, Integer> attributeIndexPair) throws SQLException, ClassNotFoundException {

		Map<String, Double> bestAttribute = new HashMap<String, Double>();
		List<List<String>> trainingDataTemp = new ArrayList<List<String>>();

		// check if trainingExamples are all positive or negative, Attributes to
		// compare are empty, add leaf Node, if it exists

		Node node = checkForLeafNode(trainingData, classValues);

		if (node != null) {
			// System.out.println(node.nodeName);
			node.children = new Node[0];
			return node;
		}
		if (attributeNames.isEmpty()) {
			node = mostCommonValue(trainingData, classValues);
			// System.out.println(node.nodeName);
			node.children = new Node[0];
			return node;
		}

		node = new Node();
		bestAttribute = best.findBestAttribute(trainingData, attributeIndexPair, attributeNames, tableName);
		node.nodeName = bestAttribute.entrySet().iterator().next().getKey();
		node.branchNames = db.distinctColumnValuesMethod(node.nodeName, tableName);
		node.informationGain = bestAttribute.get(node.nodeName);
		int nodeBranchesLength = node.branchNames.length;
		node.children = new Node[nodeBranchesLength];

		for (int s = 0; s < nodeBranchesLength; s++) {

			// Check If Training Examples Is Empty

			int columnPosition = attributeIndexPair.get(node.nodeName);

			for (int i = 0; i < trainingData.size(); i++)
				if (trainingData.get(i).get(columnPosition).equals(node.branchNames[s]))
					trainingDataTemp.add(trainingData.get(i));

			if (trainingDataTemp.isEmpty()) {
				node.children[s] = mostCommonValue(trainingData, classValues);
				// System.out.println(node.nodeName + " " + node.branchNames[s]
				// + "\n" + node.children[s].nodeName);
				node.children[s].children = new Node[0];
			} else {
				attributeNames.remove(node.nodeName);
				// System.out.println(node.nodeName + " " +
				// node.branchNames[s]);
				node.children[s] = ID3Algorithm(trainingDataTemp, attributeNames, attributeIndexPair);
			}
			trainingDataTemp.clear();
		}

		return node;
	}
}
