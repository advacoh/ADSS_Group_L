package dev.domain;
import java.util.List;
import java.util.Date;

public class EmployeeController {
    private EmployeeMemory employeeMemory;

    public void addEmployee(int ID, String name, int bankAccount, Date startDate, EmpType employementType, SalType salaryType, int salary,
     int vacationDay, String status, List<String> certificationsList) {}
    public boolean addConstraint(int empId, Date date, String timeStart, String timeEnd, boolean doubleShiftAllowed) { return false; }
    public boolean checkAvailabilityAndRole(int empId, Date date, String role) { return false; }
}