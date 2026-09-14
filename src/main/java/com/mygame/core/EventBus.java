package com.mygame.core;

/**
 * Простая шина событий. Заглушка — наполним на следующих шагах.
 */
public final class EventBus {

    private static final EventBus INSTANCE = new EventBus();

    private EventBus() {}

    public static EventBus get() {
        return INSTANCE;
    }

    // TODO: subscribe / publish
}