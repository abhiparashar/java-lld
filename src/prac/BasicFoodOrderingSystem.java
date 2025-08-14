package prac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Same MenuItem, MenuCategory, CustomerInfo classes (unchanged from Phase 5)
class MenuItem {
    private final String name;
    private final String description;
    private final double amount;

    MenuItem(String name, String description, double amount) {
        this.name = name;
        this.description = description;
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("%s - $%.2f", name, amount);
    }
}

class MenuCategory {
    private final String name;
    private final List<MenuItem> menuItems;

    MenuCategory(String name) {
        this.name = name;
        this.menuItems = new ArrayList<>();
    }

    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
    }

    public String getName() {
        return name;
    }

    public List<MenuItem> getMenuItems() {
        return new ArrayList<>(menuItems);
    }

    public void displayItems() {
        System.out.println("\n" + name.toUpperCase() + ":");
        for (int i = 0; i < menuItems.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, menuItems.get(i));
        }
    }
}

class CustomerInfo {
    private final String customerName;
    private final String number;

    CustomerInfo(String customerName, String number) {
        this.customerName = customerName;
        this.number = number;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getNumber() {
        return number;
    }
}

// Same OrderItem class (unchanged from Phase 5)
class OrderItem {
    private final MenuItem menuItem;
    private final int quantity;
    private final List<String> customizations;

    OrderItem(MenuItem menuItem, int quantity, List<String> customizations) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.customizations = new ArrayList<>(customizations);
    }

    public List<String> getCustomizations() {
        return new ArrayList<>(customizations);
    }

    public int getQuantity() {
        return quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public double getTotalPrice() {
        return menuItem.getAmount() * quantity;
    }

    public String getDescription() {
        StringBuilder desc = new StringBuilder(menuItem.getName() + " x" + quantity);
        if (!customizations.isEmpty()) {
            desc.append(" (").append(String.join(", ", customizations)).append(")");
        }
        return desc.toString();
    }
}

// ============================================================================
// OBSERVER PATTERN IMPLEMENTATION - Notification System
// ============================================================================

interface OrderObserver {
    void onOrderPlaced(Order order);
    void onOrderStatusChanged(Order order, String previousStatus, String newStatus);
    void onOrderCancelled(Order order);
    void onPaymentProcessed(Order order, PaymentResult paymentResult);
}

// Observable subject - manages observers
abstract class OrderSubject {
    private final List<OrderObserver> observers = new ArrayList<>();

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
        System.out.println("🔗 Observer registered: " + observer.getClass().getSimpleName());
    }

    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
        System.out.println("🔗 Observer removed: " + observer.getClass().getSimpleName());
    }

    protected void notifyOrderPlaced(Order order) {
        for (OrderObserver observer : observers) {
            try {
                observer.onOrderPlaced(order);
            } catch (Exception e) {
                System.err.println("Error in observer notification: " + e.getMessage());
            }
        }
    }

    protected void notifyOrderStatusChanged(Order order, String previousStatus, String newStatus) {
        for (OrderObserver observer : observers) {
            try {
                observer.onOrderStatusChanged(order, previousStatus, newStatus);
            } catch (Exception e) {
                System.err.println("Error in observer notification: " + e.getMessage());
            }
        }
    }

    protected void notifyOrderCancelled(Order order) {
        for (OrderObserver observer : observers) {
            try {
                observer.onOrderCancelled(order);
            } catch (Exception e) {
                System.err.println("Error in observer notification: " + e.getMessage());
            }
        }
    }

    protected void notifyPaymentProcessed(Order order, PaymentResult paymentResult) {
        for (OrderObserver observer : observers) {
            try {
                observer.onPaymentProcessed(order, paymentResult);
            } catch (Exception e) {
                System.err.println("Error in observer notification: " + e.getMessage());
            }
        }
    }
}

// Email notification service
class EmailNotificationService implements OrderObserver {
    private final String serviceName = "Email Service";

    @Override
    public void onOrderPlaced(Order order) {
        if (!order.getEmail().isEmpty()) {
            String subject = "Order Confirmation - " + order.getOrderId();
            String message = String.format(
                    "Dear %s,\n\nThank you for your order!\n\nOrder Details:\n- Order ID: %s\n- Total: $%.2f\n- Items: %d\n\nWe'll keep you updated on your order status.\n\nBest regards,\nRestaurant Team",
                    order.getCustomerInfo().getCustomerName(),
                    order.getOrderId(),
                    order.getTotalValue(),
                    order.getOrderItems().size()
            );
            sendEmail(order.getEmail(), subject, message);
        }
    }

    @Override
    public void onOrderStatusChanged(Order order, String previousStatus, String newStatus) {
        if (!order.getEmail().isEmpty()) {
            String subject = "Order Update - " + order.getOrderId();
            String message = createStatusUpdateMessage(order, previousStatus, newStatus);
            sendEmail(order.getEmail(), subject, message);
        }
    }

    @Override
    public void onOrderCancelled(Order order) {
        if (!order.getEmail().isEmpty()) {
            String subject = "Order Cancelled - " + order.getOrderId();
            String message = String.format(
                    "Dear %s,\n\nYour order %s has been cancelled.\n\nIf payment was processed, a refund will be issued within 3-5 business days.\n\nWe apologize for any inconvenience.\n\nBest regards,\nRestaurant Team",
                    order.getCustomerInfo().getCustomerName(),
                    order.getOrderId()
            );
            sendEmail(order.getEmail(), subject, message);
        }
    }

