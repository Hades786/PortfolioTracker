import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Portfolio class.
 * Tests portfolio management operations and calculations.
 */
public class PortfolioTest {
    private Portfolio portfolio;
    private StockHolding holding1;
    private StockHolding holding2;

    @Before
    public void setUp() {
        portfolio = new Portfolio();
        holding1 = new StockHolding("AAPL", 10, 150.0, 180.0);
        holding2 = new StockHolding("GOOGL", 5, 100.0, 120.0);
    }

    @Test
    public void testAddHolding() {
        portfolio.addHolding(holding1);
        assertEquals(1, portfolio.size());
        assertEquals("AAPL", portfolio.getHoldings().get(0).getSymbol());
    }

    @Test
    public void testAddMultipleHoldings() {
        portfolio.addHolding(holding1);
        portfolio.addHolding(holding2);
        assertEquals(2, portfolio.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullHolding() {
        portfolio.addHolding(null);
    }

    @Test
    public void testRemoveHolding() {
        portfolio.addHolding(holding1);
        portfolio.addHolding(holding2);
        portfolio.removeHolding(0);
        assertEquals(1, portfolio.size());
        assertEquals("GOOGL", portfolio.getHoldings().get(0).getSymbol());
    }

    @Test
    public void testRemoveInvalidIndex() {
        portfolio.addHolding(holding1);
        portfolio.removeHolding(99); // Should not throw, just log warning
        assertEquals(1, portfolio.size());
    }

    @Test
    public void testGetTotalValue() {
        portfolio.addHolding(holding1); // 10 * 180 = 1800
        portfolio.addHolding(holding2); // 5 * 120 = 600
        assertEquals(2400.0, portfolio.getTotalValue(), 0.01);
    }

    @Test
    public void testGetTotalProfitLoss() {
        portfolio.addHolding(holding1); // (180-150) * 10 = 300
        portfolio.addHolding(holding2); // (120-100) * 5 = 100
        assertEquals(400.0, portfolio.getTotalProfitLoss(), 0.01);
    }

    @Test
    public void testPortfolioEmpty() {
        assertTrue(portfolio.isEmpty());
        portfolio.addHolding(holding1);
        assertFalse(portfolio.isEmpty());
    }

    @Test
    public void testUpdateHolding() {
        portfolio.addHolding(holding1);
        StockHolding updated = new StockHolding("AAPL", 20, 150.0, 200.0);
        portfolio.updateHolding(0, updated);
        assertEquals(20, portfolio.getHoldings().get(0).getQuantity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithNull() {
        portfolio.addHolding(holding1);
        portfolio.updateHolding(0, null);
    }

    @Test
    public void testPortfolioListener() {
        final boolean[] updated = {false};
        portfolio.addListener(() -> updated[0] = true);
        portfolio.addHolding(holding1);
        assertTrue(updated[0]);
    }

    @Test
    public void testGetHoldingsReturnsCopy() {
        portfolio.addHolding(holding1);
        // This should not throw an exception (immutable list)
        assertThrows(UnsupportedOperationException.class, () -> {
            portfolio.getHoldings().add(holding2);
        });
    }

    @Test
    public void testZeroTotalValueWhenEmpty() {
        assertEquals(0.0, portfolio.getTotalValue(), 0.01);
    }

    @Test
    public void testZeroTotalPLWhenEmpty() {
        assertEquals(0.0, portfolio.getTotalProfitLoss(), 0.01);
    }
}
