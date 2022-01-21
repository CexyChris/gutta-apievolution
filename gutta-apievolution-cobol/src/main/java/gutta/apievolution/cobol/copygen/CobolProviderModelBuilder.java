package gutta.apievolution.cobol.copygen;

import gutta.apievolution.core.apimodel.provider.ProviderApiDefinitionElement;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinitionElementVisitor;
import gutta.apievolution.core.apimodel.provider.ProviderRecordType;

public class CobolProviderModelBuilder extends AbstractCobolModelBuilder
implements ProviderApiDefinitionElementVisitor<CobolRecord> {
	
	CobolProviderModelBuilder(int minLevelNr, int levelNrIncrement) {
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
