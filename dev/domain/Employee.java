package domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import domain.Status;

public class Employee {
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
    private Set<Certification> certifications;

    public Employee(int ID, String name, int bankAccount, LocalDate startDate, 
                    EmpType employementType, SalType salaryType, int salary, 
                    int vacation, boolean willOvertime, int dayOff, boolean doubleShiftAllowed, Set<Certification> certifications) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.employementType = Objects.requireNonNull(employementType, "Employment type cannot be null");
        this.salaryType = Objects.requireNonNull(salaryType, "Salary type cannot be null");
        this.certifications = Objects.requireNonNull(certifications, "Certifications list cannot be null");

        if (ID < 100000000 || ID > 999999999) {
            throw new IllegalArgumentException("ID must be a 9-digit positive number.");
        }
        this.ID = ID;
        if (salary <= 0) {
            throw new IllegalArgumentException("Salary must be positive.");
        }
        if (vacation <= 0) {
            throw new IllegalArgumentException("Vacation days must be positive.");
        }
        if (bankAccount <= 0) {
            throw new IllegalArgumentException("Bank account number must be positive.");
        }
        if (dayOff < 1 || dayOff > 7) {
            throw new IllegalArgumentException("Day off must be between 1 (Sunday) and 7 (Saturday).");
        }
        this.bankAccount = bankAccount;
        this.salary = salary;
        this.vacation = vacation;
        this.status = Status.ACTIVE;
        this.willOvertime = willOvertime;
        this.weeklySubmission = new WeeklySubmission(dayOff, doubleShiftAllowed);
    }

    // Getters 
    public int getID() { return ID; }
    public String getName() { return name; }
    public int getBankAccount() { return bankAccount; } 
    public LocalDate getStartDate() { return startDate; } 
    public EmpType getEmployementType() { return employementType; }
    public SalType getSalaryType() { return salaryType; }
    public int getSalary() { return salary; }
    public int getVacation() { return vacation; } 
    public Status getStatus() { return status; }
    public boolean willOvertime(){ return willOvertime; }
    
    public String getDayOff() {
        int dayOff = this.weeklySubmission.getDayOff();
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[dayOff - 1];
    }
    public Set<Certification> getCertifications(){ return certifications;}


    // Setters
    public void setName(String name) { this.name = name; } 
    public void setBankAccount(int bankAccount) { this.bankAccount = bankAccount; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEmployementType(EmpType employementType) { this.employementType = employementType; } 
    public void setSalaryType(SalType salaryType) { this.salaryType = salaryType; }
    public void setSalary(int salary) { this.salary = salary; } 
    public void setVacation(int vacation) { this.vacation = vacation; } 
    public void setStatus(Status status) { this.status = status; }
    public void setWillOverTime(boolean val){ this.willOvertime = val;}
    public void setDayOff(int dayOff){ this.weeklySubmission.setDayOff(dayOff); }

    
    public boolean isAvailable(LocalDate date, ShiftType type) { 
        try {
            return this.weeklySubmission.isAvailable(date, type);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isPrefered(LocalDate date, ShiftType type) { 
        try {
            return this.weeklySubmission.isPrefered(date, type);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean willDouble(){
        return this.weeklySubmission.willDouble();
    }

    public void setWillDouble(boolean willAllow){
        this.weeklySubmission.setDoubleShiftsAllowed(willAllow);
    }

    public boolean isHR() {
        return this.certifications.contains(Certification.HR_MANAGER);
    }

    public void addCertification(Certification newCert) {
        if (this.certifications == null) {
            this.certifications = new HashSet<>();
        }
        this.certifications.add(newCert);
    }

    public boolean removeCertification(Certification certToRemove) {
        if (this.certifications != null) {
            return this.certifications.remove(certToRemove);
        }
        return false;
    }

    public boolean isCertified(Certification cert){
        return this.certifications.contains(cert);
    }

    public Map<LocalDate, Map<ShiftType, Boolean>> getWeeklyConstraints() {
        Map<LocalDate, Map<ShiftType, SlotSubmission>> slots = this.weeklySubmission.getSlots();
        Map<LocalDate, Map<ShiftType, Boolean>> res = new HashMap<>();
        for (LocalDate date : slots.keySet()) {
            Map<ShiftType, Boolean> dailyAvailability = new HashMap<>();
            for (ShiftType type : ShiftType.values()) {
                boolean status = isAvailable(date, type);
                dailyAvailability.put(type, status);
            }
            res.put(date, dailyAvailability);
        }
        return res;
    }


    public Map<LocalDate, Map<ShiftType, Boolean>> getWeeklyPreferences() {
        Map<LocalDate, Map<ShiftType, SlotSubmission>> slots = this.weeklySubmission.getSlots();
        Map<LocalDate, Map<ShiftType, Boolean>> res = new HashMap<>();
        for (LocalDate date : slots.keySet()) {
            Map<ShiftType, Boolean> dailyPreferences = new HashMap<>();
            for (ShiftType type : ShiftType.values()) {
                boolean prefStatus = isPrefered(date, type);
                dailyPreferences.put(type, prefStatus);
            }
            res.put(date, dailyPreferences);
        }
        return res;
    }


    public void setWeeklyConstraints(Map<LocalDate, Set<ShiftType>> cons) {
        if (cons == null) {
            throw new IllegalArgumentException("Constraints map cannot be null.");
        }
        boolean submittedOnDayOff = false;        
        this.weeklySubmission.setAllConstraintsFalse();
        this.weeklySubmission.setAllPreferencesFalse();
        int employeeDayOff = this.weeklySubmission.getDayOff(); 

        for (Map.Entry<LocalDate, Set<ShiftType>> entry : cons.entrySet()) {
            LocalDate date = entry.getKey();
            Set<ShiftType> shiftsForDay = entry.getValue();
            if (date.equals(this.weeklySubmission.getDayOffDate())) {
                submittedOnDayOff = true;
                continue; 
            }
            if (shiftsForDay != null) {
                for (ShiftType shiftType : shiftsForDay) {
                    this.weeklySubmission.setConstraint(date, shiftType, true);
                }
            }
        }
        if (submittedOnDayOff) {
            throw new IllegalArgumentException("All submissions were approved except for those on your predefined day off.");
        }
    }

    public void setWeeklyPreferences(Map<LocalDate, Set<ShiftType>> prefs) {
        if (prefs == null) {
            throw new IllegalArgumentException("Preferences map cannot be null.");
        }
        boolean submittedOnDayOff = false;
        boolean prefWithoutConstraint = false;
        this.weeklySubmission.setAllPreferencesFalse();
        int employeeDayOff = this.weeklySubmission.getDayOff();
        for (Map.Entry<LocalDate, Set<ShiftType>> entry : prefs.entrySet()) {
            LocalDate date = entry.getKey();
            Set<ShiftType> shiftsForDay = entry.getValue();
            if (date.equals(this.weeklySubmission.getDayOffDate())) {
                submittedOnDayOff = true;
                continue; 
            }
            if (shiftsForDay != null) {
                for (ShiftType shiftType : shiftsForDay) {
                    if (!isAvailable(date, shiftType)) {
                        prefWithoutConstraint = true;
                        continue; 
                    }
                    this.weeklySubmission.setPreference(date, shiftType, true);
                }
            }
        }
        handlePreferenceErrors(submittedOnDayOff, prefWithoutConstraint);
    }

    // Private Helper Method
    private void handlePreferenceErrors(boolean dayOffFlag, boolean noConstraintFlag) {
        if (dayOffFlag && noConstraintFlag) {
            throw new IllegalArgumentException("Submissions partially approved: " +
                    "1) Shifts on your day off were ignored. " +
                    "2) Preferences for shifts where you are blocked (Constraint) were ignored.");
        } else if (dayOffFlag) {
            throw new IllegalArgumentException("All preferences approved, except for those on your predefined day off.");
        } else if (noConstraintFlag) {
            throw new IllegalArgumentException("All preferences approved, except for shifts where you didn't previously submitt a constraint.");
        }
    }

}