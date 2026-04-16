package dev.domain;
import java.util.List;
import java.util.Date;

public class EmployeeController {
    private UserController userController;
    private EmployeeMemory employeeMemory;

    public EmployeeController(UserController userController) {
        this.userController = userController;
        this.employeeMemory = new EmployeeMemory();
    }

    // Private Helper Methods

    private void verifyLogged(int userId) {
        if (!userController.isLogged(userId)) {
            throw new RuntimeException("Access Denied: User is not logged in.");
        }
    }

    private void verifyHR(int userID) {
        Employee user = employeeMemory.get(userID);
        if (user == null || !user.isHR()) {
            throw new IllegalArgumentException("User not certified for the action");
        }
    }

    private Employee getEmployeeOrThrow(int empId) {
        Employee emp = employeeMemory.get(empId);
        if (emp == null) {
            throw new IllegalArgumentException("Employee not found");
        }
        return emp;
    }

    private void verifyDayOff(int dayOff){
        if( dayOff<1 || dayOff>7){
            throw new IllegalArgumentException("Employee dayOff " + dayOff + " is invalid, should be between 1 and 7.");
        }
    }


    public void addEmployee(int userID, int newEmpID, String password, String name, int bankAccount, Date startDate, EmpType employementType, SalType salaryType, int salary,
    int vacationDay, boolean willOvertime, int dayOff, boolean doubleShiftAllowed, List<Certification> certificationsList) {
        try{
            verifyLogged(userID);
            verifyHR(userID);
            Employee emp = employeeMemory.get(newEmpID); 
            if(emp != null){
                throw new IllegalArgumentException("Employee ID " + newEmpID + " already exists. Use activateEmployee if they are returning.");
            }
            verifyDayOff(dayOff);
            Employee newEmp = new Employee(newEmpID, name, bankAccount, startDate, employementType, salaryType, salary, vacationDay, willOvertime, dayOff, doubleShiftAllowed, certificationsList);
            employeeMemory.save(newEmp);
            this.userController.register(newEmpID, password);
        }
        catch (IllegalArgumentException e) {
            throw e; 
        }    
    }

    public void dismissEmployee(int userID, int empId) {
        try{
            verifyLogged(userID);
            verifyHR(userID);
            Employee emp = getEmployeeOrThrow(empId);
            emp.setStatus(Status.INACTIVE);
            employeeMemory.update(emp);
            this.userController.delete(empId);
        }
        catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    public void activateEmployee(int userID, int empId, String password) {
        try{
            verifyLogged(userID);
            verifyHR(userID);
            Employee emp = getEmployeeOrThrow(empId);
            emp.setStatus(Status.ACTIVE);
            this.userController.register(empId, password);
            employeeMemory.update(emp);
        }
        catch (IllegalArgumentException e) {
            throw e; 
        }
        
    }

    // Update Methods
    public void updateFinancialDetails(int userID, int empId, int newBankAccount, SalType newSalaryType, int newSalary) {
        try{
            verifyLogged(userID);
            verifyHR(userID);
            Employee emp = getEmployeeOrThrow(empId);
            emp.setBankAccount(newBankAccount);
            emp.setSalaryType(newSalaryType);
            emp.setSalary(newSalary);
            employeeMemory.update(emp); 
        }
        catch (IllegalArgumentException e) {
            throw e; 
        }

    }

    public void updateEmploymentDetails(int userID, int empId, EmpType employementType, int vacationDay) {
        try{
            verifyLogged(userID);
            verifyHR(userID);
            Employee emp = getEmployeeOrThrow(empId);
            emp.setEmployementType(employementType);
            emp.setVacation(vacationDay);
            employeeMemory.update(emp); 
        }
        catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    public void updateEmployeeName(int userID, int empId, String newName) {
        try{
            verifyLogged(userID);
            verifyHR(userID);
            Employee emp = getEmployeeOrThrow(empId);
            if (newName == null || newName.trim().isEmpty()) {
                throw new IllegalArgumentException("Update failed: Employee name cannot be empty");
            }
            newName = newName.trim();
            if (newName.length() < 2 || newName.length() > 50) {
                throw new IllegalArgumentException("Name must be between 2 and 50 characters");
            }
            if (!newName.matches("^[a-zA-Z\\s\\-']+$")) {
                throw new IllegalArgumentException("Name contains invalid characters - numbers or symbols");
            }
            emp.setName(newName);
            employeeMemory.update(emp);
        }
        catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    public void addCertification(int userID, int empId, Certification newCertification) {
        try{
            verifyLogged(userID);
            verifyHR(userID);
            Employee emp = getEmployeeOrThrow(empId);
            emp.addCertification(newCertification);
            employeeMemory.update(emp);
        }
        catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    public void removeCertification(int userID, int empId, Certification certToRemove) {
        try{
            verifyLogged(userID);
            verifyHR(userID);
            Employee emp = getEmployeeOrThrow(empId);
            boolean wasRemoved = emp.removeCertification(certToRemove);
            if (!wasRemoved) {
                throw new IllegalArgumentException("Employee does not have the " + certToRemove + " certification");
            }
            employeeMemory.update(emp);
        }
        catch (IllegalArgumentException e) {
            throw e; 
        }
    } 

    public String getEmployeeDetails(int userID, int empId) {
        try{
            verifyLogged(userID);
            verifyHR(userID);
            Employee emp = getEmployeeOrThrow(empId);
            return emp.toString(); 
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }
}