    @Override
    public void onPaymentProcessed(Order order, PaymentResult paymentResult) {
        if (!order.getEmail().isEmpty() && paymentResult.isSuccess()) {
            String subject = "Payment Confirmation - " + order.getOrderId();
            String message = String.format(
                    "Dear %s,\n\nPayment processed successfully!\n\n- Amount: $%.2f\n- Method: %s\n- Transaction ID: %s\n\nYour order is now being prepared.\n\nBest regards,\nRestaurant Team",
                    order.getCustomerInfo().getCustomerName(),
                    order.getTotalValue(),
                    paymentResult.getPaymentMethod(),
                    paymentResult.getTransactionId()
            );
            sendEmail(order.getEmail(), subject, message);
        }
    }

    private String createStatusUpdateMessage(Order order, String previousStatus, String newStatus) {
        String customerName = order.getCustomerInfo().getCustomerName();
        String orderId = order.getOrderId();

        switch (newStatus) {
            case "CONFIRMED":
                return String.format("Dear %s,\n\nGreat news! Your order %s has been confirmed and is being prepared by our kitchen staff.\n\nEstimated preparation time: 15-20 minutes.\n\nBest regards,\nRestaurant Team", customerName, orderId);
            case "PREPARING":
                return String.format("Dear %s,\n\nYour order %s is now being prepared by our experienced chefs.\n\nWe'll notify you when it's ready for delivery.\n\nBest regards,\nRestaurant Team", customerName, orderId);
            case "READY":
                return String.format("Dear %s,\n\nYour order %s is ready! Our delivery driver will be on the way shortly.\n\nThank you for your patience.\n\nBest regards,\nRestaurant Team", customerName, orderId);
            case "IN_TRANSIT":
                return String.format("Dear %s,\n\nYour order %s is on its way!\n\nExpected delivery time: 10-15 minutes.\n\nBest regards,\nRestaurant Team", customerName, orderId);
            case "DELIVERED":
                return String.format("Dear %s,\n\nYour order %s has been delivered!\n\nWe hope you enjoy your meal. Please rate your experience in our app.\n\nBest regards,\nRestaurant Team", customerName, orderId);
            default:
                return String.format("Dear %s,\n\nYour order %s status has been updated to %s.\n\nBest regards,\nRestaurant Team", customerName, orderId, newStatus);
        }
    }

    private void sendEmail(String email, String subject, String message) {
        System.out.printf("📧 [%s] Email sent to %s%n", serviceName, email);
        System.out.printf("   Subject: %s%n", subject);
        System.out.printf("   Preview: %s...%n", message.substring(0, Math.min(60, message.length())));
    }
}

// SMS notification service
class SMSNotificationService implements OrderObserver {
    private final String serviceName = "SMS Service";

    @Override
    public void onOrderPlaced(Order order) {
        String message = String.format("Order %s placed successfully! Total: $%.2f. Track at: restaurant.com/track/%s",
                order.getOrderId(), order.getTotalValue(), order.getOrderId());
        sendSMS(order.getCustomerInfo().getNumber(), message);
    }

    @Override
    public void onOrderStatusChanged(Order order, String previousStatus, String newStatus) {
        // Send SMS for important status updates only
        if (shouldSendSMS(newStatus)) {
            String message = createSMSMessage(order, newStatus);
            sendSMS(order.getCustomerInfo().getNumber(), message);
        }
    }

    @Override
    public void onOrderCancelled(Order order) {
        String message = String.format("Order %s cancelled. Refund processed if applicable. Questions? Call (555) FOOD-123",
                order.getOrderId());
        sendSMS(order.getCustomerInfo().getNumber(), message);
    }

    @Override
    public void onPaymentProcessed(Order order, PaymentResult paymentResult) {
        if (paymentResult.isSuccess()) {
            String message = String.format("Payment confirmed! $%.2f via %s. Order %s is being prepared.",
                    order.getTotalValue(), paymentResult.getPaymentMethod(), order.getOrderId());
            sendSMS(order.getCustomerInfo().getNumber(), message);
        } else {
            String message = String.format("Payment failed for order %s. Please try a different payment method.",
                    order.getOrderId());
            sendSMS(order.getCustomerInfo().getNumber(), message);
        }
    }

    private boolean shouldSendSMS(String status) {
        // Only send SMS for critical updates to avoid spam
        return status.equals("READY") || status.equals("IN_TRANSIT") || status.equals("DELIVERED");
    }

    private String createSMSMessage(Order order, String status) {
        switch (status) {
            case "READY":
                return String.format("🍕 Order %s ready for delivery! Driver will arrive soon.", order.getOrderId());
            case "IN_TRANSIT":
                return String.format("🚚 Order %s on the way! ETA: 10-15 min. Track: restaurant.com/track/%s",
                        order.getOrderId(), order.getOrderId());
            case "DELIVERED":
                return String.format("✅ Order %s delivered! Enjoy your meal! Rate us: restaurant.com/rate",
                        order.getOrderId());
            default:
                return String.format("Order %s: %s", order.getOrderId(), status.toLowerCase());
        }
    }

    private void sendSMS(String phone, String message) {
        System.out.printf("📱 [%s] SMS sent to %s%n", serviceName, phone);
        System.out.printf("   Message: %s%n", message);
    }
}

