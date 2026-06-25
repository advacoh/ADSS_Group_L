package domain.transportation;

import dataAccess.hr.EmployeeMapper;
import dataAccess.hr.OverrideRequestMapper;
import dataAccess.hr.ShiftMapper;
import dataAccess.hr.UserMapper;
import domain.hr.*;
import enums.DeliveryStatus;
import enums.LicenseType;
import enums.SiteType;
import org.junit.jupiter.api.*;
import repository.DriverRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Transport Controller Integration Tests")
public class TransportIntegrationTest {

    private EmployeeController employeeController;
    private ShiftController shiftController;
    private TransportController transportController;
    private UserController userController;

    private String TEST_DB_URL;
    private Connection keepAliveConnection;

    private final int HR_USER_ID = 999999999;
    private final int TRANSPORT_MANAGER_ID = 888888888;
    private final int BRANCH_ID = 2; 

    @BeforeEach
    void setUp() throws SQLException {

        TEST_DB_URL = "jdbc:sqlite:file:memdb_" + UUID.randomUUID().toString() + "?mode=memory&cache=shared";
        keepAliveConnection = DriverManager.getConnection(TEST_DB_URL);

        EmployeeMapper employeeMapper = new EmployeeMapper(TEST_DB_URL);
        EmployeeMemory employeeMemory = new EmployeeMemory(employeeMapper);

        UserMapper testUserMapper = new UserMapper(TEST_DB_URL);
        userController = new UserController(testUserMapper);

        DriverRepository driverRepository = new DriverRepository(TEST_DB_URL);

        ShiftMapper shiftMapper = new ShiftMapper(TEST_DB_URL);
        ShiftMemory shiftMemory = new ShiftMemory(shiftMapper);

        OverrideRequestMapper requestMapper = new OverrideRequestMapper(TEST_DB_URL);
        RequestMemory requestMemory = new RequestMemory(requestMapper);

        employeeController = new EmployeeController(userController, employeeMemory, driverRepository);
        shiftController = new ShiftController(shiftMemory, employeeMemory, userController, requestMemory);
        transportController = new TransportController(shiftController, driverRepository);

        employeeController.registerHR(HR_USER_ID, "hrPass123");
        employeeController.registerTransportManager(TRANSPORT_MANAGER_ID, "Transport Boss", "transPass123");
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (keepAliveConnection != null && !keepAliveConnection.isClosed()) {
            keepAliveConnection.close();
        }
    }

    @Test
    @DisplayName("Should successfully allow a logged-in Transport Manager to create a valid delivery")
    void testTransportManagerCreatesDelivery() {
        
        // Arrange
        LocalDate nextSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        LocalDate deliveryDate = nextSunday.plusDays(1); 
        LocalTime deliveryTime = LocalTime.of(9, 0);     
        ShiftType shiftType = ShiftType.MORNING;

        int dayOff = nextSunday.plusDays(3).getDayOfWeek().getValue(); 

        userController.login(HR_USER_ID, "hrPass123");

        int driverId = 100000200;
        LicenseType requiredLicense = LicenseType.C;
        employeeController.addEmployee(
            HR_USER_ID, BRANCH_ID, driverId, "driverPass123", "Test Driver", 111, 
            LocalDate.now(), EmpType.FULL_TIME, SalType.GLOBAL, 9000, 10, 
            true, dayOff, false, new HashSet<>(Set.of(Certification.DRIVER)), requiredLicense
        );

        int warehouseEmpId = 100000201;
        employeeController.addEmployee(
            HR_USER_ID, BRANCH_ID, warehouseEmpId, "whPass123", "Test Warehouse", 222, 
            LocalDate.now(), EmpType.FULL_TIME, SalType.GLOBAL, 7000, 10, 
            true, dayOff, false, new HashSet<>(Set.of(Certification.WAREHOUSE)), null
        );

        // Schedule both employees for the morning shift on the delivery day
        shiftController.createShift(HR_USER_ID, BRANCH_ID, deliveryDate, shiftType);
        shiftController.setRequirement(HR_USER_ID, BRANCH_ID, deliveryDate, shiftType, Certification.DRIVER, 1);
        shiftController.setRequirement(HR_USER_ID, BRANCH_ID, deliveryDate, shiftType, Certification.WAREHOUSE, 1);
        
        shiftController.assignEmployee(HR_USER_ID, BRANCH_ID, driverId, deliveryDate, shiftType, Certification.DRIVER, false);
        shiftController.assignEmployee(HR_USER_ID, BRANCH_ID, warehouseEmpId, deliveryDate, shiftType, Certification.WAREHOUSE, false);
        
        userController.logout(HR_USER_ID);

        int sourceSiteId = 1;
        int destSiteId = BRANCH_ID; 
        String truckLicense = "123-45-678";
        
        transportController.addSite(sourceSiteId, "Logistics Center", "123 Main St", "555-0100", "Alice", SiteType.SUPPLIER, 1, "North");
        transportController.addSite(destSiteId, "Branch 2", "456 Branch St", "555-0200", "Bob", SiteType.BRANCH, 1, "North");
        transportController.addTruck(truckLicense, "Volvo FMX", 5000.0, 15000.0, requiredLicense);

        List<TransportedItem> items = List.of(new TransportedItem(1, "Apples", 50));
        TransportController.DocInput docInput = new TransportController.DocInput(1001, destSiteId, items);

        // Act
        userController.login(TRANSPORT_MANAGER_ID, "transPass123");
        
        assertTrue(employeeController.isDeliveryManager(TRANSPORT_MANAGER_ID), "User should be recognized as a Delivery Manager");

        int deliveryId = 5001;
        double recordedWeight = 6000.0; 

        boolean isCreated = assertDoesNotThrow(() -> {
            return transportController.createDelivery(
                deliveryId, deliveryDate, deliveryTime, recordedWeight,
                sourceSiteId, truckLicense, driverId, List.of(docInput)
            );
        }, "Transport Manager should be able to create the delivery without throwing exceptions");

        // Assert
        
        assertTrue(isCreated, "createDelivery should return true indicating a successful creation");

        // Verify it was correctly persisted in the domain
        List<Delivery> allDeliveries = transportController.getAllDeliveries();
        Delivery savedDelivery = allDeliveries.stream()
            .filter(d -> d.getId() == deliveryId)
            .findFirst()
            .orElse(null);

        assertNotNull(savedDelivery, "The delivery should exist in the db");
        
        // Because the driver and warehouse are properly scheduled, it should bypass PENDING
        assertEquals(DeliveryStatus.READY, savedDelivery.getStatus(), "Delivery status should be READY because the staffing verification passed");
        assertEquals(driverId, savedDelivery.getDriver().getId(), "Assigned driver ID should match");
        assertEquals(truckLicense, savedDelivery.getTruck().getLicenseNumber(), "Assigned truck license should match");
        
        userController.logout(TRANSPORT_MANAGER_ID);
    }
}