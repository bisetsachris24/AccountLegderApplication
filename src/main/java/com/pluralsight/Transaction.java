package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Transaction {

    // Formatter used to standardize how dates are displayed and stored
    public static final DateTimeFormatter ledgerdateFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Formatter used to standardize time format (24-hour format)
    public static final DateTimeFormatter ledgertimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    // Each transaction has these attributes
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;
   // CONSTRUCTOR
    // Used to create a new Transaction object with all values
    public Transaction(LocalDate date, LocalTime time, String description,
                       String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }
    // created getter using constructor
    // Used to safely access private fields from outside the class

    public LocalDate getDate()
    { return date; }
    public LocalTime getTime()
    { return time; }
    public String    getDescription()
    { return description; }
    public String    getVendor()
    { return vendor; }
    public double    getAmount()
    { return amount; }


    // toString METHOD
    // Used to display transaction in a formatted table row
    public String toString() {
        return String.format("%-12s  %-8s  %-28s  %-15s  %10.2f",
                date.format(ledgerdateFormater),
                time.format(ledgertimeFormat),
                description,
                vendor,
                amount);
    }


    // Converts a Transaction object into a string for file storage
    public String toCsvLine() {
        // Pipe-delimited format used in transactions.csv
        return date.format(ledgerdateFormater) + "|"
                + time.format(ledgertimeFormat) + "|"
                + description + "|"
                + vendor + "|"
                + String.format("%.2f", amount);
    }
}
