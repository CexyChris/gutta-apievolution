package gutta.apievolution.cobol.copygen;

public class NoMappedTypeException extends Exception {

	public NoMappedTypeException(String recordName) {
		super(String.format("Der Typ %s wird nicht abgebildet!", recordName));
	}

	private static final long serialVersionUID = -1505244840464874660L;

}
