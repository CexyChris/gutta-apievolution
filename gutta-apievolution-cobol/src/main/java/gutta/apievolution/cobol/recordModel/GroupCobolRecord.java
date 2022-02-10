package gutta.apievolution.cobol.recordModel;

import java.util.ArrayList;
import java.util.List;

public class GroupCobolRecord extends CobolRecord {

	public GroupCobolRecord(String recordName, int levelNr) {
		super(recordName, levelNr, true);
		this.groupItems = new ArrayList<CobolRecord>();
	}
	
	private List<CobolRecord> groupItems;
	
	public boolean addItem(CobolRecord item) {
		//TODO: check levelNr to avoid cycles (?).
		return this.groupItems.add(item);
	}
	
	public List<CobolRecord> getGroupItems() {
		return this.groupItems;
	}
	
	@Override
	public void increaseLevelNr(int increment) {
		super.increaseLevelNr(increment);
		this.groupItems.forEach(i -> i.increaseLevelNr(increment));
	}
}
