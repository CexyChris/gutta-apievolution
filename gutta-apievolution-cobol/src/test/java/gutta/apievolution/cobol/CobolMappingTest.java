package gutta.apievolution.cobol;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gutta.apievolution.core.apimodel.AtomicType;
import gutta.apievolution.core.apimodel.BoundedStringType;
import gutta.apievolution.core.apimodel.Field;
import gutta.apievolution.core.apimodel.NumericType;
import gutta.apievolution.core.apimodel.RecordType;
import gutta.apievolution.core.apimodel.Type;
import gutta.apievolution.core.apimodel.TypeVisitor;
import gutta.apievolution.core.apimodel.consumer.ConsumerApiDefinition;
import gutta.apievolution.core.apimodel.consumer.ConsumerField;
import gutta.apievolution.core.apimodel.provider.ProviderApiDefinition;
import gutta.apievolution.core.apimodel.provider.ProviderField;
import gutta.apievolution.core.apimodel.provider.ProviderRecordType;
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
    
	@Test
	void testCobolConversation() throws IOException {
        RevisionHistory providerRevisionHistory = this.loadRevisionHistory("apis/Kunde-provider-revision-1.api",
                "apis/Kunde-provider-revision-2.api");
        ConsumerApiDefinition consumerApi = this.loadConsumerApi("apis/Kunde-consumer-revision-1.api", 0);

        Set<Integer> supportedRevisions = new HashSet<>(Arrays.asList(0, 1));
        DefinitionResolution definitionResolution = new DefinitionResolver().resolveConsumerDefinition(providerRevisionHistory, supportedRevisions, consumerApi);
      
        //End of copied code ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        
        Type providerOutputType =  definitionResolution.resolveProviderType("Kunde");
        if (providerOutputType instanceof RecordType) {
            RecordType<?, ?, ?> recordType = (RecordType<?, ?, ?>) providerOutputType;
            this.printFields(recordType);
        }
        
        KundeRecResolution kundeRes = createKundeRecResolution();
        
        System.out.println("************");
        Type consumerInputType = definitionResolution.mapProviderType(providerOutputType);

        RecordType<?, ?, ?> consumerRecordType = (RecordType<?, ?, ?>) consumerInputType;
        RecordType<?, ?, ?> providerRecordType = (RecordType<?, ?, ?>) providerOutputType;
        Map<Field<?, ?>, int[]> consumerOffsetMap = new offsetMapper(consumerRecordType).getMap();
        Map<Field<?, ?>, int[]> providerOffsetMap = new offsetMapper(providerRecordType).getMap();
        
        System.out.println("************");
        printOffsetMapping(providerOffsetMap);
        System.out.println("************");
        printOffsetMapping(consumerOffsetMap);
        byte[] consumerContent = new byte[KundeRecRevision1.MY_DATA_STRUCTURE_len];
        consumerContent = this.writeProviderToConsumer(providerRecordType, kundeRes.getByteBuffer(), consumerContent, definitionResolution, consumerOffsetMap, providerOffsetMap);
        KundeRecRevision1 kunde = new KundeRecRevision1(consumerContent);
        
        assertEquals(padRight(KundeConstants.VORNAME, 64), kunde.getVorname());
        assertEquals(padRight(KundeConstants.NACHNAME, 32), kunde.getNachname());
        assertEquals(padRight(KundeConstants.MAILADRESSE, 512), kunde.getMailadresse());
        assertEquals(KundeConstants.FESTNETZNUMMER, kunde.getTelefonnummer());
        assertEquals(KundeConstants.GEBURTSDATUM, kunde.getGeburtsdatum());
        assertTrue(kunde.isMaennlich());

	}
	
	private byte[] writeProviderToConsumer(RecordType<?, ?, ?> providerRecordType, byte[] providerContent, byte[] consumerContent 
			, DefinitionResolution defRes, Map<Field<?, ?>, int[]> consumerOffsetMap, Map<Field<?, ?>, int[]> providerOffsetMap) {
		
		for( Field<?, ?> field : providerRecordType.getDeclaredFields()) {
			if(field.getType() instanceof RecordType) {
				writeProviderToConsumer((RecordType) field.getType(), providerContent, consumerContent, defRes, consumerOffsetMap, providerOffsetMap);
			} else {
				ConsumerField cf = defRes.mapProviderField((ProviderField) field);
				if(cf != null) {
					System.out.println(cf.getPublicName());
					int[] coffset = consumerOffsetMap.get(cf);
					int[] poffset = providerOffsetMap.get(field);
					for(int i = 0; i < poffset[1]; i++) {
						consumerContent[coffset[0] + i] = providerContent[poffset[0] + i];
					}
				}
			}
		}
		return consumerContent;
	}

	private class offsetMapper implements TypeVisitor<Boolean> {
		private int offset;
		private int length;
		private Map<Field<?,?>, int[]> map;
		
		public offsetMapper(RecordType<?, ?, ?> type) {
			this.offset = 0;
			this.map = new HashMap<Field<?, ?>, int[]>();
			this.handleRecordType(type);
		}
		
		private void mapOffsets(Field<?, ?> field) {
			if(field.getType().accept(this)) {
				int[] offsetAndLength = {this.offset, this.length};
				this.map.putIfAbsent(field, offsetAndLength);
				this.offset = this.offset + this.length;
			}
		}
		
		@Override
		public Boolean handleBoundedStringType(BoundedStringType boundedStringType) {
			this.length = boundedStringType.getBound();
			return true;
		}
		
		@Override
		public Boolean handleNumericType(NumericType numericType) {
			this.length = numericType.getIntegerPlaces() + numericType.getFractionalPlaces();
			return true;
		}

		@Override
		public Boolean handleRecordType(RecordType<?, ?, ?> recordType) {
			recordType.getDeclaredFields().forEach(f -> this.mapOffsets(f));
			return false;
		}
		
		public Map<Field<?, ?>, int[]> getMap() {
			return this.map;
		}
		
	}


	private static String padRight(String s, int n) {
	     return String.format("%-" + n + "s", s);  
	}
	
	private void printFields(RecordType<?, ?, ?> recordType) {
        for(Field<?, ?> field: recordType.getDeclaredFields()) {
        	System.out.println(
        			field.getOptionality() + ": " 
        		  + field.getInternalName() + " --> " 
        		  + field.getPublicName()
        	);
        	Type fieldType = field.getType();
        	if (fieldType instanceof RecordType) {
        		RecordType<?, ?, ?> nextRecordType = (RecordType<?, ?, ?>) fieldType;
        		this.printFields(nextRecordType);
        	}
        }
	}
	
	private static void printOffsetMapping(Map<Field<?, ?>, int[]> mapping) {
        for(Field<?, ?> field : mapping.keySet()) {
        	int[] i = mapping.get(field);
        	System.out.println(field.getInternalName() + " --> " + "[" + i[0] + ", " + i[1] + "]");
        }
	}
	
	
	private static KundeRecResolution createKundeRecResolution() {
        KundeRecResolution kundeRes = new KundeRecResolution();
        //NameNew
        kundeRes.setVorname(KundeConstants.VORNAME);
        kundeRes.setNachnameprolonged(KundeConstants.NACHNAME);
        kundeRes.setNachname(KundeConstants.NACHNAME);
        //GeburtsdatumNew
        kundeRes.setGeburtsdatumtag(KundeConstants.GEBURTSDATUM_TAG);
        kundeRes.setGeburtsdatummonat(KundeConstants.GEBURTSDATUM_MONAT);
        kundeRes.setGeburtsdatumjahr(KundeConstants.GEBURTSDATUM_JAHR);
        
        kundeRes.setFestnetznummer(KundeConstants.FESTNETZNUMMER);
        kundeRes.setMobilfunknummer(KundeConstants.MOBILFUNKNUMMER);
        kundeRes.setMailadresseshortened(KundeConstants.MAILADRESSE);
        //Name
        kundeRes.setGeschlecht(KundeConstants.GESCHLECHT);
        kundeRes.setGeburtsdatum(KundeConstants.GEBURTSDATUM);
        kundeRes.setMailadresse(KundeConstants.MAILADRESSE);
        
        return kundeRes;
	}
	


}
