/**
 * Interface for AI-powered portfolio analysis services.
 * Implementations can provide analysis through various backends (online API, local rules, etc).
 */
public interface AIService {
    /**
     * Gets investment analysis from the AI model.
     * @param portfolioSummary string containing portfolio information
     * @return AI-generated analysis as string
     * @throws RuntimeException if analysis generation fails
     */
    String getAnalysis(String portfolioSummary);

    /**
     * Checks if the service is available and properly configured.
     * @return true if service can be used, false otherwise
     */
    boolean isAvailable();

    /**
     * Gets the human-readable name of this AI service.
     * @return service name
     */
    String getServiceName();

    /**
     * Gets personalized investment recommendation based on portfolio.
     * @param portfolioSummary string containing portfolio information
     * @return AI-generated recommendations as string
     * @throws RuntimeException if recommendation generation fails
     */
    String getRecommendation(String portfolioSummary);
}
