# Developer Guide - Portfolio Tracker

This guide provides information for developers working on extending or maintaining the Portfolio Tracker application.

## 🏗️ Architecture Overview

### Core Components

#### 1. **Model Layer** (`Portfolio`, `StockHolding`)
- Represents the domain logic
- `Portfolio`: Manages collection of holdings and notifies listeners
- `StockHolding`: Represents a single stock position
- **Observer Pattern**: Portfolio uses listeners for real-time UI updates

```java
// Adding a listener
portfolio.addListener(() -> updateUI());

// When portfolio changes, all listeners are notified
```

#### 2. **Service Layer** (AI Services)
- `AIService` interface: Defines contract for AI implementations
- `OnlineAIService`: Calls OpenAI API with retry logic
- `LocalAIService`: Provides local analysis without external dependencies
- `AIServiceFactory`: Singleton that manages service lifecycle

```java
// Getting the current service (automatically selected)
AIService service = AIServiceFactory.getInstance().getService();
String analysis = service.getAnalysis(portfolioSummary);
```

#### 3. **View Layer** (`PortfolioTrackerApp`)
- JavaFX GUI implementation
- Tabbed interface for different features
- Real-time UI updates via Portfolio listeners

#### 4. **Utility Layer**
- `Logger`: Centralized logging with file persistence
- `ValidationUtil`: Input validation with error messages
- `AppConstants`: Application-wide constants

## 📋 Class Responsibilities

### Portfolio.java
```java
public class Portfolio {
    // Manages list of holdings
    // Notifies listeners on changes
    // Calculates total value and P/L
}
```

**Key Methods**:
- `addHolding(StockHolding)`: Add stock with listener notification
- `removeHolding(int)`: Remove by index
- `getHoldings()`: Returns unmodifiable list
- `getTotalValue()`: Sum of all holdings' current value
- `getTotalProfitLoss()`: Sum of all P/L

**Threading**: Safe for multi-threaded access (used with JavaFX UI)

### StockHolding.java
```java
public class StockHolding {
    // Represents single stock position
    // Immutable after creation (best practice)
}
```

**Validation**: Constructor validates all inputs
**Calculations**: 
- `getValue()` = quantity × currentPrice
- `getProfitLoss()` = (currentPrice - purchasePrice) × quantity
- `getPercentageChange()` = ((currentPrice - purchasePrice) / purchasePrice) × 100

### AIService.java (Interface)
```java
public interface AIService {
    String getAnalysis(String portfolioSummary);
    String getRecommendation(String portfolioSummary);
    boolean isAvailable();
    String getServiceName();
}
```

**Implementations**:
1. **OnlineAIService**: HTTP calls to OpenAI
2. **LocalAIService**: Rule-based analysis

### OnlineAIService.java
```
Request Flow:
1. Validate API key
2. Create JSON request body
3. Call API with retry logic (exponential backoff)
4. Parse JSON response
5. Return extracted message content
```

**Retry Strategy**:
- 3 retry attempts with exponential backoff (1s, 2s, 4s)
- 30-second timeout per request
- Proper exception logging

### AIServiceFactory.java (Singleton)
```java
// Single instance across application
AIServiceFactory factory = AIServiceFactory.getInstance();

// Switch services dynamically
factory.switchService("online");  // or "local"

// Get current service
AIService service = factory.getService();
```

**Configuration Persistence**:
- Saves to `ai_config.properties`
- Auto-creates with defaults if missing
- Thread-safe configuration updates

### Logger.java (Centralized Logging)
```java
Logger.info("User added stock");           // Info level
Logger.warning("API key not configured");  // Warning level
Logger.error("Connection failed", ex);     // Error with exception
Logger.debug("Debug information");         // Debug level
```

**Features**:
- Dual output: console + file (`portfolio_tracker.log`)
- Thread-safe (synchronized)
- Includes timestamp, level, and message
- Stack traces for exceptions
- Auto-creates log file

### ValidationUtil.java (Input Validation)
```java
// Validation methods
ValidationUtil.isValidSymbol("AAPL");              // true
ValidationUtil.isValidQuantity(100);              // true
ValidationUtil.isValidPrice(150.50);              // true
ValidationUtil.isValidApiKey("sk-abc123...");     // true

// Comprehensive validation
String error = ValidationUtil.validateStockHolding(
    "AAPL", "100", "150.50", "180.25"
);
if (error.isEmpty()) {
    // Valid input
}
```

