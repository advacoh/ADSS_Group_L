package dev.presentation.hr;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.*;
import dev.domain.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShiftFillingMenu {

    private final MenuManager manager;
    private final SchedulingService schedulingService;

    public ShiftFillingMenu(MenuManager manager, SchedulingService schedulingService) {
        this.manager = manager;
        this.schedulingService = schedulingService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Shift Filling ===");
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
        Response<List<ShiftSL>> response = schedulingService.getWeeklySchedule(
                manager.getLoggedInUserId()
        );
        if (response.isError()) {
            System.out.println("Could not fetch schedule: " + response.getErrorMessage());
            return;
        }
        System.out.println("\n=== Next Week's Schedule ===");
        for (ShiftSL shift : response.getValue()) {
            System.out.println(shift);
        }
    }

    private void assignEmployee() {
        int userId = manager.getLoggedInUserId();

        LocalDate date = InputUtil.readDayOfWeek();
        ShiftType type = InputUtil.readShiftType();

        Response<ShiftSL> shiftResponse = schedulingService.getShift(userId, date, type);
        if (shiftResponse.isError()) {
            System.out.println("No shift found: " + shiftResponse.getErrorMessage());
            return;
        }
        ShiftSL shift = shiftResponse.getValue();
        System.out.println(shift);
        List<Certification> eligibleRoles = new ArrayList<>(Arrays.asList(Certification.values()));
        eligibleRoles.remove(Certification.HR_MANAGER);
        Certification role = InputUtil.readRole(eligibleRoles);
        Response<List<EmployeeSL>> availableResponse = schedulingService.getAvailableForRole(userId, date, type, role);
        if (availableResponse.isError()) {
            System.out.println("Could not fetch employees: " + availableResponse.getErrorMessage());
            return;
        }
        List<EmployeeSL> available = availableResponse.getValue();
        if (!available.isEmpty()) {
            System.out.println("\nAvailable employees:");
            EmployeeSL selected = InputUtil.selectItem(available);
            if (selected == null) return;

            boolean isOvertime = InputUtil.readYesNo("Is this an overtime assignment?");

            Response<Void> assignResponse = schedulingService.assignEmployee(
                    userId, selected.getID(), date, type, role, isOvertime
            );
            if (assignResponse.isError()) {
                System.out.println("Assignment failed: " + assignResponse.getErrorMessage());
            } else {
                System.out.println("Assignment successful!");
                Response<ShiftSL> updated = schedulingService.getShift(userId, date, type);
                if (!updated.isError()) System.out.println(updated.getValue());
            }
        } else {
            // no one available, offer override request
            System.out.println("No " + role.getValue() + " available.");
            sendOverrideRequest(userId, date, type, role);
        }
    }

    private void sendOverrideRequest(int userId, LocalDate date, ShiftType type, Certification role) {
        Response<List<EmployeeSL>> certifiedResponse = schedulingService.getAllWithCertification(userId, role);
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

        Response<String> requestResponse = schedulingService.createOverrideRequest(
                userId, selected.getID(), date, type, role
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

        Response<ShiftSL> shiftResponse = schedulingService.getShift(userId, date, type);
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

        Response<Void> removeResponse = schedulingService.removeEmployee(
                userId, date, type, role, selected
        );
        if (removeResponse.isError())
            System.out.println("Removal failed: " + removeResponse.getErrorMessage());
        else
            System.out.println("Employee removed successfully!");
    }

    private void viewConstraints() {
        int userId = manager.getLoggedInUserId();
        int empId = InputUtil.readEmployeeId();

        Response<WeeklyConstraintsSL> response = schedulingService.getWeeklyConstraints(userId, empId);
        if (response.isError())
            System.out.println("Could not fetch constraints: " + response.getErrorMessage());
        else
            System.out.println(response.getValue());
    }

    private void viewPreferences() {
        int userId = manager.getLoggedInUserId();
        int empId = InputUtil.readEmployeeId();

        Response<WeeklyPreferenceSL> response = schedulingService.getWeeklyPreferences(userId, empId);
        if (response.isError())
            System.out.println("Could not fetch preferences: " + response.getErrorMessage());
        else
            System.out.println(response.getValue());
    }


}