package dev.presentation.employee;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.AuthService;
import dev.service.PersonnelService;
import dev.service.Response;
import dev.service.SchedulingService;

public class EmployeeMenu {

    private final MenuManager manager;
    private final AvailabilityMenu availabilityMenu;
    private final EmployeeRequestMenu requestMenu;
    private final ProfileMenu profileMenu;
    private final AuthService authService;

    public EmployeeMenu(MenuManager manager, SchedulingService schedulingService, PersonnelService personnelService, AuthService authService) {
        this.manager = manager;
        availabilityMenu = new AvailabilityMenu(manager, schedulingService);
        requestMenu = new EmployeeRequestMenu(manager, schedulingService);
        profileMenu = new ProfileMenu(manager, personnelService);
        this.authService = authService;
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
                case 4 -> { Response<Void> response = authService.logout(manager.getLoggedInUserId());
                    if (response.isError())
                        System.out.println("Logout failed: " + response.getErrorMessage());
                    else
                        System.out.println("Logging out...");
                    return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}