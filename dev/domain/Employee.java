package dev.domain;
import java.util.List;
import java.util.Date;
import java.util.*;
import dev.domain.Status;

public class Employee {
    private int ID;
    private String name;
    private int bankAccount;
    private Date startDate;
    private EmpType employementType;
    private SalType salaryType;
    private int salary;
    private WeeklySubmission weeklySubmission;
    private int vacation;
    private Status status;
    private boolean willOvertime;
    private List<Certification> certifications;

    public Employee(int ID, String name, int bankAccount, Date startDate, 
                    EmpType employementType, SalType salaryType, int salary, 
                    int vacation, boolean willOvertime, int dayOff, boolean doubleShiftAllowed, List<Certification> certifications) {
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
        this.weeklySubmission = new WeeklySubmission(dayOff, doubleShiftAllowed); 
    }

    // Getters 
    public int getID() { return ID; }
    public String getName() { return name; }
    public int getBankAccount() { return bankAccount; } 
    public Date getStartDate() { return startDate; } 
    public EmpType getEmployementType() { return employementType; }
    public SalType getSalaryType() { return salaryType; }
    public int getSalary() { return salary; }
    public int getVacation() { return vacation; } 
    public Status getStatus() { return status; }
    public boolean willOvertime(){ return willOvertime; }


    // Setters
    public void setName(String name) { this.name = name; } 
    public void setBankAccount(int bankAccount) { this.bankAccount = bankAccount; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public void setEmployementType(EmpType employementType) { this.employementType = employementType; } 
    public void setSalaryType(SalType salaryType) { this.salaryType = salaryType; }
    public void setSalary(int salary) { this.salary = salary; } 
    public void setVacation(int vacation) { this.vacation = vacation; } 
    public void setStatus(Status status) { this.status = status; }
    public void setWillOverTime(boolean val){ this.willOvertime = val;}

    
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
        sb.append(String.format("%-18s %d\n", "Day Off:", this.weeklySubmission.getDayOff()));
        
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

    public boolean isAvailable(Date date, ShiftType type) { 
        try {
            return this.weeklySubmission.isAvailable(date, type);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isPrefered(Date date, ShiftType type) { 
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
            this.certifications = new ArrayList<>();
        }
        if (!this.certifications.contains(newCert)) {
            this.certifications.add(newCert);
        }
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

    public Map<Date, Map<ShiftType, Boolean>> getWeeklyConstraints() {
        Map<Date, Map<ShiftType, SlotSubmission>> slots = this.weeklySubmission.getSlots();
        Map<Date, Map<ShiftType, Boolean>> res = new HashMap<>();
        for (Date date : slots.keySet()) {
            Map<ShiftType, Boolean> dailyAvailability = new HashMap<>();
            for (ShiftType type : ShiftType.values()) {
                boolean status = isAvailable(date, type);
                dailyAvailability.put(type, status);
            }
            res.put(date, dailyAvailability);
        }
        return res;
    }


    public Map<Date, Map<ShiftType, Boolean>> getWeeklyPreferences() {
        Map<Date, Map<ShiftType, SlotSubmission>> slots = this.weeklySubmission.getSlots();
        Map<Date, Map<ShiftType, Boolean>> res = new HashMap<>();
        for (Date date : slots.keySet()) {
            Map<ShiftType, Boolean> dailyPreferences = new HashMap<>();
            for (ShiftType type : ShiftType.values()) {
                boolean prefStatus = isPrefered(date, type);
                dailyPreferences.put(type, prefStatus);
            }
            res.put(date, dailyPreferences);
        }
        return res;
    }


    public void setWeeklyConstraints(Map<Date, Set<ShiftType>> cons) {
        if (cons == null) {
            throw new IllegalArgumentException("Constraints map cannot be null.");
        }
        boolean submittedOnDayOff = false;        
        this.weeklySubmission.setAllConstraintsFalse();
        this.weeklySubmission.setAllPreferencesFalse();
        Calendar cal = Calendar.getInstance();
        int employeeDayOff = this.weeklySubmission.getDayOff(); 
        for (Map.Entry<Date, Set<ShiftType>> entry : cons.entrySet()) {
            Date date = entry.getKey();
            Set<ShiftType> shiftsForDay = entry.getValue();
            cal.setTime(date);
            int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (currentDayOfWeek == employeeDayOff) {
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

    public void setWeeklyPreferences(Map<Date, Set<ShiftType>> prefs) {
        if (prefs == null) {
            throw new IllegalArgumentException("Preferences map cannot be null.");
        }
        boolean submittedOnDayOff = false;
        boolean prefWithoutConstraint = false;
        this.weeklySubmission.setAllPreferencesFalse();
        Calendar cal = Calendar.getInstance();
        int employeeDayOff = this.weeklySubmission.getDayOff();

        for (Map.Entry<Date, Set<ShiftType>> entry : prefs.entrySet()) {
            Date date = entry.getKey();
            Set<ShiftType> shiftsForDay = entry.getValue();
            cal.setTime(date);
            if (cal.get(Calendar.DAY_OF_WEEK) == employeeDayOff) {
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