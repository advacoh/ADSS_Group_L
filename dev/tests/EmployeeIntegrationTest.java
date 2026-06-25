package domain.hr;

import dataAccess.hr.EmployeeMapper;
import dataAccess.hr.UserMapper;
import dataAccess.hr.ShiftMapper;
import dataAccess.hr.OverrideRequestMapper;
import repository.DriverRepository;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import repository.DriverRepository;
import dataAccess.hr.EmployeeMapper;
import dataAccess.hr.UserMapper;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@DisplayName("Employee Controller Integration Tests")
public class EmployeeIntegrationTest {

    private EmployeeController employeeController;
    private EmployeeMemory employeeMemory;
    private UserController userController;
    private DriverRepository driverRepository;

    private ShiftController shiftController;
    private ShiftMemory shiftMemory;
    private RequestMemory requestMemory;
    
    private String TEST_DB_URL; 
    private Connection keepAliveConnection;

    private final int HR_USER_ID = 999999999;

    @BeforeEach
    void setUp() throws SQLException {
        TEST_DB_URL = "jdbc:sqlite:file:memdb_" + UUID.randomUUID().toString() + "?mode=memory&cache=shared";
        keepAliveConnection = DriverManager.getConnection(TEST_DB_URL);

        EmployeeMapper employeeMapper = new EmployeeMapper(TEST_DB_URL);
        employeeMemory = new EmployeeMemory(employeeMapper);

        UserMapper testUserMapper = new UserMapper(TEST_DB_URL);
        userController = new UserController(testUserMapper); 
        
        driverRepository = new DriverRepository(TEST_DB_URL); 

        ShiftMapper shiftMapper = new ShiftMapper(TEST_DB_URL);
        shiftMemory = new ShiftMemory(shiftMapper);

        OverrideRequestMapper requestMapper = new OverrideRequestMapper(TEST_DB_URL);
        requestMemory = new RequestMemory(requestMapper);

        employeeController = new EmployeeController(userController, employeeMemory, driverRepository);
        shiftController = new ShiftController(shiftMemory, employeeMemory, userController, requestMemory);

        employeeController.registerHR(HR_USER_ID, "secureHrPass123");
        userController.login(HR_USER_ID, "secureHrPass123"); 
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (keepAliveConnection != null && !keepAliveConnection.isClosed()) {
            keepAliveConnection.close();
        }
    }


    @Test
    @DisplayName("Should successfully allow HR to set role requirements for a shift")
    void testHRSetShiftRequirement() {
        // Arrange
        int branchId = 1;
        
        LocalDate nextSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        LocalDate validShiftDate = nextSunday.plusDays(3); 
        
        ShiftType shiftType = ShiftType.MORNING;
        
        // Define the roles 
        Certification role1 = Certification.CASHIER;
        int requiredCashiers = 3;
        
        Certification role2 = Certification.SHIFT_MANAGER;
        int requiredManagers = 1;

        assertDoesNotThrow(() -> {
            shiftController.createShift(HR_USER_ID, branchId, validShiftDate, shiftType);
        }, "HR should be able to create a valid shift in the next week");

        // Act
        assertDoesNotThrow(() -> {
            shiftController.setRequirement(HR_USER_ID, branchId, validShiftDate, shiftType, role1, requiredCashiers);
            shiftController.setRequirement(HR_USER_ID, branchId, validShiftDate, shiftType, role2, requiredManagers);
        }, "HR should be able to successfully set staffing requirements for multiple roles");

        // Assert
        Shift savedShift = assertDoesNotThrow(() -> {
            return shiftController.getShift(HR_USER_ID, branchId, validShiftDate, shiftType);
        }, "HR should be able to retrieve the shift");
        
        assertNotNull(savedShift, "The shift should exist in the system");

        assertEquals(requiredCashiers, savedShift.getRequiredRoles().getOrDefault(role1, 0), "The shift should require exactly 3 Cashiers");
        assertEquals(requiredManagers, savedShift.getRequiredRoles().getOrDefault(role2, 0), "The shift should require exactly 1 Shift Manager");
        
        assertEquals(0, savedShift.getRequiredRoles().getOrDefault(Certification.WAREHOUSE, 0), "Unset roles should have a requirement of 0");
        
    }

