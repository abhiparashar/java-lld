# Phase 1: Foundation Building - Complete Master Guide

## Part A: Object-Oriented Programming Principles

### 1. Encapsulation

**Definition**: Bundling data and methods that operate on that data within a single unit (class), while restricting direct access to internal components.

**Basic Implementation:**
```java
public class BankAccount {
    private double balance; // Private data
    private String accountNumber;
    
    // Public methods to access private data
    public double getBalance() {
        return balance;
    }
    
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
```

**Advanced Concepts:**
- **Access Modifiers**: private, protected, public, package-private
- **Data Hiding**: Internal implementation details are hidden
- **Interface Segregation**: Expose only necessary methods

**Interview Questions:**
1. **Q**: Why is encapsulation important?
   **A**: Provides data security, maintainability, flexibility to change implementation without affecting clients, and enforces business rules.

2. **Q**: How do you achieve encapsulation in different languages?
   **A**: Java/C#: access modifiers; Python: name mangling with underscore; JavaScript: closures or private class fields.

3. **Q**: What's the difference between encapsulation and abstraction?
   **A**: Encapsulation hides implementation details; abstraction hides complexity by showing only essential features.

**Follow-up Questions:**
- How would you handle validation in setters?
- What's the difference between encapsulation and data hiding?
- How does encapsulation support the Open-Closed Principle?

### 2. Inheritance

**Basic Implementation:**
```java
// Base class
public abstract class Vehicle {
    protected String brand;
    protected int year;
    
    public Vehicle(String brand, int year) {
        this.brand = brand;
        this.year = year;
    }
    
    public abstract void start();
    public abstract void stop();
    
    public void displayInfo() {
        System.out.println(brand + " " + year);
    }
}

// Derived class
public class Car extends Vehicle {
    private int numberOfDoors;
    
    public Car(String brand, int year, int doors) {
        super(brand, year);
        this.numberOfDoors = doors;
    }
    
    @Override
    public void start() {
        System.out.println("Car engine started");
    }
    
    @Override
    public void stop() {
        System.out.println("Car engine stopped");
    }
}
```

**Advanced Concepts:**
- **Multiple Inheritance** (interfaces in Java)
- **Method Overriding vs Overloading**
- **Abstract Classes vs Interfaces**
- **Diamond Problem** and its solutions

**Interview Questions:**
1. **Q**: When would you use abstract classes vs interfaces?
   **A**: Abstract classes for shared implementation and state; interfaces for contracts and multiple inheritance.

2. **Q**: Explain the Liskov Substitution Principle.
   **A**: Objects of derived classes should be substitutable for objects of base classes without altering program correctness.

3. **Q**: What is the diamond problem?
   **A**: Ambiguity when a class inherits from multiple classes that have a common base class. Solved by interfaces in Java.

**Follow-up Questions:**
- How do you prevent inheritance in Java/C#?
- What's the difference between `super()` and `this()`?
- When should you use composition over inheritance?

### 3. Polymorphism

**Runtime Polymorphism (Method Overriding):**
```java
public class PaymentProcessor {
    public void processPayment(PaymentMethod method, double amount) {
        method.pay(amount); // Polymorphic call
    }
}

interface PaymentMethod {
    void pay(double amount);
}

class CreditCard implements PaymentMethod {
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using Credit Card");
    }
}

class PayPal implements PaymentMethod {
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using PayPal");
    }
}
```

**Compile-time Polymorphism (Method Overloading):**
```java
public class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public double add(double a, double b) {
        return a + b;
    }
    
    public int add(int a, int b, int c) {
        return a + b + c;
    }
}
```

**Interview Questions:**
1. **Q**: What are the types of polymorphism?
   **A**: Runtime (dynamic binding/overriding) and compile-time (static binding/overloading).

2. **Q**: How does virtual method table work?
   **A**: Each class has a vtable containing pointers to virtual methods. At runtime, the correct method is called based on object type.

3. **Q**: Can you override static methods?
   **A**: No, static methods are resolved at compile time, not runtime.

**Follow-up Questions:**
- What's method hiding vs method overriding?
- How does polymorphism support the Open-Closed Principle?
- Explain covariant return types.

### 4. Abstraction

