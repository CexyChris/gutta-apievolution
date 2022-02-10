package gutta.apievolution.cobol.copygen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import gutta.apievolution.cobol.recordGen.ConsumerCobolRecordGenerator;
import gutta.apievolution.cobol.recordGen.ProviderCobolRecordGenerator;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinition;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinition;
import gutta.apievolution.core.util.IntegerRange;
import gutta.apievolution.dsl.ConsumerApiLoader;
import gutta.apievolution.dsl.ProviderApiLoader;

class CopyGeneratorTest {
	
	@Test
	void providerTest() throws IOException {
		ClassLoader classLoader = this.getClass().getClassLoader();
        List<InputStream> streams = Stream.of("apis/Kunde-provider-revision-1.api",
                "apis/Kunde-provider-revision-2.api")
                .map(name -> classLoader.getResourceAsStream(name))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
		List<ProviderApiDefinition> apiDefinitions 
		= ProviderApiLoader.loadHistoryFromStreams(IntegerRange.unbounded(), streams);
		
		File file = new File("myProviderCopy.cbl");
		ProviderCobolRecordGenerator generator = new ProviderCobolRecordGenerator();
		generator.generateCopy(apiDefinitions, "Kunde", file);
		
	}
	
	@Test
	void test() throws IOException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("apis/Kunde-consumer-revision-1.api");
        ConsumerApiDefinition consumerApi = ConsumerApiLoader.loadFromStream(inputStream, 0);
		
		File file = new File("myConsumerCopy.cbl");
		ConsumerCobolRecordGenerator generator = new ConsumerCobolRecordGenerator();
		generator.generateCopy(consumerApi, "Kunde", file);
		
	}

}
