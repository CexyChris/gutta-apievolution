package gutta.apievolution.cobol.recordModel;

public class AlphabeticCobolRecord extends ElementaryCobolRecord {

	public AlphabeticCobolRecord(String name, int levelNr, int length) {
		super(name, levelNr, CobolPictures.ALPHABETIC, length, UsageClause.DISPLAY);
	}

}