**Interface-based Abstraction:**
```java
public interface DatabaseConnection {
    void connect();
    void disconnect();
    ResultSet executeQuery(String query);
    boolean executeUpdate(String query);
}

public class MySQLConnection implements DatabaseConnection {
    private Connection connection;
    
    @Override
    public void connect() {
        // MySQL-specific connection logic
        System.out.println("Connected to MySQL");
    }
    
    @Override
    public void disconnect() {
        // MySQL-specific disconnection logic
        System.out.println("Disconnected from MySQL");
    }
    
    @Override
    public ResultSet executeQuery(String query) {
        // MySQL-specific query execution
        return null; // Simplified
    }
}
```

**Abstract Class Example:**
```java
public abstract class Shape {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    // Abstract method - must be implemented by subclasses
    public abstract double calculateArea();
    
    // Concrete method - can be used by all subclasses
    public void displayColor() {
        System.out.println("Color: " + color);
    }
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}
```

**Interview Questions:**
1. **Q**: What's the difference between abstraction and encapsulation?
   **A**: Abstraction hides complexity, encapsulation hides implementation details.

2. **Q**: When would you use an abstract class vs interface?
   **A**: Abstract class when you need shared state/implementation; interface for contracts and multiple inheritance.

## Part B: SOLID Principles

### 1. Single Responsibility Principle (SRP)

**Definition**: A class should have only one reason to change.

**Violation Example:**
```java
// BAD: Multiple responsibilities
public class User {
    private String name;
    private String email;
    
    // User data management
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    
    // Email functionality - violates SRP
    public void sendEmail(String message) {
        // Email sending logic
    }
    
    // Database operations - violates SRP
    public void saveToDatabase() {
        // Database saving logic
    }
}
```

**Correct Implementation:**
```java
// GOOD: Single responsibility
public class User {
    private String name;
    private String email;
    
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    // Getters...
}

public class EmailService {
    public void sendEmail(User user, String message) {
        // Email sending logic
    }
}

public class UserRepository {
    public void save(User user) {
        // Database saving logic
    }
}
```

**Interview Questions:**
1. **Q**: How do you identify SRP violations?
   **A**: Look for classes with multiple reasons to change, mixed concerns, or too many dependencies.

2. **Q**: What are the benefits of SRP?
   **A**: Better maintainability, easier testing, reduced coupling, clearer code organization.

### 2. Open-Closed Principle (OCP)

**Definition**: Software entities should be open for extension but closed for modification.

**Example Implementation:**
```java
// Base interface - closed for modification
public interface PaymentProcessor {
    void processPayment(double amount);
}

// Extensions - open for extension
public class CreditCardProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment: $" + amount);
    }
}

public class PayPalProcessor implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment: $" + amount);
    }
}

// Context class
public class PaymentService {
    public void processPayment(PaymentProcessor processor, double amount) {
        processor.processPayment(amount);
    }
}
```

**Interview Questions:**
1. **Q**: How does OCP reduce risk in software changes?
   **A**: By extending behavior through new classes instead of modifying existing ones, reducing the chance of breaking existing functionality.

2. **Q**: What design patterns support OCP?
   **A**: Strategy, Template Method, Factory, Observer patterns.

### 3. Liskov Substitution Principle (LSP)

**Definition**: Objects of derived classes should be substitutable for objects of base classes.

**Violation Example:**
```java
// BAD: Violates LSP
public class Rectangle {
    protected int width, height;
    
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getArea() { return width * height; }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width; // Violates LSP expectations
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height; // Violates LSP expectations
    }
}
```

**Correct Implementation:**
```java
// GOOD: Follows LSP
public abstract class Shape {
    public abstract int getArea();
}

public class Rectangle extends Shape {
    protected int width, height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int getArea() {
        return width * height;
    }
}

public class Square extends Shape {
    private int side;
    
    public Square(int side) {
        this.side = side;
    }
    
    @Override
    public int getArea() {
        return side * side;
    }
}
```

### 4. Interface Segregation Principle (ISP)

**Definition**: Clients should not be forced to depend on interfaces they don't use.

**Violation Example:**
```java
// BAD: Fat interface
public interface Worker {
    void work();
    void eat();
    void sleep(); // Not all workers need this
}

public class Robot implements Worker {
    @Override
    public void work() { /* Robot working */ }
    
    @Override
    public void eat() { 
        throw new UnsupportedOperationException("Robots don't eat");
    }
    
    @Override
    public void sleep() {
        throw new UnsupportedOperationException("Robots don't sleep");
    }
}
```

