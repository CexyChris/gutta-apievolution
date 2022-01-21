package gutta.apievolution.cobol.copygen;

import java.io.File;
import java.util.List;
import java.util.Set;

import gutta.apievolution.core.apimodel.Type;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinition;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinition;
import gutta.apievolution.core.apimodel.provider.RevisionHistory;
import gutta.apievolution.core.resolution.DefinitionResolution;
import gutta.apievolution.core.resolution.DefinitionResolver;

public class MoveGenerator {
	
	//Ausgabe und Eingabe muss unterschieden werden:
	//Fuer Ausgaben muessen wir nur die Consumer-Felder mappen,
	//Fuer Eingaben muessen wir nur die Provider-Felder mappen 
	public static void generateOutputMoves(List<ProviderApiDefinition> supportedDefinitions
			, Set<Integer> supportedRevisions, ConsumerApiDefinition consumerApi
			, String recordName, File outputFile) throws Exception {
		
		RevisionHistory revisionHistory = new RevisionHistory(supportedDefinitions);
		DefinitionResolution definitionResolution = new DefinitionResolver()
				.resolveConsumerDefinition(revisionHistory, supportedRevisions, consumerApi);
		Type consumerType = definitionResolution.resolveConsumerType(recordName);
		if(consumerType == null) {
			throw new Exception("Der Typ " + recordName + " existiert nicht!");
		}
		
		//TODO: Braucht es eine MOVE-Klasse? Können Namen noch kollidieren?
	}

}
