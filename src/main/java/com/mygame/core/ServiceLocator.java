
package com.mygame.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Простейший локатор сервисов. Заглушка.
 */
public final class ServiceLocator {

    private static final Map<Class<?>, Object> services = new ConcurrentHashMap<>();

    private ServiceLocator() {}

    public static <T> void register(Class<T> type, T impl) {
        services.put(type, impl);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> type) {
        T impl = (T) services.get(type);
        if (impl == null) {
            throw new IllegalStateException("Сервис не зарегистрирован: " + type.getName());
        }
        return impl;
    }
}
