/**
 * Application-wide constants and configuration values.
 * Centralizes magic strings and numbers for better maintainability.
 */
public class AppConstants {
    // Application Info
    public static final String APP_NAME = "Portfolio Tracker";
    public static final String APP_VERSION = "1.0.0";
    public static final String CONFIG_FILE = "ai_config.properties";
    public static final String EXPORT_FILE = "portfolio_export.csv";

    // UI Constants
    public static final int WINDOW_MIN_WIDTH = 1000;
    public static final int WINDOW_MIN_HEIGHT = 700;
    public static final int WINDOW_DEFAULT_WIDTH = 1200;
    public static final int WINDOW_DEFAULT_HEIGHT = 800;
    public static final int PADDING_STANDARD = 15;
    public static final int SPACING_STANDARD = 10;

    // Color Constants (Dark Mode)
    public static final String COLOR_DARK_BG = "#1e1e1e";
    public static final String COLOR_DARK_PRIMARY = "#252526";
    public static final String COLOR_DARK_SECONDARY = "#2d2d30";
    public static final String COLOR_ACCENT_GREEN = "#00ff88";
    public static final String COLOR_ACCENT_ORANGE = "#ff6b00";
    public static final String COLOR_ERROR = "#ff3333";
    public static final String COLOR_SUCCESS = "#00aa00";
    public static final String COLOR_INFO = "#0066cc";
    public static final String COLOR_WARNING = "#6600cc";
    public static final String COLOR_BORDER = "#333333";
    public static final String COLOR_TEXT_PRIMARY = "#ffffff";
    public static final String COLOR_TEXT_SECONDARY = "#888888";

    // AI Service Configuration
    public static final String DEFAULT_AI_SERVICE = "local";
    public static final String AI_SERVICE_ONLINE = "online";
    public static final String AI_SERVICE_LOCAL = "local";
    public static final String DEFAULT_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    public static final String DEFAULT_API_MODEL = "gpt-3.5-turbo";

    // Validation Constants
    public static final int MIN_QUANTITY = 1;
    public static final double MIN_PRICE = 0.01;
    public static final int MAX_API_RETRIES = 3;
    public static final long RETRY_DELAY_MS = 1000;

    // Logging Levels
    public static final String LOG_LEVEL_INFO = "INFO";
    public static final String LOG_LEVEL_WARNING = "WARNING";
    public static final String LOG_LEVEL_ERROR = "ERROR";
    public static final String LOG_LEVEL_DEBUG = "DEBUG";

    private AppConstants() {
        // Prevent instantiation
        throw new AssertionError("Cannot instantiate constants class");
    }
}
