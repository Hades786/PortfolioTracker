/**
 * Input validation utility for the application.
 * Provides methods for validating stock holdings and user input.
 */
public class ValidationUtil {
    private ValidationUtil() {
        // Prevent instantiation
        throw new AssertionError("Cannot instantiate ValidationUtil class");
    }

    /**
     * Validates a stock symbol.
     * @param symbol the stock symbol to validate
     * @return true if symbol is valid, false otherwise
     */
    public static boolean isValidSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return false;
        }
        // Symbol should be 1-5 characters, alphanumeric
        return symbol.matches("^[A-Z0-9]{1,5}$");
    }

    /**
     * Validates quantity.
     * @param quantity the quantity to validate
     * @return true if quantity is valid, false otherwise
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity >= AppConstants.MIN_QUANTITY;
    }

    /**
     * Validates price.
     * @param price the price to validate
     * @return true if price is valid, false otherwise
     */
    public static boolean isValidPrice(double price) {
        return price >= AppConstants.MIN_PRICE && !Double.isNaN(price) && !Double.isInfinite(price);
    }

    /**
     * Validates API key format.
     * @param apiKey the API key to validate
     * @return true if API key is valid, false otherwise
     */
    public static boolean isValidApiKey(String apiKey) {
        if (apiKey == null) {
            return false;
        }
        // OpenAI API keys typically start with 'sk-' and are at least 20 characters
        return apiKey.trim().length() >= 20;
    }

    /**
     * Validates API endpoint URL format.
     * @param endpoint the endpoint URL to validate
     * @return true if endpoint is valid, false otherwise
     */
    public static boolean isValidEndpoint(String endpoint) {
        if (endpoint == null || endpoint.trim().isEmpty()) {
            return false;
        }
        return endpoint.trim().startsWith("https://") || endpoint.trim().startsWith("http://");
    }

    /**
     * Validates a complete StockHolding for consistency.
     * @param symbol the stock symbol
     * @param quantity the quantity
     * @param buyPrice the purchase price
     * @param currentPrice the current price
     * @return validation error message, or empty string if valid
     */
    public static String validateStockHolding(String symbol, String quantity, String buyPrice, String currentPrice) {
        try {
            String normalizedSymbol = symbol == null ? "" : symbol.toUpperCase().trim();
            if (!isValidSymbol(normalizedSymbol)) {
                return "Invalid stock symbol. Use 1-5 uppercase letters/numbers.";
            }

            int qty = Integer.parseInt(quantity);
            if (!isValidQuantity(qty)) {
                return "Quantity must be at least " + AppConstants.MIN_QUANTITY;
            }

            double buy = Double.parseDouble(buyPrice);
            if (!isValidPrice(buy)) {
                return "Purchase price must be greater than " + AppConstants.MIN_PRICE;
            }

            double current = Double.parseDouble(currentPrice);
            if (!isValidPrice(current)) {
                return "Current price must be greater than " + AppConstants.MIN_PRICE;
            }

            return ""; // Valid
        } catch (NumberFormatException e) {
            return "Please enter valid numbers for quantity and prices.";
        }
    }

    /**
     * Safely parses an integer with a default value.
     * @param value the string value to parse
     * @param defaultValue the default value if parsing fails
     * @return the parsed integer or default value
     */
    public static int safeParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Logger.debug("Failed to parse int from: " + value);
            return defaultValue;
        }
    }

    /**
     * Safely parses a double with a default value.
     * @param value the string value to parse
     * @param defaultValue the default value if parsing fails
     * @return the parsed double or default value
     */
    public static double safeParseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Logger.debug("Failed to parse double from: " + value);
            return defaultValue;
        }
    }
}
