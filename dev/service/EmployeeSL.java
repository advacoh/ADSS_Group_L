package dev.service;
import java.util.List;
import dev.domain.Certification;
import dev.domain.EmpType;
import dev.domain.SalType;
import dev.domain.WeeklySubmission;
import dev.domain.Status;
import java.time.LocalDate;

public class EmployeeSL {
    private int ID;
    private String name;
    private int bankAccount;
    private LocalDate startDate;
    private EmpType employementType;
    private SalType salaryType;
    private int salary;
    private WeeklySubmission weeklySubmission;
    private int vacation;
    private Status status;
    private boolean willOvertime;
    private List<Certification> certifications;
    private String dayOff;

    public EmployeeSL(int ID, String name, int bankAccount, LocalDate startDate, 
                    EmpType employementType, SalType salaryType, int salary, 
                    int vacation, boolean willOvertime, String dayOff, boolean doubleShiftAllowed, 
                    List<Certification> certifications) {
        this.ID = ID;
        this.name = name;
        this.bankAccount = bankAccount;
        this.startDate = startDate;
        this.employementType = employementType;
        this.salaryType = salaryType;
        this.salary = salary;
        this.vacation = vacation;
        this.status = Status.ACTIVE;
        this.willOvertime = willOvertime;
        this.certifications = certifications;
        this.dayOff = dayOff;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Employee Profile:\n");
        
        sb.append(String.format("%-18s %d\n", "ID:", ID));
        sb.append(String.format("%-18s %s\n", "Name:", name));
        sb.append(String.format("%-18s %s\n", "Status:", status));
        sb.append(String.format("%-18s %d\n", "Bank Account:", bankAccount));
        
        String dateStr = (startDate != null) ? startDate.toString() : "N/A";
        sb.append(String.format("%-18s %s\n", "Start Date:", dateStr));
        
        sb.append(String.format("%-18s %s\n", "Employment Type:", employementType));
        sb.append(String.format("%-18s %s\n", "Salary Type:", salaryType));
        sb.append(String.format("%-18s %d\n", "Salary:", salary));
        sb.append(String.format("%-18s %d\n", "Vacation Days:", vacation));
        sb.append(String.format("%-18s %s\n", "Day Off:", dayOff));
        
        sb.append(String.format("%-18s ", "Certifications:"));
        if (certifications == null || certifications.isEmpty()) {
            sb.append("None\n");
        } else {
            for (Certification c : certifications) {
                sb.append(c.name()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    
}
