package support;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;

public class Rule {

	int ruleNumber;
	List<String> visited;
	public String postCondition;
	public Map<String, String> preCondition;
	public Map<Integer, String> preConditionIndex;

	public Rule() {

		ruleNumber = 0;
		postCondition = null;
		visited = new LinkedList<String>();
		preCondition = new HashMap<String, String>();
		preConditionIndex = new HashMap<Integer, String>();

	}

	public List<String>[] buildRuleSet(List<String>[] ruleList, Node node) {

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
			buildRuleSet(ruleList, node.children[i]);
			visited.remove(visited.size() - 2/* node.nodeName */);
			visited.remove(visited.size() - 1);
		}

		return ruleList;
	}

	public List<Rule>[] ruleConversion(List<Rule>[] rules, List<String>[] ruleList, int leafCount) {

		for (int i = 0; i < leafCount; i++) {

			Rule rule = new Rule();
			rules[i] = new ArrayList<>();
			int end = ruleList[i].size() - 1;

			for (int j = 0; j < end; j = j + 2) {
				rule.preConditionIndex.put(j / 2, ruleList[i].get(j));
				rule.preCondition.put(ruleList[i].get(j), ruleList[i].get(j + 1));
			}

			rule.postCondition = ruleList[i].get(end);
			rules[i].add(rule);

		}

		return rules;
	}

}
