package gutta.apievolution.cobol.recordGen;

import gutta.apievolution.cobol.recordModel.CobolRecord;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinitionElement;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinitionElementVisitor;
import gutta.apievolution.core.apimodel.provider.ProviderRecordType;

public class ProviderCobolModelBuilder extends CobolModelBuilder
implements ProviderApiDefinitionElementVisitor<CobolRecord> {
	
	ProviderCobolModelBuilder(int minLevelNr, int levelNrIncrement) {
		super(minLevelNr, levelNrIncrement);
	}

	public void build(ProviderApiDefinitionElement element) {
        element.accept(this);
    }
	
	@Override
	public CobolRecord handleProviderRecordType(ProviderRecordType recordType) {
		return this.createGroupRecord(recordType.getInternalName(), recordType);
	}
	
}
