# Liskov Substitution Principle (LSP) — Deep Dive

## The Simple Version

If your code works with a parent type, it should work **exactly the same** with any child type. No surprises, no breaking, no "oh but this subclass behaves differently."

```java
// If this works:
PaymentGateway gw = new RazorpayGateway();
gw.charge(request);

// Then this MUST also work, with zero code changes:
PaymentGateway gw = new StripeGateway();
gw.charge(request);
```

If swapping causes a crash, unexpected exception, or wrong behavior — you've violated LSP.

---

## The Classic Violation — Rectangle/Square

```java
public class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int w) { this.width = w; }
    public void setHeight(int h) { this.height = h; }
    public int area() { return width * height; }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int w) {
        this.width = w;
        this.height = w;  // must keep sides equal
    }

    @Override
    public void setHeight(int h) {
        this.width = h;
        this.height = h;
    }
}
```

**This test passes for Rectangle but BREAKS for Square:**

```java
public void resize(Rectangle r) {
    r.setWidth(5);
    r.setHeight(10);
    assert r.area() == 50;  // FAILS for Square — area is 100
}
```

The caller had a reasonable expectation: setting width and height independently. Square broke that contract silently.

---

## Real Fintech Violation — This Actually Happens

```java
public interface PaymentGateway {
    PaymentResponse charge(PaymentRequest req);
}

public class RazorpayGateway implements PaymentGateway {
    public PaymentResponse charge(PaymentRequest req) {
        return callRazorpayAPI(req);  // works for any amount
    }
}

public class WalletGateway implements PaymentGateway {
    public PaymentResponse charge(PaymentRequest req) {
        if (req.getAmount() > 10000) {
            throw new UnsupportedOperationException("Wallet limit is 10k");
            // LSP VIOLATION — caller never expected this
        }
        return deductFromWallet(req);
    }
}
```

**Why this is broken:**

```java
public class CheckoutService {
    private final PaymentGateway gateway;

    public void checkout(Order order) {
        gateway.charge(order.toPaymentRequest());
        // works with Razorpay, EXPLODES with Wallet for orders > 10k
    }
}
```

**The fix:**

```java
// Option 1: Make the limitation part of the contract
public interface PaymentGateway {
    PaymentResponse charge(PaymentRequest req);
    boolean supports(PaymentRequest req);  // caller checks first
}

// Option 2: Separate interface — wallet is NOT a full payment gateway
public interface WalletService {
    PaymentResponse charge(PaymentRequest req) throws LimitExceededException;
    double getLimit();
}
```

---

## LSP Has 3 Rules

### Rule 1: Don't Strengthen Preconditions

Parent accepts any positive amount -> Child cannot suddenly reject amounts > 10k.

```java
// Parent: any amount > 0 is fine
public class PaymentGateway {
    public void charge(double amount) {
        if (amount <= 0) throw new IllegalArgumentException();
    }
}

// BAD — child adds new restriction
public class WalletGateway extends PaymentGateway {
    public void charge(double amount) {
        if (amount <= 0) throw new IllegalArgumentException();
        if (amount > 10000) throw new LimitException();  // STRONGER precondition
    }
}
```

### Rule 2: Don't Weaken Postconditions

Parent guarantees a non-null response -> Child cannot return null.

```java
// Parent guarantees: always returns a valid PaymentResponse
public class PaymentGateway {
    public PaymentResponse charge(PaymentRequest req) {
        return new PaymentResponse(Status.SUCCESS, txnId);
    }
}

// BAD — child returns null sometimes
public class TestGateway extends PaymentGateway {
    public PaymentResponse charge(PaymentRequest req) {
        return null;  // NullPointerException downstream
    }
}
```

### Rule 3: Don't Violate Invariants

Parent guarantees balance is never negative -> Child cannot allow it.

```java
public class BankAccount {
    private double balance;

    public void withdraw(double amount) {
        if (amount > balance) throw new InsufficientFundsException();
        balance -= amount;
        // INVARIANT: balance >= 0, always
    }
}

// BAD — child breaks the invariant
public class OverdraftAccount extends BankAccount {
    public void withdraw(double amount) {
        balance -= amount;  // allows negative balance
    }
}
```

---

## How to Detect LSP Violations in Your Code

### Red Flags:

```java
// 1. instanceof checks — biggest code smell
public void process(PaymentGateway gw) {
    if (gw instanceof WalletGateway) {
        // special handling — WHY?
    }
}

// 2. Catching specific exceptions from specific subtypes
try {
    gateway.charge(req);
} catch (UnsupportedOperationException e) {
    // only WalletGateway throws this
}

// 3. Subclass throwing UnsupportedOperationException
public class ReadOnlyAccount extends BankAccount {
    public void withdraw(double amount) {
        throw new UnsupportedOperationException();
        // then it shouldn't BE a BankAccount
    }
}
```

### The Test

Take any method that accepts the parent type. Pass every child type. If you need `instanceof`, special `try-catch`, or null checks for specific children — LSP is violated.

---

## The One Sentence

> **If you need to know which subclass it is to use it correctly, your abstraction is wrong.**

The calling code should be blissfully ignorant of which implementation it's talking to.
