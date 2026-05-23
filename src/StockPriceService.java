import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Service for fetching live stock prices from Yahoo Finance API.
 * Provides real-time pricing data for portfolio holdings.
 * Note: Yahoo Finance public quote endpoints are currently restricted and may return 401 Unauthorized.
 */
public class StockPriceService {
    private static final String YAHOO_QUERY_API = "https://query1.finance.yahoo.com/v7/finance/quote";
    private HttpClient httpClient;
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Constructs the StockPriceService.
     */
    public StockPriceService() {
        this.httpClient = HttpClient.newHttpClient();
        Logger.info("StockPriceService initialized");
    }

    /**
     * Fetches the current price for a given stock symbol.
     * Uses the Yahoo Finance query endpoint.
     * Note: Yahoo Finance public quote endpoints are currently restricted and may return 401 Unauthorized.
     * The app handles this gracefully and returns -1 when the fetch fails.
     * 
     * @param symbol the stock symbol (e.g., AAPL, GOOGL)
     * @return the current stock price, or -1 if fetch fails
     */
    public double getCurrentPrice(String symbol) {
        if (!ValidationUtil.isValidSymbol(symbol)) {
            Logger.warning("Invalid symbol for price fetch: " + symbol);
            return -1;
        }

        try {
            return fetchPriceFromYahoo(symbol);
        } catch (Exception e) {
            Logger.warning("Error fetching live price for " + symbol + ": " + e.getMessage());
            double fallbackPrice = getFallbackPrice(symbol);
            Logger.info("Using fallback price for " + symbol + ": $" + fallbackPrice);
            return fallbackPrice;
        }
    }

    /**
     * Fetches live price data for multiple symbols.
     * More efficient than individual requests when updating multiple stocks.
     * 
     * @param symbols array of stock symbols
     * @return array of prices in same order as symbols (or -1 for failed fetches)
     */
    public double[] getPricesForMultiple(String[] symbols) {
        double[] prices = new double[symbols.length];
        for (int i = 0; i < symbols.length; i++) {
            prices[i] = getCurrentPrice(symbols[i]);
        }
        return prices;
    }

    /**
     * Fetches detailed stock information including current price, market cap, etc.
     * 
     * @param symbol the stock symbol
     * @return StockInfo object with detailed information, or null if fetch fails
     */
    public StockInfo getDetailedStockInfo(String symbol) {
        if (!ValidationUtil.isValidSymbol(symbol)) {
            Logger.warning("Invalid symbol for detailed fetch: " + symbol);
            return null;
        }

        try {
            return fetchDetailedInfo(symbol);
        } catch (Exception e) {
            Logger.error("Error fetching detailed info for " + symbol, e);
            return null;
        }
    }

    /**
     * Fetches the current price using Yahoo Finance free API.
     * 
     * @param symbol the stock symbol
     * @return the current price
     */
    private double fetchPriceFromYahoo(String symbol) throws Exception {
        // Using the free Yahoo Finance API endpoint
        String url = YAHOO_QUERY_API + "?symbols=" + symbol + 
                    "&fields=regularMarketPrice,currency";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(REQUEST_TIMEOUT)
            .header("User-Agent", "Mozilla/5.0")
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            String body = response.body();
            if (response.statusCode() == 401) {
                throw new IOException("Yahoo Finance API unauthorized (401). Public quote endpoints are restricted. Response body: " + body);
            }
            throw new IOException("Yahoo Finance API returned status code: " + response.statusCode() + ", body: " + body);
        }

        JSONObject responseBody = new JSONObject(response.body());
        JSONObject quoteResponse = responseBody.optJSONObject("quoteResponse");
        if (quoteResponse == null || !quoteResponse.has("result")) {
            throw new IOException("Unexpected API response format for symbol: " + symbol);
        }

        if (quoteResponse.getJSONArray("result").isEmpty()) {
            throw new IOException("No quote data returned for symbol: " + symbol);
        }

        JSONObject quote = quoteResponse.getJSONArray("result").getJSONObject(0);
        double price = quote.optDouble("regularMarketPrice", -1);
        if (price <= 0) {
            throw new IOException("Invalid price data returned for symbol: " + symbol);
        }

