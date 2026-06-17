package domain.hr;

import java.time.LocalDate;
import java.util.*;

public class EmployeeMemory {
    private Map<Integer, Employee> employees;

    public EmployeeMemory() {
        this.employees = new HashMap<>();
    }

    public EmployeeMemory(Map<Integer, Employee> employees) {
        this.employees = employees;
    }

    public boolean save(Employee employee) { 
        int id = employee.getID();
        if(!employees.containsKey(id)){
            employees.put(id, employee);
            return true;
        }
        return false;
    }

    public Employee get(int employeeID) { 
        return employees.get(employeeID); 
    }

    public void update(Employee employee) {
        int id = employee.getID();
        if (employees.containsKey(id)) {
            employees.put(id, employee); 
        }
    }

    public void delete(int employeeID) { 
        employees.remove(employeeID);
    }

    public boolean doesHRExist() {
        for (Employee e : this.employees.values()) {
            if (e.isHR())
                return true;
        }
        return false;
    }

    public List<Employee> getAllActiveEmployees(int branchId) {
        List<Employee> activeList = new ArrayList<>();
        for (Employee emp : employees.values()) {
            // Matches if the employee is active AND belongs to the targeted branch OR is a global manager (0)
            if (emp.getStatus() == Status.ACTIVE && (emp.getBranchId() == branchId || emp.getBranchId() == 0)) {
                activeList.add(emp);
            }
        }
        return activeList;
    }

    public List<Employee> getAllActiveEmployees() {
        List<Employee> activeList = new ArrayList<>();
        for (Employee emp : employees.values()) {
            if (emp.getStatus() == Status.ACTIVE) {
                activeList.add(emp);
            }
        }
        return activeList;
    }
    
    // LOCAL BRANCH SHIFTS: Finds employees available for a specific store's shift
    public List<Employee> getAllAvailableAndCertified(int branchId, LocalDate date, ShiftType shiftType, Certification role){
        List<Employee> allActiveInBranch = getAllActiveEmployees(branchId);
        List<Employee> res = new ArrayList<>();
        for(Employee emp : allActiveInBranch){
            if(emp.isAvailable(date, shiftType) && emp.isCertified(role)) {
                res.add(emp);
            }
        }
        return res;
    }

    // GLOBAL AVAILABILITY: Can be used by Logistics if drivers submit weekly constraints globally
    public List<Employee> getAllAvailableAndCertified(LocalDate date, ShiftType shiftType, Certification role) {
        List<Employee> allActive = getAllActiveEmployees();
        List<Employee> res = new ArrayList<>();
        for (Employee emp : allActive) {
            if (emp.isAvailable(date, shiftType) && emp.isCertified(role)) {
                res.add(emp);
            }
        }
        return res;
    }

    public List<Employee> getByrole(Certification role) {
        List<Employee> result = new ArrayList<>();
        for (Employee emp : employees.values()) {
            if (emp.getStatus() == Status.ACTIVE && emp.isCertified(role)) {
                result.add(emp);
            }
        }
        return result;
    }

    // LOCAL ROLE VIEW: Perfect for seeing only the drivers or roles tied to a specific branch
    public List<Employee> getByrole(int branchId, Certification role) {
        List<Employee> result = new ArrayList<>();
        for (Employee emp : employees.values()) {
            if (emp.getStatus() == Status.ACTIVE && emp.isCertified(role) && (emp.getBranchId() == branchId || emp.getBranchId() == 0)) {
                result.add(emp);
            }
        }
        return result;
    }
}