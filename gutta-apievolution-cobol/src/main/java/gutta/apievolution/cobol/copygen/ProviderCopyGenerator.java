package gutta.apievolution.cobol.copygen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import gutta.apievolution.core.apimodel.provider.ModelMerger;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinition;
import gutta.apievolution.core.apimodel.provider.RevisionHistory;

public class ProviderCopyGenerator {
	
	public static void generateCopies(List<ProviderApiDefinition> supportedDefinitions, String recordName, File outputFile) throws IOException {
		RevisionHistory revisionHistory = new RevisionHistory(supportedDefinitions);
		ProviderApiDefinition mergedDefinition = new ModelMerger().createMergedDefinition(revisionHistory);
		
		CobolModelBuilder builder = new CobolModelBuilder(5, 5);

		mergedDefinition.forEach(u -> builder.build(u));
		
        Properties properties = new Properties();
        properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        properties.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init(properties);
        
        VelocityContext context = new VelocityContext();
        context.put("root", builder.getForName(recordName));
        context.put("groupRecordMap", builder.getKnownGroupRecords());
        context.put("elementaryRecordMap", builder.getKnownElementaryRecords());
        try (FileWriter writer = new FileWriter(outputFile)) {
        	velocityEngine.mergeTemplate("cobol/GenerateRecords.vt", "UTF-8", context, writer);
        }
        
	}

}
