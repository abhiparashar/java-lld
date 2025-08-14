package prac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Same MenuItem, MenuCategory, CustomerInfo classes (unchanged from Phase 4)
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

// Same OrderItem class (unchanged from Phase 4)
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
// STATE PATTERN CLASSES (same as Phase 4)
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
// STRATEGY PATTERN IMPLEMENTATION - Payment Methods
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
        System.out.printf("   Card: ****-****-****-%s | Holder: %s%n",
                cardNumber.substring(Math.max(0, cardNumber.length() - 4)), cardholderName);

        // Simulate payment processing delay
        try { Thread.sleep(800); } catch (InterruptedException e) {}

        // Simulate occasional payment failure (5% chance)
        if (Math.random() < 0.05) {
            return new PaymentResult(false, null, "Card declined - Insufficient funds", "Credit Card");
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
        System.out.printf("   PayPal Account: %s%n", email);

        // Simulate PayPal authentication and processing
        try { Thread.sleep(1200); } catch (InterruptedException e) {}

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
        System.out.println("   Driver will collect payment upon delivery");

        String transactionId = "COD-" + System.currentTimeMillis();
        return new PaymentResult(true, transactionId, "Cash on delivery confirmed", "Cash on Delivery");
    }

    @Override
    public String getPaymentType() {
        return "Cash on Delivery";
    }
}

class ApplePayPayment implements PaymentStrategy {
    private final String deviceId;

    public ApplePayPayment(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public PaymentResult processPayment(double amount, String orderId) {
        System.out.printf("📱 Processing Apple Pay payment of $%.2f%n", amount);
        System.out.printf("   Device: %s | Touch ID/Face ID verified%n", deviceId);

        // Simulate biometric verification
        try { Thread.sleep(600); } catch (InterruptedException e) {}

        String transactionId = "AP-" + System.currentTimeMillis();
        return new PaymentResult(true, transactionId, "Apple Pay payment successful", "Apple Pay");
    }

    @Override
    public String getPaymentType() {
        return "Apple Pay";
    }
}

class GooglePayPayment implements PaymentStrategy {
    private final String accountId;

    public GooglePayPayment(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public PaymentResult processPayment(double amount, String orderId) {
        System.out.printf("🔵 Processing Google Pay payment of $%.2f%n", amount);
        System.out.printf("   Account: %s | Payment authorized%n", accountId);

        try { Thread.sleep(700); } catch (InterruptedException e) {}

        String transactionId = "GP-" + System.currentTimeMillis();
        return new PaymentResult(true, transactionId, "Google Pay payment successful", "Google Pay");
    }

    @Override
    public String getPaymentType() {
        return "Google Pay";
    }
}

// Payment Processor using Strategy Pattern
class PaymentProcessor {
    private PaymentStrategy currentStrategy;

    public PaymentProcessor() {
        // Default strategy
        this.currentStrategy = new CashOnDeliveryPayment();
    }

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.currentStrategy = strategy;
        System.out.println("💰 Payment method set to: " + strategy.getPaymentType());
    }

    public PaymentResult processPayment(double amount, String orderId) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PROCESSING PAYMENT");
        System.out.println("=".repeat(50));

        PaymentResult result = currentStrategy.processPayment(amount, orderId);

        if (result.isSuccess()) {
            System.out.println("✅ " + result);
        } else {
            System.out.println("❌ " + result);
        }

        System.out.println("=".repeat(50));
        return result;
    }

    public String getCurrentPaymentType() {
        return currentStrategy.getPaymentType();
    }
}

// ============================================================================
// ENHANCED ORDER CLASS - Now includes payment information
// ============================================================================

class Order {
    private final String orderId;
    private final List<OrderItem> orderItems;
    private final CustomerInfo customerInfo;
    private final String email;
    private final String deliveryAddress;
    private final String specialInstructions;
    private OrderState currentState;
    private PaymentResult paymentResult;  // NEW: Track payment info

    private Order(Builder builder) {
        this.orderId = generateOrderId();
        this.orderItems = new ArrayList<>(builder.orderItems);
        this.customerInfo = builder.customerInfo;
        this.email = builder.email;
        this.deliveryAddress = builder.deliveryAddress;
        this.specialInstructions = builder.specialInstructions;
        this.currentState = new PendingState(this);
        this.paymentResult = null;  // Will be set when payment is processed
    }

    private String generateOrderId() {
        return "ORD-" + System.currentTimeMillis() + "-" +
                Integer.toHexString(hashCode()).toUpperCase().substring(0, 4);
    }

    // Builder Pattern (same as Phase 4)
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

    // State management methods (same as Phase 4)
    public void setState(OrderState newState) {
        this.currentState = newState;
        System.out.println("📱 Order " + orderId + " status changed to: " + getStatus());
    }

    public void processNextStep() {
        currentState.nextStep();
    }

