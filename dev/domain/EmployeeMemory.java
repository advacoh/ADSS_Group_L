package dev.domain;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;


public class EmployeeMemory {
    private Map<Integer, Employee> employees;

    public EmployeeMemory() {
        this.employees = new HashMap<>();
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
    
    public List<Employee> getAllAvailableAndCertified(Date date, ShiftType shiftType, Certification role){
        List<Employee> allActive = getAllActiveEmployees();
        List<Employee> res = new ArrayList<>();
        for(Employee emp: allActive){
            if(emp.isAvailable(date, shiftType) && emp.isCertified(role))
                res.add(emp);
        }
        return res;
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
}