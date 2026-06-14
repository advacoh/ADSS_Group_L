package service;

import domain.hr.Certification;
import domain.hr.OverrideRequest;
import domain.hr.RequestStatus;
import domain.hr.ShiftType;

import java.time.LocalDate;

public class OverrideRequestSL {
    private final String id;
    private final int hrId;
    private final int empId;
    private final LocalDate date;
    private final ShiftType shiftType;
    private final Certification role;
    private final RequestStatus status;

    public OverrideRequestSL(OverrideRequest request) {
        this.id = request.getId();
        this.hrId = request.getHrId();
        this.empId = request.getEmpId();
        this.date = request.getDate();
        this.shiftType = request.getShiftType();
        this.role = request.getRole();
        this.status = request.getStatus();
    }

    @Override
    public String toString() {
        return String.format(
                "=== Request Details ===\n" +
                        "Request ID : %s\n" +
                        "Employee   : %d\n" +
                        "Date       : %s\n" +
                        "Shift      : %s\n" +
                        "Role       : %s\n" +
                        "Status     : %s",
                id, empId, date, shiftType.getValue(), role.getValue(), status.getValue()
        );
    }

    /**
     * Single-line representation for lists.
     * Format: Employee [ID] | [Date] | [Shift] | [Role] | [Status]
     */
    public String toShortString() {
        return String.format("Employee %d | %s | %s | %s | %s",
                empId,
                date,
                shiftType.getValue(),
                role.getValue(),
                status.getValue()
        );
    }

    // --- Getters ---
    public String getId() { return id; }
    public int getHrId() { return hrId; }
    public int getEmpId() { return empId; }
    public LocalDate getDate() { return date; }
    public ShiftType getShiftType() { return shiftType; }
    public Certification getRole() { return role; }
    public RequestStatus getStatus() { return status; }
}