    @Test
    @DisplayName("Should successfully create an employee and save to the test database")
    void testAddEmployeeSuccess() {
        // Arrange
        int newEmpId = 100000005;
        int branchId = 1;
        String name = "Integration Test User";
        String password = "password123";
        int bankAccount = 112233;
        LocalDate startDate = LocalDate.now();
        EmpType empType = EmpType.FULL_TIME;
        SalType salType = SalType.GLOBAL;
        int salary = 12000;
        int vacationDays = 14;
        boolean willOvertime = true;
        int dayOff = 6; 
        boolean doubleShiftAllowed = false;
        
        Set<Certification> certs = new HashSet<>();
        certs.add(Certification.CASHIER);

        // Act
        assertDoesNotThrow(() -> {
            employeeController.addEmployee(
                HR_USER_ID, branchId, newEmpId, password, name, bankAccount, 
                startDate, empType, salType, salary, vacationDays, 
                willOvertime, dayOff, doubleShiftAllowed, certs, null
            );
        });

        // Assert
        Employee savedEmployee = employeeMemory.get(newEmpId);
        
        assertNotNull(savedEmployee, "Employee should have been saved to the database");
        assertEquals(name, savedEmployee.getName(), "Database should match the saved name");
        assertEquals(salary, savedEmployee.getSalary(), "Database should match the saved salary");
        assertEquals(Status.ACTIVE, savedEmployee.getStatus(), "New employees should be active by default");
        assertTrue(savedEmployee.isCertified(Certification.CASHIER), "Certifications should be saved to the database");
        assertDoesNotThrow(() -> userController.isLogged(newEmpId), "Employee should be registered in the User table");
    }