**Correct Implementation:**
```java
// GOOD: Segregated interfaces
public interface Workable {
    void work();
}

public interface Eatable {
    void eat();
}

public interface Sleepable {
    void sleep();
}

public class Human implements Workable, Eatable, Sleepable {
    @Override
    public void work() { /* Human working */ }
    @Override
    public void eat() { /* Human eating */ }
    @Override
    public void sleep() { /* Human sleeping */ }
}

public class Robot implements Workable {
    @Override
    public void work() { /* Robot working */ }
}
```

### 5. Dependency Inversion Principle (DIP)

**Definition**: High-level modules should not depend on low-level modules. Both should depend on abstractions.

**Violation Example:**
```java
// BAD: High-level class depends on low-level class
public class EmailService {
    private SMTPClient smtpClient; // Direct dependency
    
    public EmailService() {
        this.smtpClient = new SMTPClient(); // Tight coupling
    }
    
    public void sendEmail(String message) {
        smtpClient.send(message);
    }
}
```

**Correct Implementation:**
```java
// GOOD: Depend on abstractions
public interface EmailClient {
    void send(String message);
}

public class SMTPClient implements EmailClient {
    @Override
    public void send(String message) {
        System.out.println("Sending via SMTP: " + message);
    }
}

public class EmailService {
    private EmailClient emailClient;
    
    // Dependency injection
    public EmailService(EmailClient emailClient) {
        this.emailClient = emailClient;
    }
    
    public void sendEmail(String message) {
        emailClient.send(message);
    }
}
```

## Part C: Design Patterns

### Creational Patterns

#### 1. Singleton Pattern

**Definition**: Ensures a class has only one instance and provides global access to it.

**Thread-Safe Implementation:**
```java
public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private static final Object lock = new Object();
    
    private DatabaseConnection() {
        // Private constructor
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
    
    public void connect() {
        System.out.println("Database connected");
    }
}
```

**Enum Singleton (Preferred):**
```java
public enum DatabaseConnection {
    INSTANCE;
    
    public void connect() {
        System.out.println("Database connected");
    }
}
```

**Interview Questions:**
1. **Q**: What are the problems with Singleton pattern?
   **A**: Testing difficulties, hidden dependencies, violates SRP, global state issues.

2. **Q**: How do you make Singleton thread-safe?
   **A**: Synchronized methods, double-checked locking, enum implementation, or initialization-on-demand holder pattern.

3. **Q**: How do you handle Singleton in multithreading?
   **A**: Use volatile keyword, synchronized blocks, or enum implementation.

#### 2. Factory Pattern

**Simple Factory:**
```java
public abstract class Vehicle {
    public abstract void start();
}

public class Car extends Vehicle {
    @Override
    public void start() {
        System.out.println("Car started");
    }
}

public class Motorcycle extends Vehicle {
    @Override
    public void start() {
        System.out.println("Motorcycle started");
    }
}

public class VehicleFactory {
    public static Vehicle createVehicle(String type) {
        switch (type.toLowerCase()) {
            case "car":
                return new Car();
            case "motorcycle":
                return new Motorcycle();
            default:
                throw new IllegalArgumentException("Unknown vehicle type");
        }
    }
}
```

**Factory Method Pattern:**
```java
public abstract class VehicleFactory {
    public abstract Vehicle createVehicle();
    
    public void processVehicle() {
        Vehicle vehicle = createVehicle();
        vehicle.start();
    }
}

public class CarFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new Car();
    }
}

public class MotorcycleFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new Motorcycle();
    }
}
```

**Interview Questions:**
1. **Q**: When would you use Factory pattern?
   **A**: When object creation is complex, when you need to decouple creation from usage, or when creation depends on runtime conditions.

2. **Q**: Difference between Factory Method and Abstract Factory?
   **A**: Factory Method creates one type of object; Abstract Factory creates families of related objects.

#### 3. Builder Pattern

