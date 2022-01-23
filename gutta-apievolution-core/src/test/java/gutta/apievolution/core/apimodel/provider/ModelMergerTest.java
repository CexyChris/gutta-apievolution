package gutta.apievolution.core.apimodel.provider;

import gutta.apievolution.core.apimodel.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelMergerTest {

    /**
     * Ensure that merging annotations works as expected.
     */
    @Test
    void testAnnotationMerging() {
        ProviderApiDefinition revision1 = new ProviderApiDefinition(QualifiedName.of("a.b"),
                new HashSet<>(
                        Arrays.asList(new Annotation("a", "b"), new Annotation("b", "c"))),
                0,
                Optional.empty());

        ProviderApiDefinition revision2 = new ProviderApiDefinition(QualifiedName.of("a.b"),
                new HashSet<>(
                        Arrays.asList(new Annotation("b", "x"), new Annotation("c", "d"))),
                1,
                Optional.empty());

        RevisionHistory revisionHistory = new RevisionHistory(revision1, revision2);
        ProviderApiDefinition mergedRevision = new ModelMerger().createMergedDefinition(revisionHistory);

        // We expect that annotations a to c exist, with the value for b from revision 2 as it is the "newer" revision
        Set<Annotation> expectedAnnotations = new HashSet<>(Arrays.asList(
                new Annotation("a", "b"),
                new Annotation("b", "x"),
                new Annotation("c", "d")
        ));

        assertEquals(expectedAnnotations, mergedRevision.getAnnotations());
    }

    /**
     * Tests that merging the fields of a type across revisions works as expected.
     */
    @Test
    void testFieldMerging() {
        ProviderApiDefinition revision1 = new ProviderApiDefinition(QualifiedName.of("test"),
                Collections.emptySet(),
                1,
                Optional.empty());

        ProviderRecordType testTypeV1 = new ProviderRecordType("Test",
                Optional.empty(),
                0,
                revision1,
                false,
                Optional.empty(),
                Optional.empty());

        ProviderField unchangedFieldV1 = new ProviderField("unchangedField",
                Optional.empty(),
                testTypeV1,
                StringType.unbounded(),
                Optionality.MANDATORY,
                Optional.empty());

        ProviderField typeChangeFieldV1 = new ProviderField("typeChangeField",
                Optional.empty(),
                testTypeV1,
                AtomicType.INT_32,
                Optionality.MANDATORY,
                Optional.empty());

        new ProviderField("deletedField",
                Optional.empty(),
                testTypeV1,
                StringType.unbounded(),
                Optionality.MANDATORY,
                Optional.empty());

        ProviderApiDefinition revision2 = new ProviderApiDefinition(QualifiedName.of("test"),
                Collections.emptySet(),
                2,
                Optional.empty());

        ProviderRecordType testTypeV2 = new ProviderRecordType("Test",
                Optional.empty(),
                0,
                revision2,
                false,
                Optional.empty(),
                Optional.of(testTypeV1));

        new ProviderField("typeChangeField",
                Optional.of("newTypeChangeField"),
                testTypeV2,
                AtomicType.INT_64,
                Optionality.MANDATORY,
                Optional.empty());

        new ProviderField("unchangedField",
                Optional.empty(),
                testTypeV2,
                StringType.unbounded(),
                Optionality.MANDATORY,
                Optional.of(unchangedFieldV1));

        new ProviderField("addedField",
                Optional.empty(),
                testTypeV2,
                StringType.unbounded(),
                Optionality.MANDATORY,
                Optional.empty());

        // Merge the test revision history into a single definition
        RevisionHistory revisionHistory = new RevisionHistory(revision1, revision2);
        ProviderApiDefinition mergedDefinition = new ModelMerger().createMergedDefinition(revisionHistory);

        // Compare the created revision against the expected one. All optional fields are opt-in because the type
        // is not used as output
        String expected = "api test [] {\n" +
                " record Test {\n" +
                "  optin typeChangeField(newTypeChangeField):int64\n" + // Must be opt-in due to the type change
                "  mandatory unchangedField(unchangedField):string\n" + // Must be mandatory as it does not change
                "  optin addedField(addedField):string\n" + // Must be opt-in because it is added
                "  optin typeChangeField(typeChangeField):int32\n" + // Must be opt-in due to type change
                "  optin deletedField(deletedField):string\n" + // Must be opt-in as it is deleted
                " }\n" +
                "}\n";

        String actual = new ProviderApiDefinitionPrinter().printApiDefinition(mergedDefinition);
        assertEquals(expected, actual);
    }

    /**
     * Tests that merging the members of an enumeration across revisions works as expected.
     */
    @Test
    void testEnumMemberMerging() {
        ProviderApiDefinition revision1 = new ProviderApiDefinition(QualifiedName.of("test"),
                Collections.emptySet(),
                1,
                Optional.empty());

        ProviderEnumType testEnumV1 = new ProviderEnumType("Test",
                Optional.empty(),
                0,
                revision1,
                Optional.empty());

        ProviderEnumMember unchangedMember = new ProviderEnumMember("UNCHANGED",
                Optional.empty(),
                testEnumV1,
                Optional.empty());

        new ProviderEnumMember("DELETED",
                Optional.empty(),
                testEnumV1,
                Optional.empty());

        ProviderApiDefinition revision2 = new ProviderApiDefinition(QualifiedName.of("test"),
                Collections.emptySet(),
                2,
                Optional.of(revision1));

        ProviderEnumType testEnumV2 = new ProviderEnumType("Test",
                Optional.empty(),
                0,
                revision2,
                Optional.of(testEnumV1));

        new ProviderEnumMember("UNCHANGED",
                Optional.empty(),
                testEnumV2,
                Optional.of(unchangedMember));

        new ProviderEnumMember("ADDED",
                Optional.empty(),
                testEnumV2,
                Optional.empty());

        // Merge the test revision history into a single definition
        RevisionHistory revisionHistory = new RevisionHistory(revision1, revision2);
        ProviderApiDefinition mergedDefinition = new ModelMerger().createMergedDefinition(revisionHistory);

        // Compare the created revision against the expected one
        String expected = "api test [] {\n" +
                " enum Test {\n" +
                "  UNCHANGED(UNCHANGED)\n" +
                "  ADDED(ADDED)\n" +
                "  DELETED(DELETED)\n" +
                " }\n" +
                "}\n";

        String actual = new ProviderApiDefinitionPrinter().printApiDefinition(mergedDefinition);
        assertEquals(expected, actual);
    }

    /**
     * Tests the model merger's ability to perform a mapping merge, i.e., to provide a map from a given revision
     * of the history to the respective elements of the merged revision.
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void testMappingMerge() {
        ProviderApiDefinition revision1 = new ProviderApiDefinition(QualifiedName.of("test"),
                Collections.emptySet(),
                1,
                Optional.empty());

        ProviderRecordType testTypeV1 = new ProviderRecordType("Test",
                Optional.empty(),
                0,
                revision1,
                false,
                Optional.empty(),
                Optional.empty());

        ProviderField unchangedFieldV1 = new ProviderField("unchangedField",
                Optional.empty(),
                testTypeV1,
                StringType.unbounded(),
                Optionality.MANDATORY,
                Optional.empty());

        ProviderField typeChangeFieldV1 = new ProviderField("typeChangeField",
                Optional.empty(),
                testTypeV1,
                AtomicType.INT_32,
                Optionality.MANDATORY,
                Optional.empty());

        ProviderField deletedFieldV1 = new ProviderField("deletedField",
                Optional.empty(),
                testTypeV1,
                StringType.unbounded(),
                Optionality.MANDATORY,
                Optional.empty());

        ProviderEnumType testEnumV1 = new ProviderEnumType("Test",
                Optional.empty(),
                0,
                revision1,
                Optional.empty());

        ProviderEnumMember unchangedMemberV1 = new ProviderEnumMember("UNCHANGED",
                Optional.empty(),
                testEnumV1,
                Optional.empty());

        ProviderEnumMember deletedMemberV1 = new ProviderEnumMember("DELETED",
                Optional.empty(),
                testEnumV1,
                Optional.empty());

        ProviderApiDefinition revision2 = new ProviderApiDefinition(QualifiedName.of("test"),
                Collections.emptySet(),
                2,
                Optional.empty());

        ProviderRecordType testTypeV2 = new ProviderRecordType("Test",
                Optional.of("TestInternal"),
                0,
                revision2,
                false,
                Optional.empty(),
                Optional.of(testTypeV1));

        ProviderField typeChangeFieldV2 = new ProviderField("typeChangeField",
                Optional.of("newTypeChangeField"),
                testTypeV2,
                AtomicType.INT_64,
                Optionality.MANDATORY,
                Optional.empty());

        ProviderField unchangedFieldV2 = new ProviderField("unchangedField",
                Optional.empty(),
                testTypeV2,
                StringType.unbounded(),
                Optionality.MANDATORY,
                Optional.of(unchangedFieldV1));

        ProviderField addedFieldV2 = new ProviderField("addedField",
                Optional.empty(),
                testTypeV2,
                StringType.unbounded(),
                Optionality.MANDATORY,
                Optional.empty());

        ProviderEnumType testEnumV2 = new ProviderEnumType("Test",
                Optional.empty(),
                0,
                revision2,
                Optional.of(testEnumV1));

        ProviderEnumMember unchangedMemberV2 = new ProviderEnumMember("UNCHANGED",
                Optional.empty(),
                testEnumV2,
                Optional.of(unchangedMemberV1));

        ProviderEnumMember addedMemberV2 = new ProviderEnumMember("ADDED",
                Optional.empty(),
                testEnumV2,
                Optional.empty());

        // Merge the test revision history into a single definition
        RevisionHistory revisionHistory = new RevisionHistory(revision1, revision2);

        // Create and inspect a mapping from the first revision
        ToMergedModelMap map1 = new ModelMerger().createMergedDefinition(revisionHistory, revision1);

        // Inspect the mapped types
        ProviderRecordType mergedTestTypeV1 = map1.<ProviderRecordType> mapType(testTypeV1).get();
        assertEquals(testTypeV1.getPublicName(), mergedTestTypeV1.getPublicName());
        assertEquals(testTypeV2.getInternalName(), mergedTestTypeV1.getInternalName());

        ProviderEnumType mergedTestEnumV1 = map1.<ProviderEnumType> mapType(testEnumV1).get();
        assertEquals(testEnumV1.getPublicName(), mergedTestEnumV1.getPublicName());
        assertEquals(testEnumV1.getInternalName(), mergedTestEnumV1.getInternalName());

        // Inspect the mapped fields
        ProviderField mergedUnchangedFieldV1 = map1.mapField(unchangedFieldV1).get();
        assertEquals(unchangedFieldV1.getPublicName(), mergedUnchangedFieldV1.getPublicName());
        assertEquals(unchangedFieldV1.getInternalName(), mergedUnchangedFieldV1.getInternalName());
        assertEquals(unchangedFieldV1.getType(), map1.mapType(mergedUnchangedFieldV1.getType()).get());
        assertEquals(unchangedFieldV1.getOptionality(), mergedUnchangedFieldV1.getOptionality());

        ProviderField mergedTypeChangeFieldV1 = map1.mapField(typeChangeFieldV1).get();
        assertEquals(typeChangeFieldV1.getPublicName(), mergedTypeChangeFieldV1.getPublicName());
        assertEquals(typeChangeFieldV1.getInternalName(), mergedTypeChangeFieldV1.getInternalName());
        assertEquals(typeChangeFieldV1.getType(), map1.mapType(mergedTypeChangeFieldV1.getType()).get());
        assertEquals(Optionality.OPT_IN, mergedTypeChangeFieldV1.getOptionality());

        ProviderField mergedDeletedFieldV1 = map1.mapField(deletedFieldV1).get();
        assertEquals(deletedFieldV1.getPublicName(), mergedDeletedFieldV1.getPublicName());
        assertEquals(deletedFieldV1.getInternalName(), mergedDeletedFieldV1.getInternalName());
        assertEquals(deletedFieldV1.getType(), map1.mapType(mergedDeletedFieldV1.getType()).get());
        assertEquals(Optionality.OPT_IN, mergedDeletedFieldV1.getOptionality());

        // Inspect the mapped members
        ProviderEnumMember mergedUnchangedMemberV1 = map1.mapEnumMember(unchangedMemberV1).get();
        assertEquals(unchangedMemberV1.getPublicName(), mergedUnchangedMemberV1.getPublicName());
        assertEquals(unchangedMemberV1.getInternalName(), mergedUnchangedMemberV1.getInternalName());

        ProviderEnumMember mergedDeletedMemberV1 = map1.mapEnumMember(deletedMemberV1).get();
        assertEquals(deletedMemberV1.getPublicName(), mergedDeletedMemberV1.getPublicName());
        assertEquals(deletedMemberV1.getInternalName(), mergedDeletedMemberV1.getInternalName());

        // Create and inspect a mapping from the second revision
        ToMergedModelMap map2 = new ModelMerger().createMergedDefinition(revisionHistory, revision2);

        ProviderRecordType mergedTestTypeV2 = map2.<ProviderRecordType> mapType(testTypeV2).get();
        assertEquals(testTypeV2.getPublicName(), mergedTestTypeV2.getPublicName());
        assertEquals(testTypeV2.getInternalName(), mergedTestTypeV2.getInternalName());

        // Inspect the mapped fields
        ProviderField mergedUnchangedFieldV2 = map2.mapField(unchangedFieldV2).get();
        assertEquals(unchangedFieldV2.getPublicName(), mergedUnchangedFieldV2.getPublicName());
        assertEquals(unchangedFieldV2.getInternalName(), mergedUnchangedFieldV2.getInternalName());
        assertEquals(unchangedFieldV2.getType(), map1.mapType(mergedUnchangedFieldV2.getType()).get());
        assertEquals(unchangedFieldV2.getOptionality(), mergedUnchangedFieldV2.getOptionality());

        ProviderField mergedTypeChangeFieldV2 = map2.mapField(typeChangeFieldV2).get();
        assertEquals(typeChangeFieldV2.getPublicName(), mergedTypeChangeFieldV2.getPublicName());
        assertEquals(typeChangeFieldV2.getInternalName(), mergedTypeChangeFieldV2.getInternalName());
        assertEquals(typeChangeFieldV2.getType(), map1.mapType(mergedTypeChangeFieldV2.getType()).get());
        assertEquals(Optionality.OPT_IN, mergedTypeChangeFieldV2.getOptionality());

        ProviderField mergedDeletedFieldV2 = map2.mapField(addedFieldV2).get();
        assertEquals(addedFieldV2.getPublicName(), mergedDeletedFieldV2.getPublicName());
        assertEquals(addedFieldV2.getInternalName(), mergedDeletedFieldV2.getInternalName());
        assertEquals(addedFieldV2.getType(), map2.mapType(mergedDeletedFieldV2.getType()).get());
        assertEquals(Optionality.OPT_IN, mergedDeletedFieldV2.getOptionality());

        // Inspect the mapped members
        ProviderEnumMember mergedUnchangedMemberV2 = map2.mapEnumMember(unchangedMemberV2).get();
        assertEquals(unchangedMemberV2.getPublicName(), mergedUnchangedMemberV2.getPublicName());
        assertEquals(unchangedMemberV2.getInternalName(), mergedUnchangedMemberV2.getInternalName());

        ProviderEnumMember mergedDeletedMemberV2 = map2.mapEnumMember(addedMemberV2).get();
        assertEquals(addedMemberV2.getPublicName(), mergedDeletedMemberV2.getPublicName());
        assertEquals(addedMemberV2.getInternalName(), mergedDeletedMemberV2.getInternalName());
    }

}
