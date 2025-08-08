package design_patterns;

// Complex subsystem classes
class CPU {
    public void freeze() {
        System.out.println("CPU: Freezing processor.");
    }

    public void jump(long position) {
        System.out.println("CPU: Jumping to " + position);
    }

    public void execute() {
        System.out.println("CPU: Executing.");
    }

    public void halt() {
        System.out.println("CPU: Halting processor.");
    }
}

class Memory {
    public void load(long position, byte[] data) {
        System.out.println("Memory: Loading data to " + position);
    }

    public void clear() {
        System.out.println("Memory: Clearing memory.");
    }

    public void dump() {
        System.out.println("Memory: Dumping memory contents.");
    }
}

class HardDrive {
    public byte[] read(long lba, int size) {
        System.out.println("HardDrive: Reading " + size + " bytes from " + lba);
        return new byte[size];
    }

    public void write(long lba, byte[] data) {
        System.out.println("HardDrive: Writing " + data.length + " bytes to " + lba);
    }

    public void spinUp() {
        System.out.println("HardDrive: Spinning up drive.");
    }

    public void spinDown() {
        System.out.println("HardDrive: Spinning down drive.");
    }
}

class Graphics {
    public void initialize() {
        System.out.println("Graphics: Initializing graphics subsystem.");
    }

    public void loadDriver() {
        System.out.println("Graphics: Loading graphics driver.");
    }

    public void setResolution(int width, int height) {
        System.out.println("Graphics: Setting resolution to " + width + "x" + height);
    }
}

class NetworkCard {
    public void initialize() {
        System.out.println("NetworkCard: Initializing network interface.");
    }

    public void connect() {
        System.out.println("NetworkCard: Connecting to network.");
    }

    public void disconnect() {
        System.out.println("NetworkCard: Disconnecting from network.");
    }
}

// Facade class
class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    private Graphics graphics;
    private NetworkCard networkCard;

    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
        this.graphics = new Graphics();
        this.networkCard = new NetworkCard();
    }

    public void start() {
        System.out.println("=== Starting computer ===");
        hardDrive.spinUp();
        cpu.freeze();
        memory.load(0, hardDrive.read(0, 1024));
        cpu.jump(0);
        cpu.execute();
        graphics.initialize();
        graphics.loadDriver();
        graphics.setResolution(1920, 1080);
        networkCard.initialize();
        networkCard.connect();
        System.out.println("Computer started successfully!\n");
    }

    public void shutdown() {
        System.out.println("=== Shutting down computer ===");
        networkCard.disconnect();
        memory.dump();
        memory.clear();
        cpu.halt();
        hardDrive.spinDown();
        System.out.println("Computer shut down successfully!\n");
    }

    public void restart() {
        System.out.println("=== Restarting computer ===");
        shutdown();
        try {
            Thread.sleep(1000); // Simulate restart delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        start();
    }

    public void saveData(String data) {
        System.out.println("=== Saving data ===");
        byte[] dataBytes = data.getBytes();
        hardDrive.write(1024, dataBytes);
        System.out.println("Data saved successfully!\n");
    }
}

public class FacadePattern {
    public static void main(String[] args) {
        System.out.println("=== Computer Facade Pattern Demo ===\n");

        // Simple interface to complex system
        ComputerFacade computer = new ComputerFacade();

        // User doesn't need to know about CPU, Memory, HardDrive complexity
        computer.start();

        computer.saveData("Hello World!");

        computer.restart();

        computer.shutdown();

        System.out.println("=== Without Facade (Complex way) ===");

        // This is what user would have to do without facade
        CPU cpu = new CPU();
        Memory memory = new Memory();
        HardDrive hardDrive = new HardDrive();
        Graphics graphics = new Graphics();
        NetworkCard networkCard = new NetworkCard();

        // Complex manual process
        System.out.println("Manual startup process:");
        hardDrive.spinUp();
        cpu.freeze();
        memory.load(0, hardDrive.read(0, 1024));
        cpu.jump(0);
        cpu.execute();
        graphics.initialize();
        graphics.loadDriver();
        graphics.setResolution(1920, 1080);
        networkCard.initialize();
        networkCard.connect();
        System.out.println("Manual startup complete (much more complex!)");
    }
}