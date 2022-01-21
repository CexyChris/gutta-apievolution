package gutta.apievolution.cobol.copygen;

import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinitionElement;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinitionElementVisitor;
import gutta.apievolution.core.apimodel.consumer.ConsumerRecordType;

public class CobolConsumerModelBuilder extends AbstractCobolModelBuilder implements ConsumerApiDefinitionElementVisitor<CobolRecord> {

	CobolConsumerModelBuilder(int minLevelNr, int levelNrIncrement) {
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
