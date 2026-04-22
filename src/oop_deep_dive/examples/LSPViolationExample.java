package oop_deep_dive.examples;

/**
 * Liskov Substitution Principle — Violation + Fix
 *
 * Real fintech scenario: WalletGateway that throws on large amounts
 * breaks the PaymentGateway contract.
 */
public class LSPViolationExample {

    // ============================================================
    // THE CONTRACT
    // ============================================================
    interface PaymentGateway {
        PaymentResponse charge(double amount);
    }

    record PaymentResponse(String status, String message) {}

    // ============================================================
    // GOOD: Razorpay honors the contract — any positive amount works
    // ============================================================
    static class RazorpayGateway implements PaymentGateway {
        @Override
        public PaymentResponse charge(double amount) {
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
            return new PaymentResponse("SUCCESS", "Charged " + amount + " via Razorpay");
        }
    }

    // ============================================================
    // BAD (LSP VIOLATION): Wallet adds a restriction the parent doesn't have
    // ============================================================
    static class WalletGatewayBad implements PaymentGateway {
        @Override
        public PaymentResponse charge(double amount) {
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
            if (amount > 10000) {
                // LSP VIOLATION — caller never expected this!
                // Parent contract says "any positive amount"
                throw new UnsupportedOperationException("Wallet limit is 10k");
            }
            return new PaymentResponse("SUCCESS", "Charged " + amount + " from wallet");
        }
    }

    // ============================================================
    // THE VICTIM — code that trusts the PaymentGateway contract
    // ============================================================
    static class CheckoutService {
        private final PaymentGateway gateway;

        CheckoutService(PaymentGateway gateway) {
            this.gateway = gateway;
        }

        public void checkout(double amount) {
            System.out.println("Attempting to charge: " + amount);
            try {
                PaymentResponse response = gateway.charge(amount);
                System.out.println("  Result: " + response);
            } catch (UnsupportedOperationException e) {
                System.out.println("  BROKEN: " + e.getMessage());
                System.out.println("  ^ This is an LSP violation — caller shouldn't need to know about wallet limits");
            }
        }
    }

    // ============================================================
    // FIX 1: Make the limitation part of the contract
    // ============================================================
    interface SmartPaymentGateway {
        PaymentResponse charge(double amount);
        boolean supports(double amount);  // caller checks first
    }

    static class WalletGatewayFixed implements SmartPaymentGateway {
        @Override
        public PaymentResponse charge(double amount) {
            if (!supports(amount)) {
                return new PaymentResponse("FAILED", "Amount exceeds wallet limit");
            }
            return new PaymentResponse("SUCCESS", "Charged " + amount + " from wallet");
        }

        @Override
        public boolean supports(double amount) {
            return amount > 0 && amount <= 10000;
        }
    }

    // ============================================================
    // FIX 2: Separate interface — wallet is NOT a full payment gateway
    // ============================================================
    interface WalletService {
        PaymentResponse charge(double amount) throws LimitExceededException;
        double getLimit();
    }

    static class LimitExceededException extends Exception {
        LimitExceededException(String msg) { super(msg); }
    }

    // ============================================================
    // Demo
    // ============================================================
    public static void main(String[] args) {
        System.out.println("=== LSP VIOLATION DEMO ===");
        System.out.println();

        // Works fine with Razorpay
        System.out.println("--- Razorpay (honors contract) ---");
        CheckoutService razorpayCheckout = new CheckoutService(new RazorpayGateway());
        razorpayCheckout.checkout(500);
        razorpayCheckout.checkout(15000);  // works fine

        System.out.println();

        // BREAKS with Wallet for large amounts
        System.out.println("--- Wallet BAD (violates LSP) ---");
        CheckoutService walletCheckout = new CheckoutService(new WalletGatewayBad());
        walletCheckout.checkout(500);      // works
        walletCheckout.checkout(15000);    // EXPLODES — LSP violation

        System.out.println();

        // Fix: Smart gateway with supports() check
        System.out.println("--- Wallet FIXED (supports() check) ---");
        WalletGatewayFixed fixedWallet = new WalletGatewayFixed();
        if (fixedWallet.supports(15000)) {
            System.out.println("  " + fixedWallet.charge(15000));
        } else {
            System.out.println("  Wallet doesn't support this amount. Use another gateway.");
        }

        System.out.println();
        System.out.println("KEY TAKEAWAY: If you need instanceof or special try-catch for a subtype,");
        System.out.println("your abstraction is wrong. That's LSP violation.");
    }
}
