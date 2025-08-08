package design_patterns;

import java.util.*;

interface Command{
    void execute();
    void undo();
    String getDescription();
}

class TurnOnLightCommand implements Command{
    private final Light light;
    public TurnOnLightCommand(Light light){

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

    @Override
    public String getDescription() {
        return "Turn On Light";
    }
}

class TurnOffLightCommand implements Command{
    private final Light light;

    TurnOffLightCommand(Light light) {
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

    @Override
    public String getDescription() {
        return "Turn Off Light";
    }
}

class Light{
    private final String location;
    private boolean isOn = false;
    Light(String location) {
        this.location = location;
    }

    public void turnOn() {
        isOn = true;
        System.out.println("💡 " + location + " light is ON");
    }

    public void turnOff() {
        isOn = false;
        System.out.println("💡 " + location + " light is OFF");
    }

    public boolean isOn() {
        return isOn;
    }
}

class Fan{
    private final String location;
    private int speed = 0;

    Fan(String location) {
        this.location = location;
    }

    public void setSpeed(int speed){
        this.speed = speed;
        String[] speeds = {"OFF", "LOW", "MEDIUM", "HIGH"};
        System.out.println("🌪️  " + location + " fan speed: " + speeds[speed]);
    }

    public int getSpeed() {
        return speed;
    }
}

class SetFanSpeedCommand implements Command{
    private final Fan fan;
    private final int fanSpeed;
    private int previousSpeed;

    SetFanSpeedCommand(Fan fan, int fanSpeed) {
        this.fan = fan;
        this.fanSpeed = fanSpeed;
    }

    @Override
    public void execute() {
       previousSpeed = fan.getSpeed();
       fan.setSpeed(fanSpeed);
    }

    @Override
    public void undo() {
        fan.setSpeed(previousSpeed);
    }

    @Override
    public String getDescription() {
        return "Set Fan Speed to " + fanSpeed;
    }
}

// Macro Command - executes multiple commands
class MacroCommand implements Command{
    private final List<Command>commandList;
    private final String name;

    MacroCommand(String name,List<Command> commandList) {
        this.commandList = commandList;
        this.name = name;
    }

    @Override
    public void execute() {
        System.out.println("Executing macro: " + name);
        for (Command command :commandList){
            command.execute();
        }
    }

    @Override
    public void undo() {
        System.out.println("Undoing macro: " + name);
        for (int i = commandList.size()-1; i>=0 ; i--) {
            Command command = commandList.get(i);
            command.undo();
        }
    }

    @Override
    public String getDescription() {
        return "Macro: " + name;
    }
}

// Invoker - Smart Remote Control
class SmartRemoteControl{
    private final Command[] slots = new Command[7];
    private final Stack<Command>commandHistory = new Stack<>();
    private final Queue<Command>scheduledCommands = new LinkedList<>();

    public void setCommand(int slot, Command command){
        slots[slot] = command;
        System.out.println("🎮 Slot " + slot + " programmed: " + command.getDescription());
    }

    public void pressButton(int slot){
        if (slots[slot] != null) {
            Command command = slots[slot];
            command.execute();
            commandHistory.add(command);
        } else {
            System.out.println("No command assigned to slot " + slot);
        }
    }

    public void pressUndo(){
        if (!commandHistory.isEmpty()) {
            Command lastCommand = commandHistory.pop();
            lastCommand.undo();
        }
    }

    public void scheduleCommand(Command command){
        scheduledCommands.offer(command);
    }

    public void executeScheduledCommands(){
        while (!scheduledCommands.isEmpty()){
          Command command =   scheduledCommands.poll();
          command.execute();
          commandHistory.add(command);
        }
    }
    
    public void showProgrammedSlots(){
        for (int i = 0; i < slots.length; i++) {
            String desc = (slots[i] != null) ? slots[i].getDescription() : "Empty";
            System.out.println("   Slot " + i + ": " + desc);
        }
    }
}

public class CommandPatternDemo {
    public static void main(String[] args) {
        System.out.println("SMART HOME COMMAND PATTERN DEMO");
        System.out.println("==================================\n");

        // Create devices (Receivers)
        Light livingRoomLight = new Light("Living Room");
        Light bedroomLight = new Light("Bedroom");
        Fan livingRoomFan = new Fan("Living Room");

        // Create commands
        Command livingRoomLightOn = new TurnOnLightCommand(livingRoomLight );
        Command livingRoomLightOff = new TurnOffLightCommand(livingRoomLight);
        Command bedroomLightOn = new TurnOnLightCommand(bedroomLight);
        Command bedroomLightOff = new TurnOffLightCommand(bedroomLight);
        Command fanLow = new SetFanSpeedCommand(livingRoomFan, 1);
        Command fanHigh = new SetFanSpeedCommand(livingRoomFan, 3);
        Command fanOff = new SetFanSpeedCommand(livingRoomFan, 0);

        // Create macro commands
        List<Command> movieModeCommands = Arrays.asList(
                livingRoomLightOff,
                bedroomLightOff,
                fanLow
        );
        Command movieMode = new MacroCommand("Movie Mode", movieModeCommands);

        List<Command> allOffCommands = Arrays.asList(
                livingRoomLightOff,
                bedroomLightOff,
                fanOff
        );
        Command allOff = new MacroCommand("All Off", allOffCommands);

        // Create smart remote
        SmartRemoteControl remote = new SmartRemoteControl();

        // Program the remote
        System.out.println("📋 Programming the remote...");
        remote.setCommand(0, livingRoomLightOn);
        remote.setCommand(1, livingRoomLightOff);
        remote.setCommand(2, bedroomLightOn);
        remote.setCommand(3, fanHigh);
        remote.setCommand(4, movieMode);
        remote.setCommand(5, allOff);

        remote.showProgrammedSlots();

        // Test basic commands
        System.out.println("\n🎮 Testing basic commands:");
        remote.pressButton(0); // Living room light on
        remote.pressButton(2); // Bedroom light on
        remote.pressButton(3); // Fan high

        // Test undo
        System.out.println("\n↩️  Testing undo:");
        remote.pressUndo(); // Undo fan high
        remote.pressUndo(); // Undo fan high
        remote.pressUndo(); // Undo bedroom light

        // Test macro command
        System.out.println("\n🎬 Testing macro command:");
        remote.pressButton(4); // Movie mode

        // Test undo macro
        System.out.println("\n↩️  Testing macro undo:");
        remote.pressUndo(); // Undo movie mode

        // Test scheduling
        System.out.println("\n⏰ Testing command scheduling:");
        remote.scheduleCommand(bedroomLightOn);
        remote.scheduleCommand(fanLow);
        remote.scheduleCommand(livingRoomLightOn);
        remote.executeScheduledCommands();

        // Final cleanup
        System.out.println("\n🧹 Cleaning up:");
        remote.pressButton(5); // All off

        System.out.println("\n✅ Command Pattern Demo Complete!");
        System.out.println("💡 Benefits demonstrated:");
        System.out.println("   • Commands as objects");
        System.out.println("   • Undo/Redo functionality");
        System.out.println("   • Macro commands");
        System.out.println("   • Command scheduling");
        System.out.println("   • Separation of invoker and receiver");
    }
}
