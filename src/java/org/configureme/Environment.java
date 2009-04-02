package org.configureme;

public interface Environment {

	/**
	 * Returns true if the Environment can be reduced
	 * @return
	 */
	public boolean isReduceable();
	
	public Environment reduce();
}
