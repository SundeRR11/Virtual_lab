package com.mygame.plugins.api;

/**
 * Точка входа плагина. Реализация кладётся в jar
 * и регистрируется в META-INF/services.
 */
public interface ModuleFactory {

    ModuleDescriptor getDescriptor();
}