package gutta.apievolution.cobol.recordModel;

import java.util.ArrayList;
import java.util.List;

public abstract class ElementaryCobolRecord extends CobolRecord {
	
	private CobolPictures picture;
	private int           length;
	private UsageClause   usage;
	private List<ConditionName> conditionNames;
	private String value;

	
	public ElementaryCobolRecord(String name, int levelNr, 
			CobolPictures picture, int length, UsageClause usage) {
		super(name, levelNr, false);
		this.picture = picture;
		this.length = length;
		this.usage = usage;
		this.conditionNames = new ArrayList<ConditionName>();
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValueClause() {
		if (this.value == null) {
			return "";
		} else {
			return String.format("%s %s%", CobolConstants.SHORTHAND_VALUE_CLAUSE, this.value);
		}
	}
	
	public boolean hasConditionNames() {
		return ! this.conditionNames.isEmpty();
	}
	
	public boolean addCondistionName(ConditionName condition) {
		return this.conditionNames.add(condition);
	}
	
	public List<ConditionName> getConditionNames() {
		return this.conditionNames;
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
