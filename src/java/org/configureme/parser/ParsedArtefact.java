package org.configureme.parser;

import java.util.ArrayList;
import java.util.List;

public class ParsedArtefact {
	private List<ParsedAttribute> attributes;
	
	private long lastModificationTimestamp;
	
	private long parseTimestamp;
	
	private String name;
	
	public ParsedArtefact(String aName){
		name = aName;
		parseTimestamp = System.currentTimeMillis();
		attributes = new ArrayList<ParsedAttribute>();
	}
	
	public void addAttribute(ParsedAttribute anAttribute){
		attributes.add(anAttribute);
	}

	public List<ParsedAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<ParsedAttribute> attributes) {
		this.attributes = attributes;
	}

	public long getLastModificationTimestamp() {
		return lastModificationTimestamp;
	}

	public void setLastModificationTimestamp(long lastModificationTimestamp) {
		this.lastModificationTimestamp = lastModificationTimestamp;
	}

	public long getParseTimestamp() {
		return parseTimestamp;
	}

	public void setParseTimestamp(long parseTimestamp) {
		this.parseTimestamp = parseTimestamp;
	}

	public String getName() {
		return name;
	}
	
	public String toString(){
		return getName()+": "+getAttributes();
	}
	
	
}
