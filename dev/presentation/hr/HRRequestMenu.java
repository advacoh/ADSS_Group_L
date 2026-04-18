package dev.presentation.hr;

import dev.domain.RequestStatus;
import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.OverrideRequestSL;
import dev.service.Response;
import dev.service.SchedulingService;

import java.util.List;

public class HRRequestMenu {

    private final MenuManager manager;
    private final SchedulingService schedulingService;

    public HRRequestMenu(MenuManager manager, SchedulingService schedulingService) {
        this.manager = manager;
        this.schedulingService = schedulingService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Request Handling ===");
            System.out.println("1) View requests");
            System.out.println("2) Back");

            switch (InputUtil.readInt()) {
                case 1 -> viewRequests();
                case 2 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewRequests() {

        List<OverrideRequestSL> requests = fetchAndDisplayRequests();
        if (requests == null || requests.isEmpty())
            return;

        OverrideRequestSL selected = selectRequest(requests);
        if (selected == null) return;
        displayRequestDetails(selected);
        promptAssignWithOverride(selected);
    }

    private List<OverrideRequestSL> fetchAndDisplayRequests() {
        Response<List<OverrideRequestSL>> response = schedulingService.viewSentRequests(manager.getLoggedInUserId());
        if (response.isError()) {
            System.out.println("Could not fetch requests: " + response.getErrorMessage());
            return null;
        }
        List<OverrideRequestSL> requests = response.getValue();
        if (requests.isEmpty()) {
            System.out.println("No open requests.");
            return requests;
        }
        System.out.println("\n=== Override Requests ===");
        for (int i = 0; i < requests.size(); i++) {
            OverrideRequestSL r = requests.get(i);
            System.out.printf("%d) Employee %d | %s | %s | %s | %s%n",
                    i + 1,
                    r.getEmpId(),
                    r.getDate(),
                    r.getShiftType().getValue(),
                    r.getRole().getValue(),
                    r.getStatus().getValue()
            );
        }

        return requests;
    }

    private OverrideRequestSL selectRequest(List<OverrideRequestSL> requests) {

        System.out.print("Select request number (0 to cancel): ");
        while (true) {
            int choice = InputUtil.readInt();
            if (choice == 0)
                return null;
            if (choice >= 1 && choice <= requests.size())
                return requests.get(choice - 1);
            System.out.println("Invalid option.");
        }
    }

    private void displayRequestDetails(OverrideRequestSL request) {
        System.out.println("\n=== Request Details ===");
        System.out.println("Request ID : " + request.getId());
        System.out.println("Employee   : " + request.getEmpId());
        System.out.println("Date       : " + request.getDate());
        System.out.println("Shift      : " + request.getShiftType().getValue());
        System.out.println("Role       : " + request.getRole().getValue());
        System.out.println("Status     : " + request.getStatus().getValue());
    }

    private void promptAssignWithOverride(OverrideRequestSL request) {
        if (request.getStatus() != RequestStatus.APPROVED) {
            System.out.println("Request has not been approved yet, cannot assign.");
            return;
        }
        System.out.print("Assign with override? (y/n): ");

        while (true) {

            switch (InputUtil.readRaw().toLowerCase()) {
                case "y" -> {
                    Response<Void> response = schedulingService.assignWithOverride(
                                            manager.getLoggedInUserId(),
                                            request.getId()
                                    );
                    if (response.isError())
                        System.out.println("Assignment failed: " + response.getErrorMessage());
                    else
                        System.out.println("Employee assigned successfully!");
                    return;
                }
                case "n" -> { return; }
                default ->
                        System.out.print("Please enter y or n: ");
            }
        }
    }

}