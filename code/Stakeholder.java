/**
 * A stakeholder in the Merkle tree. Each stakeholder contains a name, a number 
 * of coins and an audit path which determines the position of the stakeholder
 * in the stake tree. The audit path is a binary string which should be 
 * interpreted as the position of the leaf node counted from the left. For 
 * example "100" or decimal 4, means the stakeholder is the 4th stakeholder 
 * in the tree.
 */
public class Stakeholder {
	private final String name;
	private final int coins;
	private final String auditPath;
	
	public Stakeholder(String name, int coins, String auditPath) {
		this.name = name;
		this.coins = coins;
		this.auditPath = auditPath;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCoins() {
		return coins;
	}
	
	public byte[] toBytes() {
		return String.format("%s%d%s", name, coins, auditPath).
				getBytes(StandardCharsets.US_ASCII);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public String getAuditPath() {
		return auditPath;
	}
}