// Push notification service
class PushNotificationService implements OrderObserver {
    private final String serviceName = "Push Notification";

    @Override
    public void onOrderPlaced(Order order) {
        String title = "Order Placed";
        String message = String.format("Order %s placed successfully! Total: $%.2f",
                order.getOrderId(), order.getTotalValue());
        sendPushNotification(title, message, order.getOrderId());
    }

    @Override
    public void onOrderStatusChanged(Order order, String previousStatus, String newStatus) {
        String title = "Order Update";
        String message = createPushMessage(order, newStatus);
        sendPushNotification(title, message, order.getOrderId());
    }

    @Override
    public void onOrderCancelled(Order order) {
        String title = "Order Cancelled";
        String message = String.format("Order %s has been cancelled", order.getOrderId());
        sendPushNotification(title, message, order.getOrderId());
    }

    @Override
    public void onPaymentProcessed(Order order, PaymentResult paymentResult) {
        if (paymentResult.isSuccess()) {
            String title = "Payment Successful";
            String message = String.format("$%.2f paid via %s", order.getTotalValue(), paymentResult.getPaymentMethod());
            sendPushNotification(title, message, order.getOrderId());
        }
    }

    private String createPushMessage(Order order, String status) {
        switch (status) {
            case "CONFIRMED":
                return String.format("Order %s confirmed! 👨‍🍳", order.getOrderId());
            case "PREPARING":
                return String.format("Order %s is being prepared 🍳", order.getOrderId());
            case "READY":
                return String.format("Order %s is ready! 🍕", order.getOrderId());
            case "IN_TRANSIT":
                return String.format("Order %s is on the way! 🚚", order.getOrderId());
            case "DELIVERED":
                return String.format("Order %s delivered! Enjoy! 🎉", order.getOrderId());
            default:
                return String.format("Order %s: %s", order.getOrderId(), status);
        }
    }

    private void sendPushNotification(String title, String message, String orderId) {
        System.out.printf("🔔 [%s] Push notification sent%n", serviceName);
        System.out.printf("   Title: %s%n", title);
        System.out.printf("   Message: %s%n", message);
        System.out.printf("   Action: View Order %s%n", orderId);
    }
}

// Restaurant dashboard for internal notifications
class RestaurantDashboard implements OrderObserver {
    private final String serviceName = "Restaurant Dashboard";

    @Override
    public void onOrderPlaced(Order order) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.printf("🏪 [%s] %s - NEW ORDER ALERT%n", serviceName, timestamp);
        System.out.printf("   Order: %s | Customer: %s%n", order.getOrderId(), order.getCustomerInfo().getCustomerName());
        System.out.printf("   Items: %d | Total: $%.2f%n", order.getOrderItems().size(), order.getTotalValue());
        System.out.printf("   Payment: %s%n", order.isPaid() ? "✅ PAID" : "❌ PENDING");
    }

    @Override
    public void onOrderStatusChanged(Order order, String previousStatus, String newStatus) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.printf("🏪 [%s] %s - Order %s: %s → %s%n",
                serviceName, timestamp, order.getOrderId(), previousStatus, newStatus);

        // Alert for orders ready for delivery
        if ("READY".equals(newStatus)) {
            System.out.printf("   🚨 DELIVERY ALERT: Order %s ready for pickup by driver%n", order.getOrderId());
        }
    }

    @Override
    public void onOrderCancelled(Order order) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.printf("🏪 [%s] %s - ORDER CANCELLED: %s%n", serviceName, timestamp, order.getOrderId());
        System.out.printf("   ⚠️ ACTION REQUIRED: Investigate cancellation reason%n");
    }

    @Override
    public void onPaymentProcessed(Order order, PaymentResult paymentResult) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        if (paymentResult.isSuccess()) {
            System.out.printf("🏪 [%s] %s - PAYMENT SUCCESS: Order %s ($%.2f via %s)%n",
                    serviceName, timestamp, order.getOrderId(),
                    order.getTotalValue(), paymentResult.getPaymentMethod());
        } else {
            System.out.printf("🏪 [%s] %s - PAYMENT FAILED: Order %s%n",
                    serviceName, timestamp, order.getOrderId());
            System.out.printf("   ⚠️ ACTION REQUIRED: Follow up with customer%n");
        }
    }
}

// Analytics service for tracking metrics
class AnalyticsService implements OrderObserver {
    private int totalOrders = 0;
    private int successfulPayments = 0;
    private int failedPayments = 0;
    private int cancelledOrders = 0;
    private double totalRevenue = 0.0;

    @Override
    public void onOrderPlaced(Order order) {
        totalOrders++;
        System.out.printf("📊 [Analytics] Order placed - Total orders today: %d%n", totalOrders);
    }

    @Override
    public void onOrderStatusChanged(Order order, String previousStatus, String newStatus) {
        if ("DELIVERED".equals(newStatus)) {
            System.out.printf("📊 [Analytics] Order delivered - Customer satisfaction tracking initiated%n");
        }
    }

    @Override
    public void onOrderCancelled(Order order) {
        cancelledOrders++;
        System.out.printf("📊 [Analytics] Order cancelled - Total cancellations: %d (%.1f%%)%n",
                cancelledOrders, (cancelledOrders * 100.0 / totalOrders));
    }

