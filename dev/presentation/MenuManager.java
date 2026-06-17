package presentation;

import presentation.hr.HRDashboardMenu; // Swapped HRMenu import for HRDashboardMenu
import presentation.employee.EmployeeMenu;
import presentation.transport.TransportMenu;
import service.*;

public class MenuManager {

    private static final int NO_USER = -1;
    private final AuthService authService;
    private final HRDashboardMenu hrDashboardMenu; // Changed from hrMenu
    private final EmployeeMenu employeeMenu;
    private final TransportMenu transportMenu;
    private int loggedInUserId = NO_USER;

    public MenuManager(AuthService authService, SchedulingService schedulingService, PersonnelService personnelService, TransportService transportService) {
        this.authService = authService;
        // Instantiate the global dashboard instead of a specific branch menu
        this.hrDashboardMenu = new HRDashboardMenu(this, schedulingService, personnelService, authService);
        this.employeeMenu = new EmployeeMenu(this, schedulingService, personnelService, authService);
        this.transportMenu = new TransportMenu(transportService);
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
        String password = InputUtil.readString("Enter password: ");
        Response<UserSL> response = authService.login(id, password);
        if (response.isError()) {
            System.out.println("Login failed: " + response.getErrorMessage());
            return;
        }

        UserSL result = response.getValue();
        loggedInUserId = result.getUserId();

        // Router adjustments
        if (result.isHR())
            hrDashboardMenu.show(); // Diverts the HR Manager to the global tier first
        else if(result.isDeliveryManager())
            transportMenu.start();
        else
            employeeMenu.show();

        loggedInUserId = NO_USER; 
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