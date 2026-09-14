package com.mygame.view;

import com.jme3.math.Vector3f;

/**
 * Константы и математика координат сетки.
 */
public final class GridConfig {

    public static final int COLS = 4;
    public static final int ROWS = 3;
    public static final float CELL_SIZE = 3.0f;
    public static final float SLOT_SIZE = 2.7f; // Размер визуальной рамки слота

    private GridConfig() {}

    /**
     * Перевод индексов слота (col, row) в мировые 3D-координаты (X, Y, Z=0).
     */
    public static Vector3f getSlotWorldPos(int col, int row) {
        float x = (col - (COLS - 1) / 2.0f) * CELL_SIZE;
        float y = (row - (ROWS - 1) / 2.0f) * CELL_SIZE;
        return new Vector3f(x, y, 0f);
    }
}