    @Override
    public void onPaymentProcessed(Order order, PaymentResult paymentResult) {
        if (paymentResult.isSuccess()) {
            successfulPayments++;
            totalRevenue += order.getTotalValue();
            System.out.printf("📊 [Analytics] Payment success - Revenue: $%.2f | Success rate: %.1f%%n",
                    totalRevenue, (successfulPayments * 100.0 / (successfulPayments + failedPayments)));
        } else {
            failedPayments++;
            System.out.printf("📊 [Analytics] Payment failed - Failed payments: %d%n", failedPayments);
        }
    }

    public void printDashboard() {
        System.out.println("\n📊 ANALYTICS DASHBOARD");
        System.out.println("=".repeat(40));
        System.out.printf("Total Orders: %d%n", totalOrders);
        System.out.printf("Successful Payments: %d%n", successfulPayments);
        System.out.printf("Failed Payments: %d%n", failedPayments);
        System.out.printf("Cancelled Orders: %d%n", cancelledOrders);
        System.out.printf("Total Revenue: $%.2f%n", totalRevenue);
        if (successfulPayments + failedPayments > 0) {
            System.out.printf("Payment Success Rate: %.1f%%%n",
                    (successfulPayments * 100.0 / (successfulPayments + failedPayments)));
        }
        if (totalOrders > 0) {
            System.out.printf("Cancellation Rate: %.1f%%%n", (cancelledOrders * 100.0 / totalOrders));
        }
        System.out.println("=".repeat(40));
    }
}

// ============================================================================
// SAME PAYMENT SYSTEM FROM PHASE 5
// ============================================================================

interface PaymentStrategy {
    PaymentResult processPayment(double amount, String orderId);
    String getPaymentType();
}

class PaymentResult {
    private final boolean success;
    private final String transactionId;
    private final String message;
    private final String paymentMethod;

    public PaymentResult(boolean success, String transactionId, String message, String paymentMethod) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
        this.paymentMethod = paymentMethod;
    }

    public boolean isSuccess() { return success; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
    public String getPaymentMethod() { return paymentMethod; }

    @Override
    public String toString() {
        return String.format("Payment[%s]: %s (TX: %s)",
                paymentMethod, message, transactionId);
    }
}

class CreditCardPayment implements PaymentStrategy {
    private final String cardNumber;
    private final String expiryDate;
    private final String cvv;
    private final String cardholderName;

    public CreditCardPayment(String cardNumber, String expiryDate, String cvv, String cardholderName) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardholderName = cardholderName;
    }

    @Override
    public PaymentResult processPayment(double amount, String orderId) {
        System.out.printf("💳 Processing credit card payment of $%.2f%n", amount);

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        // Simulate occasional payment failure
        if (Math.random() < 0.1) {
            return new PaymentResult(false, null, "Card declined", "Credit Card");
        }

        String transactionId = "CC-" + System.currentTimeMillis();
        return new PaymentResult(true, transactionId, "Payment successful", "Credit Card");
    }

    @Override
    public String getPaymentType() {
        return "Credit Card";
    }
}

class PayPalPayment implements PaymentStrategy {
    private final String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public PaymentResult processPayment(double amount, String orderId) {
        System.out.printf("🅿️ Processing PayPal payment of $%.2f%n", amount);

        try { Thread.sleep(800); } catch (InterruptedException e) {}

        String transactionId = "PP-" + System.currentTimeMillis();
        return new PaymentResult(true, transactionId, "PayPal payment completed", "PayPal");
    }

    @Override
    public String getPaymentType() {
        return "PayPal";
    }
}

class CashOnDeliveryPayment implements PaymentStrategy {
    @Override
    public PaymentResult processPayment(double amount, String orderId) {
        System.out.printf("💵 Cash on Delivery setup for $%.2f%n", amount);

        String transactionId = "COD-" + System.currentTimeMillis();
        return new PaymentResult(true, transactionId, "Cash on delivery confirmed", "Cash on Delivery");
    }

    @Override
    public String getPaymentType() {
        return "Cash on Delivery";
    }
}

class PaymentProcessor {
    private PaymentStrategy currentStrategy;

    public PaymentProcessor() {
        this.currentStrategy = new CashOnDeliveryPayment();
    }

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.currentStrategy = strategy;
    }

    public PaymentResult processPayment(double amount, String orderId) {
        return currentStrategy.processPayment(amount, orderId);
    }
}

// ============================================================================
// SAME STATE PATTERN FROM PREVIOUS PHASES
// ============================================================================

abstract class OrderState {
    protected Order order;

    public OrderState(Order order) {
        this.order = order;
    }

    public abstract void nextStep();
    public abstract String getStatusName();

    public boolean canCancel() { return false; }
    public boolean canModify() { return false; }

    public void cancel() {
        if (canCancel()) {
            order.setState(new CancelledState(order));
        } else {
            System.out.println("❌ Cannot cancel order in " + getStatusName() + " state");
        }
    }
}

class PendingState extends OrderState {
    public PendingState(Order order) { super(order); }

    @Override
    public void nextStep() {
        System.out.println("✅ Order confirmed! Moving to preparation...");
        order.setState(new ConfirmedState(order));
    }

    @Override
    public String getStatusName() { return "PENDING"; }

    @Override
    public boolean canCancel() { return true; }
    @Override
    public boolean canModify() { return true; }
}

class ConfirmedState extends OrderState {
    public ConfirmedState(Order order) { super(order); }

