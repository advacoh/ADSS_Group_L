package domain.hr;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@DisplayName("Shift Domain Class Tests")
public class ShiftTest {

    private Shift shift;
    private final int TEST_BRANCH_ID = 1;

    @BeforeEach
    void setUp() {
        shift = new Shift("SHIFT_001", TEST_BRANCH_ID, LocalDate.now(), ShiftType.MORNING);
    }

    @Nested
    @DisplayName("1. Constructor & Basic Properties")
    class CreationTests {

        @Test
        @DisplayName("Should successfully create a shift with correct ID")
        void testShiftCreatedWithCorrectID() {
            assertEquals("SHIFT_001", shift.getID());
        }
        
        @Test
        @DisplayName("Should successfully create a shift with correct Branch ID")
        void testShiftCreatedWithCorrectBranchID() {
            assertEquals(TEST_BRANCH_ID, shift.getBranchId());
        }

        @Test
        @DisplayName("Should successfully create a shift with correct Date")
        void testShiftCreatedWithCorrectDate() {
            assertEquals(LocalDate.now(), shift.getDate());
        }

        @Test
        @DisplayName("Should successfully create a shift with correct Type")
        void testShiftCreatedWithCorrectType() {
            assertEquals(ShiftType.MORNING, shift.getType());
        }

        @Test
        @DisplayName("Should have exactly 1 SHIFT_MANAGER requirement by default")
        void testShiftManagerRequirementSetByDefault() {
            assertTrue(shift.getRequiredRoles().containsKey(Certification.SHIFT_MANAGER));
            assertEquals(1, shift.getRequiredRoles().get(Certification.SHIFT_MANAGER));
        }

        @Test
        @DisplayName("A newly created shift should not be fully staffed")
        void testNewShiftIsNotFullyStaffed() {
            assertFalse(shift.isFullyStaffed());
        }
    }

    @Nested
    @DisplayName("2. Requirement Management")
    class RequirementTests {

        @Test
        @DisplayName("Should correctly add a new role requirement")
        void testSetRequirementAddsRole() {
            shift.setRequirement(Certification.CASHIER, 2);
            assertTrue(shift.getRequiredRoles().containsKey(Certification.CASHIER));
            assertEquals(2, shift.getRequiredRoles().get(Certification.CASHIER));
        }

        @Test
        @DisplayName("Should throw exception if requirement count is negative")
        void testSetRequirementNegativeThrows() {
            assertThrows(IllegalArgumentException.class, () ->
                shift.setRequirement(Certification.CASHIER, -1));
        }

        @Test
        @DisplayName("Should throw exception if trying to require an HR_MANAGER")
        void testSetRequirementHRManagerThrows() {
            assertThrows(IllegalArgumentException.class, () ->
                shift.setRequirement(Certification.HR_MANAGER, 1));
        }

        @Test
        @DisplayName("Should throw exception if SHIFT_MANAGER requirement is set below 1")
        void testSetShiftManagerToZeroThrows() {
            assertThrows(IllegalArgumentException.class, () ->
                shift.setRequirement(Certification.SHIFT_MANAGER, 0));
        }

        @Test
        @DisplayName("Setting requirement count to 0 should remove the role completely")
        void testSetRequirementToZeroRemovesRole() {
            shift.setRequirement(Certification.CASHIER, 2);
            shift.setRequirement(Certification.CASHIER, 0);
            assertFalse(shift.getRequiredRoles().containsKey(Certification.CASHIER));
        }
    }

    @Nested
    @DisplayName("3. Employee Assignment Logic")
    class AssignmentTests {

        @Test
        @DisplayName("Should successfully assign an employee to an open role")
        void testAssignEmployeeSucceeds() {
            boolean result = shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
            assertTrue(result);
            assertTrue(shift.isEmployeeAssigned(100000001));
            assertTrue(shift.isAssignedAsRole(Certification.SHIFT_MANAGER, 100000001));
        }

        @Test
        @DisplayName("Should return false when attempting to assign an employee to a filled role")
        void testAssignEmployeeWhenRoleFilledReturnsFalse() {
            shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
            boolean result = shift.assignEmployee(Certification.SHIFT_MANAGER, 100000002);
            assertFalse(result);
        }

