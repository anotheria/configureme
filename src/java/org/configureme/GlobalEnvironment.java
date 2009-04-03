package org.configureme;

public enum GlobalEnvironment implements Environment{
	INSTANCE;

	@Override
	public boolean isReduceable() {
		return false;
	}

	@Override
	public Environment reduce() {
		throw new AssertionError("Global Environment is not reduceable");
	}
	
	public String toString(){
		return "global";
	}
	
	public String expandedStringForm(){
		return "";
	}
}