    @Override
    public void nextStep() {
        System.out.println("👨‍🍳 Kitchen started preparing your order...");
        order.setState(new PreparingState(order));
    }

    @Override
    public String getStatusName() { return "CONFIRMED"; }

    @Override
    public boolean canCancel() { return true; }
    @Override
    public boolean canModify() { return false; }
}

class PreparingState extends OrderState {
    public PreparingState(Order order) { super(order); }

    @Override
    public void nextStep() {
        System.out.println("🍕 Order is ready for pickup/delivery!");
        order.setState(new ReadyState(order));
    }

    @Override
    public String getStatusName() { return "PREPARING"; }
}

class ReadyState extends OrderState {
    public ReadyState(Order order) { super(order); }

    @Override
    public void nextStep() {
        System.out.println("🚚 Order is out for delivery!");
        order.setState(new InTransitState(order));
    }

    @Override
    public String getStatusName() { return "READY"; }
}

class InTransitState extends OrderState {
    public InTransitState(Order order) { super(order); }

    @Override
    public void nextStep() {
        System.out.println("🎉 Order delivered successfully!");
        order.setState(new DeliveredState(order));
    }

    @Override
    public String getStatusName() { return "IN_TRANSIT"; }
}

class DeliveredState extends OrderState {
    public DeliveredState(Order order) { super(order); }

    @Override
    public void nextStep() {
        System.out.println("✨ Order already delivered! No further action needed.");
    }

    @Override
    public String getStatusName() { return "DELIVERED"; }
}

class CancelledState extends OrderState {
    public CancelledState(Order order) { super(order); }

    @Override
    public void nextStep() {
        System.out.println("❌ Order was cancelled. No further processing possible.");
    }

    @Override
    public String getStatusName() { return "CANCELLED"; }
}

// ============================================================================
// ENHANCED ORDER CLASS - Now extends OrderSubject for Observer pattern
// ============================================================================

class Order extends OrderSubject {
    private final String orderId;
    private final List<OrderItem> orderItems;
    private final CustomerInfo customerInfo;
    private final String email;
    private final String deliveryAddress;
    private final String specialInstructions;
    private OrderState currentState;
    private PaymentResult paymentResult;

    private Order(Builder builder) {
        this.orderId = generateOrderId();
        this.orderItems = new ArrayList<>(builder.orderItems);
        this.customerInfo = builder.customerInfo;
        this.email = builder.email;
        this.deliveryAddress = builder.deliveryAddress;
        this.specialInstructions = builder.specialInstructions;
        this.currentState = new PendingState(this);
        this.paymentResult = null;
    }

    private String generateOrderId() {
        return "ORD-" + System.currentTimeMillis() + "-" +
                Integer.toHexString(hashCode()).toUpperCase().substring(0, 4);
    }

    // Builder Pattern (same as previous phases)
    public static class Builder {
        private CustomerInfo customerInfo;
        private List<OrderItem> orderItems = new ArrayList<>();
        private String email = "";
        private String deliveryAddress = "";
        private String specialInstructions = "";

        public Builder(String customerName, String customerPhone) {
            this.customerInfo = new CustomerInfo(customerName, customerPhone);
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setDeliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public Builder setSpecialInstructions(String specialInstructions) {
            this.specialInstructions = specialInstructions;
            return this;
        }

        public Builder addItem(MenuItem item, int quantity, List<String> customizations) {
            this.orderItems.add(new OrderItem(item, quantity, customizations));
            return this;
        }

        public Builder addItem(MenuItem item, int quantity) {
            return addItem(item, quantity, new ArrayList<>());
        }

        public Order build() {
            if (orderItems.isEmpty()) {
                throw new IllegalStateException("Order must contain at least one item");
            }
            if (customerInfo.getCustomerName() == null || customerInfo.getCustomerName().trim().isEmpty()) {
                throw new IllegalStateException("Customer name is required");
            }
            return new Order(this);
        }
    }

    // Enhanced state management with notifications
    public void setState(OrderState newState) {
        String previousStatus = this.currentState.getStatusName();
        this.currentState = newState;
        String newStatus = newState.getStatusName();

        System.out.println("📱 Order " + orderId + " status changed to: " + newStatus);

        // Notify observers about status change
        notifyOrderStatusChanged(this, previousStatus, newStatus);
    }

    public void processNextStep() {
        currentState.nextStep();
    }

    public void cancel() {
        if (canCancel()) {
            currentState.cancel();
            // Notify observers about cancellation
            notifyOrderCancelled(this);
        } else {
            System.out.println("❌ Cannot cancel order in current state");
        }
    }

    public boolean canCancel() {
        return currentState.canCancel();
    }

    public boolean canModify() {
        return currentState.canModify();
    }

    public String getStatus() {
        return currentState.getStatusName();
    }

    // Payment methods with notifications
    public void setPaymentResult(PaymentResult result) {
        this.paymentResult = result;
        // Notify observers about payment processing
        notifyPaymentProcessed(this, result);
    }

    public boolean isPaid() {
        return paymentResult != null && paymentResult.isSuccess();
    }

    // Method to trigger order placed notification
    public void triggerOrderPlacedNotification() {
        notifyOrderPlaced(this);
    }

