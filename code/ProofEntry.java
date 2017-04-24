import javax.xml.bind.DatatypeConverter;

/**
 * A class containing an entry (x1, x2, hash) in a Merkle proof, where
 * "x1" is the number of coins in the left subtree, "x2" is the number
 * of coins the right subtree and "hash" is the Merkle hash 
 * H(left hash | right hash | x1 | x2).
 */
public class ProofEntry {
	private final byte[] hash;
	private final int x1, x2;
	
	public ProofEntry(byte[] hash, int x1, int x2) {
		this.hash = hash;
		this.x1 = x1;
		this.x2 = x2;
	}
	
	public int getLeftBound() {
		return x1;
	}
	
	public int getRightBound() {
		return x2;
	}
	
	public byte[] getMerkleHash() {
		return hash;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", 
				DatatypeConverter.printHexBinary(hash), 
				x1, 
				x2);
	}
}
