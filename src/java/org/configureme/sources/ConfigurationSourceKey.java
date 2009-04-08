package org.configureme.sources;

public class ConfigurationSourceKey {
	public enum Type {
		FILE;
	};
	
	public enum Format{
		JSON,
		PROPERTIES,
		XML;
		
		public String getExtension(){
			return toString().toLowerCase();
		}
	}
	
	private Type type;
	private Format format;
	private String name;

	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Format getFormat() {
		return format;
	}
	public void setFormat(Format format) {
		this.format = format;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString(){
		return getType().toString()+"::"+getName()+"::"+getFormat();
	}
	
	public boolean equals(Object o){
		return o instanceof ConfigurationSourceKey ? 
				((ConfigurationSourceKey)o).type == type &&
				((ConfigurationSourceKey)o).format == format &&
				((ConfigurationSourceKey)o).name.equals(name) : 
					false;
	}
	
	public int hashCode(){
		int result = 17;
		result = 31 * result + type.hashCode();
		result = 31 * result + format.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}
}
