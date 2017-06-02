package org.configureme.sources.configurationrepository;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * ReplyObject is a holder object for all rest api replies. It contains some status info and additional objects
 * with requested information.
 *
 * @author lrosenberg
 * @since 13.02.13 15:26
 * @version $Id: $Id
 */
@XmlRootElement(name = "reply")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReplyObject {
    /**
     * True if the call was successful.
     */
    @XmlElement
    private boolean success;
    /**
     * Optional message in case call failed (exception message).
     */
    @XmlElement
    private String message;

    /**
     * Map with results object.
     */
    @XmlElement
    private final Map<String, Object> results = new HashMap<>();

    /**
     * Creates a new empty result object.
     */
    public ReplyObject() {
    }

    /**
     * Creates a new result object with one result.
     *
     * @param name   name of the result bean.
     * @param result object for the first result bean.
     */
    public ReplyObject(String name, Object result) {
        results.put(name, result);
    }

    /**
     * Adds
     *
     * @param name a {@link java.lang.String} object.
     * @param result a {@link java.lang.Object} object.
     */
    public void addResult(String name, Object result) {
        results.put(name, result);
    }

    /**
     * Factory method that creates a new reply object for successful request.
     *
     * @param name a {@link java.lang.String} object.
     * @param result a {@link java.lang.Object} object.
     * @return a {@link org.configureme.sources.configurationrepository.ReplyObject} object.
     */
    public static ReplyObject success(final String name, final Object result) {
        final ReplyObject ret = new ReplyObject(name, result);
        ret.success = true;
        return ret;
    }

    /**
     * Factory method that creates a new reply object for successful request.
     *
     * @return a {@link org.configureme.sources.configurationrepository.ReplyObject} object.
     */
    public static ReplyObject success() {
        final ReplyObject ret = new ReplyObject();
        ret.success = true;
        return ret;
    }

    /**
     * Factory method that creates a new erroneous reply object.
     *
     * @param message a {@link java.lang.String} object.
     * @return a {@link org.configureme.sources.configurationrepository.ReplyObject} object.
     */
    public static ReplyObject error(final String message) {
        final ReplyObject ret = new ReplyObject();
        ret.success = false;
        ret.message = message;
        return ret;
    }

    /**
     * <p>error.</p>
     *
     * @param exc a {@link java.lang.Throwable} object.
     * @return a {@link org.configureme.sources.configurationrepository.ReplyObject} object.
     */
    public static ReplyObject error(final Throwable exc) {
        final ReplyObject ret = new ReplyObject();
        ret.success = false;
        ret.message = exc.getClass().getSimpleName() + ": " + exc.getMessage();
        return ret;
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder("ReplyObject ");
        ret.append("Success: ").append(success);
        if (message != null) {
            ret.append(", Message: ").append(message);
        }
        ret.append(", Results: ").append(results);
        return ret.toString();
    }

    /**
     * <p>Getter for the field {@code message}.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMessage() {
        return message;
    }

    /**
     * <p>isSuccess.</p>
     *
     * @return a boolean.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * <p>Setter for the field {@code success}.</p>
     *
     * @param success a boolean.
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * <p>Getter for the field {@code results}.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, Object> getResults() {
        return results;
    }

}
