// import domain.hr.*;
// import domain.transportation.*;
// import enums.*;

// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;

// public class Main {
//     public static void main(String[] args) {
//         System.out.println("=== Transport Integration Smoke Test ===");

//         ShiftMemory shiftMemory = new ShiftMemory();
//         EmployeeMemory employeeMemory = new EmployeeMemory();
//         UserMemory userMemory = new UserMemory();
//         UserController userController = new UserController(userMemory);
//         RequestMemory requestMemory = new RequestMemory();

//         ShiftController shiftController = new ShiftController(shiftMemory, employeeMemory, userController, requestMemory);
//         TransportController transportController = new TransportController(shiftController);

//         DeliveryZone southZone = new DeliveryZone(1, "South");

//         Site supplier = new Site(
//                 1,
//                 "Tnuva Supplier",
//                 "Industrial Area, Ashdod",
//                 "08-1234567",
//                 "Dana Levi",
//                 SiteType.SUPPLIER,
//                 southZone
//         );

//         Site branch = new Site(
//                 2,
//                 "Beer Sheva Branch",
//                 "Rager Blvd 10, Beer Sheva",
//                 "08-7654321",
//                 "Yossi Cohen",
//                 SiteType.BRANCH,
//                 southZone
//         );

//         Truck truck = new Truck(
//                 "123-45-678",
//                 "Volvo FH",
//                 8000,
//                 15000,
//                 LicenseType.C
//         );

//         Set<Certification> driverCertifications = new HashSet<>();

//         Driver validDriver = new Driver(
//                 206000001,
//                 "David Levi",
//                 123456,
//                 LocalDate.of(2024, 1, 1),
//                 EmpType.FULL_TIME,
//                 SalType.GLOBAL,
//                 10000,
//                 12,
//                 true,
//                 6,
//                 true,
//                 driverCertifications,
//                 LicenseType.C
//         );

//         Driver invalidDriver = new Driver(
//                 206000002,
//                 "Sarah Cohen",
//                 654321,
//                 LocalDate.of(2024, 1, 1),
//                 EmpType.FULL_TIME,
//                 SalType.GLOBAL, 
//                 9000,
//                 10,
//                 true,
//                 6,
//                 true,
//                 new HashSet<>(),
//                 LicenseType.B
//         );

//         transportController.addSite(supplier);
//         transportController.addSite(branch);
//         transportController.addTruck(truck);
//         transportController.addDriver(validDriver);
//         transportController.addDriver(invalidDriver);

//         TransportedItem milk = new TransportedItem(1, "Milk 3%", 100);
//         TransportedItem bread = new TransportedItem(2, "Bread", 50);

//         List<TransportedItem> items = new ArrayList<>();
//         items.add(milk);
//         items.add(bread);

//         DeliveryDocument document = new DeliveryDocument(1001, branch, items);

//         List<DeliveryDocument> documents = new ArrayList<>();
//         documents.add(document);

//         Delivery validDelivery = new Delivery(
//                 1,
//                 LocalDate.now(),
//                 LocalTime.of(8, 30),
//                 14000,
//                 DeliveryStatus.PLANNED,
//                 supplier,
//                 truck,
//                 validDriver,
//                 documents
//         );

//         Delivery wrongLicenseDelivery = new Delivery(
//                 2,
//                 LocalDate.now(),
//                 LocalTime.of(10, 0),
//                 12000,
//                 DeliveryStatus.PLANNED,
//                 supplier,
//                 truck,
//                 invalidDriver,
//                 documents
//         );

//         Delivery overweightDelivery = new Delivery(
//                 3,
//                 LocalDate.now(),
//                 LocalTime.of(11, 0),
//                 20000,
//                 DeliveryStatus.PLANNED,
//                 supplier,
//                 truck,
//                 validDriver,
//                 documents
//         );

//         System.out.println("\n--- Basic validation tests ---");
//         System.out.println("Driver C compatible with truck C: " +
//                 transportController.isDriverCompatibleWithTruck(validDriver, truck));

//         System.out.println("Driver B compatible with truck C: " +
//                 transportController.isDriverCompatibleWithTruck(invalidDriver, truck));

//         System.out.println("14000 overweight? " +
//                 transportController.isOverweight(14000, truck));

//         System.out.println("20000 overweight? " +
//                 transportController.isOverweight(20000, truck));

//         System.out.println("Valid delivery has documents? " +
//                 transportController.hasDocuments(validDelivery));

//         System.out.println("All documents valid? " +
//                 transportController.areAllDocumentsValid(validDelivery));

//         System.out.println("\n--- createDelivery tests ---");

//         boolean createdValid = transportController.createDelivery(validDelivery);
//         System.out.println("Valid delivery created: " + createdValid);
//         System.out.println("Valid delivery status: " + validDelivery.getStatus());

//         boolean createdWrongLicense = transportController.createDelivery(wrongLicenseDelivery);
//         System.out.println("Wrong license delivery created: " + createdWrongLicense);

//         boolean createdOverweight = transportController.createDelivery(overweightDelivery);
//         System.out.println("Overweight delivery created: " + createdOverweight);
//         System.out.println("Overweight delivery status: " + overweightDelivery.getStatus());

//         System.out.println("\n--- Repository content ---");
//         System.out.println("Sites count: " + transportController.getAllSites().size());
//         System.out.println("Trucks count: " + transportController.getAllTrucks().size());
//         System.out.println("Drivers count: " + transportController.getAllDrivers().size());
//         System.out.println("Deliveries count: " + transportController.getAllDeliveries().size());

//         System.out.println("\n=== Test finished ===");
//     }
// }