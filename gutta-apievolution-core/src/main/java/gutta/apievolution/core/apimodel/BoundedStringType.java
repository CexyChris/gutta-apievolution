package gutta.apievolution.core.apimodel;

/**
 * This type represents the bounded variant of the {@link StringType}.
 */
public class BoundedStringType extends StringType {

    private final int bound;

    BoundedStringType(int bound) {
        this.bound = bound;
    }

    /**
     * Returns the bound of this string type.
     * @return see above
     */
    public int getBound() {
        return this.bound;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + this.bound;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof BoundedStringType) {
            return this.stateEquals((BoundedStringType) that);
        } else {
            return false;
        }
    }

    boolean stateEquals(BoundedStringType that) {
        return this.bound == that.bound;
    }

    @Override
    public <R> R accept(TypeVisitor<R> visitor) {
        return visitor.handleBoundedStringType(this);
    }

    @Override
    public String toString() {
        return "string(" + this.getBound() + ")";
    }

}
