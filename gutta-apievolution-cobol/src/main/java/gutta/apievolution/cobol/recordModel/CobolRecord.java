package gutta.apievolution.cobol.recordModel;

import java.util.Objects;

public abstract class CobolRecord {
	
	private String  recordName;
	private int     levelNr;
	private boolean groupRecord;
	
	public CobolRecord(String recordName, int levelNr, boolean groupRecord) {
		this.recordName = recordName;
		this.levelNr = levelNr;
		this.groupRecord = groupRecord;
	}

	public String getRecordName() {
		return recordName;
	}

	public int getLevelNr() {
		return levelNr;
	}

	public boolean isGroupRecord() {
		return groupRecord;
	}
	
	public void increaseLevelNr(int increment) {
		this.levelNr += increment;
	}
	
	public String getTwoDigitLevelNr() {
		return String.format("%02d", this.levelNr);
	}

	@Override
	public int hashCode() {
		return Objects.hash(groupRecord, levelNr, recordName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CobolRecord other = (CobolRecord) obj;
		return levelNr == other.levelNr && Objects.equals(recordName, other.recordName);
	}
	
	
	
	
	
}
