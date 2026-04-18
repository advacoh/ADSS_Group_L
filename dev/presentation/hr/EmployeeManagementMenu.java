package dev.presentation.hr;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.PersonnelService;
import dev.service.SchedulingService;

public class EmployeeManagementMenu {

    private final MenuManager manager;
    private final PersonnelService personnelService;

    public EmployeeManagementMenu(MenuManager manager, PersonnelService personnelService) {
        this.manager = manager;
        this.personnelService = personnelService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Employee Management ===");
            System.out.println("1) Hire employee");
            System.out.println("2) Dismiss employee");
            System.out.println("3) Rehire employee");
            System.out.println("4) View/edit employee data");
            System.out.println("5) Back");

            switch (InputUtil.readInt()) {
                case 1 -> hireEmployee();
                case 2 -> dismissEmployee();
                case 3 -> rehireEmployee();
                case 4 -> viewEditEmployee();
                case 5 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void hireEmployee() {
        // TODO
    }

    private void dismissEmployee() {
        // TODO
    }

    private void rehireEmployee() {
        // TODO
    }

    private void viewEditEmployee() {
        // TODO
    }
}