**Implementation:**
```java
public class Computer {
    private String CPU;
    private String RAM;
    private String storage;
    private String GPU;
    private boolean hasWiFi;
    
    private Computer(Builder builder) {
        this.CPU = builder.CPU;
        this.RAM = builder.RAM;
        this.storage = builder.storage;
        this.GPU = builder.GPU;
        this.hasWiFi = builder.hasWiFi;
    }
    
    public static class Builder {
        private String CPU;
        private String RAM;
        private String storage;
        private String GPU;
        private boolean hasWiFi;
        
        public Builder setCPU(String CPU) {
            this.CPU = CPU;
            return this;
        }
        
        public Builder setRAM(String RAM) {
            this.RAM = RAM;
            return this;
        }
        
        public Builder setStorage(String storage) {
            this.storage = storage;
            return this;
        }
        
        public Builder setGPU(String GPU) {
            this.GPU = GPU;
            return this;
        }
        
        public Builder setWiFi(boolean hasWiFi) {
            this.hasWiFi = hasWiFi;
            return this;
        }
        
        public Computer build() {
            return new Computer(this);
        }
    }
}

// Usage
Computer computer = new Computer.Builder()
    .setCPU("Intel i7")
    .setRAM("16GB")
    .setStorage("512GB SSD")
    .setWiFi(true)
    .build();
```

**Interview Questions:**
1. **Q**: When would you use Builder pattern?
   **A**: When you have many optional parameters, complex object construction, or need immutable objects.

2. **Q**: Builder vs Constructor?
   **A**: Builder provides better readability, handles optional parameters elegantly, and ensures object immutability.

### Behavioral Patterns

#### 1. Observer Pattern

**Implementation:**
```java
import java.util.*;

// Observer interface
public interface Observer {
    void update(String message);
}

// Subject interface
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}

// Concrete Subject
public class NewsAgency implements Subject {
    private List<Observer> observers;
    private String news;
    
    public NewsAgency() {
        this.observers = new ArrayList<>();
    }
    
    @Override
    public void attach(Observer observer) {
        observers.add(observer);
    }
    
    @Override
    public void detach(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(news);
        }
    }
    
    public void setNews(String news) {
        this.news = news;
        notifyObservers();
    }
}

// Concrete Observer
public class NewsChannel implements Observer {
    private String name;
    private String news;
    
    public NewsChannel(String name) {
        this.name = name;
    }
    
    @Override
    public void update(String news) {
        this.news = news;
        System.out.println(name + " received news: " + news);
    }
}
```

**Interview Questions:**
1. **Q**: What problem does Observer pattern solve?
   **A**: Defines one-to-many dependency between objects so that when one object changes state, all dependents are notified.

2. **Q**: Observer vs Pub-Sub pattern?
   **A**: Observer is synchronous and direct; Pub-Sub is asynchronous and uses message broker.

#### 2. Strategy Pattern

**Implementation:**
```java
// Strategy interface
public interface PaymentStrategy {
    void pay(double amount);
}

// Concrete strategies
public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    private String cvv;
    
    public CreditCardPayment(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using Credit Card");
    }
}

public class PayPalPayment implements PaymentStrategy {
    private String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using PayPal");
    }
}

// Context
public class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    public void checkout(double amount) {
        paymentStrategy.pay(amount);
    }
}
```

**Interview Questions:**
1. **Q**: When would you use Strategy pattern?
   **A**: When you have multiple ways to perform a task and want to choose the algorithm at runtime.

2. **Q**: Strategy vs State pattern?
   **A**: Strategy focuses on algorithms; State focuses on changing behavior based on internal state.

#### 3. Command Pattern

**Implementation:**
```java
// Command interface
public interface Command {
    void execute();
    void undo();
}

// Receiver
public class Light {
    private boolean isOn = false;
    
    public void turnOn() {
        isOn = true;
        System.out.println("Light is ON");
    }
    
    public void turnOff() {
        isOn = false;
        System.out.println("Light is OFF");
    }
}

// Concrete Commands
public class TurnOnCommand implements Command {
    private Light light;
    
    public TurnOnCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        light.turnOn();
    }
    
    @Override
    public void undo() {
        light.turnOff();
    }
}

public class TurnOffCommand implements Command {
    private Light light;
    
    public TurnOffCommand(Light light) {
        this.light = light;
    }
    
    @Override
    public void execute() {
        light.turnOff();
    }
    
    @Override
    public void undo() {
        light.turnOn();
    }
}

// Invoker
public class RemoteControl {
    private Command command;
    private Command lastCommand;
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    public void pressButton() {
        command.execute();
        lastCommand = command;
    }
    
    public void pressUndo() {
        if (lastCommand != null) {
            lastCommand.undo();
        }
    }
}
```

