public class Node {
	private final Node left, right;
	private final Stakeholder stakeholder;
	private final byte[] hash;
	
	private Node(Node left, Node right, Stakeholder stakeholder, byte[] hash) {
		this.stakeholder = stakeholder;
		this.left = left;
		this.right = right;
		this.hash = hash;
	}
	
	public Node(Node left, Node right, byte[] hash) {
		this(left, right, null, hash);
	}
	
	public Node(Stakeholder stakeholder) {
		this(null, null, stakeholder, SHA2(stakeholder.toBytes()));
	}
	
	public boolean isLeaf() {
		return stakeholder != null;
	}
	
	public Stakeholder getStakeholder() {
		return stakeholder;
	}
	
	public Node getLeftNode() {
		return left;
	}
	
	public Node getRightNode() {
		return right;
	}
	
	public byte[] getMerkleHash() {
		return hash;
	}
	
	public int getCoins() {
		if (isLeaf()) {
			return stakeholder.getCoins();
		}
		return left.getCoins() + right.getCoins();
	}
}
