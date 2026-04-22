package oop_deep_dive.examples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulation done RIGHT — BankAccount with proper validation,
 * defensive copies, and no mindless getters/setters.
 */
public class EncapsulationExample {

    // ============================================================
    // GOOD: Proper encapsulation — validation + controlled access
    // ============================================================
    static class BankAccount {
        private double balance;
        private final String accountNumber;
        private final List<String> transactionHistory;

        public BankAccount(String accountNumber, double initialBalance) {
            if (accountNumber == null || accountNumber.isBlank()) {
                throw new IllegalArgumentException("Account number is required");
            }
            if (initialBalance < 0) {
                throw new IllegalArgumentException("Initial balance cannot be negative");
            }
            this.accountNumber = accountNumber;
            this.balance = initialBalance;
            this.transactionHistory = new ArrayList<>();
            this.transactionHistory.add("OPENING BALANCE: " + initialBalance);
        }

        public double getBalance() {
            return balance;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        // DEFENSIVE COPY — caller can't mutate our internal list
        public List<String> getTransactionHistory() {
            return Collections.unmodifiableList(transactionHistory);
        }

        public void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Deposit amount must be positive, got: " + amount);
            }
            balance += amount;
            transactionHistory.add("DEPOSIT: " + amount);
        }

        public boolean withdraw(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Withdrawal amount must be positive, got: " + amount);
            }
            if (amount > balance) {
                return false; // insufficient funds
            }
            balance -= amount;
            transactionHistory.add("WITHDRAWAL: " + amount);
            return true;
        }

        // NO setter for balance — you can't set it directly
        // NO setter for accountNumber — it's final
        // NO setter for transactionHistory — it's managed internally
    }

    // ============================================================
    // BAD: Anti-pattern — private fields with mindless getters/setters
    // This is data hiding WITHOUT real encapsulation
    // ============================================================
    static class BadBankAccount {
        private double balance;

        public double getBalance() { return balance; }

        // This is basically a public field with extra steps
        public void setBalance(double balance) { this.balance = balance; }
        // Anyone can do: account.setBalance(-99999) — no validation, no audit trail
    }

    // ============================================================
    // Demo
    // ============================================================
    public static void main(String[] args) {
        // Good encapsulation
        BankAccount account = new BankAccount("ACC001", 1000.0);
        account.deposit(500.0);
        account.withdraw(200.0);

        System.out.println("Balance: " + account.getBalance()); // 1300.0
        System.out.println("History: " + account.getTransactionHistory());

        // This would throw — can't mutate internal state
        try {
            account.getTransactionHistory().add("HACKED");
        } catch (UnsupportedOperationException e) {
            System.out.println("Can't mutate transaction history from outside!");
        }

        // This would throw — validation catches it
        try {
            account.deposit(-100);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        // Constructor validation
        try {
            new BankAccount("", 1000);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }
}
