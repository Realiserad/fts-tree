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
