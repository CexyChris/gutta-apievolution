package gutta.apievolution.cobol.copygen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import gutta.apievolution.core.apimodel.provider.ModelMerger;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinition;
import gutta.apievolution.core.apimodel.provider.RevisionHistory;

public class ProviderCopyGenerator extends CopyGenerator {
	
	public void generateCopy(List<ProviderApiDefinition> supportedDefinitions, String recordName, File outputFile) throws IOException {
		RevisionHistory revisionHistory = new RevisionHistory(supportedDefinitions);
		ProviderApiDefinition mergedDefinition = new ModelMerger().createMergedDefinition(revisionHistory);
		
		CobolProviderModelBuilder builder = new CobolProviderModelBuilder(5, 5);

		mergedDefinition.forEach(u -> builder.build(u));
		this.initContext(recordName, builder);
		
        try (FileWriter writer = new FileWriter(outputFile)) {
        	this.getVelocityEngine().mergeTemplate("cobol/GenerateRecords.vt", "UTF-8", this.getContext(), writer);
        }
        
	}

}
