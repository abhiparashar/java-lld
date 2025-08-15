package projects.smartHome;// Phase 1: Basic Device Control
// Simple implementation with direct device control

import java.util.Stack;

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

    // Getters for command pattern
    public String getLocation() {
        return location;
    }

    public int getBrightness() {
        return brightness;
    }

    public boolean isOn(){
        return isOn;
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

    //getters for command
    public String getLocation() {
        return location;
    }

    public int getTargetTemperature() {
        return targetTemperature;
    }

    public int getCurrentTemperature() {
        return currentTemperature;
    }

    public boolean isOn() {
        return isOn;
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

    // Getters for command pattern
    public boolean isArmed() { return isArmed; }
    public boolean isMotionDetected() { return motionDetected; }
}

interface Command{
    public void execute();
    public void undo();
    String getDescription();
}

// Concrete Commands for Light Operations
class LightOnCommand implements Command{
    private final Light light;
    private int previousBrightness;
    private boolean previousState;

    LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        previousState = light.isOn();
        previousBrightness = light.getBrightness();
        light.turnOn();
    }

    @Override
    public void undo() {
        if(!previousState){
            light.turnOff();
        }else {
            light.setBrightness(previousBrightness);
        }
    }

    @Override
    public String getDescription() {
        return "Turn ON " + light.getLocation() + " light";
    }
}

class LightOffCommand implements Command{
    private final Light light;
    private boolean previousState;
    private int previousBrightness;

    LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        previousBrightness = light.getBrightness();
        previousState = light.isOn();
        light.turnOff();
    }

    @Override
    public void undo() {
        if(previousState){
            light.setBrightness(previousBrightness);
        }
    }

    @Override
    public String getDescription() {
        return "Turn OFF " + light.getLocation() + " light";
    }
}

class LightBrightnessCommand implements Command{
    private final Light light;
    private int previousBrightness;
    private final int newBrightness;

    LightBrightnessCommand(Light light, int newBrightness) {
        this.light = light;
        this.newBrightness = newBrightness;
    }


    @Override
    public void execute() {
        previousBrightness = light.getBrightness();
        light.setBrightness(newBrightness);
    }

    @Override
    public void undo() {
        light.setBrightness(previousBrightness);
    }

    @Override
    public String getDescription() {
        return "Set " + light.getLocation() + " light brightness to " + newBrightness + "%";
    }
}

// Concrete Commands for Thermostat Operations
class ThermostatOnCommand implements Command{
    private final Thermostat thermostat;
    private boolean previousState;

    ThermostatOnCommand(Thermostat thermostat) {
        this.thermostat = thermostat;
    }

    @Override
    public void execute() {
        previousState = thermostat.isOn();
        thermostat.turnOn();
    }

    @Override
    public void undo() {
        if (!previousState) {
            thermostat.turnOff();
        }
    }

    @Override
    public String getDescription() {
        return "Turn ON " + thermostat.getLocation() + " thermostat";
    }
}

class ThermostatOffCommand implements Command{
    private final Thermostat thermostat;
    private boolean previousState;

    ThermostatOffCommand(Thermostat thermostat) {
        this.thermostat = thermostat;
    }

    @Override
    public void execute() {
        previousState = thermostat.isOn();
        thermostat.turnOff();
    }

    @Override
    public void undo() {
        if(previousState){
            thermostat.turnOn();
        }
    }

    @Override
    public String getDescription() {
        return "Turn OFF " + thermostat.getLocation() + " thermostat";
    }
}

class ThermostatSetTemperatureCommand implements Command{
    private final Thermostat thermostat;
    private final int newTemperature;
    private int previousTemperature;

    ThermostatSetTemperatureCommand(Thermostat thermostat, int newTemperature) {
        this.thermostat = thermostat;
        this.newTemperature = newTemperature;
    }

    @Override
    public void execute() {
        previousTemperature = thermostat.getCurrentTemperature();
        thermostat.setTemperature(newTemperature);
    }

    @Override
    public void undo() {
        thermostat.setTemperature(previousTemperature);
    }

    @Override
    public String getDescription() {
        return "Set " + thermostat.getLocation() + " thermostat to " + newTemperature + "°F";
    }
}

// Concrete Commands for Security Operations
class SecurityArmCommand implements Command{
    private final SecuritySystem securitySystem;
    private boolean previousState;

    SecurityArmCommand(SecuritySystem securitySystem) {
        this.securitySystem = securitySystem;
    }

    @Override
    public void execute() {
        previousState = securitySystem.isArmed();
        securitySystem.arm();
    }

    @Override
    public void undo() {
        if (!previousState) {
            securitySystem.disarm();
        }
    }

    @Override
    public String getDescription() {
        return "ARM security system";
    }
}

class SecurityDisarmCommand implements Command{
    private final SecuritySystem securitySystem;
    private boolean previousState;

    SecurityDisarmCommand(SecuritySystem securitySystem) {
        this.securitySystem = securitySystem;
    }

    @Override
    public void execute() {
        previousState = securitySystem.isArmed();
        securitySystem.disarm();
    }

