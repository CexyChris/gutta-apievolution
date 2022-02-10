package gutta.apievolution.cobol.moveGen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import gutta.apievolution.core.apimodel.BoundedStringType;
import gutta.apievolution.core.apimodel.Field;
import gutta.apievolution.core.apimodel.NumericType;
import gutta.apievolution.core.apimodel.RecordType;
import gutta.apievolution.core.apimodel.Type;
import gutta.apievolution.core.apimodel.TypeVisitor;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinition;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinitionElement;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinitionElementVisitor;
import gutta.apievolution.core.apimodel.consumer.ConsumerField;
import gutta.apievolution.core.apimodel.consumer.ConsumerRecordType;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinitionElement;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinitionElementVisitor;
import gutta.apievolution.core.apimodel.provider.ProviderField;
import gutta.apievolution.core.apimodel.provider.ProviderRecordType;
import gutta.apievolution.core.apimodel.provider.RevisionHistory;
import gutta.apievolution.core.resolution.DefinitionResolution;
import gutta.apievolution.core.resolution.DefinitionResolver;

class MoveModelBuilder implements TypeVisitor<Consumer<Field>>, ProviderApiDefinitionElementVisitor<Void> {
	
	private DefinitionResolution definitionResolution;
	private List<MoveStatement> outputMoves;
	private List<MoveStatement> inputMoves;
	
	public List<MoveStatement> getOutputMoves() {
		return outputMoves;
	}

	public List<MoveStatement> getInputMoves() {
		return inputMoves;
	}

	void buildMoves(RevisionHistory revisionHistory
			, Set<Integer> supportedRevisions, ConsumerApiDefinition consumerApi
			, String recordName, File outputFile) throws UnresolvableNameException {
		
		this.outputMoves = new ArrayList<MoveStatement>();
		
		this.definitionResolution = new DefinitionResolver()
				.resolveConsumerDefinition(revisionHistory, supportedRevisions, consumerApi);
		Type providerType = this.definitionResolution.resolveProviderType(recordName);
		if(providerType == null) {
			throw new UnresolvableNameException(recordName);
		}
		ProviderApiDefinitionElement pade = (ProviderApiDefinitionElement) providerType;
		pade.accept(this);
		
		this.inputMoves = new ArrayList<MoveStatement>();
		this.outputMoves.stream()
			.map(ms -> new MoveStatement(ms.getTo(), ms.getFrom()))
			.forEach(ms -> this.inputMoves.add(ms));
	}
	
	private void processGroupRecord(RecordType<?, ?, ?> recordType) {
		for(Field providerField: recordType.getDeclaredFields()) {
			ConsumerField consumerField = 
					this.definitionResolution.mapProviderField((ProviderField) providerField);
			if (consumerField != null) { //if there is no mapping, there is nothing to do.
				Consumer<Field> action = providerField.getType().accept(this);
				action.accept(providerField);
			}
		}
	}
	
	@Override
	public Consumer<Field> handleRecordType(RecordType<?, ?, ?> recordType) {
		return f -> this.processGroupRecord((RecordType) f.getType());
	}
	
	@Override
	public Consumer<Field> handleNumericType(NumericType numericType) {
		return this.addMoveStatement();
	}
	
	@Override
	public Consumer<Field> handleBoundedStringType(BoundedStringType boundedStringType) {
		return this.addMoveStatement();
	}

	private Consumer<Field> addMoveStatement() {
		return f -> this.outputMoves.add(
				new MoveStatement(f.getInternalName()
						, this.definitionResolution.mapProviderField((ProviderField) f).getPublicName()));
	}
	
	
	@Override
	public Void handleProviderRecordType(ProviderRecordType recordType) {
		this.processGroupRecord((RecordType) recordType);
		return null;
	}
	
	
	

}
