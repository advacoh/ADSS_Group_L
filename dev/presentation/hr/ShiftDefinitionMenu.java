package dev.presentation.hr;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.SchedulingService;

public class ShiftDefinitionMenu {

    private final MenuManager manager;
    private final SchedulingService schedulingService;

    public ShiftDefinitionMenu(MenuManager manager, SchedulingService schedulingService) {
        this.manager = manager;
        this.schedulingService = schedulingService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Shift Definition ===");
            System.out.println("1) Create shift");
            System.out.println("2) Set requirements");
            System.out.println("3) Back");

            switch (InputUtil.readInt()) {
                case 1 -> createShift();
                case 2 -> setRequirements();
                case 3 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createShift() {
        // TODO
    }

    private void setRequirements() {
        // TODO
    }
}