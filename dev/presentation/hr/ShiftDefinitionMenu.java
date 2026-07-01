package presentation.hr;

import presentation.InputUtil;
import presentation.MenuManager;
import service.Response;
import service.SchedulingService;
import domain.hr.ShiftType;
import domain.hr.Certification;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShiftDefinitionMenu {

    private final MenuManager manager;
    private final SchedulingService schedulingService;
    private final int branchId; // The active branch context

    // Updated constructor to capture the branch context passed from HRMenu
    public ShiftDefinitionMenu(MenuManager manager, SchedulingService schedulingService, int branchId) {
        this.manager = manager;
        this.schedulingService = schedulingService;
        this.branchId = branchId;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Shift Definition (Branch ID: " + branchId + ") ===");
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
        LocalDate date = InputUtil.readDayOfWeek();
        ShiftType type = InputUtil.readShiftType();

        // Passed branchId to contextually create a shift profile for this branch location
        Response<Void> response = schedulingService.createShift(
                manager.getLoggedInUserId(), branchId, date, type
        );

        if (response.isError())
            System.out.println("Shift was not created: " + response.getErrorMessage());
        else
            System.out.println("Shift created successfully!");
    }

    private void setRequirements() {
        LocalDate date = InputUtil.readDayOfWeek();
        ShiftType type = InputUtil.readShiftType();
        List<Certification> eligibleRoles = new ArrayList<>(Arrays.asList(Certification.values()));
        eligibleRoles.remove(Certification.HR_MANAGER);
        Certification role = InputUtil.readRole(eligibleRoles);

        int count = InputUtil.readInt("Select amount: ");

        // Passed branchId to map role staffing constraints directly onto this branch's schedule
        Response<Void> response = schedulingService.setRequirement(
                manager.getLoggedInUserId(), branchId, date, type, role, count
        );

        if (response.isError())
            System.out.println("Requirement was not set: " + response.getErrorMessage());
        else
            System.out.println("Requirement set successfully!");
    }
}