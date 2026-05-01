package com.pluralsight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;


public class AccountingLedgerApp {

    // All the loaded transactions live in this list. */
    static ArrayList<Transaction> transactions = new ArrayList<>();

    //** One Scanner reads input from the keyboard for the whole program.
    static Scanner scanner = new Scanner(System.in);

    /**
     * Name of the file we read from / write to.
     */
    static final String amaniFile = "src/main/resources/transactions.csv";


    // MAIN this is where Java run, this is where everything starts

    public static void main(String[] args) {
        printBanner();
        loadTransactions();   // step 1: read the CSV into memory
        homeMenu();           // step 2: start the Home menu loop
        scanner.close();
    }

    /**
     * A welcome banner shown once when the program starts.
     */
    static void printBanner() {
        System.out.println(" AMANI's LEDGER ");
    }


    static void loadTransactions() {
        // Create a File object that represents the transactions.csv file.
        // This does NOT read the file yet—it just points to its location.
        File f = new File(amaniFile);

        // Check if the file exists in the system.
        // If it does NOT exist, there is no data to load.
        if (!f.exists()) {
            // Exit the method early to avoid errors (like FileNotFoundException).
            // The program will simply continue with an empty transactions list.
            return;
        }
        // Use try-with-resources to automatically close the reader after use.
// BufferedReader improves performance by reading text efficiently line-by-line.
// FileReader is used to read characters from the file.
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            // Variable to temporarily store each line read from the file
            String line;
            // Read the file one line at a time until the end (null means no more lines)
            while ((line = reader.readLine()) != null) {
                // Remove leading and trailing whitespace from the line
                line = line.trim();
                // If the line is empty, skip it and move to the next iteration
                if (line.isEmpty()) continue;
                // If the line is a header (e.g., column names like "date|time|..."),
                // skip it so it is not treated as actual transaction data// skip blank lines
                if (line.startsWith("date|")) continue;// skip header line
                // Wrap parsing logic in a try-catch block to prevent the program
                // from crashing if a line is malformed or contains invalid data
                try {
                    // Split the line into parts using "|" as the delimiter
                    // Each part represents a field: date, time, description, vendor, amount
                    String[] parts = line.split("\\|");
                    // Parse the date string into a LocalDate object using the defined formatter
                    LocalDate date = LocalDate.parse(parts[0], Transaction.ledgerdateFormater);
                    // Parse the time string into a LocalTime object using the defined formatter
                    LocalTime time = LocalTime.parse(parts[1], Transaction.ledgertimeFormat);
                    // Extract description and vendor as plain strings
                    String description = parts[2];
                    String vendor = parts[3];
                    // Convert the amount from String to double
                    double amount = Double.parseDouble(parts[4]);
                    // Create a new Transaction object using parsed data
                    // and add it to the in-memory transactions list
                    transactions.add(new Transaction(date, time, description, vendor, amount));
                } catch (Exception ex) {
                    // One bad line shouldn't stop the whole program.
                    System.out.print("Skipped a bad line: " + line);
                }
            }
        } catch (IOException e) {
            // Catch any file-related errors (e.g., file not found, read failure)
            //    // IOException is used for problems during file input/output operations
            //
            //    // Print an error message to the console to inform the user
            //    // getMessage() provides details about what went wrong
            System.err.println("Could not read file: " + e.getMessage());
        }
    }


    static void saveTransaction(Transaction t) {
        // Add the new transaction to the in-memory list (ArrayList)
        // This ensures it is immediately available for viewing in the program
        transactions.add(t);
        // Use try-with-resources to safely open and automatically close the writer
        // FileWriter with 'true' enables APPEND mode (prevents overwriting existing data)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(amaniFile, true))) {
            // Convert the Transaction object into a CSV-formatted string
            // and write it to the file
            writer.write(t.toCsvLine());
            // Move to the next line in the file after writing the transaction
            writer.newLine();
        } catch (IOException e) {
            // Handle any file writing errors (e.g., permission issues, disk problems)
            // Print an error message so the user is aware something went wrong
            System.err.println("Could not save: " + e.getMessage());
        }
    }

    // HOME MENU

    static void homeMenu() {
        // Boolean flag used to control the loop.
        // As long as 'running' is true, the menu will keep displaying.
        boolean running = true;
        // Main loop that keeps the application running until the user chooses to exit
        while (running) {


            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║              HOME  MENU                  ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  D)  Add Deposit                         ║");
            System.out.println("║  P)  Make Payment  (Debit)               ║");
            System.out.println("║  L)  Ledger                              ║");
            System.out.println("║  X)  Exit                                ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Enter your choice: ");
// Read user input from the keyboard
// trim() removes extra spaces, toUpperCase() ensures input is case-insensitive
            String choice = scanner.nextLine().trim().toUpperCase();
            // Use switch-case to handle different user menu selections
            switch (choice) {
                // Call method to add a deposit transaction
                case "D":
                    addDeposit();
                    break;
                // Call method to record a payment (expense)
                case "P":
                    addPayment();
                    break;
                // Navigate to the ledger menu (view transactions and reports)
                case "L":
                    ledgerMenu();
                    break;

                case "X":
                    // Set running to false to exit the loop and terminate the program
                    running = false;
                    System.out.println("\nGoodbye! Thanks for using the Accounting Ledger.\n");
                    break;
                default:
                    // Handle invalid input (anything other than D, P, L, X)
                    System.out.println(">> Invalid choice. Pick D, P, L or X.\n");
            }
        }
    }


    static void addDeposit() {
        // Display section header to indicate user is adding a deposit
        System.out.println("\n-- Add Deposit --");
        // Prompt user to enter a description for the transaction
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
// Prompt user to enter the vendor or source of the deposit
        System.out.print("Vendor / Source: ");
        String vendor = scanner.nextLine().trim();
        // Call helper method to ensure the user enters a valid positive amount
        double amount = readPositiveAmount("Amount: ");
        // Get the current date and time from the system
        LocalDateTime now = LocalDateTime.now();
// Extract only the date portion
        LocalDate date = now.toLocalDate();
        // Extract only the time portion and remove nanoseconds for cleaner formatting
        LocalTime time = now.toLocalTime().withNano(0);   // throw away nanoseconds
// Create a new Transaction object and save it (both in memory and file)
        // Deposits remain as positive values
        saveTransaction(new Transaction(date, time, description, vendor, amount));
        System.out.println(">> Deposit saved!\n");
    }


    static void addPayment() {
        // Display section header to indicate user is recording a payment (expense)
        System.out.println("\n-- Make Payment --");

        System.out.print("Description: ");
        // Prompt user for a description of the payment
        String description = scanner.nextLine().trim();
// Prompt user for the vendor (who the payment is made to)
        System.out.print("Vendor: ");
        String vendor = scanner.nextLine().trim();
//Read and validate a positive amount of user
        double amount = readPositiveAmount("Amount: ");
        // Convert the amount to negative to represent money going out (expense)
        amount = -amount;   // i also changed the sign

        // Get the current date and time
        LocalDateTime now = LocalDateTime.now();
        // Create and save the transaction (stored in memory and appended to file)
        saveTransaction(new Transaction(
                now.toLocalDate(),// this code helps me to extract current date
                now.toLocalTime().withNano(0),// extract with nanoseconds
                description, vendor, amount));
        System.out.println(">> Payment saved!\n"); // statement to confirm successful saved
    }


    // LEDGER MENU
    // =========================================================================
    static void ledgerMenu() {
        // Boolean flag to control the Ledger menu loop
        // Keeps the user inside the Ledger menu until they choose to go back
        boolean inLedger = true;
        // Loop continuously displays the Ledger menu options
        // until the user selects "H" (Home)
        while (inLedger) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║             LEDGER  MENU                 ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  A)  All                                 ║");
            System.out.println("║  D)  Deposits                            ║");
            System.out.println("║  P)  Payments                            ║");
            System.out.println("║  R)  Reports                             ║");
            System.out.println("║  H)  Home                                ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim().toUpperCase();
            switch (choice) {
                // Display all transactions (both deposits and payments)
                case "A":
                    showAll();
                    break;
                // Display only deposit transactions (positive amounts)
                case "D":
                    showDeposits();
                    break;
                // Display only payment transactions (negative amounts)
                case "P":
                    showPayments();
                    break;
                // Navigate to the Reports menu for filtered views

                case "R":
                    reportsMenu();
                    break;
                // Exit the Ledger menu and return to the Home menu
                case "H":
                    inLedger = false;
                    break;   // back to Home
                default:
                    System.out.println(">> Invalid choice. Pick A, D, P, R or H.\n");
            }
        }
    }


    // Creates and returns a new list of transactions sorted from newest to oldest
    static ArrayList<Transaction> getAllNewestFirst() {
        // Create a copy of the original transactions list
        // This prevents modifying the original data in memory
        ArrayList<Transaction> copy = new ArrayList<>(transactions);
        // Sort transactions by date first, then by time
        // Then reverse the order so the newest transactions appear first
        copy.sort(Comparator
                .comparing(Transaction::getDate)
                .thenComparing(Transaction::getTime)
                .reversed());
        // Return the sorted copy
        return copy;
    }

    // Displays all transactions in a formatted table
    static void showAll() {
        // Call printList method to display all transactions
        // "All Entries" is the title shown above the table
        printList("All Entries", getAllNewestFirst());
    }

    // Displays only deposit transactions (transactions with positive amounts)
    static void showDeposits() {
        // Create a new list to store filtered deposit transactions
        ArrayList<Transaction> deposits = new ArrayList<>();
        // Loop through all transactions sorted from newest to oldest
        for (Transaction t : getAllNewestFirst()) {
            // A deposit is defined as any transaction with a positive amount
            if (t.getAmount() > 0)
                deposits.add(t); // Add the transaction to the deposits list
        }
        printList("Deposits", deposits);
    }

    // // Displays only payment transactions (transactions with negative amounts)
    static void showPayments() {
        // Create a new list to store only payment (expense) transactions

        ArrayList<Transaction> payments = new ArrayList<>();
        // Loop through all transactions sorted from newest to oldest
        for (Transaction t : getAllNewestFirst()) {
            // A payment is defined as any transaction with a negative amount
            if (t.getAmount() < 0)
                // Add the transaction to the payments list
                payments.add(t);
        }
        printList("Payments", payments);
    }

    // Displays a list of transactions in a clean, formatted table
