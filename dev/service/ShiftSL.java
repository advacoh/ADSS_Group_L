package dev.service;

import dev.domain.Certification;
import dev.domain.Shift;
import dev.domain.ShiftType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for the Shift domain model.
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

    // --- Getters ---
    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public ShiftType getType() { return type; }
    public Map<Certification, Integer> getRequiredRoles() { return requiredRoles; }
    public Map<Certification, List<Integer>> getAssignments() { return assignments; }
    public List<Integer> getOvertimeEmployees() { return overtimeEmployees; }
    public boolean isFullyStaffed() { return isFullyStaffed; }
}