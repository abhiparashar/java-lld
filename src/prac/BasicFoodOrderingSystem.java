package prac;

import java.util.ArrayList;
import java.util.List;

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

    // FIX: Add method to add items to category
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
    private final String email;
    private final String address;

    CustomerInfo(String customerName, String number, String email, String address) {
        this.customerName = customerName;
        this.number = number;
        this.email = email;
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }
}

// FIX: OrderItem should represent ONE menu item with quantity
class OrderItem {
    private final MenuItem menuItem;  // Single item, not list
    private final int quantity;

    OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    // FIX: Correct price calculation
    public double getTotalPrice() {
        return menuItem.getAmount() * quantity;
    }

    public String getDescription() {
        return menuItem.getName() + " x" + quantity;
    }
}

// FIX: Order should contain multiple OrderItems
class Order {
    private final List<OrderItem> orderItems;  // List of OrderItems
    private final CustomerInfo customerInfo;
    private String status;  // FIX: Add status tracking

    Order(CustomerInfo customerInfo) {
        this.orderItems = new ArrayList<>();
        this.customerInfo = customerInfo;
        this.status = "PENDING";  // FIX: Initialize status
    }

    // FIX: Method to add items to order
    public void addItem(MenuItem menuItem, int quantity) {
        orderItems.add(new OrderItem(menuItem, quantity));
    }

    // FIX: Correct total calculation
    public double getTotalValue() {
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    // FIX: Status management
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

    // FIX: Display order summary
    public void displaySummary() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("ORDER SUMMARY");
        System.out.println("Customer: " + customerInfo.getCustomerName());
        System.out.println("Phone: " + customerInfo.getNumber());
        System.out.println("Status: " + status);
        System.out.println("Items:");
        for (OrderItem item : orderItems) {
            System.out.printf("  - %s: $%.2f%n",
                    item.getDescription(), item.getTotalPrice());
        }
        System.out.printf("Total: $%.2f%n", getTotalValue());
        System.out.println("=".repeat(40));
    }
}

// FIX: Make OrderSummary functional or remove it
class OrderSummary {
    private final Order order;

    OrderSummary(Order order) {
        this.order = order;
    }

    public void printSummary() {
        order.displaySummary();
    }
}

// FIX: Create a working restaurant system
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

    public Order createOrder(CustomerInfo customer) {
        return new Order(customer);
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

public class BasicFoodOrderingSystem {
    public static void main(String[] args) {
        System.out.println("🍕 BASIC FOOD ORDERING SYSTEM - PHASE 1 🍕\n");

        // Create restaurant
        SimpleRestaurant restaurant = new SimpleRestaurant();

        // Display menu
        restaurant.displayMenu();

        // Create customer
        CustomerInfo customer = new CustomerInfo(
                "John Doe",
                "+1-555-0123",
                "john@email.com",
                "123 Main St"
        );

        // Create order
        Order order = restaurant.createOrder(customer);

        // Add items to order
        MenuItem pizza = restaurant.findMenuItem("Pizza", 1); // Margherita
        MenuItem burger = restaurant.findMenuItem("Burgers", 1); // Cheeseburger
        MenuItem drink = restaurant.findMenuItem("Beverages", 1); // Coca Cola

        if (pizza != null) order.addItem(pizza, 2);
        if (burger != null) order.addItem(burger, 1);
        if (drink != null) order.addItem(drink, 3);

        // Place order
        restaurant.placeOrder(order);

        // Process order through stages
        System.out.println("\n🔄 Processing order...\n");
        restaurant.processOrder(order); // PENDING -> CONFIRMED
        restaurant.processOrder(order); // CONFIRMED -> PREPARING
        restaurant.processOrder(order); // PREPARING -> READY
        restaurant.processOrder(order); // READY -> DELIVERED

        System.out.println("\n✨ Phase 1 Complete! Basic system working!");
        System.out.println("✅ Can create orders");
        System.out.println("✅ Can add menu items");
        System.out.println("✅ Can calculate totals");
        System.out.println("✅ Can track status");
        System.out.println("✅ Can process orders");
    }
}