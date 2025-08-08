package design_patterns;

interface RemittanceState {
    void processPayment();
    void verify();
    void collect();
    void cancel();
}

class RemittanceTransaction {
    private RemittanceState initiatedState;
    private RemittanceState verifiedState;
    private RemittanceState paidState;
    private RemittanceState completedState;
    private RemittanceState cancelledState;
    private RemittanceState currentState;
    private String transactionId;
    private double amount;

    public RemittanceTransaction(String transactionId, double amount){
        this.transactionId = transactionId;
        this.amount = amount;

        initiatedState = new InitiatedState(this);
        verifiedState = new VerifiedState(this);
        paidState = new PaidState(this);
        completedState = new CompletedState(this);
        cancelledState = new CancelledState(this);

        // Start in initiated state
        currentState = initiatedState;
    }

    public void cancel(){currentState.cancel();}
    public void processPayment(){currentState.processPayment();}
    public void verify(){currentState.verify();}
    public void collect(){currentState.collect();}

    // State management methods
    public void setState(RemittanceState state) { this.currentState = state; }
    public RemittanceState getInitiatedState() { return initiatedState; }
    public RemittanceState getVerifiedState() { return verifiedState; }
    public RemittanceState getPaidState() { return paidState; }
    public RemittanceState getCompletedState() { return completedState; }
    public RemittanceState getCancelledState() { return cancelledState; }

    public String getTransactionId() { return transactionId; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId +
                " | Amount: $" + amount +
                " | State: " + currentState.getClass().getSimpleName();
    }
}

// Concrete State 1 - Initial state
class InitiatedState implements RemittanceState{
    private RemittanceTransaction transaction;

    public InitiatedState(RemittanceTransaction transaction){
        this.transaction = transaction;
    }

    @Override
    public void processPayment() {
        System.out.println("Please verify identity first");
    }

    @Override
    public void verify() {
        System.out.println("Identity verified. Transaction ready for payment.");
        transaction.setState(transaction.getVerifiedState());
    }

    @Override
    public void collect() {
        System.out.println("Cannot collect - transaction not paid yet");
    }

    @Override
    public void cancel() {
        System.out.println("Transaction cancelled - no charges applied");
        transaction.setState(transaction.getCancelledState());
    }
}

class VerifiedState implements RemittanceState{
    private final RemittanceTransaction transaction;

    VerifiedState(RemittanceTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void processPayment() {
        System.out.println("Payment processed. Money ready for collection.");
        transaction.setState(transaction.getPaidState());
    }

    @Override
    public void verify() {
        System.out.println("Already verified");
    }

    @Override
    public void collect() {
        System.out.println("Cannot collect - payment not processed yet");
    }

    @Override
    public void cancel() {
        System.out.println("Transaction cancelled - no payment taken");
        transaction.setState(transaction.getCancelledState());
    }
}

class PaidState implements RemittanceState{
    private final RemittanceTransaction transaction;

    PaidState(RemittanceTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void processPayment() {
        System.out.println("Payment already processed");
    }

    @Override
    public void verify() {
        System.out.println("Already verified and paid");
    }

    @Override
    public void collect() {
        System.out.println("Money collected by recipient. Transaction completed.");
        transaction.setState(transaction.getCompletedState());
    }

    @Override
    public void cancel() {
        System.out.println("Initiating refund - will take 3-5 business days");
        transaction.setState(transaction.getCancelledState());
    }
}

class CompletedState implements RemittanceState{
    private final RemittanceTransaction transaction;

    CompletedState(RemittanceTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void processPayment() {
        System.out.println("Transaction already completed");
    }

    @Override
    public void verify() {
        System.out.println("Transaction already completed");
    }

    @Override
    public void collect() {
        System.out.println("Money already collected");
    }

    @Override
    public void cancel() {
        System.out.println("Cannot cancel - transaction already completed");
    }
}

class CancelledState implements RemittanceState {
    private final RemittanceTransaction transaction;

    CancelledState(RemittanceTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void processPayment() {
        System.out.println("Transaction is cancelled");
    }

    @Override
    public void verify() {
        System.out.println("Transaction is cancelled");
    }

    @Override
    public void collect() {
        System.out.println("Transaction is cancelled");
    }

    @Override
    public void cancel() {
        System.out.println("Transaction already cancelled");
    }
}

public class StatePatternDemo {
    public static void main(String[] args) {
        RemittanceTransaction transaction = new RemittanceTransaction("TXN001", 500.0);

        System.out.println(transaction);
        System.out.println();

        // Try to pay without verification
        transaction.processPayment();
        System.out.println();

        // Normal flow: verify -> pay -> collect
        transaction.verify();
        System.out.println(transaction);
        System.out.println();

        transaction.processPayment();
        System.out.println(transaction);
        System.out.println();

        transaction.collect();
        System.out.println(transaction);
        System.out.println();

        // Try to cancel completed transaction
        transaction.cancel();

        System.out.println("\n--- Testing Cancellation ---");

        // New transaction to test cancellation
        RemittanceTransaction transaction2 = new RemittanceTransaction("TXN002", 250.0);
        transaction2.verify();
        transaction2.processPayment();
        transaction2.cancel(); // Cancel after payment
        System.out.println(transaction2);
    }
}
