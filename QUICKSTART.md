# Portfolio Tracker - Quick Start Guide

Get started with Portfolio Tracker in 5 minutes!

## ⚡ Installation (30 seconds)

### Option 1: From Executable JAR
```bash
java -jar portfolio-tracker-1.0.0-shaded.jar
```

### Option 2: From Source with Maven
```bash
cd PortfolioTracker
mvn javafx:run
```

## 🎯 Your First Portfolio (2 minutes)

1. **App opens** → Click the **Portfolio** tab (already selected)

2. **Add Your First Stock**
   - Symbol: `AAPL`
   - Quantity: `10`
   - Purchase Price: `150`
   - Current Price: `180`
   - Click **Add Stock**

3. **Add Another Stock**
   - Symbol: `GOOGL`
   - Quantity: `5`
   - Purchase Price: `100`
   - Current Price: `120`
   - Click **Add Stock**

4. **Check Your Stats** → See total value and profit/loss at the bottom!

## 🤖 Get AI Analysis (1 minute)

### Local Analysis (Fastest - No Setup!)
1. Go to **AI Analysis** tab
2. Click **Get Analysis**
3. See recommendations instantly!

### Online Analysis (Better Insights)
1. Get free API key: https://platform.openai.com/api-keys
2. Go to **Settings** tab
3. Select "Online API"
4. Paste your API key
5. Click **Save Key**
6. Go to **AI Analysis** → Click **Get Analysis**

## 💡 Pro Tips

| Task | Steps |
|------|-------|
| **Remove Stock** | Select row → Click "Remove Selected" |
| **Export Data** | Settings tab → "Export Portfolio (CSV)" |
| **Switch Theme** | Top right → Toggle "🌙" button |
| **Switch AI** | Top area → Select "Local Analysis" or "Online API" |
| **See Your P/L** | Orange box at bottom shows profit/loss |

## 🎨 UI Overview

```
┌─────────────────────────────────────────────────┐
│  📈 Portfolio Tracker  [AI Service] [Dark/Light]│
├─────────────────────────────────────────────────┤
│ [Portfolio] [AI Analysis] [Settings]            │
├─────────────────────────────────────────────────┤
│                                                 │
│  Add Stock Form (Symbol, Qty, Prices)          │
│                                                 │
│  ┌─────────────────────────────────────────┐   │
│  │ Symbol │ Qty │ Value ($) │ P/L ($)     │   │
│  ├─────────────────────────────────────────┤   │
│  │ AAPL   │ 10  │ 1800.00   │ 300.00      │   │
│  │ GOOGL  │ 5   │ 600.00    │ 100.00      │   │
│  └─────────────────────────────────────────┘   │
│                  [Remove Selected]              │
│                                                 │
│  Total Value: $2,400 │ Total P/L: $400        │
│                                                 │
└─────────────────────────────────────────────────┘
│ ✓ Ready                      Portfolio Tracker v1.0.0 │
└─────────────────────────────────────────────────────┘
```

## ❓ FAQ

**Q: Do I need internet?**  
A: No! Use Local Analysis. Internet needed only for Online API.

**Q: Is my API key safe?**  
A: Yes! Masked in UI, never logged. Stored locally in `ai_config.properties`.

**Q: Can I import/export my portfolio?**  
A: Yes! Go to Settings → "Export Portfolio (CSV)" to download.

**Q: What if AI analysis is slow?**  
A: First request might take 10-30 seconds. Subsequent ones are faster. Use Local Analysis for instant results.

**Q: How do I backup my portfolio?**  
A: Export to CSV from Settings tab, or copy `ai_config.properties` to backup folder.

## 🚨 Troubleshooting

| Problem | Solution |
|---------|----------|
| Won't start | Ensure Java 11+ installed: `java -version` |
| Online API fails | Check internet, verify API key, check logs in `portfolio_tracker.log` |
| App crashes | Check `portfolio_tracker.log` for errors |
| Numbers look weird | Refresh portfolio by adding/removing a stock |

## 📞 Need Help?

1. Check `portfolio_tracker.log` for error details
2. Review full README.md for detailed documentation
3. Read DEVELOPER_GUIDE.md if you want to contribute

## 🎓 Next Steps

- ✅ Master Portfolio tab to add/manage stocks
- ✅ Experiment with both AI analysis options
- ✅ Export portfolio and open in Excel
- ✅ Try switching themes with dark mode toggle
- ✅ Explore Settings to configure your API key

---

**Start investing smarter today!** 🚀

Want detailed docs? See **README.md**  
Want to contribute? See **DEVELOPER_GUIDE.md**
