# Abstract Classes — Deep Dive (Principal Engineer Level)

## What Is an Abstract Class?

A class that **cannot be instantiated** on its own. It exists to provide a **common foundation** for subclasses. It says: "Here's what every child must do, and here's some shared behavior for free."

```java
public abstract class Payment {
    // You CANNOT do: new Payment() — compile error
}
```

---

## The Two Things an Abstract Class Gives You

### 1. Abstract Methods — the contract (MUST override)
```java
public abstract double calculateFee();
```
No body. Every subclass is forced to implement this.

### 2. Concrete Methods — shared behavior (CAN override)
```java
public void printReceipt() {
    System.out.println("Amount: " + getAmount());
    System.out.println("Fee: " + calculateFee());
}
```
Shared logic that subclasses inherit for free.

---

## Real Production Use Cases (Not Textbook Examples)

### 1. JDK's `AbstractList` — The Gold Standard

When you write `new ArrayList<>()`, it extends `AbstractList`.

```java
// From OpenJDK source
public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {

    // THE ONLY THING YOU MUST IMPLEMENT
    abstract public E get(int index);

    // 20+ methods built ON TOP of get() for free
    public int indexOf(Object o) {
        ListIterator<E> it = listIterator();
        while (it.hasNext()) {
            if (o.equals(it.next())) return it.previousIndex();
        }
        return -1;
    }

    public Iterator<E> iterator() {
        return new Itr();  // built using get()
    }

    // Mutators — throw by default, override to enable
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }
}
```

**Why this matters:**
- `ArrayList` implements `get()` using an array. Gets `indexOf`, `iterator`, `contains`, `subList` — all for free.
- This is called the **"skeletal implementation" pattern** — Joshua Bloch documented it in Effective Java.

### 2. Google Guava's `ImmutableCollection` — Immutability via Abstract Class

```java
// From Guava source
public abstract class ImmutableCollection<E> extends AbstractCollection<E> {

    // ALL mutation methods are final and throw
    @Deprecated
    @DoNotCall
    public final boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall
    public final boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @DoNotCall
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    // Package-private constructor — YOU cannot extend this
    ImmutableCollection() {}
}
```

**What Guava does here that Lombok `@Value` can't:**
- `final` on every mutation method — subclasses CAN'T accidentally re-enable mutation
- Package-private constructor — only Guava's own `ImmutableList`, `ImmutableSet` can extend
- Custom serialization — prevents deserialization attacks

### 3. Spring's Template Method Pattern

```java
// From Spring JDBC source
public abstract class AbstractLobCreatingPreparedStatementCallback
        implements PreparedStatementCallback<Integer> {

    private final LobHandler lobHandler;  // shared state

    // Concrete — handles lifecycle (open LOB -> delegate -> execute -> cleanup)
    public Integer doInPreparedStatement(PreparedStatement ps) {
        LobCreator lobCreator = this.lobHandler.getLobCreator();
        try {
            setValues(ps, lobCreator);    // YOUR code goes here
            return ps.executeUpdate();
        } finally {
            lobCreator.close();           // cleanup guaranteed
        }
    }

    // You implement ONLY this
    protected abstract void setValues(PreparedStatement ps, LobCreator lobCreator);
}
```

The resource lifecycle (create -> use -> close) is **locked down**. You literally cannot forget to close the `LobCreator`.

---

## The Honest Truth: Where Abstract Classes Live

### In Libraries (JDK, Guava, Spring) — Everywhere

| Where | Abstract Class | Why |
|---|---|---|
| JDK `AbstractList` | Skeletal implementation — 20+ methods from `get()` | Needs shared iterator state, `modCount` field |
| Guava `ImmutableCollection` | Locks mutation with `final` methods | Needs package-private constructor |
| Spring JDBC callbacks | Template method — lifecycle management | Needs constructor injection |
| `InputStream`/`OutputStream` | `read(byte[])` built on `read()` | Needs shared buffering state |

### In Your Fintech Microservice — Rare (Maybe 2-3 in Entire Codebase)

Your typical Spring Boot service looks like:
```java
@Service
public class PaymentService {
    private final PaymentGateway gateway;
    private final TransactionRepository repo;
    // ... interfaces + Spring DI + composition
}
```

**No abstract classes.** And that's correct design.

| Code Type | Abstract Classes? | What You Use Instead |
|---|---|---|
| Libraries (JDK, Guava, Spring) | Everywhere | — |
| Your fintech microservice | Rare | Interfaces + Spring DI + composition |
| Reconciliation / Batch jobs | Yes — template method | — |
| REST controllers, services | Almost never | Interface + `@Service` |
| DTOs, entities | Never | Records, `@Value` |

---

## The 10% Where Abstract Classes Win in Application Code

### 1. Reconciliation Jobs (Compliance-Critical Workflow)

```java
public abstract class ReconciliationJob {

    private final AlertService alertService;
    private final ReconRepository reconRepo;

    // Locked-down flow — nobody skips the audit step
    public final ReconResult run(LocalDate date) {
        List<Transaction> source = fetchSourceTransactions(date);
        List<Transaction> bank = fetchBankStatements(date);
        ReconResult result = match(source, bank);
        reconRepo.save(result);
        if (result.hasMismatches()) {
            alertService.alert("Recon mismatch: " + result.summary());
        }
        return result;
    }

    protected abstract List<Transaction> fetchSourceTransactions(LocalDate date);
    protected abstract List<Transaction> fetchBankStatements(LocalDate date);

    protected ReconResult match(List<Transaction> source, List<Transaction> bank) {
        // default matching by UTR number, amount, date
    }
}
```

