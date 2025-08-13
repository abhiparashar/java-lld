package prac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Same MenuItem, MenuCategory, CustomerInfo classes (unchanged from Phase 2)
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

// Same OrderItem class (unchanged from Phase 2)
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
// STATE PATTERN IMPLEMENTATION - Each order status is now a proper class
// ============================================================================

abstract class OrderState {
    protected Order order;  // Reference to the order this state belongs to

    public OrderState(Order order) {
        this.order = order;
    }

    // Each state knows how to handle the next step
    public abstract void nextStep();

    // Each state knows its name
    public abstract String getStatusName();

    // Each state knows its own business rules
    public boolean canCancel() { return false; }  // Default: cannot cancel
    public boolean canModify() { return false; }  // Default: cannot modify

    // Handle cancellation
    public void cancel() {
        if (canCancel()) {
            order.setState(new CancelledState(order));
        } else {
            System.out.println("❌ Cannot cancel order in " + getStatusName() + " state");
        }
    }
}

class PendingState extends OrderState {
    public PendingState(Order order) {
        super(order);
    }

    @Override
    public void nextStep() {
        System.out.println("✅ Order confirmed! Moving to preparation...");
        order.setState(new ConfirmedState(order));
    }

    @Override
    public String getStatusName() {
        return "PENDING";
    }

    @Override
    public boolean canCancel() {
        return true;  // Can cancel when pending
    }

    @Override
    public boolean canModify() {
        return true;  // Can modify when pending
    }
}

class ConfirmedState extends OrderState {
    public ConfirmedState(Order order) {
        super(order);
    }

    @Override
    public void nextStep() {
        System.out.println("👨‍🍳 Kitchen started preparing your order...");
        order.setState(new PreparingState(order));
    }

    @Override
    public String getStatusName() {
        return "CONFIRMED";
    }

    @Override
    public boolean canCancel() {
        return true;  // Can still cancel when confirmed
    }

    @Override
    public boolean canModify() {
        return false; // Cannot modify once confirmed
    }
}

class PreparingState extends OrderState {
    public PreparingState(Order order) {
        super(order);
    }

    @Override
    public void nextStep() {
        System.out.println("🍕 Order is ready for pickup/delivery!");
        order.setState(new ReadyState(order));
    }

    @Override
    public String getStatusName() {
        return "PREPARING";
    }

    @Override
    public boolean canCancel() {
        return false; // Cannot cancel when being prepared
    }

    @Override
    public boolean canModify() {
        return false; // Cannot modify when being prepared
    }
}

class ReadyState extends OrderState {
    public ReadyState(Order order) {
        super(order);
    }

    @Override
    public void nextStep() {
        System.out.println("🚚 Order is out for delivery!");
        order.setState(new InTransitState(order));
    }

    @Override
    public String getStatusName() {
        return "READY";
    }

    @Override
    public boolean canCancel() {
        return false; // Cannot cancel when ready
    }

    @Override
    public boolean canModify() {
        return false; // Cannot modify when ready
    }
}

class InTransitState extends OrderState {
    public InTransitState(Order order) {
        super(order);
    }

    @Override
    public void nextStep() {
        System.out.println("🎉 Order delivered successfully!");
        order.setState(new DeliveredState(order));
    }

    @Override
    public String getStatusName() {
        return "IN_TRANSIT";
    }

    @Override
    public boolean canCancel() {
        return false; // Cannot cancel when in transit
    }

    @Override
    public boolean canModify() {
        return false; // Cannot modify when in transit
    }
}

class DeliveredState extends OrderState {
    public DeliveredState(Order order) {
        super(order);
    }

    @Override
    public void nextStep() {
        System.out.println("✨ Order already delivered! No further action needed.");
    }

    @Override
    public String getStatusName() {
        return "DELIVERED";
    }

    @Override
    public boolean canCancel() {
        return false; // Cannot cancel when delivered
    }

    @Override
    public boolean canModify() {
        return false; // Cannot modify when delivered
    }
}

class CancelledState extends OrderState {
    public CancelledState(Order order) {
        super(order);
    }

