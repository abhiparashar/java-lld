Of course. Here is a definitive, no-nonsense guide for each pattern, designed to be a practical mental checklist.

---

### CREATIONAL PATTERNS

#### Factory Method 🏭

*   **✅ Use It When...**
    *   You need to create an object, but the exact class to create is determined at runtime based on some condition (e.g., a parameter, a configuration file, user input).
    *   You are building a framework or library and want to allow users to provide their own implementations of your objects.
    *   You want to decouple your client code from the concrete classes it needs to instantiate. The client code works with an interface, not an implementation.

*   **📍 Where You'll See It...**
    *   **Plugin Systems:** A host application creating different plugin objects.
    *   **Database Drivers:** `DriverManager.getConnection(url)` inspects the URL to load the correct `MySqlDriver`, `PostgresDriver`, etc.
    *   **DI Frameworks:** The core mechanism for creating objects based on configuration.

*   **❌ AVOID It When...**
    *   Your object creation is simple (`new MyObject()`) and is not going to change.
    *   You have a single, straightforward implementation. Don't abstract for the sake of it.
    *   The creation logic is just a simple `if/else` that you don't expect to grow.

*   **🧠 Litmus Test:** "Do I want the client to say 'give me a thing' without knowing the messy details of *which thing* it gets or *how* it's made?"

#### Singleton 👑

*   **✅ Use It When...**
    *   (Modern Usage) You use a Dependency Injection container to manage the lifecycle of an object and configure it to have a "singleton scope" (one instance per container). This is for stateless services like `UserService` or `PaymentCalculator`.
    *   (Classic Pattern - Use with Extreme Caution) You are managing a truly global, stateless resource like a hardware interface or a system-wide logger where DI is not possible or practical.

*   **📍 Where You'll See It...**
    *   **DI Frameworks:** The most common and correct modern usage.
    *   **Logging Frameworks:** A common historical example.
    *   **Configuration Managers:** A single object that provides access to application settings.

*   **❌ AVOID It When...**
    *   **Almost always.** The classic `MyClass.getInstance()` pattern creates hidden dependencies, makes unit testing a nightmare, and is essentially a global variable in disguise.
    *   You need to manage application state. This is global state hell.
    *   You can just pass the object as a dependency in the constructor. **Always prefer dependency injection.**

*   **🧠 Litmus Test:** "Is it physically impossible or nonsensical for more than one instance to exist, *and* I have no way to use dependency injection?" If the answer isn't a hard YES to both, find another way.

#### Builder 🔨

*   **✅ Use It When...**
    *   An object has a large number of constructor parameters, most of which are optional.
    *   You want to create an **immutable object** but the construction process is too complex for a single constructor call.
    *   You need to enforce a specific multi-step process to build an object correctly.

*   **📍 Where You'll See It...**
    *   **API Clients:** Building an HTTP request with headers, body, method, etc. (`Request.Builder`).
    *   **Query Builders:** Constructing complex SQL or NoSQL database queries (`new QueryBuilder().where(...).limit(...).build()`).
    *   **Test Data:** Creating complex objects for unit tests is a killer use case.

*   **❌ AVOID It When...**
    *   The object is simple, with only a few (mostly required) parameters. A constructor is cleaner and more direct.
    *   The object is mutable and can be configured with simple setters after creation.

*   **🧠 Litmus Test:** "Is my constructor signature getting ridiculously long, or do I need to guide the user through a complex, step-by-step creation process?"

---

### STRUCTURAL PATTERNS

#### Adapter 🔌

*   **✅ Use It When...**
    *   You need to make an **existing class** with an incompatible interface work with another part of your system that expects a different interface.
    *   You are introducing a new third-party library and want to shield your application from its specific API, wrapping it in an interface you control.

*   **📍 Where You'll See It...**
    *   **Legacy Code Integration:** The #1 use case. Wrapping an old component to work in a new system.
    *   **Third-Party API Wrappers:** Your `MyStripeAdapter` implements your internal `IPaymentGateway` interface by making calls to Stripe's SDK.

*   **❌ AVOID It When...**
    *   You are designing a system from scratch. Don't intentionally create incompatible interfaces just to use the pattern.
    *   You need to provide different *behavior*, not just a different *interface*. That's the Strategy pattern.

*   **🧠 Litmus Test:** "Am I trying to plug a component with a three-prong plug into a two-prong outlet?"

#### Decorator 🎨

*   **✅ Use It When...**
    *   You need to add extra responsibilities to an object **dynamically at runtime**.
    *   You need to support multiple, combinable behaviors (e.g., a compressed stream that is also encrypted).
    *   Subclassing would lead to a "class explosion" (`Window`, `WindowWithBorder`, `WindowWithScrollbar`, `WindowWithBorderAndScrollbar`...).

