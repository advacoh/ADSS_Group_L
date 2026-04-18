package dev.presentation.employee;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.PersonnelService;
import dev.service.SchedulingService;

public class EmployeeMenu {

    private final MenuManager manager;
    private final AvailabilityMenu availabilityMenu;
    private final EmployeeRequestMenu requestMenu;
    private final ProfileMenu profileMenu;

    public EmployeeMenu(MenuManager manager, SchedulingService schedulingService, PersonnelService personnelService) {
        this.manager = manager;
        availabilityMenu = new AvailabilityMenu(manager, schedulingService);
        requestMenu = new EmployeeRequestMenu(manager, schedulingService);
        profileMenu = new ProfileMenu(manager, personnelService);
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Employee Menu ===");
            System.out.println("1) Availability");
            System.out.println("2) Requests");
            System.out.println("3) My Profile");
            System.out.println("4) Logout");

            switch (InputUtil.readInt()) {
                case 1 -> availabilityMenu.show();
                case 2 -> requestMenu.show();
                case 3 -> profileMenu.show();
                case 4 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}