package dev.presentation.hr;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.Response;
import dev.service.SchedulingService;
import dev.domain.ShiftType;
import dev.domain.Certification;
import java.time.LocalDate;

public class ShiftDefinitionMenu {

    private final MenuManager manager;
    private final SchedulingService schedulingService;

    public ShiftDefinitionMenu(MenuManager manager, SchedulingService schedulingService) {
        this.manager = manager;
        this.schedulingService = schedulingService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Shift Definition ===");
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

        Response<Void> response = schedulingService.createShift(
                manager.getLoggedInUserId(), date, type
        );

        if (response.isError())
            System.out.println("Shift was not created: " + response.getErrorMessage());
        else
            System.out.println("Shift created successfully!");
    }

    private void setRequirements() {
        LocalDate date = InputUtil.readDayOfWeek();
        ShiftType type = InputUtil.readShiftType();
        Certification role = InputUtil.readRole();

        int count = InputUtil.readInt("Select amount: ");

        Response<Void> response = schedulingService.setRequirement(
                manager.getLoggedInUserId(), date, type, role, count
        );

        if (response.isError())
            System.out.println("Requirement was not set: " + response.getErrorMessage());
        else
            System.out.println("Requirement set successfully!");
    }
}