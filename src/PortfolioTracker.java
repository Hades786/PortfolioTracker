import java.util.Scanner;

public class PortfolioTracker {
    public static void main(String[] args) {
        Scanner sc = new Scanner (System.in);
        Portfolio portfolio = new Portfolio();

        while(true){
            System.out.println("\n1. Add stock");
            System.out.println("2. Show report");
            System.out.println("3. Exit");
            System.out.println("Choice: ");
            String choice = sc.next();

            if("1".equals(choice)){
                System.out.println("Enter stock symbol: ");
                String sym = sc.next();
                System.out.println("Enter quantity: ");
                int qty = sc.nextInt();
                System.out.println("Enter purchase price: ");
                double buy = sc.nextDouble();
                System.out.println("Enter current price: ");
                double cur = sc.nextDouble();

                portfolio.addHolding(new StockHolding(sym, qty, buy, cur));
                System.out.println("Added.");
            } else if ("2".equals(choice)) {
                portfolio.showReport();
            } else if ("3".equals(choice)) {
                System.out.println("Exiting...");
                break;
            }
            else {
                System.out.println("Unknown choice. Try again.");
            }
        }
        sc.close();
    }
}
