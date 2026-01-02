package com.miniioc.core.scanner;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PackageScanner {

    public List<Class<?>> findClasses(String packageName) throws ClassNotFoundException {
        String path = packageName.replace('.', '/');
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (url == null) return new ArrayList<>();
        File directory = new File(url.getFile());
        List<Class<?>> classes = new ArrayList<>();
        findClasses(directory, packageName, classes);
        return classes;
    }

    private void findClasses(File directory, String packageName, List<Class<?>> classes) throws ClassNotFoundException {
        if (!directory.exists()) return;

        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                findClasses(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replaceAll("\\.class$", "");
                classes.add(Class.forName(className));
            }
        }
    }
}
