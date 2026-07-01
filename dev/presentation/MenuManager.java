package presentation;

import presentation.hr.HRDashboardMenu; // Swapped HRMenu import for HRDashboardMenu
import enums.SiteType;
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
    private final TransportService transportService;

    public MenuManager(AuthService authService, SchedulingService schedulingService, PersonnelService personnelService, TransportService transportService) {
        this.authService = authService;
        this.hrDashboardMenu = new HRDashboardMenu(this, schedulingService, personnelService, authService, transportService);
        this.employeeMenu = new EmployeeMenu(this, schedulingService, personnelService, authService);
        this.transportMenu = new TransportMenu(transportService);
        this.transportService = transportService;
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
    
    if (response.isError()) {
        System.out.println("Registration failed: " + response.getErrorMessage());
    } else {
        System.out.println("HR registered successfully.");
        
        // Automatically register Branch 0 with default values
        int branchId = 0;
        String branchName = "Branch 0 (HQ)";
        String address = "Default HR Branch Address";
        String phoneNumber = "N/A";
        String contactPerson = "HR Admin";
        SiteType siteType = SiteType.BRANCH; 
        int zoneId = 0;
        String zoneName = "Default Zone";

        boolean isBranchAdded = transportService.addSite(
            branchId, 
            branchName, 
            address, 
            phoneNumber, 
            contactPerson, 
            siteType, 
            zoneId,
            zoneName   
        );

        if (isBranchAdded) {
            System.out.println("Branch 0 automatically registered successfully as a transport site.");
        } else {
            System.out.println("Note: Branch 0 already exists in the system.");
        }
    }
}
    public int getLoggedInUserId() { return loggedInUserId; }
}