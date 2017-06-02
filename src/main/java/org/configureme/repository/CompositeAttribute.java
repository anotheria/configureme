package org.configureme.repository;

import java.util.ArrayList;
import java.util.List;

/**
 * The internal representation of an composite attribute.
 *
 * @author lrosenberg
 * @version $Id: $Id
 */
public class CompositeAttribute {
    /**
     * The name of the attribute.
     */
    private final String name;

    /**
     * The container for attribute values.
     */
    private List<Attribute> attributeList = new ArrayList<>();

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


    /**
     * <p>Getter for the field {@code attributeList}.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Attribute> getAttributeList() {
        return attributeList;
    }

    /**
     * <p>Setter for the field {@code attributeList}.</p>
     *
     * @param attributeList a {@link java.util.List} object.
     */
    public void setAttributeList(List<Attribute> attributeList) {
        this.attributeList = attributeList;
    }


    /**
     * <p>addAttributeList.</p>
     *
     * @param attribute a {@link org.configureme.repository.Attribute} object.
     */
    public void addAttributeList(Attribute attribute) {
        attributeList.add(attribute);
    }

    @Override
    public String toString() {
        return name + '=' + attributeList;
    }
}