    // Other methods
    public double getTotalValue() {
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void displaySummary() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ORDER SUMMARY - " + orderId);
        System.out.println("Customer: " + customerInfo.getCustomerName());
        System.out.println("Phone: " + customerInfo.getNumber());

        if (!email.isEmpty()) {
            System.out.println("Email: " + email);
        }

        if (!deliveryAddress.isEmpty()) {
            System.out.println("Delivery Address: " + deliveryAddress);
        }

        System.out.println("Status: " + getStatus() +
                " (Can cancel: " + canCancel() +
                ", Can modify: " + canModify() + ")");

        System.out.println("Items:");
        for (OrderItem item : orderItems) {
            System.out.printf("  - %s: $%.2f%n",
                    item.getDescription(), item.getTotalPrice());
        }

        System.out.printf("Total: $%.2f%n", getTotalValue());

        if (paymentResult != null) {
            System.out.println("Payment: " + paymentResult);
        } else {
            System.out.println("Payment: Not processed");
        }

        if (!specialInstructions.isEmpty()) {
            System.out.println("Special Instructions: " + specialInstructions);
        }

        System.out.println("=".repeat(50));
    }

    // Getters
    public String getOrderId() { return orderId; }
    public CustomerInfo getCustomerInfo() { return customerInfo; }
    public String getEmail() { return email; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public List<OrderItem> getOrderItems() { return new ArrayList<>(orderItems); }
    public OrderState getCurrentState() { return currentState; }
    public PaymentResult getPaymentResult() { return paymentResult; }
}

// ============================================================================
// SAME COMMAND PATTERN FROM PHASE 5 (with notification integration)
// ============================================================================

interface Command {
    boolean execute();
    void undo();
    String getDescription();
}

class PlaceOrderCommand implements Command {
    private final OrderManager orderManager;
    private final Order order;
    private final PaymentProcessor paymentProcessor;

    public PlaceOrderCommand(OrderManager orderManager, Order order, PaymentProcessor paymentProcessor) {
        this.orderManager = orderManager;
        this.order = order;
        this.paymentProcessor = paymentProcessor;
    }

    @Override
    public boolean execute() {
        // Process payment first
        PaymentResult paymentResult = paymentProcessor.processPayment(order.getTotalValue(), order.getOrderId());
        order.setPaymentResult(paymentResult);

        if (paymentResult.isSuccess()) {
            orderManager.addOrderDirect(order);
            // Trigger order placed notification
            order.triggerOrderPlacedNotification();
            System.out.println("✅ Executed: Order " + order.getOrderId() + " placed and paid");
            return true;
        } else {
            System.out.println("❌ Failed: Payment failed for Order " + order.getOrderId());
            return false;
        }
    }

    @Override
    public void undo() {
        orderManager.removeOrderDirect(order.getOrderId());
        System.out.println("↩️ Undone: Order " + order.getOrderId() + " removed");
    }

    @Override
    public String getDescription() {
        String paymentInfo = order.isPaid() ? order.getPaymentResult().getPaymentMethod() : "Payment Failed";
        return "Place Order " + order.getOrderId() + " (" + paymentInfo + ")";
    }
}

class ProcessOrderCommand implements Command {
    private final OrderManager orderManager;
    private final String orderId;
    private OrderState previousState;

    public ProcessOrderCommand(OrderManager orderManager, String orderId) {
        this.orderManager = orderManager;
        this.orderId = orderId;
    }

    @Override
    public boolean execute() {
        Order order = orderManager.getOrder(orderId);
        if (order != null) {
            previousState = order.getCurrentState();
            order.processNextStep();
            System.out.println("✅ Executed: Order " + orderId + " status updated");
            return true;
        }
        return false;
    }

    @Override
    public void undo() {
        Order order = orderManager.getOrder(orderId);
        if (order != null && previousState != null) {
            order.setState(previousState);
            System.out.println("↩️ Undone: Order " + orderId + " status reverted");
        }
    }

    @Override
    public String getDescription() {
        return "Process Order " + orderId;
    }
}

class CancelOrderCommand implements Command {
    private final OrderManager orderManager;
    private final String orderId;
    private OrderState previousState;

    public CancelOrderCommand(OrderManager orderManager, String orderId) {
        this.orderManager = orderManager;
        this.orderId = orderId;
    }

    @Override
    public boolean execute() {
        Order order = orderManager.getOrder(orderId);
        if (order != null && order.canCancel()) {
            previousState = order.getCurrentState();
            order.cancel();
            System.out.println("✅ Executed: Order " + orderId + " cancelled");
            return true;
        }
        return false;
    }

    @Override
    public void undo() {
        Order order = orderManager.getOrder(orderId);
        if (order != null && previousState != null) {
            order.setState(previousState);
            System.out.println("↩️ Undone: Order " + orderId + " cancellation reverted");
        }
    }

    @Override
    public String getDescription() {
        return "Cancel Order " + orderId;
    }
}

// Same CommandInvoker and OrderManager classes (unchanged from Phase 5)
class CommandInvoker {
    private final List<Command> history = new ArrayList<>();
    private int currentPosition = -1;

    public boolean executeCommand(Command command) {
        if (currentPosition < history.size() - 1) {
            history.subList(currentPosition + 1, history.size()).clear();
        }

        boolean success = command.execute();

        if (success) {
            history.add(command);
            currentPosition++;
        }

        return success;
    }

    public boolean undo() {
        if (currentPosition >= 0) {
            Command command = history.get(currentPosition);
            command.undo();
            currentPosition--;
            return true;
        }
        System.out.println("❌ Nothing to undo");
        return false;
    }

