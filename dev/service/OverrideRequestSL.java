package dev.service;

import dev.domain.Certification;
import dev.domain.OverrideRequest;
import dev.domain.RequestStatus;
import dev.domain.ShiftType;

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

    // --- Getters ---
    public String getId() { return id; }
    public int getHrId() { return hrId; }
    public int getEmpId() { return empId; }
    public LocalDate getDate() { return date; }
    public ShiftType getShiftType() { return shiftType; }
    public Certification getRole() { return role; }
    public RequestStatus getStatus() { return status; }
}