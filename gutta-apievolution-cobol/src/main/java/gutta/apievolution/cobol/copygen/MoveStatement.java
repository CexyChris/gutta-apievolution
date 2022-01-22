package gutta.apievolution.cobol.copygen;

import java.util.Objects;

public class MoveStatement {
	
	private String from;
	private String to;
	
	public MoveStatement(String from, String to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MoveStatement other = (MoveStatement) obj;
		return Objects.equals(from, other.from) && Objects.equals(to, other.to);
	}
	
	@Override
	public String toString() {
		return String.format("%s %s %s %s", CobolConstants.MOVE, this.from, CobolConstants.TO, this.to);
	}
}
