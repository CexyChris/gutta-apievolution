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

class GenerateRecordsForEvalExample {
	
	private List<ProviderApiDefinition> createDefs(String... apiDefs) {
		ClassLoader classLoader = this.getClass().getClassLoader();
        List<InputStream> streams = Stream.of(apiDefs)
                .map(name -> classLoader.getResourceAsStream(name))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
		return ProviderApiLoader.loadHistoryFromStreams(IntegerRange.unbounded(), streams);
	}

	@Test
	void testGenerateKundeConsumerR1() throws IOException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("apis/Kunde-consumer-revision-1.api");
        ConsumerApiDefinition consumerApi = ConsumerApiLoader.loadFromStream(inputStream, 0);
		
		File file = new File("Kunde-Consumer.cpy");
		ConsumerCobolRecordGenerator generator = new ConsumerCobolRecordGenerator();
		generator.generateCopy(consumerApi, "Kunde", file);
	}
	
	@Test
	void testGenerateZahlungConsumerR1() throws IOException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("apis/Zahlung-consumer-revision-1.api");
        ConsumerApiDefinition consumerApi = ConsumerApiLoader.loadFromStream(inputStream, 0);
		
		File file = new File("Zahlung-Consumer.cpy");
		ConsumerCobolRecordGenerator generator = new ConsumerCobolRecordGenerator();
		generator.generateCopy(consumerApi, "Zahlung", file);
	}
	
	@Test
	void testGenerateKundeProviderR1() throws IOException {
		List<ProviderApiDefinition> apiDefinitions = this.createDefs("apis/Kunde-provider-revision-1.api");
		
		File file = new File("Kunde-provider-revision-1-intern.cpy");
		ProviderCobolRecordGenerator generator = new ProviderCobolRecordGenerator();
		generator.generateCopy(apiDefinitions, "Kunde", file);
	}
	
	@Test
	void testGenerateKundeProviderR2Intern() throws IOException {
		List<ProviderApiDefinition> apiDefinitions = this.createDefs("apis/Kunde-provider-revision-1.api",
                "apis/Kunde-provider-revision-2.api");
		
		File file = new File("Kunde-provider-revision-2-intern.cpy");
		ProviderCobolRecordGenerator generator = new ProviderCobolRecordGenerator();
		generator.generateCopy(apiDefinitions, "Kunde", file);
	}
	
	/**
	 * Note: Revision2 is no longer supported!
	 * @throws IOException
	 */
	@Test
	void testGenerateKundeProviderR3Intern() throws IOException {
		List<ProviderApiDefinition> apiDefinitions = this.createDefs("apis/Kunde-provider-revision-1.api",
                "apis/Kunde-provider-revision-3.api");
		
		File file = new File("Kunde-provider-revision-3-intern.cpy");
		ProviderCobolRecordGenerator generator = new ProviderCobolRecordGenerator();
		generator.generateCopy(apiDefinitions, "Kunde", file);
	}
	
	@Test
	void testGenerateZahlungProviderR1() throws IOException {
		List<ProviderApiDefinition> apiDefinitions = this.createDefs("apis/Zahlung-provider-revision-1.api");
		
		File file = new File("Zahlung-provider-revision-1-intern.cpy");
		ProviderCobolRecordGenerator generator = new ProviderCobolRecordGenerator();
		generator.generateCopy(apiDefinitions, "Zahlung", file);
	}
	
	@Test
	void testGenerateZahlungProviderR2Intern() throws IOException {
		List<ProviderApiDefinition> apiDefinitions = this.createDefs("apis/Zahlung-provider-revision-1.api",
                "apis/Zahlung-provider-revision-2.api");
		
		File file = new File("Zahlung-provider-revision-2-intern.cpy");
		ProviderCobolRecordGenerator generator = new ProviderCobolRecordGenerator();
		generator.generateCopy(apiDefinitions, "Zahlung", file);
	}

}