*   **📍 Where You'll See It...**
    *   **Web Framework Middleware:** A request handler is decorated by logging, authentication, and caching layers.
    *   **Java I/O Streams:** `new BufferedInputStream(new FileInputStream(...))`.
    *   **UI Toolkits:** Adding borders, shadows, or other effects to components.

*   **❌ AVOID It When...**
    *   You only need to add a single, static responsibility. A simple subclass is often much easier.
    *   You care about object identity (`instanceof` checks can get tricky).

*   **🧠 Litmus Test:** "Do I need to stack responsibilities like LEGOs, in different combinations, without changing the core object?"

#### Facade 🏛️

*   **✅ Use It When...**
    *   You have a complex system with many moving parts (e.g., multiple classes, libraries, or subsystems).
    *   You want to provide a simple, unified, high-level interface to that complexity for most client code.
    *   You want to decouple the majority of your app from the messy details of a subsystem.

*   **📍 Where You'll See It...**
    *   **Service Layers:** An `OrderService` provides a simple `placeOrder()` method that coordinates the `Inventory`, `Billing`, and `Shipping` subsystems.
    *   **API Wrappers:** Creating a single class that simplifies a complex third-party library down to the 5-6 methods you actually need.

*   **❌ AVOID It When...**
    *   The underlying system is already simple. A facade that just calls one method on one other object is pointless ceremony.
    *   You need the client to have fine-grained control (though a good facade doesn't prevent access to the subsystem if needed).

*   **🧠 Litmus Test:** "Am I hiding a tangled mess of wires behind a clean, simple control panel?"

---

### BEHAVIORAL PATTERNS

#### Strategy ⚡

*   **✅ Use It When...**
    *   You have multiple ways (algorithms) to perform a single task.
    *   You want to be able to switch between these algorithms at runtime.
    *   You want to add new algorithms in the future without changing the client code that uses them.

*   **📍 Where You'll See It...**
    *   **Payment Processing:** `StripeStrategy`, `PaypalStrategy`.
    *   **File Compression:** `ZipCompressionStrategy`, `RarCompressionStrategy`.
    *   **Sorting/Searching:** Providing different algorithms for performance trade-offs.

*   **❌ AVOID It When...**
    *   You only have one algorithm and it's not expected to change. (YAGNI - You Ain't Gonna Need It).
    *   Your "strategies" are just simple value lookups, not distinct algorithms.

*   **🧠 Litmus Test:** "Does my code have a `switch` statement that changes *how* something is done, and do I expect to add more `case`s to it later?"

#### Observer 👁️

*   **✅ Use It When...**
    *   A change in one object (the Subject) needs to trigger updates in an unknown number of other objects (the Observers).
    *   The Subject should not be tightly coupled to the Observers. It shouldn't know or care what they are.
    *   You are building event-driven systems.

*   **📍 Where You'll See It...**
    *   **GUI Toolkits:** `button.addEventListener('click', handler)`.
    *   **MVC/MVVM Architectures:** The Model notifies the View of changes.
    *   **Message Queues (Kafka, RabbitMQ):** A producer publishes an event, and multiple consumers can subscribe to it.

*   **❌ AVOID It When...**
    *   The interaction is simple and synchronous. A direct method call is better.
    *   You have only one observer.
    *   **Be aware of memory leaks!** Observers must be explicitly deregistered in many languages to avoid the Subject holding onto them forever.

*   **🧠 Litmus Test:** "Does one object need to shout 'Something happened!' and have other, unknown objects react to the news?"

#### Command 📋

*   **✅ Use It When...**
    *   You need to implement undo/redo functionality.
    *   You need to queue operations to be executed later (e.g., in a background thread).
    *   You need to log operations or save them to be replayed.
    *   You want to parameterize objects with an action.

*   **📍 Where You'll See It...**
    *   **Text Editors/Image Editors:** The foundation of the undo/redo stack.
    *   **Job Queues/Worker Threads:** A web request creates a `ProcessVideoCommand` object and puts it on a queue.
    *   **Installers:** A sequence of installation commands that can be rolled back on failure.

*   **❌ AVOID It When...**
    *   You just want to call a method. This is the #1 abuse. `new SaveCommand().execute()` is terrible if `service.save()` does the same thing. Don't wrap a simple method call in an object for no reason.

*   **🧠 Litmus Test:** "Do I need to treat this method call as a *thing*—something I can hold onto, put in a list, or pass around—before, during, or after it's executed?"