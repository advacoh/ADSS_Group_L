package domain.hr;

import repository.DriverRepository;
import dataAccess.hr.EmployeeMapper;
import dataAccess.hr.UserMapper;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@DisplayName("Employee Controller Integration Tests")
public class EmployeeIntegrationTest {

    private EmployeeController employeeController;
    private EmployeeMemory employeeMemory;
    private UserController userController;
    private DriverRepository driverRepository;

    private ShiftController shiftController;
    private ShiftMemory shiftMemory;
    private RequestMemory requestMemory;
    
    private final String TEST_DB_URL = "jdbc:sqlite::memory:"; 
    private final int HR_USER_ID = 999999999;

    @BeforeEach
    void setUp() {
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
        assertFalse(savedPreferences.get(nextMonday).get(ShiftType.EVENING), "Monday Evening should NOT be a preference");
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

    
}