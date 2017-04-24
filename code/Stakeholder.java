import java.nio.charset.StandardCharsets;

/**
 * A stakeholder in the Merkle tree. Each stakeholder has a name,
 * and controls an amount of coins.
 */
public class Stakeholder {
	private final String name;
	private final int coins;
	
	public Stakeholder(String name, int coins) {
		this.name = name;
		this.coins = coins;
	}
	
	public String getName() {
		return name;
	}
	
	public int getCoins() {
		return coins;
	}
	
	public byte[] toBytes() {
		return String.format("%s%d", name, coins).
				getBytes(StandardCharsets.US_ASCII);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
