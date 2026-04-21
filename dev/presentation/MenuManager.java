package dev.presentation;

import dev.presentation.hr.HRMenu;
import dev.presentation.employee.EmployeeMenu;
import dev.service.*;

public class MenuManager {

    private static final int NO_USER = -1;
    private final AuthService authService;
    private final HRMenu hrMenu;
    private final EmployeeMenu employeeMenu;

    private int loggedInUserId = NO_USER;

    public MenuManager(AuthService authService, SchedulingService schedulingService, PersonnelService personnelService) {
        this.authService = authService;
        this.hrMenu = new HRMenu(this, schedulingService, personnelService, authService);
        this.employeeMenu = new EmployeeMenu(this, schedulingService, personnelService, authService);
    }

    public void start() {
        mainMenu();
    }

    private void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1) Login");
            System.out.println("2) Register as HR");
            System.out.println("3) Exit");

            switch (InputUtil.readInt()) {
                case 1 -> login();
                case 2 -> registerHR();
                case 3 -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        System.out.println("Goodbye!");
    }

    private void login() {
        int id = InputUtil.readInt("Enter ID: ");
        String password = InputUtil.readString("Enter password: ");;
        Response<UserSL> response = authService.login(id, password);
        if (response.isError()) {
            System.out.println("Login failed: " + response.getErrorMessage());
            return;
        }

        UserSL result = response.getValue();
        loggedInUserId = result.getUserId();

        if (result.isHR())
            hrMenu.show();
        else
            employeeMenu.show();

        loggedInUserId = NO_USER; // clear session after menu exits
    }

    private void registerHR() {
        int id = InputUtil.readInt("Enter ID: ");
        String password = InputUtil.readString("Enter password: ");
        Response<Void> response = authService.registerHR(id, password);
        if (response.isError())
            System.out.println("Registration failed: " + response.getErrorMessage());
        else
            System.out.println("HR registered successfully.");
    }

    public int getLoggedInUserId() { return loggedInUserId; }
}