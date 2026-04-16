package dev.domain;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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