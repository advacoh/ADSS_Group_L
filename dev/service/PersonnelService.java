package dev.service;
import dev.domain.UserController;
import dev.domain.EmployeeController;
import dev.domain.EmpType;
import dev.domain.SalType;
import java.util.List;

public class PersonnelService {
    private UserController userController;
    private EmployeeController employeeController;

    public void addEmployee(int activeUserId, int newEmpID, String name, int bankAccount, Date startDate, EmpType employementType,
     SalType salaryType, int salary, int vacDay, List<String> certificationsList) {}
    public boolean deactivateEmployee(int activeUserId, int targetEmpId) { return false; }
    public String getEmployeeDetails(int activeUserId, int targetEmpId) { return ""; }
    public boolean addCertification(int activeUserId, int targetEmpId, String role) { return false; }
    public boolean requireHRAuthorization(int activeUserId) { return false; }
}