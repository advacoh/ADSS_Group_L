package domain;

import java.time.LocalDate;
import java.util.*;

public class Shift {
    private final String ID;
    private final LocalDate date;
    private final ShiftType type;

    private final Map<Certification, Integer> requiredRoles;
    private final Map<Certification, List<Integer>> assignments;
    private final List<Integer> overtimeEmployees;

    public Shift(String id, LocalDate date, ShiftType type) {
        this.ID = id;
        this.date = date;
        this.type = type;
        this.requiredRoles = new HashMap<>();
        this.assignments = new HashMap<>();
        this.overtimeEmployees = new ArrayList<>();
        this.requiredRoles.put(Certification.SHIFT_MANAGER, 1);
    }

    public void setRequirement(Certification role, int newCount) {
        if (newCount < 0) {
            throw new IllegalArgumentException("Requirement count cannot be negative");
        }
        if (role == Certification.HR_MANAGER) {
            throw new IllegalArgumentException("HR_MANAGER cannot be set as a shift requirement");
        }
        if (role == Certification.SHIFT_MANAGER && newCount < 1) {
            throw new IllegalArgumentException("There must be at least one SHIFT_MANAGER");
        }
        if (newCount == 0) {
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

    public boolean assignEmployee(Certification role, int employeeId) {
        if (isEmployeeAssigned(employeeId)) {
            boolean isShiftManager = assignments
                    .getOrDefault(Certification.SHIFT_MANAGER, Collections.emptyList())
                    .contains(employeeId);
            if (!isShiftManager) {
                throw new IllegalStateException("Employee " + employeeId + " is already assigned in this shift and is not the shift manager");
            }
            // Count how many roles they already hold
            long rolesHeld = assignments.values().stream()
                    .filter(list -> list.contains(employeeId))
                    .count();
            if (rolesHeld >= 2) {
                throw new IllegalStateException("Shift manager " + employeeId + " already holds 2 roles in this shift");
            }
        }

        if (isRoleFilled(role)) {
            return false;
        }
        assignments.computeIfAbsent(role, k -> new ArrayList<>()).add(employeeId);
        return true;
    }

    public void addOvertimeEmployee(int employeeId) {
        if (!isEmployeeAssigned(employeeId)) {
            throw new IllegalStateException("Employee " + employeeId + " is not assigned to this shift");
        }
        if (overtimeEmployees.contains(employeeId)) {
            throw new IllegalStateException("Employee " + employeeId + " is already marked for overtime");
        }
        overtimeEmployees.add(employeeId);
    }

    public void removeEmployee(Certification role, int employeeId) {
        List<Integer> assigned = assignments.get(role);
        if (assigned == null || !assigned.remove(Integer.valueOf(employeeId))) {
            throw new IllegalArgumentException("Employee " + employeeId + " is not assigned to role " + role);
        }
        if (!isEmployeeAssigned(employeeId)) {
            overtimeEmployees.remove(Integer.valueOf(employeeId));
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

    public boolean isAssignedAsRole(Certification role, int empId) {
        List<Integer> employeesInRole = assignments.get(role);

        if (employeesInRole == null) {
            return false;
        }

        return employeesInRole.contains(empId);
    }

    public int countRoles(int empId) {
        int count = 0;

        for (List<Integer> employees : assignments.values()) {
            if (employees.contains(empId)) {
                count++;
            }
        }

        return count;
    }

    // --- Getters ---
    public String getID() { return ID; }
    public LocalDate getDate() { return date; }
    public ShiftType getType() { return type; }
    public boolean canAcceptOvertime() {
        return this.type == ShiftType.MORNING;
    }
    public Map<Certification, Integer> getRequiredRoles() {
        return Collections.unmodifiableMap(requiredRoles);
    }
    public Map<Certification, List<Integer>> getAssignments() {
        Map<Certification, List<Integer>> unmodifiableAssignments = new HashMap<>();
        assignments.forEach((role, list) ->
                unmodifiableAssignments.put(role, Collections.unmodifiableList(list))
        );
        return Collections.unmodifiableMap(unmodifiableAssignments);
    }
    public List<Integer> getOvertimeEmployees() {
        return Collections.unmodifiableList(overtimeEmployees);
    }
//    public List<Integer> getOvertimeEmployees() { return Collections.unmodifiableList(overtimeEmployees); }
//
//    public Map<Certification, List<Integer>> getAssignments() {
//        return assignments;
//    }
}