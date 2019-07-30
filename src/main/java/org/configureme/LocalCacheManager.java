package org.configureme;

import java.util.HashMap;
import java.util.Map;

import org.configureme.resolver.ResolveManager;

/**
 * Cache for object in oder to cover situation with loops in ConfigureAlso.
 *
 * @author Ivan Batura
 */
public class LocalCacheManager {

    /**
     * Lock object for singleton creation.
     */
    private static final Object LOCK = new Object();
    /**
     * {@link ResolveManager} instance,
     */
    private static LocalCacheManager instance;
    /**
     * Cache.
     */
    private final ThreadLocal<Map<String, Map<Environment, Object>>> localCache = new ThreadLocal<>();

    /**
     * Private constructor.
     */
    private LocalCacheManager() {
    }

    /**
     * Get singleton instance of {@link LocalCacheManager}.
     *
     * @return {@link LocalCacheManager}
     */
    public static LocalCacheManager instance() {
        if (instance != null)
            return instance;
        synchronized (LOCK) {
            if (instance != null)
                return instance;

            instance = new LocalCacheManager();
            return instance;
        }
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
    public Object getCachedObject(final String name, final Environment environment) {
        Map<String, Map<Environment, Object>> globalCache = localCache.get();
        if (globalCache == null) {
            globalCache = new HashMap<>();
            localCache.set(globalCache);
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
    public void setCachedObject(final String name, final Environment environment, final Object o) {
        Map<String, Map<Environment, Object>> cache = localCache.get();
        if (cache == null) {
            cache = new HashMap<>();
            localCache.set(cache);
        }
        Map<Environment, Object> environmentCache = cache.get(name);
        if (environmentCache == null) {
            environmentCache = new HashMap<>();
            cache.put(name, environmentCache);
        }
        environmentCache.put(environment, o);
    }
}
