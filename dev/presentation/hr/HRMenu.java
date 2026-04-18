package dev.presentation.hr;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.presentation.MenuManager;
import dev.service.PersonnelService;
import dev.service.SchedulingService;

public class HRMenu {

    private final MenuManager manager;

    private final ShiftDefinitionMenu shiftDefinitionMenu;
    private final ShiftFillingMenu shiftFillingMenu;
    private final EmployeeManagementMenu employeeMenu;
    private final HRRequestMenu requestMenu;

    public HRMenu(MenuManager manager, SchedulingService schedulingService, PersonnelService personnelService) {
        this.manager = manager;
        shiftDefinitionMenu = new ShiftDefinitionMenu(manager, schedulingService);
        shiftFillingMenu = new ShiftFillingMenu(manager, schedulingService);
        employeeMenu = new EmployeeManagementMenu(manager, personnelService);
        requestMenu = new HRRequestMenu(manager, schedulingService);
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
        // TODO: read date, read shift type, display past shift details
    }

}