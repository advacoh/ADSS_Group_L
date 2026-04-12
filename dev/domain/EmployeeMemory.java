package dev.domain;
import java.util.Map;

public class EmployeeMemory {
    private Map<Integer, Employee> Employees;

    public boolean save(Employee employee) { 
        int Id = employee.getID();
        if(!Employees.containsKey(id)){
            Employees.put(Id, employee);
            return true;
        }
        return false;
    }
    public Employee get(int employeeID) { 
        return employees.get(employeeId); 
    }

    public boolean delete(int employeeID) { 
        if (employees.containsKey(employeeID)) {
            employees.remove(employeeID);
            return true;
        }
        return false;
 }
}