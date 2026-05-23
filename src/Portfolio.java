import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a portfolio of stock holdings.
 * Manages portfolio operations and notifies listeners of changes.
 */
public class Portfolio {
    private List<StockHolding> holdings = new ArrayList<>();
    private List<PortfolioListener> listeners = new ArrayList<>();

    /**
     * Listener interface for portfolio changes.
     */
    public interface PortfolioListener {
        /**
         * Called when portfolio is updated.
         */
        void onPortfolioUpdated();
    }

    /**
     * Adds a listener for portfolio updates.
     * @param listener the listener to add
     */
    public void addListener(PortfolioListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            Logger.debug("Portfolio listener added");
        }
    }

    /**
     * Removes a listener from portfolio updates.
     * @param listener the listener to remove
     */
    public void removeListener(PortfolioListener listener) {
        if (listeners.remove(listener)) {
            Logger.debug("Portfolio listener removed");
        }
    }

    /**
     * Notifies all listeners of portfolio update.
     */
    private void notifyListeners() {
        for (PortfolioListener listener : listeners) {
            try {
                listener.onPortfolioUpdated();
            } catch (Exception e) {
                Logger.error("Error notifying portfolio listener", e);
            }
        }
    }

    /**
     * Adds a stock holding to the portfolio.
     * @param holding the stock holding to add
     * @throws IllegalArgumentException if holding is null
     */
    public void addHolding(StockHolding holding) {
        if (holding == null) {
            Logger.error("Attempted to add null holding");
            throw new IllegalArgumentException("Holding cannot be null");
        }
        holdings.add(holding);
        notifyListeners();
        Logger.info("Added holding: " + holding.getSymbol() + " x" + holding.getQuantity());
    }

    /**
     * Removes a holding at specified index.
     * @param index the index of the holding to remove
     */
    public void removeHolding(int index) {
        if (index >= 0 && index < holdings.size()) {
            StockHolding removed = holdings.remove(index);
            notifyListeners();
            Logger.info("Removed holding: " + removed.getSymbol());
        } else {
            Logger.warning("Invalid index for removal: " + index);
        }
    }

    /**
     * Updates a holding at specified index.
     * @param index the index of the holding to update
     * @param holding the new holding data
     * @throws IllegalArgumentException if holding is null
     */
    public void updateHolding(int index, StockHolding holding) {
        if (holding == null) {
            Logger.error("Attempted to update with null holding");
            throw new IllegalArgumentException("Holding cannot be null");
        }
        if (index >= 0 && index < holdings.size()) {
            holdings.set(index, holding);
            notifyListeners();
            Logger.info("Updated holding: " + holding.getSymbol());
        } else {
            Logger.warning("Invalid index for update: " + index);
        }
    }

    /**
     * Gets an immutable copy of all holdings.
     * @return list of holdings
     */
    public List<StockHolding> getHoldings() {
        return Collections.unmodifiableList(new ArrayList<>(holdings));
    }

    /**
     * Gets the total portfolio value.
     * @return total value of all holdings at current price
     */
    public double getTotalValue() {
        double total = 0;
        for(StockHolding h : holdings) {
            total += h.getValue();
        }
        return total;
    }

    /**
     * Gets the total profit/loss across portfolio.
     * @return total profit/loss
     */
    public double getTotalProfitLoss(){
        double total = 0;
        for (StockHolding h : holdings) {
            total += h.getProfitLoss();
        }
        return total;
    }

    /**
     * Gets the portfolio size.
     * @return number of holdings
     */
    public int size() {
        return holdings.size();
    }

    /**
     * Checks if portfolio is empty.
     * @return true if no holdings, false otherwise
     */
    public boolean isEmpty() {
        return holdings.isEmpty();
    }

    /**
     * Removes all holdings from the portfolio.
     */
    public void clear() {
        holdings.clear();
        notifyListeners();
        Logger.info("Cleared all portfolio holdings");
    }

    /**
     * Prints portfolio report to console.
     */
    public void showReport() {
        Logger.info("=== Portfolio Report ===");
        for (StockHolding h : holdings) {
            Logger.info(String.format("%s | Qty: %d | Value: %.2f | P/L: %.2f",
                h.getSymbol(), h.getQuantity(), h.getValue(), h.getProfitLoss()));
        }
        Logger.info(String.format("Total Value: %.2f | Total P/L: %.2f",
            getTotalValue(), getTotalProfitLoss()));
    }
}
