package gutta.apievolution.cobol;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import gutta.apievolution.core.apimodel.Field;
import gutta.apievolution.core.apimodel.RecordType;
import gutta.apievolution.core.apimodel.Type;
import gutta.apievolution.core.apimodel.TypeVisitor;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinition;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinition;
import gutta.apievolution.core.apimodel.provider.RevisionHistory;
import gutta.apievolution.core.resolution.DefinitionResolution;
import gutta.apievolution.core.resolution.DefinitionResolver;
import gutta.apievolution.core.util.IntegerRange;
import gutta.apievolution.dsl.ConsumerApiLoader;
import gutta.apievolution.dsl.ProviderApiLoader;

class CobolMappingTest {
	//This is copied nearly literally from the JSON example.
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
    
    private static final String KundenNr = "0123456789ABCDEF";

	@Test
	void testCobolConversation() throws IOException {
        RevisionHistory providerRevisionHistory = this.loadRevisionHistory("apis/cobol-provider-revision-1.api",
                "apis/cobol-provider-revision-2.api");
        ConsumerApiDefinition consumerApi = this.loadConsumerApi("apis/cobol-consumer-api.api", 0);

        Set<Integer> supportedRevisions = new HashSet<>(Arrays.asList(0, 1));
        DefinitionResolution definitionResolution = new DefinitionResolver().resolveConsumerDefinition(providerRevisionHistory, supportedRevisions, consumerApi);
      
        //End of copied code ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        Type consumerOutput = definitionResolution.resolveConsumerType("KundenNr");
        
        Type providerInput  =  definitionResolution.mapConsumerType(consumerOutput);
        
        
        //Create an Object, which looks like the Provider's internal Representation
        //i.e. the provider has now filled its copybook and is ready to send it to the consoomer.
        String vorname = padRight("Christoph", 64);
        String nameFiller1 = " ";
        String nachnameProlonged = padRight("Buck", 64);
        String gebTag = "06";
        String gebFiller1 = ":";
        String gebMonat = "09";
        String gebFiller2 = ":";
        String gebJahr ="1995";
        String festnetznummer = padRight("0511362", 16);
        String mobilfunknummer = padRight("1481", 16);
        String mailadresseShortened = padRight("mail@ivv.de", 256);

        String nameNew = vorname + nameFiller1 + nachnameProlonged;
        String geburtsdatumNew = gebTag + gebFiller1 + gebMonat + gebFiller2 + gebJahr;
        
        String KundeRevision2 = nameNew + geburtsdatumNew + festnetznummer + mobilfunknummer + mailadresseShortened; 
        
        
        Type providerOutput =  definitionResolution.resolveProviderType("Kunde");
        Type consumerInput = definitionResolution.mapProviderType(providerOutput);
        
        CobolFieldVisitor visitor = new CobolFieldVisitor();
        providerOutput.accept(visitor);
        System.out.println("++++++++++++++++++++++++++++++++++");
        consumerInput.accept(visitor);
	}
	
	private static String padRight(String s, int n) {
	     return String.format("%-" + n + "s", s);  
	}
	
	private class CobolFieldVisitor implements TypeVisitor<Void> {
		
		@Override
		public Void handleRecordType(RecordType<?, ?, ?> recordType) {
			Iterator<?> iterator = recordType.iterator();
			while(iterator.hasNext()) {
				System.out.println(iterator.next().toString());
			}
			return null;
		}
	}
	

	
	private class KundeProviderRevision2Copy {
		
		private String content;
		
		public KundeProviderRevision2Copy(String kundeRecord) throws Exception {
			if(kundeRecord.length() != 427) {
				throw new Exception("Die Zeichenkette ist kein Kunde in Revision 2!");
			}
			this.content = kundeRecord;
		}
		
		public String getNameNew() {
			return content.substring(0, 130);
		}
		public String getVorname() {
			return content.substring(0, 65);
		}
	}

}
