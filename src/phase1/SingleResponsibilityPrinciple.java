package phase1;

// BAD: Multiple responsibilities
class Car{
    private String name;
    private String email;

    // User data

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Email functionality - violates SRP
    public void sendEmail(){
        // Email sending logic
    }

    // Database operations - violates SRP
    public void saveData(){
        // Database saving logic
    }
}

// GOOD: Single responsibility
class User{
    private String email;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

class EmailService{
    public void sendEmail(User user, String message){
        // Email sending logic
    }
}

class UserRepository{
    public void saveData(User user){
        // Database saving logic
    }
}

public class SingleResponsibilityPrinciple {
    public static void main(String[] args) {
        System.out.println("Examples of srp");
    }
}
