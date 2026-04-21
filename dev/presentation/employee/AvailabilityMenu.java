package dev.presentation.employee;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dev.domain.ShiftType;
import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.Response;
import dev.service.SchedulingService;
import dev.service.WeeklyConstraintsSL;
import dev.service.WeeklyPreferenceSL;


public class AvailabilityMenu {

    private final MenuManager manager;
    private final SchedulingService schedulingService;

    public AvailabilityMenu(MenuManager manager, SchedulingService schedulingService) {
        this.manager = manager;
        this.schedulingService = schedulingService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Availability ===");
            System.out.println("1) View constraints");
            System.out.println("2) Set constraints");
            System.out.println("3) View preferences");
            System.out.println("4) Set preferences");
            System.out.println("5) Back");

            switch (InputUtil.readInt()) {
                case 1 -> viewConstraints();
                case 2 -> setConstraints();
                case 3 -> viewPreferences();
                case 4 -> setPreferences();
                case 5 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewConstraints() {
        int userId = manager.getLoggedInUserId();

        Response<WeeklyConstraintsSL> response = schedulingService.getWeeklyConstraints(userId, userId);
        if (response.isError())
            System.out.println("Could not fetch constraints: " + response.getErrorMessage());
        else
            System.out.println(response.getValue());
    }

    private void setConstraints() {
        System.out.println("\n--- Set Weekly Constraints ---");
        System.out.println("For each shift, indicate if you are available.");

        Map<LocalDate, Set<ShiftType>> constraints = new HashMap<>();

        LocalDate sunday = LocalDate.now().plusDays(1);
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }

        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        for (int i = 0; i < 7; i++) {
            LocalDate day = sunday.plusDays(i);
            Set<ShiftType> availableShifts = new HashSet<>();

            System.out.println("\n" + dayNames[i] + " (" + day + "):");

            for (ShiftType shift : ShiftType.values()) {
                if (InputUtil.readYesNo("Available for " + shift.getValue() + "?")) {
                    availableShifts.add(shift);
                }
            }

            if (!availableShifts.isEmpty()) {
                constraints.put(day, availableShifts);
            }
        }

        Response<Void> response = schedulingService.setWeeklyConstraints(
                manager.getLoggedInUserId(), constraints
        );

        if (!response.isError())
            System.out.println("Constraints set successfully.");
        else
            System.out.println("Failed: " + response.getErrorMessage());
    }

    private void viewPreferences() {
        int userId = manager.getLoggedInUserId();

        Response<WeeklyPreferenceSL> response = schedulingService.getWeeklyPreferences(userId, userId);
        if (response.isError())
            System.out.println("Could not fetch preferences: " + response.getErrorMessage());
        else
            System.out.println(response.getValue());
    }

    private void setPreferences() {
        System.out.println("\n--- Set Weekly Preferences ---");
        System.out.println("For each shift, indicate if you prefer to work.");
        System.out.println("Note: You can only prefer shifts you are already available for.");

        Map<LocalDate, Set<ShiftType>> preferences = new HashMap<>();

        LocalDate sunday = LocalDate.now().plusDays(1);
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }

        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        for (int i = 0; i < 7; i++) {
            LocalDate day = sunday.plusDays(i);
            Set<ShiftType> preferredShifts = new HashSet<>();

            System.out.println("\n" + dayNames[i] + " (" + day + "):");

            for (ShiftType shift : ShiftType.values()) {
                if (InputUtil.readYesNo("Prefer " + shift.getValue() + "?")) {
                    preferredShifts.add(shift);
                }
            }

            if (!preferredShifts.isEmpty()) {
                preferences.put(day, preferredShifts);
            }
        }

        Response<Void> response = schedulingService.setWeeklyPreferences(
                manager.getLoggedInUserId(), preferences
        );

        if (!response.isError())
            System.out.println("Preferences submitted successfully.");
        else
            System.out.println("Failed: " + response.getErrorMessage());
    }
}