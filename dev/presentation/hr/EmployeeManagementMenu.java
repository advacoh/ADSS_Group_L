package dev.presentation.hr;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import dev.domain.EmpType;
import dev.domain.SalType;
import dev.domain.Certification;
import dev.service.Response;
import dev.presentation.InputUtil;
import dev.presentation.MenuManager;
import dev.service.PersonnelService;
import dev.service.SchedulingService;

public class EmployeeManagementMenu {

    private final MenuManager manager;
    private final PersonnelService personnelService;

    public EmployeeManagementMenu(MenuManager manager, PersonnelService personnelService) {
        this.manager = manager;
        this.personnelService = personnelService;
    }

    public void show() {
        while (true) {
            System.out.println("\n=== Employee Management ===");
            System.out.println("1) Hire employee");
            System.out.println("2) Dismiss employee");
            System.out.println("3) Rehire employee");
            System.out.println("4) View/edit employee data");
            System.out.println("5) Back");

            switch (InputUtil.readInt()) {
                case 1 -> hireEmployee();
                case 2 -> dismissEmployee();
                case 3 -> rehireEmployee();
                case 4 -> viewEditEmployee();
                case 5 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void hireEmployee() {
        System.out.println("\n--- Hire New Employee ---");

        int empID = InputUtil.readInt("Enter Employee ID (9 digits): ");
        String password = InputUtil.readString("Enter Password: ");
        String name = InputUtil.readString("Enter Full Name: ");
        int bankAccount = InputUtil.readInt("Enter Bank Account Number: ");

        System.out.println("Enter Start Date:");
        LocalDate startDate = InputUtil.readDate();

        System.out.println("Employment Type:");
        System.out.println("1) Full Time");
        System.out.println("2) Part Time");
        EmpType empType = InputUtil.readInt("Choice: ") == 1 ? EmpType.FULL_TIME : EmpType.PART_TIME;

        System.out.println("Salary Type:");
        System.out.println("1) Global");
        System.out.println("2) Hourly");
        SalType salType = InputUtil.readInt("Choice: ") == 1 ? SalType.GLOBAL : SalType.HOURLY;

        int salary = InputUtil.readInt("Enter Salary: ");
        int vacationDays = InputUtil.readInt("Enter Vacation Days: ");

        System.out.println("Will do overtime? 1) Yes  2) No");
        boolean willOvertime = InputUtil.readInt("Choice: ") == 1;

        int dayOff = InputUtil.readInt("Day Off (1=Sun, 2=Mon, 3=Tue, 4=Wed, 5=Thu, 6=Fri, 7=Sat): ");

        System.out.println("Allow double shifts? 1) Yes  2) No");
        boolean doubleShift = InputUtil.readInt("Choice: ") == 1;

        List<Certification> certifications = new ArrayList<>();
        while (true) {
            certifications.add(InputUtil.readRole());
            System.out.println("Add another certification? 1) Yes  2) No");
            if (InputUtil.readInt("Choice: ") != 1) break;
        }

        Response<Void> response = personnelService.addEmployee(
            manager.getLoggedInUserId(), empID, password, name, bankAccount, startDate,
            empType, salType, salary, vacationDays, willOvertime,
            dayOff, doubleShift, certifications);

        if (!response.isError()) {
            System.out.println("Employee hired successfully.");
        } else {
            System.out.println("Failed: " + response.getErrorMessage());
        }
    }

    




    private void dismissEmployee() {
        // TODO
    }

    private void rehireEmployee() {
        // TODO
    }

    private void viewEditEmployee() {
        // TODO
    }
}