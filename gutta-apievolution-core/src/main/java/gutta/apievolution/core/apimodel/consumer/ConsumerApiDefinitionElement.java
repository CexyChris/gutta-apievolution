package gutta.apievolution.core.apimodel.consumer;

public interface ConsumerApiDefinitionElement {

    <R> R accept(ConsumerApiDefinitionElementVisitor<R> visitor);

}
