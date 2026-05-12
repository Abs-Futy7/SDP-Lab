# Smart Transportation Management System

## Factory Method Design Pattern Explanation

This project demonstrates the **Factory Method Design Pattern** using a smart transportation management system. The system can create and dispatch different vehicle types such as `Bus`, `Taxi`, `MotorcycleDelivery`, and `ElectricScooter`.

The main idea is simple: the client code does not directly create vehicle objects using `new Bus()`, `new Taxi()`, or `new ElectricScooter()`. Instead, object creation is handled by factory classes. This keeps the object creation logic separate from the business workflow.

---

## Comparison With Previous Implementation

The previous implementation already had the basic vehicle model. It defined the `Vehicle` interface and four concrete classes:

- `Bus`
- `Taxi`
- `MotorcycleDelivery`
- `ElectricScooter`

Each concrete class implemented the required vehicle operations such as `startTrip()`, `calculateFare()`, `assignRoute()`, `getVehicleInfo()`, and `getVehicleType()`.

However, the previous implementation did not fully apply the Factory Method Design Pattern because it only defined the products. It did not define the factory classes responsible for creating those products.

### Previous Implementation Structure

```text
Vehicle interface
  |
  | implemented by
  v
Bus
Taxi
MotorcycleDelivery
ElectricScooter
```

In this version, the system had a good abstraction for vehicles, but object creation was not separated into a factory layer. If a client needed a bus, taxi, or scooter, the client would eventually need to create the object directly.

For example, without a factory, client code would look like this:

```java
Vehicle vehicle = new Bus();
```

This creates direct dependency between the client code and the concrete class `Bus`.

### Current Factory Method Implementation Structure

The current implementation keeps the previous `Vehicle` interface and concrete vehicle classes, then adds the Factory Method layer.

```text
Vehicle interface
  |
  | implemented by
  v
Concrete Vehicles

VehicleFactory
  |
  | extended by
  v
Concrete Factories
  |
  | create
  v
Concrete Vehicles
```

The new factory-related classes are:

- `VehicleFactory`
- `BusFactory`
- `TaxiFactory`
- `MotorcycleDeliveryFactory`
- `ElectricScooterFactory`
- `TransportationSystem`

Now the client does not create a vehicle directly. Instead, it asks the factory system for the correct factory:

```java
VehicleFactory factory = TransportationSystem.getFactory(type);
Vehicle vehicle = factory.orderVehicle(route, distance);
```

This is a cleaner design because the client only knows about `VehicleFactory` and `Vehicle`, not the concrete classes.

### Main Differences

| Topic | Previous Implementation | Current Factory Method Implementation |
|---|---|---|
| Product interface | Had `Vehicle` | Still has `Vehicle` |
| Concrete products | Had `Bus`, `Taxi`, `MotorcycleDelivery`, `ElectricScooter` | Still has the same concrete products |
| Factory method | Not present | Present as `createVehicle()` |
| Abstract creator | Not present | Present as `VehicleFactory` |
| Concrete creators | Not present | Present as separate factory classes |
| Object creation | Would be done directly using concrete classes | Done through factory classes |
| Client dependency | Client may depend on `Bus`, `Taxi`, etc. | Client depends on `VehicleFactory` and `Vehicle` |
| Scalability | Harder to manage as types increase | Easier to extend with new vehicle types |

### What Was Improved

The improved version adds a proper creation layer. This means the responsibility of creating objects is moved away from the client and placed inside factory classes.

The previous version answered:

```text
What can each vehicle do?
```

The current version answers both:

```text
What can each vehicle do?
How should the correct vehicle object be created?
```

That second question is exactly where the Factory Method Pattern becomes useful.

---

## Main Components of the Code

### 1. Product Interface: `Vehicle`

`Vehicle` is the common interface for all vehicle types.

```java
interface Vehicle {
    void startTrip();
    double calculateFare(double distance);
    void assignRoute(String route);
    void getVehicleInfo();
    String getVehicleType();
}
```

Every vehicle must follow this contract. Because of this, the system can treat all vehicles in the same way, even though their internal behavior is different.

For example:

- A `Bus` has capacity `40` and fare per km `15`.
- A `Taxi` has capacity `4` and fare per km `30`.
- A `MotorcycleDelivery` has capacity `1` and fare per km `10`.
- An `ElectricScooter` has capacity `1` and fare per km `8`.

All of them implement the same `Vehicle` interface.

---

### 2. Concrete Products

The concrete product classes are:

- `Bus`
- `Taxi`
- `MotorcycleDelivery`
- `ElectricScooter`

Each class provides its own implementation of the methods declared in `Vehicle`.

Example from `Bus`:

```java
class Bus implements Vehicle {
    private final int capacity = 40;
    private final double farePerKm = 15;

    @Override
    public void startTrip() {
        System.out.println("Bus trip started.");
    }

    @Override
    public double calculateFare(double distance) {
        return distance * farePerKm;
    }
}
```

This means each vehicle can calculate fare differently, show different information, and start trips in its own way.

---

### 3. Abstract Creator: `VehicleFactory`

`VehicleFactory` is the abstract creator class. It contains the factory method:

```java
public abstract Vehicle createVehicle();
```

This method is not implemented inside `VehicleFactory`. Instead, subclasses decide which vehicle object should be created.

`VehicleFactory` also contains the common workflow method:

```java
public Vehicle orderVehicle(String route, double distance)
```

This method performs the common steps for every vehicle:

1. Create a vehicle using `createVehicle()`.
2. Show vehicle information.
3. Assign a route.
4. Start the trip.
5. Calculate and display the fare.
6. Return the created vehicle.

