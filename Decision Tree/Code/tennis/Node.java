package tennis;

public class Node {

	public Node[] children;
	public String nodeName;
	public String[] branchNames;
	public double informationGain;

	// Attribute values that are being added to Node
	// node name, branch names, child nodes

	public Node() {
		super();
	}
	
	public Node(String nodeName, String[] branchNames, double informationGain) {
		this.nodeName = nodeName;
		this.branchNames = branchNames;
		this.informationGain = informationGain;
		this.children = new Node[branchNames.length];
	}
	
	@Override
	public String toString() {
		return nodeName;
	}

}
