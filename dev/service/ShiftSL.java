package service;

import domain.hr.Certification;
import domain.hr.Shift;
import domain.hr.ShiftType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Transfer Object for the Shift domain model.
 * Used to pass shift data across the Service Layer without exposing domain logic.
 */
public class ShiftSL {
    private final String id;
    private final LocalDate date;
    private final ShiftType type;
    private final Map<Certification, Integer> requiredRoles;
    private final Map<Certification, List<Integer>> assignments;
    private final List<Integer> overtimeEmployees;
    private final boolean isFullyStaffed;

    public ShiftSL(Shift shift) {
        this.id = shift.getID();
        this.date = shift.getDate();
        this.type = shift.getType();

        this.requiredRoles = new HashMap<>(shift.getRequiredRoles());
        this.assignments = new HashMap<>(shift.getAssignments());
        this.overtimeEmployees = new ArrayList<>(shift.getOvertimeEmployees());
        this.isFullyStaffed = shift.isFullyStaffed();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("====================================\n");
        sb.append("Shift ID: ").append(id).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("Type: ").append(type).append("\n");
        sb.append("------------------------------------\n");

        // Required Roles
        sb.append("Required Roles:\n");

        if (requiredRoles.isEmpty()) {
            sb.append("  None\n");
        } else {
            for (Map.Entry<Certification, Integer> entry
                    : requiredRoles.entrySet()) {

                sb.append("  ")
                        .append(entry.getKey())
                        .append(" -> Required: ")
                        .append(entry.getValue())
                        .append("\n");
            }
        }

        sb.append("------------------------------------\n");

        // Assignments
        sb.append("Assignments:\n");

        if (assignments.isEmpty()) {
            sb.append("  None\n");
        } else {
            for (Map.Entry<Certification, List<Integer>> entry
                    : assignments.entrySet()) {

                sb.append("  ")
                        .append(entry.getKey())
                        .append(" -> Employees: ");

                if (entry.getValue().isEmpty()) {
                    sb.append("None");
                } else {
                    sb.append(entry.getValue());
                }

                sb.append("\n");
            }
        }

        sb.append("------------------------------------\n");

        // Overtime
        sb.append("Overtime Employees: ");

        if (overtimeEmployees.isEmpty()) {
            sb.append("None\n");
        } else {
            sb.append(overtimeEmployees).append("\n");
        }

        sb.append("------------------------------------\n");

        // Status
        sb.append("Fully Staffed: ")
                .append(isFullyStaffed ? "YES" : "NO")
                .append("\n");

        sb.append("====================================\n");

        return sb.toString();
    }
    public String shortString() {
        return date + " " + type +
                " | Fully Staffed: " +
                (isFullyStaffed ? "YES" : "NO");
    }

    // --- Getters ---
    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public ShiftType getType() { return type; }
    public Map<Certification, Integer> getRequiredRoles() { return requiredRoles; }
    public Map<Certification, List<Integer>> getAssignments() { return assignments; }
    public List<Integer> getOvertimeEmployees() { return overtimeEmployees; }
    public boolean isFullyStaffed() { return isFullyStaffed; }
}