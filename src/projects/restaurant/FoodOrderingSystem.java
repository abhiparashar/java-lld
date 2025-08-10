package projects.restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MenuItem{
    private final Integer id;
    private final String name;
    private final String description;
    private final double price;
    private final double category;

    MenuItem(int id, String name, String description, double price, double category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public double getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public Integer getId() {
        return id;
    }
}

class MenuCategory{
    private final String name;
    private  List<MenuItem> items;

    MenuCategory(String name) {
        this.name = name;
        this.items= new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<MenuItem> getItems() {
        return items;
    }
}

class MenuManager {
    private final Map<String, MenuCategory>categories;
    private final Map<Integer, MenuItem>allItems;

    MenuManager() {
        this.categories = new HashMap<>();
        this.allItems = new HashMap<>();
    }


    public void addCategory(String categoryName){
        categories.putIfAbsent(categoryName, new MenuCategory(categoryName));
    }

    public void addMenuItem(MenuItem menuItem){
        allItems.put(menuItem.getId(), menuItem);
    }
}

public class FoodOrderingSystem {
    public static void main(String[] args) {
        System.out.println("Hello world");
    }
}
