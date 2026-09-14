package com.mygame.modules;

import com.mygame.plugins.api.ModuleDescriptor;
import com.mygame.plugins.api.ModuleFactory;

/**
 * Встроенный куб, если папка plugins/ пуста.
 */
public final class BuiltinCubeFactory implements ModuleFactory {

    @Override
    public ModuleDescriptor getDescriptor() {
        // Оранжевый куб 2.0
        return new ModuleDescriptor(
                "builtin-cube",
                "Встроенный куб",
                2.0f,
                0.90f, 0.50f, 0.25f
        );
    }
}