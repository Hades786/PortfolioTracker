import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the ValidationUtil class.
 * Tests all input validation methods.
 */
public class ValidationUtilTest {

    @Test
    public void testValidSymbols() {
        assertTrue(ValidationUtil.isValidSymbol("AAPL"));
        assertTrue(ValidationUtil.isValidSymbol("GOOGL"));
        assertTrue(ValidationUtil.isValidSymbol("A"));
        assertTrue(ValidationUtil.isValidSymbol("ABCDE"));
        assertTrue(ValidationUtil.isValidSymbol("BRK"));
    }

    @Test
    public void testInvalidSymbols() {
        assertFalse(ValidationUtil.isValidSymbol(null));
        assertFalse(ValidationUtil.isValidSymbol(""));
        assertFalse(ValidationUtil.isValidSymbol("   "));
        assertFalse(ValidationUtil.isValidSymbol("VERYLONGSYMBOL"));
        assertFalse(ValidationUtil.isValidSymbol("aapl")); // lowercase
        assertFalse(ValidationUtil.isValidSymbol("A@PL")); // special char
    }

    @Test
    public void testValidQuantity() {
        assertTrue(ValidationUtil.isValidQuantity(1));
        assertTrue(ValidationUtil.isValidQuantity(100));
        assertTrue(ValidationUtil.isValidQuantity(1000000));
    }

    @Test
    public void testInvalidQuantity() {
        assertFalse(ValidationUtil.isValidQuantity(0));
        assertFalse(ValidationUtil.isValidQuantity(-1));
        assertFalse(ValidationUtil.isValidQuantity(-100));
    }

    @Test
    public void testValidPrice() {
        assertTrue(ValidationUtil.isValidPrice(0.01));
        assertTrue(ValidationUtil.isValidPrice(100.0));
        assertTrue(ValidationUtil.isValidPrice(1000000.99));
    }

    @Test
    public void testInvalidPrice() {
        assertFalse(ValidationUtil.isValidPrice(0));
        assertFalse(ValidationUtil.isValidPrice(-10.0));
        assertFalse(ValidationUtil.isValidPrice(Double.NaN));
        assertFalse(ValidationUtil.isValidPrice(Double.POSITIVE_INFINITY));
        assertFalse(ValidationUtil.isValidPrice(Double.NEGATIVE_INFINITY));
    }

    @Test
    public void testValidApiKey() {
        assertTrue(ValidationUtil.isValidApiKey("sk-1234567890abcdefghij"));
        assertTrue(ValidationUtil.isValidApiKey("sk-verylongapikeythatisvalid123456789"));
    }

    @Test
    public void testInvalidApiKey() {
        assertFalse(ValidationUtil.isValidApiKey(null));
        assertFalse(ValidationUtil.isValidApiKey(""));
        assertFalse(ValidationUtil.isValidApiKey("short"));
        assertFalse(ValidationUtil.isValidApiKey("   "));
    }

    @Test
    public void testValidEndpoint() {
        assertTrue(ValidationUtil.isValidEndpoint("https://api.example.com/v1/endpoint"));
        assertTrue(ValidationUtil.isValidEndpoint("http://localhost:8080/api"));
    }

    @Test
    public void testInvalidEndpoint() {
        assertFalse(ValidationUtil.isValidEndpoint(null));
        assertFalse(ValidationUtil.isValidEndpoint(""));
        assertFalse(ValidationUtil.isValidEndpoint("ftp://invalid.com"));
        assertFalse(ValidationUtil.isValidEndpoint("invalid-endpoint.com"));
    }

    @Test
    public void testValidateStockHoldingValid() {
        String result = ValidationUtil.validateStockHolding("AAPL", "100", "150.50", "180.75");
        assertEquals("", result);
    }

    @Test
    public void testValidateStockHoldingInvalidSymbol() {
        String result = ValidationUtil.validateStockHolding("", "100", "150.50", "180.75");
        assertNotEquals("", result);
        assertTrue(result.contains("symbol"));
    }

    @Test
    public void testValidateStockHoldingInvalidQuantity() {
        String result = ValidationUtil.validateStockHolding("AAPL", "0", "150.50", "180.75");
        assertNotEquals("", result);
        assertTrue(result.contains("Quantity"));
    }

    @Test
    public void testValidateStockHoldingInvalidPrice() {
        String result = ValidationUtil.validateStockHolding("AAPL", "100", "-10", "180.75");
        assertNotEquals("", result);
        assertTrue(result.contains("price"));
    }

    @Test
    public void testValidateStockHoldingNonNumeric() {
        String result = ValidationUtil.validateStockHolding("AAPL", "abc", "150.50", "180.75");
        assertNotEquals("", result);
        assertTrue(result.contains("numbers"));
    }

    @Test
    public void testSafeParseIntValid() {
        assertEquals(100, ValidationUtil.safeParseInt("100", 0));
        assertEquals(-50, ValidationUtil.safeParseInt("-50", 0));
    }

    @Test
    public void testSafeParseIntInvalid() {
        assertEquals(0, ValidationUtil.safeParseInt("abc", 0));
        assertEquals(99, ValidationUtil.safeParseInt("invalid", 99));
        assertEquals(0, ValidationUtil.safeParseInt("", 0));
    }

    @Test
    public void testSafeParseDoubleValid() {
        assertEquals(150.50, ValidationUtil.safeParseDouble("150.50", 0), 0.01);
        assertEquals(-10.5, ValidationUtil.safeParseDouble("-10.5", 0), 0.01);
    }

    @Test
    public void testSafeParseDoubleInvalid() {
        assertEquals(0, ValidationUtil.safeParseDouble("abc", 0), 0.01);
        assertEquals(99.99, ValidationUtil.safeParseDouble("invalid", 99.99), 0.01);
        assertEquals(0, ValidationUtil.safeParseDouble("", 0), 0.01);
    }

    @Test
    public void testValidateStockHoldingMultipleErrors() {
        String result = ValidationUtil.validateStockHolding("", "-1", "0", "invalid");
        // Should catch first error
        assertNotEquals("", result);
    }

    @Test
    public void testSymbolCaseSensitivity() {
        assertTrue(ValidationUtil.isValidSymbol("AAPL"));
        assertFalse(ValidationUtil.isValidSymbol("aapl"));
        assertFalse(ValidationUtil.isValidSymbol("AaPl"));
    }

    @Test
    public void testBoundaryValues() {
        // Minimum valid price
        assertTrue(ValidationUtil.isValidPrice(0.01));
        assertFalse(ValidationUtil.isValidPrice(0.00));
        
        // Minimum valid quantity
        assertTrue(ValidationUtil.isValidQuantity(1));
        assertFalse(ValidationUtil.isValidQuantity(0));
    }
}
