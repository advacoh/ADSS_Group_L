
// import domain.hr.*;
// import domain.transportation.*;
// import enums.DeliveryStatus;
// import enums.LicenseType;
// import enums.SiteType;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.ArrayList;
// import java.util.List;
// import static org.junit.jupiter.api.Assertions.*;

// public class TransportControllerTest {

//     private TransportController controller;
//     private Truck heavyTruck;
//     private Driver qualifiedDriver;
//     private Driver unqualifiedDriver;

//     //this method runs before each test and resets the system so that tests do not affect each other
//     @BeforeEach
//     public void setUp() {
//         controller = new TransportController();
//         //Assuming that C1 is the highest license
//         heavyTruck = new Truck("123-45-678", "Volvo", 5000, 15000, LicenseType.C1);
//         qualifiedDriver = new Driver("111111111", "Yossi", LicenseType.C1);
//         unqualifiedDriver = new Driver("222222222", "Dani", LicenseType.C);
//     }

//     //add truck and then check if it was added correctly
//     @Test
//     public void testAddAndRetrieveTruck() {
//         boolean isAdded = controller.addTruck(heavyTruck);
//         assertTrue(isAdded, "Truck should be added successfully");

//         List<Truck> trucks = controller.getAllTrucks();
//         assertEquals(1, trucks.size(), "Truck list should contain 1 truck");
//         assertEquals("123-45-678", trucks.get(0).getLicenseNumber(), "License number should match");
//     }

//     // add driver and then check if it was added correctly
//     @Test
//     public void testAddAndRetrieveDriver() {
//         controller.addDriver(qualifiedDriver);
//         List<Driver> drivers = controller.getAllDrivers();
        
//         assertEquals(1, drivers.size(), "Driver list should contain 1 driver");
//         assertEquals("Yossi", drivers.get(0).getName(), "Driver name should match");
//     }

//     // testing if a driver with the correct license type is compatible with the truck that requires that license type
//     @Test
//     public void testIsDriverCompatibleWithTruck_True() {
//         boolean isCompatible = controller.isDriverCompatibleWithTruck(qualifiedDriver, heavyTruck);
//         assertTrue(isCompatible, "Driver with C1 license should be compatible with C1 truck");
//     }

//     // test on driver with a license that does not match the truck's required license type
//     @Test
//     public void testIsDriverCompatibleWithTruck_False() {
//         boolean isCompatible = controller.isDriverCompatibleWithTruck(unqualifiedDriver, heavyTruck);
//         assertFalse(isCompatible, "Driver with C license should NOT be compatible with CE truck");
//     }

//     // testing over weight
//     @Test
//     public void testIsOverweight_True() {
//         boolean isOverweight = controller.isOverweight(16000, heavyTruck);
//         assertTrue(isOverweight, "Should return true when recorded weight exceeds max capacity");
//     }

//     // testing not overweight
//     @Test
//     public void testIsOverweight_False() {
//         boolean isOverweight = controller.isOverweight(10000, heavyTruck);
//         assertFalse(isOverweight, "Should return false when recorded weight is within capacity");
//     }

//     // testing creating transport failed due to incorrect license
//     @Test
//     public void testCreateDelivery_FailsDueToIncompatibleDriver() {
//         Delivery delivery = new Delivery(1, LocalDate.now(), LocalTime.now(), 10000, DeliveryStatus.PLANNED, 
//                 null, heavyTruck, unqualifiedDriver, new ArrayList<>());
        
//         boolean result = controller.createDelivery(delivery);
//         assertFalse(result, "Delivery creation should fail due to incompatible driver");
//         assertEquals(0, controller.getAllDeliveries().size(), "Delivery should not be added to repository");
//     }

//     // Attempt to create a shipment failed due to weight exceedance and customized status update
//     @Test
//     public void testCreateDelivery_FailsDueToOverweightAndUpdatesStatus() {
//         Delivery delivery = new Delivery(2, LocalDate.now(), LocalTime.now(), 20000, DeliveryStatus.PLANNED, 
//                 null, heavyTruck, qualifiedDriver, new ArrayList<>());
        
//         boolean result = controller.createDelivery(delivery);
//         assertFalse(result, "Delivery creation should fail due to overweight");
//         assertEquals(DeliveryStatus.OVERWEIGHT, delivery.getStatus(), "Delivery status should change to OVERWEIGHT");
//         assertEquals(0, controller.getAllDeliveries().size(), "Delivery should not be added to repository");
//     }

//     // Attempt to create transport failed due to lack of documents (empty list)    
//     @Test
//     public void testCreateDelivery_FailsDueToNoDocuments() {
//         List<DeliveryDocument> emptyDocs = new ArrayList<>();
//         Delivery delivery = new Delivery(3, LocalDate.now(), LocalTime.now(), 10000, DeliveryStatus.PLANNED, 
//                 null, heavyTruck, qualifiedDriver, emptyDocs);
        
//         boolean result = controller.createDelivery(delivery);
//         assertFalse(result, "Delivery creation should fail when document list is empty");
//     }

//     // Successfully creating a transport with all data correct
//     @Test
//     public void testCreateDelivery_Success() {
//         DeliveryZone zone = new DeliveryZone(1, "Center");

//         Site destination = new Site(
//                 1,
//                 "Tel Aviv Store",
//                 "Rothschild 1",
//                 "0501234567",
//                 "Dan",
//                 SiteType.BRANCH,
//                 zone
//         );

//         List<TransportedItem> items = new ArrayList<>();
//         items.add(new TransportedItem(1, "Milk", 10)); // תתאימי אם צריך

//         DeliveryDocument doc = new DeliveryDocument(1, destination, items);

//         List<DeliveryDocument> validDocs = new ArrayList<>();
//         validDocs.add(doc);

//         Delivery delivery = new Delivery(
//                 4,
//                 LocalDate.now(),
//                 LocalTime.now(),
//                 10000,
//                 DeliveryStatus.PLANNED,
//                 null,
//                 heavyTruck,
//                 qualifiedDriver,
//                 validDocs
//         );

//         boolean result = controller.createDelivery(delivery);

//         assertTrue(result, "Delivery creation should be successful");
//     }
// }