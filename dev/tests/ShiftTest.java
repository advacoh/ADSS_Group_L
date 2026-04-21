import dev.domain.Shift;
import dev.domain.Certification;
import dev.domain.ShiftType;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

public class ShiftTest {

    private Shift shift;

    @BeforeEach
    void setUp() {
        shift = new Shift("SHIFT_001", LocalDate.now(), ShiftType.MORNING);
    }

    // Creation Tests 

    @Test
    void testShiftCreatedWithCorrectID() {
        assertEquals("SHIFT_001", shift.getID());
    }

    @Test
    void testShiftCreatedWithCorrectDate() {
        assertEquals(LocalDate.now(), shift.getDate());
    }

    @Test
    void testShiftCreatedWithCorrectType() {
        assertEquals(ShiftType.MORNING, shift.getType());
    }

    @Test
    void testShiftManagerRequirementSetByDefault() {
        assertTrue(shift.getRequiredRoles().containsKey(Certification.SHIFT_MANAGER));
        assertEquals(1, shift.getRequiredRoles().get(Certification.SHIFT_MANAGER));
    }

    @Test
    void testNewShiftIsNotFullyStaffed() {
        assertFalse(shift.isFullyStaffed());
    }

    // setRequirement Tests 

    @Test
    void testSetRequirementAddsRole() {
        shift.setRequirement(Certification.CASHIER, 2);
        assertTrue(shift.getRequiredRoles().containsKey(Certification.CASHIER));
        assertEquals(2, shift.getRequiredRoles().get(Certification.CASHIER));
    }

    @Test
    void testSetRequirementNegativeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            shift.setRequirement(Certification.CASHIER, -1));
    }

    @Test
    void testSetRequirementHRManagerThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            shift.setRequirement(Certification.HR_MANAGER, 1));
    }

    @Test
    void testSetShiftManagerToZeroThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            shift.setRequirement(Certification.SHIFT_MANAGER, 0));
    }

    @Test
    void testSetRequirementToZeroRemovesRole() {
        shift.setRequirement(Certification.CASHIER, 2);
        shift.setRequirement(Certification.CASHIER, 0);
        assertFalse(shift.getRequiredRoles().containsKey(Certification.CASHIER));
    }

    // assignEmployee Tests 

    @Test
    void testAssignEmployeeSucceeds() {
        boolean result = shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
        assertTrue(result);
        assertTrue(shift.isEmployeeAssigned(100000001));
    }

    @Test
    void testAssignEmployeeWhenRoleFilledReturnsFalse() {
        shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
        boolean result = shift.assignEmployee(Certification.SHIFT_MANAGER, 100000002);
        assertFalse(result);
    }

    @Test
    void testAssignSameEmployeeTwiceThrowsIfNotShiftManager() {
        shift.setRequirement(Certification.CASHIER, 2);
        shift.assignEmployee(Certification.CASHIER, 100000002);
        assertThrows(IllegalStateException.class, () ->
            shift.assignEmployee(Certification.CASHIER, 100000002));
    }

    @Test
    void testShiftManagerCanHoldTwoRoles() {
        shift.setRequirement(Certification.CASHIER, 2);
        shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
        boolean result = shift.assignEmployee(Certification.CASHIER, 100000001);
        assertTrue(result);
        assertEquals(2, shift.countRoles(100000001));
    }

    @Test
    void testShiftManagerCannotHoldMoreThanTwoRoles() {
        shift.setRequirement(Certification.CASHIER, 2);
        shift.setRequirement(Certification.WAREHOUSE, 2);
        shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
        shift.assignEmployee(Certification.CASHIER, 100000001);
        assertThrows(IllegalStateException.class, () ->
            shift.assignEmployee(Certification.WAREHOUSE, 100000001));
    }

    // removeEmployee Tests 

    @Test
    void testRemoveEmployeeSucceeds() {
        shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
        shift.removeEmployee(Certification.SHIFT_MANAGER, 100000001);
        assertFalse(shift.isEmployeeAssigned(100000001));
    }

    @Test
    void testRemoveEmployeeNotAssignedThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            shift.removeEmployee(Certification.SHIFT_MANAGER, 999999999));
    }

    // isFullyStaffed Tests 

    @Test
    void testShiftIsFullyStaffedWhenAllRolesFilled() {
        shift.setRequirement(Certification.CASHIER, 1);
        shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
        shift.assignEmployee(Certification.CASHIER, 100000002);
        assertTrue(shift.isFullyStaffed());
    }

    @Test
    void testShiftNotFullyStaffedWhenRoleMissing() {
        shift.setRequirement(Certification.CASHIER, 1);
        shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
        assertFalse(shift.isFullyStaffed());
    }

    // Overtime Tests

    @Test
    void testMorningShiftCanAcceptOvertime() {
        assertTrue(shift.canAcceptOvertime());
    }

    @Test
    void testEveningShiftCannotAcceptOvertime() {
        Shift eveningShift = new Shift("SHIFT_002", LocalDate.now(), ShiftType.EVENING);
        assertFalse(eveningShift.canAcceptOvertime());
    }

    @Test
    void testAddOvertimeEmployeeSucceeds() {
        shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
        assertDoesNotThrow(() -> shift.addOvertimeEmployee(100000001));
        assertTrue(shift.getOvertimeEmployees().contains(100000001));
    }

    @Test
    void testAddOvertimeEmployeeNotAssignedThrows() {
        assertThrows(IllegalStateException.class, () ->
            shift.addOvertimeEmployee(999999999));
    }

    @Test
    void testAddOvertimeEmployeeTwiceThrows() {
        shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
        shift.addOvertimeEmployee(100000001);
        assertThrows(IllegalStateException.class, () ->
            shift.addOvertimeEmployee(100000001));
    }

    // isAssignedAsRole Tests 

    @Test
    void testIsAssignedAsRoleReturnsTrue() {
        shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
        assertTrue(shift.isAssignedAsRole(Certification.SHIFT_MANAGER, 100000001));
    }

    @Test
    void testIsAssignedAsRoleReturnsFalse() {
        assertFalse(shift.isAssignedAsRole(Certification.SHIFT_MANAGER, 100000001));
    }
}