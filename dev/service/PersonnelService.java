package service;
import domain.UserController;
import domain.EmployeeController;
import domain.Certification;
import domain.EmpType;
import domain.Employee;
import domain.SalType;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Set;


public class PersonnelService {
    private UserController userController;
    private EmployeeController employeeController;

    public PersonnelService(UserController userController, EmployeeController employeeController){
        this.userController = userController;
        this.employeeController = employeeController;
    }

    public Response<Void> addEmployee(int activeUserId, int newEmpID, String password, String name, int bankAccount, LocalDate startDate, EmpType empType,
        SalType salaryType, int salary, int vacationDay, boolean willOvertime, int dayOff, boolean doubleShiftAllowed, Set<Certification> certificationsList) {
        try{
            employeeController.addEmployee(
                activeUserId, newEmpID, password, name, bankAccount, startDate,
                empType, salaryType, salary, vacationDay, willOvertime,
                dayOff, doubleShiftAllowed, certificationsList);
                return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> deactivateEmployee(int activeUserId, int targetEmpId) {
        try {
            employeeController.dismissEmployee(activeUserId, targetEmpId);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> activateEmployee(int userID, int empId, String password){
        try{
            employeeController.activateEmployee(userID, empId, password);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> addCertification(int activeUserId, int targetEmpId, Certification role) { 
        try{
            this.employeeController.addCertification(activeUserId, targetEmpId, role);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
     }

    public Response<Void> removeCertification(int activeUserId, int targetEmpId, Certification role){
        try{
            this.employeeController.removeCertification(activeUserId, targetEmpId, role);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> updateFinancialDetails(int userID, int empId, int newBankAccount, SalType newSalaryType, int newSalary){
        try{  
            this.employeeController.updateFinancialDetails(userID, empId, newBankAccount, newSalaryType, newSalary);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> updateEmploymentDetails(int userID, int empId, EmpType empType, int vacationDay, LocalDate startDate){
        try{
            this.employeeController.updateEmploymentDetails(userID, empId, empType, vacationDay, startDate);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }
    

    public Response<Void> updateEmployeeName(int userID, int empId, String newName){
        try{
            this.employeeController.updateEmployeeName(userID, empId, newName);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }


    public Response<Void> updateEmployeeSettings(int userID, int empId, int dayOff, boolean willDouble, boolean willOverTime) {
        try {
            this.employeeController.updateEmployeeSettings(userID, empId, dayOff, willDouble, willOverTime);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<EmployeeSL> getEmployeeDetails(int activeUserId, int targetEmpId) { 
        try{
            Employee emp = this.employeeController.getEmployeeDetails(activeUserId,targetEmpId);
            EmployeeSL empSL = new EmployeeSL(targetEmpId, emp.getName(), emp.getBankAccount(), emp.getStartDate(), 
            emp.getEmployementType(), emp.getSalaryType(), emp.getSalary(), emp.getVacation(), emp.willOvertime(), emp.getDayOff(),
            emp.willDouble(), emp.getCertifications(), emp.getStatus());
            return Response.success(empSL);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }
}

