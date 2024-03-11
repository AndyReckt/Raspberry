package me.andyreckt.raspberry.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@UtilityClass
public class RaspberryUtils {
    public boolean startsWithIgnoreCase(String str, String prefix) {
        return startsWith(str, prefix, true);
    }

    private boolean startsWith(String str, String prefix, boolean ignoreCase) {
        if (str != null && prefix != null) {
            return prefix.length() <= str.length() && str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
        } else {
            return str == null && prefix == null;
        }
    }

    public <T> List<T> performOnImmutable(List<T> list, Consumer<List<T>> action) {
        try {
            action.accept(list);
        } catch (UnsupportedOperationException ex) {
            list = new ArrayList<>(list);
            action.accept(list);
        }

        return list;
    }

    public Collection<Class<?>> getClassesInPackage(Class<?> main, String packageName) {
        Collection<Class<?>> classes = new ArrayList<>();

        CodeSource codeSource = main.getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        JarFile jarFile;

        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw (new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e));
        }

        Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;

            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }

            if (className != null) {
                Class<?> clazz = null;

                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }

        try {
            jarFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.unmodifiableCollection(classes);
    }

    @SneakyThrows
    public void setFinalStatic(Field field, Object newValue) {
        field.setAccessible(true);

        Field modifiersField = getModifiersField();
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    @SneakyThrows
    public void setFinal(Field field, Object instance, Object newValue) {
        field.setAccessible(true);

        Field modifiersField = getModifiersField();
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(instance, newValue);
    }

    // --add-opens java.base/java.lang=ALL-UNNAMED
    public Field getModifiersField() throws NoSuchFieldException {
        try {
            return Field.class.getDeclaredField("modifiers");
        } catch (NoSuchFieldException e) {
            try {
                Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
                getDeclaredFields0.setAccessible(true);
                Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
                for (Field field : fields) {
                    if ("modifiers".equals(field.getName())) {
                        return field;
                    }
                }
            } catch (ReflectiveOperationException ex) {
                e.addSuppressed(ex);
            }
            throw e;
        }
    }
}
