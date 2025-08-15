package projects.smartHome;// Phase 1: Basic Device Control
// Simple implementation with direct device control

// Basic Light Device
class Light {
    private String location;
    private boolean isOn;
    private int brightness; // 0-100

    public Light(String location) {
        this.location = location;
        this.isOn = false;
        this.brightness = 0;
    }

    public void turnOn() {
        this.isOn = true;
        this.brightness = 100;
        System.out.println(location + " light is now ON (100% brightness)");
    }

    public void turnOff() {
        this.isOn = false;
        this.brightness = 0;
        System.out.println(location + " light is now OFF");
    }

    public void setBrightness(int level) {
        if (level >= 0 && level <= 100) {
            this.brightness = level;
            this.isOn = level > 0;
            System.out.println(location + " light brightness set to " + level + "%");
        }
    }

    public void getStatus() {
        System.out.println(location + " Light - Status: " + (isOn ? "ON" : "OFF") +
                ", Brightness: " + brightness + "%");
    }
}

// Basic Thermostat Device
class Thermostat {
    private String location;
    private boolean isOn;
    private int targetTemperature;
    private int currentTemperature;

    public Thermostat(String location, int currentTemp) {
        this.location = location;
        this.isOn = false;
        this.currentTemperature = currentTemp;
        this.targetTemperature = currentTemp;
    }

    public void turnOn() {
        this.isOn = true;
        System.out.println(location + " thermostat is now ON");
    }

    public void turnOff() {
        this.isOn = false;
        System.out.println(location + " thermostat is now OFF");
    }

    public void setTemperature(int temperature) {
        if (temperature >= 60 && temperature <= 85) {
            this.targetTemperature = temperature;
            if (!isOn) {
                turnOn();
            }
            System.out.println(location + " thermostat set to " + temperature + "°F");
        } else {
            System.out.println("Temperature must be between 60-85°F");
        }
    }

    public void getStatus() {
        System.out.println(location + " Thermostat - Status: " + (isOn ? "ON" : "OFF") +
                ", Target: " + targetTemperature + "°F, Current: " +
                currentTemperature + "°F");
    }
}

// Basic Security System
class SecuritySystem {
    private boolean isArmed;
    private boolean motionDetected;

    public SecuritySystem() {
        this.isArmed = false;
        this.motionDetected = false;
    }

    public void arm() {
        this.isArmed = true;
        System.out.println("Security system is now ARMED");
    }

    public void disarm() {
        this.isArmed = false;
        this.motionDetected = false;
        System.out.println("Security system is now DISARMED");
    }

    public void simulateMotion() {
        this.motionDetected = true;
        if (isArmed) {
            System.out.println("ALERT: Motion detected! Security breach!");
        } else {
            System.out.println("Motion detected (system disarmed)");
        }
    }

    public void getStatus() {
        System.out.println("Security System - Status: " + (isArmed ? "ARMED" : "DISARMED") +
                ", Motion: " + (motionDetected ? "DETECTED" : "CLEAR"));
    }
}

// Simple Home Controller
class SimpleHomeController {
    private Light livingRoomLight;
    private Light bedroomLight;
    private Thermostat mainThermostat;
    private SecuritySystem security;

    public SimpleHomeController() {
        // Initialize devices
        this.livingRoomLight = new Light("Living Room");
        this.bedroomLight = new Light("Bedroom");
        this.mainThermostat = new Thermostat("Main Floor", 72);
        this.security = new SecuritySystem();
    }

    public void controlLivingRoomLight(String action, int... params) {
        switch (action.toLowerCase()) {
            case "on":
                livingRoomLight.turnOn();
                break;
            case "off":
                livingRoomLight.turnOff();
                break;
            case "brightness":
                if (params.length > 0) {
                    livingRoomLight.setBrightness(params[0]);
                }
                break;
        }
    }

    public void controlBedroomLight(String action, int... params) {
        switch (action.toLowerCase()) {
            case "on":
                bedroomLight.turnOn();
                break;
            case "off":
                bedroomLight.turnOff();
                break;
            case "brightness":
                if (params.length > 0) {
                    bedroomLight.setBrightness(params[0]);
                }
                break;
        }
    }

    public void controlThermostat(String action, int... params) {
        switch (action.toLowerCase()) {
            case "on":
                mainThermostat.turnOn();
                break;
            case "off":
                mainThermostat.turnOff();
                break;
            case "temperature":
                if (params.length > 0) {
                    mainThermostat.setTemperature(params[0]);
                }
                break;
        }
    }

    public void controlSecurity(String action) {
        switch (action.toLowerCase()) {
            case "arm":
                security.arm();
                break;
            case "disarm":
                security.disarm();
                break;
            case "motion":
                security.simulateMotion();
                break;
        }
    }

    public void showAllStatus() {
        System.out.println("\n=== HOME STATUS ===");
        livingRoomLight.getStatus();
        bedroomLight.getStatus();
        mainThermostat.getStatus();
        security.getStatus();
        System.out.println("==================\n");
    }
}

// Demo Application
public class SmartHomePhase1 {
    public static void main(String[] args) {
        SimpleHomeController home = new SimpleHomeController();

        System.out.println("Smart Home Control System - Phase 1");
        System.out.println("===================================\n");

        // Initial status
        home.showAllStatus();

        // Control lights
        System.out.println("Controlling lights...");
        home.controlLivingRoomLight("on");
        home.controlBedroomLight("on");
        home.controlLivingRoomLight("brightness", 50);

        // Control thermostat
        System.out.println("\nControlling thermostat...");
        home.controlThermostat("temperature", 75);

        // Control security
        System.out.println("\nControlling security...");
        home.controlSecurity("arm");
        home.controlSecurity("motion");

        // Final status
        home.showAllStatus();

        // Turn off lights
        System.out.println("Turning off lights...");
        home.controlLivingRoomLight("off");
        home.controlBedroomLight("off");
        home.controlSecurity("disarm");

        home.showAllStatus();
    }
}