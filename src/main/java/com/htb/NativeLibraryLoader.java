package com.htb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class NativeLibraryLoader {

    private static final String NATIVE_DIR = "META-INF/native/";
    private static boolean loaded = false;

    public static synchronized void loadLibraries() {
        if (loaded) return;
        try {
            extractAndLoadNativeLibs();
            loaded = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load native libraries", e);
        }
    }

    private static void extractAndLoadNativeLibs() throws IOException {
        String jarPath;
        try {
            jarPath = HiperTacoBrons.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (Exception e) {
            throw new IOException("Failed to resolve jar path", e);
        }
        Path tempDir = Files.createTempDirectory("javafx-native-");
        List<String> dllPaths = new ArrayList<>();

        if (jarPath.endsWith(".jar")) {
            try (JarFile jar = new JarFile(jarPath)) {
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(NATIVE_DIR) && !entry.isDirectory()) {
                        String fileName = name.substring(name.lastIndexOf('/') + 1);
                        File outFile = new File(tempDir.toFile(), fileName);
                        try (InputStream is = jar.getInputStream(entry);
                             OutputStream os = new FileOutputStream(outFile)) {
                            byte[] buf = new byte[8192];
                            int len;
                            while ((len = is.read(buf)) != -1) {
                                os.write(buf, 0, len);
                            }
                        }
                        dllPaths.add(outFile.getAbsolutePath());
                    }
                }
            }
        } else {
            Path nativePath = Paths.get(jarPath, NATIVE_DIR);
            if (Files.isDirectory(nativePath)) {
                Files.walk(nativePath)
                        .filter(p -> p.toString().endsWith(".dll"))
                        .forEach(p -> {
                            try {
                                File outFile = new File(tempDir.toFile(), p.getFileName().toString());
                                Files.copy(p, outFile.toPath());
                                dllPaths.add(outFile.getAbsolutePath());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        }

        for (String dll : dllPaths) {
            System.load(dll);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.walk(tempDir)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> p.toFile().delete());
            } catch (IOException ignored) {
            }
        }));
    }
}
