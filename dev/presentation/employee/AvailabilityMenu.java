package dev.presentation.employee;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.SchedulingService;

public class AvailabilityMenu {

    private final MenuManager manager;
    private final SchedulingService schedulingService;

    public AvailabilityMenu(MenuManager manager, SchedulingService schedulingService) {
        this.manager = manager;
        this.schedulingService = schedulingService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Availability ===");
            System.out.println("1) View constraints");
            System.out.println("2) Set constraints");
            System.out.println("3) View preferences");
            System.out.println("4) Set preferences");
            System.out.println("5) Back");

            switch (InputUtil.readInt()) {
                case 1 -> viewConstraints();
                case 2 -> setConstraints();
                case 3 -> viewPreferences();
                case 4 -> setPreferences();
                case 5 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewConstraints() {
        // TODO
    }

    private void setConstraints() {
        // TODO
    }

    private void viewPreferences() {
        // TODO
    }

    private void setPreferences() {
        // TODO
    }
}