package dev.domain;
import java.util.List;
import java.util.Date;

public class Employee {
    private int ID;
    private String name;
    private int bankAccount;
    private Date startDate;
    private EmpType employementType;
    private SalType salaryType;
    private int salary;
    private Availability availability;
    private int vacation;
    private String status;
    private List<String> certifications;

    public boolean hasRole(String role) { return false; }
    public boolean isAvailable(Date date, String type) { return false; }
    public void addConstraint(Constraint c) {}
}