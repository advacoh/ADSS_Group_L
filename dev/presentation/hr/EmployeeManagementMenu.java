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
import dev.service.EmployeeSL;
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
            System.out.println("4) View employee data");
            System.out.println("5) Edit employee data");
            System.out.println("6) Back");

            switch (InputUtil.readInt()) {
                case 1 -> hireEmployee();
                case 2 -> dismissEmployee();
                case 3 -> rehireEmployee();
                case 4 -> viewEmployeeData();
                case 5 -> editEmployeeData();
                case 6 -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void hireEmployee() {
        System.out.println("\n--- Hire New Employee ---");

        int empID = InputUtil.readInt("Enter Employee ID (9 digits): ");
        String password = InputUtil.readString("Enter Password: (at least 6 digits)");
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
        System.out.println("\n--- Dismiss Employee ---");

        int empID = InputUtil.readInt("Enter Employee ID to dismiss: ");

        System.out.println("Are you sure you want to dismiss employee " + empID + "?");
        System.out.println("1) Yes  2) No");
        if (InputUtil.readInt("Choice: ") != 1) {
            System.out.println("Dismissal cancelled.");
            return;
        }

        Response<Void> response = personnelService.deactivateEmployee(manager.getLoggedInUserId(), empID);

        if (!response.isError()) {
            System.out.println("Employee " + empID + " has been successfully dismissed.");
        } else {
            System.out.println("Failed: " + response.getErrorMessage());
        }
    }


    private void rehireEmployee() {
        System.out.println("\n--- Rehire Employee ---");

        int empID = InputUtil.readInt("Enter Employee ID to rehire: ");

        System.out.println("Are you sure you want to rehire employee " + empID + "?");
        System.out.println("1) Yes  2) No");
        if (InputUtil.readInt("Choice: ") != 1) {
            System.out.println("rehiring cancelled.");
            return;
        } 
        String password = InputUtil.readString("Enter the Employee's new Password (at least 6 digits):");

        Response<Void> response = personnelService.activateEmployee(manager.getLoggedInUserId(), empID, password);

        if (!response.isError()) {
            System.out.println("Employee " + empID + " has been successfully rehired.");
        } else {
            System.out.println("Failed: " + response.getErrorMessage());
        }
    }

    private void viewEmployeeData() {
        System.out.println("\n--- View Employee Data ---");

        int empID = InputUtil.readInt("Enter Employee ID: ");

        Response<EmployeeSL> response = personnelService.getEmployeeDetails(
            manager.getLoggedInUserId(), empID);

        if (!response.isError()) {
            System.out.println(response.getValue().toString());
        } else {
            System.out.println("Failed: " + response.getErrorMessage());
        }
    }

    private void editEmployeeData() {
        System.out.println("\n--- Edit Employee Data ---");

        int empID = InputUtil.readInt("Enter Employee ID: ");

        System.out.println("What would you like to edit?");
        System.out.println("1) Financial Details");
        System.out.println("2) Employment Details");
        System.out.println("3) Name");
        System.out.println("4) Back");

        switch (InputUtil.readInt("Choice: ")) {
            case 1 -> editFinancialDetails(empID);
            case 2 -> editEmploymentDetails(empID);
            case 3 -> editName(empID);
            case 4 -> { return; }
            default -> System.out.println("Invalid option.");
        }
    }

    private void editFinancialDetails(int empID) {
        System.out.println("\n--- Edit Financial Details ---");

        int bankAccount = InputUtil.readInt("Enter new Bank Account Number: ");

        System.out.println("Salary Type:");
        System.out.println("1) Global  2) Hourly");
        SalType salType = InputUtil.readInt("Choice: ") == 1 ? SalType.GLOBAL : SalType.HOURLY;

        int salary = InputUtil.readInt("Enter new Salary: ");

        Response<Void> response = personnelService.updateFinancialDetails(
            manager.getLoggedInUserId(), empID, bankAccount, salType, salary);

        if (!response.isError()) {
            System.out.println("Financial details updated successfully.");
        } else {
            System.out.println("Failed: " + response.getErrorMessage());
        }
    }

    private void editEmploymentDetails(int empID) {
        System.out.println("\n--- Edit Employment Details ---");

        System.out.println("Employment Type:");
        System.out.println("1) Full Time  2) Part Time");
        EmpType empType = InputUtil.readInt("Choice: ") == 1 ? EmpType.FULL_TIME : EmpType.PART_TIME;

        int vacationDays = InputUtil.readInt("Enter new Vacation Days: ");

        System.out.println("Enter new Start Date:");
        LocalDate startDate = InputUtil.readDate();

        Response<Void> response = personnelService.updateEmploymentDetails(
            manager.getLoggedInUserId(), empID, empType, vacationDays, startDate);

        if (!response.isError()) {
            System.out.println("Employment details updated successfully.");
        } else {
            System.out.println("Failed: " + response.getErrorMessage());
        }
    }

    private void editName(int empID) {
        System.out.println("\n--- Edit Employee Name ---");

        String newName = InputUtil.readString("Enter new Name: ");

        Response<Void> response = personnelService.updateEmployeeName(
            manager.getLoggedInUserId(), empID, newName);

        if (!response.isError()) {
            System.out.println("Name updated successfully.");
        } else {
            System.out.println("Failed: " + response.getErrorMessage());
        }
    }
}