# Portfolio Tracker - Modern Investment Management Application

A comprehensive JavaFX-based portfolio tracking application with AI-powered analysis capabilities. Manage your stock holdings, track profits/losses, and get investment recommendations powered by local or online AI models.

## Features

### Portfolio Management
- **Add/Remove Stocks**: Easily manage your stock holdings
- **Real-time Tracking**: Track current prices and instant P/L calculations
- **Portfolio Summary**: View total portfolio value and overall profit/loss
- **CSV Export**: Export your portfolio data for backup or analysis
- **Percentage Tracking**: View percentage gains/losses at a glance

### 🤖 AI-Powered Analysis
- **Dual AI Backends**:
  - **Local Analysis Engine**: Works offline without API keys, perfect for privacy
  - **Online API Integration**: Leverage OpenAI models for advanced analysis
- **Portfolio Analysis**: Get comprehensive analysis of your holdings
- **Investment Recommendations**: Receive data-driven investment suggestions
- **Seamless Switching**: Switch between local and online AI services on the fly

### Modern UI
- **Dark Mode Support**: Easy on the eyes with built-in dark theme
- **Responsive Design**: Clean, modern interface built with JavaFX
- **Intuitive Navigation**: Tabbed interface for easy access to features
- **Professional Styling**: Color-coded metrics for quick insights
- **Light Mode Available**: Switch to light theme for different environments

## System Requirements

- **Java**: 11 or higher (JDK or JRE)
- **Operating System**: Windows, macOS, or Linux
- **Memory**: 512MB minimum
- **Network**: Internet connection (optional, only for online AI features)

## Installation

### Prerequisites
- JDK 11 or higher installed
- Maven 3.6+ (for building from source)

### Quick Start

1. **Clone or Extract the Project**
   ```bash
   cd PortfolioTracker
   ```

2. **Build the Project**
   ```bash
   mvn clean package
   ```

3. **Run the Application**
   ```bash
   mvn javafx:run
   ```

   Or run the JAR directly:
   ```bash
   java -jar target/portfolio-tracker-1.0.0-shaded.jar
   ```

## Usage Guide

### Adding Stocks

1. Go to the **Portfolio** tab
2. Fill in the stock details:
   - **Symbol**: Stock ticker symbol (e.g., AAPL, GOOGL)
   - **Quantity**: Number of shares owned
   - **Purchase Price**: Price per share at the time of purchase
   - **Current Price**: Current market price per share
3. Click **Add Stock**
4. Your stock appears in the table below

### Managing Your Portfolio

- **View Holdings**: All stocks are displayed in an organized table
- **Remove Stock**: Select a stock row and click **Remove Selected**
- **View Statistics**: Real-time portfolio value and P/L shown at the bottom
- **Color Coding**: Green indicates gains, red indicates losses

### Using AI Analysis

#### Local Analysis (No Setup Required ⚡)
1. Go to the **AI Analysis** tab
2. Ensure "Local Analysis" is selected from the AI Service dropdown
3. Click **Get Analysis** to receive portfolio insights
4. Click **Get Recommendations** for investment suggestions
5. Click **Clear** to reset the analysis area

**Note**: Local analysis works without internet and requires no API key!

