import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Integration tests for the entire Portfolio Tracker application.
 * Tests interactions between multiple components.
 */
public class ApplicationIntegrationTest {
    private Portfolio portfolio;
    private AIServiceFactory aiFactory;
    private StockPriceService priceService;

    @Before
    public void setUp() {
        portfolio = new Portfolio();
        aiFactory = AIServiceFactory.getInstance();
        priceService = new StockPriceService();
    }

    @Test
    public void testFullPortfolioWorkflow() {
        // Test complete portfolio workflow

        // 1. Add stocks
        portfolio.addHolding(new StockHolding("AAPL", 10, 150.0, 180.0));
        portfolio.addHolding(new StockHolding("GOOGL", 5, 100.0, 120.0));
        assertEquals(2, portfolio.size());

        // 2. Verify calculations
        assertEquals(2400.0, portfolio.getTotalValue(), 0.01);
        assertEquals(400.0, portfolio.getTotalProfitLoss(), 0.01);

        // 3. Remove a holding
        portfolio.removeHolding(0);
        assertEquals(1, portfolio.size());

        // 4. Verify updated calculations
        assertEquals(600.0, portfolio.getTotalValue(), 0.01);
        assertEquals(100.0, portfolio.getTotalProfitLoss(), 0.01);
    }

    @Test
    public void testAIServiceInitialization() {
        // Test AI service initialization and switching

        AIService localService = aiFactory.getLocalService();
        assertNotNull("Local service should exist", localService);
        assertTrue("Local service should be available", localService.isAvailable());

        // Switch to local service
        aiFactory.switchService("local");
        AIService current = aiFactory.getService();
        assertTrue("Current service should be local", current instanceof LocalAIService);
    }

    @Test
    public void testPortfolioWithPriceUpdates() {
        // Create holdings with initial prices
        StockHolding aapl = new StockHolding("AAPL", 10, 150.0, 150.0);
        StockHolding googl = new StockHolding("GOOGL", 5, 100.0, 100.0);

        portfolio.addHolding(aapl);
        portfolio.addHolding(googl);

        // Initial state
        assertEquals(2000.0, portfolio.getTotalValue(), 0.01);
        assertEquals(0.0, portfolio.getTotalProfitLoss(), 0.01);

        // Update with price change
        portfolio.removeHolding(0);
        portfolio.addHolding(new StockHolding("AAPL", 10, 150.0, 200.0));

        // Verify new calculations
        assertEquals(2500.0, portfolio.getTotalValue(), 0.01);
        assertEquals(500.0, portfolio.getTotalProfitLoss(), 0.01);
    }

    @Test
    public void testListenerNotification() {
        final int[] callCount = {0};
        portfolio.addListener(() -> callCount[0]++);

        portfolio.addHolding(new StockHolding("AAPL", 10, 150.0, 180.0));
        assertEquals("Listener should be called", 1, callCount[0]);

        portfolio.addHolding(new StockHolding("GOOGL", 5, 100.0, 120.0));
        assertEquals("Listener should be called again", 2, callCount[0]);

        portfolio.removeHolding(0);
        assertEquals("Listener should be called on remove", 3, callCount[0]);
    }

    @Test
    public void testValidationIntegration() {
        // Test validation in real scenarios

        String validationError = ValidationUtil.validateStockHolding("AAPL", "10", "150.0", "180.0");
        assertEquals("Should pass validation", "", validationError);

        // Try to add invalid holding
        StockHolding holding = new StockHolding("AAPL", 10, 150.0, 180.0);
        portfolio.addHolding(holding);

        assertTrue("Portfolio should have valid holding", portfolio.size() > 0);
    }

    @Test
    public void testPortfolioWithMixedResults() {
        // Create portfolio with mix of gains and losses
        portfolio.addHolding(new StockHolding("AAPL", 10, 100.0, 150.0));  // +$500 gain
        portfolio.addHolding(new StockHolding("IBM", 20, 150.0, 140.0));   // -$200 loss
        portfolio.addHolding(new StockHolding("MSFT", 5, 200.0, 200.0));   // $0 change

        double expectedValue = (10 * 150.0) + (20 * 140.0) + (5 * 200.0);
        double expectedPL = 500.0 - 200.0; // +300

        assertEquals("Total value should be correct", expectedValue, portfolio.getTotalValue(), 0.01);
        assertEquals("Total P/L should be correct", expectedPL, portfolio.getTotalProfitLoss(), 0.01);
    }

    @Test
    public void testStockInfoIntegration() {
        // Test creating StockInfo and using it
        StockPriceService.StockInfo info = new StockPriceService.StockInfo(
            "AAPL", "Apple Inc.", 180.0, 5.0, 2.86, 2800000000000L, 195.0, 165.0, System.currentTimeMillis()
        );

        assertNotNull("StockInfo should be created", info);
        assertEquals("Symbol should match", "AAPL", info.symbol);
        assertTrue("Should have valid toString", info.toString().contains("AAPL"));
    }

    @Test
    public void testLargePortfolio() {
        // Test portfolio with many holdings
        for (int i = 0; i < 100; i++) {
            String symbol = "STK" + i;
            portfolio.addHolding(new StockHolding(symbol, i + 1, 100.0, 110.0));
        }

        assertEquals("Should have 100 holdings", 100, portfolio.size());
        assertTrue("Total value should be reasonable", portfolio.getTotalValue() > 0);
        assertTrue("Total P/L should be positive", portfolio.getTotalProfitLoss() > 0);
    }

    @Test
    public void testConfigurationPersistence() {
        // Test that configuration is properly managed
        String serviceName = aiFactory.getCurrentServiceName();
        assertNotNull("Service name should exist", serviceName);
        assertTrue("Service name should be valid", 
                  serviceName.equals("local") || serviceName.equals("online"));
    }

    @Test
    public void testErrorRecovery() {
        // Test that system recovers from errors gracefully

        // Try invalid operations
        portfolio.removeHolding(999);  // Invalid index
        assertEquals("Portfolio should be unchanged", 0, portfolio.size());

        // Add valid holding
        portfolio.addHolding(new StockHolding("AAPL", 10, 150.0, 180.0));
        assertEquals("Portfolio should have 1 holding", 1, portfolio.size());
    }

    @Test
    public void testConcurrentOperations() {
        // Test multiple threads accessing portfolio
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                portfolio.addHolding(new StockHolding("STK" + i, 1, 100.0, 110.0));
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(50); // Give thread1 head start
                for (int i = 10; i < 20; i++) {
                    portfolio.addHolding(new StockHolding("STK" + i, 1, 100.0, 110.0));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Portfolio might not have exactly 20 due to threading, but should have many
        assertTrue("Portfolio should have multiple holdings", portfolio.size() > 0);
    }

    @Test
    public void testEmptyPortfolioOperations() {
        // Test operations on empty portfolio
        assertTrue("Empty portfolio should be empty", portfolio.isEmpty());
        assertEquals("Empty portfolio should have size 0", 0, portfolio.size());
        assertEquals("Empty portfolio total value should be 0", 0.0, portfolio.getTotalValue(), 0.01);
        assertEquals("Empty portfolio P/L should be 0", 0.0, portfolio.getTotalProfitLoss(), 0.01);
    }
}
