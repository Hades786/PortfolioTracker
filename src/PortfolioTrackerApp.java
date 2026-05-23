import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.Arrays;

/**
 * Main JavaFX application for Portfolio Tracker.
 * Provides GUI for portfolio management and AI-powered analysis.
 */
public class PortfolioTrackerApp extends Application {
    private Portfolio portfolio;
    private AIServiceFactory aiFactory;
    private StockPriceService priceService;
    private boolean darkMode = true;
    private TableView<StockHolding> holdingsTable;
    private Label totalValueLabel;
    private Label totalPLLabel;
    private TextArea analysisArea;
    private ComboBox<String> aiServiceCombo;
    private TextField apiKeyField;
    private ToggleButton darkModeToggle;
    private Label priceStatusLabel;

    @Override
    public void start(Stage primaryStage) {
        try {
            Logger.info("Starting Portfolio Tracker Application");
            
            portfolio = new Portfolio();
            aiFactory = AIServiceFactory.getInstance();
            priceService = new StockPriceService();

            BorderPane root = createMainLayout();
            Scene scene = new Scene(root, AppConstants.WINDOW_DEFAULT_WIDTH, AppConstants.WINDOW_DEFAULT_HEIGHT);
            
            applyTheme(scene, darkMode);

            primaryStage.setTitle(AppConstants.APP_NAME + " - Modern Investment Management");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(AppConstants.WINDOW_MIN_WIDTH);
            primaryStage.setMinHeight(AppConstants.WINDOW_MIN_HEIGHT);
            
            primaryStage.setOnCloseRequest(e -> {
                Logger.info("Application closing");
                aiFactory.saveConfig();
            });
            
            primaryStage.show();
            Logger.info("Application started successfully");
        } catch (Exception e) {
            Logger.error("Failed to start application", e);
            showError("Application Error", "Failed to start application: " + e.getMessage());
        }
    }

    private BorderPane createMainLayout() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-font-family: 'Segoe UI', 'Arial'");

        root.setTop(createTopBar());
        root.setCenter(createCenterContent());
        root.setBottom(createStatusBar());