#### 4. State Pattern

**Implementation:**
```java
// State interface
public interface State {
    void insertQuarter();
    void ejectQuarter();
    void turnCrank();
    void dispense();
}

// Context
public class GumballMachine {
    private State noQuarterState;
    private State hasQuarterState;
    private State soldState;
    private State soldOutState;
    
    private State currentState;
    private int count;
    
    public GumballMachine(int count) {
        this.count = count;
        
        noQuarterState = new NoQuarterState(this);
        hasQuarterState = new HasQuarterState(this);
        soldState = new SoldState(this);
        soldOutState = new SoldOutState(this);
        
        if (count > 0) {
            currentState = noQuarterState;
        } else {
            currentState = soldOutState;
        }
    }
    
    public void insertQuarter() { currentState.insertQuarter(); }
    public void ejectQuarter() { currentState.ejectQuarter(); }
    public void turnCrank() { currentState.turnCrank(); }
    public void dispense() { currentState.dispense(); }
    
    // State management methods
    public void setState(State state) { this.currentState = state; }
    public State getNoQuarterState() { return noQuarterState; }
    public State getHasQuarterState() { return hasQuarterState; }
    public State getSoldState() { return soldState; }
    public State getSoldOutState() { return soldOutState; }
    
    public void releaseBall() {
        if (count > 0) {
            count--;
            System.out.println("A gumball comes rolling out the slot...");
        }
    }
    
    public int getCount() { return count; }
}

// Concrete State
public class NoQuarterState implements State {
    private GumballMachine gumballMachine;
    
    public NoQuarterState(GumballMachine gumballMachine) {
        this.gumballMachine = gumballMachine;
    }
    
    @Override
    public void insertQuarter() {
        System.out.println("You inserted a quarter");
        gumballMachine.setState(gumballMachine.getHasQuarterState());
    }
    
    @Override
    public void ejectQuarter() {
        System.out.println("You haven't inserted a quarter");
    }
    
    @Override
    public void turnCrank() {
        System.out.println("You turned, but there's no quarter");
    }
    
    @Override
    public void dispense() {
        System.out.println("You need to pay first");
    }
}
```

### Structural Patterns

#### 1. Decorator Pattern

**Implementation:**
```java
// Component interface
public interface Coffee {
    String getDescription();
    double getCost();
}

// Concrete component
public class SimpleCoffee implements Coffee {
    @Override
    public String getDescription() {
        return "Simple coffee";
    }
    
    @Override
    public double getCost() {
        return 2.0;
    }
}

// Base decorator
public abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
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

// Concrete decorators
public class MilkDecorator extends CoffeeDecorator {
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

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
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

// Usage
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
System.out.println(coffee.getDescription() + " costs $" + coffee.getCost());
```

#### 2. Adapter Pattern

**Implementation:**
```java
// Target interface (what client expects)
public interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Adaptee (existing interface)
public interface AdvancedMediaPlayer {
    void playVlc(String fileName);
    void playMp4(String fileName);
}

// Concrete Adaptee
public class VlcPlayer implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        System.out.println("Playing vlc file: " + fileName);
    }
    
    @Override
    public void playMp4(String fileName) {
        // Do nothing
    }
}

public class Mp4Player implements AdvancedMediaPlayer {
    @Override
    public void playVlc(String fileName) {
        // Do nothing
    }
    
    @Override
    public void playMp4(String fileName) {
        System.out.println("Playing mp4 file: " + fileName);
    }
}

// Adapter
public class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedMusicPlayer;
    
    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedMusicPlayer = new VlcPlayer();
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedMusicPlayer = new Mp4Player();
        }
    }
    
    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedMusicPlayer.playVlc(fileName);
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedMusicPlayer.playMp4(fileName);
        }
    }
}

// Client
public class AudioPlayer implements MediaPlayer {
    private MediaAdapter mediaAdapter;
    
    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing mp3 file: " + fileName);
        } else if (audioType.equalsIgnoreCase("vlc") || audioType.equalsIgnoreCase("mp4")) {
            mediaAdapter = new MediaAdapter(audioType);
            mediaAdapter.play(audioType, fileName);
        } else {
            System.out.println("Invalid media. " + audioType + " format not supported");
        }
    }
}
```

#### 3. Facade Pattern

