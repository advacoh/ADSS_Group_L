package service;
import domain.hr.UserController;
import domain.transportation.TransportController;
import domain.hr.EmployeeController;
import domain.hr.Certification;
import domain.hr.EmpType;
import domain.hr.Employee;
import domain.hr.SalType;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import enums.DeliveryStatus;
import enums.LicenseType;


public class PersonnelService {
    private UserController userController;
    private EmployeeController employeeController;
    private final TransportController transportController;

    public PersonnelService(UserController userController, EmployeeController employeeController, TransportController transportController){
        this.userController = userController;
        this.employeeController = employeeController;
        this.transportController = transportController;
    }
    
    public Response<Void> addEmployee(int activeUserId, int branchId, int newEmpID, String password, String name, int bankAccount, LocalDate startDate, EmpType empType,
                                      SalType salaryType, int salary, int vacationDay, boolean willOvertime, int dayOff, boolean doubleShiftAllowed, Set<Certification> certificationsList, LicenseType licenseType) {
        try {
            employeeController.addEmployee(
                    activeUserId, branchId, newEmpID, password, name, bankAccount, startDate,
                    empType, salaryType, salary, vacationDay, willOvertime,
                    dayOff, doubleShiftAllowed, certificationsList, licenseType);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> deactivateEmployee(int activeUserId, int branchId, int targetEmpId) {
        try {
            employeeController.dismissEmployee(activeUserId, branchId, targetEmpId);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> activateEmployee(int userID, int branchId, int empId, String password) {
        try {
            employeeController.activateEmployee(userID, branchId, empId, password);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> addCertification(int activeUserId, int branchId, int targetEmpId, Certification role) { 
        try {
            this.employeeController.addCertification(activeUserId, branchId, targetEmpId, role);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> removeCertification(int activeUserId, int branchId, int targetEmpId, Certification role) {
        try {
            this.employeeController.removeCertification(activeUserId, branchId, targetEmpId, role);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> updateFinancialDetails(int userID, int branchId, int empId, int newBankAccount, SalType newSalaryType, int newSalary) {
        try {  
            this.employeeController.updateFinancialDetails(userID, branchId, empId, newBankAccount, newSalaryType, newSalary);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> updateEmploymentDetails(int userID, int branchId, int empId, EmpType empType, int vacationDay, LocalDate startDate) {
        try {
            this.employeeController.updateEmploymentDetails(userID, branchId, empId, empType, vacationDay, startDate);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }
    

    public Response<Void> updateEmployeeName(int userID, int branchId, int empId, String newName) {
        try {
            this.employeeController.updateEmployeeName(userID, branchId, empId, newName);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }


    // emp sets for themselves
    public Response<Void> updateEmployeeSettings(int employeeId, int dayOff, boolean willDouble, boolean willOverTime) {
        try {
            this.employeeController.updateEmployeeSettings(employeeId, dayOff, willDouble, willOverTime);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    // HR sets for employee
    public Response<Void> updateEmployeeSettings(int hrUserId, int branchId, int targetEmpId, int dayOff, boolean willDouble, boolean willOverTime) {
        try {
            this.employeeController.updateEmployeeSettings(hrUserId, branchId, targetEmpId, dayOff, willDouble, willOverTime);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    // emp sets for themselves
    public Response<EmployeeSL> getEmployeeDetails(int employeeId) { 
        try {
            Employee emp = this.employeeController.getEmployeeDetails(employeeId);
            return Response.success(mapToSL(employeeId, emp));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

        // HR sets for employee
    public Response<EmployeeSL> getEmployeeDetails(int hrUserId, int branchId, int targetEmpId) { 
        try {
            Employee emp = this.employeeController.getEmployeeDetails(hrUserId, branchId, targetEmpId);
            return Response.success(mapToSL(targetEmpId, emp));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

   public Response<Boolean> branchExists(int branchId) {
        try {
            boolean exists = this.transportController.siteExists(branchId); 
            return Response.success(exists);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

   
    public Response<Void> addTransportManager(int empId, String empName, String password) {
    try {
        this.employeeController.registerTransportManager(empId, empName, password);
        return Response.success(null);
    } catch (IllegalArgumentException | IllegalStateException e) {
        return Response.failure(e.getMessage());
    }
}

    private EmployeeSL mapToSL(int empId, Employee emp) {
        return new EmployeeSL(
            empId, 
            emp.getName(), 
            emp.getBankAccount(), 
            emp.getStartDate(), 
            emp.getEmployementType(), 
            emp.getSalaryType(), 
            emp.getSalary(), 
            emp.getVacation(), 
            emp.willOvertime(), 
            emp.getDayOff(),
            emp.willDouble(), 
            emp.getCertifications(), 
            emp.getStatus()
        );
    }

    public List<DeliverySL> getPendingDeliveries() {
        return transportController.getAllDeliveries().stream()
                .filter(delivery -> delivery.getStatus() == DeliveryStatus.PENDING)
                .map(DeliverySL::new)
                .collect(Collectors.toList());
    }
}

