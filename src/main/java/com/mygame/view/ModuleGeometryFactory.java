package com.mygame.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.mygame.plugins.api.ModuleDescriptor;

/**
 * Создаёт 3D-представление модуля на сцене.
 */
public final class ModuleGeometryFactory {

    private ModuleGeometryFactory() {}

    public static Node createCube(AssetManager assets, ModuleDescriptor desc, int col, int row) {
        float half = desc.getSize() / 2f;

        Box box = new Box(half, half, half);
        Geometry geom = new Geometry("ModuleGeom_" + desc.getId(), box);

        // Lighting — чтобы куб читался объёмно при DirectionalLight
        Material mat = new Material(assets, "Common/MatDefs/Light/Lighting.j3md");
        ColorRGBA color = new ColorRGBA(desc.getR(), desc.getG(), desc.getB(), 1f);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", color.mult(0.45f));
        mat.setColor("Specular", ColorRGBA.White.mult(0.2f));
        mat.setFloat("Shininess", 16f);
        geom.setMaterial(mat);

        Node wrapper = new Node("Module_" + col + "_" + row + "_" + desc.getId());
        wrapper.attachChild(geom);

        Vector3f pos = GridConfig.getSlotWorldPos(col, row);
        // Центр куба в центре слота, слегка приподнят
        wrapper.setLocalTranslation(pos.x, pos.y, half);

        wrapper.setUserData("col", col);
        wrapper.setUserData("row", row);
        wrapper.setUserData("moduleId", desc.getId());

        return wrapper;
    }
}