package domain;

import java.time.LocalDate;

public class OverrideRequest {
    private final String id;
    private final int hrId;
    private final int empId;
    private final LocalDate date;
    private final ShiftType shiftType;
    private final Certification role;
    private RequestStatus status;

    public OverrideRequest(String id, int hrId, int empId, LocalDate date, ShiftType shiftType, Certification role) {
        this.id = id;
        this.hrId = hrId;
        this.empId = empId;
        this.date = date;
        this.shiftType = shiftType;
        this.role = role;
        this.status = RequestStatus.PENDING;
    }
    public void approve() { this.status = RequestStatus.APPROVED; }
    public void reject()  { this.status = RequestStatus.REJECTED; }

    public String getId()           { return id; }
    public int getHrId()            { return hrId; }
    public int getEmpId()           { return empId; }
    public LocalDate getDate()      { return date; }
    public ShiftType getShiftType() { return shiftType; }
    public Certification getRole()  { return role; }
    public RequestStatus getStatus(){ return status; }
}
