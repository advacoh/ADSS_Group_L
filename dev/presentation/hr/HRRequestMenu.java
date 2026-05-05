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
        if (requests == null || requests.isEmpty()) return;

        OverrideRequestSL selected = InputUtil.selectItem(requests);
        if (selected == null) return;

        System.out.println(selected);
        promptAssignWithOverride(selected);
    }

    private List<OverrideRequestSL> fetchAndDisplayRequests() {
        Response<List<OverrideRequestSL>> response = schedulingService.viewSentRequests(
                manager.getLoggedInUserId()
        );
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
            System.out.println((i + 1) + ") " + requests.get(i).toShortString());
        }

        return requests;
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