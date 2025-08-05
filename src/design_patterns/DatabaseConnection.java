package design_patterns;

//1. Singleton Pattern
//Definition: Ensures a class has only one instance and provides global access to it.
public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private static final Object lock = new Object();

    private DatabaseConnection() {
        // Private constructor
    }

    public static DatabaseConnection getInstance(){
        if(instance==null){
            synchronized (lock){
                if(instance==null){
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    };

    public void connect() {
        System.out.println("Database connected");
    }
}
