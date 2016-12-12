package best;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.dbConnection;

public class BestAttribute {

	int compareIndex;
	String className;
	List<List<String>> trainingDataMain;

	dbConnection db;

	public BestAttribute(int compareIndex, String className, List<List<String>> trainingDataMain)
			throws ClassNotFoundException, SQLException {

		db = new dbConnection();

		this.className = className;
		this.compareIndex = compareIndex;
		this.trainingDataMain = trainingDataMain;

	}

	// Entropy & Information Gain

	public double calculateEntropy(List<List<String>> trainingData, String tableName)
			throws ClassNotFoundException, SQLException {

		double entropy = 0.0;
		double totalE = trainingData.size();
		String[] classValues = db.distinctColumnValuesMethod(className, tableName);

		int classIndex = compareIndex - 1;
		int distinctClassCount = classValues.length;
		double[] pE = new double[(int) distinctClassCount];

		// Calculate Class Distribution

		for (int i = 0; i < distinctClassCount; i++)
			for (int j = 0; j < totalE; j++)
				if (trainingData.get(j).get(classIndex).equals(classValues[i]))
					pE[i]++;

		// Calculate Entropy

		for (int i = 0; i < distinctClassCount; i++)
			if (pE[i] == 0)
				continue;
			else
				entropy += (-1 * pE[i] / totalE) * ((Math.log10(pE[i] / totalE)) / (Math.log10(2)));

		return entropy;
	}

	public double calculateInformationGain(int columnPosition, double informationGain, String[] distinctColumnValues,
			List<List<String>> trainingData, String tableName) throws ClassNotFoundException, SQLException {

		double subEntropy = 0.0;
		int distinctColumnCount = distinctColumnValues.length;
		List<List<String>> trainingExamplesTemp = new ArrayList<List<String>>();

		for (int i1 = 0; i1 < distinctColumnCount; i1++) {

			// Group Similar Column Name Values
			for (int i2 = 0; i2 < trainingData.size(); i2++)
				if ((trainingData.get(i2).get(columnPosition).equals(distinctColumnValues[i1])))
					trainingExamplesTemp.add(trainingData.get(i2));

			// Calculate Information Gain

			double subSize = trainingExamplesTemp.size();
			double mainSize = trainingData.size();

			if (trainingExamplesTemp.size() == 0)
				subEntropy = 0.0;// System.out.println(123);
			else {
				subEntropy = calculateEntropy(trainingExamplesTemp, tableName);
				subEntropy = (subSize / mainSize) * subEntropy;
			}

			informationGain -= subEntropy;
			trainingExamplesTemp.clear();
		}

		return informationGain;
	}

	public Map<String, Double> findBestAttribute(List<List<String>> trainingData,
			Map<String, Integer> attributeIndexPair, List<String> attributeNames, String tableName)
					throws ClassNotFoundException, SQLException {

		int columnCount = attributeNames.size();
		double[] informationGain = new double[(int) columnCount];
		List<Double> informationGainList = new ArrayList<Double>();
		Map<Double, String> informationGainMap = new HashMap<Double, String>();

		// Calculate Entropy of Initial Data
		Arrays.fill(informationGain, calculateEntropy(trainingDataMain, tableName));

		for (int i = 0; i < attributeNames.size(); i++) {

			String columnName = attributeNames.get(i);
			int columnPosition = attributeIndexPair.get(columnName);
			String[] distinctColumnValues = db.distinctColumnValuesMethod(columnName, tableName);

			informationGain[i] = calculateInformationGain(columnPosition, informationGain[i], distinctColumnValues,
					trainingData, tableName);

			informationGainList.add(informationGain[i]);
			informationGainMap.put(informationGain[i], columnName);

		}

		Collections.sort(informationGainList);
		Collections.reverse(informationGainList);
		String nodeName = informationGainMap.get(informationGainList.get(0));

		Map<String, Double> bestAttribute = new HashMap<String, Double>();
		bestAttribute.put(nodeName, informationGainList.get(0));

		return bestAttribute;

	}

}
