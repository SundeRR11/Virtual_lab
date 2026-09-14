package com.mygame.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

/**
 * Управляет сеткой COLS x ROWS, рисует слоты и кнопки "+".
 */
public class GridManager {

    private final Node gridNode = new Node("GridNode");
    private final Node plusButtonsNode = new Node("PlusButtonsNode");
    private final Node modulesNode = new Node("ModulesNode");

    private final boolean[][] occupied = new boolean[GridConfig.COLS][GridConfig.ROWS];
    private final Geometry[][] plusGeoms = new Geometry[GridConfig.COLS][GridConfig.ROWS];

    private int totalOccupied = 0;

    public void init(Node rootNode, AssetManager assetManager) {
        rootNode.attachChild(gridNode);
        rootNode.attachChild(plusButtonsNode);
        rootNode.attachChild(modulesNode);

        createGridSlots(assetManager);
        createPlusButtons(assetManager);
        updatePlusVisibility();
    }

    private void createGridSlots(AssetManager assetManager) {
        float size = GridConfig.SLOT_SIZE;

        for (int col = 0; col < GridConfig.COLS; col++) {
            for (int row = 0; row < GridConfig.ROWS; row++) {
                Quad quad = new Quad(size, size);
                Geometry slotGeom = new Geometry("Slot_" + col + "_" + row, quad);

                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", new ColorRGBA(0.25f, 0.27f, 0.32f, 1f));
                slotGeom.setMaterial(mat);

                Vector3f pos = GridConfig.getSlotWorldPos(col, row);
                // Сдвиг на half-size, т.к. Quad строится от нижнего-левого угла
                slotGeom.setLocalTranslation(pos.x - size / 2f, pos.y - size / 2f, -0.1f);

                gridNode.attachChild(slotGeom);
            }
        }
    }

    private void createPlusButtons(AssetManager assetManager) {
        float btnSize = 1.0f;

        for (int col = 0; col < GridConfig.COLS; col++) {
            for (int row = 0; row < GridConfig.ROWS; row++) {
                Quad quad = new Quad(btnSize, btnSize);
                Geometry plusGeom = new Geometry("Plus_" + col + "_" + row, quad);

                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.setColor("Color", new ColorRGBA(0.2f, 0.8f, 0.3f, 1f));
                plusGeom.setMaterial(mat);

                Vector3f pos = GridConfig.getSlotWorldPos(col, row);
                plusGeom.setLocalTranslation(pos.x - btnSize / 2f, pos.y - btnSize / 2f, 0.1f);

                // Сохраняем координаты слота в UserData геометрии
                plusGeom.setUserData("col", col);
                plusGeom.setUserData("row", row);

                plusButtonsNode.attachChild(plusGeom);
                plusGeoms[col][row] = plusGeom;
            }
        }
    }

    /**
     * Пересчёт видимости кнопок "+" по правилу соседства.
     */
    public void updatePlusVisibility() {
        for (int col = 0; col < GridConfig.COLS; col++) {
            for (int row = 0; row < GridConfig.ROWS; row++) {
                if (occupied[col][row]) {
                    // Слот занят — кнопку "+" не показываем никогда
                    plusGeoms[col][row].setCullHint(Spatial.CullHint.Always);
                    continue;
                }

                if (totalOccupied == 0) {
                    // Если поле пустое — показываем везде
                    plusGeoms[col][row].setCullHint(Spatial.CullHint.Never);
                } else {
                    // Если не пустое — показываем только у занятых соседей
                    boolean hasNeighbor = hasOccupiedNeighbor(col, row);
                    plusGeoms[col][row].setCullHint(hasNeighbor ? Spatial.CullHint.Never : Spatial.CullHint.Always);
                }
            }
        }
    }

    private boolean hasOccupiedNeighbor(int col, int row) {
        if (col > 0 && occupied[col - 1][row]) return true;
        if (col < GridConfig.COLS - 1 && occupied[col + 1][row]) return true;
        if (row > 0 && occupied[col][row - 1]) return true;
        if (row < GridConfig.ROWS - 1 && occupied[col][row + 1]) return true;
        return false;
    }

    public void setOccupied(int col, int row, boolean state) {
        if (occupied[col][row] != state) {
            occupied[col][row] = state;
            totalOccupied += state ? 1 : -1;
            updatePlusVisibility();
        }
    }

    public boolean isOccupied(int col, int row) {
        return occupied[col][row];
    }

    public Node getModulesNode() {
        return modulesNode;
    }
}