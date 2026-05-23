import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the StockHolding class.
 * Tests calculations and validations for individual stock holdings.
 */
public class StockHoldingTest {

    @Test
    public void testConstructorValid() {
        StockHolding holding = new StockHolding("AAPL", 10, 150.0, 180.0);
        assertEquals("AAPL", holding.getSymbol());
        assertEquals(10, holding.getQuantity());
        assertEquals(150.0, holding.getPurchasePrice(), 0.01);
        assertEquals(180.0, holding.getCurrentPrice(), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullSymbol() {
        new StockHolding(null, 10, 150.0, 180.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorEmptySymbol() {
        new StockHolding("", 10, 150.0, 180.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorZeroQuantity() {
        new StockHolding("AAPL", 0, 150.0, 180.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegativeQuantity() {
        new StockHolding("AAPL", -5, 150.0, 180.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorZeroPrice() {
        new StockHolding("AAPL", 10, 0, 180.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegativePrice() {
        new StockHolding("AAPL", 10, -150.0, 180.0);
    }

    @Test
    public void testGetValue() {
        StockHolding holding = new StockHolding("AAPL", 10, 150.0, 180.0);
        assertEquals(1800.0, holding.getValue(), 0.01);
    }

    @Test
    public void testGetValueWithDecimals() {
        StockHolding holding = new StockHolding("GOOGL", 2, 145.50, 156.75);
        assertEquals(313.5, holding.getValue(), 0.01);
    }

    @Test
    public void testGetProfitLossPositive() {
        StockHolding holding = new StockHolding("AAPL", 10, 150.0, 180.0);
        assertEquals(300.0, holding.getProfitLoss(), 0.01);
    }

    @Test
    public void testGetProfitLossNegative() {
        StockHolding holding = new StockHolding("AAPL", 10, 150.0, 120.0);
        assertEquals(-300.0, holding.getProfitLoss(), 0.01);
    }

    @Test
    public void testGetProfitLossZero() {
        StockHolding holding = new StockHolding("AAPL", 10, 150.0, 150.0);
        assertEquals(0.0, holding.getProfitLoss(), 0.01);
    }

    @Test
    public void testGetPercentageChangePositive() {
        StockHolding holding = new StockHolding("AAPL", 10, 100.0, 150.0);
        assertEquals(50.0, holding.getPercentageChange(), 0.01);
    }

    @Test
    public void testGetPercentageChangeNegative() {
        StockHolding holding = new StockHolding("AAPL", 10, 100.0, 75.0);
        assertEquals(-25.0, holding.getPercentageChange(), 0.01);
    }

    @Test
    public void testGetPercentageChangeZero() {
        StockHolding holding = new StockHolding("AAPL", 10, 100.0, 100.0);
        assertEquals(0.0, holding.getPercentageChange(), 0.01);
    }

    @Test
    public void testToString() {
        StockHolding holding = new StockHolding("AAPL", 10, 150.0, 180.0);
        String result = holding.toString();
        assertTrue(result.contains("AAPL"));
        assertTrue(result.contains("10 shares"));
        assertTrue(result.contains("180.00"));
    }

    @Test
    public void testLargeQuantity() {
        StockHolding holding = new StockHolding("AAPL", 1000, 100.0, 150.0);
        assertEquals(150000.0, holding.getValue(), 0.01);
        assertEquals(50000.0, holding.getProfitLoss(), 0.01);
    }

    @Test
    public void testSmallPrices() {
        StockHolding holding = new StockHolding("PENNY", 100, 0.01, 0.02);
        assertEquals(2.0, holding.getValue(), 0.01);
        assertEquals(1.0, holding.getProfitLoss(), 0.01);
    }

    @Test
    public void testGetters() {
        StockHolding holding = new StockHolding("GOOGL", 5, 100.0, 120.0);
        assertEquals("GOOGL", holding.getSymbol());
        assertEquals(5, holding.getQuantity());
        assertEquals(100.0, holding.getPurchasePrice(), 0.01);
        assertEquals(120.0, holding.getCurrentPrice(), 0.01);
    }
}
