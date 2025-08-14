package prac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Same MenuItem, MenuCategory, CustomerInfo classes (unchanged from Phase 3)
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

// Same OrderItem class (unchanged from Phase 3)
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
// STATE PATTERN CLASSES (same as Phase 3)
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
// ORDER CLASS (same as Phase 3, but with ID generation for tracking)
// ============================================================================

class Order {
    private final String orderId;  // NEW: ID for tracking
    private final List<OrderItem> orderItems;
    private final CustomerInfo customerInfo;
    private final String email;
    private final String deliveryAddress;
    private final String specialInstructions;
    private OrderState currentState;

    private Order(Builder builder) {
        this.orderId = generateOrderId();  // NEW: Generate unique ID
        this.orderItems = new ArrayList<>(builder.orderItems);
        this.customerInfo = builder.customerInfo;
        this.email = builder.email;
        this.deliveryAddress = builder.deliveryAddress;
        this.specialInstructions = builder.specialInstructions;
        this.currentState = new PendingState(this);
    }

    // NEW: Generate unique order ID
    private String generateOrderId() {
        return "ORD-" + System.currentTimeMillis() + "-" +
                Integer.toHexString(hashCode()).toUpperCase().substring(0, 4);
    }

    // Builder Pattern (same as Phase 3)
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

    // State management methods (same as Phase 3)
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

        if (!specialInstructions.isEmpty()) {
            System.out.println("Special Instructions: " + specialInstructions);
        }

        System.out.println("=".repeat(50));
    }

    // NEW: Getters for Command pattern
    public String getOrderId() { return orderId; }
    public CustomerInfo getCustomerInfo() { return customerInfo; }
    public List<OrderItem> getOrderItems() { return new ArrayList<>(orderItems); }
    public OrderState getCurrentState() { return currentState; }  // For commands to save state
}

// ============================================================================
// COMMAND PATTERN IMPLEMENTATION - The new functionality!
// ============================================================================

interface Command {
    boolean execute();  // Perform the operation
    void undo();        // Reverse the operation
    String getDescription(); // For logging/history display
}

class PlaceOrderCommand implements Command {
    private final OrderManager orderManager;
    private final Order order;

    public PlaceOrderCommand(OrderManager orderManager, Order order) {
        this.orderManager = orderManager;
        this.order = order;
    }

    @Override
    public boolean execute() {
        orderManager.addOrderDirect(order);
        System.out.println("✅ Executed: Order " + order.getOrderId() + " placed successfully");
        return true;
    }

    @Override
    public void undo() {
        orderManager.removeOrderDirect(order.getOrderId());
        System.out.println("↩️ Undone: Order " + order.getOrderId() + " removed");
    }

    @Override
    public String getDescription() {
        return "Place Order " + order.getOrderId() + " (" + order.getCustomerInfo().getCustomerName() + ")";
    }
}

class ProcessOrderCommand implements Command {
    private final OrderManager orderManager;
    private final String orderId;
    private OrderState previousState;  // Store previous state for undo

    public ProcessOrderCommand(OrderManager orderManager, String orderId) {
        this.orderManager = orderManager;
        this.orderId = orderId;
    }

