package gutta.apievolution.cobol.copygen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

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

public class MoveGenerator implements TypeVisitor<Consumer<Field>>, ConsumerApiDefinitionElementVisitor<Void> {
	
	private DefinitionResolution definitionResolution;
	private List<MoveStatement> outputMoves;
	
	
	//Ausgabe und Eingabe muss unterschieden werden:
	//Fuer Ausgaben muessen wir nur die Consumer-Felder mappen,
	//Fuer Eingaben muessen wir nur die Provider-Felder mappen 
	public void generateOutputMoves(RevisionHistory revisionHistory
			, Set<Integer> supportedRevisions, ConsumerApiDefinition consumerApi
			, String recordName, File outputFile) throws Exception {
		
		this.outputMoves = new ArrayList<MoveStatement>();
		
		this.definitionResolution = new DefinitionResolver()
				.resolveConsumerDefinition(revisionHistory, supportedRevisions, consumerApi);
		Type consumerType = this.definitionResolution.resolveConsumerType(recordName);
		if(consumerType == null) {
			throw new Exception("Der Typ " + recordName + " existiert nicht!");
		}
		ConsumerApiDefinitionElement cade = (ConsumerApiDefinitionElement) consumerType;
		cade.accept(this);
		
		this.outputMoves.forEach(m -> System.out.println(m.toString()));
	}
	
	private void processGroupRecord(RecordType<?, ?, ?> recordType) {
		for(Field consumerField: recordType.getDeclaredFields()) {
			ProviderField providerField = 
					this.definitionResolution.mapConsumerField((ConsumerField) consumerField);
			System.out.println(providerField);
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
