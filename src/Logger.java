import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Centralized logging utility for the application.
 * Provides thread-safe logging with file and console output.
 */
public class Logger {
    private static final String LOG_FILE = "portfolio_tracker.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final boolean ENABLE_FILE_LOG = true;
    private static final Object lock = new Object();

    private Logger() {
        // Prevent instantiation
        throw new AssertionError("Cannot instantiate Logger class");
    }

    /**
     * Logs an informational message.
     */
    public static void info(String message) {
        log(AppConstants.LOG_LEVEL_INFO, message, null);
    }

    /**
     * Logs a warning message.
     */
    public static void warning(String message) {
        log(AppConstants.LOG_LEVEL_WARNING, message, null);
    }

    /**
     * Logs an error message with exception details.
     */
    public static void error(String message, Throwable throwable) {
        log(AppConstants.LOG_LEVEL_ERROR, message, throwable);
    }

    /**
     * Logs an error message.
     */
    public static void error(String message) {
        log(AppConstants.LOG_LEVEL_ERROR, message, null);
    }

    /**
     * Logs a debug message.
     */
    public static void debug(String message) {
        log(AppConstants.LOG_LEVEL_DEBUG, message, null);
    }

    private static void log(String level, String message, Throwable throwable) {
        synchronized (lock) {
            String timestamp = LocalDateTime.now().format(FORMATTER);
            String logMessage = String.format("[%s] [%s] %s", timestamp, level, message);

            // Console output
            System.out.println(logMessage);
            if (throwable != null) {
                throwable.printStackTrace();
            }

            // File output
            if (ENABLE_FILE_LOG) {
                try {
                    String fileContent = logMessage;
                    if (throwable != null) {
                        fileContent += "\n" + getStackTrace(throwable);
                    }
                    fileContent += "\n";

                    Files.write(Paths.get(LOG_FILE), fileContent.getBytes(),
                        java.nio.file.StandardOpenOption.CREATE,
                        java.nio.file.StandardOpenOption.APPEND);
                } catch (IOException e) {
                    System.err.println("Failed to write to log file: " + e.getMessage());
                }
            }
        }
    }

    private static String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element).append("\n");
        }
        return sb.toString();
    }
}
