package gutta.apievolution.cobol.recordGen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import gutta.apievolution.core.apimodel.provider.ModelMerger;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinition;
import gutta.apievolution.core.apimodel.provider.RevisionHistory;

public class ProviderCobolRecordGenerator extends CobolRecordGenerator<ProviderCobolModelBuilder> {
	
	public ProviderCobolRecordGenerator() {
		super();
		this.setBuilder(new ProviderCobolModelBuilder(5, 5));
	}
	
	public void generateCopy(List<ProviderApiDefinition> supportedDefinitions, String recordName, File outputFile) throws IOException {
		RevisionHistory revisionHistory = new RevisionHistory(supportedDefinitions);
		ProviderApiDefinition mergedDefinition = new ModelMerger().createMergedDefinition(revisionHistory);
		mergedDefinition.forEach(u -> this.getBuilder().build(u));
		this.initContext(recordName);
		
        try (FileWriter writer = new FileWriter(outputFile)) {
        	this.getVelocityEngine().mergeTemplate("cobol/GenerateRecords.vt", "UTF-8", this.getContext(), writer);
        }
        
	}

}