// This method is reusable for all views (All, Deposits, Payments, Reports)
    static void printList(String title, ArrayList<Transaction> list) {
        // Print a blank line for spacing
        System.out.println();
        // Print the title of the section (e.g., "Deposits", "Payments")
        System.out.println("====================  " + title + "  ====================");
        // Print column headers with fixed-width formatting for alignment
        // %-12s = left-aligned string with width 12, etc.
        System.out.printf("%-12s  %-8s  %-28s  %-15s  %10s%n",
                "DATE", "TIME", "DESCRIPTION", "VENDOR", "AMOUNT");
        System.out.println("-----------------------------------------------------------------------------------");
        // Check if the list is empty
        if (list.isEmpty()) {

            // Inform the user that no transactions were found
            System.out.println("  (no transactions found)");
        } else {
            // Loop through each transaction and print it
            // The toString() method in Transaction class formats each row
            for (Transaction t : list) System.out.println(t);
        }
        // print closing line
        System.out.println("===================================================================================\n");
    }


    // REPORTS MENU

    static void reportsMenu() {
        boolean inReports = true;
        while (inReports) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║            REPORTS  MENU                 ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  1)  Month To Date                       ║");
            System.out.println("║  2)  Previous Month                      ║");
            System.out.println("║  3)  Year To Date                        ║");
            System.out.println("║  4)  Previous Year                       ║");
            System.out.println("║  5)  Search by Vendor                    ║");
            System.out.println("║  0)  Back                                ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Enter your choice: ");