    public boolean redo() {
        if (currentPosition < history.size() - 1) {
            currentPosition++;
            Command command = history.get(currentPosition);
            command.execute();
            return true;
        }
        System.out.println("❌ Nothing to redo");
        return false;
    }

    public void showHistory() {
        System.out.println("\n📋 COMMAND HISTORY:");
        System.out.println("=".repeat(60));

        if (history.isEmpty()) {
            System.out.println("No commands executed yet");
        } else {
            for (int i = 0; i < history.size(); i++) {
                String indicator = (i == currentPosition) ? " <- CURRENT" : "";
                String status = (i <= currentPosition) ? "✅" : "❌";
                System.out.printf("%s %d. %s%s%n",
                        status, i + 1, history.get(i).getDescription(), indicator);
            }
        }

        System.out.println("=".repeat(60));
    }
}

class OrderManager {
    private final List<Order> orders = new ArrayList<>();

    public void addOrderDirect(Order order) {
        orders.add(order);
    }

    public void removeOrderDirect(String orderId) {
        orders.removeIf(order -> order.getOrderId().equals(orderId));
    }

    public Order getOrder(String orderId) {
        return orders.stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
}

// ============================================================================
// ENHANCED RESTAURANT CLASS - Now with Observer Pattern
// ============================================================================

class SimpleRestaurant {
    private final List<MenuCategory> categories;
    private final OrderManager orderManager;
    private final CommandInvoker commandInvoker;
    private final PaymentProcessor paymentProcessor;

    // Notification services
    private final EmailNotificationService emailService;
    private final SMSNotificationService smsService;
    private final PushNotificationService pushService;
    private final RestaurantDashboard restaurantDashboard;
    private final AnalyticsService analyticsService;

    SimpleRestaurant() {
        this.categories = new ArrayList<>();
        this.orderManager = new OrderManager();
        this.commandInvoker = new CommandInvoker();
        this.paymentProcessor = new PaymentProcessor();

        // Initialize notification services
        this.emailService = new EmailNotificationService();
        this.smsService = new SMSNotificationService();
        this.pushService = new PushNotificationService();
        this.restaurantDashboard = new RestaurantDashboard();
        this.analyticsService = new AnalyticsService();

        initializeMenu();
    }

    private void initializeMenu() {
        MenuCategory pizza = new MenuCategory("Pizza");
        pizza.addMenuItem(new MenuItem("Margherita", "Classic tomato and mozzarella", 12.99));
        pizza.addMenuItem(new MenuItem("Pepperoni", "Pepperoni with cheese", 14.99));
        pizza.addMenuItem(new MenuItem("Quattro Stagioni", "Four seasons pizza", 16.99));
        categories.add(pizza);

        MenuCategory burgers = new MenuCategory("Burgers");
        burgers.addMenuItem(new MenuItem("Cheeseburger", "Beef with cheese", 9.99));
        burgers.addMenuItem(new MenuItem("Chicken Burger", "Grilled chicken", 8.99));
        burgers.addMenuItem(new MenuItem("Veggie Burger", "Plant-based patty", 10.99));
        categories.add(burgers);

        MenuCategory beverages = new MenuCategory("Beverages");
        beverages.addMenuItem(new MenuItem("Coca Cola", "Refreshing cola", 2.99));
        beverages.addMenuItem(new MenuItem("Water", "Bottled water", 1.99));
        beverages.addMenuItem(new MenuItem("Orange Juice", "Fresh squeezed", 4.99));
        categories.add(beverages);
    }

    public void displayMenu() {
        System.out.println("📋 RESTAURANT MENU");
        System.out.println("=".repeat(40));
        for (MenuCategory category : categories) {
            category.displayItems();
        }
        System.out.println("=".repeat(40));
    }

    public MenuItem findMenuItem(String categoryName, int itemIndex) {
        for (MenuCategory category : categories) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                List<MenuItem> items = category.getMenuItems();
                if (itemIndex >= 1 && itemIndex <= items.size()) {
                    return items.get(itemIndex - 1);
                }
            }
        }
        return null;
    }

    public Order.Builder createOrderBuilder(String customerName, String customerPhone) {
        return new Order.Builder(customerName, customerPhone);
    }

    public void setPaymentMethod(PaymentStrategy paymentStrategy) {
        paymentProcessor.setPaymentStrategy(paymentStrategy);
    }

    // Subscribe order to notification services
    public boolean placeOrder(Order order) {
        // Subscribe order to all notification services
        order.addObserver(emailService);
        order.addObserver(smsService);
        order.addObserver(pushService);
        order.addObserver(restaurantDashboard);
        order.addObserver(analyticsService);

        Command command = new PlaceOrderCommand(orderManager, order, paymentProcessor);
        boolean success = commandInvoker.executeCommand(command);
        if (success) {
            order.displaySummary();
        }
        return success;
    }

    public boolean processOrder(String orderId) {
        Command command = new ProcessOrderCommand(orderManager, orderId);
        return commandInvoker.executeCommand(command);
    }

    public boolean cancelOrder(String orderId) {
        Command command = new CancelOrderCommand(orderManager, orderId);
        return commandInvoker.executeCommand(command);
    }

    public boolean undo() {
        return commandInvoker.undo();
    }

    public boolean redo() {
        return commandInvoker.redo();
    }

    public void showCommandHistory() {
        commandInvoker.showHistory();
    }

