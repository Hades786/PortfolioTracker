import java.util.ArrayList;
import java.util.List;

public class Portfolio {
    private List<StockHolding> holdings = new ArrayList<>();

    public void addHolding(StockHolding holding) {
        holdings.add(holding);
    }

    public double getTotalValue() {
        double total = 0;
        for(StockHolding h : holdings) {
            total += h.getValue();
        }
        return total;
    }
    public double getTotalProfitLoss(){
        double total = 0;
        for (StockHolding h : holdings) {
            total += h.getProfitLoss();
        }
        return total;
    }
    public void showReport() {
    System.out.println("------ Portfolio Report ------");
    for (StockHolding h : holdings) {
        System.out.printf("%s | Qty: %d | Value: %.2f | P/L: %.2f%n",h.getSymbol(),h.getQuantity(),h.getValue(),h.getProfitLoss());
        }
    System.out.printf("Total Value: %.2f | Total P/L: %.2f%n", getTotalValue(),getTotalProfitLoss());
    }
}
