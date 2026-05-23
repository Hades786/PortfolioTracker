import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Factory for managing AI service instances.
 * Singleton pattern ensures single configuration source across the application.
 * Handles loading/saving configuration and switching between services.
 */
public class AIServiceFactory {
    private static AIService currentService;
    private static AIServiceFactory instance;
    private Properties config;
    private OnlineAIService onlineService;
    private LocalAIService localService;

    private AIServiceFactory() {
        config = new Properties();
        loadConfig();
        initializeServices();
    }

    /**
     * Gets the singleton instance of AIServiceFactory.
     * @return the AIServiceFactory instance
     */
    public static AIServiceFactory getInstance() {
        if (instance == null) {
            instance = new AIServiceFactory();
        }
        return instance;
    }

    /**
     * Loads configuration from file.
     * Creates default configuration if file doesn't exist.
     */
    private void loadConfig() {
        try {
            if (Files.exists(Paths.get(AppConstants.CONFIG_FILE))) {
                try (FileInputStream fis = new FileInputStream(AppConstants.CONFIG_FILE)) {
                    config.load(fis);
                    Logger.info("Configuration loaded from " + AppConstants.CONFIG_FILE);
                }
            } else {
                Logger.info("Configuration file not found. Creating with defaults.");
                setDefaults();
                saveConfig();
            }
        } catch (IOException e) {
            Logger.error("Could not load config file. Using defaults.", e);
            setDefaults();
        }
    }

    /**
     * Sets default configuration values.
     */
    private void setDefaults() {
        config.setProperty("ai_service", AppConstants.DEFAULT_AI_SERVICE);
        config.setProperty("api_endpoint", AppConstants.DEFAULT_API_ENDPOINT);
        config.setProperty("api_model", AppConstants.DEFAULT_API_MODEL);
        config.setProperty("api_key", "");
    }

    /**
     * Initializes both AI service implementations.
     */
    private void initializeServices() {
        String apiKey = config.getProperty("api_key", "");
        String endpoint = config.getProperty("api_endpoint", AppConstants.DEFAULT_API_ENDPOINT);
        String model = config.getProperty("api_model", AppConstants.DEFAULT_API_MODEL);

        onlineService = new OnlineAIService(apiKey, endpoint, model);
        localService = new LocalAIService();

        String serviceType = config.getProperty("ai_service", AppConstants.DEFAULT_AI_SERVICE);
        switchService(serviceType);
    }

    /**
     * Gets the current active AI service.
     * @return the current AIService instance
     */
    public AIService getService() {
        if (currentService == null) {
            Logger.warning("Current service is null, using local service");
            currentService = localService;
        }
        return currentService;
    }

    /**
     * Switches between AI services.
     * Falls back to local service if online service is not available.
     * @param serviceType "online" or "local"
     */
    public void switchService(String serviceType) {
        try {
            if ("online".equalsIgnoreCase(serviceType)) {
                if (onlineService.isAvailable()) {
                    currentService = onlineService;
                    config.setProperty("ai_service", "online");
                    Logger.info("Switched to Online AI Service");
                } else {
                    Logger.warning("Online service not configured. Falling back to local.");
                    currentService = localService;
                    config.setProperty("ai_service", "local");
                }
            } else {
                currentService = localService;
                config.setProperty("ai_service", "local");
                Logger.info("Switched to Local AI Service");
            }
            saveConfig();
        } catch (Exception e) {
            Logger.error("Error switching service", e);
            currentService = localService;
        }
    }

    /**
     * Sets the API key for online service.
     * @param apiKey the OpenAI API key
     */
    public void setAPIKey(String apiKey) {
        if (!ValidationUtil.isValidApiKey(apiKey)) {
            Logger.warning("API key validation failed");
            throw new IllegalArgumentException("Invalid API key format");
        }
        config.setProperty("api_key", apiKey);
        onlineService.setApiKey(apiKey);
        saveConfig();
        Logger.info("API key updated");
    }

    /**
     * Sets the API endpoint.
     * @param endpoint the API endpoint URL
     */
    public void setAPIEndpoint(String endpoint) {
        if (!ValidationUtil.isValidEndpoint(endpoint)) {
            Logger.warning("API endpoint validation failed");
            throw new IllegalArgumentException("Invalid API endpoint format");
        }
        config.setProperty("api_endpoint", endpoint);
        saveConfig();
        Logger.info("API endpoint updated");
    }

    /**
     * Sets the API model name.
     * @param model the model name (e.g., gpt-3.5-turbo)
     */
    public void setAPIModel(String model) {
        if (model == null || model.trim().isEmpty()) {
            Logger.warning("Model name cannot be empty");
            throw new IllegalArgumentException("Model name cannot be empty");
        }
        config.setProperty("api_model", model);
        saveConfig();
        Logger.info("API model updated to: " + model);
    }

    /**
     * Gets the current API key.
     * @return the API key (masked for security in logs)
     */
    public String getAPIKey() {
        return config.getProperty("api_key", "");
    }

    /**
     * Gets the current API endpoint.
     * @return the API endpoint URL
     */
    public String getAPIEndpoint() {
        return config.getProperty("api_endpoint", "");
    }

    /**
     * Gets the current API model.
     * @return the model name
     */
    public String getAPIModel() {
        return config.getProperty("api_model", "");
    }

    /**
     * Gets the name of the current service ("online" or "local").
     * @return the current service name
     */
    public String getCurrentServiceName() {
        return config.getProperty("ai_service", "local");
    }

    /**
     * Gets the online service instance.
     * @return the OnlineAIService instance
     */
    public OnlineAIService getOnlineService() {
        return onlineService;
    }

    /**
     * Gets the local service instance.
     * @return the LocalAIService instance
     */
    public LocalAIService getLocalService() {
        return localService;
    }

    /**
     * Saves configuration to file.
     */
    public void saveConfig() {
        try (FileOutputStream fos = new FileOutputStream(AppConstants.CONFIG_FILE)) {
            config.store(fos, "PortfolioTracker AI Configuration");
            Logger.debug("Configuration saved");
        } catch (IOException e) {
            Logger.error("Could not save config file", e);
        }
    }
}