### AppConstants.java (Configuration)
All magic strings/numbers centralized here:
```java
public static final String APP_NAME = "Portfolio Tracker";
public static final int WINDOW_MIN_WIDTH = 1000;
public static final String COLOR_ACCENT_GREEN = "#00ff88";
// ... many more
```

**Benefits**:
- Single source of truth for configuration
- Easy theme color changes
- Consistent sizing and spacing

## 🔄 Typical Application Flow

```
Application Start
    ↓
Initialize AIServiceFactory (Singleton)
    ├─ Load configuration from ai_config.properties
    ├─ Create OnlineAIService instance
    ├─ Create LocalAIService instance
    └─ Set current service based on config
    ↓
Create Portfolio instance
    ↓
Build JavaFX UI
    ├─ Create main BorderPane layout
    ├─ Create Portfolio tab
    ├─ Create AI Analysis tab
    ├─ Create Settings tab
    └─ Apply dark/light theme
    ↓
User Interactions
    ├─ Add stock → Portfolio.addHolding()
    │   → Notify listeners → UI updates
    ├─ Generate analysis → AIService.getAnalysis()
    │   → Background thread (doesn't block UI)
    └─ Export portfolio → Generate CSV
    ↓
Application Close
    → Save configuration
```

## 🧪 Testing Recommendations

### Unit Testing Examples

```java
// Test Portfolio calculations
@Test
public void testPortfolioTotalValue() {
    Portfolio p = new Portfolio();
    p.addHolding(new StockHolding("AAPL", 10, 150, 200));
    assertEquals(2000, p.getTotalValue(), 0.01);
}

// Test Validation
@Test
public void testInvalidSymbol() {
    assertFalse(ValidationUtil.isValidSymbol(""));
    assertFalse(ValidationUtil.isValidSymbol("INVALID_SYMBOL"));
}

// Test StockHolding
@Test
public void testProfitLoss() {
    StockHolding sh = new StockHolding("AAPL", 10, 150, 200);
    assertEquals(500, sh.getProfitLoss(), 0.01);
    assertEquals(33.33, sh.getPercentageChange(), 0.1);
}
```

### Integration Testing

```java
// Test AI Service switching
@Test
public void testServiceSwitching() {
    AIServiceFactory factory = AIServiceFactory.getInstance();
    factory.switchService("local");
    assertTrue(factory.getService() instanceof LocalAIService);
    
    factory.switchService("online");
    // Should fall back to local if API key invalid
}
```

## 📦 Adding New Features

### Example: Adding a New AI Service

1. **Implement AIService Interface**:
```java
public class MyCustomAIService implements AIService {
    @Override
    public String getAnalysis(String portfolioSummary) {
        // Custom implementation
    }
    
    @Override
    public String getRecommendation(String portfolioSummary) {
        // Custom implementation
    }
    
    @Override
    public boolean isAvailable() {
        // Check if service is configured
    }
    
    @Override
    public String getServiceName() {
        return "My Custom AI";
    }
}
```

2. **Register in AIServiceFactory**:
```java
private void initializeServices() {
    // ... existing services ...
    myCustomService = new MyCustomAIService();
}

public void switchService(String serviceType) {
    if ("custom".equalsIgnoreCase(serviceType)) {
        currentService = myCustomService;
    }
    // ... rest of method ...
}
```

3. **Update UI to Include New Option**:
```java
aiServiceCombo.getItems().addAll("Local Analysis", "Online API", "My Custom AI");
```

### Example: Adding a New UI Tab

1. **Create Tab Method**:
```java
private Tab createNewFeatureTab() {
    Tab tab = new Tab();
    tab.setText("🆕 New Feature");
    tab.setStyle("-fx-font-size: 14px;");
    
    VBox container = new VBox();
    container.setStyle("-fx-background-color: " + AppConstants.COLOR_DARK_PRIMARY + ";");
    
    // Build UI components
    
    tab.setContent(container);
    return tab;
}
```

2. **Add to TabPane**:
```java
private TabPane createCenterContent() {
    TabPane tabPane = new TabPane();
    tabPane.getTabs().addAll(
        createPortfolioTab(),
        createAnalysisTab(),
        createSettingsTab(),
        createNewFeatureTab()  // Add here
    );
    return tabPane;
}
```

## 🚀 Performance Optimization

