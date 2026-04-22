package oop_deep_dive.examples;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interfaces + Composition — the pattern that replaces abstract classes
 * in 90% of application code.
 *
 * Shows: multiple contracts, reusable behavior via composition,
 * and the interface + skeletal implementation pattern.
 */
public class InterfaceCompositionExample {

    // ============================================================
    // CONTRACTS — each class picks what it fulfills
    // ============================================================
    interface Cacheable {
        String cacheKey();
        Duration ttl();
    }

    interface Auditable {
        String auditLog();
        String modifiedBy();
    }

    interface Reportable {
        String toReportRow();
    }

    // ============================================================
    // REUSABLE SERVICES (composition, not inheritance)
    // ============================================================
    static class CacheManager {
        private final Map<String, Object> cache = new ConcurrentHashMap<>();

        public void cache(Cacheable item) {
            cache.put(item.cacheKey(), item);
            System.out.println("[CACHE] Stored: " + item.cacheKey() + " (ttl: " + item.ttl() + ")");
        }

        public Object get(String key) {
            return cache.get(key);
        }
    }

    static class AuditLogger {
        public void log(Auditable item) {
            System.out.println("[AUDIT] " + item.auditLog() + " by " + item.modifiedBy());
        }
    }

    // ============================================================
    // ORDER — implements MULTIPLE contracts (impossible with abstract class)
    // ============================================================
    static class Order implements Cacheable, Auditable, Reportable {
        private final String orderId;
        private final double amount;
        private final String lastEditor;

        // Composed services — injected, not inherited
        private final CacheManager cacheManager;
        private final AuditLogger auditLogger;

        public Order(String orderId, double amount, String lastEditor,
                     CacheManager cacheManager, AuditLogger auditLogger) {
            this.orderId = orderId;
            this.amount = amount;
            this.lastEditor = lastEditor;
            this.cacheManager = cacheManager;
            this.auditLogger = auditLogger;
        }

        // --- Cacheable ---
        @Override
        public String cacheKey() { return "order:" + orderId; }

        @Override
        public Duration ttl() { return Duration.ofMinutes(30); }

        // --- Auditable ---
        @Override
        public String auditLog() { return "Order " + orderId + " amount=" + amount; }

        @Override
        public String modifiedBy() { return lastEditor; }

        // --- Reportable ---
        @Override
        public String toReportRow() { return orderId + "," + amount + "," + lastEditor; }

        // Business method that uses composed services
        public void save() {
            cacheManager.cache(this);
            auditLogger.log(this);
            System.out.println("[ORDER] Saved: " + orderId);
        }
    }

    // ============================================================
    // PAYMENT — implements only Auditable + Reportable (not Cacheable)
    // Each class picks exactly what it needs — no forced empty methods
    // ============================================================
    static class Payment implements Auditable, Reportable {
        private final String paymentId;
        private final double amount;
        private final String processor;

        private final AuditLogger auditLogger;

        public Payment(String paymentId, double amount, String processor, AuditLogger auditLogger) {
            this.paymentId = paymentId;
            this.amount = amount;
            this.processor = processor;
            this.auditLogger = auditLogger;
        }

        @Override
        public String auditLog() { return "Payment " + paymentId + " via " + processor; }

        @Override
        public String modifiedBy() { return "system"; }

        @Override
        public String toReportRow() { return paymentId + "," + amount + "," + processor; }

        public void process() {
            auditLogger.log(this);
            System.out.println("[PAYMENT] Processed: " + paymentId);
        }
    }

    // ============================================================
    // Demo
    // ============================================================
    public static void main(String[] args) {
        CacheManager cacheManager = new CacheManager();
        AuditLogger auditLogger = new AuditLogger();

        // Order uses all 3 contracts
        Order order = new Order("ORD-001", 2500.0, "abhishek", cacheManager, auditLogger);
        order.save();

        System.out.println();

        // Payment uses only 2 contracts — no Cacheable, no forced empty methods
        Payment payment = new Payment("PAY-001", 2500.0, "razorpay", auditLogger);
        payment.process();

        System.out.println();

        // Polymorphism still works — any Auditable can be audited
        System.out.println("=== Auditing all items ===");
        for (Auditable item : new Auditable[]{order, payment}) {
            auditLogger.log(item);
        }
    }
}
