import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implementation of AIService using online OpenAI API.
 * Handles API communication with proper error handling and retry logic.
 */
public class OnlineAIService implements AIService {
    private String apiKey;
    private String apiEndpoint;
    private String model;
    private HttpClient httpClient;
    private boolean available;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

    /**
     * Constructs OnlineAIService with API credentials.
     * @param apiKey the OpenAI API key
     * @param apiEndpoint the API endpoint URL
     * @param model the model to use (e.g., gpt-3.5-turbo)
     */
    public OnlineAIService(String apiKey, String apiEndpoint, String model) {
        this.apiKey = apiKey;
        this.apiEndpoint = apiEndpoint;
        this.model = model;
        this.httpClient = HttpClient.newHttpClient();
        this.available = isApiKeyValid();
        Logger.info("OnlineAIService initialized. Available: " + this.available);
    }

    @Override
    public String getAnalysis(String portfolioSummary) {
        if (!available) {
            Logger.warning("Online AI service is not configured");
            return "Online AI service is not configured. Please set your API key in settings.";
        }

        try {
            String prompt = "Analyze this investment portfolio and provide insights:\n\n" + portfolioSummary;
            Logger.info("Requesting portfolio analysis from online service");
            return callAPIWithRetry(prompt, AppConstants.MAX_API_RETRIES);
        } catch (Exception e) {
            Logger.error("Error calling online AI service", e);
            return "Error calling online AI service: " + e.getMessage();
        }
    }

    @Override
    public String getRecommendation(String portfolioSummary) {
        if (!available) {
            Logger.warning("Online AI service is not configured for recommendations");
            return "Online AI service is not configured.";
        }

        try {
            String prompt = "Based on this portfolio, what are your top 3 investment recommendations?\n\n" + portfolioSummary;
            Logger.info("Requesting investment recommendations from online service");
            return callAPIWithRetry(prompt, AppConstants.MAX_API_RETRIES);
        } catch (Exception e) {
            Logger.error("Error getting recommendations", e);
            return "Error getting recommendations: " + e.getMessage();
        }
    }

    /**
     * Calls API with retry logic and exponential backoff.
     * @param prompt the prompt to send
     * @param maxRetries maximum number of retries
     * @return API response text
     */
    private String callAPIWithRetry(String prompt, int maxRetries) throws IOException, InterruptedException {
        for (int i = 0; i < maxRetries; i++) {
            try {
                Logger.debug("API call attempt " + (i + 1) + " of " + maxRetries);
                return callOpenAIAPI(prompt);
            } catch (IOException e) {
                if (i == maxRetries - 1) {
                    Logger.error("API call failed after " + maxRetries + " retries", e);
                    throw e;
                }
                long delayMs = AppConstants.RETRY_DELAY_MS * (long) Math.pow(2, i);
                Logger.warning("API call failed, retrying in " + delayMs + "ms. Attempt " + (i + 1) + "/" + maxRetries);
                Thread.sleep(delayMs); // Exponential backoff
            }
        }
        return "Failed to get response from AI service after " + maxRetries + " attempts";
    }

    /**
     * Makes the actual API call to OpenAI.
     * @param prompt the prompt text
     * @return the API response content
     */
    private String callOpenAIAPI(String prompt) throws IOException, InterruptedException {
        JSONObject requestBody = createRequestBody(prompt);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiEndpoint))
            .timeout(REQUEST_TIMEOUT)
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            String errorMsg = "API returned status code: " + response.statusCode();
            Logger.error("API Error: " + response.body());
            throw new IOException(errorMsg);
        }

        return parseAPIResponse(response.body());
    }

    /**
     * Creates the JSON request body for the API call.
     * @param prompt the prompt text
     * @return JSON object with request parameters
     */
    private JSONObject createRequestBody(String prompt) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("messages", new JSONArray()
            .put(new JSONObject()
                .put("role", "system")
                .put("content", "You are a financial advisor providing investment insights. Be concise and practical."))
            .put(new JSONObject()
                .put("role", "user")
                .put("content", prompt))
        );
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 500);
        return requestBody;
    }

    /**
     * Parses the API response to extract content.
     * @param responseBody the API response JSON
     * @return the extracted message content
     */
    private String parseAPIResponse(String responseBody) throws JSONException {
        try {
            JSONObject response = new JSONObject(responseBody);
            return response.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
        } catch (JSONException e) {
            Logger.error("Failed to parse API response", e);
            throw e;
        }
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public String getServiceName() {
        return "OpenAI API (" + model + ")";
    }

    /**
     * Updates the API key and validates it.
     * @param apiKey the new API key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.available = isApiKeyValid();
        if (available) {
            Logger.info("API key updated successfully");
        } else {
            Logger.warning("Invalid API key provided");
        }
    }

    /**
     * Validates the API key format.
     * @return true if API key is valid, false otherwise
     */
    private boolean isApiKeyValid() {
        return ValidationUtil.isValidApiKey(apiKey);
    }
}

