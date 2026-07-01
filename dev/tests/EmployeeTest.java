package domain.hr;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.*;

@DisplayName("Employee Domain Class Tests")
public class EmployeeTest {

    private Employee employee;

    private LocalDate getNextWeekDay(DayOfWeek targetDay) {
        LocalDate sunday = LocalDate.now();
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }
        return sunday.plusDays(targetDay.getValue() % 7);
    }

    @BeforeEach
    void setUp() {
        employee = new Employee(
            100000001, "Test User", 123456,
            LocalDate.of(2024, 1, 1),
            EmpType.FULL_TIME, SalType.GLOBAL, 10000,
            15, true, 6, false, 
            new HashSet<>(List.of(Certification.CASHIER))
        );
    }

    @Nested
    @DisplayName("1. Constructor Validation & Instantiation")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should successfully create an employee with correct basic properties")
        void testValidEmployeeCreation() {
            assertEquals("Test User", employee.getName());
            assertEquals(100000001, employee.getID());
            assertEquals(10000, employee.getSalary());
            assertEquals(15, employee.getVacation());
            assertEquals(Status.ACTIVE, employee.getStatus());
        }

        @Test
        @DisplayName("Should throw NullPointerException when required objects are null")
        void testNullValidations() {
            assertThrows(NullPointerException.class, () -> new Employee(100000001, null, 123456, LocalDate.of(2024, 1, 1), EmpType.FULL_TIME, SalType.GLOBAL, 10000, 15, true, 6, false, new HashSet<>(List.of(Certification.CASHIER))));
            assertThrows(NullPointerException.class, () -> new Employee(100000001, "Test User", 123456, null, EmpType.FULL_TIME, SalType.GLOBAL, 10000, 15, true, 6, false, new HashSet<>(List.of(Certification.CASHIER))));
            assertThrows(NullPointerException.class, () -> new Employee(100000001, "Test User", 123456, LocalDate.of(2024, 1, 1), null, SalType.GLOBAL, 10000, 15, true, 6, false, new HashSet<>(List.of(Certification.CASHIER))));
            assertThrows(NullPointerException.class, () -> new Employee(100000001, "Test User", 123456, LocalDate.of(2024, 1, 1), EmpType.FULL_TIME, null, 10000, 15, true, 6, false, new HashSet<>(List.of(Certification.CASHIER))));
            assertThrows(NullPointerException.class, () -> new Employee(100000001, "Test User", 123456, LocalDate.of(2024, 1, 1), EmpType.FULL_TIME, SalType.GLOBAL, 10000, 15, true, 6, false, null));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for negative or invalid number inputs")
        void testNumericValidations() {
            assertThrows(IllegalArgumentException.class, () -> new Employee(100000001, "Test User", 123456, LocalDate.of(2024, 1, 1), EmpType.FULL_TIME, SalType.GLOBAL, -1, 15, true, 6, false, new HashSet<>()));
            assertThrows(IllegalArgumentException.class, () -> new Employee(100000001, "Test User", 123456, LocalDate.of(2024, 1, 1), EmpType.FULL_TIME, SalType.GLOBAL, 0, 15, true, 6, false, new HashSet<>()));
            assertThrows(IllegalArgumentException.class, () -> new Employee(100000001, "Test User", 123456, LocalDate.of(2024, 1, 1), EmpType.FULL_TIME, SalType.GLOBAL, 10000, -1, true, 6, false, new HashSet<>()));
            assertThrows(IllegalArgumentException.class, () -> new Employee(100000001, "Test User", -1, LocalDate.of(2024, 1, 1), EmpType.FULL_TIME, SalType.GLOBAL, 10000, 15, true, 6, false, new HashSet<>()));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when dayOff is outside 1-7 range")
        void testDayOffBoundaries() {
            assertThrows(IllegalArgumentException.class, () -> new Employee(100000001, "Test User", 123456, LocalDate.of(2024, 1, 1), EmpType.FULL_TIME, SalType.GLOBAL, 10000, 15, true, 0, false, new HashSet<>()));
            assertThrows(IllegalArgumentException.class, () -> new Employee(100000001, "Test User", 123456, LocalDate.of(2024, 1, 1), EmpType.FULL_TIME, SalType.GLOBAL, 10000, 15, true, 8, false, new HashSet<>()));
        }
    }

    @Nested
    @DisplayName("2. Certifications & Role Checks")
    class CertificationTests {

        @Test
        @DisplayName("Should correctly initialize and identify baseline certifications")
        void testInitialCertifications() {
            assertTrue(employee.isCertified(Certification.CASHIER));
            assertFalse(employee.isHR());
        }

        @Test
        @DisplayName("Should allow adding new, non-duplicate certifications")
        void testAddCertification() {
            employee.addCertification(Certification.WAREHOUSE);
            assertTrue(employee.isCertified(Certification.WAREHOUSE));
            
            employee.addCertification(Certification.CASHIER);
            long count = employee.getCertifications().stream().filter(c -> c == Certification.CASHIER).count();
            assertEquals(1, count, "Duplicate certifications should not be added to the Set");
        }

        @Test
        @DisplayName("Should allow removing certifications and return boolean status")
        void testRemoveCertification() {
            assertTrue(employee.removeCertification(Certification.CASHIER));
            assertFalse(employee.isCertified(Certification.CASHIER));
            
            assertFalse(employee.removeCertification(Certification.HR_MANAGER), "Removing non-existent cert should return false");
        }

        @Test
        @DisplayName("Should accurately reflect HR status based on current certifications")
        void testHRRoleToggle() {
            employee.addCertification(Certification.HR_MANAGER);
            assertTrue(employee.isHR());
            
            employee.removeCertification(Certification.HR_MANAGER);
            assertFalse(employee.isHR());
        }
    }

    @Nested
    @DisplayName("3. Weekly Scheduling (Constraints & Preferences)")
    class WeeklySubmissionTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException on null map submissions")
        void testNullSubmissions() {
            assertThrows(IllegalArgumentException.class, () -> employee.setWeeklyConstraints(null));
            assertThrows(IllegalArgumentException.class, () -> employee.setWeeklyPreferences(null));
        }

        @Test
        @DisplayName("Should successfully submit constraints for a valid work day")
        void testSetValidWeeklyConstraints() {
            LocalDate monday = getNextWeekDay(DayOfWeek.MONDAY);
            Map<LocalDate, Set<ShiftType>> cons = new HashMap<>();
            cons.put(monday, new HashSet<>(Set.of(ShiftType.MORNING)));

            assertDoesNotThrow(() -> employee.setWeeklyConstraints(cons));
            assertTrue(employee.isAvailable(monday, ShiftType.MORNING));
        }

        @Test
        @DisplayName("Should clear all constraints when passed an empty map")
        void testClearConstraints() {
            LocalDate monday = getNextWeekDay(DayOfWeek.MONDAY);
            Map<LocalDate, Set<ShiftType>> cons = new HashMap<>();
            cons.put(monday, new HashSet<>(Set.of(ShiftType.MORNING)));
            employee.setWeeklyConstraints(cons);
            
            employee.setWeeklyConstraints(new HashMap<>());
            assertFalse(employee.isAvailable(monday, ShiftType.MORNING));
        }

        @Test
        @DisplayName("Should throw exception when attempting to submit a constraint on predefined day off")
        void testConstraintOnDayOffThrows() {
            LocalDate sunday = LocalDate.now();
            while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
                sunday = sunday.plusDays(1);
            }
            LocalDate friday = sunday.plusDays(5); 

            Map<LocalDate, Set<ShiftType>> cons = new HashMap<>();
            cons.put(friday, new HashSet<>(Set.of(ShiftType.MORNING)));

            assertThrows(IllegalArgumentException.class, () -> employee.setWeeklyConstraints(cons));
        }

        @Test
        @DisplayName("Should throw exception when submitting a preference for an unavailable shift")
        void testPreferenceWithoutConstraintThrows() {
            LocalDate monday = getNextWeekDay(DayOfWeek.MONDAY);
            employee.setWeeklyConstraints(new HashMap<>());

            Map<LocalDate, Set<ShiftType>> prefs = new HashMap<>();
            prefs.put(monday, new HashSet<>(Set.of(ShiftType.MORNING)));

            assertThrows(IllegalArgumentException.class, () -> employee.setWeeklyPreferences(prefs));
        }
    }

    @Nested
    @DisplayName("4. Standard Getters, Setters & Status")
    class StandardPropertyTests {

        @Test
        @DisplayName("Should correctly update basic employee details")
        void testBasicSetters() {
            employee.setName("New Name");
            assertEquals("New Name", employee.getName());

            employee.setSalary(20000);
            assertEquals(20000, employee.getSalary());

            employee.setVacation(20);
            assertEquals(20, employee.getVacation());

            employee.setBankAccount(999999);
            assertEquals(999999, employee.getBankAccount());
        }

        @Test
        @DisplayName("Should correctly toggle Active/Inactive status")
        void testStatusToggles() {
            employee.setStatus(Status.INACTIVE);
            assertEquals(Status.INACTIVE, employee.getStatus());

            employee.setStatus(Status.ACTIVE);
            assertEquals(Status.ACTIVE, employee.getStatus());
        }
    }
}