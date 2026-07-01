package presentation.employee;

import domain.hr.RequestStatus;
import presentation.InputUtil;
import presentation.MenuManager;
import service.OverrideRequestSL;
import service.Response;
import service.SchedulingService;

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

        OverrideRequestSL selected = InputUtil.selectItem(requests);
        if (selected == null) return;

        System.out.println(selected);
        promptRespond(selected);
    }

    private void promptRespond(OverrideRequestSL request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            System.out.println("This request has already been responded to.");
            return;
        }

        System.out.print("Respond to this request? (y/n): ");
        while (true) {
            switch (InputUtil.readRaw().toLowerCase()) {
                case "y" -> {
                    approveOrReject(request);
                    return;
                }
                case "n" -> { return; }
                default -> System.out.print("Please enter y or n: ");
            }
        }
    }

    private void approveOrReject(OverrideRequestSL request) {
        System.out.print("Approve? (y/n): ");
        while (true) {
            switch (InputUtil.readRaw().toLowerCase()) {
                case "y" -> {
                    respond(request, true);
                    return;
                }
                case "n" -> {
                    respond(request, false);
                    return;
                }
                default -> System.out.print("Please enter y or n: ");
            }
        }
    }

    private List<OverrideRequestSL> fetchAndDisplayRequests() {
        Response<List<OverrideRequestSL>> response = schedulingService.viewReceivedRequests(
                manager.getLoggedInUserId()
        );

        if (response.isError()) {
            System.out.println("Could not fetch requests: " + response.getErrorMessage());
            return null;
        }

        List<OverrideRequestSL> requests = response.getValue();
        if (requests.isEmpty()) {
            System.out.println("No pending requests.");
            return requests;
        }

        System.out.println("\n=== Received Override Requests ===");
        for (int i = 0; i < requests.size(); i++) {
            System.out.println((i + 1) + ") " + requests.get(i).toShortString());
        }

        return requests;
    }

    private void respond(OverrideRequestSL request, boolean approved) {
        Response<Void> response = schedulingService.respondToRequest(
                manager.getLoggedInUserId(),
                request.getId(),
                approved
        );
        if (response.isError())
            System.out.println("Failed to respond: " + response.getErrorMessage());
        else
            System.out.println(approved ? "Request approved." : "Request rejected.");
    }
}