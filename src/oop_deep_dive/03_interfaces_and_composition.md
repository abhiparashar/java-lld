# Interfaces, Multiple Inheritance & Composition — Deep Dive

## 1. Multiple Inheritance via Interfaces

Java says: **one parent class, multiple interfaces.** That's the rule.

### Why Java Banned Multiple Class Inheritance

```java
// C++ allows this. Java doesn't.
class Scanner { public void scan() { } }
class Printer { public void print() { } }
class AllInOne extends Scanner, Printer { } // ILLEGAL in Java
```

### What Java Gives You Instead

```java
public interface Scannable { void scan(); }
public interface Printable { void print(); }
public interface Faxable { void fax(); }

public class AllInOneMachine implements Scannable, Printable, Faxable {
    public void scan() { /* ... */ }
    public void print() { /* ... */ }
    public void fax() { /* ... */ }
}
```

### Real Fintech Example — Multiple Compliance Contracts

```java
public interface Auditable {
    AuditLog toAuditLog();
}

public interface Reportable {
    ReportRow toRBIReport();
}

public interface Reversible {
    Transaction reverse();
    boolean isReversed();
}

// UPI payment must be all three
public class UPITransaction implements Auditable, Reportable, Reversible {
    public AuditLog toAuditLog() { /* ... */ }
    public ReportRow toRBIReport() { /* ... */ }
    public Transaction reverse() { /* ... */ }
    public boolean isReversed() { /* ... */ }
}

// Fixed deposit is auditable and reportable, but NOT reversible
public class FixedDeposit implements Auditable, Reportable {
    public AuditLog toAuditLog() { /* ... */ }
    public ReportRow toRBIReport() { /* ... */ }
}
```

Each class picks exactly the contracts it fulfills. No more, no less.

---

## 2. "Interfaces with Composition" — What It Actually Means

### The Problem — You Need Multiple Behaviors

```java
// With abstract classes, you're stuck — Java allows only ONE parent
public class Order extends Cacheable {  // can't also extend Auditable
}
```

### The Solution

```java
// Define contracts
public interface Cacheable {
    String cacheKey();
    Duration ttl();
}

public interface Auditable {
    String auditLog();
    String modifiedBy();
}

// Reusable implementations (composition, not inheritance)
public class CacheManager {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public void cache(Cacheable item) {
        cache.put(item.cacheKey(), item);
        scheduleEviction(item.cacheKey(), item.ttl());
    }
}

public class AuditLogger {
    public void log(Auditable item) {
        auditStore.save(item.auditLog(), item.modifiedBy());
    }
}

// Now Order can be BOTH — impossible with abstract classes
public class Order implements Cacheable, Auditable {
    private final CacheManager cacheManager;   // composition
    private final AuditLogger auditLogger;     // composition

    public String cacheKey() { return "order:" + id; }
    public Duration ttl() { return Duration.ofMinutes(30); }
    public String auditLog() { return "Order " + id + " modified"; }
    public String modifiedBy() { return lastEditor; }

    public void save() {
        cacheManager.cache(this);
        auditLogger.log(this);
    }
}
```

**Why this is better than abstract classes here:**
- `Order` fulfills **multiple contracts** (Cacheable + Auditable)
- Behavior lives in separate, testable, reusable objects
- Adding `Searchable` doesn't require changing the inheritance tree

---

## 3. Interface + Skeletal Implementation (Effective Java Pattern)

The most powerful pattern: **interface for the type system, abstract class for shared plumbing.**

```java
// Interface defines the contract
public interface PaymentGateway {
    PaymentResponse charge(PaymentRequest req);
    PaymentResponse refund(String txnId);
    PaymentStatus status(String txnId);
}

// Abstract class provides shared infrastructure
public abstract class AbstractPaymentGateway implements PaymentGateway {

    private final RestTemplate http;
    private final RetryTemplate retry;
    private final MeterRegistry metrics;

    // Shared retry + metrics wrapper
    protected <T> T callWithRetry(String operation, Supplier<T> call) {
        return retry.execute(ctx -> {
            Timer.Sample sample = Timer.start(metrics);
            try {
                T result = call.get();
                sample.stop(metrics.timer("gateway." + operation, "status", "success"));
                return result;
            } catch (Exception e) {
                sample.stop(metrics.timer("gateway." + operation, "status", "failure"));
                throw e;
            }
        });
    }
}

// Concrete — only payment-specific logic
public class RazorpayGateway extends AbstractPaymentGateway {

    public PaymentResponse charge(PaymentRequest req) {
        return callWithRetry("charge", () -> {
            // Razorpay-specific API call — 5 lines, not 30
        });
    }
}
```

---

## 4. Abstract Class vs Interface — Decision Flowchart

```
Do subclasses share state (fields)?
|-- NO --> Interface
|-- YES --> Do you need to lock down a workflow with final methods?
            |-- YES --> Abstract class
            |-- NO --> Can you extract shared state into a helper and inject it?
                       |-- YES --> Interface + composition
                       |-- NO --> Abstract class
```

### Quick Reference

| Scenario | Use Abstract Class | Use Interface |
|---|---|---|
| Shared state (fields) | Yes | No |
| Shared method implementation | Yes | Limited (default methods) |
| Constructor logic needed | Yes | No |
| "is-a" relationship | Yes | No |
| Multiple inheritance needed | No (single only) | Yes |
| Unrelated classes share behavior | No | Yes |
| Pure contract, no logic | Overkill | Yes |

### The Rule

> Abstract class = "is-a" with shared state (Payment -> UPIPayment)
> Interface + composition = "can-do" with multiple capabilities (Order is Cacheable AND Auditable)