    @Override
    public void nextStep() {
        System.out.println("❌ Order was cancelled. No further processing possible.");
    }

    @Override
    public String getStatusName() {
        return "CANCELLED";
    }

    @Override
    public boolean canCancel() {
        return false; // Already cancelled
    }

    @Override
    public boolean canModify() {
        return false; // Cannot modify cancelled order
    }
}

// ============================================================================
// ENHANCED ORDER CLASS - Now uses State Pattern instead of String status
// ============================================================================

class Order {
    private final List<OrderItem> orderItems;
    private final CustomerInfo customerInfo;
    private final String email;
    private final String deliveryAddress;
    private final String specialInstructions;
    private OrderState currentState;  // STATE OBJECT instead of String status!

    // Private constructor that accepts Builder
    private Order(Builder builder) {
        this.orderItems = new ArrayList<>(builder.orderItems);
        this.customerInfo = builder.customerInfo;
        this.email = builder.email;
        this.deliveryAddress = builder.deliveryAddress;
        this.specialInstructions = builder.specialInstructions;
        this.currentState = new PendingState(this);  // Start in PENDING state
    }

    // Builder Pattern (same as Phase 2)
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

    // ============================================================================
    // STATE MANAGEMENT METHODS - Delegate to current state
    // ============================================================================

    public void setState(OrderState newState) {
        this.currentState = newState;
        System.out.println("📱 Order status changed to: " + getStatus());
    }

    public void processNextStep() {
        currentState.nextStep();  // Delegate to current state
    }

    public void cancel() {
        currentState.cancel();    // Delegate to current state
    }

    public boolean canCancel() {
        return currentState.canCancel();  // Ask current state
    }

    public boolean canModify() {
        return currentState.canModify();  // Ask current state
    }

    public String getStatus() {
        return currentState.getStatusName();  // Get from current state
    }

    // ============================================================================
    // OTHER ORDER METHODS (unchanged from Phase 2)
    // ============================================================================

    public double getTotalValue() {
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public List<OrderItem> getOrderItems() {
        return new ArrayList<>(orderItems);
    }

    // ENHANCED display showing state capabilities
    public void displaySummary() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ORDER SUMMARY");
        System.out.println("Customer: " + customerInfo.getCustomerName());
        System.out.println("Phone: " + customerInfo.getNumber());

        if (!email.isEmpty()) {
            System.out.println("Email: " + email);
        }

        if (!deliveryAddress.isEmpty()) {
            System.out.println("Delivery Address: " + deliveryAddress);
        }

        // SHOW STATE AND CAPABILITIES
        System.out.println("Status: " + getStatus() +
                " (Can cancel: " + canCancel() +
                ", Can modify: " + canModify() + ")");

        System.out.println("Items:");
        for (OrderItem item : orderItems) {
            System.out.printf("  - %s: $%.2f%n",
                    item.getDescription(), item.getTotalPrice());
        }

        System.out.printf("Total: $%.2f%n", getTotalValue());

        if (!specialInstructions.isEmpty()) {
            System.out.println("Special Instructions: " + specialInstructions);
        }

        System.out.println("=".repeat(50));
    }
}

class OrderSummary {
    private final Order order;

    OrderSummary(Order order) {
        this.order = order;
    }

    public void printSummary() {
        order.displaySummary();
    }
}

// ============================================================================
// UPDATED RESTAURANT CLASS - Now works with State Pattern
// ============================================================================

class SimpleRestaurant {
    private final List<MenuCategory> categories;
    private final List<Order> orders;

