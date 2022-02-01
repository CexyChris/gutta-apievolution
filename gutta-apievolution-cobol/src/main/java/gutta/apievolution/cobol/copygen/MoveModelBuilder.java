package gutta.apievolution.cobol.copygen;

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
import gutta.apievolution.core.apimodel.provider.ProviderField;
import gutta.apievolution.core.apimodel.provider.RevisionHistory;
import gutta.apievolution.core.resolution.DefinitionResolution;
import gutta.apievolution.core.resolution.DefinitionResolver;

class MoveModelBuilder implements TypeVisitor<Consumer<Field>>, ConsumerApiDefinitionElementVisitor<Void> {
	
	private DefinitionResolution definitionResolution;
	private List<MoveStatement> outputMoves;
	private List<MoveStatement> inputMoves;
	
	public List<MoveStatement> getOutputMoves() {
		return outputMoves;
	}

	public List<MoveStatement> getInputMoves() {
		return inputMoves;
	}

	public void generateMoves(RevisionHistory revisionHistory
			, Set<Integer> supportedRevisions, ConsumerApiDefinition consumerApi
			, String recordName, File outputFile) throws NoMappedTypeException {
		
		this.outputMoves = new ArrayList<MoveStatement>();
		
		this.definitionResolution = new DefinitionResolver()
				.resolveConsumerDefinition(revisionHistory, supportedRevisions, consumerApi);
		Type consumerType = this.definitionResolution.resolveConsumerType(recordName);
		if(consumerType == null) {
			throw new NoMappedTypeException(recordName);
		}
		ConsumerApiDefinitionElement cade = (ConsumerApiDefinitionElement) consumerType;
		cade.accept(this);
		
		this.inputMoves = new ArrayList<MoveStatement>();
		this.outputMoves.stream()
			.map(ms -> new MoveStatement(ms.getTo(), ms.getFrom()))
			.forEach(ms -> this.inputMoves.add(ms));
	}
	
	private void processGroupRecord(RecordType<?, ?, ?> recordType) {
		for(Field consumerField: recordType.getDeclaredFields()) {
			ProviderField providerField = 
					this.definitionResolution.mapConsumerField((ConsumerField) consumerField);
			if (providerField != null) { //if there is no mapping, there is nothing to do.
				Consumer<Field> action = consumerField.getType().accept(this);
				action.accept(consumerField);
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
				new MoveStatement(f.getPublicName()
						, this.definitionResolution.mapConsumerField((ConsumerField) f).getPublicName()));
	}
	
	
	@Override
	public Void handleConsumerRecordType(ConsumerRecordType recordType) {
		this.processGroupRecord((RecordType) recordType);
		return null;
	}
	
	
	

}
