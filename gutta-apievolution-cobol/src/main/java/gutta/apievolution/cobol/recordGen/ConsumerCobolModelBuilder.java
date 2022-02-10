package gutta.apievolution.cobol.recordGen;

import gutta.apievolution.cobol.recordModel.CobolRecord;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinitionElement;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinitionElementVisitor;
import gutta.apievolution.core.apimodel.consumer.ConsumerRecordType;

public class ConsumerCobolModelBuilder extends CobolModelBuilder implements ConsumerApiDefinitionElementVisitor<CobolRecord> {

	ConsumerCobolModelBuilder(int minLevelNr, int levelNrIncrement) {
		super(minLevelNr, levelNrIncrement);
	}
	
	public void build(ConsumerApiDefinitionElement element) {
		element.accept(this);
	}
	
	@Override
	public CobolRecord handleConsumerRecordType(ConsumerRecordType recordType) {
		return this.createGroupRecord(recordType.getInternalName(), recordType);
	}
	
	

}