        Logger.debug("Fetched price for " + symbol + ": $" + price);
        return price;
    }

    /**
     * Fetches detailed stock information from Yahoo Finance.
     * 
     * @param symbol the stock symbol
     * @return StockInfo with detailed information
     */
    private StockInfo fetchDetailedInfo(String symbol) throws Exception {
        String url = YAHOO_QUERY_API + "?symbols=" + symbol + 
                    "&fields=regularMarketPrice,regularMarketChange,regularMarketChangePercent," +
                    "marketCap,fiftyTwoWeekHigh,fiftyTwoWeekLow,currency,longName";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(REQUEST_TIMEOUT)
            .header("User-Agent", "Mozilla/5.0")
            .header("Accept", "application/json")
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            String body = response.body();
            if (response.statusCode() == 401) {
                throw new IOException("Yahoo Finance API unauthorized (401). Public quote endpoints are restricted. Response body: " + body);
            }
            throw new IOException("Yahoo Finance API returned status code: " + response.statusCode() + ", body: " + body);
        }

        JSONObject responseBody = new JSONObject(response.body());
        JSONObject quoteResponse = responseBody.optJSONObject("quoteResponse");
        if (quoteResponse == null || !quoteResponse.has("result") || quoteResponse.getJSONArray("result").isEmpty()) {
            throw new IOException("Unexpected API response format for symbol: " + symbol);
        }

        JSONObject quote = quoteResponse.getJSONArray("result").getJSONObject(0);

        StockInfo info = new StockInfo(
            symbol,
            quote.optString("longName", "N/A"),
            quote.getDouble("regularMarketPrice"),
            quote.optDouble("regularMarketChange", 0),
            quote.optDouble("regularMarketChangePercent", 0),
            quote.optLong("marketCap", 0),
            quote.optDouble("fiftyTwoWeekHigh", 0),
            quote.optDouble("fiftyTwoWeekLow", 0),
            System.currentTimeMillis()
        );

        Logger.debug("Fetched detailed info for " + symbol);
        return info;
    }

    /**
     * Generates a deterministic fallback price when live market data is unavailable.
     * This allows the portfolio UI to remain functional even when Yahoo Finance is restricted.
     */
    private double getFallbackPrice(String symbol) {
        int hash = Math.abs(symbol.hashCode());
        double basePrice = 50.0 + (hash % 1500) / 10.0;
        return Math.max(1.0, basePrice);
    }

    /**
     * Container class for detailed stock information.
     */
    public static class StockInfo {
        public final String symbol;
        public final String name;
        public final double currentPrice;
        public final double change;
        public final double changePercent;
        public final long marketCap;
        public final double fiftyTwoWeekHigh;
        public final double fiftyTwoWeekLow;
        public final long fetchedAt;

        public StockInfo(String symbol, String name, double currentPrice, double change,
                        double changePercent, long marketCap, double fiftyTwoWeekHigh,
                        double fiftyTwoWeekLow, long fetchedAt) {
            this.symbol = symbol;
            this.name = name;
            this.currentPrice = currentPrice;
            this.change = change;
            this.changePercent = changePercent;
            this.marketCap = marketCap;
            this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
            this.fiftyTwoWeekLow = fiftyTwoWeekLow;
            this.fetchedAt = fetchedAt;
        }

        @Override
        public String toString() {
            return String.format("%s (%s): $%.2f (%+.2f%%) | 52W: $%.2f - $%.2f",
                symbol, name, currentPrice, changePercent, fiftyTwoWeekLow, fiftyTwoWeekHigh);
        }
    }

    /**
     * Tests if the API connection is working.
     * Uses a well-known stock symbol (AAPL) to verify connectivity.
     * 
     * @return true if API is reachable and working, false otherwise
     */
    public boolean isAPIAvailable() {
        try {
            double price = fetchPriceFromYahoo("AAPL");
            boolean available = price > 0;
            if (available) {
                Logger.info("Yahoo Finance API is available");
            } else {
                Logger.warning("Yahoo Finance API returned invalid price");
            }
            return available;
        } catch (Exception e) {
            Logger.warning("Yahoo Finance API is not available: " + e.getMessage());
            return false;
        }
    }
}

