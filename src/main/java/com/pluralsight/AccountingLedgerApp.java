package com.pluralsight;

import java.util.Scanner;


public class AccountingLedgerApp {

    // ---- Shared scanner: reads keyboard input for the whole app ----
    static Scanner scanner = new Scanner(System.in);


    //  MAIN  - the program starts here
    // ============================
    public static void main(String[] args) {
        printBanner();   // show a welcome message once
        homeMenu();      // start the menu loop
        scanner.close(); // tidy up when we're done
    }


    //  BANNER
    // ============================
    static void printBanner() {
        System.out.println();
        System.out.println("=========================================");
        System.out.println("   AMANI'SACCOUNTING LEDGER  ");
        System.out.println("=========================================\n");
    }

    // ============================
    //  HOME MENU
    // ============================
    static void homeMenu() {
        boolean running = true;
        while (running) {
            // 1) Print the menu
            System.out.println("╔══════════════════════════════════════════╗");
            System.out.println("║              HOME  MENU                  ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║  D)  Add Deposit                         ║");
            System.out.println("║  P)  Make Payment  (Debit)               ║");
            System.out.println("║  L)  Ledger                              ║");
            System.out.println("║  X)  Exit                                ║");
            System.out.println("╚══════════════════════════════════════════╝");
            System.out.print("Enter your choice: ");

            // 2) Read what the user typed
            //    .trim()       -> remove spaces
            //    .toUpperCase() -> turn 'd' into 'D' so the switch matches
            String choice = scanner.nextLine().trim().toUpperCase();

            // 3) Decide what to do
            switch (choice) {
                case "D":
                    System.out.println("\n>> [D] Add Deposit \n");
                    break;
                case "P":
                    System.out.println("\n>> [P] Make Payment \n");
                    break;
                case "L":
                    System.out.println("\n>> [L] Ledger — \n");
                    break;
                case "X":
                    running = false;   // stops the while loop -> ends the program
                    System.out.println("\nGoodbye! Thanks for using the Accounting Ledger.\n");
                    break;
                default:
                    System.out.println("\n>> Invalid choice. Please pick D, P, L or X.\n");
            }
        }
    }
}
