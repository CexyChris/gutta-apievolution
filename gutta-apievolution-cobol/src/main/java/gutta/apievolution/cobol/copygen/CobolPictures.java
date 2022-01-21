package gutta.apievolution.cobol.copygen;

public enum CobolPictures {
	
	ALPHANUMERIC('X'),
	ALPHABETIC('A'),
	NUMERIC('9');
	
	private final char pic;
	
	private CobolPictures(char pic) {
		this.pic = pic;
	}
	
	@Override
	public String toString() {
		return String.valueOf(pic);
	}

}
