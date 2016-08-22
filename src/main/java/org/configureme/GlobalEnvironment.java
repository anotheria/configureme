package org.configureme;

/**
 * GlobalEnvironment is an Environment implementation which is used, when no other Environment is specified explictely. GlobalEnvironment is not reduceable.
 * The expandedStringForm of the GlobalEnvironment is an empty string. There might only be one instance of the GlobalEnvironment, hence it is an enum.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public enum GlobalEnvironment implements Environment{
	/**
	 * The one and only instance.
	 */
	INSTANCE;

	/** {@inheritDoc} */
	@Override
	public boolean isReduceable() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public Environment reduce() {
		throw new AssertionError("Global Environment is not reduceable");
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString(){
		return "global";
	}
	
	/** {@inheritDoc} */
	@Override
	public String expandedStringForm(){
		return "";
	}
}