So the workflow is shared, but the actual object creation is delegated to subclasses.

---

### 4. Concrete Creators

The concrete factory classes are:

- `BusFactory`
- `TaxiFactory`
- `MotorcycleDeliveryFactory`
- `ElectricScooterFactory`

Each factory overrides `createVehicle()` and returns one specific vehicle type.

Example:

```java
class BusFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new Bus();
    }
}
```

This is the main Factory Method behavior. `BusFactory` knows how to create a `Bus`, `TaxiFactory` knows how to create a `Taxi`, and so on.

---

### 5. Factory Registry: `TransportationSystem`

`TransportationSystem` works as a simple registry. It receives a vehicle type as text and returns the correct factory.

```java
VehicleFactory factory = TransportationSystem.getFactory(type);
```

Internally, it maps user input like `"bus"` or `"taxi"` to the correct factory class.

This keeps the `Main` class cleaner because `Main` does not need to directly decide which vehicle class should be created.

---

## How the Code Works Step by Step

1. The program starts from the `main()` method in the `Main` class.
2. A `Scanner` takes input from the user.
3. The user enters a vehicle type such as `bus`, `taxi`, `motorcycle`, or `scooter`.
4. The user enters a route name.
5. The user enters the distance in kilometers.
6. `Main` calls:

```java
VehicleFactory factory = TransportationSystem.getFactory(type);
```

7. `TransportationSystem` returns the correct factory object.
8. `Main` calls:

```java
Vehicle vehicle = factory.orderVehicle(route, distance);
```

9. Inside `orderVehicle()`, the factory method `createVehicle()` creates the correct vehicle object.
10. The vehicle information is printed.
11. The route is assigned.
12. The trip starts.
13. The fare is calculated using the vehicle's own fare rate.
14. The vehicle is added to the `fleet` list.
15. When the user exits, the program prints a fleet summary.

---

## How the Code Maintains the Factory Method Pattern

The Factory Method Pattern says that object creation should be delegated to subclasses instead of being handled directly by the client.

This code maintains the pattern in the following ways:

### Object creation is separated from client code

The `Main` class does not create vehicles like this:

```java
Vehicle vehicle = new Bus();
```

Instead, it asks a factory to create the vehicle:

```java
VehicleFactory factory = TransportationSystem.getFactory(type);
Vehicle vehicle = factory.orderVehicle(route, distance);
```

This means `Main` depends on abstraction, not concrete vehicle classes.

### `createVehicle()` is the factory method

The method:

```java
public abstract Vehicle createVehicle();
```

is the factory method. It defines the object creation operation but lets subclasses decide the exact object type.

### Concrete factories decide the concrete product

For example:

```java
class TaxiFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new Taxi();
    }
}
```

Here, `TaxiFactory` decides that the created product should be a `Taxi`.

### Common workflow is reused

The `orderVehicle()` method is written only once in `VehicleFactory`. All factories reuse it.

This avoids duplicate code. The system does not need separate order logic for bus, taxi, motorcycle, and scooter.

---

## How the Factory Method Works in This Program

The flow of Factory Method in this project is:

```text
Main
  |
  | asks for a factory
  v
TransportationSystem
  |
  | returns correct factory
  v
VehicleFactory reference
  |
  | calls orderVehicle()
  v
createVehicle()
  |
  | implemented by concrete factory
  v
Bus / Taxi / MotorcycleDelivery / ElectricScooter
```

The client only works with:

- `VehicleFactory`
- `Vehicle`

The client does not need to know the internal creation details of each vehicle.

---

## Why This Design Is Scalable

This design is scalable because new vehicle types can be added with minimum changes to existing code.

Suppose we want to add a new vehicle type called `MetroRail`.

We would need to:

1. Create a new class `MetroRail` that implements `Vehicle`.
2. Create a new factory class `MetroRailFactory` that extends `VehicleFactory`.
3. Add one new case in `TransportationSystem.getFactory()`.

Example:

```java
class MetroRail implements Vehicle {
    @Override
    public void startTrip() {
        System.out.println("Metro rail trip started.");
    }

    @Override
    public double calculateFare(double distance) {
        return distance * 20;
    }

    @Override
    public void assignRoute(String route) {
        System.out.println("Metro rail assigned to route: " + route);
    }

    @Override
    public void getVehicleInfo() {
        System.out.println("Vehicle: Metro Rail");
    }

    @Override
    public String getVehicleType() {
        return "Metro Rail";
    }
}

class MetroRailFactory extends VehicleFactory {
    @Override
    public Vehicle createVehicle() {
        return new MetroRail();
    }
}
```

Then register it:

```java
case "metro":
    return new MetroRailFactory();
```

The important point is that the existing `Main` class does not need to change. The trip ordering process also does not need to change because `orderVehicle()` already works with the `Vehicle` interface.

---

## Benefits of This Implementation

- **Loose coupling:** `Main` does not depend directly on concrete classes like `Bus` or `Taxi`.
- **Code reuse:** The common ordering workflow is written once in `VehicleFactory`.
- **Easy extension:** New vehicles can be added by creating a new product class and a new factory class.
- **Cleaner client code:** The client only asks for a factory and uses the returned vehicle through the `Vehicle` interface.
- **Better maintainability:** Vehicle creation logic stays inside factory classes instead of being spread across the program.

---

## Conclusion

This code follows the Factory Method Design Pattern by using an abstract `VehicleFactory` with a factory method named `createVehicle()`. Each concrete factory creates one specific type of vehicle, while the client works only with abstract types.

Because object creation is separated from the main program logic, the system becomes easier to maintain, easier to extend, and more scalable when new vehicle types are added.
