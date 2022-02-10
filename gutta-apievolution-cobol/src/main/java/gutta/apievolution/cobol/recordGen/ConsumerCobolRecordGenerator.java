package gutta.apievolution.cobol.recordGen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinition;

public class ConsumerCobolRecordGenerator extends CobolRecordGenerator<ConsumerCobolModelBuilder> {
	
	public ConsumerCobolRecordGenerator() {
		super();
		this.setBuilder(new ConsumerCobolModelBuilder(5, 5));
	}
	
	public void generateCopy(ConsumerApiDefinition consumerApi, String recordName, File outputFile) throws IOException {
		
		consumerApi.forEach(u -> this.getBuilder().build(u));
		this.initContext(recordName);
		
        try (FileWriter writer = new FileWriter(outputFile)) {
        	this.getVelocityEngine().mergeTemplate("cobol/GenerateRecords.vt", "UTF-8", this.getContext(), writer);
        }
	}

}
