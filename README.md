# Investment Portfolio Tracker

A simple Java console application to track and calculate the value and profit/loss of stock holdings in a personal investment portfolio. Built as a demonstration of programming, problem-solving, and applied finance concepts.

---

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [Technologies](#technologies)
- [Installation](#installation)
- [Usage](#usage)
- [Example](#example)
- [Project Structure](#project-structure)
- [Optional Enhancements](#optional-enhancements)
- [License](#license)

---

## Project Overview
This project allows a user to manually add stocks to their portfolio, view a report of the current value of each holding, and calculate the total profit or loss. It demonstrates:
- Object-oriented programming (OOP) concepts in Java
- Use of classes and methods to model real-world objects
- Basic console input/output
- Simple arithmetic calculations for investment tracking

---

## Features
- Add stock holdings with:
  - Stock symbol
  - Quantity of shares
  - Purchase price
  - Current price
- Display a portfolio report showing:
  - Value of each stock
  - Profit or loss per stock
  - Total portfolio value and overall profit/loss
- Simple console menu with options to add stocks, view report, and exit

---

## Technologies
- Java 24
- IntelliJ IDEA (or any Java IDE)
- No external dependencies or build tools required
- Optional: JUnit 5 for unit testing

---

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/PortfolioTracker.git
   ```
2. Open IntelliJ IDEA → **File → Open** → select the project folder.
3. Ensure your **Project SDK** is set to Java 24:
   - `File → Project Structure → Project SDK → 24`
4. You’re ready to run the project.

---

## Usage
1. Run the program by opening `PortfolioTracker.java` → click the green **Run** button next to `main`.
2. Follow the console prompts:
   - Enter `1` to add a stock
   - Enter `2` to show the portfolio report
   - Enter `3` to exit the program

---

## Example
```
1. Add stock
2. Show report
3. Exit
Choice: 1
Enter stock symbol: AAPL
Enter quantity: 10
Enter purchase price: 100
Enter current price: 120
Added.

1. Add stock
2. Show report
3. Exit
Choice: 2
------ Portfolio Report ------
AAPL | Qty: 10 | Value: 1200.00 | P/L: 200.00
Total Value: 1200.00 | Total P/L: 200.00
```

---

## Project Structure
```
PortfolioTracker/
 ├── src/
 │   └── main/java/
 │       ├── StockHolding.java      # Defines stock holding attributes and methods
 │       ├── Portfolio.java         # Manages list of stocks, calculations, and report
 │       └── PortfolioTracker.java  # Main class with console menu
 └── README.md
```

---

## Optional Enhancements
- Save/load portfolio data to a CSV file
- Fetch live stock prices from an API (e.g., Yahoo Finance)
- Export portfolio report to PDF or Excel
- Add a simple GUI with JavaFX
- Unit tests with JUnit 5 for all calculations

---

## License
This project is open source and free to use for educational purposes.
