package domain.transportation;

import enums.DeliveryStatus;
import enums.SiteType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Delivery Domain Class Tests")
public class DeliveryTest {

    private Delivery delivery;
    private Site sourceSite;
    private Truck truck;
    
    // We will use a null driver to simplify the test setup since the Delivery class 
    // just holds the reference and doesn't call methods on it internally.
    private Driver driver = null; 
    
    private DeliveryDocument doc1;
    private DeliveryDocument doc2;
    private DeliveryDocument doc3;
    private DeliveryDocument doc4;

    @BeforeEach
    void setUp() {
        DeliveryZone dummyZone = new DeliveryZone(1, "North Zone");
        
        // Setup Sites
        sourceSite = new Site(10, "HQ", "123 Main", "555-0000", "Alice", SiteType.SUPPLIER, dummyZone);
        Site branch1 = new Site(20, "Branch A", "456 Side", "555-0001", "Bob", SiteType.BRANCH, dummyZone);
        Site branch2 = new Site(30, "Branch B", "789 East", "555-0002", "Charlie", SiteType.BRANCH, dummyZone);
        Site supplier = new Site(40, "Supplier X", "321 West", "555-0003", "Dave", SiteType.SUPPLIER, dummyZone);

        // Setup Documents
        doc1 = new DeliveryDocument(100, branch1, new ArrayList<>());
        doc2 = new DeliveryDocument(101, branch2, new ArrayList<>());
        doc3 = new DeliveryDocument(102, branch1, new ArrayList<>()); // Duplicate destination to test distinct
        doc4 = new DeliveryDocument(103, supplier, new ArrayList<>()); // Non branch destination to test filtering

        // Setup Truck
        truck = new Truck("123-45", "Volvo", 5000, 15000, null);

        // Initialize Delivery
        delivery = new Delivery(
            5001,
            LocalDate.of(2026, 1, 1),
            LocalTime.of(8, 30),
            10000.0,
            DeliveryStatus.PLANNED,
            sourceSite,
            truck,
            driver,
            List.of(doc1, doc2, doc3, doc4)
        );
    }

    @Test
    @DisplayName("Should successfully create a delivery with correct basic properties")
    void testValidDeliveryCreation() {
        assertEquals(5001, delivery.getId(), "Delivery ID should match initialized value");
        assertEquals(LocalDate.of(2026, 1, 1), delivery.getDate(), "Date should match initialized value");
        assertEquals(LocalTime.of(8, 30), delivery.getDepartureTime(), "Departure time should match initialized value");
        assertEquals(10000.0, delivery.getRecordedWeight(), "Weight should match initialized value");
        assertEquals(DeliveryStatus.PLANNED, delivery.getStatus(), "Status should match initialized value");
        assertEquals(sourceSite, delivery.getSource(), "Source site should match initialized reference");
        assertEquals(truck, delivery.getTruck(), "Truck should match initialized reference");
        assertNull(delivery.getDriver(), "Driver should be null as initialized");
        assertEquals(4, delivery.getDocuments().size(), "Should contain exactly 4 documents");
        assertEquals(0, delivery.getCurrentStep(), "Initial current step should be 0");
    }

    @Test
    @DisplayName("Should correctly filter out non-branches and return distinct branch IDs")
    void testGetBranchesLogic() {
        List<Integer> branches = delivery.getBranches();
        assertEquals(2, branches.size(), "Should return exactly 2 distinct branch IDs");
        assertTrue(branches.contains(20), "Branch ID 20 should be in the list");
        assertTrue(branches.contains(30), "Branch ID 30 should be in the list");
        assertFalse(branches.contains(40), "Supplier ID 40 should be filtered out");
    }

    @Test
    @DisplayName("Should successfully update standard mutable fields")
    void testStateUpdates() {
        // Test Status
        delivery.setStatus(DeliveryStatus.EXECUTING);
        assertEquals(DeliveryStatus.EXECUTING, delivery.getStatus(), "Status should update to EXECUTING");

        // Test Weight
        delivery.setRecordedWeight(9500.5);
        assertEquals(9500.5, delivery.getRecordedWeight(), "Recorded weight should update correctly");

        // Test Pending Reason
        delivery.setPendingReason("Missing Driver");
        assertEquals("Missing Driver", delivery.getPendingReason(), "Pending reason should update correctly");

        // Test Step Increment
        delivery.incrementStep();
        assertEquals(1, delivery.getCurrentStep(), "Current step should increment to 1");
        
        delivery.incrementStep();
        assertEquals(2, delivery.getCurrentStep(), "Current step should increment to 2");
    }

    @Test
    @DisplayName("Should update reference dependencies via setters")
    void testDependencySetters() {
        LocalDate newDate = LocalDate.of(2026, 12, 31);
        LocalTime newTime = LocalTime.of(14, 0);
        
        delivery.setDate(newDate);
        delivery.setDepartureTime(newTime);
        
        assertEquals(newDate, delivery.getDate(), "Date should update correctly");
        assertEquals(newTime, delivery.getDepartureTime(), "Departure time should update correctly");
        
        // Ensure documents can be overwritten
        delivery.setDocuments(new ArrayList<>());
        assertTrue(delivery.getDocuments().isEmpty(), "Documents list should be empty after setting a new list");
    }
}