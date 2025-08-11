package projects.restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}

public class FoodOrderingSystem {
    public static void main(String[] args) {
        System.out.println("Hello world");
    }
}
