package presentation.hr;

import domain.hr.ShiftType;
import presentation.InputUtil;
import presentation.MenuManager;
import service.*;

import java.time.LocalDate;
import java.util.List;

public class HRMenu {

    private final MenuManager manager;
    private final SchedulingService schedulingService;
    private final AuthService authService;
    private final int branchId; // The active branch scope for this entire menu session

    private final ShiftDefinitionMenu shiftDefinitionMenu;
    private final ShiftFillingMenu shiftFillingMenu;
    private final EmployeeManagementMenu employeeMenu;
    private final HRRequestMenu requestMenu;

    private final PersonnelService personnelService;


    public HRMenu(MenuManager manager, SchedulingService schedulingService, PersonnelService personnelService, AuthService authService, int branchId) {
        this.manager = manager;
        this.schedulingService = schedulingService;
        this.personnelService = personnelService;
        this.authService = authService;
        this.branchId = branchId;
        
        // Passing the branchId forward so sub-menus automatically target the correct store
        this.shiftDefinitionMenu = new ShiftDefinitionMenu(manager, schedulingService, branchId);
        this.shiftFillingMenu = new ShiftFillingMenu(manager, schedulingService, branchId);
        this.employeeMenu = new EmployeeManagementMenu(manager, personnelService, branchId);
        this.requestMenu = new HRRequestMenu(manager, schedulingService, branchId);
    }

    public void show() {

        while (true) {

            System.out.println("\n=== HR Menu (Branch ID: " + branchId + ") ===");

            System.out.println("1) Shift Definition");
            System.out.println("2) Shift Filling");
            System.out.println("3) Employee Management");
            System.out.println("4) Request Handling");
            System.out.println("5) View History");
            System.out.println("6) View PENDING Deliveries Alerts");
            System.out.println("7) Back to Global Dashboard");

            int choice = InputUtil.readInt();

            switch (choice) {

                case 1 ->
                        shiftDefinitionMenu.show();

                case 2 ->
                        shiftFillingMenu.show();

                case 3 ->
                        employeeMenu.show();

                case 4 ->
                        requestMenu.show();

                case 5 -> viewHistory();
                case 6 -> viewPendingDeliveriesAlerts();
                case 7 -> { 
                    System.out.println("Returning to global dashboard...");
                    return;
                }

                default ->
                        System.out.println(
                                "Invalid option."
                        );
            }
        }
    }

    private void viewHistory() {
        System.out.println("\n=== View History ===");

        LocalDate date = InputUtil.readDate();
        ShiftType type = InputUtil.readShiftType();

        displayPastShift(date, type);
    }

    private void displayPastShift(LocalDate date, ShiftType type) {
        // Added branchId here so history is filtered specifically to this branch context
        Response<ShiftSL> response = schedulingService.getPastShift(
                manager.getLoggedInUserId(), branchId, date, type
        );
        if (response.isError()) {
            System.out.println("Shift not found: " + response.getErrorMessage());
        } else {
            System.out.println(response.getValue().toString());
        }
    }

    private void viewPendingDeliveriesAlerts() {
        System.out.println("\n=== Deliveries Awaiting HR Staffing ===");
        List<DeliverySL> pendingDeliveries = personnelService.getPendingDeliveries();
        
        if (pendingDeliveries.isEmpty()) {
            System.out.println("Good news! There are no pending deliveries awaiting staffing.");
            return;
        }
        
        for (DeliverySL d : pendingDeliveries) {
            System.out.println("Delivery ID: " + d.getId() + " | Date: " + d.getDate() + " | Departure: " + d.getDepartureTime());
            System.out.println("Missing Requirement: " + d.getPendingReason());
            System.out.println("---------------------------------------------------");
        }
        
        System.out.println("Make sure to assign a Driver or a Warehouse Worker to the required shifts to resolve these!");
    }

}