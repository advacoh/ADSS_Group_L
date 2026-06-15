import domain.hr.Employee;
import domain.hr.EmpType;
import domain.hr.SalType;
import domain.hr.Certification;
import domain.hr.Status;
import domain.hr.ShiftType;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.*;

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
        // New employee before each test
        employee = new Employee(
            100000001, "Test User", 123456,
            LocalDate.of(2024, 1, 1),
            EmpType.FULL_TIME, SalType.GLOBAL, 10000,
            15, true, 6, false,
            new HashSet<>(List.of(Certification.CASHIER))
        );
    }

    // Basic Creation Tests

    @Test
    void testEmployeeCreatedWithCorrectName() {
        assertEquals("Test User", employee.getName());
    }

    @Test
    void testEmployeeCreatedWithCorrectID() {
        assertEquals(100000001, employee.getID());
    }

    @Test
    void testEmployeeIsActiveByDefault() {
        assertEquals(Status.ACTIVE, employee.getStatus());
    }

    @Test
    void testEmployeeCreatedWithCorrectSalary() {
        assertEquals(10000, employee.getSalary());
    }

    @Test
    void testEmployeeCreatedWithCorrectVacationDays() {
        assertEquals(15, employee.getVacation());
    }

    // Certification Tests 

    @Test
    void testEmployeeHasInitialCertification() {
        assertTrue(employee.isCertified(Certification.CASHIER));
    }

    @Test
    void testAddCertification() {
        employee.addCertification(Certification.WAREHOUSE);
        assertTrue(employee.isCertified(Certification.WAREHOUSE));
    }

    @Test
    void testAddDuplicateCertificationDoesNotDuplicate() {
        employee.addCertification(Certification.CASHIER);
        long count = employee.getCertifications().stream()
            .filter(c -> c == Certification.CASHIER)
            .count();
        assertEquals(1, count);
    }

    @Test
    void testRemoveCertification() {
        employee.removeCertification(Certification.CASHIER);
        assertFalse(employee.isCertified(Certification.CASHIER));
    }

    @Test
    void testRemoveNonExistentCertificationReturnsFalse() {
        boolean result = employee.removeCertification(Certification.HR_MANAGER);
        assertFalse(result);
    }

    @Test
    void testRemoveExistingCertificationReturnsTrue() {
        boolean result = employee.removeCertification(Certification.CASHIER);
        assertTrue(result);
    }

    // Constructor Validation Tests

    @Test
    void testNullNameThrows() {
        assertThrows(NullPointerException.class, () ->
            new Employee(100000001, null, 123456,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                15, true, 6, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testNullStartDateThrows() {
        assertThrows(NullPointerException.class, () ->
            new Employee(100000001, "Test User", 123456,
                null,
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                15, true, 6, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testNullEmploymentTypeThrows() {
        assertThrows(NullPointerException.class, () ->
            new Employee(100000001, "Test User", 123456,
                LocalDate.of(2024, 1, 1),
                null, SalType.GLOBAL, 10000,
                15, true, 6, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testNullSalaryTypeThrows() {
        assertThrows(NullPointerException.class, () ->
            new Employee(100000001, "Test User", 123456,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, null, 10000,
                15, true, 6, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testNullCertificationsThrows() {
        assertThrows(NullPointerException.class, () ->
            new Employee(100000001, "Test User", 123456,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                15, true, 6, false,
                null));
    }

    @Test
    void testIDTooShortThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Employee(12345678, "Test User", 123456,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                15, true, 6, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testIDTooLongThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Employee(1000000000, "Test User", 123456,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                15, true, 6, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testNegativeSalaryThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Employee(100000001, "Test User", 123456,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, SalType.GLOBAL, -1,
                15, true, 6, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testZeroSalaryThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Employee(100000001, "Test User", 123456,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, SalType.GLOBAL, 0,
                15, true, 6, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testNegativeVacationThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Employee(100000001, "Test User", 123456,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                -1, true, 6, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testNegativeBankAccountThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Employee(100000001, "Test User", -1,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                15, true, 6, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testDayOffZeroThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Employee(100000001, "Test User", 123456,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                15, true, 0, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    @Test
    void testDayOffEightThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Employee(100000001, "Test User", 123456,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                15, true, 8, false,
                new HashSet<>(List.of(Certification.CASHIER))));
    }

    // HR Tests 

    @Test
    void testIsNotHRByDefault() {
        assertFalse(employee.isHR());
    }

    @Test
    void testBecomeHRAfterAddingCertification() {
        employee.addCertification(Certification.HR_MANAGER);
        assertTrue(employee.isHR());
    }

    @Test
    void testNoLongerHRAfterRemovingCertification() {
        employee.addCertification(Certification.HR_MANAGER);
        employee.removeCertification(Certification.HR_MANAGER);
        assertFalse(employee.isHR());
    }

    // Status Tests 

    @Test
    void testDismissedEmployeeIsInactive() {
        employee.setStatus(Status.INACTIVE);
        assertEquals(Status.INACTIVE, employee.getStatus());
    }

    @Test
    void testReactivatedEmployeeIsActive() {
        employee.setStatus(Status.INACTIVE);
        employee.setStatus(Status.ACTIVE);
        assertEquals(Status.ACTIVE, employee.getStatus());
    }

    //  Setter Tests 

    @Test
    void testSetName() {
        employee.setName("New Name");
        assertEquals("New Name", employee.getName());
    }

    @Test
    void testSetSalary() {
        employee.setSalary(20000);
        assertEquals(20000, employee.getSalary());
    }

    @Test
    void testSetVacation() {
        employee.setVacation(20);
        assertEquals(20, employee.getVacation());
    }

    @Test
    void testSetBankAccount() {
        employee.setBankAccount(999999);
        assertEquals(999999, employee.getBankAccount());
    }

    // Weekly Constraints Tests 

    @Test
    void testSetWeeklyConstraintsThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () ->
            employee.setWeeklyConstraints(null));
    }

    @Test
    void testSetWeeklyPreferencesThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () ->
            employee.setWeeklyPreferences(null));
    }

    @Test
    void testSetWeeklyConstraintsValidDay() {
        LocalDate monday = getNextWeekDay(DayOfWeek.MONDAY);

        Map<LocalDate, Set<ShiftType>> cons = new HashMap<>();
        cons.put(monday, new HashSet<>(Set.of(ShiftType.MORNING)));

        assertDoesNotThrow(() -> employee.setWeeklyConstraints(cons));
        assertTrue(employee.isAvailable(monday, ShiftType.MORNING));
    }

    @Test
    void testSetWeeklyConstraintsOnDayOffThrowsWarning() {
        LocalDate sunday = LocalDate.now();
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }
        LocalDate friday = sunday.plusDays(5); // Friday of that week

        Map<LocalDate, Set<ShiftType>> cons = new HashMap<>();
        cons.put(friday, new HashSet<>(Set.of(ShiftType.MORNING)));

        assertThrows(IllegalArgumentException.class, () ->
            employee.setWeeklyConstraints(cons));
    }

    @Test
    void testSetWeeklyPreferencesWithoutConstraintThrowsWarning() {
        LocalDate sunday = LocalDate.now();
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }
        LocalDate monday = sunday.plusDays(1);

        employee.setWeeklyConstraints(new HashMap<>());

        Map<LocalDate, Set<ShiftType>> prefs = new HashMap<>();
        prefs.put(monday, new HashSet<>(Set.of(ShiftType.MORNING)));

        assertThrows(IllegalArgumentException.class, () ->
            employee.setWeeklyPreferences(prefs));
    }

    @Test
    void testEmptyConstraintsClearsAll() {
        LocalDate monday = getNextWeekDay(DayOfWeek.MONDAY);

        Map<LocalDate, Set<ShiftType>> cons = new HashMap<>();
        cons.put(monday, new HashSet<>(Set.of(ShiftType.MORNING)));
        employee.setWeeklyConstraints(cons);
        assertTrue(employee.isAvailable(monday, ShiftType.MORNING));

        employee.setWeeklyConstraints(new HashMap<>());
        assertFalse(employee.isAvailable(monday, ShiftType.MORNING));
    }
}