**Implementation:**
```java
// Complex subsystem classes
public class CPU {
    public void freeze() { System.out.println("CPU: Freezing processor."); }
    public void jump(long position) { System.out.println("CPU: Jumping to " + position); }
    public void execute() { System.out.println("CPU: Executing."); }
}

public class Memory {
    public void load(long position, byte[] data) {
        System.out.println("Memory: Loading data to " + position);
    }
}

public class HardDrive {
    public byte[] read(long lba, int size) {
        System.out.println("HardDrive: Reading " + size + " bytes from " + lba);
        return new byte[size];
    }
}

// Facade class
public class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    
    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }
    
    public void start() {
        System.out.println("Starting computer...");
        cpu.freeze();
        memory.load(0, hardDrive.read(0, 1024));
        cpu.jump(0);
        cpu.execute();
        System.out.println("Computer started successfully!");
    }
}
```

## Part D: UML Diagrams and System Modeling

### 1. Class Diagrams

**Basic Elements:**
- **Classes**: Rectangle with class name, attributes, and methods
- **Relationships**: Association, Aggregation, Composition, Inheritance
- **Multiplicity**: 1, 0..1, 1..*, 0..*

**Example: Library Management System**
```
┌─────────────────────┐
│       Library       │
├─────────────────────┤
│ - name: String      │
│ - address: String   │
├─────────────────────┤
│ + addBook()         │
│ + removeBook()      │
│ + searchBook()      │
└─────────────────────┘
           │ 1
           │ has
           │ *
┌─────────────────────┐
│        Book         │
├─────────────────────┤
│ - isbn: String      │
│ - title: String     │
│ - author: String    │
├─────────────────────┤
│ + getDetails()      │
│ + checkOut()        │
│ + returnBook()      │
└─────────────────────┘
```

### 2. Sequence Diagrams

**Example: User Login Process**
```
User    →    LoginUI    →    AuthService    →    Database
 │              │              │               │
 │──login()──→  │              │               │
 │              │──validate()→ │               │
 │              │              │──checkUser()→ │
 │              │              │←──result───── │
 │              │←──status──── │               │
 │←──response── │              │               │
```

### 3. Use Case Diagrams

**Elements:**
- **Actors**: External entities (users, systems)
- **Use Cases**: Functional requirements
- **System Boundary**: Scope of the system
- **Relationships**: Include, Extend, Generalization

**Interview Questions:**
1. **Q**: What's the difference between aggregation and composition?
   **A**: Aggregation is "has-a" relationship (loose coupling); Composition is "part-of" relationship (strong coupling, lifecycle dependency).

2. **Q**: When would you use sequence diagrams?
   **A**: To show interaction between objects over time, especially for complex workflows.

## Part E: Code Organization and Clean Architecture

### 1. Package Structure

**Layered Architecture Example:**
```
com.company.app
├── controller/          # Presentation layer
│   ├── UserController.java
│   └── BookController.java
├── service/            # Business logic layer
│   ├── UserService.java
│   └── BookService.java
├── repository/         # Data access layer
│   ├── UserRepository.java
│   └── BookRepository.java
├── model/             # Domain entities
│   ├── User.java
│   └── Book.java
├── dto/               # Data transfer objects
│   ├── UserDTO.java
│   └── BookDTO.java
└── config/            # Configuration
    └── DatabaseConfig.java
```

### 2. Clean Code Principles

**Naming Conventions:**
```java
// GOOD: Descriptive names
public class CustomerOrderProcessor {
    private List<Order> pendingOrders;
    
    public void processHighPriorityOrders() {
        for (Order order : pendingOrders) {
            if (order.getPriority() == Priority.HIGH) {
                processOrder(order);
            }
        }
    }
}

// BAD: Poor naming
public class COP {
    private List<Order> po;
    
    public void process() {
        for (Order o : po) {
            if (o.getP() == Priority.HIGH) {
                proc(o);
            }
        }
    }
}
```

**Function Design:**
```java
// GOOD: Single responsibility, small functions
public class OrderValidator {
    public boolean isValidOrder(Order order) {
        return hasValidCustomer(order) && 
               hasValidItems(order) && 
               hasValidPayment(order);
    }
    
    private boolean hasValidCustomer(Order order) {
        return order.getCustomer() != null && 
               order.getCustomer().getId() > 0;
    }
    
    private boolean hasValidItems(Order order) {
        return order.getItems() != null && 
               !order.getItems().isEmpty();
    }
    
    private boolean hasValidPayment(Order order) {
        return order.getPaymentMethod() != null;
    }
}
```

