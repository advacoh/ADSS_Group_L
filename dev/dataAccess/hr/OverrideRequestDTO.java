package dataAccess.hr;

import java.time.LocalDate;

public class OverrideRequestDTO {
    private final String id;
    private final int hrId;
    private final int empId;
    private final LocalDate date;
    private final String shiftType;   // "M" or "E"
    private final String role;        // Certification.name() e.g. "CASHIER"
    private final String status;      // RequestStatus.name() e.g. "PENDING"

    public OverrideRequestDTO(String id, int hrId, int empId, LocalDate date,
                              String shiftType, String role, String status) {
        this.id = id;
        this.hrId = hrId;
        this.empId = empId;
        this.date = date;
        this.shiftType = shiftType;
        this.role = role;
        this.status = status;
    }

    public String getId()         { return id; }
    public int getHrId()          { return hrId; }
    public int getEmpId()         { return empId; }
    public LocalDate getDate()    { return date; }
    public String getShiftType()  { return shiftType; }
    public String getRole()       { return role; }
    public String getStatus()     { return status; }
}