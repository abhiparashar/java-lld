# Encapsulation — Deep Dive (Interview-Ready)

## What Is Encapsulation?

**Bundling data AND the behavior that operates on that data into a single unit (class), and exposing a controlled interface.**

It's NOT just making fields private. That's data hiding — a subset of encapsulation.

---

## The BankAccount Example

```java
public class BankAccount {
    private double balance;
    private String accountNumber;

    public BankAccount(String accountNumber, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number is required");
        }
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
```

**What encapsulation gives here:**
- `balance` is private — callers can't set it to -1000
- `deposit()` bundles validation WITH the mutation — you can't add money without passing the check
- If tomorrow we change `balance` from `double` to `BigDecimal`, no caller breaks

---

## Interview Question 1: How Would You Handle Validation in Setters?

### Weak Answer (5/10):
> "I'll validate in the constructor and use Lombok @Value."

### Strong Answer (9/10):
> "Validate in the constructor first — fail fast at object creation. Throw `IllegalArgumentException` with a clear message for invalid arguments. If setters are needed, each setter should enforce its own invariants: null checks, range checks, format checks. The object should never be in an invalid state between method calls.
>
> For mutable reference types like `List` or `Date`, I'd use defensive copies to prevent the caller from mutating internal state.
>
> Where possible, I'd prefer immutability to avoid setters altogether — `final` fields, no setters, return new instances for changes."

### Key Points to Hit:
1. **Constructor validation** — fail fast with `IllegalArgumentException`
2. **Each setter enforces invariants** — null, range, format
3. **Defensive copies** for mutable reference types
4. **Prefer immutability** when possible
5. **Clear exception messages** — not just "invalid"

---

## Interview Question 2: Encapsulation vs Data Hiding

### Weak Answer (4/10):
> "Data hiding is part of encapsulation. Encapsulation gives flexibility."

### Strong Answer (9/10):
> "**Data hiding** is restricting direct access to internal fields using access modifiers (`private`, `protected`). It's about *visibility*.
>
> **Encapsulation** is bundling data AND the behavior that operates on that data into a single unit, exposing a controlled interface. It's about *bundling + controlled access*.
>
> Example from BankAccount: Data hiding is `balance` being `private`. Encapsulation is `deposit()` and `withdraw()` bundling validation logic with the data. The caller doesn't know we check `amount > 0` — they just call the method. If tomorrow I add logging or audit trails inside `withdraw()`, no caller changes.
>
> You can have data hiding without real encapsulation — private fields with mindless getters/setters that expose everything. That's an anti-pattern — essentially public fields with extra steps."

### The Key Distinction:
- **Data hiding** = what you **can't see** (access control)
- **Encapsulation** = what you **interact with** (interface over implementation)

---

## Interview Question 3: How Does Encapsulation Support the Open-Closed Principle?

### Weak Answer (3/10):
> "It discourages callers from mutating fields."

### Strong Answer (9/10):
> "Encapsulation gives classes a stable public contract while hiding internals. This means I can extend behavior — through inheritance or composition — without modifying the existing class.
>
> For example, because `balance` is encapsulated behind `getBalance()`, any new account type can extend `BankAccount` without modifying it:
>
> ```java
> public class SavingsAccount extends BankAccount {
>     public double calculateInterest() {
>         return getBalance() * 0.04;  // uses the interface, not the field
>     }
> }
> ```
>
> If `balance` were `public`, subclasses and callers would depend directly on that field. Changing it from `double` to `BigDecimal` would break every class that touches it — that violates 'closed for modification.'
>
> Encapsulation is what makes 'closed for modification' possible."

---

## Answer Template for Any Encapsulation Question

1. **Define** the concept in one sentence
2. **Example** — show it in code or relate to the given problem
3. **Consequence** — what breaks if you don't do this?

---

## Mock Interview Scoring

| Question | First Attempt | Target |
|---|---|---|
| Validation in setters | 6/10 | 9/10 |
| Encapsulation vs Data hiding | 5/10 | 9/10 |
| Encapsulation & OCP | 3/10 | 9/10 |

**Key Improvement Areas:**
- Lead with crisp definitions, not vague keywords
- Always include a concrete code example
- Explain the consequence — "what breaks without this?"
- Never say "flexibility" or "maintainability" without explaining HOW
