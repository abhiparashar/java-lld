package prac;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Your MenuItem, MenuCategory, CustomerInfo classes are PERFECT - no changes needed!

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

// FIX #1: Update OrderItem's getDescription() to show customizations
class OrderItem {
    private final MenuItem menuItem;
    private final int quantity;
    private final List<String> customizations;

    OrderItem(MenuItem menuItem, int quantity, List<String> customizations) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.customizations = new ArrayList<>(customizations); // Defensive copy
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

    // FIX #1: Show customizations in description
    public String getDescription() {
        StringBuilder desc = new StringBuilder(menuItem.getName() + " x" + quantity);
        if (!customizations.isEmpty()) {
            desc.append(" (").append(String.join(", ", customizations)).append(")");
        }
        return desc.toString();
    }
}

// FIX #2: MAJOR RESTRUCTURING - Move Builder INSIDE Order class
class Order {
    private final List<OrderItem> orderItems;
    private final CustomerInfo customerInfo;
    private final String email;              // NEW
    private final String deliveryAddress;    // NEW
    private final String specialInstructions; // NEW
    private String status;

    // FIX #2: PRIVATE constructor that accepts Builder
    private Order(Builder builder) {
        this.orderItems = new ArrayList<>(builder.orderItems);
        this.customerInfo = builder.customerInfo;
        this.email = builder.email;
        this.deliveryAddress = builder.deliveryAddress;
        this.specialInstructions = builder.specialInstructions;
        this.status = "PENDING";
    }

    // FIX #2: Builder is NESTED INSIDE Order class
    public static class Builder {
        // Required fields
        private CustomerInfo customerInfo;

        // Optional fields
        private List<OrderItem> orderItems = new ArrayList<>();
        private String email = "";
        private String deliveryAddress = "";
        private String specialInstructions = "";

        // Constructor for required fields
        public Builder(String customerName, String customerPhone) {
            this.customerInfo = new CustomerInfo(customerName, customerPhone);
        }

        // FIX #3: Fluent methods that return this
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

        // FIX #3: addItem that ACTUALLY stores the item
        public Builder addItem(MenuItem item, int quantity, List<String> customizations) {
            this.orderItems.add(new OrderItem(item, quantity, customizations));
            return this;
        }

        // Convenience method for no customizations
        public Builder addItem(MenuItem item, int quantity) {
            return addItem(item, quantity, new ArrayList<>());
        }

        // FIX #4: build() creates ORDER, not OrderBuilder!
        public Order build() {
            // FIX #5: Add validation
            if (orderItems.isEmpty()) {
                throw new IllegalStateException("Order must contain at least one item");
            }
            if (customerInfo.getCustomerName() == null || customerInfo.getCustomerName().trim().isEmpty()) {
                throw new IllegalStateException("Customer name is required");
            }

            return new Order(this); // Create ORDER, not OrderBuilder!
        }
    }

    // Rest of Order methods
    public double getTotalValue() {
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("📱 Order status updated to: " + newStatus);
    }

    public String getStatus() {
        return status;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public List<OrderItem> getOrderItems() {
        return new ArrayList<>(orderItems);
    }

    // FIX #6: Enhanced display showing new fields
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

        System.out.println("Status: " + status);
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

// FIX #7: Update SimpleRestaurant to use Builder
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
        categories.add(pizza);

        // Create Burger category
        MenuCategory burgers = new MenuCategory("Burgers");
        burgers.addMenuItem(new MenuItem("Cheeseburger", "Beef with cheese", 9.99));
        burgers.addMenuItem(new MenuItem("Chicken Burger", "Grilled chicken", 8.99));
        categories.add(burgers);

        // Create Beverages category
        MenuCategory beverages = new MenuCategory("Beverages");
        beverages.addMenuItem(new MenuItem("Coca Cola", "Refreshing cola", 2.99));
        beverages.addMenuItem(new MenuItem("Water", "Bottled water", 1.99));
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

    // FIX #7: Return Builder instead of Order
    public Order.Builder createOrderBuilder(String customerName, String customerPhone) {
        return new Order.Builder(customerName, customerPhone);
    }

    public void placeOrder(Order order) {
        orders.add(order);
        System.out.println("✅ Order placed successfully!");
        order.displaySummary();
    }

    public void processOrder(Order order) {
        String currentStatus = order.getStatus();
        switch (currentStatus) {
            case "PENDING":
                order.updateStatus("CONFIRMED");
                break;
            case "CONFIRMED":
                order.updateStatus("PREPARING");
                break;
            case "PREPARING":
                order.updateStatus("READY");
                break;
            case "READY":
                order.updateStatus("DELIVERED");
                break;
            default:
                System.out.println("Order already completed");
        }
    }
}

// DELETE YOUR SEPARATE OrderBuilder CLASS - IT'S NOW INSIDE Order!

public class BasicFoodOrderingSystem {
    public static void main(String[] args) {
        System.out.println("🍕 PHASE 2: FIXED BUILDER PATTERN 🍕\n");

        // Create restaurant
        SimpleRestaurant restaurant = new SimpleRestaurant();

        // Display menu
        restaurant.displayMenu();

        // Get menu items
        MenuItem margherita = restaurant.findMenuItem("Pizza", 1);
        MenuItem cheeseburger = restaurant.findMenuItem("Burgers", 1);
        MenuItem cola = restaurant.findMenuItem("Beverages", 1);

        System.out.println("\n🎯 DEMONSTRATING FIXED BUILDER PATTERN:\n");

        // FIX #8: Working demo using Builder properly
        System.out.println("📝 Example 1: Simple Order");
        Order simpleOrder = restaurant.createOrderBuilder("Alice Smith", "+1-555-0001")
                .addItem(margherita, 1)
                .addItem(cola, 2)
                .build();

        restaurant.placeOrder(simpleOrder);

        System.out.println("\n📝 Example 2: Complex Order with All Features");
        Order complexOrder = restaurant.createOrderBuilder("Bob Johnson", "+1-555-0002")
                .setEmail("bob.johnson@email.com")
                .setDeliveryAddress("789 Work Avenue, Office Building")
                .setSpecialInstructions("Please call when you arrive, ring doorbell twice")
                .addItem(margherita, 2, Arrays.asList("Extra cheese", "Thin crust"))
                .addItem(cheeseburger, 1, Arrays.asList("No onions", "Extra pickles"))
                .addItem(cola, 3)
                .build();

        restaurant.placeOrder(complexOrder);

        // Test validation
        System.out.println("\n📝 Example 3: Testing Validation");
        try {
            Order invalidOrder = restaurant.createOrderBuilder("", "+1-555-0003")
                    .build(); // Should fail - no name and no items
        } catch (IllegalStateException e) {
            System.out.println("❌ Validation caught error: " + e.getMessage());
        }

        System.out.println("\n✨ ALL FIXES APPLIED SUCCESSFULLY!");
        System.out.println("=".repeat(60));
        System.out.println("🔧 WHAT WAS FIXED:");
        System.out.println("  ✅ Builder moved INSIDE Order class");
        System.out.println("  ✅ build() creates Order, not OrderBuilder");
        System.out.println("  ✅ addItem() actually stores items");
        System.out.println("  ✅ Customizations display properly");
        System.out.println("  ✅ Added validation");
        System.out.println("  ✅ Working demo showing all features");
        System.out.println("=".repeat(60));
    }
}