    public void cancel() {
        currentState.cancel();
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

    // NEW: Payment methods
    public void setPaymentResult(PaymentResult result) {
        this.paymentResult = result;
    }

    public boolean isPaid() {
        return paymentResult != null && paymentResult.isSuccess();
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

        // NEW: Show payment information
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
    public List<OrderItem> getOrderItems() { return new ArrayList<>(orderItems); }
    public OrderState getCurrentState() { return currentState; }
    public PaymentResult getPaymentResult() { return paymentResult; }
}

// ============================================================================
// UPDATED COMMAND PATTERN - Now includes payment processing
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
        System.out.println("↩️ Undone: Order " + order.getOrderId() + " removed (payment would be refunded)");
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
        System.out.println("❌ Failed: Order " + orderId + " not found");
        return false;
    }

    @Override
    public void undo() {
        Order order = orderManager.getOrder(orderId);
        if (order != null && previousState != null) {
            order.setState(previousState);
            System.out.println("↩️ Undone: Order " + orderId + " status reverted to " + previousState.getStatusName());
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
        System.out.println("❌ Failed: Cannot cancel order " + orderId);
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

// Same CommandInvoker class (unchanged from Phase 4)
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

// Same OrderManager class (unchanged from Phase 4)
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

    public void displayAllOrders() {
        System.out.println("\n📊 ALL ORDERS:");
        System.out.println("=".repeat(70));

        if (orders.isEmpty()) {
            System.out.println("No orders found");
        } else {
            for (Order order : orders) {
                String paymentInfo = order.isPaid() ? order.getPaymentResult().getPaymentMethod() : "No Payment";
                System.out.printf("Order %s | %s | Status: %s | Total: $%.2f | Payment: %s%n",
                        order.getOrderId(),
                        order.getCustomerInfo().getCustomerName(),
                        order.getStatus(),
                        order.getTotalValue(),
                        paymentInfo);
            }
        }

        System.out.println("=".repeat(70));
    }
}

// ============================================================================
// UPDATED RESTAURANT CLASS - Now with Payment Strategy
// ============================================================================

class SimpleRestaurant {
    private final List<MenuCategory> categories;
    private final OrderManager orderManager;
    private final CommandInvoker commandInvoker;
    private final PaymentProcessor paymentProcessor;  // NEW: Payment processor

    SimpleRestaurant() {
        this.categories = new ArrayList<>();
        this.orderManager = new OrderManager();
        this.commandInvoker = new CommandInvoker();
        this.paymentProcessor = new PaymentProcessor();  // NEW: Initialize with default
        initializeMenu();
    }

    private void initializeMenu() {
        // Create Pizza category
        MenuCategory pizza = new MenuCategory("Pizza");
        pizza.addMenuItem(new MenuItem("Margherita", "Classic tomato and mozzarella", 12.99));
        pizza.addMenuItem(new MenuItem("Pepperoni", "Pepperoni with cheese", 14.99));
        pizza.addMenuItem(new MenuItem("Quattro Stagioni", "Four seasons pizza", 16.99));
        categories.add(pizza);

        // Create Burger category
        MenuCategory burgers = new MenuCategory("Burgers");
        burgers.addMenuItem(new MenuItem("Cheeseburger", "Beef with cheese", 9.99));
        burgers.addMenuItem(new MenuItem("Chicken Burger", "Grilled chicken", 8.99));
        burgers.addMenuItem(new MenuItem("Veggie Burger", "Plant-based patty", 10.99));
        categories.add(burgers);

        // Create Beverages category
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

    // NEW: Set payment method before placing orders
    public void setPaymentMethod(PaymentStrategy paymentStrategy) {
        paymentProcessor.setPaymentStrategy(paymentStrategy);
    }

    // Updated to use payment processor
    public boolean placeOrder(Order order) {
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

    public void showAllOrders() {
        orderManager.displayAllOrders();
    }

    public Order getOrder(String orderId) {
        return orderManager.getOrder(orderId);
    }

    public String getCurrentPaymentMethod() {
        return paymentProcessor.getCurrentPaymentType();
    }
}

public class BasicFoodOrderingSystem {
    public static void main(String[] args) {
        System.out.println("🍕 PHASE 5: STRATEGY PATTERN PAYMENT SYSTEM 🍕\n");

        // Create restaurant
        SimpleRestaurant restaurant = new SimpleRestaurant();

        // Display menu
        restaurant.displayMenu();

        // Get menu items
        MenuItem margherita = restaurant.findMenuItem("Pizza", 1);
        MenuItem cheeseburger = restaurant.findMenuItem("Burgers", 1);
        MenuItem cola = restaurant.findMenuItem("Beverages", 1);

        System.out.println("\n🎯 DEMONSTRATING STRATEGY PATTERN FOR PAYMENTS:\n");

        // Example 1: Credit Card Payment
        System.out.println("📝 Example 1: Credit Card Payment");
        restaurant.setPaymentMethod(new CreditCardPayment("4532123456789012", "12/26", "123", "Alice Johnson"));

        Order order1 = restaurant.createOrderBuilder("Alice Johnson", "+1-555-0001")
                .setEmail("alice@email.com")
                .setDeliveryAddress("123 Strategy Street")
                .addItem(margherita, 2, Arrays.asList("Extra cheese"))
                .addItem(cola, 1)
                .build();

        restaurant.placeOrder(order1);

        // Example 2: PayPal Payment
        System.out.println("\n📝 Example 2: PayPal Payment");
        restaurant.setPaymentMethod(new PayPalPayment("bob.smith@paypal.com"));

        Order order2 = restaurant.createOrderBuilder("Bob Smith", "+1-555-0002")
                .setEmail("bob@email.com")
                .addItem(cheeseburger, 1, Arrays.asList("No onions"))
                .addItem(cola, 2)
                .build();

        restaurant.placeOrder(order2);

        // Example 3: Cash on Delivery
        System.out.println("\n📝 Example 3: Cash on Delivery");
        restaurant.setPaymentMethod(new CashOnDeliveryPayment());

        Order order3 = restaurant.createOrderBuilder("Carol Davis", "+1-555-0003")
                .setDeliveryAddress("789 Cash Avenue")
                .addItem(margherita, 1)
                .addItem(cheeseburger, 1)
                .build();

        restaurant.placeOrder(order3);

        // Example 4: Apple Pay
        System.out.println("\n📝 Example 4: Apple Pay");
        restaurant.setPaymentMethod(new ApplePayPayment("iPhone-13-Pro"));

        Order order4 = restaurant.createOrderBuilder("David Wilson", "+1-555-0004")
                .addItem(margherita, 1, Arrays.asList("Thin crust"))
                .addItem(cola, 3)
                .build();

        restaurant.placeOrder(order4);

        // Example 5: Google Pay
        System.out.println("\n📝 Example 5: Google Pay");
        restaurant.setPaymentMethod(new GooglePayPayment("user@gmail.com"));

        Order order5 = restaurant.createOrderBuilder("Emma Brown", "+1-555-0005")
                .setEmail("emma@email.com")
                .addItem(cheeseburger, 2)
                .build();

        restaurant.placeOrder(order5);

        // Show all orders with payment information
        restaurant.showAllOrders();

        // Show command history with payment methods
        restaurant.showCommandHistory();

        // Demonstrate payment failure handling
        System.out.println("\n📝 Example 6: Testing Payment Failure");
        restaurant.setPaymentMethod(new CreditCardPayment("1111111111111111", "01/25", "000", "Test User"));

        Order failOrder = restaurant.createOrderBuilder("Test User", "+1-555-9999")
                .addItem(margherita, 1)
                .build();

        // This might fail due to the 5% random failure chance in CreditCardPayment
        boolean success = restaurant.placeOrder(failOrder);
        if (!success) {
            System.out.println("🔄 Retrying with different payment method...");
            restaurant.setPaymentMethod(new CashOnDeliveryPayment());
            restaurant.placeOrder(failOrder);
        }

        // Process some orders to show the system working
        System.out.println("\n🔄 Processing some orders...\n");
        if (order1.isPaid()) {
            restaurant.processOrder(order1.getOrderId());
            restaurant.processOrder(order1.getOrderId());
        }

        if (order2.isPaid()) {
            restaurant.processOrder(order2.getOrderId());
        }

        // Final status
        restaurant.showAllOrders();

        System.out.println("\n✨ PHASE 5 COMPLETE!");
        System.out.println("=".repeat(60));
        System.out.println("🎉 STRATEGY PATTERN BENEFITS DEMONSTRATED:");
        System.out.println("  ✅ Multiple payment methods without changing Order class");
        System.out.println("  ✅ Easy to add new payment types (Bitcoin, Bank Transfer, etc.)");
        System.out.println("  ✅ Payment logic encapsulated in strategy classes");
        System.out.println("  ✅ Runtime switching between payment methods");
        System.out.println("  ✅ Payment-specific behavior handled properly");
        System.out.println("  ✅ Payment failures handled gracefully");
        System.out.println("=".repeat(60));
        System.out.println("\n💭 COMPARE WITH PHASE 4 PROBLEMS:");
        System.out.println("❌ Phase 4: Payment hardcoded, no choice for customers");
        System.out.println("❌ Phase 4: Adding new payment = modifying existing code");
        System.out.println("✅ Phase 5: Multiple payment options, easy to extend!");
        System.out.println("\n🚀 Next Phase: Order status changes are silent...");
        System.out.println("💭 Customers want notifications when their order status changes!");
    }
}