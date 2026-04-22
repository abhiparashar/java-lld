# The Diamond Problem — Deep Dive

## The Problem

```
        interface A
       /          \
  interface B    interface C
       \          /
        class D
```

Both B and C provide a default method with the same signature. D inherits both. Which one wins?

```java
public interface Loggable {
    default String format() {
        return "LOG: " + this.toString();
    }
}

public interface Auditable {
    default String format() {
        return "AUDIT: " + this.toString();
    }
}

// Diamond — which format() does Transaction get?
public class Transaction implements Loggable, Auditable {
    // COMPILE ERROR: class Transaction inherits unrelated defaults
    // for format() from types Loggable and Auditable
}
```

**Java refuses to guess. You must resolve it explicitly.**

---

## Solution 1: Override and Choose

```java
public class Transaction implements Loggable, Auditable {

    @Override
    public String format() {
        return Loggable.super.format();  // explicitly pick Loggable's
    }
}
```

## Solution 2: Override With Your Own Logic

```java
public class Transaction implements Loggable, Auditable {

    @Override
    public String format() {
        return "TXN:" + id + "|" + amount + "|" + status;
    }
}
```

## Solution 3: Combine Both

```java
public class Transaction implements Loggable, Auditable {

    @Override
    public String format() {
        return Loggable.super.format() + " | " + Auditable.super.format();
    }
}
```

---

## Java's Resolution Rules (Priority Order)

```
1. Class wins over interface
   -> If Transaction defines format(), that's used. Period.

2. Subinterface wins over parent interface
   -> If Auditable extends Loggable and both have format(),
     Auditable's version wins.

3. If ambiguous -> compile error, YOU must resolve it.
```

### Rule 2 Example:

```java
public interface Formattable {
    default String format() { return "base"; }
}

public interface AuditFormattable extends Formattable {
    default String format() { return "audit"; }  // more specific wins
}

public class Transaction implements Formattable, AuditFormattable {
    // No error — AuditFormattable.format() wins automatically
}
```

---

## The REAL Diamond Problem in Practice

The dangerous version isn't conflicting defaults. It's **conflicting contracts**:

```java
public interface Cache {
    void clear();  // removes all cached entries
}

public interface TransactionBuffer {
    void clear();  // flushes pending transactions to DB
}

public class TransactionCache implements Cache, TransactionBuffer {
    @Override
    public void clear() {
        // What does clear() mean here?
        // Delete cache? Flush to DB? Both?
        // The SEMANTIC conflict is the real problem
    }
}
```

**Fix: Don't have a single class implement semantically conflicting methods. Use composition:**

```java
public class TransactionProcessor {
    private final Cache cache;
    private final TransactionBuffer buffer;

    public void clearCache() { cache.clear(); }
    public void flushTransactions() { buffer.clear(); }
}
```

---

## Why the Diamond Problem Matters Less Than People Think

In real fintech codebases, you almost never have two interfaces with the same default method. It's mostly an interview topic.

**But** knowing the resolution rules saves you when it does happen — usually with third-party libraries where two interfaces accidentally collide.

The real lesson: **semantic conflicts are harder than syntactic ones.** The compiler catches method signature collisions. Nobody catches two methods that compile fine but mean completely different things.
