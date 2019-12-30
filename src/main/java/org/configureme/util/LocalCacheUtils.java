package org.configureme.util;

import java.util.HashMap;
import java.util.Map;

import org.configureme.Environment;

/**
 * Cache for object in oder to cover situation with loops in ConfigureAlso.
 *
 * @author Ivan Batura
 */
public final class LocalCacheUtils {

    /**
     * Cache.
     */
    private static final ThreadLocal<Map<String, Map<Environment, Object>>> LOCAL_CACHE = new ThreadLocal<>();

    /**
     * Private constructor.
     */
    private LocalCacheUtils() {
        throw new UnsupportedOperationException("Cannot be initiated");
    }

    /**
     * Get cached object in order to handle situation with loop.
     *
     * @param name
     *         name of the config
     * @param environment
     *         environment
     * @return instance of the already configures object
     */
    public static Object getCachedObject(final String name, final Environment environment) {
        Map<String, Map<Environment, Object>> globalCache = LOCAL_CACHE.get();
        if (globalCache == null) {
            globalCache = new HashMap<>();
            LOCAL_CACHE.set(globalCache);
        }
        Map<Environment, Object> environmentCache = globalCache.get(name);
        if (environmentCache == null) {
            environmentCache = new HashMap<>();
            globalCache.put(name, environmentCache);
        }

        return environmentCache.get(environment);
    }

    /**
     * Put configured object to the cache.
     *
     * @param name
     *         name of the config
     * @param environment
     *         environment
     * @param o
     *         object to cache
     */
    public static void setCachedObject(final String name, final Environment environment, final Object o) {
        Map<String, Map<Environment, Object>> cache = LOCAL_CACHE.get();
        if (cache == null) {
            cache = new HashMap<>();
            LOCAL_CACHE.set(cache);
        }
        Map<Environment, Object> environmentCache = cache.get(name);
        if (environmentCache == null) {
            environmentCache = new HashMap<>();
            cache.put(name, environmentCache);
        }
        environmentCache.put(environment, o);
    }

}
