package gutta.apievolution.cobol.copygen;

public class AlphanumericCobolRecord extends ElementaryCobolRecord {

	public AlphanumericCobolRecord(String name, int levelNr, int length) {
		super(name, levelNr, CobolPictures.ALPHANUMERIC, length, UsageClause.DISPLAY);
	}

}