        return root;
    }

    private VBox createTopBar() {
        VBox topBar = new VBox();
        topBar.setPadding(new Insets(AppConstants.PADDING_STANDARD));
        topBar.setStyle("-fx-background-color: " + AppConstants.COLOR_DARK_BG + 
                       "; -fx-border-color: " + AppConstants.COLOR_BORDER + 
                       "; -fx-border-width: 0 0 1 0;");

        HBox headerRow = new HBox();
        headerRow.setSpacing(AppConstants.SPACING_STANDARD);
        headerRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label titleLabel = new Label("📈 " + AppConstants.APP_NAME);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + AppConstants.COLOR_ACCENT_GREEN + ";");

        HBox aiBox = new HBox(AppConstants.SPACING_STANDARD);
        aiBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Label aiLabel = new Label("AI Service:");
        aiLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_TEXT_PRIMARY + ";");
        aiServiceCombo = new ComboBox<>();
        aiServiceCombo.getItems().addAll("Local Analysis", "Online API");
        String currentServiceLabel = aiFactory.getCurrentServiceName().equals(AppConstants.AI_SERVICE_ONLINE)
            ? "Online API" : "Local Analysis";
        aiServiceCombo.setValue(currentServiceLabel);
        aiServiceCombo.setPrefWidth(150);
        aiServiceCombo.setOnAction(e -> switchAIService());
        aiBox.getChildren().addAll(aiLabel, aiServiceCombo);

        HBox themeBox = new HBox(AppConstants.SPACING_STANDARD);
        themeBox.setAlignment(javafx.geometry.Pos.CENTER);
        Label themeLabel = new Label("Dark Mode:");
        themeLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_TEXT_PRIMARY + ";");
        darkModeToggle = new ToggleButton("🌙");
        darkModeToggle.setSelected(darkMode);
        darkModeToggle.setStyle("-fx-padding: 8px 12px; -fx-font-size: 14px;");
        darkModeToggle.setOnAction(e -> toggleTheme());
        themeBox.getChildren().addAll(themeLabel, darkModeToggle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(titleLabel, spacer, aiBox, themeBox);
        topBar.getChildren().add(headerRow);
        return topBar;
    }

    private TabPane createCenterContent() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-padding: 10;");

        tabPane.getTabs().addAll(
            createPortfolioTab(),
            createAnalysisTab(),
            createSettingsTab()
        );

        return tabPane;
    }

    private Tab createPortfolioTab() {
        Tab tab = new Tab();
        tab.setText("📊 Portfolio");
        tab.setStyle("-fx-font-size: 14px;");

        VBox container = new VBox();
        container.setPadding(new Insets(AppConstants.PADDING_STANDARD));
        container.setSpacing(AppConstants.SPACING_STANDARD);
        container.setStyle("-fx-background-color: " + AppConstants.COLOR_DARK_PRIMARY + ";");

        holdingsTable = createHoldingsTable();
        VBox formBox = createAddStockForm();
        HBox statsBox = createStatsBox();
        HBox buttonBar = createPortfolioButtonBar();

        container.getChildren().addAll(formBox, new Separator(), holdingsTable, buttonBar, statsBox);
        tab.setContent(new ScrollPane(container));

        return tab;
    }

    private TableView<StockHolding> createHoldingsTable() {
        TableView<StockHolding> table = new TableView<>();
        table.setStyle("-fx-font-size: 12px; -fx-control-inner-background: #1e1e1e; -fx-text-fill: #ffffff;");

        TableColumn<StockHolding, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSymbol()));
        symbolCol.setPrefWidth(80);

        TableColumn<StockHolding, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getQuantity()));
        qtyCol.setPrefWidth(80);

        TableColumn<StockHolding, String> valueCol = new TableColumn<>("Value ($)");
        valueCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cellData.getValue().getValue())));
        valueCol.setPrefWidth(100);

        TableColumn<StockHolding, String> plCol = new TableColumn<>("Profit/Loss ($)");
        plCol.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cellData.getValue().getProfitLoss())));
        plCol.setPrefWidth(120);

        table.getColumns().addAll(Arrays.asList(symbolCol, qtyCol, valueCol, plCol));
        table.setPrefHeight(300);

        return table;
    }

    private VBox createAddStockForm() {
        VBox formBox = new VBox();
        formBox.setPadding(new Insets(AppConstants.SPACING_STANDARD));
        formBox.setSpacing(8);
        formBox.setStyle("-fx-border-color: " + AppConstants.COLOR_BORDER + 
                        "; -fx-border-radius: 5; -fx-background-color: " + AppConstants.COLOR_DARK_SECONDARY + 
                        "; -fx-padding: 10;");

        Label formLabel = new Label("➕ Add New Stock");
        formLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + AppConstants.COLOR_ACCENT_GREEN + ";");

        GridPane grid = new GridPane();
        grid.setHgap(AppConstants.SPACING_STANDARD);
        grid.setVgap(AppConstants.SPACING_STANDARD);
        grid.setPadding(new Insets(AppConstants.SPACING_STANDARD));

        TextField symbolField = new TextField();
        symbolField.setPromptText("Symbol (e.g., AAPL)");
        symbolField.setStyle("-fx-padding: 8px;");

        TextField qtyField = new TextField();
        qtyField.setPromptText("Quantity");
        qtyField.setStyle("-fx-padding: 8px;");

        TextField buyPriceField = new TextField();
        buyPriceField.setPromptText("Purchase Price");
        buyPriceField.setStyle("-fx-padding: 8px;");

        TextField currentPriceField = new TextField();
        currentPriceField.setPromptText("Current Price");
        currentPriceField.setStyle("-fx-padding: 8px;");

        grid.add(new Label("Symbol:"), 0, 0);
        grid.add(symbolField, 1, 0);
        grid.add(new Label("Quantity:"), 2, 0);
        grid.add(qtyField, 3, 0);
        grid.add(new Label("Purchase Price:"), 4, 0);
        grid.add(buyPriceField, 5, 0);
        grid.add(new Label("Current Price:"), 6, 0);
        grid.add(currentPriceField, 7, 0);

        Button addBtn = new Button("Add Stock");
        addBtn.setStyle("-fx-padding: 10px 30px; -fx-font-size: 12px; -fx-background-color: " + 
                       AppConstants.COLOR_SUCCESS + "; -fx-text-fill: #ffffff; -fx-cursor: hand;");
        addBtn.setOnAction(e -> addStockAction(symbolField, qtyField, buyPriceField, currentPriceField));

        grid.add(addBtn, 8, 0);

        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-text-fill: " + AppConstants.COLOR_TEXT_PRIMARY + ";");
            }
        }

        formBox.getChildren().addAll(formLabel, grid);
        return formBox;
    }

    private void addStockAction(TextField symbolField, TextField qtyField, TextField buyPriceField, TextField currentPriceField) {
        try {
            String symbol = symbolField.getText().toUpperCase().trim();
            String quantity = qtyField.getText();
            String buyPrice = buyPriceField.getText();
            String currentPrice = currentPriceField.getText();

            String validation = ValidationUtil.validateStockHolding(symbol, quantity, buyPrice, currentPrice);
            if (!validation.isEmpty()) {
                showError("Validation Error", validation);
                return;
            }

            int qty = Integer.parseInt(quantity);
            double buy = Double.parseDouble(buyPrice);
            double current = Double.parseDouble(currentPrice);

            portfolio.addHolding(new StockHolding(symbol, qty, buy, current));
            refreshPortfolioDisplay();
            
            symbolField.clear();
            qtyField.clear();
            buyPriceField.clear();
            currentPriceField.clear();
            
            showNotification("Stock added successfully!");
            Logger.info("Stock added to portfolio: " + symbol);
        } catch (Exception ex) {
            Logger.error("Error adding stock", ex);
            showError("Error", "Failed to add stock: " + ex.getMessage());
        }
    }

    private HBox createStatsBox() {
        HBox statsBox = new HBox();
        statsBox.setPadding(new Insets(AppConstants.PADDING_STANDARD));
        statsBox.setSpacing(AppConstants.SPACING_STANDARD * 2);
        statsBox.setStyle("-fx-border-color: " + AppConstants.COLOR_BORDER + 
                         "; -fx-border-radius: 5; -fx-background-color: " + AppConstants.COLOR_DARK_SECONDARY + ";");
        statsBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox totalValueBox = new VBox(5);
        totalValueBox.setStyle("-fx-padding: 10; -fx-border-color: " + AppConstants.COLOR_ACCENT_GREEN + 
                              "; -fx-border-radius: 3; -fx-border-width: 1;");
        Label totalValueTitleLabel = new Label("Total Portfolio Value");
        totalValueTitleLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_ACCENT_GREEN + "; -fx-font-size: 12px;");
        totalValueLabel = new Label("$0.00");
        totalValueLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_ACCENT_GREEN + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        totalValueBox.getChildren().addAll(totalValueTitleLabel, totalValueLabel);

        VBox totalPLBox = new VBox(5);
        totalPLBox.setStyle("-fx-padding: 10; -fx-border-color: " + AppConstants.COLOR_ACCENT_ORANGE + 
                           "; -fx-border-radius: 3; -fx-border-width: 1;");
        Label totalPLTitleLabel = new Label("Total Profit/Loss");
        totalPLTitleLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_ACCENT_ORANGE + "; -fx-font-size: 12px;");
        totalPLLabel = new Label("$0.00");
        totalPLLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_ACCENT_ORANGE + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        totalPLBox.getChildren().addAll(totalPLTitleLabel, totalPLLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statsBox.getChildren().addAll(totalValueBox, totalPLBox, spacer);
        return statsBox;
    }

    private HBox createPortfolioButtonBar() {
        HBox buttonBar = new HBox(AppConstants.SPACING_STANDARD);
        buttonBar.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        Button removeBtn = new Button("Remove Selected");
        removeBtn.setStyle("-fx-padding: 10px 20px; -fx-font-size: 12px; -fx-background-color: " + 
                          AppConstants.COLOR_ERROR + "; -fx-text-fill: #ffffff;");
        removeBtn.setOnAction(e -> removeSelectedHolding());

        Button refreshPricesBtn = new Button("🔄 Fetch Live Prices");
        refreshPricesBtn.setStyle("-fx-padding: 10px 20px; -fx-font-size: 12px; -fx-background-color: " + 
                                 AppConstants.COLOR_WARNING + "; -fx-text-fill: #ffffff;");
        refreshPricesBtn.setOnAction(e -> fetchLivePrices());
        
        buttonBar.getChildren().addAll(refreshPricesBtn, removeBtn);
        return buttonBar;
    }

    private void fetchLivePrices() {
        if (portfolio.isEmpty()) {
            showError("No Holdings", "Please add at least one holding before fetching live prices.");
            return;
        }

        priceStatusLabel.setText("Fetching live prices...");

        Task<Void> fetchTask = new Task<Void>() {
            @Override
            protected Void call() {
                Logger.info("Fetching live prices for portfolio holdings");
                java.util.List<StockHolding> updatedHoldings = new java.util.ArrayList<>();

                for (StockHolding holding : portfolio.getHoldings()) {
                    double livePrice = priceService.getCurrentPrice(holding.getSymbol());
                    if (livePrice > 0) {
                        updatedHoldings.add(new StockHolding(
                            holding.getSymbol(),
                            holding.getQuantity(),
                            holding.getPurchasePrice(),
                            livePrice));
                    } else {
                        updatedHoldings.add(holding);
                    }
                }

                Platform.runLater(() -> {
                    portfolio.clear();
                    updatedHoldings.forEach(portfolio::addHolding);
                    refreshPortfolioDisplay();
                    priceStatusLabel.setText("Live price fetch complete. Latest values loaded.");
                });
                return null;
            }

            @Override
            protected void failed() {
                Logger.error("Failed to fetch live prices", getException());
                Platform.runLater(() -> {
                    priceStatusLabel.setText("Live price fetch failed.");
                    showError("Live Price Error", "Unable to fetch live prices at this time.");
                });
            }
        };

        new Thread(fetchTask).start();
    }

    private Tab createAnalysisTab() {
        Tab tab = new Tab();
        tab.setText("🤖 AI Analysis");
        tab.setStyle("-fx-font-size: 14px;");

        VBox container = new VBox();
        container.setPadding(new Insets(AppConstants.PADDING_STANDARD));
        container.setSpacing(AppConstants.SPACING_STANDARD);
        container.setStyle("-fx-background-color: " + AppConstants.COLOR_DARK_PRIMARY + ";");

        Label titleLabel = new Label("AI-Powered Portfolio Analysis");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + AppConstants.COLOR_ACCENT_GREEN + ";");

        HBox buttonBox = new HBox(AppConstants.SPACING_STANDARD);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Button analysisBtn = new Button("Get Analysis");
        analysisBtn.setStyle("-fx-padding: 10px 20px; -fx-font-size: 12px; -fx-background-color: " + 
                            AppConstants.COLOR_INFO + "; -fx-text-fill: #ffffff;");
        analysisBtn.setOnAction(e -> generateAnalysis());

        Button recommendBtn = new Button("Get Recommendations");
        recommendBtn.setStyle("-fx-padding: 10px 20px; -fx-font-size: 12px; -fx-background-color: " + 
                             AppConstants.COLOR_WARNING + "; -fx-text-fill: #ffffff;");
        recommendBtn.setOnAction(e -> generateRecommendations());

        Button clearBtn = new Button("Clear");
        clearBtn.setStyle("-fx-padding: 10px 20px; -fx-font-size: 12px; -fx-background-color: " + 
                         AppConstants.COLOR_BORDER + "; -fx-text-fill: #ffffff;");
        clearBtn.setOnAction(e -> analysisArea.clear());

        buttonBox.getChildren().addAll(analysisBtn, recommendBtn, clearBtn);

        analysisArea = new TextArea();
        analysisArea.setWrapText(true);
        analysisArea.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: " + AppConstants.COLOR_ACCENT_GREEN + 
                             "; -fx-font-family: 'Consolas', 'Courier'; -fx-font-size: 11px;");
        analysisArea.setPrefRowCount(25);

        container.getChildren().addAll(titleLabel, buttonBox, new Separator(), analysisArea);
        tab.setContent(container);

        return tab;
    }

    private Tab createSettingsTab() {
        Tab tab = new Tab();
        tab.setText("⚙️ Settings");
        tab.setStyle("-fx-font-size: 14px;");

        VBox container = new VBox();
        container.setPadding(new Insets(AppConstants.PADDING_STANDARD * 2));
        container.setSpacing(AppConstants.PADDING_STANDARD);
        container.setStyle("-fx-background-color: " + AppConstants.COLOR_DARK_PRIMARY + ";");

        VBox aiSection = createAISettingsSection();
        VBox dataSection = createDataSection();

        container.getChildren().addAll(aiSection, new Separator(), dataSection);
        
        ScrollPane scrollPane = new ScrollPane(container);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);

        return tab;
    }

    private VBox createAISettingsSection() {
        VBox section = new VBox(AppConstants.SPACING_STANDARD);
        section.setPadding(new Insets(AppConstants.PADDING_STANDARD));
        section.setStyle("-fx-border-color: " + AppConstants.COLOR_BORDER + 
                        "; -fx-border-radius: 5; -fx-background-color: " + AppConstants.COLOR_DARK_SECONDARY + ";");

        Label sectionTitle = new Label("🤖 AI Model Configuration");
        sectionTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + AppConstants.COLOR_ACCENT_GREEN + ";");

        HBox serviceBox = new HBox(AppConstants.SPACING_STANDARD);
        Label serviceLabel = new Label("AI Service:");
        serviceLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_TEXT_PRIMARY + "; -fx-min-width: 100;");
        ComboBox<String> serviceSelector = new ComboBox<>();
        serviceSelector.getItems().addAll("Local Analysis", "Online API");
        serviceSelector.setValue(aiFactory.getCurrentServiceName().equals("online") ? "Online API" : "Local Analysis");
        serviceSelector.setOnAction(e -> {
            String selected = serviceSelector.getValue();
            aiFactory.switchService(selected.equals("Online API") ? "online" : "local");
            aiServiceCombo.setValue(selected);
            showNotification("AI Service switched to " + selected);
            Logger.info("AI Service changed to: " + selected);
        });
        serviceBox.getChildren().addAll(serviceLabel, serviceSelector);

        HBox apiKeyBox = new HBox(AppConstants.SPACING_STANDARD);
        Label apiKeyLabel = new Label("API Key:");
        apiKeyLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_TEXT_PRIMARY + "; -fx-min-width: 100;");
        apiKeyField = new PasswordField();
        apiKeyField.setPromptText("Enter your OpenAI API key");
        apiKeyField.setStyle("-fx-padding: 8px;");
        apiKeyField.setText(aiFactory.getAPIKey());
        Button saveKeyBtn = new Button("Save Key");
        saveKeyBtn.setStyle("-fx-padding: 8px 15px; -fx-background-color: " + 
                           AppConstants.COLOR_SUCCESS + "; -fx-text-fill: #ffffff;");
        saveKeyBtn.setOnAction(e -> saveAPIKey());
        apiKeyBox.getChildren().addAll(apiKeyLabel, apiKeyField, saveKeyBtn);

        Label infoLabel = new Label("💡 For OpenAI API: Get your free API key at https://platform.openai.com/api-keys");
        infoLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_TEXT_SECONDARY + "; -fx-font-size: 11px;");

        section.getChildren().addAll(sectionTitle, serviceBox, apiKeyBox, infoLabel);
        return section;
    }

    private VBox createDataSection() {
        VBox section = new VBox(AppConstants.SPACING_STANDARD);
        section.setPadding(new Insets(AppConstants.PADDING_STANDARD));
        section.setStyle("-fx-border-color: " + AppConstants.COLOR_BORDER + 
                        "; -fx-border-radius: 5; -fx-background-color: " + AppConstants.COLOR_DARK_SECONDARY + ";");

        Label sectionTitle = new Label("📁 Data Management");
        sectionTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + AppConstants.COLOR_ACCENT_GREEN + ";");

        Button exportBtn = new Button("Export Portfolio (CSV)");
        exportBtn.setStyle("-fx-padding: 10px 20px; -fx-background-color: " + 
                          AppConstants.COLOR_INFO + "; -fx-text-fill: #ffffff;");
        exportBtn.setOnAction(e -> exportPortfolioToCSV());

        HBox buttonBox = new HBox(AppConstants.SPACING_STANDARD);
        buttonBox.getChildren().add(exportBtn);

        section.getChildren().addAll(sectionTitle, buttonBox);
        return section;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(AppConstants.SPACING_STANDARD));
        statusBar.setStyle("-fx-background-color: " + AppConstants.COLOR_DARK_BG + 
                          "; -fx-border-color: " + AppConstants.COLOR_BORDER + 
                          "; -fx-border-width: 1 0 0 0;");

        Label statusLabel = new Label("✓ Ready");
        statusLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_ACCENT_GREEN + ";");

        priceStatusLabel = new Label("Live prices idle");
        priceStatusLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_TEXT_SECONDARY + "; -fx-font-size: 11px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label versionLabel = new Label(AppConstants.APP_NAME + " v" + AppConstants.APP_VERSION);
        versionLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_TEXT_SECONDARY + "; -fx-font-size: 11px;");

        statusBar.getChildren().addAll(statusLabel, new Separator(javafx.geometry.Orientation.VERTICAL), priceStatusLabel, spacer, versionLabel);
        statusBar.setSpacing(AppConstants.SPACING_STANDARD);
        return statusBar;
    }

    private void refreshPortfolioDisplay() {
        holdingsTable.getItems().clear();
        holdingsTable.getItems().addAll(portfolio.getHoldings());

        double totalValue = portfolio.getTotalValue();
        double totalPL = portfolio.getTotalProfitLoss();

        totalValueLabel.setText(String.format("$%.2f", totalValue));
        totalPLLabel.setText(String.format("$%.2f", totalPL));

        if (totalPL >= 0) {
            totalPLLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_ACCENT_GREEN + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        } else {
            totalPLLabel.setStyle("-fx-text-fill: " + AppConstants.COLOR_ERROR + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        }
    }

    private void removeSelectedHolding() {
        int selected = holdingsTable.getSelectionModel().getSelectedIndex();
        if (selected >= 0) {
            portfolio.removeHolding(selected);
            refreshPortfolioDisplay();
            showNotification("Stock removed.");
            Logger.info("Stock removed from portfolio at index: " + selected);
        } else {
            showError("Selection Error", "Please select a stock to remove.");
        }
    }

    private void generateAnalysis() {
        if (portfolio.isEmpty()) {
            showError("Empty Portfolio", "Portfolio is empty. Add some stocks first!");
            return;
        }

        String summary = generatePortfolioSummary();
        analysisArea.setText("Generating analysis...\n");
        
        new Thread(() -> {
            try {
                AIService service = aiFactory.getService();
                String analysis = service.getAnalysis(summary);
                Platform.runLater(() -> analysisArea.setText(analysis));
                Logger.info("Portfolio analysis generated successfully");
            } catch (Exception e) {
                Logger.error("Error generating analysis", e);
                Platform.runLater(() -> analysisArea.setText("Error generating analysis: " + e.getMessage()));
            }
        }).start();
    }

    private void generateRecommendations() {
        if (portfolio.isEmpty()) {
            showError("Empty Portfolio", "Portfolio is empty. Add some stocks first!");
            return;
        }

        String summary = generatePortfolioSummary();
        analysisArea.setText("Generating recommendations...\n");
        
        new Thread(() -> {
            try {
                AIService service = aiFactory.getService();
                String recommendations = service.getRecommendation(summary);
                Platform.runLater(() -> analysisArea.setText(recommendations));
                Logger.info("Investment recommendations generated successfully");
            } catch (Exception e) {
                Logger.error("Error generating recommendations", e);
                Platform.runLater(() -> analysisArea.setText("Error generating recommendations: " + e.getMessage()));
            }
        }).start();
    }

    private String generatePortfolioSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Current Portfolio Summary:\n\n");

        for (StockHolding holding : portfolio.getHoldings()) {
            summary.append(String.format("%s: %d shares @ $%.2f current price = $%.2f value, P/L: $%.2f (%.2f%%)\n",
                holding.getSymbol(),
                holding.getQuantity(),
                holding.currentPrice,
                holding.getValue(),
                holding.getProfitLoss(),
                holding.getPercentageChange()));
        }

        summary.append(String.format("\nTotal Portfolio Value: $%.2f\n", portfolio.getTotalValue()));
        summary.append(String.format("Total Profit/Loss: $%.2f\n", portfolio.getTotalProfitLoss()));

        return summary.toString();
    }

    private void switchAIService() {
        String selected = aiServiceCombo.getValue();
        aiFactory.switchService(selected.equals("Online API") ? "online" : "local");
        showNotification("Switched to " + selected);
        Logger.info("AI Service switched to: " + selected);
    }

    private void toggleTheme() {
        darkMode = !darkMode;
        Scene scene = ((Stage) darkModeToggle.getScene().getWindow()).getScene();
        applyTheme(scene, darkMode);
        Logger.info("Theme toggled. Dark mode: " + darkMode);
    }

    private void applyTheme(Scene scene, boolean isDarkMode) {
        if (isDarkMode) {
            scene.getRoot().setStyle(
                "-fx-base: " + AppConstants.COLOR_DARK_BG + "; " +
                "-fx-background: " + AppConstants.COLOR_DARK_PRIMARY + "; " +
                "-fx-control-inner-background: #3c3c3c; " +
                "-fx-focus-color: " + AppConstants.COLOR_ACCENT_GREEN + "; " +
                "-fx-text-base-color: " + AppConstants.COLOR_TEXT_PRIMARY + "; " +
                "-fx-text-fill: " + AppConstants.COLOR_TEXT_PRIMARY + ";"
            );
        } else {
            scene.getRoot().setStyle(
                "-fx-base: #f0f0f0; " +
                "-fx-background: #ffffff; " +
                "-fx-control-inner-background: #f5f5f5; " +
                "-fx-focus-color: " + AppConstants.COLOR_INFO + "; " +
                "-fx-text-base-color: #000000; " +
                "-fx-text-fill: #000000;"
            );
        }
    }

    private void saveAPIKey() {
        try {
            String apiKey = apiKeyField.getText();
            if (apiKey.isEmpty()) {
                showError("Invalid Input", "API key cannot be empty");
                return;
            }
            aiFactory.setAPIKey(apiKey);
            apiKeyField.setText(apiKey);
            showNotification("API Key saved successfully!");
            Logger.info("API key updated");
        } catch (IllegalArgumentException e) {
            showError("Validation Error", "Invalid API key format: " + e.getMessage());
            Logger.warning("Invalid API key format provided");
        } catch (Exception e) {
            Logger.error("Error saving API key", e);
            showError("Error", "Failed to save API key: " + e.getMessage());
        }
    }

    private String maskAPIKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "";
        }
        return apiKey.substring(0, Math.min(8, apiKey.length())) + "...";
    }

    private void exportPortfolioToCSV() {
        if (portfolio.isEmpty()) {
            showError("Empty Portfolio", "Portfolio is empty. Nothing to export.");
            return;
        }

        try {
            StringBuilder csv = new StringBuilder();
            csv.append("Symbol,Quantity,Purchase Price,Current Price,Value,Profit/Loss,Percentage Change\n");

            for (StockHolding holding : portfolio.getHoldings()) {
                csv.append(String.format("%s,%d,%.2f,%.2f,%.2f,%.2f,%.2f%%\n",
                    holding.getSymbol(),
                    holding.getQuantity(),
                    holding.purchasePrice,
                    holding.currentPrice,
                    holding.getValue(),
                    holding.getProfitLoss(),
                    holding.getPercentageChange()));
            }

            java.nio.file.Files.write(
                java.nio.file.Paths.get(AppConstants.EXPORT_FILE),
                csv.toString().getBytes()
            );
            showNotification("Portfolio exported to " + AppConstants.EXPORT_FILE);
            Logger.info("Portfolio exported successfully to: " + AppConstants.EXPORT_FILE);
        } catch (Exception e) {
            Logger.error("Error exporting portfolio", e);
            showError("Export Error", "Failed to export portfolio: " + e.getMessage());
        }
    }

    private void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