    @Override
    public void undo() {
        if (previousState) {
            securitySystem.arm();
        }
    }

    @Override
    public String getDescription() {
        return "DISARM security system";
    }
}

class SecurityMotionCommand implements Command{
    private final SecuritySystem securitySystem;

    SecurityMotionCommand(SecuritySystem securitySystem) {
        this.securitySystem = securitySystem;
    }

    @Override
    public void execute() {
        securitySystem.simulateMotion();
    }

    @Override
    public void undo() {
        System.out.println("Cannot undo motion detection simulation");
    }

    @Override
    public String getDescription() {
        return "Simulate motion detection";
    }
}

// Remote Control (Invoker) - Uses Command Pattern
class SmartHomeRemote {
    private Light livingRoomLight;
    private Light bedroomLight;
    private Thermostat mainThermostat;
    private SecuritySystem security;

    // Command history for undo functionality
    private Stack<Command>commandsHistory;

    public SmartHomeRemote() {
        // Initialize devices
        this.livingRoomLight = new Light("Living Room");
        this.bedroomLight = new Light("Bedroom");
        this.mainThermostat = new Thermostat("Main Floor", 72);
        this.security = new SecuritySystem();
        this.commandsHistory = new Stack<>();
    }

    // Execute any command and store it for undo
    public void executeCommand(Command command){
        command.execute();
        commandsHistory.push(command);
    }

    // Undo the last command
    public void undoLastCommand(){
        if (!commandsHistory.isEmpty()) {
            Command lastCommand = commandsHistory.pop();
            lastCommand.undo();
        } else {
            System.out.println("No commands to undo");
        }
    }

    // Convenience methods to create and execute commands
    public void controlLivingRoomLight(String action, int...params){
        Command command = createLightCommand(livingRoomLight, action, params);
        if(command != null){
            executeCommand(command);
        }
    }

    public void controlBedroomLight(String action, int...params){
        Command command = createLightCommand(bedroomLight,action,params);
        if(command != null){
            executeCommand(command);
        }
    }

    public void controlThermostat(String action, int...params){
        Command command = createThermostatCommand(mainThermostat,action,params);
        if(command != null){
            executeCommand(command);
        }
    }

    public void controlSecurity(String action){
        Command command = createSecurityCommand(security, action);
        if(command != null){
            executeCommand(command);
        }
    }

    // Helper methods to create commands
    public Command createLightCommand(Light light, String action, int...params){
        switch (action.toLowerCase()){
            case "on":
                return new LightOnCommand(light);
            case "off":
                return new LightOffCommand(light);
            case "brightness":
               if(params.length>0){
                    return new LightBrightnessCommand(light,params[0]);
               }
               break;
        }
        return null;
    }

    public Command createThermostatCommand(Thermostat thermostat, String action, int...params){
        switch (action.toLowerCase()){
            case "on":
                return new ThermostatOnCommand(thermostat);
            case "off":
                return new ThermostatOffCommand(thermostat);
            case "temperature":
                if(params.length >0){
                    return new ThermostatSetTemperatureCommand(thermostat, params[0]);
                }
        }
        return null;
    }

    public Command createSecurityCommand(SecuritySystem securitySystem, String action){
        switch (action.toLowerCase()){
            case "arm":
                return new SecurityArmCommand(security);
            case "disarm":
                return new SecurityDisarmCommand(security);
            case "motion":
                return new SecurityMotionCommand(security);
        }
        return null;
    }

    public void showCommandHistory(){
        if(commandsHistory.isEmpty()){
            System.out.println("No commands executed yet");
        }else {
            Stack<Command>temp = new Stack<>();
            int count = 0;
            while (!commandsHistory.isEmpty()){
                Command command = commandsHistory.pop();
                temp.push(command);
                count++;
            }
            // Restore the stack
            while (!temp.isEmpty()){
                commandsHistory.push(temp.pop());
            }
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
        SmartHomeRemote remote = new SmartHomeRemote();

        System.out.println("Smart Home Control System - Phase 2 (Command Pattern)");
        System.out.println("====================================================\n");

        // Initial status
        remote.showAllStatus();

        // Control devices using command pattern
        System.out.println("Executing commands...");
        remote.controlLivingRoomLight("on");
        remote.controlBedroomLight("on");
        remote.controlLivingRoomLight("brightness", 50);
        remote.controlThermostat("temperature", 75);
        remote.controlSecurity("arm");
        remote.controlSecurity("motion");

        // Show current status
        remote.showAllStatus();

        // Show command history
        remote.showCommandHistory();

        // Demonstrate undo functionality
        System.out.println("Demonstrating UNDO functionality...");
        remote.undoLastCommand(); // Undo motion simulation
        remote.undoLastCommand(); // Undo security arm
        remote.undoLastCommand(); // Undo thermostat setting

        // Show final status
        remote.showAllStatus();

        // Turn off lights
        System.out.println("Turning off lights...");
        remote.controlLivingRoomLight("off");
        remote.controlBedroomLight("off");
        remote.controlSecurity("disarm");

        remote.showAllStatus();
    }
}