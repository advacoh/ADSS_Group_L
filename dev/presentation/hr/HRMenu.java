package dev.presentation.hr;

import dev.domain.ShiftType;
import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.presentation.MenuManager;
import dev.service.PersonnelService;
import dev.service.Response;
import dev.service.SchedulingService;
import dev.service.ShiftSL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HRMenu {

    private final MenuManager manager;

    private final ShiftDefinitionMenu shiftDefinitionMenu;
    private final ShiftFillingMenu shiftFillingMenu;
    private final EmployeeManagementMenu employeeMenu;
    private final HRRequestMenu requestMenu;
    private final SchedulingService schedulingService;

    public HRMenu(MenuManager manager, SchedulingService schedulingService, PersonnelService personnelService) {
        this.manager = manager;
        shiftDefinitionMenu = new ShiftDefinitionMenu(manager, schedulingService);
        shiftFillingMenu = new ShiftFillingMenu(manager, schedulingService);
        employeeMenu = new EmployeeManagementMenu(manager, personnelService);
        requestMenu = new HRRequestMenu(manager, schedulingService);
        this.schedulingService = schedulingService;
    }

    public void show() {

        while (true) {

            System.out.println("\n=== HR Menu ===");

            System.out.println("1) Shift Definition");
            System.out.println("2) Shift Filling");
            System.out.println("3) Employee Management");
            System.out.println("4) Request Handling");
            System.out.println("5) View History");
            System.out.println("6) Logout");

            int choice = InputUtil.readInt();

            switch (choice) {

                case 1 ->
                        shiftDefinitionMenu.show();

                case 2 ->
                        shiftFillingMenu.show();

                case 3 ->
                        employeeMenu.show();

                case 4 ->
                        requestMenu.show();

                case 5 -> viewHistory();
                case 6 -> {
                    //TODO LOGOUT
                    System.out.println("Logging out...");
                    return;
                }

                default ->
                        System.out.println(
                                "Invalid option."
                        );
            }
        }
    }

    private void viewHistory() {
        while (true) {
            System.out.println("\n=== View History ===");
            System.out.println("Enter 'q' to go back.");

            LocalDate date = readDateOrQuit();
            if (date == null) return;

            ShiftType type = readShiftTypeOrQuit();
            if (type == null) return;

            displayPastShift(date, type);
        }
    }

    private LocalDate readDateOrQuit() {
        System.out.print("Enter date (dd/MM/yyyy) or 'q' to go back: ");
        String input = InputUtil.readRaw();
        if (input.equalsIgnoreCase("q")) return null;
        try {
            return LocalDate.parse(input, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use dd/MM/yyyy.");
            return readDateOrQuit();
        }
    }

    private ShiftType readShiftTypeOrQuit() {
        while (true) {
            System.out.print("Enter the Shift type — m for morning, e for evening, or 'q' to go back: ");
            String input = InputUtil.readRaw();
            switch (input.toLowerCase()) {
                case "m" -> { return ShiftType.MORNING; }
                case "e" -> { return ShiftType.EVENING; }
                case "q" -> { return null; }
                default  -> System.out.println("Please enter M, E, or Q.");
            }
        }
    }

    private void displayPastShift(LocalDate date, ShiftType type) {
        Response<ShiftSL> response = schedulingService.getPastShift(
                manager.getLoggedInUserId(), date, type
        );
        if (response.isError()) {
            System.out.println("Shift not found: " + response.getErrorMessage());
        } else {
            System.out.println(response.getValue().toString());
        }
    }

}