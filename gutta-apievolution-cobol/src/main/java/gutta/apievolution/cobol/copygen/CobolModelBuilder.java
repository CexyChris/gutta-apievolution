package gutta.apievolution.cobol.copygen;

import java.util.HashMap;
import java.util.Map;

import gutta.apievolution.core.apimodel.BoundedStringType;
import gutta.apievolution.core.apimodel.NumericType;
import gutta.apievolution.core.apimodel.RecordType;
import gutta.apievolution.core.apimodel.TypeVisitor;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinitionElement;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinitionElementVisitor;
import gutta.apievolution.core.apimodel.provider.ProviderField;
import gutta.apievolution.core.apimodel.provider.ProviderRecordType;

public class CobolModelBuilder implements ProviderApiDefinitionElementVisitor<CobolRecord>, TypeVisitor<CobolRecord> {
	
	private Map<String, GroupCobolRecord> knownGroupRecords;
	private Map<String, ElementaryCobolRecord> knownElementaryRecords;
	private int LevelNr;
	private final int levelNrIncrement;
	private String currentName;
	
	CobolModelBuilder(int minLevelNr, int levelNrIncrement) {
		this.knownGroupRecords = new HashMap<String, GroupCobolRecord>();
		this.knownElementaryRecords = new HashMap<String, ElementaryCobolRecord>();
		this.LevelNr = minLevelNr;
		this.levelNrIncrement = levelNrIncrement;
	}
	
	public void build(ProviderApiDefinitionElement element) {
        element.accept(this);
    }
	
	public GroupCobolRecord getForName(String name) {
		return this.knownGroupRecords.get(name);
	}
	
	
	@Override
	public CobolRecord handleProviderRecordType(ProviderRecordType recordType) {
//		CobolRecord cached = this.knownGroupRecords.get(recordType.getInternalName());
//		if (cached != null) {
//			cached.increaseLevelNr(this.levelNrIncrement);
//			return cached;
//		}
		GroupCobolRecord groupCobolRecord = new GroupCobolRecord(recordType.getInternalName(), this.LevelNr);
		this.LevelNr += this.levelNrIncrement;
		for(ProviderField providerField : recordType.getDeclaredFields()) {
			this.currentName = providerField.getInternalName();
			CobolRecord subordinateItem = providerField.getType().accept(this);
			groupCobolRecord.addItem(subordinateItem);
		}
		this.LevelNr -= this.levelNrIncrement;
		this.knownGroupRecords.put(recordType.getInternalName(), groupCobolRecord);
		return groupCobolRecord;
	}
	
	
	@Override
	public CobolRecord handleNumericType(NumericType numericType) {
		int length = numericType.getIntegerPlaces() + numericType.getFractionalPlaces();
		ElementaryCobolRecord elem = 
				new NumericCobolRecord(this.currentName, this.LevelNr, length, UsageClause.DISPLAY);
		this.knownElementaryRecords.put(this.currentName, elem);
		return elem;
	}
	
	@Override
	public CobolRecord handleBoundedStringType(BoundedStringType boundedStringType) {
		int length = boundedStringType.getBound();
		ElementaryCobolRecord elem = 
				new AlphanumericCobolRecord(this.currentName, this.LevelNr, length);
		this.knownElementaryRecords.put(this.currentName, elem);
		return elem;
	}
	
	@Override
	public CobolRecord handleRecordType(RecordType<?, ?, ?> recordType) {
		return this.handleProviderRecordType((ProviderRecordType) recordType);
	}

	public Map<String, GroupCobolRecord> getKnownGroupRecords() {
		return this.knownGroupRecords;
	}

	public Map<String, ElementaryCobolRecord> getKnownElementaryRecords() {
		return this.knownElementaryRecords;
	}	

}
