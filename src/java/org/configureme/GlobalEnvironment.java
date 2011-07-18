package org.configureme;

/**
 * GlobalEnvironment is an Environment implementation which is used, when no other Environment is specified explictely. GlobalEnvironment is not reduceable. 
 * The expandedStringForm of the GlobalEnvironment is an empty string. There might only be one instance of the GlobalEnvironment, hence it is an enum.
 * @author lrosenberg
 */
public enum GlobalEnvironment implements Environment{
	/**
	 * The one and only instance.
	 */
	INSTANCE;

	@Override
	public boolean isReduceable() {
		return false;
	}

	@Override
	public Environment reduce() {
		throw new AssertionError("Global Environment is not reduceable");
	}
	
	@Override
	public String toString(){
		return "global";
	}
	
	@Override
	public String expandedStringForm(){
		return "";
	}
}
