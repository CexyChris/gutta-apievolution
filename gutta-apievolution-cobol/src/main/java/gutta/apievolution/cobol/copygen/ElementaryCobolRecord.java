package gutta.apievolution.cobol.copygen;

public abstract class ElementaryCobolRecord extends CobolRecord {
	
	private CobolPictures picture;
	private int           length;
	private UsageClause   usage;

	
	public ElementaryCobolRecord(String name, int levelNr, 
			CobolPictures picture, int length, UsageClause usage) {
		super(name, levelNr, false);
		this.picture = picture;
		this.length = length;
		this.usage = usage;
	}
	
	public String getPictureClause() {
		return String.format("%s %s(%d)",CobolConstants.SHORTHAND_PICTURE_CLAUSE, this.picture.toString(), this.length);
	}
	
	public CobolPictures getPicture() {
		return this.picture;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public UsageClause getUsage() {
		return this.usage;
	}
	
	

}