#### Online API (OpenAI - Advanced Analysis 🚀)
1. Get a free API key from [OpenAI Platform](https://platform.openai.com/api-keys)
2. Go to the **Settings** tab
3. Select "Online API" from the AI Service dropdown
4. Enter your API key in the API Key field
5. Click **Save Key**
6. Go back to **AI Analysis** tab and use the analysis features

### Exporting Data

1. Go to **Settings** tab
2. Click **Export Portfolio (CSV)**
3. Your portfolio will be saved as `portfolio_export.csv` in the application directory
4. Open in Excel, Google Sheets, or any spreadsheet application

## Configuration

### AI Service Configuration

The application stores configuration in `ai_config.properties`:

```properties
ai_service=local
api_endpoint=https://api.openai.com/v1/chat/completions
api_model=gpt-3.5-turbo
api_key=
```

### Logging

Application logs are stored in `portfolio_tracker.log`. All actions and errors are logged for debugging.

## 📁 Project Structure

```
PortfolioTracker/
├── src/
│   ├── PortfolioTrackerApp.java      # Main JavaFX application
│   ├── Portfolio.java                 # Portfolio model with observer pattern
│   ├── StockHolding.java              # Individual stock holding model
│   ├── AIService.java                 # AI service interface
│   ├── OnlineAIService.java           # OpenAI integration
│   ├── LocalAIService.java            # Local analysis engine (no API needed)
│   ├── AIServiceFactory.java          # Factory for AI services (Singleton)
│   ├── Logger.java                    # Centralized logging utility
│   ├── ValidationUtil.java            # Input validation utilities
│   └── AppConstants.java              # Application constants
├── pom.xml                            # Maven build configuration
├── README.md                          # This file
├── ai_config.properties               # AI configuration (auto-generated)
└── portfolio_tracker.log              # Application logs (auto-generated)
```

## Technical Architecture

### Design Patterns Used

1. **Singleton Pattern**: `AIServiceFactory` ensures single configuration source
2. **Factory Pattern**: Creates appropriate AI service instances dynamically
3. **Strategy Pattern**: `AIService` interface for pluggable implementations
4. **Observer Pattern**: `PortfolioListener` for real-time UI updates
5. **MVC Pattern**: Clear separation between Model, View, and Controller layers

### Best Practices Implemented

- ✅ Comprehensive JavaDoc documentation for all classes
- ✅ Centralized logging system with file and console output
- ✅ Input validation with detailed error messages
- ✅ Proper exception handling throughout the application
- ✅ Thread-safe operations for background tasks
- ✅ Resource management and cleanup
- ✅ Configuration management with fallback defaults
- ✅ Security considerations (API key masking, no sensitive data in logs)
- ✅ Constants centralization for easy maintenance
- ✅ Immutable collections for thread safety

## API Integration Details

### OpenAI Integration

The `OnlineAIService` uses OpenAI's Chat Completion API with:

- **Model**: gpt-3.5-turbo (configurable in settings)
- **Temperature**: 0.7 (balanced creativity and consistency)
- **Max Tokens**: 500 (response length limit)
- **Retry Logic**: Exponential backoff up to 3 retries on failure
- **Timeout**: 30 seconds per request
- **System Prompt**: Financial advisor context for better analysis

## Troubleshooting

### Application won't start
```bash
# Ensure Java 11+ is installed
java -version

# Check JAVA_HOME environment variable
echo %JAVA_HOME%  # Windows
echo $JAVA_HOME   # Mac/Linux
```

### Online AI features not working
- Verify internet connection: `ping api.openai.com`
- Check API key validity in Settings tab
- Ensure API key has sufficient quota at [OpenAI Billing](https://platform.openai.com/account/billing/overview)
- Review logs in `portfolio_tracker.log` for detailed errors
- Try switching back to Local Analysis to verify UI works

### Build issues with Maven
```bash
# Clean and rebuild everything
mvn clean install

# Skip tests if compilation fails
mvn clean package -DskipTests

# Force update of dependencies
mvn clean package -U
```

### Port or Runtime Issues
```bash
# Run with increased memory
java -Xmx1024m -jar target/portfolio-tracker-1.0.0-shaded.jar

# Run with debug output
java -Ddebug=true -jar target/portfolio-tracker-1.0.0-shaded.jar
```

## Future Enhancements

- Real-time stock price updates via Yahoo Finance API
- Portfolio persistence with SQLite database
- Advanced charting and technical analysis visualization
- Price alerts and push notifications
- Multiple portfolio support with switching
- Web dashboard with cloud sync
- Mobile companion app
- User authentication and secure storage
- Portfolio performance benchmarking

## Security Considerations

- API keys are masked with `***` in the UI
- Configuration file (`ai_config.properties`) should have restricted permissions
- Never commit API keys to version control (use .gitignore)
- Avoid sharing `ai_config.properties` containing API keys
- For production: use environment variables instead of config files
- All API communication uses HTTPS
- No sensitive data is logged to file

## License

This project is provided as-is for educational and personal use.

## Support

For issues, suggestions, or improvements:
1. Check the logs in `portfolio_tracker.log`
2. Review error messages in the Settings tab
3. Ensure all dependencies are properly installed
4. Try the Local Analysis feature to isolate API issues

## Dependencies

| Dependency | Version | Purpose |
|-----------|---------|---------|
| JavaFX | 20.0.1 | GUI framework |
| org.json | 20230227 | JSON parsing and generation |
| JUnit | 4.13.2 | Unit testing |
| Maven Shade Plugin | 3.5.0 | Package creation |
| Maven Compiler | 3.11.0 | Java compilation |

## Application Metadata

- **Version**: 1.0.0
- **Java Minimum**: 11
- **Last Updated**: May 2026
- **Build Tool**: Maven 3.6+
- **License Type**: Educational Use

---

**Enjoy managing your portfolio with AI-powered insights! **
