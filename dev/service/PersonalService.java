package dev.service;
import dev.domain.UserController;
import dev.domain.EmployeeController;
import dev.domain.Certification;
import dev.domain.EmpType;
import dev.domain.Employee;
import dev.domain.SalType;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;


public class PersonalService {
    private UserController userController;
    private EmployeeController employeeController;

    public PersonalService(UserController userController, EmployeeController employeeController){
        this.userController = userController;
        this.employeeController = employeeController;
    }

    public void addEmployee(int activeUserId, String rawData) {
        if (rawData == null || rawData.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }

        String[] parts = rawData.split(",");
        if (parts.length < 14) {
            throw new IllegalArgumentException("Missing information. Expected 14 fields, but got " + parts.length);
        }

        try {
            int newEmpID = Integer.parseInt(parts[0].trim());
            String password = parts[1].trim();
            String name = parts[2].trim();
            int bankAccount = Integer.parseInt(parts[3].trim());

            LocalDate startDate;
            try {
                startDate = LocalDate.parse(parts[4].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Please use dd/MM/yyyy.");
            }

            EmpType empType = parseEnum(EmpType.class, parts[5].trim(), "Employment Type");
            SalType salaryType = parseEnum(SalType.class, parts[6].trim(), "Salary Type");

            int salary = Integer.parseInt(parts[7].trim());
            int vacationDay = Integer.parseInt(parts[8].trim());
            boolean willOvertime = parseBooleanStrict(parts[9].trim(), "Overtime Preference");
            int dayOff = Integer.parseInt(parts[10].trim());
            boolean doubleShiftAllowed = parseBooleanStrict(parts[11].trim(), "Double Shift Preference");

            List<Certification> certificationsList = parseCertifications(parts[12].trim());

            employeeController.addEmployee(
                activeUserId, newEmpID, password, name, bankAccount, startDate,
                empType, salaryType, salary, vacationDay, willOvertime,
                dayOff, doubleShiftAllowed, certificationsList
            );

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Numeric error: One of the fields (ID, Bank Account, Salary, vacation Day and day off) must be a whole number.");
        }
    }

    private <T extends Enum<T>> T parseEnum(Class<T> enumClass, String value, String fieldName) {
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid " + fieldName + ": '" + value + "'.");
        }
    }

    private boolean parseBooleanStrict(String value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        String val = value.trim().toLowerCase();
        if (val.equals("true")) return true;
        if (val.equals("false")) return false;
        throw new IllegalArgumentException(fieldName + " must be either 'true' or 'false'. Received: '" + value + "'");
    }

    private List<Certification> parseCertifications(String certsString) {
        List<Certification> list = new ArrayList<>();
        if (certsString == null || certsString.isEmpty() || certsString.equalsIgnoreCase("none")) {
            return list;
        }
        
        String[] certNames = certsString.split(";");
        for (String certName : certNames) {
            list.add(parseEnum(Certification.class, certName, "Certification"));
        }
        return list;
    }

    public String deactivateEmployee(int activeUserId, int targetEmpId) {
        try {
            employeeController.dismissEmployee(activeUserId, targetEmpId);
            return "Employee with ID " + targetEmpId + " has been successfully deactivated.";
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Deactivation failed: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("An error occurred during deactivation: " + e.getMessage());
        }
    }

    public String addCertification(int activeUserId, int targetEmpId, String role) { 
        try{
            Certification cert = Certification.valueOf(role.trim().toUpperCase());
            this.employeeController.addCertification(activeUserId, targetEmpId, cert);
            return "Employee certification: " + role + " was added successfully.";
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Adding certification faild: " + role);
        }
     }

    public String removeCertification(int activeUserId, int targetEmpId, String role){
        try{
            Certification cert = Certification.valueOf(role.trim().toUpperCase());
            this.employeeController.removeCertification(activeUserId, targetEmpId, cert);
            return "Employee certification: " + role + " was removed successfully.";
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("removing certification failed: " + role);
        }
    }

    public String updateFinancialDetails(int userID, int empId, int newBankAccount, String newSalaryType, int newSalary){
        try{
            SalType salType;
            try {
                salType = SalType.valueOf(newSalaryType.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid Salary Type: '" + newSalaryType + "'. Must be MONTHLY or HOURLY.");
            }

            this.employeeController.updateFinancialDetails(userID, empId, newBankAccount, salType, newSalary);
            return "Updating financial details was successful. ";
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Updating financial details failed: " + e.getMessage());
        }
    }

    public String updateEmploymentDetails(int userID, int empId, String rawEmpType, int vacationDay, String rawStartDay){
        try{
            EmpType empType;
            try {
                empType = EmpType.valueOf(rawEmpType.trim().toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                throw new IllegalArgumentException("Invalid Employment Type: '" + rawEmpType + "'.");
            }
           
            LocalDate startDate;
            try {
                startDate = LocalDate.parse(rawStartDay.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format: '" + rawStartDay + "'. Use dd/MM/yyyy.");
            }
            this.employeeController.updateEmploymentDetails(userID, empId, empType, vacationDay, startDate);
            return "Updating employment details was successful.";
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Updating employment details failed: " + e.getMessage());
        }
    }
    

    public String updateEmployeeName(int userID, int empId, String newName){
        try{
            this.employeeController.updateEmployeeName(userID, empId, newName);
            return "Updating the employee's name was successful. ";
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Updating the employee's name failed: " + e.getMessage());
        }
    }


    public String updateEmployeeSettings(int userID, int empId, int dayOff, String rawWillDouble, String rawWillOverTime) {
        try {
            boolean willDouble = parseBooleanStrict(rawWillDouble, "Double Shift preference");
            boolean willOverTime = parseBooleanStrict(rawWillOverTime, "Overtime preference");
            this.employeeController.updateEmployeeSettings(userID, empId, dayOff, willDouble, willOverTime);
            return "Updating the employee's settings was successful.";
        } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Updating the employee's settings failed: " + e.getMessage());
            }
    }

    public String getEmployeeDetails(int activeUserId, int targetEmpId) { 
        try{
            Employee emp = this.employeeController.getEmployeeDetails(activeUserId,targetEmpId);
            EmployeeSL empSL = new EmployeeSL(targetEmpId, emp.getName(), emp.getBankAccount(), emp.getStartDate(), 
            emp.getEmployementType(), emp.getSalaryType(), emp.getSalary(), emp.getVacation(), emp.willOvertime(), emp.getDayOff(),
            emp.willDouble(), emp.getCertifications());
            return empSL.toString();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}

