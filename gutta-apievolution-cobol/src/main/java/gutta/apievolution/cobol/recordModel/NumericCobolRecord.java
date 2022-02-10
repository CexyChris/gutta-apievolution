package gutta.apievolution.cobol.recordModel;

public class NumericCobolRecord extends ElementaryCobolRecord {

	public NumericCobolRecord(String name, int levelNr, int length, UsageClause usage) {
		super(name, levelNr, CobolPictures.NUMERIC, length, usage);
	}
	
}
