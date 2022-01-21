package gutta.apievolution.cobol.copygen;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import gutta.apievolution.core.apimodel.provider.ProviderApiDefinition;
import gutta.apievolution.core.util.IntegerRange;
import gutta.apievolution.dsl.ProviderApiLoader;

class CopyGeneratorTest {
	
	@Test
	void test() throws IOException {
		ClassLoader classLoader = this.getClass().getClassLoader();
        List<InputStream> streams = Stream.of("apis/cobol-provider-revision-1.api",
                "apis/cobol-provider-revision-2.api")
                .map(name -> classLoader.getResourceAsStream(name))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
		List<ProviderApiDefinition> apiDefinitions 
		= ProviderApiLoader.loadHistoryFromStreams(IntegerRange.unbounded(), streams);
		
		File file = new File("myProviderCopy.cbl");
		ProviderCopyGenerator.generateCopies(apiDefinitions, "Kunde", file);
		
	}

}