    @Test
    @DisplayName("Should fail to create an employee if the HR user is not logged in")
    void testAddEmployeeFailsWhenNotLogged() {
        // Arrange
        int newEmpId = 100000006;
        userController.logout(HR_USER_ID); 

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            employeeController.addEmployee(
                HR_USER_ID, 1, newEmpId, "pass123", "Test User", 12345, 
                LocalDate.now(), EmpType.FULL_TIME, SalType.GLOBAL, 10000, 
                10, false, 7, false, new HashSet<>(), null
            );
        });

        assertTrue(exception.getMessage().contains("Access Denied"));
        assertNull(employeeMemory.get(newEmpId));
    }

    @Test
    @DisplayName("Should successfully save an employee's weekly constraints and preferences")
    void testEmployeeSubmitsConstraintsAndPreferences() {
        // Arrange
        int empId = 100000007;
        int branchId = 1;
        
        employeeController.addEmployee(
            HR_USER_ID, branchId, empId, "empPass123", "Scheduling User", 55555, 
            LocalDate.now(), EmpType.FULL_TIME, SalType.GLOBAL, 8000, 10, 
            true, 6, false, Set.of(Certification.CASHIER), null
        );
        
        userController.login(empId, "empPass123");

        // Calculate a valid Monday for the next week 
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        Map<LocalDate, Set<ShiftType>> constraints = new HashMap<>();
        constraints.put(nextMonday, new HashSet<>(Set.of(ShiftType.MORNING, ShiftType.EVENING)));

        // Only prefers Morning
        Map<LocalDate, Set<ShiftType>> preferences = new HashMap<>();
        preferences.put(nextMonday, new HashSet<>(Set.of(ShiftType.MORNING)));

        // Act
        assertDoesNotThrow(() -> {
            shiftController.setWeeklyConstraints(empId, constraints);
            shiftController.setWeeklyPreferences(empId, preferences);
        });

        // Assert
        Map<LocalDate, Map<ShiftType, Boolean>> savedConstraints = shiftController.getWeeklyConstraints(empId);
        Map<LocalDate, Map<ShiftType, Boolean>> savedPreferences = shiftController.getWeeklyPreferences(empId);

        assertTrue(savedConstraints.get(nextMonday).get(ShiftType.MORNING), "Monday Morning should be an available constraint");
        assertTrue(savedConstraints.get(nextMonday).get(ShiftType.EVENING), "Monday Evening should be an available constraint");
        assertTrue(savedPreferences.get(nextMonday).get(ShiftType.MORNING), "Monday Morning should be a preference");
    }


    @Test
    @DisplayName("Should successfully allow HR to assign an available employee to a shift")
    void testHRAssignsEmployeeToShift() {
        // Arrange
        int empId = 100000008;
        int branchId = 1;
        
        LocalDate nextSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        LocalDate validShiftDate = nextSunday.plusDays(1); // The Monday of that valid week
        
        ShiftType shiftType = ShiftType.MORNING;
        Certification role = Certification.CASHIER;

        employeeController.addEmployee(
            HR_USER_ID, branchId, empId, "empPass123", "Available Employee", 55555, 
            LocalDate.now(), EmpType.FULL_TIME, SalType.GLOBAL, 8000, 10, 
            true, 6, false, Set.of(role), null
        );

        userController.login(empId, "empPass123");
        Map<LocalDate, Set<ShiftType>> constraints = new HashMap<>();
        constraints.put(validShiftDate, new HashSet<>(Set.of(shiftType)));
        
        shiftController.setWeeklyConstraints(empId, constraints);
        userController.logout(empId); 

        shiftController.createShift(HR_USER_ID, branchId, validShiftDate, shiftType);
        shiftController.setRequirement(HR_USER_ID, branchId, validShiftDate, shiftType, role, 1);

        // Act - HR assigns the employee to the shift
        assertDoesNotThrow(() -> {
            shiftController.assignEmployee(HR_USER_ID, branchId, empId, validShiftDate, shiftType, role, false);
        });

        // Assert
        Shift savedShift = shiftController.getShift(HR_USER_ID, branchId, validShiftDate, shiftType);
        
        assertTrue(savedShift.isEmployeeAssigned(empId), "Employee should be assigned to the shift");
        assertTrue(savedShift.isAssignedAsRole(role, empId), "Employee should be assigned specifically as a Cashier");
    }

    // @Test
    // @DisplayName("Should successfully process an override request: HR submits, Employee approves, HR assigns")
    // void testOverrideRequestFlow() {
    //     // Arrange
    //     int empId = 100000009; 
    //     int branchId = 1;
    //     String password = "overridePass123";
        
    //     LocalDate nextSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
    //     LocalDate validShiftDate = nextSunday.plusDays(2); 
        
    //     ShiftType shiftType = ShiftType.EVENING;
    //     Certification role = Certification.CASHIER;

        
    //     employeeController.addEmployee(
    //         HR_USER_ID, branchId, empId, password, "Override Target", 55555, 
    //         LocalDate.now(), EmpType.FULL_TIME, SalType.GLOBAL, 8000, 10, 
    //         true, 6, false, Set.of(role), null
    //     );

    //     // Employee sets constraints making himself unavailable for the Evening shift
    //     shiftController.createShift(HR_USER_ID, branchId, validShiftDate, shiftType);
    //     shiftController.setRequirement(HR_USER_ID, branchId, validShiftDate, shiftType, role, 1);

    //     userController.login(empId, password);
    //     Map<LocalDate, Set<ShiftType>> constraints = new HashMap<>();
    //     constraints.put(validShiftDate, new HashSet<>(Set.of(ShiftType.MORNING))); 
    //     shiftController.setWeeklyConstraints(empId, constraints);
    //     userController.logout(empId);

    //     // Act & Assert

    //     String requestId = assertDoesNotThrow(() -> {
    //         return shiftController.createOverrideRequest(HR_USER_ID, branchId, empId, validShiftDate, shiftType, role);
    //     }, "HR should be able to create an override request for an unavailable employee");
        
    //     assertNotNull(requestId, "Override request ID should be generated");

    //     userController.login(empId, password);
        
    //     assertDoesNotThrow(() -> {
    //         shiftController.respondToRequest(empId, requestId, true);
    //     }, "Employee should be able to approve the pending request");
        
    //     OverrideRequest request = shiftController.viewRequest(empId, requestId);
    //     assertEquals(RequestStatus.APPROVED, request.getStatus(), "The request status should now be APPROVED");
        
    //     userController.logout(empId);

    //     assertDoesNotThrow(() -> {
    //         shiftController.assignWithOverride(HR_USER_ID, branchId, requestId);
    //     }, "HR should successfully assign the employee using the approved override request");

    //     Shift savedShift = shiftController.getShift(HR_USER_ID, branchId, validShiftDate, shiftType);
        
    //     assertTrue(savedShift.isEmployeeAssigned(empId), "Employee should be formally assigned to the shift");
    //     assertTrue(savedShift.isAssignedAsRole(role, empId), "Employee should be assigned specifically as a Cashier");
    // }

    
    @Test
    @DisplayName("Should successfully allow HR to view details of an INACTIVE (dismissed) employee")
    void testHRViewsInactiveEmployeeDetails() {
        // Arrange
        int targetEmpId = 100000010; 
        int branchId = 1;
        String password = "toBeDismissed123";
        Certification role = Certification.CASHIER;

        employeeController.addEmployee(
            HR_USER_ID, branchId, targetEmpId, password, "Departed Employee", 55555, 
            LocalDate.now(), EmpType.FULL_TIME, SalType.GLOBAL, 8000, 10, 
            true, 6, false, Set.of(role), null
        );

        assertDoesNotThrow(() -> {
            employeeController.dismissEmployee(HR_USER_ID, branchId, targetEmpId);
        }, "HR should be able to successfully dismiss the employee");

        // Act
        Employee retrievedEmployee = assertDoesNotThrow(() -> {
            return employeeController.getEmployeeDetails(HR_USER_ID, branchId, targetEmpId);
        }, "HR should be able to retrieve details of an inactive employee");

        // Assert
        assertNotNull(retrievedEmployee, "The system should still return the employee object");
        assertEquals(targetEmpId, retrievedEmployee.getID(), "Retrieved employee ID should match");
        assertEquals("Departed Employee", retrievedEmployee.getName(), "Retrieved employee name should match");
        
        assertEquals(Status.INACTIVE, retrievedEmployee.getStatus(), "Employee status must be INACTIVE in the system");
        
        assertThrows(RuntimeException.class, () -> {
            userController.login(targetEmpId, password);
        }, "Dismissed employee's user credentials should be deleted, preventing login");
    }

    @Test
    @DisplayName("Should successfully allow HR to view shifts that are in history")
    void testHRViewsPastShiftHistory() {
        // Arrange
        int branchId = 1;
        
        LocalDate pastDate = LocalDate.now().minusDays(1);
        ShiftType shiftType = ShiftType.MORNING;
        String shiftId = branchId + "_" + pastDate.toString() + "_" + shiftType.name();
        
        // We bypass shiftController.createShift() because it restricts creation to the next week.
        // By saving directly to shiftMemory, we simulate a shift that was created in the past.
        Shift pastShift = new Shift(shiftId, branchId, pastDate, shiftType);
        shiftMemory.save(pastShift); 

        // Act
        Shift retrievedShift = assertDoesNotThrow(() -> {
            return shiftController.getPastShift(HR_USER_ID, branchId, pastDate, shiftType);
        }, "HR should be able to successfully retrieve a past shift");

        // Assert
        assertNotNull(retrievedShift, "The retrieved past shift should not be null");
        assertEquals(pastDate, retrievedShift.getDate(), "The date of the retrieved shift should match the past date");
        assertEquals(shiftType, retrievedShift.getType(), "The shift type should match");
        assertEquals(branchId, retrievedShift.getBranchId(), "The branch ID should match");
        
        assertThrows(IllegalArgumentException.class, () -> {
             shiftController.getShift(HR_USER_ID, branchId, pastDate, shiftType);
        }, "Standard getShift should fail because the shift has been moved to history");
    }


}