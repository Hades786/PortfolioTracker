/**
 * Represents a single stock holding in the portfolio.
 * Contains information about a stock purchase including symbol, quantity, and prices.
 */
public class StockHolding {
    private String symbol;
    private int quantity;
    public double purchasePrice;
    public double currentPrice;

    /**
     * Constructs a StockHolding.
     * @param symbol the stock symbol (e.g., AAPL)
     * @param quantity the number of shares held
     * @param purchasePrice the price at which shares were purchased
     * @param currentPrice the current market price of shares
     * @throws IllegalArgumentException if any value is invalid
     */
    public StockHolding(String symbol, int quantity, double purchasePrice, double currentPrice) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (purchasePrice <= 0 || currentPrice <= 0) {
            throw new IllegalArgumentException("Prices must be positive");
        }

        this.symbol = symbol;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.currentPrice = currentPrice;
    }

    /**
     * Calculates the total current value of this holding.
     * @return quantity multiplied by current price
     */
    public double getValue() {
        return quantity * currentPrice;
    }

    /**
     * Calculates total profit or loss on this holding.
     * @return (currentPrice - purchasePrice) × quantity
     */
    public double getProfitLoss(){
        return (currentPrice - purchasePrice) * quantity;
    }

    /**
     * Gets the stock symbol.
     * @return the stock symbol
     */
    public String getSymbol(){
        return symbol;
    }

    /**
     * Gets the quantity of shares.
     * @return the number of shares held
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the purchase price per share.
     * @return the purchase price
     */
    public double getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Gets the current price per share.
     * @return the current price
     */
    public double getCurrentPrice() {
        return currentPrice;
    }

    /**
     * Calculates the percentage gain/loss.
     * @return percentage change from purchase to current price
     */
    public double getPercentageChange() {
        return ((currentPrice - purchasePrice) / purchasePrice) * 100;
    }

    @Override
    public String toString() {
        return String.format("%s: %d shares @ $%.2f (value: $%.2f, P/L: $%.2f)",
            symbol, quantity, currentPrice, getValue(), getProfitLoss());
    }
}
