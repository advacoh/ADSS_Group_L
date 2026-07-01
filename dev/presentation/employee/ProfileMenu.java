package presentation.employee;

import presentation.InputUtil;
import presentation.MenuManager;
import service.EmployeeSL;
import service.PersonnelService;
import service.Response;

public class ProfileMenu {

    private final MenuManager manager;
    private final PersonnelService personnelService;

    public ProfileMenu(MenuManager manager, PersonnelService personnelService) {
        this.manager = manager;
        this.personnelService = personnelService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== My Profile ===");
            System.out.println("1) View my details");
            System.out.println("2) Edit settings");
            System.out.println("3) Back");

            switch (InputUtil.readInt()) {
                case 1 -> viewDetails();
                case 2 -> editSettings();
                case 3 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewDetails() {
        System.out.println("\n--- View Details ---");

        Response<EmployeeSL> response = personnelService.getEmployeeDetails(
            manager.getLoggedInUserId());

        if (!response.isError()) {
            System.out.println(response.getValue().toString());
        } else {
            System.out.println("Failed: " + response.getErrorMessage());
        }
    }

    private void editSettings() {
        System.out.println("\n--- Edit Your Settings ---");
        
        int dayOff = InputUtil.readInt("Day Off (1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri, 7=Sat): ");

        System.out.println("Allow double shifts? 1) Yes  2) No");
        boolean willDouble = InputUtil.readInt("Choice: ") == 1;

        System.out.println("Will do overtime? 1) Yes  2) No");
        boolean willOvertime = InputUtil.readInt("Choice: ") == 1;

        Response<Void> response = personnelService.updateEmployeeSettings(
            manager.getLoggedInUserId(), dayOff, willDouble, willOvertime);

        if (!response.isError()) {
            System.out.println("Settings updated successfully.");
        } else {
            System.out.println("Failed: " + response.getErrorMessage());
        }
    }
}