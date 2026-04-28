package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Transaction {

    public static final DateTimeFormatter ledgerdateFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter ledgertimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;

    public Transaction(LocalDate date, LocalTime time, String description,
                       String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }
    // created getter using constructor
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

    /** Pretty one-line display for printing on screen. */

    public String toString() {
        return String.format("%-12s  %-8s  %-28s  %-15s  %10.2f",
                date.format(ledgerdateFormater),
                time.format(ledgertimeFormat),
                description,
                vendor,
                amount);
    }

    /**
     * Builds the pipe-delimited line we save to transactions.csv.
     * Example: 2026-04-15|10:13:25|ergonomic keyboard|Amazon|-89.50
     */
    public String toCsvLine() {
        return date.format(ledgerdateFormater) + "|"
                + time.format(ledgertimeFormat) + "|"
                + description + "|"
                + vendor + "|"
                + String.format("%.2f", amount);
    }
}
