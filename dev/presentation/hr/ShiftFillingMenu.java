package dev.presentation.hr;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.SchedulingService;

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
        // TODO
    }

    private void assignEmployee() {
        // TODO: if assignment fails due to availability/certification, offer to create override request
    }

    private void unassignEmployee() {
        // TODO
    }

    private void viewConstraints() {
        // TODO
    }

    private void viewPreferences() {
        // TODO
    }
}