package projects.restaurant;

import java.util.*;

class MenuItem{
    private final int id;
    private final String name;
    private final String description;
    private final double price;
    private final String category;

    MenuItem(int id, String name, String description, double price, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getInfo() {
        return String.format("%s - $%.2f (%s)", name, price, category);
    }

    @Override
    public String toString() {
        return String.format("MenuItem{id=%d, name='%s', price=%.2f, category='%s'}",
                id, name, price, category);
    }
}

class MenuCategory{
    private final List<MenuItem> menuItems;
    private final String name;

    MenuCategory(String name) {
        this.menuItems = new ArrayList<>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void addItem(MenuItem menuItem){
        menuItems.add(menuItem);
    }
}

class MenuManager{
    private final Map<Integer, MenuItem>allItems;
    private final Map<String, MenuCategory>categories;

    MenuManager(Map<String, MenuItem> allItems, Map<String, MenuCategory> categories) {
        this.allItems = new HashMap<>();
        this.categories = new HashMap<>();
    }

    public void addCategory(String categoryName){
        categories.putIfAbsent(categoryName, new MenuCategory(categoryName));
    }

    public void addMenuItem(MenuItem item){
        addCategory(item.getCategory());
        categories.get(item.getCategory()).addItem(item);
        allItems.put(item.getId(), item);
    }

    public MenuItem  getItemById(int id){
       return allItems.get(id);
    }

    public List<MenuItem> getItemsByCategory(String categoryName){
        MenuCategory menuCategory = categories.get(categoryName);
        return menuCategory != null ? menuCategory.getMenuItems() : new ArrayList<>();
    }

    public Set<String> getAllCategories() {
        return new HashSet<>(categories.keySet());
    }
}

class OrderItem{
    private final MenuItem menuItem;
    private final int quantity;
    private final List<String>customizations;

    OrderItem(MenuItem menuItem, int quantity, List<String> customizations) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.customizations = new ArrayList<>();
    }

    public int getPrice(){
        return (int) (menuItem.getPrice() * quantity);
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public String getDescription() {
        StringBuilder desc = new StringBuilder(menuItem.getName() + " x" + quantity);
        if (!customizations.isEmpty()) {
            desc.append(" (").append(String.join(", ", customizations)).append(")");
        }
        return desc.toString();
    }

    public int getQuantity() {
        return quantity;
    }

    public List<String> getCustomizations() {
        return new ArrayList<>(customizations);
    }
}

class CustomerInfo{
    private final String name;
    private final String phoneNumber;
    private final String email;

    CustomerInfo(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return String.format("CustomerInfo{name='%s', phone='%s', email='%s'}", name, phoneNumber, email);
    }
}

// Builder Pattern Implementation
class OrderBuilder  {
    private final List<OrderItem> orderItems;
    private final CustomerInfo customerInfo;
    private final String deliveryAddress;
    private final String specialInstructions;

    private OrderBuilder (Builder builder) {
        this.orderItems = new ArrayList<>(builder.orderItems);
        this.customerInfo = builder.customerInfo;
        this.deliveryAddress = builder.deliveryAddress;
        this.specialInstructions = builder.specialInstructions;
    }

    public static class Builder {
        private List<OrderItem> orderItems = new ArrayList<>();
        private CustomerInfo customerInfo;
        private String deliveryAddress;
        private String specialInstructions = "";

        public Builder addItem(MenuItem menuItem, int quantity) {
            return addItem(menuItem, quantity, new ArrayList<>());
        }

        public Builder addItem(MenuItem menuItem, int quantity, List<String> customizations) {
            orderItems.add(new OrderItem(menuItem, quantity, customizations));
            return this;
        }

        public Builder setCustomerInfo(String name, String phone, String email) {
            this.customerInfo = new CustomerInfo(name, phone, email);
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

        public OrderBuilder build() {
            if (orderItems.isEmpty()) {
                throw new IllegalStateException("Order must contain at least one item");
            }
            if (customerInfo == null) {
                throw new IllegalStateException("Customer information is required");
            }
            return new OrderBuilder(this);
        }
    }
}

// STATE PATTERN - For Order Status Management


public class FoodOrderingSystem {
    public static void main(String[] args) {
        System.out.println("Hello world");
    }
}
