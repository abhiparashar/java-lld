package design_patterns;

class Computer{
    private String CPU;
    private String RAM;
    private String storage;
    private String GPU;
    private boolean hasWiFi;

    private Computer(Builder builder){
        this.CPU = builder.CPU;
        this.RAM = builder.RAM;
        this.storage = builder.storage;
        this.GPU = builder.GPU;
        this.hasWiFi = builder.hasWiFi;
    }

    public static class Builder{
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

        public Builder setHasWiFi(boolean hasWiFi) {
            this.hasWiFi = hasWiFi;
            return this;
        }

        public Computer build(){
           return new Computer(this);
        };
    }
}
public class BuilderPattern {
    Computer computer = new Computer.Builder()
            .setCPU("Intel i7")
            .setRAM("16GB")
            .setStorage("512GB SSD")
            .setHasWiFi(true)
            .build();
}