**Why abstract class:** The alert-on-mismatch and save-to-db are compliance requirements. If a developer forgets them, RBI audit fails. The `final` method guarantees it.

### 2. Multi-Tenant Loan Underwriting

```java
public abstract class LoanUnderwriter {

    private final BureauService bureau;
    private final FraudCheckService fraud;

    public final UnderwritingDecision evaluate(LoanApplication app) {
        CreditReport report = bureau.pull(app.getPan());
        if (fraud.isFlagged(app)) return UnderwritingDecision.REJECT;
        int score = calculateRiskScore(app, report);
        return score >= threshold()
            ? UnderwritingDecision.APPROVE
            : UnderwritingDecision.REJECT;
    }

    protected abstract int calculateRiskScore(LoanApplication app, CreditReport report);
    protected abstract int threshold();
}
```

### 3. Regulatory Report Generation

```java
public abstract class RegulatoryReport {

    public final void generate(LocalDate date) {
        String header = buildHeader(date);
        List<String> rows = buildRows(date);
        String checksum = computeChecksum(rows);
        upload(header, rows, checksum);
    }

    protected abstract String buildHeader(LocalDate date);
    protected abstract List<String> buildRows(LocalDate date);

    private String computeChecksum(List<String> rows) { /* SHA256 */ }
    private void upload(String header, List<String> rows, String checksum) { /* SFTP */ }
}
```

---

## Immutable Abstract Classes

You CAN make them, and Guava does exactly this. But there's a tension:

```java
public abstract class ImmutableShape {
    private final String color;
    private final double area;

    protected ImmutableShape(String color, double area) {
        this.color = color;
        this.area = area;
    }

    // No setters. Fields are final. Immutable.
    public abstract double perimeter();
}
```

**The problem:** Subclasses can add mutable fields.

```java
public class Circle extends ImmutableShape {
    private double radius; // NOT final — mutable!
    public void setRadius(double r) { this.radius = r; } // oops
}
```

**How Guava solves this:** Package-private constructor. You can't extend it outside the package.

**Java 17+ solution:** Sealed classes.

```java
public sealed abstract class Payment permits UPIPayment, CardPayment {
    // Now ONLY these two can extend. You control the full hierarchy.
}
```

---

## 7 Common Traps

### Trap 1: The "God Abstract Class"

```java
// 13 abstract methods — every new payment type must implement all of them
public abstract class AbstractPaymentProcessor {
    public final void process() {
        validate(); checkFraud(); calculateFee(); applyDiscount();
        checkLimit(); debit(); credit(); notifyUser(); notifySender();
        updateLedger(); generateReceipt(); sendToSettlement(); reportToRBI();
    }
    // 13 abstract methods...
}
```

**Fix:** Break into smaller collaborators via composition.

### Trap 2: Inheriting for Code Reuse, Not "is-a"

```java
// Burning your only inheritance slot for logging
public abstract class BaseService {
    protected final Logger log = LoggerFactory.getLogger(getClass());
}
public class PaymentService extends BaseService { ... }
```

**Fix:** Just inject the logger. `private static final Logger log = ...`

### Trap 3: Forcing All Subclasses Into the Same Shape

```java
// Assumes all recon jobs work with files
protected abstract File downloadFile(LocalDate date);
// Kafka recon has no file — forced to create a temp file hack
```

**Fix:** Abstract the DATA, not the mechanism: `protected abstract List<Transaction> fetchTransactions(LocalDate date);`

### Trap 4: Leaking Internal State via `protected`

```java
protected List<LedgerEntry> entries = new ArrayList<>();
protected double runningBalance;
// Subclass can do: runningBalance = 0; entries.clear(); — bye bye audit trail
```

**Fix:** Fields always `private`. Expose via `protected` getters with defensive copies.

### Trap 5: Abstract Class With No Abstract Methods

```java
public abstract class BaseEntity {
    private Long id;
    private LocalDateTime createdAt;
    // Just shared fields, no contract, no template
}
```

**Fix:** Use `@Embeddable` or `@MappedSuperclass` or composition.

### Trap 6: Calling Abstract Methods in Constructor

```java
public Report() {
    this.title = generateTitle();  // DANGER — child fields not initialized yet
}
```

**Fix:** Never call overridable methods in constructors. Pass data through constructor params.

### Trap 7: Deep Inheritance Chains

```
BaseService -> AbstractPaymentService -> AbstractGatewayService -> AbstractCardService -> VisaCardService
```

**Fix:** Max 2 levels. More than that = refactor to composition.

---

## The Decision Rule

> In modern application code, if you're reaching for an abstract class, pause and ask: "Can I do this with an interface + strategy injection?" 90% of the time, yes. The 10% is when you need a **locked-down workflow** (template method with `final`) that junior devs cannot accidentally skip steps in.

## Quick Trap Reference

| Trap | Smell | Fix |
|---|---|---|
| God abstract class | 8+ abstract methods | Split into composed services |
| Inheriting for reuse | `BaseService`, `BaseController` | Inject utilities, don't inherit |
| Wrong abstraction level | Dummy implementations | Abstract the data, not mechanism |
| Protected fields | `protected List`, `protected double` | Private + protected getters |
| No abstract methods | Just shared fields | Composition or `@Embeddable` |
| Abstract call in constructor | Overridable method in constructor | Pass through constructor params |
| Deep chains | 3+ levels | Max 2, then composition |
