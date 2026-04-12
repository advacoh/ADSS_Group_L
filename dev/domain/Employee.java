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

    public Employee(int ID, String name, int bankAccount, Date startDate, 
                    EmpType employementType, SalType salaryType, int salary, 
                    int vacation, List<String> certifications) {
        this.ID = ID;
        this.name = name;
        this.bankAccount = bankAccount;
        this.startDate = startDate;
        this.employementType = employementType;
        this.salaryType = salaryType;
        this.salary = salary;
        this.vacation = vacation;
        this.status = "ACTIVE";
        this.certifications = certifications;
        this.availability = new Availability(); 
    }


    public int getID(){ return ID; }
    public int getName(){ return name; }

    public boolean hasRole(String role) { 
        return certifications.contains(role); 
    }

    public boolean isAvailable(Date date, String type) { 
        return this.availability.isAvailable(date, type); 
    }

    public boolean addConstraint(Constraint c) {
        return this.availability.addConstraint(c);
    }
}