    SimpleRestaurant() {
        this.categories = new ArrayList<>();
        this.orders = new ArrayList<>();
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

    public void placeOrder(Order order) {
        orders.add(order);
        System.out.println("✅ Order placed successfully!");
        order.displaySummary();
    }

    // SIMPLIFIED - just delegate to order's state
    public void processOrder(Order order) {
        order.processNextStep();
    }

    // NEW - Handle cancellation through state
    public void cancelOrder(Order order) {
        order.cancel();
    }
}

public class BasicFoodOrderingSystem {
    public static void main(String[] args) {
        System.out.println("🍕 PHASE 3: STATE PATTERN FOOD ORDERING 🍕\n");

        // Create restaurant
        SimpleRestaurant restaurant = new SimpleRestaurant();

        // Display menu
        restaurant.displayMenu();

        // Get menu items
        MenuItem margherita = restaurant.findMenuItem("Pizza", 1);
        MenuItem cheeseburger = restaurant.findMenuItem("Burgers", 1);
        MenuItem cola = restaurant.findMenuItem("Beverages", 1);

        System.out.println("\n🎯 DEMONSTRATING STATE PATTERN BENEFITS:\n");

        // Example 1: Normal order flow
        System.out.println("📝 Example 1: Normal Order Processing");
        Order order1 = restaurant.createOrderBuilder("Alice Smith", "+1-555-0001")
                .setEmail("alice@email.com")
                .setDeliveryAddress("123 State Street")
                .addItem(margherita, 2, Arrays.asList("Extra cheese", "Thin crust"))
                .addItem(cola, 1)
                .build();

        restaurant.placeOrder(order1);

        // Show state transitions
        System.out.println("\n🔄 Processing through states...");
        System.out.println("Current status: " + order1.getStatus() +
                " | Can cancel: " + order1.canCancel());

        restaurant.processOrder(order1); // PENDING -> CONFIRMED
        restaurant.processOrder(order1); // CONFIRMED -> PREPARING

        System.out.println("Current status: " + order1.getStatus() +
                " | Can cancel: " + order1.canCancel());

        restaurant.processOrder(order1); // PREPARING -> READY
        restaurant.processOrder(order1); // READY -> IN_TRANSIT
        restaurant.processOrder(order1); // IN_TRANSIT -> DELIVERED

        // Try to process completed order
        restaurant.processOrder(order1); // Should show "already delivered"

        // Example 2: Cancellation rules
        System.out.println("\n📝 Example 2: Testing Cancellation Rules");
        Order order2 = restaurant.createOrderBuilder("Bob Johnson", "+1-555-0002")
                .addItem(cheeseburger, 1, Arrays.asList("No onions"))
                .addItem(cola, 2)
                .build();

        restaurant.placeOrder(order2);

        System.out.println("\nTrying to cancel PENDING order:");
        restaurant.cancelOrder(order2); // Should work

        System.out.println("\nTrying to cancel CANCELLED order:");
        restaurant.cancelOrder(order2); // Should fail gracefully

        // Example 3: Invalid state transitions prevented
        System.out.println("\n📝 Example 3: State Transition Control");
        Order order3 = restaurant.createOrderBuilder("Charlie Brown", "+1-555-0003")
                .addItem(margherita, 1)
                .build();

        restaurant.placeOrder(order3);
        restaurant.processOrder(order3); // PENDING -> CONFIRMED
        restaurant.processOrder(order3); // CONFIRMED -> PREPARING

        System.out.println("\nTrying to cancel PREPARING order:");
        restaurant.cancelOrder(order3); // Should fail - cannot cancel when preparing

        System.out.println("\n✨ PHASE 3 COMPLETE!");
        System.out.println("=".repeat(60));
        System.out.println("🎉 STATE PATTERN BENEFITS DEMONSTRATED:");
        System.out.println("  ✅ No more string status typos");
        System.out.println("  ✅ Invalid state transitions prevented");
        System.out.println("  ✅ Business rules encapsulated in states");
        System.out.println("  ✅ Easy to add new states or modify rules");
        System.out.println("  ✅ Each state knows its own capabilities");
        System.out.println("  ✅ Clean separation of state-specific behavior");
        System.out.println("=".repeat(60));
        System.out.println("\n💭 COMPARE WITH PHASE 2 PROBLEMS:");
        System.out.println("❌ Phase 2: order.updateStatus(\"DELIVRED\") // Typo!");
        System.out.println("❌ Phase 2: order.updateStatus(\"PENDING\") // Invalid transition!");
        System.out.println("✅ Phase 3: States prevent all these problems!");
        System.out.println("\n🚀 Next Phase: What if we want to undo operations?");
    }
}