## Practice Projects

### Project 1: Media Player System (Week 1)
**Objective**: Practice OOP principles and basic design patterns

**Requirements:**
- Support multiple audio formats (MP3, WAV, FLAC)
- Implement volume controls and equalizer
- Create playlist functionality
- Add shuffle and repeat modes

**Patterns to Implement:**
- Strategy Pattern (for audio formats)
- Decorator Pattern (for audio effects)
- Observer Pattern (for UI updates)
- Factory Pattern (for format handlers)

**Key Classes:**
```java
public interface AudioFormat {
    void play(String filename);
    void pause();
    void stop();
}

public class MediaPlayer {
    private AudioFormat audioFormat;
    private List<Observer> observers;
    // Implementation...
}
```

### Project 2: Food Ordering System (Week 2)
**Objective**: Apply SOLID principles and multiple design patterns

**Requirements:**
- Menu management with categories
- Order processing with different payment methods
- Delivery tracking system
- User notification system

**Patterns to Implement:**
- Builder Pattern (for complex orders)
- Command Pattern (for order operations)
- State Pattern (for order status)
- Observer Pattern (for notifications)

**Key Learning Points:**
- How to structure complex business logic
- Handling state transitions
- Implementing notification systems

### Project 3: Smart Home Control System (Week 3)
**Objective**: Master behavioral patterns and system integration

**Requirements:**
- Control multiple device types (lights, thermostats, security)
- Create automation rules and schedules
- Remote control interface
- Device status monitoring

**Patterns to Implement:**
- Command Pattern (for device controls)
- Facade Pattern (for system interface)
- Observer Pattern (for status updates)
- Template Method (for device operations)

## Common Interview Questions and Answers

### OOP Concepts

**Q1**: Explain the difference between abstract classes and interfaces.
**A**: Abstract classes can have both abstract and concrete methods, state, and constructors. Interfaces define contracts with only abstract methods (until Java 8). Use abstract classes for shared implementation; interfaces for contracts and multiple inheritance.

**Q2**: What is method overloading vs overriding?
**A**: Overloading is compile-time polymorphism with same method name but different parameters. Overriding is runtime polymorphism where subclass provides specific implementation of parent class method.

**Q3**: How does composition differ from inheritance?
**A**: Inheritance is "is-a" relationship (tight coupling); Composition is "has-a" relationship (loose coupling). Favor composition for flexibility and code reuse.

### Design Patterns

**Q4**: When would you use Observer pattern vs Pub-Sub pattern?
**A**: Observer for direct, synchronous notifications within same application. Pub-Sub for asynchronous, decoupled communication, often across applications.

**Q5**: Explain the difference between Factory Method and Abstract Factory.
**A**: Factory Method creates one type of object; Abstract Factory creates families of related objects. Factory Method uses inheritance; Abstract Factory uses composition.

**Q6**: What are the disadvantages of Singleton pattern?
**A**: Testing difficulties, hidden dependencies, global state, violates SRP, threading issues, and makes code tightly coupled.

### SOLID Principles

**Q7**: How does Dependency Inversion help in testing?
**A**: By depending on abstractions instead of concrete classes, you can easily inject mock objects during testing, making unit tests isolated and reliable.

**Q8**: Give an example of Interface Segregation Principle violation.
**A**: A large interface that forces classes to implement methods they don't need, like a Worker interface with eat(), work(), and sleep() methods that Robot class would have to implement unnecessarily.

## Next Steps

After completing Phase 1, you should be able to:
- Design classes following OOP principles
- Apply SOLID principles to create maintainable code
- Implement common design patterns appropriately
- Create UML diagrams for system design
- Structure code using clean architecture principles

**Recommended Timeline:**
- **Week 1**: OOP concepts and first project
- **Week 2**: SOLID principles and second project
- **Week 3**: Design patterns and third project

**Assessment Checklist:**
- [ ] Can explain all four OOP principles with examples
- [ ] Can identify SOLID principle violations in code
- [ ] Can implement at least 5 design patterns from memory
- [ ] Can create class and sequence diagrams
- [ ] Can structure a project with proper layered architecture

Ready for **Phase 2: System Components & Architecture**!