import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.xml.bind.DatatypeConverter;

public class Fts {
	public static void main(String[] args) {
		new Fts();
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
	
	public Fts() {
		// Create some stakeholders
		List<Stakeholder> stakeholders = new ArrayList<>();
		for (int i = 0, c = 20; i < 8; i++, c = c % 2 == 0 ? c / 2 : c*3 + 1) {
			final String name = String.format("Stakeholder %d", i);
			final String auditPath = Integer.toBinaryString(i);
			final int coins = c;
			stakeholders.add(new Stakeholder(name, coins, auditPath));
		}
		// Create the Merkle tree
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
