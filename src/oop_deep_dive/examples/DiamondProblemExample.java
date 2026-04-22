package oop_deep_dive.examples;

/**
 * Diamond Problem — The conflict, resolution, and the REAL problem.
 */
public class DiamondProblemExample {

    // ============================================================
    // THE CONFLICT: Two interfaces with same default method
    // ============================================================
    interface Loggable {
        default String format() {
            return "LOG: " + this.toString();
        }
    }

    interface Auditable {
        default String format() {
            return "AUDIT: " + this.toString();
        }
    }

    // ============================================================
    // SOLUTION 1: Pick one explicitly
    // ============================================================
    static class TransactionV1 implements Loggable, Auditable {
        private final String id;
        TransactionV1(String id) { this.id = id; }

        @Override
        public String format() {
            return Loggable.super.format();  // explicitly choose Loggable
        }

        @Override
        public String toString() { return "Transaction(" + id + ")"; }
    }

    // ============================================================
    // SOLUTION 2: Your own implementation
    // ============================================================
    static class TransactionV2 implements Loggable, Auditable {
        private final String id;
        private final double amount;
        TransactionV2(String id, double amount) { this.id = id; this.amount = amount; }

        @Override
        public String format() {
            return "TXN:" + id + "|" + amount;  // ignore both defaults
        }

        @Override
        public String toString() { return "Transaction(" + id + ")"; }
    }

    // ============================================================
    // SOLUTION 3: Combine both
    // ============================================================
    static class TransactionV3 implements Loggable, Auditable {
        private final String id;
        TransactionV3(String id) { this.id = id; }

        @Override
        public String format() {
            return Loggable.super.format() + " | " + Auditable.super.format();
        }

        @Override
        public String toString() { return "Transaction(" + id + ")"; }
    }

    // ============================================================
    // RULE 2 DEMO: Subinterface wins over parent interface
    // ============================================================
    interface Formattable {
        default String display() { return "base format"; }
    }

    interface AuditFormattable extends Formattable {
        default String display() { return "audit format"; }  // more specific wins
    }

    static class AutoResolved implements Formattable, AuditFormattable {
        // No error — AuditFormattable.display() wins automatically
    }

    // ============================================================
    // THE REAL DIAMOND PROBLEM: Semantic conflicts
    // (This compiles fine but is logically broken)
    // ============================================================
    interface Cache {
        void clear();  // means: remove cached entries
    }

    interface TransactionBuffer {
        void clear();  // means: flush pending transactions to DB
    }

    // BAD — What does clear() mean here?
    static class TransactionCacheBad implements Cache, TransactionBuffer {
        @Override
        public void clear() {
            // Delete cache? Flush to DB? Both?
            System.out.println("[BAD] Unclear what clear() should do here");
        }
    }

    // FIX — Use composition to separate the semantics
    static class CacheImpl implements Cache {
        @Override
        public void clear() { System.out.println("[CACHE] Cleared cached entries"); }
    }

    static class BufferImpl implements TransactionBuffer {
        @Override
        public void clear() { System.out.println("[BUFFER] Flushed transactions to DB"); }
    }

    static class TransactionProcessorFixed {
        private final Cache cache;
        private final TransactionBuffer buffer;

        TransactionProcessorFixed(Cache cache, TransactionBuffer buffer) {
            this.cache = cache;
            this.buffer = buffer;
        }

        public void clearCache() { cache.clear(); }
        public void flushTransactions() { buffer.clear(); }
    }

    // ============================================================
    // Demo
    // ============================================================
    public static void main(String[] args) {
        System.out.println("=== DIAMOND PROBLEM SOLUTIONS ===");
        System.out.println();

        System.out.println("Solution 1 — Pick Loggable:");
        System.out.println("  " + new TransactionV1("TXN001").format());

        System.out.println("Solution 2 — Own implementation:");
        System.out.println("  " + new TransactionV2("TXN002", 1500.0).format());

        System.out.println("Solution 3 — Combine both:");
        System.out.println("  " + new TransactionV3("TXN003").format());

        System.out.println();
        System.out.println("Rule 2 — Subinterface wins:");
        System.out.println("  " + new AutoResolved().display());

        System.out.println();
        System.out.println("=== THE REAL PROBLEM: SEMANTIC CONFLICTS ===");
        System.out.println();

        System.out.println("BAD — single class, ambiguous clear():");
        new TransactionCacheBad().clear();

        System.out.println();
        System.out.println("FIXED — composition, clear semantics:");
        TransactionProcessorFixed fixed = new TransactionProcessorFixed(
                new CacheImpl(), new BufferImpl());
        fixed.clearCache();
        fixed.flushTransactions();
    }
}
