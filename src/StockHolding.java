public class StockHolding {
    private String symbol;
    private int quantity;
    private double purchasePrice;
    private double currentPrice;

    public StockHolding(String symbol, int quantity, double purchasePrice, double currentPrice) {
        this.symbol = symbol;
        this.quantity =  quantity;
        this.purchasePrice = purchasePrice;
        this.currentPrice = currentPrice;
    }
    public double getValue() {
        return quantity * currentPrice;
    }
    public double getProfitLoss(){
        return (currentPrice - purchasePrice) * quantity;
    }
    public String getSymbol(){
        return symbol;
    }
    public int getQuantity() {
        return quantity;
    }
}
