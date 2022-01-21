package gutta.apievolution.cobol.copygen;

import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public abstract class CopyGenerator {
	
	private VelocityEngine velocityEngine;
	private VelocityContext context;
	
	CopyGenerator() {			
        Properties properties = new Properties();
        properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        properties.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        this.velocityEngine = new VelocityEngine();
        this.velocityEngine.init(properties);
        
        this.context = new VelocityContext();
	}
	
	void initContext(String recordName, AbstractCobolModelBuilder builder) {
        this.getContext().put("root", builder.getForName(recordName));
        this.getContext().put("groupRecordMap", builder.getKnownGroupRecords());
        this.getContext().put("elementaryRecordMap", builder.getKnownElementaryRecords());
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public VelocityContext getContext() {
		return context;
	}
	
}