    public Order getOrder(String orderId) {
        return orderManager.getOrder(orderId);
    }

    public void showAnalytics() {
        analyticsService.printDashboard();
    }
}

public class BasicFoodOrderingSystem {
    public static void main(String[] args) {
        System.out.println("🍕 PHASE 6: OBSERVER PATTERN NOTIFICATION SYSTEM 🍕\n");

        SimpleRestaurant restaurant = new SimpleRestaurant();
        restaurant.displayMenu();

        MenuItem margherita = restaurant.findMenuItem("Pizza", 1);
        MenuItem cheeseburger = restaurant.findMenuItem("Burgers", 1);
        MenuItem cola = restaurant.findMenuItem("Beverages", 1);

        System.out.println("\n🎯 DEMONSTRATING OBSERVER PATTERN FOR NOTIFICATIONS:\n");

        // Example 1: Full notification cycle
        System.out.println("📝 Example 1: Complete Order Lifecycle with Notifications");
        restaurant.setPaymentMethod(new CreditCardPayment("4532123456789012", "12/26", "123", "Alice Johnson"));

        Order order1 = restaurant.createOrderBuilder("Alice Johnson", "+1-555-0001")
                .setEmail("alice@email.com")
                .setDeliveryAddress("123 Observer Street")
                .setSpecialInstructions("Please call when arriving")
                .addItem(margherita, 2, Arrays.asList("Extra cheese"))
                .addItem(cola, 1)
                .build();

        restaurant.placeOrder(order1);

        // Process through states with notifications
        System.out.println("\n🔄 Processing order through states...\n");

        try {
            Thread.sleep(2000);
            restaurant.processOrder(order1.getOrderId()); // PENDING -> CONFIRMED

            Thread.sleep(2000);
            restaurant.processOrder(order1.getOrderId()); // CONFIRMED -> PREPARING

            Thread.sleep(2000);
            restaurant.processOrder(order1.getOrderId()); // PREPARING -> READY

            Thread.sleep(2000);
            restaurant.processOrder(order1.getOrderId()); // READY -> IN_TRANSIT

            Thread.sleep(2000);
            restaurant.processOrder(order1.getOrderId()); // IN_TRANSIT -> DELIVERED
        } catch (InterruptedException e) {
            System.out.println("Demo interrupted");
        }

        // Example 2: Multiple payment methods
        System.out.println("\n📝 Example 2: Different Payment Methods");

        restaurant.setPaymentMethod(new PayPalPayment("bob@paypal.com"));
        Order order2 = restaurant.createOrderBuilder("Bob Smith", "+1-555-0002")
                .setEmail("bob@email.com")
                .addItem(cheeseburger, 1)
                .addItem(cola, 2)
                .build();
        restaurant.placeOrder(order2);

        restaurant.setPaymentMethod(new CashOnDeliveryPayment());
        Order order3 = restaurant.createOrderBuilder("Carol Davis", "+1-555-0003")
                .setEmail("carol@email.com")
                .setDeliveryAddress("789 Cash Avenue")
                .addItem(margherita, 1)
                .build();
        restaurant.placeOrder(order3);

        // Example 3: Cancellation notifications
        System.out.println("\n📝 Example 3: Order Cancellation");
        restaurant.processOrder(order2.getOrderId()); // PENDING -> CONFIRMED
        restaurant.cancelOrder(order2.getOrderId());   // Cancel confirmed order

        // Example 4: Payment failure handling
        System.out.println("\n📝 Example 4: Payment Failure Notifications");
        restaurant.setPaymentMethod(new CreditCardPayment("1111111111111111", "01/25", "000", "Test User"));

        Order failOrder = restaurant.createOrderBuilder("Test User", "+1-555-9999")
                .setEmail("test@email.com")
                .addItem(margherita, 1)
                .build();

        // This might fail due to random failure in CreditCardPayment
        for (int i = 0; i < 3; i++) {
            boolean success = restaurant.placeOrder(failOrder);
            if (success) break;
            System.out.println("Retrying payment...");
        }

        // Show analytics
        restaurant.showAnalytics();

        System.out.println("\n✨ PHASE 6 COMPLETE!");
        System.out.println("=".repeat(60));
        System.out.println("🎉 OBSERVER PATTERN BENEFITS DEMONSTRATED:");
        System.out.println("  ✅ Order class doesn't know about notification details");
        System.out.println("  ✅ Easy to add/remove notification types");
        System.out.println("  ✅ Different notification rules per service");
        System.out.println("  ✅ Loose coupling between order events and notifications");
        System.out.println("  ✅ Multiple notification channels (Email, SMS, Push)");
        System.out.println("  ✅ Internal notifications for restaurant staff");
        System.out.println("  ✅ Analytics tracking without modifying core logic");
        System.out.println("  ✅ Error handling prevents notification failures from breaking orders");
        System.out.println("=".repeat(60));
        System.out.println("\n💭 COMPARE WITH PHASE 5 PROBLEMS:");
        System.out.println("❌ Phase 5: Order status changes were silent");
        System.out.println("❌ Phase 5: No way to notify customers about updates");
        System.out.println("✅ Phase 6: Rich notification system with multiple channels!");
        System.out.println("\n🚀 SYSTEM EVOLUTION COMPLETE!");
        System.out.println("✨ All 6 Design Patterns Working Together Harmoniously!");
    }
}