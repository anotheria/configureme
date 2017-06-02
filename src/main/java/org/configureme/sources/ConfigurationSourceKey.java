package org.configureme.sources;

/**
 * A configuration source key is the unique identification of a configuration source. This class is also used as key in maps.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class ConfigurationSourceKey {
	/**
	 * The type of the source.
	 */
	public enum Type {
		/**
		 * Configuration comes from a file.
		 */
		FILE,
		/**
		 * Configuration comes from a test fixture.
		 */
		FIXTURE,

		/**
		 * Configuration comes from a REST API.
		 */
		REST,

		/**
		 * Configuration comes from a repository.
		 */
		REPOSITORY
	}

	/**
	 * The format of the file. The format decides which parser will be used to parse the file.
	 * @author lrosenberg
	 */
	public enum Format{
		/**
		 * JSON format.
		 */
		JSON,
		/**
		 * Properties format.
		 */
		PROPERTIES,
		/**
		 * XML format.
		 */
		XML;
		
		/**
		 * Returns the extension under which file would be stored. 
		 * @return the extension under which file would be stored
		 */
		public String getExtension(){
			return toString().toLowerCase();
		}
	}
	
	/**
	 * The type of the source.
	 */
	private Type type;
	/**
	 * The format of the source.
	 */
	private Format format;
	/**
	 * The name of the source.
	 */
	private String name;
	/**
	 * Externally provided url for remote configuration repository.
	 */
	private String remoteConfigurationRepositoryUrl;
	
	/**
	 * Creates a new key.
	 */
	public ConfigurationSourceKey(){
		
	}
	
	/**
	 * Creates a new key preset to given parameters.
	 *
	 * @param aType the type of the source
	 * @param aFormat the format of the source
	 * @param aName the name of the source
	 */
	public ConfigurationSourceKey(Type aType, Format aFormat, String aName){
		type = aType;
		format = aFormat;
		name = aName;
	}

	/**
	 * <p>Getter for the field {@code type}.</p>
	 *
	 * @return a {@link org.configureme.sources.ConfigurationSourceKey.Type} object.
	 */
	public Type getType() {
		return type;
	}
	/**
	 * <p>Setter for the field {@code type}.</p>
	 *
	 * @param type a {@link org.configureme.sources.ConfigurationSourceKey.Type} object.
	 */
	public void setType(Type type) {
		this.type = type;
	}
	/**
	 * <p>setTypeIfNotDefault.</p>
	 *
	 * @param defType a {@link org.configureme.sources.ConfigurationSourceKey.Type} object.
	 * @param toChange a {@link org.configureme.sources.ConfigurationSourceKey.Type} object.
	 */
	public void setTypeIfNotDefault(final Type defType, final Type toChange){
		this.type = defType != Type.FILE  ? defType : toChange;
	}
	/**
	 * <p>Getter for the field {@code format}.</p>
	 *
	 * @return a {@link org.configureme.sources.ConfigurationSourceKey.Format} object.
	 */
	public Format getFormat() {
		return format;
	}
	/**
	 * <p>Setter for the field {@code format}.</p>
	 *
	 * @param format a {@link org.configureme.sources.ConfigurationSourceKey.Format} object.
	 */
	public void setFormat(Format format) {
		this.format = format;
	}
	/**
	 * <p>Getter for the field {@code name}.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name;
	}
	/**
	 * <p>Setter for the field {@code name}.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * <p>Getter for the field {@code remoteConfigurationRepositoryUrl}.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getRemoteConfigurationRepositoryUrl() {
		return remoteConfigurationRepositoryUrl;
	}
	/**
	 * <p>Setter for the field {@code remoteConfigurationRepositoryUrl}.</p>
	 *
	 * @param remoteConfigurationRepositoryUrl a {@link java.lang.String} object.
	 */
	public void setRemoteConfigurationRepositoryUrl(String remoteConfigurationRepositoryUrl) {
		this.remoteConfigurationRepositoryUrl = remoteConfigurationRepositoryUrl;
	}

	@Override
	public String toString(){
        return type +"::"+ name +"::"+ format;
	}
	
	@Override
	public boolean equals(Object o){
		return o instanceof ConfigurationSourceKey ? 
				((ConfigurationSourceKey)o).type == type &&
				((ConfigurationSourceKey)o).format == format &&
				((ConfigurationSourceKey)o).name.equals(name) : 
					false;
	}
	
	@Override
	public int hashCode(){
		int result = 17;
		result = 31 * result + type.hashCode();
		result = 31 * result + format.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
	
	/**
	 * Creates a new configuration source key for property files.
	 *
	 * @param name name of the property file.
	 * @return a new configuration source key instance for property files
	 */
	public static final ConfigurationSourceKey propertyFile(final String name){
		return new ConfigurationSourceKey(Type.FILE, Format.PROPERTIES, name);
	}

	/**
	 * Creates a new configuration source key for xml files.
	 *
	 * @param name name of the xml file.
	 * @return a new configuration source key instance for xml files
	 */
	public static final ConfigurationSourceKey xmlFile(final String name){
		return new ConfigurationSourceKey(Type.FILE, Format.XML, name);
	}

	/**
	 * Creates a new configuration source key for json files.
	 *
	 * @param name name of the json file.
	 * @return a new configuration source key instance for json files
	 */
	public static final ConfigurationSourceKey jsonFile(final String name){
		return new ConfigurationSourceKey(Type.FILE, Format.JSON, name);
	}

}
