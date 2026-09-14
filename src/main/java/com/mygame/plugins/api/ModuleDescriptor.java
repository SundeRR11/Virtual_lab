package com.mygame.plugins.api;

/**
 * Описание модуля из плагина. Чистая Java, без jME.
 */
public final class ModuleDescriptor {

    private final String id;
    private final String name;
    private final float size; // длина ребра куба в мировых единицах
    private final float r, g, b;

    public ModuleDescriptor(String id, String name, float size, float r, float g, float b) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public float getSize() { return size; }
    public float getR() { return r; }
    public float getG() { return g; }
    public float getB() { return b; }
}