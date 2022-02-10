package gutta.apievolution.cobol.copygen;

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

class MoveGeneratorTest {
	
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
	void OutputMoveTest() throws UnresolvableNameException, IOException {
		RevisionHistory providerRevisionHistory = this.loadRevisionHistory("apis/cobol-provider-revision-1.api",
                "apis/cobol-provider-revision-2.api");
        ConsumerApiDefinition consumerApi = this.loadConsumerApi("apis/cobol-consumer-api.api", 0);

        Set<Integer> supportedRevisions = new HashSet<>(Arrays.asList(0, 1));
        
        MoveGenerator generator = new MoveGenerator();
        
        File outMoves = new File("myOutputMoves.cbl");
        generator.generateOutputMoves(providerRevisionHistory, supportedRevisions, consumerApi, "Kunde", outMoves);
	}
	
	@Test
	void InputMoveTest() throws UnresolvableNameException, IOException {
		RevisionHistory providerRevisionHistory = this.loadRevisionHistory("apis/cobol-provider-revision-1.api",
                "apis/cobol-provider-revision-2.api");
        ConsumerApiDefinition consumerApi = this.loadConsumerApi("apis/cobol-consumer-api.api", 0);

        Set<Integer> supportedRevisions = new HashSet<>(Arrays.asList(0, 1));
        
        MoveGenerator generator = new MoveGenerator();
        
        File inMoves = new File("myInputMoves.cbl");
        generator.generateInputMoves(providerRevisionHistory, supportedRevisions, consumerApi, "Kunde", inMoves);
	}

}
