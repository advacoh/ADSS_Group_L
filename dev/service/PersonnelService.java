package dev.service;
import dev.domain.UserController;
import dev.domain.EmployeeController;
import dev.domain.EmpType;
import dev.domain.SalType;
import java.util.List;

public class PersonnelService {
    private UserController userController;
    private EmployeeController employeeController;

    public void addEmployee(String activeUserId, String newEmpID, String name, int bankAccount, Date startDate, EmpType employementType,
     SalType salaryType, int salary, int vacDay, List<String> certificationsList) {}
    public boolean deactivateEmployee(String activeUserId, String targetEmpId) { return false; }
    public String getEmployeeDetails(String activeUserId, String targetEmpId) { return ""; }
    public boolean addCertification(String activeUserId, String targetEmpId, String role) { return false; }
    public boolean requireHRAuthorization(String activeUserId) { return false; }
}