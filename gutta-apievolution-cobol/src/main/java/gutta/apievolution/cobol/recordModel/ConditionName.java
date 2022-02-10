package gutta.apievolution.cobol.recordModel;

import java.util.Objects;

public class ConditionName {
	
	private String name;
	private String value;
	
	public ConditionName(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConditionName other = (ConditionName) obj;
		return Objects.equals(name, other.name) && Objects.equals(value, other.value);
	}
	
}
