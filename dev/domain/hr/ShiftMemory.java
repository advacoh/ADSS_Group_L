package domain.hr;

import dataAccess.hr.ShiftDTO;
import dataAccess.hr.ShiftMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ShiftMemory {
    // Nested Structure: Map<BranchId, Map<Date_Type_Key, Shift>>
    private final Map<Integer, Map<String, Shift>> activeShifts;
    private final Map<Integer, Map<String, Shift>> pastShifts;
    private final ShiftMapper mapper;

   public ShiftMemory(ShiftMapper mapper) {
    this.mapper = mapper;
    this.activeShifts = new HashMap<>();
    this.pastShifts = new HashMap<>();
    loadFromDB();
}

    // ── INIT ────────────────────────────────────────────────────────────────

    private void loadFromDB() {
        for (ShiftDTO dto : mapper.getAllActiveShifts()) {
            Shift shift = toShift(dto);
            String key = getShiftKey(shift.getDate(), shift.getType());
            activeShifts.computeIfAbsent(shift.getBranchId(), k -> new HashMap<>()).put(key, shift);
        }
        // past shifts are NOT loaded on startup — they stay in DB only,
        // fetched on demand in getPast()
    }

    // ── PRIVATE HELPERS ─────────────────────────────────────────────────────

    private String getShiftKey(LocalDate date, ShiftType type) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "_" + type.name();
    }

    private Shift toShift(ShiftDTO dto) {
        Shift shift = new Shift(
            dto.getId(),
            dto.getBranchId(),
            dto.getDate(),
            ShiftType.valueOf(dto.getShiftType())
        );
        // restore requiredRoles
        for (Map.Entry<String, Integer> entry : dto.getRequiredRoles().entrySet()) {
            shift.setRequirement(Certification.valueOf(entry.getKey()), entry.getValue());
        }
        // restore assignments
        for (Map.Entry<String, List<Integer>> entry : dto.getAssignments().entrySet()) {
            Certification cert = Certification.valueOf(entry.getKey());
            for (int empId : entry.getValue()) {
                shift.assignEmployee(cert, empId);
            }
        }
        // restore overtime
        for (int empId : dto.getOvertimeEmployees()) {
            shift.addOvertimeEmployee(empId);
        }
        return shift;
    }

    private ShiftDTO fromShift(Shift shift, boolean isActive) {
        // convert requiredRoles
        Map<String, Integer> requiredRoles = new HashMap<>();
        for (Map.Entry<Certification, Integer> entry : shift.getRequiredRoles().entrySet()) {
            requiredRoles.put(entry.getKey().name(), entry.getValue());
        }
        // convert assignments
        Map<String, List<Integer>> assignments = new HashMap<>();
        for (Map.Entry<Certification, List<Integer>> entry : shift.getAssignments().entrySet()) {
            assignments.put(entry.getKey().name(), new ArrayList<>(entry.getValue()));
        }
        return new ShiftDTO(
            shift.getID(),
            shift.getBranchId(),
            shift.getDate(),
            shift.getType().name(),
            isActive,
            requiredRoles,
            assignments,
            new ArrayList<>(shift.getOvertimeEmployees())
        );
    }

    // ── PUBLIC API (same signatures as before) ───────────────────────────────

    public void save(Shift shift) {
        int branchId = shift.getBranchId();
        String key = getShiftKey(shift.getDate(), shift.getType());

        activeShifts.computeIfAbsent(branchId, k -> new HashMap<>());

        if (activeShifts.get(branchId).containsKey(key)) {
            throw new IllegalArgumentException("Shift already exists for branch " + branchId
                    + " on " + shift.getDate() + " (" + shift.getType() + ")");
        }
        activeShifts.get(branchId).put(key, shift);
        mapper.save(fromShift(shift, true));
    }

    public void archiveShift(int branchId, LocalDate day, ShiftType type) {
        String key = getShiftKey(day, type);
        Map<String, Shift> branchActive = activeShifts.get(branchId);

        if (branchActive == null || !branchActive.containsKey(key)) {
            throw new IllegalArgumentException("Cannot archive: Shift not found for branch "
                    + branchId + " on " + day + " " + type);
        }

        Shift shift = branchActive.remove(key);
        pastShifts.computeIfAbsent(branchId, k -> new HashMap<>()).put(key, shift);
        mapper.archive(branchId, day, type.name());
    }

    public Shift get(int branchId, LocalDate date, ShiftType type) {
        String key = getShiftKey(date, type);
        Shift shift = activeShifts.getOrDefault(branchId, Collections.emptyMap()).get(key);
        if (shift == null) {
            throw new IllegalArgumentException("Shift not found for branch "
                    + branchId + " on " + date + " " + type);
        }
        return shift;
    }

    public Shift getPast(int branchId, LocalDate date, ShiftType type) {
        String key = getShiftKey(date, type);

        // check in-memory cache first
        Shift cached = pastShifts.getOrDefault(branchId, Collections.emptyMap()).get(key);
        if (cached != null) return cached;

        // fall back to DB (past shifts are not preloaded)
        ShiftDTO dto = mapper.getPast(branchId, date, type.name());
        if (dto == null) {
            throw new IllegalArgumentException("No archived shift found for branch "
                    + branchId + " on " + date + " " + type);
        }
        Shift shift = toShift(dto);
        pastShifts.computeIfAbsent(branchId, k -> new HashMap<>()).put(key, shift);
        return shift;
    }

    public void update(Shift shift) {
        String key = getShiftKey(shift.getDate(), shift.getType());
        boolean isActive = activeShifts.getOrDefault(shift.getBranchId(), Collections.emptyMap()).containsKey(key);
        mapper.update(fromShift(shift, isActive));
    }

    public List<Shift> getByBranchAndDate(int branchId, LocalDate date) {
        List<Shift> result = new ArrayList<>();
        Map<String, Shift> branchShifts = activeShifts.getOrDefault(branchId, Collections.emptyMap());
        for (Shift s : branchShifts.values()) {
            if (s.getDate().equals(date)) result.add(s);
        }
        return result;
    }

    public List<Shift> getAllActiveShifts(int branchId) {
        return new ArrayList<>(activeShifts.getOrDefault(branchId, Collections.emptyMap()).values());
    }

    public List<Shift> getAllActiveShifts() {
        List<Shift> allActive = new ArrayList<>();
        for (Map<String, Shift> branchMap : activeShifts.values()) {
            allActive.addAll(branchMap.values());
        }
        return allActive;
    }

    public List<Shift> getShiftsByDateAndType(LocalDate date, ShiftType type) {
        List<Shift> result = new ArrayList<>();
        String key = getShiftKey(date, type);
        for (Map<String, Shift> branchMap : activeShifts.values()) {
            if (branchMap.containsKey(key)) result.add(branchMap.get(key));
        }
        return result;
    }
}