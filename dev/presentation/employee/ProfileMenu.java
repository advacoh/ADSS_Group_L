package dev.presentation.employee;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.PersonnelService;

public class ProfileMenu {

    private final MenuManager manager;
    private final PersonnelService personnelService;

    public ProfileMenu(MenuManager manager, PersonnelService personnelService) {
        this.manager = manager;
        this.personnelService = personnelService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== My Profile ===");
            System.out.println("1) View my details");
            System.out.println("2) Edit settings");
            System.out.println("3) Back");

            switch (InputUtil.readInt()) {
                case 1 -> viewDetails();
                case 2 -> editSettings();
                case 3 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewDetails() {
        // TODO
    }

    private void editSettings() {
        // TODO
    }
}