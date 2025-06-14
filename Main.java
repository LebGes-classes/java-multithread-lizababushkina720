import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ManagementSystem system = new ManagementSystem();
        try {
            system.readDataFromExcel("base_multithreads.xlsx");
            system.simulateWork();
        } catch (IOException e) {
            System.err.println("ошибка: " + e.getMessage());
        }
    }
}