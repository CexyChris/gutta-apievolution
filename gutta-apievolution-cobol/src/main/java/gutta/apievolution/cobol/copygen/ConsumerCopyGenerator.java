package gutta.apievolution.cobol.copygen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinition;

public class ConsumerCopyGenerator extends CopyGenerator {
	
	public void generateCopy(ConsumerApiDefinition consumerApi, String recordName, File outputFile) throws IOException {
		
		CobolConsumerModelBuilder builder = new CobolConsumerModelBuilder(5, 5);

		consumerApi.forEach(u -> builder.build(u));
		this.initContext(recordName, builder);
		
        try (FileWriter writer = new FileWriter(outputFile)) {
        	this.getVelocityEngine().mergeTemplate("cobol/GenerateRecords.vt", "UTF-8", this.getContext(), writer);
        }
        
		
	}

}
