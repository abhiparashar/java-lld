package design_patterns;

import java.util.HashMap;
import java.util.Map;

interface PaymentStrategy{
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy{
    private final String cardNumber;
    private final String cvv;
    private final String userName;

    public CreditCardPayment(String cardNumber, String cvv, String userName ){
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.userName = userName;
    }

    @Override
    public void pay(double amount) {
        System.out.println("💳 Paid $" + amount + " with CreditCard by " + userName +
                " with card number " + cardNumber + " and cvv " + cvv);
    }
}

class PayPalPayment implements PaymentStrategy {
    private final String email;

    public PayPalPayment(String email){
        this.email = email;
    }

    @Override
    public void pay(double amount) {
        System.out.println("💰 Paid $" + amount + " with Paypal by " + email);
    }
}

class BitcoinPayment implements PaymentStrategy {
    private final String bitcoinId;

    public BitcoinPayment(String bitcoinId){
        this.bitcoinId = bitcoinId;
    }

    @Override
    public void pay(double amount) {
        System.out.println("₿ Paid $" + amount + " with Bitcoin by " + bitcoinId);
    }
}

class BankTransferPayment implements PaymentStrategy {
    private final String accountNumber;
    private final String bankName;

    BankTransferPayment(String accountNumber, String bankName) {
        this.accountNumber = accountNumber;
        this.bankName = bankName;
    }

    @Override
    public void pay(double amount) {
        System.out.println("🏦 Paid $" + amount + " with Bank Transfer from account " +
                accountNumber + " via bank " + bankName);
    }
}

class ShoppingCart{
    private Map<String, Double> map;
    private PaymentStrategy paymentStrategy;

    public ShoppingCart(){
        this.map = new HashMap<>();
    }

    public void addItem(String productName, double price){
        map.put(productName, price);
        System.out.println("🛒 Added: " + productName + " ($" + price + ")");
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy){
        this.paymentStrategy = paymentStrategy;
        System.out.println("💡 Payment method set");
    }

    private double calculateTotal() {
        return map.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    // FIXED: Show cart contents
    public void showCart() {
        System.out.println("📋 Cart Contents:");
        for (Map.Entry<String, Double> item : map.entrySet()) {
            System.out.println("    - " + item.getKey() + ": $" + item.getValue());
        }
        System.out.println("    Total: $" + calculateTotal());
    }

    // FIXED: Actually call the payment strategy
    public void checkout(){
        if (paymentStrategy == null) {
            System.out.println("❌ Please select a payment method first!");
            return;
        }

        if (map.isEmpty()) {
            System.out.println("❌ Cart is empty!");
            return;
        }

        double total = calculateTotal();
        System.out.println("💰 Total amount: $" + total);
        paymentStrategy.pay(total);  // FIXED: Actually call pay method
        System.out.println("✅ Payment completed!\n");
    }
}

public class StrategyPatternDemo {
    public static void main(String[] args) {
        System.out.println("🛍️  STRATEGY PATTERN DEMONSTRATION");
        System.out.println("===================================\n");

        // Create shopping cart
        ShoppingCart cart = new ShoppingCart();

        // Create different payment strategies
        PaymentStrategy creditCard = new CreditCardPayment("1234567890123456", "123", "John Doe");
        PaymentStrategy paypal = new PayPalPayment("john.doe@email.com");
        PaymentStrategy bitcoin = new BitcoinPayment("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa");
        PaymentStrategy bankTransfer = new BankTransferPayment("9876543210", "Chase Bank");

        // Scenario 1: Shopping and paying with Credit Card
        System.out.println("🛒 SCENARIO 1: Credit Card Payment");
        System.out.println("----------------------------------");
        cart.addItem("Laptop", 999.99);
        cart.addItem("Mouse", 29.99);
        cart.addItem("Keyboard", 79.99);
        cart.showCart();

        cart.setPaymentStrategy(creditCard);
        cart.checkout();

        // Scenario 2: Change mind, use PayPal instead
        System.out.println("🛒 SCENARIO 2: Switching Payment Methods");
        System.out.println("----------------------------------------");
        cart.addItem("Headphones", 159.99);
        cart.addItem("Webcam", 89.99);
        cart.showCart();

        System.out.println("🤔 Customer changes mind about payment method...");
        cart.setPaymentStrategy(paypal);
        cart.checkout();

        // Scenario 3: Tech-savvy customer uses Bitcoin
        System.out.println("🛒 SCENARIO 3: Bitcoin Payment");
        System.out.println("------------------------------");
        cart.addItem("Smartphone", 799.99);
        cart.addItem("Phone Case", 19.99);
        cart.showCart();

        cart.setPaymentStrategy(bitcoin);
        cart.checkout();

        // Scenario 4: Large purchase with Bank Transfer
        System.out.println("🛒 SCENARIO 4: Bank Transfer for Large Purchase");
        System.out.println("----------------------------------------------");
        cart.addItem("Gaming PC", 2499.99);
        cart.addItem("Monitor", 399.99);
        cart.addItem("Gaming Chair", 299.99);
        cart.showCart();

        cart.setPaymentStrategy(bankTransfer);
        cart.checkout();

        // Scenario 5: Demonstrate error handling
        System.out.println("🛒 SCENARIO 5: Error Handling");
        System.out.println("-----------------------------");
        ShoppingCart emptyCart = new ShoppingCart();
        emptyCart.checkout(); // Should show error

        ShoppingCart noPaymentCart = new ShoppingCart();
        noPaymentCart.addItem("Test Item", 10.0);
        noPaymentCart.checkout(); // Should show error

        // Summary
        System.out.println("📊 STRATEGY PATTERN BENEFITS DEMONSTRATED:");
        System.out.println("✅ Multiple payment algorithms encapsulated");
        System.out.println("✅ Easy to switch strategies at runtime");
        System.out.println("✅ Easy to add new payment methods");
        System.out.println("✅ Shopping cart doesn't know payment details");
        System.out.println("✅ Each strategy handles its own logic");
    }
}