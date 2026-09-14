package com.mygame.app;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.mygame.modules.BuiltinCubeFactory;
import com.mygame.plugins.api.ModuleDescriptor;
import com.mygame.plugins.api.ModuleFactory;
import com.mygame.plugins.registry.JarModuleLoader;
import com.mygame.view.GridManager;
import com.mygame.view.ModuleGeometryFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main extends SimpleApplication {

    private static final float CAM_Z = 14f;
    private static final float NEAR  = 1f;
    private static final float FAR   = 100f;
    private static final String CLICK_ACTION = "MOUSE_CLICK";

    private GridManager gridManager;
    private final List<ModuleFactory> moduleFactories = new ArrayList<>();
    private ModuleFactory selectedFactory;

    public static void main(String[] args) {
        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Виртуальная лаборатория — Электротехника");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setFrameRate(60);

        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        setupCamera();
        setupLights();

        flyCam.setEnabled(false);
        // Курсор видим для кликов по «+»
        inputManager.setCursorVisible(true);
        viewPort.setBackgroundColor(new ColorRGBA(0.15f, 0.15f, 0.18f, 1f));

        gridManager = new GridManager();
        gridManager.init(rootNode, assetManager);

        loadModulePlugins();
        setupInput();
    }

    private void loadModulePlugins() {
        // Папка plugins/ рядом с рабочей директорией (корень проекта при Run из IDE)
        File pluginsDir = new File("plugins");
        List<ModuleFactory> fromJars = JarModuleLoader.load(pluginsDir);
        moduleFactories.addAll(fromJars);

        if (moduleFactories.isEmpty()) {
            System.out.println("[plugins] Используем встроенный куб (fallback)");
            moduleFactories.add(new BuiltinCubeFactory());
        }

        selectedFactory = moduleFactories.get(0);

        System.out.println("[plugins] Активный модуль: "
                + selectedFactory.getDescriptor().getName());
    }

    private void setupCamera() {
        cam.setLocation(new Vector3f(0, 0, CAM_Z));
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y);
        cam.setParallelProjection(true);
        updateFrustum();
    }

    private void setupLights() {
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.40f, 0.40f, 0.40f, 1f));
        rootNode.addLight(ambient);

        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -0.8f, -0.3f).normalizeLocal());
        sun.setColor(new ColorRGBA(0.90f, 0.90f, 0.90f, 1f));
        rootNode.addLight(sun);

        // Мягкие тени — куб лучше читается как 3D
        DirectionalLightShadowRenderer dlsr =
                new DirectionalLightShadowRenderer(assetManager, 1024, 2);
        dlsr.setLight(sun);
        dlsr.setShadowIntensity(0.55f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        viewPort.addProcessor(dlsr);
    }

    private void setupInput() {
        inputManager.addMapping(CLICK_ACTION, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener((ActionListener) (name, isPressed, tpf) -> {
            if (CLICK_ACTION.equals(name) && !isPressed) {
                handleClick();
            }
        }, CLICK_ACTION);
    }

    private void handleClick() {
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f origin = cam.getWorldCoordinates(click2d, 0f);
        Vector3f dir = cam.getWorldCoordinates(click2d, 1f)
                .subtractLocal(origin)
                .normalizeLocal();

        Ray ray = new Ray(origin, dir);
        CollisionResults results = new CollisionResults();
        rootNode.collideWith(ray, results);

        for (CollisionResult res : results) {
            Geometry geom = res.getGeometry();
            if (geom != null && geom.getName().startsWith("Plus_")) {
                Integer col = geom.getUserData("col");
                Integer row = geom.getUserData("row");
                if (col != null && row != null && !gridManager.isOccupied(col, row)) {
                    placeModule(col, row);
                }
                break;
            }
        }
    }

    private void placeModule(int col, int row) {
        if (selectedFactory == null) {
            return;
        }

        ModuleDescriptor desc = selectedFactory.getDescriptor();
        Node moduleNode = ModuleGeometryFactory.createCube(assetManager, desc, col, row);

        gridManager.setOccupied(col, row, true);
        gridManager.getModulesNode().attachChild(moduleNode);
    }

    @Override
    public void simpleUpdate(float tpf) {
        updateFrustum();
    }

    private void updateFrustum() {
        float aspect = (float) cam.getWidth() / (float) cam.getHeight();
        float vh = 12f;
        float vw = vh * aspect;
        cam.setFrustum(NEAR, FAR, -vw / 2f, vw / 2f, vh / 2f, -vh / 2f);
    }
}