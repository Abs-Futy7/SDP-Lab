import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * =========================================================
 * SMART TRANSPORTATION MANAGEMENT SYSTEM
 * Factory Method Design Pattern Implementation
 * =========================================================
 */

// =========================================================
// PRODUCT INTERFACE (unchanged)
// =========================================================
interface Vehicle {
    void startTrip();

    double calculateFare(double distance);

    void assignRoute(String route);

    void getVehicleInfo();

    String getVehicleType();
}

// =========================================================
// CONCRETE PRODUCTS (unchanged)
// =========================================================
class Bus implements Vehicle {
    private final int capacity = 40;
    private final double farePerKm = 15;
    private String route;

    @Override
    public void startTrip() {
        System.out.println("Bus trip started.");
    }

    @Override
    public double calculateFare(double distance) {
        return distance * farePerKm;
    }

    @Override
    public void assignRoute(String route) {
        this.route = route;
        System.out.println("Bus assigned to route: " + route);
    }

    @Override
    public void getVehicleInfo() {
        System.out.println("Vehicle: Bus | Capacity: " + capacity + " | Fare/KM: " + farePerKm);
    }

    @Override
    public String getVehicleType() {
        return "Bus";
    }
}

class Taxi implements Vehicle {
    private final int capacity = 4;
    private final double farePerKm = 30;
    private String route;

    @Override
    public void startTrip() {
        System.out.println("Taxi trip started.");
    }

    @Override
    public double calculateFare(double distance) {
        return distance * farePerKm;
    }

    @Override
    public void assignRoute(String route) {
        this.route = route;
        System.out.println("Taxi assigned to route: " + route);
    }

    @Override
    public void getVehicleInfo() {
        System.out.println("Vehicle: Taxi | Capacity: " + capacity + " | Fare/KM: " + farePerKm);
    }

    @Override
    public String getVehicleType() {
        return "Taxi";
    }
}

class MotorcycleDelivery implements Vehicle {
    private final int capacity = 1;
    private final double farePerKm = 10;
    private String route;

    @Override
    public void startTrip() {
        System.out.println("Motorcycle delivery started.");
    }

    @Override
    public double calculateFare(double distance) {
        return distance * farePerKm;
    }

    @Override
    public void assignRoute(String route) {
        this.route = route;
        System.out.println("Motorcycle assigned to route: " + route);
    }

    @Override
    public void getVehicleInfo() {
        System.out.println("Vehicle: Motorcycle Delivery | Capacity: " + capacity + " | Fare/KM: " + farePerKm);
    }

    @Override
    public String getVehicleType() {
        return "Motorcycle Delivery";
    }
}

class ElectricScooter implements Vehicle {
    private final int capacity = 1;
    private final double farePerKm = 8;
    private String route;

    @Override
    public void startTrip() {
        System.out.println("Electric scooter trip started.");
    }

    @Override
    public double calculateFare(double distance) {
        return distance * farePerKm;
    }

    @Override
    public void assignRoute(String route) {
        this.route = route;
        System.out.println("Scooter assigned to route: " + route);
    }

    @Override
    public void getVehicleInfo() {
        System.out.println("Vehicle: Electric Scooter | Capacity: " + capacity + " | Fare/KM: " + farePerKm);
    }

    @Override
    public String getVehicleType() {
        return "Electric Scooter";
    }
}

// =========================================================
// ABSTRACT CREATOR
// The "factory method" createVehicle() is declared here.
// Subclasses decide which concrete vehicle to instantiate.
// =========================================================
abstract class VehicleFactory {

    // *** THE FACTORY METHOD ***
    // Subclasses override this to return their specific vehicle.
    public abstract Vehicle createVehicle();

    // Template method: common workflow that uses the factory method
    public Vehicle orderVehicle(String route, double distance) {
        // 1. Call factory method to get the product
        Vehicle vehicle = createVehicle();

        // 2. Use the product through its interface
        vehicle.getVehicleInfo();
        vehicle.assignRoute(route);
        vehicle.startTrip();

        double fare = vehicle.calculateFare(distance);
        System.out.println("Total Fare: BDT " + fare);
        System.out.println("----------------------------------");

        return vehicle;
    }
}

// =========================================================
// CONCRETE CREATORS
// Each subclass overrides createVehicle() to instantiate
// one specific type of Vehicle.
// =========================================================
class BusFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new Bus();
    }
}

class TaxiFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new Taxi();
    }
}

class MotorcycleDeliveryFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new MotorcycleDelivery();
    }
}

class ElectricScooterFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new ElectricScooter();
    }
}

// =========================================================
// FACTORY REGISTRY (optional helper)
// Maps a string key to the correct factory.
// Keeps client code free of if-else chains.
// =========================================================
class TransportationSystem {

    public static VehicleFactory getFactory(String vehicleType) {
        switch (vehicleType.toLowerCase()) {
            case "bus":
                return new BusFactory();
            case "taxi":
                return new TaxiFactory();
            case "motorcycle":
                return new MotorcycleDeliveryFactory();
            case "scooter":
                return new ElectricScooterFactory();
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
        }
    }
}

// =========================================================
// CLIENT / MAIN
// The client works only with VehicleFactory and Vehicle —
// it never references Bus, Taxi, etc. directly.
// =========================================================
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Vehicle> fleet = new ArrayList<>();

        System.out.println("=== Smart Transportation Management System ===");

        boolean running = true;
        while (running) {
            System.out.println("\nAvailable vehicle types: bus | taxi | motorcycle | scooter");
            System.out.print("Enter vehicle type (or 'exit' to quit): ");
            String type = scanner.nextLine().trim();

            if (type.equalsIgnoreCase("exit")) {
                running = false;
                continue;
            }

            System.out.print("Enter route name: ");
            String route = scanner.nextLine().trim();

            System.out.print("Enter distance (km): ");
            double distance = Double.parseDouble(scanner.nextLine().trim());

            try {
                // Client asks the FACTORY for a vehicle — no 'new Bus()' here!
                VehicleFactory factory = TransportationSystem.getFactory(type);
                Vehicle vehicle = factory.orderVehicle(route, distance);
                fleet.add(vehicle);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("\n=== Fleet Summary ===");
        System.out.println("Total vehicles dispatched: " + fleet.size());
        fleet.forEach(v -> System.out.println(" - " + v.getVehicleType()));

        scanner.close();
    }
}