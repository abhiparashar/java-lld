package phase1;

// Base interface - closed for modification
interface PaymentProcessor{
    void processPayment(double amount);
}
// Extensions - open for extension
class CreditCardProcessor implements PaymentProcessor{

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment: $" + amount);
    }
}

class PayPalProcessor implements PaymentProcessor{

    @Override
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment: $" + amount);
    }
}

// Context class
class PaymentService{
    public void processPayment(PaymentProcessor processor, double amount){
        processor.processPayment(amount);
    }
}

public class OpenClosedPrinciple {
    public static void main(String[] args) {
        System.out.println("Open-closed principle");
    }
}
