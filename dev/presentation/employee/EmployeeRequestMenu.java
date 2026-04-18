package dev.presentation.employee;

import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.OverrideRequestSL;
import dev.service.SchedulingService;

import java.util.List;

public class EmployeeRequestMenu {

    private final MenuManager manager;
    private final SchedulingService schedulingService;

    public EmployeeRequestMenu(MenuManager manager, SchedulingService schedulingService) {
        this.manager = manager;
        this.schedulingService = schedulingService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Requests ===");
            System.out.println("1) View requests");
            System.out.println("2) Back");

            switch (InputUtil.readInt()) {
                case 1 -> viewRequests();
                case 2 -> {
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewRequests() {
        List<OverrideRequestSL> requests = fetchAndDisplayRequests();
        if (requests == null || requests.isEmpty()) return;

        OverrideRequestSL selected = selectRequest(requests);
        if (selected == null) return;

        displayRequestDetails(selected);
        promptRespond(selected);
    }

    private List<OverrideRequestSL> fetchAndDisplayRequests() {
        // TODO: call service, print numbered list of requests
        // return the list so selectRequest can index into it
        return null;
    }

    private OverrideRequestSL selectRequest(List<OverrideRequestSL> requests) {
        // TODO: prompt "Select request number (0 to cancel)"
        // validate index and return the chosen request
        return null;
    }

    private void displayRequestDetails(OverrideRequestSL request) {
        // TODO: print all fields of the request clearly
    }

    private void promptRespond(OverrideRequestSL request) {
        // TODO: "Respond? (y/n)" -> "Approve? (y/n)" -> call service
    }
}