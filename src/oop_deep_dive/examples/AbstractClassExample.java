package oop_deep_dive.examples;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Classes in practice — Template Method Pattern
 *
 * Real fintech use case: Reconciliation jobs where the workflow
 * is locked down (compliance) but data sources vary.
 */
public class AbstractClassExample {

    // ============================================================
    // Simulated domain objects
    // ============================================================
    record Transaction(String utr, double amount, LocalDate date, String source) {}

    record ReconResult(List<Transaction> matched, List<Transaction> mismatched) {
        boolean hasMismatches() { return !mismatched.isEmpty(); }
        String summary() { return mismatched.size() + " mismatches found"; }
    }

    // ============================================================
    // ABSTRACT CLASS — Template Method Pattern
    // The workflow is LOCKED DOWN with `final`. Nobody skips audit/alert.
    // ============================================================
    static abstract class ReconciliationJob {

        // FINAL — subclasses CANNOT override this. Compliance guaranteed.
        public final ReconResult run(LocalDate date) {
            System.out.println("[RECON] Starting reconciliation for " + date);

            List<Transaction> source = fetchSourceTransactions(date);
            List<Transaction> bank = fetchBankStatements(date);
            ReconResult result = match(source, bank);

            // These steps can NEVER be skipped — that's why abstract class wins here
            saveToDatabase(result);
            if (result.hasMismatches()) {
                sendAlert(result.summary());
            }

            System.out.println("[RECON] Completed. " + result.summary());
            return result;
        }

        // Subclasses implement ONLY these — their specific data source
        protected abstract List<Transaction> fetchSourceTransactions(LocalDate date);
        protected abstract List<Transaction> fetchBankStatements(LocalDate date);

        // Default matching logic — most jobs use this, some override
        protected ReconResult match(List<Transaction> source, List<Transaction> bank) {
            List<Transaction> matched = new ArrayList<>();
            List<Transaction> mismatched = new ArrayList<>();

            for (Transaction s : source) {
                boolean found = bank.stream()
                        .anyMatch(b -> b.utr().equals(s.utr()) && b.amount() == s.amount());
                if (found) {
                    matched.add(s);
                } else {
                    mismatched.add(s);
                }
            }
            return new ReconResult(matched, mismatched);
        }

        // Shared infrastructure — same for all jobs
        private void saveToDatabase(ReconResult result) {
            System.out.println("[DB] Saving recon result: " + result.matched().size() + " matched, "
                    + result.mismatched().size() + " mismatched");
        }

        private void sendAlert(String message) {
            System.out.println("[ALERT] " + message);
        }
    }

    // ============================================================
    // CONCRETE: UPI Reconciliation — fetches from internal DB + NPCI file
    // ============================================================
    static class UPIReconciliationJob extends ReconciliationJob {

        @Override
        protected List<Transaction> fetchSourceTransactions(LocalDate date) {
            System.out.println("[UPI] Fetching internal UPI transactions...");
            return List.of(
                    new Transaction("UTR001", 500.0, date, "internal"),
                    new Transaction("UTR002", 1200.0, date, "internal"),
                    new Transaction("UTR003", 300.0, date, "internal")
            );
        }

        @Override
        protected List<Transaction> fetchBankStatements(LocalDate date) {
            System.out.println("[UPI] Parsing NPCI settlement file...");
            return List.of(
                    new Transaction("UTR001", 500.0, date, "npci"),
                    new Transaction("UTR002", 1200.0, date, "npci")
                    // UTR003 missing — will be flagged as mismatch
            );
        }
    }

    // ============================================================
    // CONCRETE: Card Reconciliation — fetches from internal DB + Visa file
    // ============================================================
    static class CardReconciliationJob extends ReconciliationJob {

        @Override
        protected List<Transaction> fetchSourceTransactions(LocalDate date) {
            System.out.println("[CARD] Fetching internal card transactions...");
            return List.of(
                    new Transaction("CARD001", 2500.0, date, "internal"),
                    new Transaction("CARD002", 800.0, date, "internal")
            );
        }

        @Override
        protected List<Transaction> fetchBankStatements(LocalDate date) {
            System.out.println("[CARD] Parsing Visa settlement file...");
            return List.of(
                    new Transaction("CARD001", 2500.0, date, "visa"),
                    new Transaction("CARD002", 800.0, date, "visa")
            );
        }
    }

    // ============================================================
    // Demo
    // ============================================================
    public static void main(String[] args) {
        LocalDate today = LocalDate.now();

        System.out.println("=== UPI RECONCILIATION ===");
        ReconciliationJob upiRecon = new UPIReconciliationJob();
        upiRecon.run(today);

        System.out.println();

        System.out.println("=== CARD RECONCILIATION ===");
        ReconciliationJob cardRecon = new CardReconciliationJob();
        cardRecon.run(today);

        // Key point: Both jobs follow the EXACT same workflow.
        // Neither can skip saveToDatabase() or sendAlert().
        // But each fetches data from its own source.
    }
}
