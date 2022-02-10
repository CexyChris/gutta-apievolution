package gutta.apievolution.cobol.copygen;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import gutta.apievolution.cobol.moveGen.MoveGenerator;
import gutta.apievolution.cobol.moveGen.UnresolvableNameException;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinition;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinition;
import gutta.apievolution.core.apimodel.provider.RevisionHistory;
import gutta.apievolution.core.util.IntegerRange;
import gutta.apievolution.dsl.ConsumerApiLoader;
import gutta.apievolution.dsl.ProviderApiLoader;

class GenerateMovesForEvalExample {
	
    private RevisionHistory loadRevisionHistory(String... fileNames) throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        List<InputStream> streams = Stream.of(fileNames)
                .map(name -> classLoader.getResourceAsStream(name))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<ProviderApiDefinition> apiDefinitions = ProviderApiLoader.loadHistoryFromStreams(IntegerRange.unbounded(),
                streams);
        RevisionHistory revisionHistory = new RevisionHistory(apiDefinitions);

        for (InputStream stream : streams) {
            stream.close();
        }

        return revisionHistory;
    }

    private ConsumerApiDefinition loadConsumerApi(String fileName, int referencedRevision) throws IOException {
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            return ConsumerApiLoader.loadFromStream(inputStream, referencedRevision);
        }
    }

	@Test
	void testKundeR1OutR1() throws IOException, UnresolvableNameException {
		RevisionHistory providerRevisionHistory = this.loadRevisionHistory("apis/Kunde-provider-revision-1.api");
        ConsumerApiDefinition consumerApi = this.loadConsumerApi("apis/Kunde-consumer-revision-1.api", 0);

        Set<Integer> supportedRevisions = new HashSet<>(Arrays.asList(0));
        
        MoveGenerator generator = new MoveGenerator();
        
        File outMoves = new File("KundeR1OutR1.cpy");
        generator.generateOutputMoves(providerRevisionHistory, supportedRevisions, consumerApi, "Kunde", outMoves);
	}
	
	@Test
	void testKundeR1OutR2() throws IOException, UnresolvableNameException {
		RevisionHistory providerRevisionHistory = this.loadRevisionHistory("apis/Kunde-provider-revision-1.api",
                "apis/Kunde-provider-revision-2.api");
        ConsumerApiDefinition consumerApi = this.loadConsumerApi("apis/Kunde-consumer-revision-1.api", 0);

        Set<Integer> supportedRevisions = new HashSet<>(Arrays.asList(0, 1));
        
        MoveGenerator generator = new MoveGenerator();
        
        File outMoves = new File("KundeR1OutR2.cbl");
        generator.generateOutputMoves(providerRevisionHistory, supportedRevisions, consumerApi, "Kunde", outMoves);
	}
	
	@Test
	void testKundeR1OutR3() throws IOException, UnresolvableNameException {
		RevisionHistory providerRevisionHistory = this.loadRevisionHistory("apis/Kunde-provider-revision-1.api",
                "apis/Kunde-provider-revision-2.api", "apis/Kunde-provider-revision-3.api");
        ConsumerApiDefinition consumerApi = this.loadConsumerApi("apis/Kunde-consumer-revision-1.api", 0);
        //NOTE: Revision 2 is no longer supported!
        Set<Integer> supportedRevisions = new HashSet<>(Arrays.asList(0, 2));
        
        MoveGenerator generator = new MoveGenerator();
        
        File outMoves = new File("KundeR1OutR3.cbl");
        generator.generateOutputMoves(providerRevisionHistory, supportedRevisions, consumerApi, "Kunde", outMoves);
	}
	
	@Test
	void testZahlungR1InR1() throws IOException, UnresolvableNameException {
		RevisionHistory providerRevisionHistory = this.loadRevisionHistory("apis/Zahlung-provider-revision-1.api");
        ConsumerApiDefinition consumerApi = this.loadConsumerApi("apis/Zahlung-consumer-revision-1.api", 0);

        Set<Integer> supportedRevisions = new HashSet<>(Arrays.asList(0));
        
        MoveGenerator generator = new MoveGenerator();
        
        File outMoves = new File("ZahlungR1InR1.cpy");
        generator.generateInputMoves(providerRevisionHistory, supportedRevisions, consumerApi, "Zahlung", outMoves);
	}
	
	@Test
	void testZahlungR1InR2() throws IOException, UnresolvableNameException {
		RevisionHistory providerRevisionHistory = this.loadRevisionHistory("apis/Zahlung-provider-revision-1.api",
				"apis/Zahlung-provider-revision-2.api");
        ConsumerApiDefinition consumerApi = this.loadConsumerApi("apis/Zahlung-consumer-revision-1.api", 0);

        Set<Integer> supportedRevisions = new HashSet<>(Arrays.asList(0, 1));
        
        MoveGenerator generator = new MoveGenerator();
        
        File outMoves = new File("ZahlungR1InR2.cpy");
        generator.generateInputMoves(providerRevisionHistory, supportedRevisions, consumerApi, "Zahlung", outMoves);
	}

}
