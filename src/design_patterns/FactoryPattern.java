package design_patterns;

abstract class Vehicle{
    public abstract void start();
}

class Car extends Vehicle{

    @Override
    public void start() {
        System.out.println("Car started");
    }
}

class Motorcycle extends Vehicle{

    @Override
    public void start() {
        System.out.println("Motorcycle started");
    }
}

class VehicleFactory {
    public static Vehicle createVehicle(String type){
        switch (type.toLowerCase()){
            case "car" :
                return new Car();
            case "motorcycle":
                return new Motorcycle();
            default:
                throw new IllegalArgumentException("Unknown vehicle type");
        }
    }
}

public class FactoryPattern{
    public static void main(String[] args) {
        Vehicle vehicle = VehicleFactory.createVehicle("car");
        vehicle.start();
    }
}
