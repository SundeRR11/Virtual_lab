package com.mygame.plugins.registry;

import com.mygame.plugins.api.ModuleFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Сканирует папку plugins/*.jar и подгружает ModuleFactory через ServiceLoader.
 */
public final class JarModuleLoader {

    private JarModuleLoader() {}

    public static List<ModuleFactory> load(File pluginsDir) {
        List<ModuleFactory> result = new ArrayList<>();

        if (pluginsDir == null || !pluginsDir.isDirectory()) {
            System.out.println("[plugins] Папка не найдена: " + pluginsDir);
            return result;
        }

        File[] jars = pluginsDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            System.out.println("[plugins] Jar-файлы не найдены в " + pluginsDir.getAbsolutePath());
            return result;
        }

        List<URL> urls = new ArrayList<>();
        for (File jar : jars) {
            try {
                urls.add(jar.toURI().toURL());
                System.out.println("[plugins] Найден: " + jar.getName());
            } catch (Exception e) {
                System.err.println("[plugins] Не удалось прочитать " + jar.getName() + ": " + e.getMessage());
            }
        }

        if (urls.isEmpty()) {
            return result;
        }

        // parent = текущий classloader приложения (видит api)
        URLClassLoader loader = new URLClassLoader(
                urls.toArray(new URL[0]),
                ModuleFactory.class.getClassLoader()
        );

        ServiceLoader<ModuleFactory> serviceLoader =
                ServiceLoader.load(ModuleFactory.class, loader);

        for (ModuleFactory factory : serviceLoader) {
            result.add(factory);
            System.out.println("[plugins] Загружен модуль: "
                    + factory.getDescriptor().getName()
                    + " (" + factory.getDescriptor().getId() + ")");
        }

        return result;
    }
}