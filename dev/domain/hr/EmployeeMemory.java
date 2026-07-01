package domain.hr;

import dataAccess.hr.*;
import java.time.LocalDate;
import java.util.*;

public class EmployeeMemory {
    private final EmployeeMapper mapper;

    public EmployeeMemory(EmployeeMapper mapper) {
        this.mapper = mapper;
    }

    public boolean save(Employee employee) {
        return mapper.save(toDTO(employee));
    }

    public Employee get(int employeeID) {
        EmployeeDTO dto = mapper.get(employeeID);
        return dto != null ? toDomain(dto) : null;
    }

    public void update(Employee employee) {
        mapper.update(toDTO(employee));
    }

    public void delete(int employeeID) {
        mapper.delete(employeeID);
    }

    public boolean doesHRExist() {
        return mapper.doesHRExist();
    }

    public List<Employee> getAllAvailableAndCertified(int branchId, LocalDate date, ShiftType shiftType, Certification role) {
        return toDomainList(mapper.getAvailableAndCertified(branchId, date, shiftType.name(), role.name()));
    }

    public List<Employee> getAllAvailableAndCertified(LocalDate date, ShiftType shiftType, Certification role) {
        return toDomainList(mapper.getAvailableAndCertified(date, shiftType.name(), role.name()));
    }

    public List<Employee> getByRole(Certification role) {
        return toDomainList(mapper.getByRole(role.name()));
    }

    public List<Employee> getByRole(int branchId, Certification role) {
        return toDomainList(mapper.getByRole(branchId, role.name()));
    }

    // ── CONVERSION ───────────────────────────────────────────────────────────

    private EmployeeDTO toDTO(Employee e) {
        return new EmployeeDTO(
            e.getID(),
            e.getName(),
            e.getBankAccount(),
            e.getStartDate(),
            e.getEmployementType().name(),
            e.getSalaryType().name(),
            e.getSalary(),
            e.getVacation(),
            e.willOvertime(),
            e.getStatus().name(),
            e.getBranchId(),
            certToStrings(e.getCertifications()),
            toSubmissionDTO(e.getWeeklySubmission())
        );
    }

   private Employee toDomain(EmployeeDTO dto) {
    if (dto == null) return null;

    // 1. Extract weekly submission primitives safely
    int dayOff = 1; 
    boolean doubleShiftAllowed = false;
    if (dto.getWeeklySubmission() != null) {
        dayOff = dto.getWeeklySubmission().getDayOff();
        doubleShiftAllowed = dto.getWeeklySubmission().isDoubleShiftAllowed();
    }

    // 2. Call your exact 12-parameter constructor
    Employee emp = new Employee(
        dto.getId(),
        dto.getName(),
        dto.getBankAccount(),
        dto.getStartDate(),
        EmpType.valueOf(dto.getEmploymentType()),
        SalType.valueOf(dto.getSalaryType()),
        dto.getSalary(),
        dto.getVacation(),
        dto.isWillOvertime(),
        dayOff,
        doubleShiftAllowed,
        stringsToCerts(dto.getCertifications())
    );

    emp.setStatus(Status.valueOf(dto.getStatus()));

    // 3. Set the Branch ID using your specific setter rule
    emp.setBranchId(dto.getBranchId());

    // 4. Rehydrate the slots onto the domain's newly created WeeklySubmission object
    if (dto.getWeeklySubmission() != null && dto.getWeeklySubmission().getSlots() != null) {
        WeeklySubmission ws = emp.getWeeklySubmission();
        if (ws != null) {
            // Clear out the constructor's default next-week dates 
            ws.getSlots().clear(); 
            
            for (SlotDTO slot : dto.getWeeklySubmission().getSlots()) {
                SlotSubmission s = new SlotSubmission();
                s.setConstraint(slot.isConstraint());
                s.setPreference(slot.isPreference());
                
                // Directly populate the map structure to avoid the missing setSlot method
                ws.getSlots()
                  .computeIfAbsent(slot.getDate(), k -> new HashMap<>())
                  .put(ShiftType.valueOf(slot.getShiftType()), s);
            }
        }
    }

    return emp;
}
    private WeeklySubmissionDTO toSubmissionDTO(WeeklySubmission ws) {
        if (ws == null) return null;
        List<SlotDTO> slotDTOs = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<ShiftType, SlotSubmission>> dayEntry : ws.getSlots().entrySet()) {
            for (Map.Entry<ShiftType, SlotSubmission> shiftEntry : dayEntry.getValue().entrySet()) {
                slotDTOs.add(new SlotDTO(
                    dayEntry.getKey(),
                    shiftEntry.getKey().name(),
                    shiftEntry.getValue().isConstraint(),
                    shiftEntry.getValue().isPreference()
                ));
            }
        }
        return new WeeklySubmissionDTO(ws.getDayOff(), ws.willDouble(), slotDTOs);
    }

   private WeeklySubmission toSubmissionDomain(WeeklySubmissionDTO dto) {
    if (dto == null) return null;
    
    WeeklySubmission ws = new WeeklySubmission(dto.getDayOff(), dto.isDoubleShiftAllowed());
    
    // Clear out the constructor's default next-week initializations 
    // to ensure we only use the exact dates coming from the database/DTO
    ws.getSlots().clear(); 
    
    for (SlotDTO slot : dto.getSlots()) {
        SlotSubmission s = new SlotSubmission();
        s.setConstraint(slot.isConstraint());
        s.setPreference(slot.isPreference());
        
        // Directly populate the map structure bypassing the missing setSlot method
        ws.getSlots()
          .computeIfAbsent(slot.getDate(), k -> new HashMap<>())
          .put(ShiftType.valueOf(slot.getShiftType()), s);
    }
    
    return ws;
}

    private Set<String> certToStrings(Set<Certification> certs) {
        Set<String> result = new HashSet<>();
        for (Certification c : certs) result.add(c.name());
        return result;
    }

    private Set<Certification> stringsToCerts(Set<String> certs) {
        Set<Certification> result = new HashSet<>();
        for (String c : certs) result.add(Certification.valueOf(c));
        return result;
    }

    private List<Employee> toDomainList(List<EmployeeDTO> dtos) {
        List<Employee> result = new ArrayList<>();
        for (EmployeeDTO dto : dtos) result.add(toDomain(dto));
        return result;
    }
}