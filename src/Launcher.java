import java.io.*;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.*;

/**
 * JavaFX Application Launcher
 * Extracts native JavaFX libraries and sets up the environment
 */
public class Launcher {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Portfolio Tracker Launcher ===");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        
        // Try direct launch first
        try {
            System.out.println("Attempting to launch PortfolioTrackerApp...");
            PortfolioTrackerApp.main(new String[0]);
        } catch (Exception e) {
            System.err.println("Failed to launch: " + e.getMessage());
            e.printStackTrace();
            
            // If that fails, try extracting native libraries
            System.out.println("\nAttempting alternative setup...");
            extractAndRunAlternative();
        }
    }
    
    private static void extractAndRunAlternative() throws Exception {
        Path nativeDir = Paths.get("native");
        Files.createDirectories(nativeDir);
        
        // Extract native libraries from JAR files
        File libDir = new File("lib");
        if (libDir.exists()) {
            for (File jar : libDir.listFiles((d, n) -> n.endsWith(".jar"))) {
                extractNativesFromJar(jar, nativeDir);
            }
        }
        
        // Set library path
        System.setProperty("java.library.path", nativeDir.toString());
        
        // Try again
        try {
            PortfolioTrackerApp.main(new String[0]);
        } catch (Exception e) {
            System.err.println("Still failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void extractNativesFromJar(File jar, Path dest) throws Exception {
        try (JarFile jarFile = new JarFile(jar)) {
            jarFile.stream()
                .filter(entry -> entry.getName().startsWith("com/sun/glass") || 
                                entry.getName().contains(".dll"))
                .forEach(entry -> {
                    try (InputStream is = jarFile.getInputStream(entry)) {
                        String fileName = new File(entry.getName()).getName();
                        Path outPath = dest.resolve(fileName);
                        Files.copy(is, outPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        // Silent fail for individual files
                    }
                });
        } catch (Exception e) {
            // Silent fail for individual JARs
        }
    }
}
