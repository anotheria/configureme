package org.configureme.parser;

import org.configureme.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 * The combination of the name and the value of an attribute in an environment. Usually an attribute diffenent values in different environments. The parses collects all values of an attribute and submits it
 * in a large list to the configuration repository, which in turn creates hierarchical structures of the attributes and their values.
 *
 * @author another
 */
public class CompositeParsedAttribute extends ParsedAttribute {
    /**
     * Name of the attribute.
     */
    private String name;
    /**
     * Value of the attribute in this environment.
     */
    private List<ParsedAttribute> parsedAttributes = new ArrayList<ParsedAttribute>();
    /**
     * Environment of the attribute.
     */
    private Environment environment;

    /**
     * Returns the name of the attribute.
     *
     * @return the name of the attribute
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the attribute.
     *
     * @param name attribute name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public List<ParsedAttribute> getParsedAttributes() {
        return parsedAttributes;
    }

    public void addParsedAttribute(ParsedAttribute parsedAttribute) {
        parsedAttributes.add(parsedAttribute);
    }

    public void setParsedAttributes(List<ParsedAttribute> parsedAttributes) {
        this.parsedAttributes = parsedAttributes;
    }

    /**
     * Returns the environemnt of this attribute value.
     *
     * @return the environemnt of this attribute value
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Sets the environment for this attribute value.
     *
     * @param environment environment to set
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String toString() {
        return name + " = " + parsedAttributes + " in " + environment;
    }
}

 