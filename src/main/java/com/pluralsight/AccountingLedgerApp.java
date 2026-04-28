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

    /** One Scanner reads input from the keyboard for the whole program. */
    static Scanner scanner = new Scanner(System.in);

    /** Name of the file we read from / write to. */
    static final String amaniFile = "transactions.csv";


    // MAIN this is where Java run, this is where everything starts

    public static void main(String[] args) {
        printBanner();
        loadTransactions();   // step 1: read the CSV into memory
        homeMenu();           // step 2: start the Home menu loop
        scanner.close();
    }

    /** A welcome banner shown once when the program starts. */
    static void printBanner() {
        System.out.println(" AMANI's LEDGER ");
    }

    // =========================================================================

    /**
     * Reads transactions.csv line-by-line and converts each line into a
     * Transaction object. If the file does not exist yet, we just start with
     * an empty list (that is fine — the first deposit will create it).
     */
    static void loadTransactions() {
        File f = new File(amaniFile);

        if (!f.exists()) {
            return;
        }
        // try-with-resources auto-closes the reader, even if there's an error.
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;             // skip blank lines
                if (line.startsWith("date|")) continue;   // skip header line
                try {
                    String[] parts = line.split("\\|");
                    LocalDate date = LocalDate.parse(parts[0], Transaction.ledgerdateFormater);
                    LocalTime time = LocalTime.parse(parts[1], Transaction.ledgertimeFormat);
                    String description = parts[2];
                    String vendor      = parts[3];
                    double amount      = Double.parseDouble(parts[4]);
                    transactions.add(new Transaction(date, time, description, vendor, amount));
                } catch (Exception ex) {
                    // One bad line shouldn't stop the whole program.
                    System.out.print("Skipped a bad line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
        }
    }


    static void saveTransaction(Transaction t) {
        transactions.add(t);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(amaniFile, true))) {
            writer.write(t.toCsvLine());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Could not save: " + e.getMessage());
        }
    }


    // HOME MENU
    // =========================================================================
    static void homeMenu() {
        boolean running = true;
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

            String choice = scanner.nextLine().trim().toUpperCase();
            switch (choice) {
                case "D": addDeposit();
                break;
                case "P": addPayment();
                break;
                case "L": ledgerMenu();
                break;
                case "X":
                    running = false;
                    System.out.println("\nGoodbye! Thanks for using the Accounting Ledger.\n");
                    break;
                default:
                    System.out.println(">> Invalid choice. Pick D, P, L or X.\n");
            }
        }
    }


    static void addDeposit() {
        System.out.println("\n-- Add Deposit --");
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Vendor / Source: ");
        String vendor = scanner.nextLine().trim();

        double amount = readPositiveAmount("Amount: ");

        // LocalDateTime.now() = "right now". Split into a date and a time.
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime().withNano(0);   // throw away nanoseconds

        saveTransaction(new Transaction(date, time, description, vendor, amount));
        System.out.println(">> Deposit saved!\n");
    }


    static void addPayment() {
        System.out.println("\n-- Make Payment --");
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Vendor: ");
        String vendor = scanner.nextLine().trim();

        double amount = readPositiveAmount("Amount: ");
        amount = -amount;   // change the sign

        LocalDateTime now = LocalDateTime.now();
        saveTransaction(new Transaction(
                now.toLocalDate(),
                now.toLocalTime().withNano(0),
                description, vendor, amount));
        System.out.println(">> Payment saved!\n");
    }

    // =========================================================================
    // LEDGER MENU
    // =========================================================================
    static void ledgerMenu() {
        boolean inLedger = true;
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
                case "A": showAll();
                break;
                case "D": showDeposits();
                break;
                case "P": showPayments();
                break;
                case "R": reportsMenu();
                break;
                case "H": inLedger = false;
                break;   // back to Home
                default:
                    System.out.println(">> Invalid choice. Pick A, D, P, R or H.\n");
            }
        }
    }

    /** Returns a fresh copy of transactions sorted NEWEST FIRST. */
    static ArrayList<Transaction> getAllNewestFirst() {
        ArrayList<Transaction> copy = new ArrayList<>(transactions);
        copy.sort(Comparator
                .comparing(Transaction::getDate)
                .thenComparing(Transaction::getTime)
                .reversed());
        return copy;
    }

    static void showAll() {
        printList("All Entries", getAllNewestFirst());
    }

    static void showDeposits() {
        ArrayList<Transaction> deposits = new ArrayList<>();
        for (Transaction t : getAllNewestFirst()) {
            if (t.getAmount() > 0) deposits.add(t);
        }
        printList("Deposits", deposits);
    }

    static void showPayments() {
        ArrayList<Transaction> payments = new ArrayList<>();
        for (Transaction t : getAllNewestFirst()) {
            if (t.getAmount() < 0) payments.add(t);
        }
        printList("Payments", payments);
    }

    /**
     * Prints a list of transactions as a nice table.
     * Reused by every screen that shows a list.
     */
    static void printList(String title, ArrayList<Transaction> list) {
        System.out.println();
        System.out.println("====================  " + title + "  ====================");
        System.out.printf("%-12s  %-8s  %-28s  %-15s  %10s%n",
                "DATE", "TIME", "DESCRIPTION", "VENDOR", "AMOUNT");
        System.out.println("-----------------------------------------------------------------------------------");
        if (list.isEmpty()) {
            System.out.println("  (no transactions found)");
        } else {
            for (Transaction t : list) System.out.println(t);
        }
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

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": monthToDate();
                break;
                case "2": previousMonth();
                break;
                case "3": yearToDate();
                break;
                case "4": previousYear();
                break;
                case "5": vendorSearch();
                break;
                case "0": inReports = false;
                break;
                default:
                    System.out.println(">> Invalid choice. Pick 0-5.\n");
            }
        }
    }

    /** Returns transactions whose date is between start and end (INCLUSIVE). */
    static ArrayList<Transaction> filterByDate(LocalDate start, LocalDate end) {
        ArrayList<Transaction> result = new ArrayList<>();
        for (Transaction t : getAllNewestFirst()) {
            LocalDate d = t.getDate();
            if (d.isBefore(start) || d.isAfter(end)) continue;
            result.add(t);
        }
        return result;
    }

    static void monthToDate() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfMonth(1);
        printList("Month To Date  (" + start + " → " + today + ")", filterByDate(start, today));
    }

    static void previousMonth() {
        LocalDate today = LocalDate.now();
        LocalDate firstOfThisMonth = today.withDayOfMonth(1);
        LocalDate lastOfPrevMonth  = firstOfThisMonth.minusDays(1);
        LocalDate firstOfPrevMonth = lastOfPrevMonth.withDayOfMonth(1);
        printList("Previous Month (" + firstOfPrevMonth + " → " + lastOfPrevMonth + ")",
                filterByDate(firstOfPrevMonth, lastOfPrevMonth));
    }

    static void yearToDate() {
        LocalDate today = LocalDate.now();
        LocalDate start = today.withDayOfYear(1);   // Jan 1 of this year
        printList("Year To Date  (" + start + " → " + today + ")", filterByDate(start, today));
    }

    static void previousYear() {
        int year = LocalDate.now().getYear() - 1;
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end   = LocalDate.of(year, 12, 31);
        printList("Previous Year (" + year + ")", filterByDate(start, end));
    }

    static void vendorSearch() {
        System.out.print("Vendor name (partial match, case-insensitive): ");
        String query = scanner.nextLine().trim();
        if (query.isEmpty()) {
            System.out.println(">> No vendor name entered.\n");
            return;
        }
        ArrayList<Transaction> result = new ArrayList<>();
        String q = query.toLowerCase();
        for (Transaction t : getAllNewestFirst()) {
            if (t.getVendor().toLowerCase().contains(q)) result.add(t);
        }
        printList("Vendor: \"" + query + "\"", result);
    }




    /** Keeps prompting until the user types a valid number greater than 0. */
    static double readPositiveAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String raw = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(raw);
                if (value <= 0) {
                    System.out.println(">> Amount must be greater than 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println(">> That's not a valid number. Try again.");
            }
        }
    }
}
