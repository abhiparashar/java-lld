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

    OrderItem(MenuItem menuItem, int quantity) {
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

public class FoodOrderingSystem {
    public static void main(String[] args) {
        System.out.println("Hello world");
    }
}
