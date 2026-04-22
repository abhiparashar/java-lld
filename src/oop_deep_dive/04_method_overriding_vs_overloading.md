# Method Overriding vs Overloading — Deep Dive

## Overloading — Same Name, Different Parameters (Compile-Time)

The **compiler** picks which method to call based on argument types. No polymorphism involved.

```java
public class FeeCalculator {

    // domestic transfer — flat fee
    public double calculate(double amount) {
        return 5.0;
    }

    // international transfer — needs currency
    public double calculate(double amount, String currency) {
        return amount * getForexRate(currency) * 0.02;
    }

    // bulk transfers — list of amounts
    public double calculate(List<Double> amounts) {
        return amounts.stream().mapToDouble(this::calculate).sum();
    }
}
```

---

## Overriding — Same Signature, Different Class (Runtime)

The **JVM** decides which method to call at runtime based on the actual object type.

```java
public interface PaymentGateway {
    PaymentResponse charge(PaymentRequest req);
}

public class RazorpayGateway implements PaymentGateway {
    @Override
    public PaymentResponse charge(PaymentRequest req) {
        // Razorpay API call
    }
}

public class StripeGateway implements PaymentGateway {
    @Override
    public PaymentResponse charge(PaymentRequest req) {
        // Stripe API call
    }
}

// At runtime, JVM decides which charge() to call
PaymentGateway gw = gatewayFactory.getGateway(merchant);
gw.charge(request);  // Razorpay or Stripe? Decided at RUNTIME.
```

---

## The Trap Nobody Tells You About

```java
public class TransactionLogger {
    public void log(Object obj) {
        System.out.println("Object: " + obj);
    }

    public void log(Transaction txn) {
        System.out.println("Transaction: " + txn.getId());
    }
}

// Quiz: what does this print?
Object t = new Transaction("TXN001");
logger.log(t);
```

**Answer: `"Object: ..."`**

Overloading is resolved by the **compile-time type** of the variable, which is `Object`, not `Transaction`. This is a real bug that shows up in production logging.

**The fix:** Use `instanceof` inside the method, or redesign to use overriding instead of overloading.

---

## Quick Reference Table

| | Overloading | Overriding |
|---|---|---|
| **Where** | Same class | Parent -> Child |
| **What changes** | Parameter types/count | Implementation body |
| **When resolved** | Compile time | Runtime |
| **Annotation** | None | `@Override` |
| **Return type** | Can differ | Must be same or covariant |
| **Access modifier** | Can differ | Can't be MORE restrictive |
| **static methods** | Can overload | Cannot override (method hiding) |
| **private methods** | Can overload | Cannot override |
| **final methods** | Can overload | Cannot override |

---

## Key Takeaway

- **Overloading** = convenience (multiple ways to call similar logic)
- **Overriding** = polymorphism (different behavior for different types)
- **Watch out:** Overloading resolves on **declared type**, not actual type. This is the source of subtle bugs.