// Read user input for reports menu selection
// trim() removes extra spaces (no need for toUpperCase since input is numeric)
            String choice = scanner.nextLine().trim();
            // Use switch-case to handle different report options
            switch (choice) {
                case "1":
                    // Display transactions from the beginning of the current month to today
                    monthToDate();
                    break;
                case "2":
                    // Display transactions from the previous month
                    previousMonth();
                    break;
                case "3":
                    yearToDate();
                    break;
                case "4":
                    previousYear();
                    break;
                case "5":
                    vendorSearch();
                    break;
                case "0":
                    inReports = false;
                    break;
                default:
                    System.out.println(">> NO buenoooo Pick 0-5.\n");
            }
        }
    }


    // Filters transactions based on a date range (inclusive)
// Returns only transactions whose date falls between start and end
    static ArrayList<Transaction> filterByDate(LocalDate start, LocalDate end) {
        // Create a list to store filtered transactions
        ArrayList<Transaction> result = new ArrayList<>();
        // Loop through all transactions sorted from newest to oldest
        for (Transaction t : getAllNewestFirst()) {
            // Get the date of the current transaction
            LocalDate d = t.getDate();
            // Skip this transaction if it is outside the date range
            // (before the start date OR after the end date)
            if (d.isBefore(start) || d.isAfter(end)) continue;
            // If the date is within the range, add it to the result list
            result.add(t);
        }
        return result;
    }

    static void monthToDate() {
        // Get today's date
        LocalDate today = LocalDate.now();
        // Set the start date to the first day of the current month
        // Example: if today is May 1 → start becomes May 1
        LocalDate start = today.withDayOfMonth(1);
        // Filter transactions within the date range and display them
        // The title also shows the date range for clarity
        printList("Month To Date  (" + start + " → " + today + ")", filterByDate(start, today));
    }

    static void previousMonth() {
        LocalDate today = LocalDate.now();
        // Get the first day of the current month
        LocalDate firstOfThisMonth = today.withDayOfMonth(1);
        // Subtract one day to get the last day of the previous month
        // Example: if today is May 10 → this becomes April 30
        LocalDate lastOfPrevMonth = firstOfThisMonth.minusDays(1);
        // Get the first day of the previous month
        // Example: April 30 → becomes April 1
        LocalDate firstOfPrevMonth = lastOfPrevMonth.withDayOfMonth(1);
        // Filter transactions within the previous month date range and display them
        printList("Previous Month (" + firstOfPrevMonth + " → " + lastOfPrevMonth + ")",
                filterByDate(firstOfPrevMonth, lastOfPrevMonth));
    }

    static void yearToDate() {
        LocalDate today = LocalDate.now();
        // Set the start date to January 1st of the current year
        // withDayOfYear(1) automatically sets the date to the first day of the year
        LocalDate start = today.withDayOfYear(1);// Jan 1 of this year
        // Filter transactions within the date range and display them
        // The title includes the date range for clarity
        printList("Year To Date  (" + start + " → " + today + ")", filterByDate(start, today));
    }

    static void previousYear() {
        int year = LocalDate.now().getYear() - 1;
        // Create the start date: January 1st of the previous year
        LocalDate start = LocalDate.of(year, 1, 1);
        // Create the end date: December 31st of the previous year
        LocalDate end = LocalDate.of(year, 12, 31);
        printList("Previous Year (" + year + ")", filterByDate(start, end));
    }

    static void vendorSearch() {
        System.out.print("Vendor name (partial match, case-insensitive): ");
        // Read user input and remove extra spaces
        String query = scanner.nextLine().trim();
        // Check if the user entered nothing
        if (query.isEmpty()) {
            System.out.println(">> No vendor name entered.\n");
            // Exit the method early to avoid unnecessary processing
            return;
        }
        ArrayList<Transaction> result = new ArrayList<>();
        String q = query.toLowerCase();
        for (Transaction t : getAllNewestFirst()) {
            // Convert vendor name to lowercase and check if it contains the search query
            // This allows partial matching (e.g., "ama" matches "Amazon")
            if (t.getVendor().toLowerCase().contains(q))
                // Add matching transaction to the result list
                result.add(t);
        }
        // Display the filtered results with a title showing the search query
        printList("Vendor: \"" + query + "\"", result);
    }


    // Prompts the user until they enter a valid positive number
// This method ensures input is numeric and greater than 0
    static double readPositiveAmount(String prompt) {
        // Infinite loop that continues until valid input is entered
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                // Attempt to convert the input string to a double
                double value = Double.parseDouble(raw);
                // Check if the value is less than or equal to zero
                if (value <= 0) {
                    // Inform the user that only positive values are allowed
                    System.out.println(">> Amount must be greater than 0.");
                    continue;  // Continue the loop to ask again
                }
                // If valid it will return the value
                return value;
            } catch (NumberFormatException e) {
                // Handle invalid input (non-numeric values)
                System.out.println(">> That's not a valid number. Try again.");
            }
        }
    }
}
