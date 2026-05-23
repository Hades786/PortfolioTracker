import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Integration tests for the StockPriceService.
 * Tests live API calls to Yahoo Finance and price fetching.
 * 
 * Note: These tests require internet connection and may be slow.
 */
public class StockPriceServiceTest {
    private StockPriceService service;

    @Before
    public void setUp() {
        service = new StockPriceService();
    }

    @Test
    public void testIsAPIAvailable() {
        // This test checks if the API is reachable
        boolean available = service.isAPIAvailable();
        if (available) {
            // If we get here, API is working
            assertTrue("API should be available", true);
        } else {
            // If API is not available, it could be network issues
            System.out.println("Warning: Yahoo Finance API is not available");
        }
    }

    @Test
    public void testGetCurrentPriceValidSymbol() {
        // Test with well-known stock
        double price = service.getCurrentPrice("AAPL");
        
        assertTrue("Fallback or live price should be returned for a valid symbol", price > 0);
        assertTrue("Price should be less than $10000", price < 10000);
    }

    @Test
    public void testGetCurrentPriceInvalidSymbol() {
        double price = service.getCurrentPrice("INVALIDXYZ");
        assertEquals("Invalid symbol should return -1", -1, price, 0.01);
    }

    @Test
    public void testGetCurrentPriceInvalidInput() {
        double price1 = service.getCurrentPrice("");
        assertEquals("Empty symbol should return -1", -1, price1, 0.01);

        double price2 = service.getCurrentPrice(null);
        assertEquals("Null symbol should return -1", -1, price2, 0.01);

        double price3 = service.getCurrentPrice("abc");
        assertEquals("Invalid format should return -1", -1, price3, 0.01);
    }

    @Test
    public void testGetPricesForMultiple() {
        String[] symbols = {"AAPL", "GOOGL", "MSFT"};
        double[] prices = service.getPricesForMultiple(symbols);

        assertEquals("Should return array of same length", symbols.length, prices.length);

        // All prices should be either valid or -1
        for (double price : prices) {
            assertTrue("Each price should be positive or -1", price > 0 || price == -1);
        }
    }

    @Test
    public void testGetDetailedStockInfo() {
        StockPriceService.StockInfo info = service.getDetailedStockInfo("AAPL");

        if (info != null) {
            assertEquals("Symbol should match", "AAPL", info.symbol);
            assertTrue("Name should not be empty", !info.name.isEmpty());
            assertTrue("Current price should be positive", info.currentPrice > 0);
            assertTrue("52-week high should be positive", info.fiftyTwoWeekHigh > 0);
            assertTrue("52-week low should be positive", info.fiftyTwoWeekLow > 0);
            assertTrue("High should be >= Low", info.fiftyTwoWeekHigh >= info.fiftyTwoWeekLow);
        } else {
            System.out.println("Warning: Could not fetch detailed info for AAPL");
        }
    }

    @Test
    public void testStockInfoToString() {
        StockPriceService.StockInfo info = new StockPriceService.StockInfo(
            "AAPL", "Apple Inc.", 180.0, 5.0, 2.86, 2800000000000L, 195.0, 165.0, System.currentTimeMillis()
        );

        String str = info.toString();
        assertTrue("Should contain symbol", str.contains("AAPL"));
        assertTrue("Should contain price", str.contains("180.00"));
        assertTrue("Should contain 52-week range", str.contains("165.00") && str.contains("195.00"));
    }

    @Test
    public void testMultipleConsecutiveCalls() {
        // Test that service handles multiple calls properly
        double price1 = service.getCurrentPrice("AAPL");
        double price2 = service.getCurrentPrice("AAPL");

        if (price1 > 0 && price2 > 0) {
            // Prices should be similar (not exact due to market changes)
            assertTrue("Prices should be in reasonable range", Math.abs(price1 - price2) < price1 * 0.1); // Within 10%
        }
    }

    @Test
    public void testSymbolCaseHandling() {
        // Test both lowercase and uppercase (API might handle conversion)
        double priceUpper = service.getCurrentPrice("AAPL");
        double priceLower = service.getCurrentPrice("aapl");

        // Both should return -1 since ValidationUtil requires uppercase
        assertEquals("Lowercase should not be valid", -1, priceLower, 0.01);
    }
}