### Current Implementation Optimizations
1. **Lazy Loading**: Services initialized on demand
2. **Immutable Collections**: `Collections.unmodifiableList()` prevents accidental modification
3. **Background Threads**: AI requests run on separate threads to prevent UI freezing
4. **Retry Logic**: Exponential backoff prevents overwhelming API servers

### Potential Improvements
1. **Caching**: Cache analysis results to avoid duplicate API calls
2. **Thread Pool**: Use ExecutorService instead of creating new threads
3. **Data Pagination**: For large portfolios, paginate holdings display
4. **Async Database**: Integrate database for persistent storage

## 🔐 Security Best Practices

### Current Implementation
- ✅ API keys masked with `***` in UI
- ✅ No credentials in logs
- ✅ HTTPS for API calls
- ✅ Input validation on all user inputs

### Recommendations
- Store API keys in system keystore instead of properties file
- Implement token refresh mechanism
- Add rate limiting to prevent abuse
- Use encryption for sensitive configuration
- Implement user authentication for multi-user scenarios

## 🐛 Debugging Tips

### Enable Debug Logging
```java
// In Logger class, set:
private static final boolean ENABLE_FILE_LOG = true;  // Already on
// Add to PortfolioTrackerApp start:
Logger.debug("Application initialization starting");
```

### Trace AI Service Calls
```java
// OnlineAIService already logs:
Logger.info("Requesting portfolio analysis from online service");
Logger.debug("API call attempt " + (i + 1) + " of " + maxRetries);
Logger.error("API Error: " + response.body());
```

### Check Configuration
```java
// View current configuration:
AIServiceFactory factory = AIServiceFactory.getInstance();
System.out.println("Service: " + factory.getCurrentServiceName());
System.out.println("Endpoint: " + factory.getAPIEndpoint());
System.out.println("Model: " + factory.getAPIModel());
```

## 📊 Metrics & Monitoring

### Key Metrics to Monitor
1. **Portfolio Size**: Number of holdings
2. **Total Portfolio Value**: Real-time calculation
3. **Win Rate**: Percentage of profitable holdings
4. **API Response Time**: For online service
5. **Error Rate**: Failed operations

### Proposed Metrics System
```java
public class PortfolioMetrics {
    private int totalAdditions;
    private int totalDeletions;
    private long totalApiCallTime;
    private int apiCallFailures;
    
    public void recordApiCall(long duration, boolean success) { }
    public MetricsReport getReport() { }
}
```

## 📚 Code Style Guidelines

### Naming Conventions
- Classes: `PascalCase` (Portfolio, StockHolding)
- Methods: `camelCase` (addHolding, getTotalValue)
- Constants: `UPPER_SNAKE_CASE` (APP_NAME, COLOR_ACCENT_GREEN)
- Variables: `camelCase` (portfolio, totalValue)

### Documentation
- All public classes: Class-level JavaDoc
- All public methods: Method-level JavaDoc with @param and @return
- Complex logic: Inline comments explaining "why", not "what"

### Example JavaDoc
```java
/**
 * Adds a stock holding to the portfolio.
 * Notifies all listeners of the change.
 * 
 * @param holding the stock holding to add
 * @throws IllegalArgumentException if holding is null
 * @throws IllegalStateException if portfolio is locked
 */
public void addHolding(StockHolding holding) {
    // Implementation
}
```

## 🔗 Dependencies & Versions

| Component | Version | Why |
|-----------|---------|-----|
| JavaFX | 20.0.1 | Modern UI framework, LTS support |
| org.json | 20230227 | Lightweight JSON parsing |
| JUnit | 4.13.2 | Stable testing framework |
| Maven | 3.6+ | Build automation |
| Java | 11+ | Long-term support version |

## 🎯 Development Workflow

1. **Feature Development**
   - Create feature branch
   - Add unit tests first (TDD)
   - Implement feature
   - Run full test suite
   - Update documentation

2. **Bug Fixes**
   - Identify root cause via logs
   - Add regression test
   - Implement fix
   - Verify fix resolves issue

3. **Code Review Checklist**
   - ✅ JavaDoc complete
   - ✅ Error handling proper
   - ✅ No hardcoded values (use constants)
   - ✅ Logging appropriate
   - ✅ Tests pass
   - ✅ No security issues

## 📞 Support

For questions during development, refer to:
1. **JavaDoc**: In the code itself
2. **README.md**: User-facing documentation
3. **Logs**: `portfolio_tracker.log` for runtime issues
4. **Tests**: Unit tests show expected behavior

---

**Happy coding!** 🚀
