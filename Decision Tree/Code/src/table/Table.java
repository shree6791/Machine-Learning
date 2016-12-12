package table;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

import java.io.IOException;
import java.sql.SQLException;

import db.dbConnection;
import best.BestAttribute;

public class Table {

	int compareIndex = 0;

	String className;
	String[] classValues;
	List<String> columnNames;
	Map<String, String> columnPairNames;

	List<List<String>> dataSampleMain;
	List<Double>[] averageColumnValues;

	dbConnection db;

	public Table(String tableName) throws ClassNotFoundException, SQLException, IOException {

		db = new dbConnection();

		columnNames = db.columnNames(tableName);
		dataSampleMain = db.trainingExamplesTrainingSet(tableName);
		className = db.columnNames(tableName).get(columnNames.size() - 1);
		classValues = db.distinctColumnValuesMethod(className, tableName);

		columnNames.remove(className);
		compareIndex = columnNames.size() + 1;
		columnPairNames = new HashMap<String, String>();

	}

	// DB Related Functions

	@SuppressWarnings("unchecked")
	public List<Double>[] findAverageColumnValues(String tableName) throws SQLException, ClassNotFoundException {

		boolean conversion = true;
		averageColumnValues = new ArrayList[columnNames.size()];

		for (int i = 0; i < columnNames.size(); i++) {

			averageColumnValues[i] = new ArrayList<Double>();
			String columnUnderConsideration = columnNames.get(i);
			String[] distinctColumnValues = db.distinctColumnValuesMethod(columnUnderConsideration, tableName);

			// Check If Column Values are Discrete

			for (int j = 0; j < distinctColumnValues.length; j++) {
				if (isDouble(distinctColumnValues[j]))
					continue;
				conversion = false;
				break;
			}

			if (!conversion)
				continue;

			List<List<String>> dataSampleTemp = db.orderBy(columnUnderConsideration, tableName);

			for (int k = 0; k < dataSampleMain.size() - 1; k++) {

				String rowValue1 = dataSampleTemp.get(k).get(i);
				String rowValue2 = dataSampleTemp.get(k + 1).get(i);

				if (rowValue1.equals(rowValue2))
					continue;
				else {

					double temp1 = Double.parseDouble(dataSampleTemp.get(k).get(i));
					double temp2 = Double.parseDouble(dataSampleTemp.get(k + 1).get(i));
					double avg = (temp1 + temp2) / 2;

					if (averageColumnValues[i].contains(avg))
						continue;

					averageColumnValues[i].add(avg);
				}
			}

			Collections.sort(averageColumnValues[i]);
		}

		return averageColumnValues;
	}

	public Map<String, String> addColumns(String tableName) throws SQLException, ClassNotFoundException {

		dbConnection db = new dbConnection();
		Map<String, Integer> attributeIndexPair;
		BestAttribute best = new BestAttribute(compareIndex, className, dataSampleMain);

		int twoCount = 2;
		String newColumnName, actualNewColumnName;
		List<String> deleteColumns = new ArrayList<String>();

		for (int i = 0; i < columnNames.size(); i++) {

			if (averageColumnValues[i].isEmpty())
				continue;

			Map<String, String> columNamesContinousData = new HashMap<String, String>();
			String columnUnderConsideration = columnNames.get(i);
			deleteColumns.add(columnUnderConsideration);

			for (int j = 0; j < averageColumnValues[i].size(); j++) {

				double avgValueUnderConsideration = averageColumnValues[i].get(j);
				newColumnName = columnUnderConsideration + "_" + i + j;
				actualNewColumnName = columnUnderConsideration + " > " + avgValueUnderConsideration;
				columNamesContinousData.put(newColumnName, actualNewColumnName);
				db.addColumn(newColumnName, tableName);

				// Update New Column as Yes or No for
				// "avgValueUnderConsideration"

				for (int q = 0; q < twoCount; q++) {
					if (q % 2 == 0)
						db.updateColumnYes(avgValueUnderConsideration, columnUnderConsideration, newColumnName,
								tableName);
					else
						db.updateColumnNo(avgValueUnderConsideration, columnUnderConsideration, newColumnName,
								tableName);
				}

			}

			List<String> newColumnNames = db.columnNames(tableName);
			List<List<String>> newDataSample = db.trainingExamples(tableName);

			for (int j = 0; j < compareIndex; j++)
				newColumnNames.remove(0);

			attributeIndexPair = makeAttributeIndexPair(tableName);

			Map<String, Double> selectedAttributePair = best.findBestAttribute(newDataSample, attributeIndexPair,
					newColumnNames, tableName);
			String selectedColumn = selectedAttributePair.entrySet().iterator().next().getKey();
			columnPairNames.put(selectedColumn, columNamesContinousData.get(selectedColumn));
			columNamesContinousData.clear();
			selectedAttributePair.clear();

			for (int q = 0; q < newColumnNames.size(); q++)
				if (!newColumnNames.get(q).equals(selectedColumn))
					db.dropColumn(newColumnNames.get(q), tableName);

			compareIndex++;

		}

		if (deleteColumns.isEmpty())
			return null;

		for (int i = 0; i < deleteColumns.size(); i++)
			db.dropColumn(deleteColumns.get(i), tableName);

		String reorder = "";
		columnNames = db.columnNames(tableName);

		for (int i = 1; i < columnNames.size(); i++)
			reorder += columnNames.get(i) + ", ";

		reorder += className + " ";
		db.reorderColumns(reorder, tableName);

		return columnPairNames;

	}

	// Helper Function

	public boolean isDouble(String columnValue) {

		try {
			Double.parseDouble(columnValue);
		} catch (Exception e) {
			return false;
		}
		return true;

	}

	public Map<Integer, String> makeIndexAttributePair(String tableName) throws SQLException {

		List<String> columnNames = db.columnNames(tableName);
		Map<Integer, String> indexAttributePair = new HashMap<Integer, String>();

		for (int i = 0; i < columnNames.size(); i++)
			indexAttributePair.put(i, columnNames.get(i));

		return indexAttributePair;

	}

	public Map<String, Integer> makeAttributeIndexPair(String tableName) throws SQLException {

		List<String> columnNames = db.columnNames(tableName);
		Map<String, Integer> attributeIndexPair = new HashMap<String, Integer>();

		for (int i = 0; i < columnNames.size(); i++)
			attributeIndexPair.put(columnNames.get(i), i);

		return attributeIndexPair;

	}

	public List<List<String>> makeData(List<List<String>> dataSample, int startIndex, int endIndex) {

		List<List<String>> data = new ArrayList<List<String>>();

		for (int i = startIndex; i < endIndex; i++)
			data.add(dataSample.get(i));

		return data;

	}

}
