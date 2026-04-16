package dev.domain;

import java.util.Map;

public class EmployeeMemory {
    private Map<Integer, Employee> Employees;

    public boolean save(Employee employee) { return false; }
    public Employee get(int employeeID) { return null; }
    public boolean delete(int employeeID) { return false; }
}