package dev.domain;
import java.time.LocalDate;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
        if(!employees.containsKey(employeeID))
            return null;
        else
            return employees.get(employeeID); 
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
    
    public List<Employee> getAllAvailableAndCertified(LocalDate date, ShiftType shiftType, Certification role){
        List<Employee> allActive = getAllActiveEmployees();
        List<Employee> res = new ArrayList<>();
        for(Employee emp: allActive){
            if(emp.isAvailable(date, shiftType) && emp.isCertified(role))
                res.add(emp);
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

    public void update(Employee employee) {
        int id = employee.getID();
        if (employees.containsKey(id)) {
            employees.put(id, employee); 
        }
    }

    public void delete(int employeeID) { 
        if (employees.containsKey(employeeID)) {
            employees.remove(employeeID);
        }
    }

    public boolean doesHRExist() {
        for (Employee e : this.employees.values()) {
            if (e.isHR())
                return true;
        }
        return false;
    }
}