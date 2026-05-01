# Accounting Ledger Application

# Overview
The Accounting Ledger Application is a Java-based console program designed to help users track financial transactions such as deposits and payments.

The application allows users to:
- Record transactions
- Store them in a file (`transactions.csv`)
- View and filter transaction history
- Generate financial reports

This project demonstrates real-world backend development concepts using core Java.

---

##  Features

###  Home Menu
- Add Deposit
- Make Payment (Debit)
- View Ledger
- Exit Application

---

###  Transactions
- **Deposits** are stored as positive values
- **Payments** are stored as negative values
- Each transaction includes:
    - Date
    - Time
    - Description
    - Vendor
    - Amount

---

###  Ledger Options
- View all transactions
- View only deposits
- View only payments
- Transactions are sorted **newest first**

---

### Reports
- Month-To-Date
- Previous Month
- Year-To-Date
- Previous Year
- Search by Vendor (case-insensitive)

---

### File Storage
- Data is stored in a `transactions.csv` file
- Uses pipe-delimited format (`|`)
- Automatically loads data on startup
- Appends new transactions without overwriting existing data

---

## Technologies Used
- Java (Core Java)
- File I/O (`BufferedReader`, `BufferedWriter`)
- Collections (`ArrayList`)
- Date & Time API (`LocalDate`, `LocalTime`, `LocalDateTime`)
- OOP (Object-Oriented Programming)

---

##  Project Structure

---

##  How It Works

1. On startup, the application reads `transactions.csv`
2. Converts each line into a `Transaction` object
3. Stores data in an `ArrayList`
4. User interacts through menu options
5. New transactions are saved to both memory and file

---

##  Key Concepts Demonstrated

- Object-Oriented Programming (OOP)
- File Handling (Read/Write)
- Exception Handling
- Data Structures (`ArrayList`)
- Sorting with `Comparator`
- Input Validation
- Date/Time Manipulation

---

## Example Transaction Format
2026-04-15|10:13:25|Laptop Purchase|Amazon|-899.99
2026-04-16|14:22:10|Salary Deposit|Company|2500.00

