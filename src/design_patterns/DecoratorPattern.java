package design_patterns;

interface Coffee {
    String getDescription();
    double getCost();
}

class SimpleCoffee implements Coffee{

    @Override
    public String getDescription() {
        return "Simple coffee";
    }

    @Override
    public double getCost() {
        return 2.0;
    }
}

class Espresso implements Coffee{

    @Override
    public String getDescription() {
        return "Espresso";
    }

    @Override
    public double getCost() {
        return 3.0;
    }
}

// Base decorator
abstract class CoffeeDecorator implements Coffee{
    protected Coffee coffee;

    public CoffeeDecorator(Coffee coffee){
        this.coffee = coffee;
    }

    @Override
    public String getDescription() {
        return coffee.getDescription();
    }

    @Override
    public double getCost() {
        return coffee.getCost();
    }
}

// Concrete decorator
class MilkDecorator extends CoffeeDecorator{

    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", milk";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 0.5;
    }
}

class SugarDecorator extends CoffeeDecorator{

    public SugarDecorator(Coffee coffee){
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", sugar";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 0.3;
    }
}

class VanillaDecorator extends CoffeeDecorator{

    public VanillaDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", vanilla";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 0.7;
    }
}

class WhippedCreamDecorator extends CoffeeDecorator {
    public WhippedCreamDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", whipped cream";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 0.8;
    }
}

public class DecoratorPattern {
    public static void main(String[] args) {
        System.out.println("=== Coffee Shop Decorator Pattern ===\n");

        // Original example - Simple coffee with milk and sugar
        Coffee coffee1 = new SimpleCoffee();
        coffee1 = new MilkDecorator(coffee1);
        coffee1 = new SugarDecorator(coffee1);
        System.out.println(coffee1.getDescription() + " costs $" + String.format("%.2f", coffee1.getCost()));

        // Espresso with whipped cream and vanilla
        Coffee coffee2 = new Espresso();
        coffee2 = new WhippedCreamDecorator(coffee2);
        coffee2 = new VanillaDecorator(coffee2);
        System.out.println(coffee2.getDescription() + " costs $" + String.format("%.2f", coffee2.getCost()));

        // Complex combination
        Coffee coffee3 = new SimpleCoffee();
        coffee3 = new MilkDecorator(coffee3);
        coffee3 = new SugarDecorator(coffee3);
        coffee3 = new VanillaDecorator(coffee3);
        coffee3 = new WhippedCreamDecorator(coffee3);
        System.out.println(coffee3.getDescription() + " costs $" + String.format("%.2f", coffee3.getCost()));

        // Demonstrate step-by-step decoration
        System.out.println("\n=== Step by Step Decoration ===");
        Coffee stepCoffee = new SimpleCoffee();
        System.out.println("1. " + stepCoffee.getDescription() + " - $" + String.format("%.2f", stepCoffee.getCost()));

        stepCoffee = new MilkDecorator(stepCoffee);
        System.out.println("2. " + stepCoffee.getDescription() + " - $" + String.format("%.2f", stepCoffee.getCost()));

        stepCoffee = new SugarDecorator(stepCoffee);
        System.out.println("3. " + stepCoffee.getDescription() + " - $" + String.format("%.2f", stepCoffee.getCost()));

        stepCoffee = new VanillaDecorator(stepCoffee);
        System.out.println("4. " + stepCoffee.getDescription() + " - $" + String.format("%.2f", stepCoffee.getCost()));
    }
}
