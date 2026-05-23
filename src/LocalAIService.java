/**
 * Implementation of AIService using local analysis without external APIs.
 * Provides basic portfolio analysis and recommendations based on built-in rules.
 */
public class LocalAIService implements AIService {
    private boolean available;

    /**
     * Constructs LocalAIService.
     * Local service is always available as it doesn't require external configuration.
     */
    public LocalAIService() {
        this.available = true;
        Logger.info("LocalAIService initialized and ready");
    }

    @Override
    public String getAnalysis(String portfolioSummary) {
        Logger.info("Generating local portfolio analysis");
        return generateLocalAnalysis(portfolioSummary);
    }

    @Override
    public String getRecommendation(String portfolioSummary) {
        Logger.info("Generating local investment recommendations");
        return generateLocalRecommendation(portfolioSummary);
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public String getServiceName() {
        return "Local Analysis Engine";
    }

    /**
     * Generates local analysis based on portfolio summary.
     * @param portfolioSummary the portfolio summary text
     * @return analysis string
     */
    private String generateLocalAnalysis(String portfolioSummary) {
        try {
            StringBuilder analysis = new StringBuilder();
            analysis.append("📊 LOCAL PORTFOLIO ANALYSIS\n\n");

            // Extract key metrics (simple parsing)
            if (portfolioSummary.contains("Total Value:")) {
                analysis.append("✓ Your portfolio is actively tracked\n");
            }

            if (portfolioSummary.contains("P/L:")) {
                if (portfolioSummary.contains("-")) {
                    analysis.append("⚠ Some holdings show negative returns - consider diversification\n");
                } else {
                    analysis.append("✓ All holdings are in positive territory\n");
                }
            }

            analysis.append("\nDiversification Recommendations:\n");
            analysis.append("• Ensure no single stock exceeds 20-30% of portfolio value\n");
            analysis.append("• Consider adding low-cost index funds for stability\n");
            analysis.append("• Maintain a mix of growth and value stocks\n");

            analysis.append("\nRisk Management:\n");
            analysis.append("• Set stop-loss orders at 10-15% below entry price\n");
            analysis.append("• Review portfolio quarterly\n");
            analysis.append("• Keep 6-12 months of emergency funds in cash/bonds\n");

            Logger.debug("Local analysis generated successfully");
            return analysis.toString();
        } catch (Exception e) {
            Logger.error("Error generating local analysis", e);
            return "Error generating analysis. Please try again.";
        }
    }

    /**
     * Generates local recommendations based on portfolio summary.
     * @param portfolioSummary the portfolio summary text
     * @return recommendations string
     */
    private String generateLocalRecommendation(String portfolioSummary) {
        try {
            StringBuilder recommendations = new StringBuilder();
            recommendations.append("💡 TOP INVESTMENT RECOMMENDATIONS\n\n");

            recommendations.append("1. REBALANCING\n");
            recommendations.append("   Review quarterly and rebalance to your target allocation\n");
            recommendations.append("   to maintain consistent risk levels.\n\n");

            recommendations.append("2. DIVERSIFICATION\n");
            recommendations.append("   Add uncorrelated assets to reduce portfolio volatility.\n");
            recommendations.append("   Consider: Tech, Healthcare, Consumer, Utilities, Energy\n\n");

            recommendations.append("3. DOLLAR-COST AVERAGING\n");
            recommendations.append("   Invest fixed amounts regularly instead of lump sums\n");
            recommendations.append("   to reduce timing risk.\n");

            Logger.debug("Local recommendations generated successfully");
            return recommendations.toString();
        } catch (Exception e) {
            Logger.error("Error generating local recommendations", e);
            return "Error generating recommendations. Please try again.";
        }
    }
}
