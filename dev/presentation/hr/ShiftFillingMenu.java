package presentation.hr;

import java.util.Comparator;
import presentation.InputUtil;
import presentation.MenuManager;
import service.*;
import domain.hr.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShiftFillingMenu {

    private final MenuManager manager;
    private final SchedulingService schedulingService;
    private final int branchId; // The active branch context

    // Updated constructor to capture the branch context passed from HRMenu
    public ShiftFillingMenu(MenuManager manager, SchedulingService schedulingService, int branchId) {
        this.manager = manager;
        this.schedulingService = schedulingService;
        this.branchId = branchId;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Shift Filling (Branch ID: " + branchId + ") ===");
            System.out.println("1) View next week's schedule");
            System.out.println("2) Assign employee");
            System.out.println("3) Unassign employee");
            System.out.println("4) View employee constraints");
            System.out.println("5) View employee preferences");
            System.out.println("6) Back");

            switch (InputUtil.readInt()) {
                case 1 -> viewWeeklySchedule();
                case 2 -> assignEmployee();
                case 3 -> unassignEmployee();
                case 4 -> viewConstraints();
                case 5 -> viewPreferences();
                case 6 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewWeeklySchedule() {
        // Passed branchId to scope the schedule view
        Response<List<ShiftSL>> response = schedulingService.getWeeklySchedule(
                manager.getLoggedInUserId(), branchId
        );
        if (response.isError()) {
            System.out.println("Could not fetch schedule: " + response.getErrorMessage());
            return;
        }
         List<ShiftSL> shifts = response.getValue();
        shifts.sort(Comparator.comparing(ShiftSL::getDate)
                            .thenComparing(ShiftSL::getType));

        System.out.println("\n=== Next Week's Schedule ===");
        for (ShiftSL shift : shifts) {
            System.out.println(shift);
        }
    }

    private void assignEmployee() {
        int userId = manager.getLoggedInUserId();

        LocalDate date = InputUtil.readDayOfWeek();
        ShiftType type = InputUtil.readShiftType();

        // Passed branchId to look up the shift profile for this branch site
        Response<ShiftSL> shiftResponse = schedulingService.getShift(userId, branchId, date, type);
        if (shiftResponse.isError()) {
            System.out.println("No shift found: " + shiftResponse.getErrorMessage());
            return;
        }
        ShiftSL shift = shiftResponse.getValue();
        System.out.println(shift);
        List<Certification> eligibleRoles = new ArrayList<>(Arrays.asList(Certification.values()));
        eligibleRoles.remove(Certification.HR_MANAGER);
        Certification role = InputUtil.readRole(eligibleRoles);
        
        // Passed branchId to fetch candidates deployed or eligible at this branch
        Response<List<EmployeeSL>> availableResponse = schedulingService.getAvailableForRole(userId, branchId, date, type, role);
        if (availableResponse.isError()) {
            System.out.println("Could not fetch employees: " + availableResponse.getErrorMessage());
            return;
        }
        List<EmployeeSL> available = availableResponse.getValue();
        if (!available.isEmpty()) {
            System.out.println("\nAvailable employees:");
            EmployeeSL selected = InputUtil.selectItem(available);
            if (selected == null) return;
            boolean isOvertime = false;

            if (shift.getType() == ShiftType.MORNING) {
                isOvertime = InputUtil.readYesNo("Is this an overtime assignment?");
            }

            // Passed branchId context to register the assignment mapping
            Response<Void> assignResponse = schedulingService.assignEmployee(
                    userId, branchId, selected.getID(), date, type, role, isOvertime
            );
            if (assignResponse.isError()) {
                System.out.println("Assignment failed: " + assignResponse.getErrorMessage());
            } else {
                System.out.println("Assignment successful!");
                Response<ShiftSL> updated = schedulingService.getShift(userId, branchId, date, type);
                if (!updated.isError()) System.out.println(updated.getValue());
            }
        } else {
            System.out.println("No " + role.getValue() + " available.");
            sendOverrideRequest(userId, date, type, role);
        }
    }

    private void sendOverrideRequest(int userId, LocalDate date, ShiftType type, Certification role) {
        // Passed branchId so you track certified workers applicable to this store branch layout
        Response<List<EmployeeSL>> certifiedResponse = schedulingService.getAllWithCertification(userId, branchId, role);
        if (certifiedResponse.isError()) {
            System.out.println("Could not fetch certified employees: " + certifiedResponse.getErrorMessage());
            return;
        }

        List<EmployeeSL> certified = certifiedResponse.getValue();
        if (certified.isEmpty()) {
            System.out.println("No certified " + role.getValue() + " employees exist.");
            return;
        }

        System.out.println("\nSend override request to:");
        EmployeeSL selected = InputUtil.selectItem(certified);
        if (selected == null) return;

        // Passed branchId context to link the generated override notice to this specific location
        Response<String> requestResponse = schedulingService.createOverrideRequest(
                userId, branchId, selected.getID(), date, type, role
        );
        if (requestResponse.isError())
            System.out.println("Request failed: " + requestResponse.getErrorMessage());
        else
            System.out.println("Request sent successfully!");
    }

    private void unassignEmployee() {
        int userId = manager.getLoggedInUserId();
        LocalDate date = InputUtil.readDayOfWeek();
        ShiftType type = InputUtil.readShiftType();
        List<Certification> eligibleRoles = new ArrayList<>(Arrays.asList(Certification.values()));
        eligibleRoles.remove(Certification.HR_MANAGER);
        Certification role = InputUtil.readRole(eligibleRoles);

        // Passed branchId context
        Response<ShiftSL> shiftResponse = schedulingService.getShift(userId, branchId, date, type);
        if (shiftResponse.isError()) {
            System.out.println("No shift found: " + shiftResponse.getErrorMessage());
            return;
        }

        List<Integer> assigned = shiftResponse.getValue().getAssignments()
                .getOrDefault(role, List.of());

        if (assigned.isEmpty()) {
            System.out.println("No employees assigned to this role.");
            return;
        }

        System.out.println("\nAssigned employees:");
        Integer selected = InputUtil.selectItem(assigned);
        if (selected == null) return;

        // Passed branchId context
        Response<Void> removeResponse = schedulingService.removeEmployee(
                userId, branchId, date, type, role, selected
        );
        if (removeResponse.isError())
            System.out.println("Removal failed: " + removeResponse.getErrorMessage());
        else
            System.out.println("Employee removed successfully!");
    }

    private void viewConstraints() {
        int userId = manager.getLoggedInUserId();
        int empId = InputUtil.readEmployeeId();

        // Passed branchId context to authorize viewing logs inside this branch context
        Response<WeeklyConstraintsSL> response = schedulingService.getWeeklyConstraints(userId, branchId, empId);
        if (response.isError())
            System.out.println("Could not fetch constraints: " + response.getErrorMessage());
        else
            System.out.println(response.getValue());
    }

    private void viewPreferences() {
        int userId = manager.getLoggedInUserId();
        int empId = InputUtil.readEmployeeId();

        // Passed branchId context
        Response<WeeklyPreferenceSL> response = schedulingService.getWeeklyPreferences(userId, branchId, empId);
        if (response.isError())
            System.out.println("Could not fetch preferences: " + response.getErrorMessage());
        else
            System.out.println(response.getValue());
    }
}