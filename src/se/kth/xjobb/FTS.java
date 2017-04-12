package se.kth.xjobb;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class FTS {
	public static void main(String[] args) {
		new FTS();
	}
	
	/**
	 * Computes the message digest SHA2(b1 | b2 | b3 | b4) where
	 * | denotes concatenation.
	 */
	public byte[] SHA2(byte[] b1, byte[] b2, byte[] b3, byte[] b4) {
		try {
			MessageDigest sha2 = MessageDigest.getInstance("SHA-256");
			byte[] b = new byte[b1.length + b2.length + b3.length + b4.length];
			System.arraycopy(b1, 0, b, 0, b1.length);
			System.arraycopy(b2, 0, b, b1.length, b2.length);
			System.arraycopy(b3, 0, b, b1.length + b2.length, b3.length);
			System.arraycopy(b4, 0, b, b1.length + b2.length + b3.length, b4.length);
			return sha2.digest(b);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Computes the message digest SHA2(b).
	 */
	public byte[] SHA2(byte[] b) {
		try {
			MessageDigest sha2 = MessageDigest.getInstance("SHA-256");
			return sha2.digest(b);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * A stakeholder in the stake tree. Each stakeholder has a name, a number 
	 * of coins and fan audit path which determines the position of the stakeholder
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
	
	/**
	 * A class containing the result produced by follow-the-satoshi. 
	 */
	public class FtsResult {
		private final List<ProofEntry> merkleProof;
		private final Stakeholder stakeholder;
		
		public FtsResult(List<ProofEntry> merkleProof, Stakeholder stakeholder) {
			this.merkleProof = merkleProof;
			this.stakeholder = stakeholder;
		}
		
		public Stakeholder getStakeholder() {
			return stakeholder;
		}
		
		public List<ProofEntry> getMerkleProof() {
			return merkleProof;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("merkleProof {\n");
			for (ProofEntry proofEntry : merkleProof) {
				sb.append(String.format("   %s\n", proofEntry.toString()));
			}
			sb.append("}\n");
			sb.append("stakeholder {\n");
			sb.append(String.format("   %s\n", stakeholder.toString()));
			sb.append("}");
			return sb.toString();
		}
	}
	
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
	
	public FTS() {
		// Create some stakeholders
		List<Stakeholder> stakeholders = new ArrayList<>();
		for (int i = 0, c = 20; i < 8; i++, c = c % 2 == 0 ? c / 2 : c*3 + 1) {
			final String name = String.format("Stakeholder %d", i);
			final String auditPath = Integer.toBinaryString(i);
			final int coins = c;
			stakeholders.add(new Stakeholder(name, coins, auditPath));
		}
		// Create the stake tree
		final Node[] tree = createMerkleTree(stakeholders);
		System.out.println("Doing follow-the-satoshi in the stake tree.");
		FtsResult ftsResult = ftsTree(tree, new Random(42));
		System.out.println(ftsResult);
		System.out.println("Verifying the result.");
		ftsVerify(new Random(42), tree[1].getMerkleHash(), ftsResult);
	}
	
	public Node[] createMerkleTree(List<Stakeholder> stakeholders) {
		final Node[] tree = new Node[stakeholders.size() * 2];
		System.out.println(String.format("Creating Merkle tree with %d nodes.", tree.length - 1));
		for (int i = 0; i < stakeholders.size(); i++) {
			tree[stakeholders.size() + i] = new Node(stakeholders.get(i));
		}
		for (int i = stakeholders.size() - 1; i > 0; i--) {
			final Node left = tree[i * 2];
			final Node right = tree[i * 2 + 1];
			final byte[] hash = SHA2(left.getMerkleHash(), 
					right.getMerkleHash(), 
					String.valueOf(left.getCoins()).getBytes(StandardCharsets.US_ASCII), 
					String.valueOf(right.getCoins()).getBytes(StandardCharsets.US_ASCII));
			tree[i] = new Node(left, right, hash);
		}
		for (int i = 1; i < tree.length; i++) {
			System.out.println(String.format("Hash %d: %s", i, DatatypeConverter.printHexBinary(tree[i].getMerkleHash())));
		}
		return tree;
	}
	
	public FtsResult ftsTree(Node[] tree, Random rng) {
		int i = 1;
		List<ProofEntry> merkleProof = new ArrayList<>();
		while (true) {
			if (tree[i].isLeaf()) {
				return new FtsResult(merkleProof, tree[i].getStakeholder());
			}
			final int x1 = tree[i].getLeftNode().getCoins();
			final int x2 = tree[i].getRightNode().getCoins();
			System.out.println(String.format("Left subtree %d coins / right subtree %d coins.", x1, x2));
			final int r = rng.nextInt(x1 + x2) + 1;
			System.out.println(String.format("Picking coin number %d", r));
			if (r <= x1) {
				System.out.println("Choosing left subtree...");
				i *= 2;
				merkleProof.add(new ProofEntry(tree[i + 1].getMerkleHash(), x1, x2));
			} else {
				System.out.println("Choosing right subtree...");
				i = i*2 + 1;
				merkleProof.add(new ProofEntry(tree[i -1].getMerkleHash(), x1, x2));
			}
		}
	}
	
	public boolean ftsVerify(Random rng, byte[] merkleRootHash, FtsResult ftsResult) {
		StringBuilder auditPath = new StringBuilder();
		System.out.print("Checking audit path... ");
		for (ProofEntry proofEntry : ftsResult.getMerkleProof()) {
			final int x1 = proofEntry.getLeftBound();
			final int x2 = proofEntry.getRightBound();
			final int r = rng.nextInt(x1 + x2) + 1;
			if (r <= x1) {
				System.out.print("0 ");
				auditPath.append('0');
			} else {
				System.out.print("1 ");
				auditPath.append('1');
			}
		}
		if (!auditPath.toString().equals(ftsResult.getStakeholder().getAuditPath())) {
			System.out.println(String.format("Not OK! (expected %s)", ftsResult.getStakeholder().getAuditPath()));
			return false;
		}
		System.out.println("OK!");
		byte[] hx = SHA2(ftsResult.getStakeholder().toBytes());
		for (int i = ftsResult.getMerkleProof().size() - 1; i >= 0; i--) {
			final ProofEntry proofEntry = ftsResult.getMerkleProof().get(i);
			final byte[] x1 = String.valueOf(proofEntry.getLeftBound()).getBytes(StandardCharsets.US_ASCII);
			final byte[] x2 = String.valueOf(proofEntry.getRightBound()).getBytes(StandardCharsets.US_ASCII);
			final byte[] hy = proofEntry.getMerkleHash();
			if (ftsResult.getStakeholder().getAuditPath().charAt(i) == '0') {
				hx = SHA2(hx, hy, x1, x2);
			} else {
				hx = SHA2(hy, hx, x1, x2);
			}
			System.out.println(String.format("Next hash: %s", DatatypeConverter.printHexBinary(hx)));
		}
		boolean result = Arrays.equals(hx, merkleRootHash);
		System.out.println(result ? "Root hash matches!" : "Invalid Merkle proof.");
		return result;
	}
}