    @Override
    public boolean execute() {
        Order order = orderManager.getOrder(orderId);
        if (order != null) {
            // Save current state for undo
            previousState = order.getCurrentState();

            // Process to next step
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
    private OrderState previousState;  // Store state before cancellation

    public CancelOrderCommand(OrderManager orderManager, String orderId) {
        this.orderManager = orderManager;
        this.orderId = orderId;
    }

    @Override
    public boolean execute() {
        Order order = orderManager.getOrder(orderId);
        if (order != null && order.canCancel()) {
            // Save current state for undo
            previousState = order.getCurrentState();

            // Cancel the order
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

// ============================================================================
// COMMAND INVOKER - Manages command history and undo/redo
// ============================================================================

class CommandInvoker {
    private final List<Command> history = new ArrayList<>();
    private int currentPosition = -1;  // Current position in history

    public boolean executeCommand(Command command) {
        // Remove any commands after current position (for redo functionality)
        if (currentPosition < history.size() - 1) {
            history.subList(currentPosition + 1, history.size()).clear();
        }

        // Execute the command
        boolean success = command.execute();

        if (success) {
            // Add to history and move position
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
        System.out.printf("Position: %d/%d | Can Undo: %s | Can Redo: %s%n",
                currentPosition + 1, history.size(),
                (currentPosition >= 0),
                (currentPosition < history.size() - 1));
        System.out.println("=".repeat(60));
    }
}

// ============================================================================
// ORDER MANAGER - Manages orders and works with commands
// ============================================================================

class OrderManager {
    private final List<Order> orders = new ArrayList<>();

    // Direct methods used by commands
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
        System.out.println("=".repeat(60));

        if (orders.isEmpty()) {
            System.out.println("No orders found");
        } else {
            for (Order order : orders) {
                System.out.printf("Order %s | %s | Status: %s | Total: $%.2f%n",
                        order.getOrderId(),
                        order.getCustomerInfo().getCustomerName(),
                        order.getStatus(),
                        order.getTotalValue());
            }
        }

        System.out.println("=".repeat(60));
    }
}

// ============================================================================
// UPDATED RESTAURANT CLASS - Now uses Command Pattern
// ============================================================================

class SimpleRestaurant {
    private final List<MenuCategory> categories;
    private final OrderManager orderManager;
    private final CommandInvoker commandInvoker;

    SimpleRestaurant() {
        this.categories = new ArrayList<>();
        this.orderManager = new OrderManager();
        this.commandInvoker = new CommandInvoker();
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

    // NEW: All operations now use Command Pattern
    public boolean placeOrder(Order order) {
        Command command = new PlaceOrderCommand(orderManager, order);
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

    // NEW: Undo/Redo functionality
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
}

public class BasicFoodOrderingSystem {
    public static void main(String[] args) {
        System.out.println("🍕 PHASE 4: COMMAND PATTERN FOOD ORDERING 🍕\n");

        // Create restaurant
        SimpleRestaurant restaurant = new SimpleRestaurant();

        // Display menu
        restaurant.displayMenu();

        // Get menu items
        MenuItem margherita = restaurant.findMenuItem("Pizza", 1);
        MenuItem cheeseburger = restaurant.findMenuItem("Burgers", 1);
        MenuItem cola = restaurant.findMenuItem("Beverages", 1);

        System.out.println("\n🎯 DEMONSTRATING COMMAND PATTERN BENEFITS:\n");

        // Example 1: Place orders and show history
        System.out.println("📝 Example 1: Placing Orders");

        Order order1 = restaurant.createOrderBuilder("Alice Smith", "+1-555-0001")
                .setEmail("alice@email.com")
                .setDeliveryAddress("123 Command Street")
                .addItem(margherita, 2, Arrays.asList("Extra cheese"))
                .addItem(cola, 1)
                .build();

        Order order2 = restaurant.createOrderBuilder("Bob Johnson", "+1-555-0002")
                .addItem(cheeseburger, 1, Arrays.asList("No onions"))
                .addItem(cola, 2)
                .build();

        restaurant.placeOrder(order1);
        restaurant.placeOrder(order2);

        restaurant.showCommandHistory();

        // Example 2: Process orders and show undo functionality
        System.out.println("\n📝 Example 2: Processing Orders and Undo");

        restaurant.processOrder(order1.getOrderId()); // PENDING -> CONFIRMED
        restaurant.processOrder(order1.getOrderId()); // CONFIRMED -> PREPARING
        restaurant.processOrder(order2.getOrderId()); // PENDING -> CONFIRMED

        restaurant.showCommandHistory();

        System.out.println("\n↩️ Undoing last command...");
        restaurant.undo(); // Undo Bob's order processing

        restaurant.showCommandHistory();

        // Example 3: Cancellation and undo
        System.out.println("\n📝 Example 3: Cancellation and Undo");

        restaurant.cancelOrder(order2.getOrderId()); // Cancel Bob's order
        restaurant.showAllOrders();

        System.out.println("\n↩️ Undoing cancellation...");
        restaurant.undo(); // Undo the cancellation

        restaurant.showAllOrders();

        // Example 4: Multiple undo/redo operations
        System.out.println("\n📝 Example 4: Multiple Undo/Redo Operations");

        restaurant.showCommandHistory();

        System.out.println("\n↩️ Undoing multiple commands...");
        restaurant.undo();
        restaurant.undo();
        restaurant.undo();

        restaurant.showCommandHistory();

        System.out.println("\n↪️ Redoing commands...");
        restaurant.redo();
        restaurant.redo();

        restaurant.showCommandHistory();

        System.out.println("\n✨ PHASE 4 COMPLETE!");
        System.out.println("=".repeat(60));
        System.out.println("🎉 COMMAND PATTERN BENEFITS DEMONSTRATED:");
        System.out.println("  ✅ Operations can be undone and redone");
        System.out.println("  ✅ Complete command history tracking");
        System.out.println("  ✅ Operations are encapsulated in command objects");
        System.out.println("  ✅ Easy to add new operations without changing existing code");
        System.out.println("  ✅ Support for macro commands (batch operations)");
        System.out.println("  ✅ Audit trail of all operations performed");
        System.out.println("=".repeat(60));
        System.out.println("\n💭 COMPARE WITH PHASE 3 PROBLEMS:");
        System.out.println("❌ Phase 3: restaurant.placeOrder(order) // No undo!");
        System.out.println("❌ Phase 3: restaurant.processOrder(order) // Irreversible!");
        System.out.println("✅ Phase 4: Can undo any operation!");
        System.out.println("\n🚀 Next Phase: Payment methods are hardcoded...");
        System.out.println("💭 What if customers want different payment options?");
    }
}