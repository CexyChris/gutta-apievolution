package gutta.apievolution.cobol.recordModel;

public enum UsageClause {
	
	DISPLAY("DISPLAY"),
	BINARY("BINARY"),
	PACKEDDECIMAL("PACKED-DECIMAL"),
	INDEX("INDEX");
	
	private String usage;

	public String getText() {
		return usage;
	}
	
	private UsageClause(String text) {
		this.usage = text;
	}

}
