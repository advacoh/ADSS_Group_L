package dev.domain;

import java.time.LocalDate;
import java.util.*;

public class Shift {
    private final String ID;
    private final LocalDate date;
    private final ShiftType type;

    // Fixed: Using Certification consistently
    private final Map<Certification, Integer> requiredRoles;
    private final Map<Certification, List<Integer>> assignments;

    public Shift(String id, LocalDate date, ShiftType type) {
        this.ID = id;
        this.date = date;
        this.type = type;
        this.requiredRoles = new HashMap<>();
        this.assignments = new HashMap<>();
    }


    public void setRequirement(Certification role, int newCount) {
        if (newCount <= 0) {
            requiredRoles.remove(role);
            assignments.remove(role);
            return;
        }
        requiredRoles.put(role, newCount);
        List<Integer> currentAssignments = assignments.get(role);
        if (currentAssignments != null) {
            while (currentAssignments.size() > newCount) {
                currentAssignments.removeLast();
            }
        }
    }

    public boolean addEmployee(Certification role, int employeeId) {
        if (isRoleFilled(role)) {
            return false;
        }
        assignments.computeIfAbsent(role, k -> new ArrayList<>()).add(employeeId);
        return true;
    }

    public void removeEmployee(Certification role, int employeeId) {
        if (assignments.containsKey(role)) {
            assignments.get(role).remove(Integer.valueOf(employeeId));
        }
    }

    public boolean isRoleFilled(Certification role) {
        if (!requiredRoles.containsKey(role)) return true;

        int required = requiredRoles.get(role);
        int assigned = assignments.getOrDefault(role, Collections.emptyList()).size();
        return assigned >= required;
    }

    public boolean isFullyStaffed() {
        if (requiredRoles.isEmpty()) return false;
        return requiredRoles.keySet().stream().allMatch(this::isRoleFilled);
    }

    public boolean isEmployeeAssigned(int employeeId) {
        for (List<Integer> employeeList : assignments.values()) {
            if (employeeList.contains(employeeId)) {
                return true;
            }
        }
        return false;
    }

    // --- Getters ---

    public String getID() { return ID; }
    public LocalDate getDate() { return date; }
    public ShiftType getType() { return type; }

    public Map<Certification, List<Integer>> getAssignments() {
        return assignments;
    }
}