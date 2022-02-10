package gutta.apievolution.cobol.moveGen;

public class UnresolvableNameException extends Exception {

	public UnresolvableNameException(String recordName) {
		super(String.format("Der Typ %s kann nicht aufgeloest werden!", recordName));
	}

	private static final long serialVersionUID = -1505244840464874660L;

}
