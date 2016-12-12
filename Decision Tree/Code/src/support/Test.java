package support;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.SQLException;

import db.dbConnection;

public class Test {

	dbConnection db;

	public Test() throws ClassNotFoundException, SQLException {
		db = new dbConnection();
	}

	public double testAccuracy(Map<Integer, String> attributeIndex, List<Rule>[] rules, List<List<String>> data,
			String tableName) throws SQLException, ClassNotFoundException {

		int correct = 0;
		int rulePosition = 0;
		int ruleCount = rules.length;
		boolean correctPrediction = false;
		int examplesCount = data.size();// (int) db.rowCount(tableName);
		int columnCount = db.columnNames(tableName).size();
		// List<List<String>> trainingExampleList =
		// db.trainingExamples(tableName);

		for (int i = 0; i < examplesCount; i++) {

			for (int j = 0; j < ruleCount; j++) {

				for (int k = 0; k < columnCount; k++) {

					String currentColumn = attributeIndex.get(k);
					String currentColumnValue = data.get(i).get(k);
					String ruleValue = rules[j].get(rulePosition).preCondition.get(currentColumn);

					if (k == columnCount - 1) {

						ruleValue = rules[j].get(rulePosition).postCondition;

						if (ruleValue.equals(currentColumnValue))
							correctPrediction = true;
						else
							correctPrediction = false;

					} else if (ruleValue == null)
						correctPrediction = true;
					else if (ruleValue.equals(currentColumnValue))
						correctPrediction = true;
					else {
						correctPrediction = false;
						break;
					}

				}

				if (correctPrediction == true) {
					correctPrediction = false;
					correct++;
					break;
				}
			}
		}

		double accuracy = (double) correct / examplesCount * 100;

		return accuracy;

	}

	@SuppressWarnings({ "unchecked" })
	public List<Rule>[] pruneTree(Map<Integer, String> attributeIndex, List<Rule>[] rules, double accuracy,
			List<List<String>> data, String tableName) throws ClassNotFoundException, SQLException {

		int ruleCount = rules.length;

		for (int i = 0; i < ruleCount; i++) {

			int rulePosition = 0;
			List<Rule>[] singleRule = new ArrayList[1];
			int ruleLength = rules[i].get(rulePosition).preConditionIndex.size();

			for (int k = ruleLength - 1; k > 0; k--) {

				// Check Accuracy of Original Rule

				singleRule[0] = rules[i];
				double currentRuleAccuracy = testAccuracy(attributeIndex, singleRule, data, tableName);

				// Remove Last Attribute Present in Rule List
				
				String currentNodeName = rules[i].get(rulePosition).preConditionIndex.get(k);
				String currentBranchName = rules[i].get(rulePosition).preCondition.get(currentNodeName);
				rules[i].get(rulePosition).preCondition.remove(currentNodeName);

				// Check Accuracy of Modified Rule

				singleRule[0] = rules[i];
				double modifiedRuleAccuracy = testAccuracy(attributeIndex, singleRule, data, tableName);

				if (modifiedRuleAccuracy <= currentRuleAccuracy) {
					rules[i].get(rulePosition).preCondition.put(currentNodeName, currentBranchName);
					break;
				}

			}
		}
		return rules;
	}

}
