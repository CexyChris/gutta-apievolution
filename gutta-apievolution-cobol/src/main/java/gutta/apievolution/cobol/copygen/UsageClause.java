package gutta.apievolution.cobol.copygen;

public enum UsageClause {
	
	DISPLAY("DISPLAY"),
	BINARY("BINARY"),
	PACKEDDECIMAL("PACKED-DECIMAL"),
	INDEX("INDEX");
	
	private String text;

	public String getText() {
		return text;
	}
	
	private UsageClause(String text) {
		this.text = text;
	}
	
	

}
