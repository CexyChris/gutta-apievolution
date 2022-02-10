package gutta.apievolution.cobol.recordGen;

import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public abstract class CobolRecordGenerator<B extends CobolModelBuilder> {
	
	private VelocityEngine velocityEngine;
	private VelocityContext context;
	private B builder;
	
	CobolRecordGenerator() {			
        Properties properties = new Properties();
        properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        properties.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        this.velocityEngine = new VelocityEngine();
        this.velocityEngine.init(properties);
        
        this.context = new VelocityContext();
	}
	
	void initContext(String recordName) {
        this.getContext().put("root", this.builder.getForName(recordName));
        this.getContext().put("groupRecordMap", this.builder.getKnownGroupRecords());
        this.getContext().put("elementaryRecordMap", this.builder.getKnownElementaryRecords());
	}

	VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	VelocityContext getContext() {
		return context;
	}
	
	B getBuilder() {
		return this.builder;
	}
	
	void setBuilder(B builder) {
		this.builder = builder;
	}
	
}
