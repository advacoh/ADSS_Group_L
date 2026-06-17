package presentation.hr;

import java.time.LocalDate;
import presentation.InputUtil;
import presentation.MenuManager;
import service.*;

public class HRDashboardMenu {
    private final MenuManager manager;
    private final SchedulingService schedulingService;
    private final PersonnelService personnelService; // Handles employee AND branch data checks
    private final AuthService authService;

    public HRDashboardMenu(MenuManager manager, SchedulingService schedulingService, 
                             PersonnelService personnelService, AuthService authService) {
        this.manager = manager;
        this.schedulingService = schedulingService;
        this.personnelService = personnelService;
        this.authService = authService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== HR Global Dashboard ===");
            System.out.println("1) Register New Branch");
            System.out.println("2) Select Branch to Manage");
            System.out.println("3) Add Transport Manager");
            System.out.println("4) Set Global Deadline");
            System.out.println("5) Logout");

            int choice = InputUtil.readInt();

            switch (choice) {
                case 1 -> registerNewBranch();
                case 2 -> openBranchMenu();
                case 3 -> addTransportManager();
                case 4 -> setGlobalDeadline();
                case 5 -> {
                    Response<Void> response = authService.logout(manager.getLoggedInUserId());
                    if (response.isError()) {
                        System.out.println("Logout failed: " + response.getErrorMessage());
                    } else {
                        System.out.println("Logging out...");
                    }
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void openBranchMenu() {
        System.out.print("Enter Branch ID: ");
        int branchId = InputUtil.readInt();
        
        Response<Boolean> existsResponse = personnelService.branchExists(branchId);
        if (existsResponse.isError() || !existsResponse.getValue()) {
            System.out.println("Branch does not exist or error occurred.");
            return;
        }

        HRMenu branchMenu = new HRMenu(manager, schedulingService, personnelService, authService, branchId);
        branchMenu.show();
    }

    private void registerNewBranch() {
        System.out.println("\n=== Register New Branch ===");
        System.out.print("Enter New Branch ID: ");
        int branchId = InputUtil.readInt();

        String branchName = InputUtil.readString("Enter Branch Name/Location: "); 

        Response<Void> response = personnelService.registerBranch(branchId, branchName);
        if (response.isError()) {
            System.out.println("Registration failed: " + response.getErrorMessage());
        } else {
            System.out.println("Branch " + branchId + " registered successfully.");
        }
    }

    private void addTransportManager() {
        System.out.println("\n=== Add New Transport Manager ===");
        System.out.print("Enter Employee ID: ");
        int empId = InputUtil.readInt();

        String empName = InputUtil.readString("Enter Employee Name: ");

        String password = InputUtil.readString("Enter Initial Password: ");

        Response<Void> response = personnelService.addTransportManager(empId, empName, password);
        if (response.isError()) {
         System.out.println("Failed to add transport manager: " + response.getErrorMessage());
        } else {
            System.out.println("Transport Manager " + empName + " successfully added to the system.");
        }
}

    private void setGlobalDeadline() {
        System.out.println("\n=== Set Global Submission Deadline ===");

        String dateInput = InputUtil.readString("Enter Deadline Date (YYYY-MM-DD): ");

        try {
            LocalDate deadlineDate = LocalDate.parse(dateInput);
            int hrUserId = manager.getLoggedInUserId();

            Response<Void> response = schedulingService.setDeadline(hrUserId, deadlineDate);
            if (response.isError()) {
                System.out.println("Failed to set deadline: " + response.getErrorMessage());
            } else {
                System.out.println("Global submission deadline successfully updated to: " + deadlineDate);
            }
        } catch (Exception e) {
            System.out.println("Invalid date format. Please use the YYYY-MM-DD layout.");
        }
    }
}