        @Test
        @DisplayName("Should throw exception if non-manager is assigned twice to the shift")
        void testAssignSameEmployeeTwiceThrowsIfNotShiftManager() {
            shift.setRequirement(Certification.CASHIER, 2);
            shift.assignEmployee(Certification.CASHIER, 100000002);
            assertThrows(IllegalStateException.class, () ->
                shift.assignEmployee(Certification.CASHIER, 100000002));
        }

        @Test
        @DisplayName("A Shift Manager should be able to hold exactly two roles")
        void testShiftManagerCanHoldTwoRoles() {
            shift.setRequirement(Certification.CASHIER, 2);
            shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
            boolean result = shift.assignEmployee(Certification.CASHIER, 100000001);
            
            assertTrue(result);
            assertEquals(2, shift.countRoles(100000001));
        }

        @Test
        @DisplayName("A Shift Manager should throw exception if assigned to a third role")
        void testShiftManagerCannotHoldMoreThanTwoRoles() {
            shift.setRequirement(Certification.CASHIER, 2);
            shift.setRequirement(Certification.WAREHOUSE, 2);
            
            shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
            shift.assignEmployee(Certification.CASHIER, 100000001);
            
            assertThrows(IllegalStateException.class, () ->
                shift.assignEmployee(Certification.WAREHOUSE, 100000001));
        }
    }

    @Nested
    @DisplayName("4. Employee Removal & Staffing Status")
    class RemovalAndStaffingTests {

        @Test
        @DisplayName("Should successfully remove an assigned employee")
        void testRemoveEmployeeSucceeds() {
            shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
            shift.removeEmployee(Certification.SHIFT_MANAGER, 100000001);
            assertFalse(shift.isEmployeeAssigned(100000001));
        }

        @Test
        @DisplayName("Should throw exception if removing an unassigned employee")
        void testRemoveEmployeeNotAssignedThrows() {
            assertThrows(IllegalArgumentException.class, () ->
                shift.removeEmployee(Certification.SHIFT_MANAGER, 999999999));
        }

        @Test
        @DisplayName("isFullyStaffed should return true only when all requirements are met")
        void testShiftIsFullyStaffedWhenAllRolesFilled() {
            shift.setRequirement(Certification.CASHIER, 1);
            shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
            shift.assignEmployee(Certification.CASHIER, 100000002);
            assertTrue(shift.isFullyStaffed());
        }

        @Test
        @DisplayName("isFullyStaffed should return false if any role is missing")
        void testShiftNotFullyStaffedWhenRoleMissing() {
            shift.setRequirement(Certification.CASHIER, 1);
            shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
            assertFalse(shift.isFullyStaffed());
        }
    }

    @Nested
    @DisplayName("5. Overtime Logic")
    class OvertimeTests {

        @Test
        @DisplayName("Morning shifts should be allowed to accept overtime")
        void testMorningShiftCanAcceptOvertime() {
            assertTrue(shift.canAcceptOvertime());
        }

        @Test
        @DisplayName("Evening shifts should NOT be allowed to accept overtime")
        void testEveningShiftCannotAcceptOvertime() {
            Shift eveningShift = new Shift("SHIFT_002", TEST_BRANCH_ID, LocalDate.now(), ShiftType.EVENING);
            assertFalse(eveningShift.canAcceptOvertime());
        }

        @Test
        @DisplayName("Should successfully mark an assigned employee for overtime")
        void testAddOvertimeEmployeeSucceeds() {
            shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
            assertDoesNotThrow(() -> shift.addOvertimeEmployee(100000001));
            assertTrue(shift.getOvertimeEmployees().contains(100000001));
        }

        @Test
        @DisplayName("Should throw exception when marking an unassigned employee for overtime")
        void testAddOvertimeEmployeeNotAssignedThrows() {
            assertThrows(IllegalStateException.class, () ->
                shift.addOvertimeEmployee(999999999));
        }

        @Test
        @DisplayName("Should throw exception if employee is marked for overtime twice")
        void testAddOvertimeEmployeeTwiceThrows() {
            shift.assignEmployee(Certification.SHIFT_MANAGER, 100000001);
            shift.addOvertimeEmployee(100000001);
            assertThrows(IllegalStateException.class, () ->
                shift.addOvertimeEmployee(100000001));
        }
    }
}


