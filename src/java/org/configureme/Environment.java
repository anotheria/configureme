package org.configureme;
/**
 * Defines an environment in which an application can be executed and which may be configured differently.
 * @author lrosenberg
 */
public interface Environment {

	/**
	 * Returns true if the Environment can be reduced
	 * @return
	 */
	public boolean isReduceable();
	
	/**
	 * Returns a reduced form of the environment. A reduced form of an environment is the form with less variants, hence less underscores. 
	 * The ultimatively reduced Environment is the GlobalEnvironment.
	 * @return
	 */
	public Environment reduce();
	
	/**
	 * Returns the string representation of the environment of form x_y_z, where x,y and z are subsequent parts of the Environment.
	 * @return
	 */
	public String expandedStringForm();
}
