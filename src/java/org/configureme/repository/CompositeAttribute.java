package org.configureme.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * The internal representation of an composite attribute.
 *
 * @author lrosenberg
 */
public class CompositeAttribute {
    /**
     * The name of the attribute.
     */
    private String name;

    /**
     * The container for attribute values.
     */
    private List<Attribute> attributeList = new ArrayList<Attribute>();

    /**
     * Creates a new attribute.
     *
     * @param aName attribute name
     */
    public CompositeAttribute(String aName) {
        name = aName;
    }

    /**
     * Returns the name of the AttributeValue.
     *
     * @return the name of the AttributeValue
     */
    public String getName() {
        return name;
    }


    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }


    public void addAttributeList(Attribute attribute) {
        attributeList.add(attribute);
    }

    @Override
    public String toString() {
        return getName() + "=" + attributeList;
    }
}
