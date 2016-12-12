package support;

import java.util.List;
import java.util.Map;

public class Print {

	int treeHeight;
	List<String> classValues;
	Map<String, String> columnPairNames;

	public Print(Map<String, String> columnPairNames, List<String> classValues, Node node) {

		this.classValues = classValues;
		treeHeight = heightOfTree(node);
		this.columnPairNames = columnPairNames;

	}

	// Helper Functions

	public int heightOfTree(Node node) {

		if (node == null)
			return -1;
		else {
			int ht = 0;
			for (int i = 0; i < node.children.length; i++)
				ht = 1 + Math.max(heightOfTree(node.children[i]), ht);
			return ht;
		}

	}

	public int numberOfLeaves(Node node, int leafCount) {

		if (node.branchNames == null)
			leafCount++;
		else
			for (int i = 0; i < node.children.length; i++)
				leafCount = numberOfLeaves(node.children[i], leafCount);

		return leafCount;

	}

	// Print Functions

	public void printRules(List<Rule>[] rules) {

		int size = rules.length;

		for (int i = 0; i < size; i++) {

			int rulePosition = 0;
			int ruleLength = rules[i].get(rulePosition).preCondition.size();

			for (int j = 0; j < ruleLength; j++) {

				String currentNodeName = rules[i].get(rulePosition).preConditionIndex.get(j);
				String currentBranchName = rules[i].get(rulePosition).preCondition.get(currentNodeName);

				if (columnPairNames == null)
					System.out.print(currentNodeName + " = " + currentBranchName);
				else
					System.out.print(columnPairNames.get(currentNodeName) + " = " + currentBranchName);

				if (j < ruleLength - 1)
					System.out.print(" ^ ");
			}

			System.out.println(" => " + rules[i].get(rulePosition).postCondition);

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

				// Print Tree DecisionTree

				if (classValues.contains(node.children[i].nodeName) && columnPairNames == null)
					System.out.print(
							node.nodeName + " = " + node.branchNames[i] + " : " + node.children[i].nodeName + "\n");
				else if (columnPairNames == null)
					System.out.println(node.nodeName + " = " + node.branchNames[i]);

				// Print Tree Iris

				else if (classValues.contains(node.children[i].nodeName) /* && !simple */)
					System.out.print(columnPairNames.get(node.nodeName) + " = " + node.branchNames[i] + " : "
							+ node.children[i].nodeName + "\n");
				else
					System.out.println(columnPairNames.get(node.nodeName) + " = " + node.branchNames[i]);

				printTree(node.children[i], node);

			}
	}

}
