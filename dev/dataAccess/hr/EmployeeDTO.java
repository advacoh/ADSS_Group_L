package dataAccess.hr;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Set;

public class EmployeeDTO {
    private final int id;
    private final String name;
    private final int bankAccount;
    private final LocalDate startDate;
    private final String employmentType;
    private final String salaryType;
    private final int salary;
    private final int vacation;
    private final boolean willOvertime;
    private final String status;
    private final int branchId;
    private final Set<String> certifications;
    private final WeeklySubmissionDTO weeklySubmission;

    public EmployeeDTO(int id, String name, int bankAccount, LocalDate startDate,
                       String employmentType, String salaryType, int salary,
                       int vacation, boolean willOvertime, String status,
                       int branchId, Set<String> certifications, WeeklySubmissionDTO weeklySubmission) {
        this.id = id;
        this.name = name;
        this.bankAccount = bankAccount;
        this.startDate = startDate;
        this.employmentType = employmentType;
        this.salaryType = salaryType;
        this.salary = salary;
        this.vacation = vacation;
        this.willOvertime = willOvertime;
        this.status = status;
        this.branchId = branchId;
        // Defensive copy to guarantee immutability of the Set
        this.certifications = certifications != null ? Set.copyOf(certifications) : Collections.emptySet();
        this.weeklySubmission = weeklySubmission;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBankAccount() {
        return bankAccount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public String getSalaryType() {
        return salaryType;
    }

    public int getSalary() {
        return salary;
    }

    public int getVacation() {
        return vacation;
    }

    public boolean isWillOvertime() {
        return willOvertime;
    }

    public String getStatus() {
        return status;
    }

    public int getBranchId() {
        return branchId;
    }

    public Set<String> getCertifications() {
        return certifications;
    }

    public WeeklySubmissionDTO getWeeklySubmission() {
        return weeklySubmission;
    }
}