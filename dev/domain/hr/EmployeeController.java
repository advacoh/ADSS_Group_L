package domain.hr;
import java.util.*;
import java.time.LocalDate;
import domain.transportation.Driver;
import enums.LicenseType;

public class EmployeeController {
    private UserController userController;
    private EmployeeMemory employeeMemory;

    public EmployeeController(UserController userController, EmployeeMemory employeeMemory) {
        this.userController = userController;
        this.employeeMemory = employeeMemory;
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

    private void verifyEmployeeInBranch(int empId, int branchId) {
        Employee emp = getEmployeeOrThrow(empId);
        if (emp.getBranchId() != branchId) {
            throw new IllegalArgumentException("Target employee does not belong to branch " + branchId);
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

    public void addEmployee(int userID, int branchId, int newEmpID, String password, String name, int bankAccount, LocalDate startDate, EmpType employementType, SalType salaryType, int salary,
                            int vacationDay, boolean willOvertime, int dayOff, boolean doubleShiftAllowed, Set<Certification> certificationsList, LicenseType licenseType) {
        try {
            verifyLogged(userID);
            verifyHR(userID);
            
            Employee emp = employeeMemory.get(newEmpID); 
            if (emp != null) {
                throw new IllegalArgumentException("Employee ID " + newEmpID + " already exists. Use activateEmployee if they are returning.");
            }
            verifyDayOff(dayOff);
            this.userController.validatePassword(password);
            
            Employee newEmp;
            //special case for driver, if the employee has a driver certification and a license type is provided, create a Driver instance
            if (certificationsList.contains(Certification.DRIVER) && licenseType != null) {
                newEmp = new Driver(newEmpID, name, bankAccount, startDate, employementType, salaryType, salary, vacationDay, willOvertime, dayOff, doubleShiftAllowed, certificationsList, licenseType);
            } else {
                newEmp = new Employee(newEmpID, name, bankAccount, startDate, employementType, salaryType, salary, vacationDay, willOvertime, dayOff, doubleShiftAllowed, certificationsList);
            }
            
            newEmp.setBranchId(branchId); 
            
            this.employeeMemory.save(newEmp);
            this.userController.register(newEmpID, password);
        } catch (IllegalArgumentException e) {
            throw e; 
        }    
    }

    public void dismissEmployee(int userID, int branchId, int empId) {
        try {
            verifyLogged(userID);
            verifyHR(userID);
            verifyEmployeeInBranch(empId, branchId);
            
            Employee emp = getEmployeeOrThrow(empId);
            emp.setStatus(Status.INACTIVE);
            employeeMemory.update(emp);
            this.userController.delete(empId);
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    public void activateEmployee(int userID, int branchId, int empId, String password) {
        try {
            verifyLogged(userID);
            verifyHR(userID);
            verifyEmployeeInBranch(empId, branchId);
            
            Employee emp = getEmployeeOrThrow(empId);
            emp.setStatus(Status.ACTIVE);
            this.userController.register(empId, password);
            employeeMemory.update(emp);
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    public void addCertification(int userID, int branchId, int empId, Certification newCertification) {
        try {
            verifyLogged(userID);
            verifyHR(userID);
            verifyEmployeeInBranch(empId, branchId);
            
            Employee emp = getEmployeeOrThrow(empId);
            emp.addCertification(newCertification);
            employeeMemory.update(emp);
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    public void removeCertification(int userID, int branchId, int empId, Certification certToRemove) {
        try {
            verifyLogged(userID);
            verifyHR(userID);
            verifyEmployeeInBranch(empId, branchId);
            
            Employee emp = getEmployeeOrThrow(empId);
            boolean wasRemoved = emp.removeCertification(certToRemove);
            if (!wasRemoved) {
                throw new IllegalArgumentException("Employee does not have the " + certToRemove + " certification");
            }
            employeeMemory.update(emp);
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }


    // Update Methods
    public void updateFinancialDetails(int userID, int branchId, int empId, int newBankAccount, SalType newSalaryType, int newSalary) {
        try {
            verifyLogged(userID);
            verifyHR(userID);
            verifyEmployeeInBranch(empId, branchId);
            
            Employee emp = getEmployeeOrThrow(empId);
            emp.setBankAccount(newBankAccount);
            emp.setSalaryType(newSalaryType);
            emp.setSalary(newSalary);
            employeeMemory.update(emp); 
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    public void updateEmploymentDetails(int userID, int branchId, int empId, EmpType employementType, int vacationDay, LocalDate startDate) {
        try {
            verifyLogged(userID);
            verifyHR(userID);
            verifyEmployeeInBranch(empId, branchId);
            
            Employee emp = getEmployeeOrThrow(empId);
            emp.setEmployementType(employementType);
            emp.setVacation(vacationDay);
            emp.setStartDate(startDate);
            employeeMemory.update(emp); 
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    public void updateEmployeeName(int userID, int branchId, int empId, String newName) {
        try {
            verifyLogged(userID);
            verifyHR(userID);
            verifyEmployeeInBranch(empId, branchId);
            
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
            
            Employee emp = getEmployeeOrThrow(empId);
            emp.setName(newName);
            employeeMemory.update(emp);
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    //emp sets for themselves
    public void updateEmployeeSettings(int employeeId, int dayOff, boolean willDouble, boolean willOverTime) {
        try {
            verifyLogged(employeeId);
            verifyDayOff(dayOff);
            
            Employee emp = getEmployeeOrThrow(employeeId);
            emp.setDayOff(dayOff);
            emp.setWillDouble(willDouble);
            emp.setWillOverTime(willOverTime);
            employeeMemory.update(emp); // Fixed: Changes are committed to storage
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    // HR Manager sets for employee
    public void updateEmployeeSettings(int hrUserId, int branchId, int targetEmpId, int dayOff, boolean willDouble, boolean willOverTime) {
        try {
            verifyLogged(hrUserId);
            verifyHR(hrUserId);
            verifyEmployeeInBranch(targetEmpId, branchId);
            verifyDayOff(dayOff);
            
            Employee emp = getEmployeeOrThrow(targetEmpId);
            emp.setDayOff(dayOff);
            emp.setWillDouble(willDouble);
            emp.setWillOverTime(willOverTime);
            employeeMemory.update(emp); // Fixed: Changes are committed to storage
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    //emp for themselves
    public Employee getEmployeeDetails(int employeeId) {
        try {
            verifyLogged(employeeId);
            return getEmployeeOrThrow(employeeId); 
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

    // HR Manager for employee
    public Employee getEmployeeDetails(int hrUserId, int branchId, int targetEmpId) {
        try {
            verifyLogged(hrUserId);
            verifyHR(hrUserId);
            verifyEmployeeInBranch(targetEmpId, branchId);
            return getEmployeeOrThrow(targetEmpId); 
        } catch (IllegalArgumentException e) {
            throw e; 
        }
    }

     public void registerHR(int userID, String password) {
        try {
            if (!this.employeeMemory.doesHRExist()) {
                Set<Certification> cert = new HashSet<>();
                cert.add(Certification.HR_MANAGER);
                Employee HREmp = new Employee(userID, "System Admin HR", 100, LocalDate.now(), EmpType.FULL_TIME, SalType.GLOBAL, 100, 1, false, 7, false, cert);
                HREmp.setBranchId(0); // System HQ placeholder branch context
                this.userController.register(userID, password);
                this.employeeMemory.save(HREmp);
            } else {
                throw new IllegalArgumentException("HR manager employee already exists in the system.");
            } 
        } catch (IllegalArgumentException e) {
            throw e; 
        }     
    }

    public void registerTransportManager(int empId, String empName, String password) {
    try {
        if (this.employeeMemory.get(empId) == null) {
            Set<Certification> certs = new HashSet<>();
            certs.add(Certification.DELIVERY_MANAGER); 

            Employee transportManager = new Employee(
                empId, empName, 100, LocalDate.now(), EmpType.FULL_TIME, 
                SalType.GLOBAL, 100, 1, false, 7, false, certs
            );

            transportManager.setBranchId(0); 

            this.userController.register(empId, password);
            this.employeeMemory.save(transportManager);
        } else {
            throw new IllegalArgumentException("An employee with ID " + empId + " already exists in the system.");
        } 
    } catch (IllegalArgumentException e) {
        throw e; 
    }     
}

    public boolean isHR(int empId) {
        Employee emp = getEmployeeOrThrow(empId);
        return emp.isHR();
    }

    public boolean isDeliveryManager(int empId) {
        Employee emp = getEmployeeOrThrow(empId);
        return emp.isDeliveryManager();
    }

   
}