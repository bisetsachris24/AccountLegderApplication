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
import java.util.Scanner;

/**

 */
public class AccountingLedgerApp {

    // ---- Shared data ----
    static ArrayList<Transaction> transactions = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static final String FILE_NAME = "transactions.csv";

    // ============================
    //  MAIN
    // ============================
    public static void main(String[] args) {
        printBanner();
        loadTransactions();   // NEW: read existing data first
        homeMenu();
        scanner.close();
    }

    static void printBanner() {
        System.out.println();
        System.out.println("=========================================");
        System.out.println("   ACCOUNTING LEDGER  -  Capstone 1");
        System.out.println("=========================================\n");
    }

    // ============================
    //  FILE I/O   (NEW IN STEP 3)
    // ============================
    static void loadTransactions() {
        File f = new File(FILE_NAME);
        if (!f.exists()) {
            return;   // first run: nothing to load yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;            // skip blank lines
                if (line.startsWith("date|")) continue;  // skip header row

                try {
                    String[] parts = line.split("\\|");
                    LocalDate date     = LocalDate.parse(parts[0], Transaction.DATE_FORMAT);
                    LocalTime time     = LocalTime.parse(parts[1], Transaction.TIME_FORMAT);
                    String description = parts[2];
                    String vendor      = parts[3];
                    double amount      = Double.parseDouble(parts[4]);
                    transactions.add(new Transaction(date, time, description, vendor, amount));
                } catch (Exception ex) {
                    System.err.println("Skipped a bad line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read file: " + e.getMessage());
        }
    }

    static void saveTransaction(Transaction t) {
        transactions.add(t);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(t.toCsvLine());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Could not save: " + e.getMessage());
        }
    }

    // ============================
    //  HOME MENU
    // ============================
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
                case "D": addDeposit();   break;   // NEW: real method
                case "P": addPayment();   break;   // NEW: real method
                case "L":
                    System.out.println("\n>> [L] Ledger — coming in Step 4!\n");
                    break;
                case "X":
                    running = false;
                    System.out.println("\nGoodbye! Thanks for using the Accounting Ledger.\n");
                    break;
                default:
                    System.out.println("\n>> Invalid choice. Pick D, P, L or X.\n");
            }
        }
    }

    // ============================
    //  ADD DEPOSIT / PAYMENT   (NEW IN STEP 3)
    // ============================
    static void addDeposit() {
        System.out.println("\n-- Add Deposit --");
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        System.out.print("Vendor / Source: ");
        String vendor = scanner.nextLine().trim();

        double amount = readPositiveAmount("Amount: ");

        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime().withNano(0);

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
        amount = -amount;   // flip the sign

        LocalDateTime now = LocalDateTime.now();
        saveTransaction(new Transaction(
                now.toLocalDate(),
                now.toLocalTime().withNano(0),
                description, vendor, amount));
        System.out.println(">> Payment saved!\n");
    }

    // ============================
    //  INPUT HELPER   (NEW IN STEP 3)
    // ============================
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
