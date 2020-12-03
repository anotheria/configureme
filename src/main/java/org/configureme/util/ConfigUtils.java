package org.configureme.util;

/**
 * Util for configuration staff.
 *
 * @author Ivan Batura
 */
public final class ConfigUtils {

    private ConfigUtils() {
    }

    /**
     * Calculates default configuration artefact name for a java class.
     *
     * @param targetClazz target class
     * @return default configuration artefact name for a given java class. For MyConfigurable it would be "myconfigurable"
     */
    public static String extractConfigurationNameFromClassName(final Class<?> targetClazz) {
        return targetClazz.getName().substring(targetClazz.getName().lastIndexOf('.') + 1).toLowerCase();
    }

}
