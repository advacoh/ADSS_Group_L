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
    
    private final String TEST_DB_URL = "jdbc:sqlite::memory:"; 
    private final int HR_USER_ID = 999999999;

    @BeforeEach
    void setUp() {
        EmployeeMapper employeeMapper = new EmployeeMapper(TEST_DB_URL);
        employeeMemory = new EmployeeMemory(employeeMapper);

        UserMapper testUserMapper = new UserMapper(TEST_DB_URL);
        userController = new UserController(testUserMapper); 
        
        driverRepository = new DriverRepository(TEST_DB_URL); 

        employeeController = new EmployeeController(userController, employeeMemory, driverRepository);

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
}