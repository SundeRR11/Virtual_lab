package com.mygame.modules.cube;

import com.mygame.plugins.api.ModuleDescriptor;
import com.mygame.plugins.api.ModuleFactory;

public final class CubeModuleFactory implements ModuleFactory {

    @Override
    public ModuleDescriptor getDescriptor() {
        return new ModuleDescriptor(
                "cube-blue",
                "Синий куб (jar)",
                1.6f,
                0.20f, 0.45f, 0.95f
        );
    }
}