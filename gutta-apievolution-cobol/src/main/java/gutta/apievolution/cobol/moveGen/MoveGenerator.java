package gutta.apievolution.cobol.moveGen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinition;
import gutta.apievolution.core.apimodel.provider.RevisionHistory;

public class MoveGenerator {
	
	private MoveModelBuilder builder;
	
	private VelocityEngine velocityEngine;
	private VelocityContext context;
	
	public MoveGenerator() {
		Properties properties = new Properties();
        properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        properties.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        this.velocityEngine = new VelocityEngine();
        this.velocityEngine.init(properties);
        
        this.context = new VelocityContext();
        
        this.builder = new MoveModelBuilder();
	}
	
	public void generateOutputMoves(RevisionHistory providerRevisionHistory
			, Set<Integer> supportedRevisions,ConsumerApiDefinition consumerApi
			, String recordName, File outputFile) throws UnresolvableNameException, IOException {
		
		this.builder.buildMoves(providerRevisionHistory
				, supportedRevisions, consumerApi
				, recordName, outputFile);
		
		this.context.put("moves", builder.getOutputMoves());
		this.context.put("direction", "out");
		this.printMoves(outputFile);
	}
	
	public void generateInputMoves(RevisionHistory providerRevisionHistory
			, Set<Integer> supportedRevisions,ConsumerApiDefinition consumerApi
			, String recordName, File outputFile) throws UnresolvableNameException, IOException {
		
		this.builder.buildMoves(providerRevisionHistory
				, supportedRevisions, consumerApi
				, recordName, outputFile);
		
		this.context.put("moves", builder.getInputMoves());
		this.context.put("direction", "in");
		this.printMoves(outputFile);
	}

	private void printMoves(File outputFile) throws IOException {
		try (FileWriter writer = new FileWriter(outputFile)) {
        	this.velocityEngine.mergeTemplate("cobol/GenerateMoves.vt", "UTF-8", this.context, writer);
        }
	}

}
