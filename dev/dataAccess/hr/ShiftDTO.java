package dataAccess.hr;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ShiftDTO {
    private final String id;
    private final int branchId;
    private final LocalDate date;
    private final String shiftType;             // ShiftType.name(), e.g. "MORNING"
    private final boolean isActive;
    private final Map<String, Integer> requiredRoles;        // certification name -> count
    private final Map<String, List<Integer>> assignments;    // certification name -> employee ids
    private final List<Integer> overtimeEmployees;

    public ShiftDTO(String id, int branchId, LocalDate date, String shiftType, boolean isActive,
                     Map<String, Integer> requiredRoles, Map<String, List<Integer>> assignments,
                     List<Integer> overtimeEmployees) {
        this.id = id;
        this.branchId = branchId;
        this.date = date;
        this.shiftType = shiftType;
        this.isActive = isActive;
        this.requiredRoles = requiredRoles;
        this.assignments = assignments;
        this.overtimeEmployees = overtimeEmployees;
    }

    public String getId() {
        return id;
    }

    public int getBranchId() {
        return branchId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getShiftType() {
        return shiftType;
    }

    public boolean isActive() {
        return isActive;
    }

    public Map<String, Integer> getRequiredRoles() {
        return requiredRoles;
    }

    public Map<String, List<Integer>> getAssignments() {
        return assignments;
    }

    public List<Integer> getOvertimeEmployees() {
        return overtimeEmployees;
    }
}