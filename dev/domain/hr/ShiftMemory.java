package domain.hr;

import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class ShiftMemory {
    // Nested Structure: Map<BranchId, Map<Date_Type_Key, Shift>>
    private final Map<Integer, Map<String, Shift>> activeShifts;
    private final Map<Integer, Map<String, Shift>> pastShifts;

    public ShiftMemory() {
        this.activeShifts = new HashMap<>();
        this.pastShifts = new HashMap<>();
    }

    public ShiftMemory(Map<Integer, Map<String, Shift>> activeShifts, Map<Integer, Map<String, Shift>> pastShifts) {
        this.activeShifts = activeShifts;
        this.pastShifts = pastShifts;
    }

    private String getShiftKey(LocalDate date, ShiftType type) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "_" + type.name();
    }

    public void save(Shift shift) {
        int branchId = shift.getBranchId();
        String key = getShiftKey(shift.getDate(), shift.getType());
        
        activeShifts.computeIfAbsent(branchId, k -> new HashMap<>());
        
        if (activeShifts.get(branchId).containsKey(key)) {
            throw new IllegalArgumentException("Shift already exists for branch " + branchId + " on " + shift.getDate() + " (" + shift.getType() + ")");
        }
        activeShifts.get(branchId).put(key, shift);
    }

    public void archiveShift(int branchId, LocalDate day, ShiftType type) {
        String key = getShiftKey(day, type);
        Map<String, Shift> branchActive = activeShifts.get(branchId);
        
        if (branchActive == null || !branchActive.containsKey(key)) {
            throw new IllegalArgumentException("Cannot archive: Shift not found for branch " + branchId + " on " + day + " " + type);
        }
        
        Shift shift = branchActive.remove(key);
        pastShifts.computeIfAbsent(branchId, k -> new HashMap<>()).put(key, shift);
    }


    public Shift get(int branchId, LocalDate date, ShiftType type) {
        String key = getShiftKey(date, type);
        Shift shift = activeShifts.getOrDefault(branchId, Collections.emptyMap()).get(key);
        if (shift == null) {
            throw new IllegalArgumentException("Shift not found for branch " + branchId + " on " + date + " " + type);
        }
        return shift;
    }

    public Shift getPast(int branchId, LocalDate date, ShiftType type) {
        String key = getShiftKey(date, type);
        Shift shift = pastShifts.getOrDefault(branchId, Collections.emptyMap()).get(key);
        if (shift == null) {
            throw new IllegalArgumentException("No archived shift found for branch " + branchId + " on " + date + " " + type);
        }
        return shift;
    }


    // Localized check for canAssign 
    public List<Shift> getByBranchAndDate(int branchId, LocalDate date) {
        List<Shift> result = new ArrayList<>();
        Map<String, Shift> branchShifts = activeShifts.getOrDefault(branchId, Collections.emptyMap());
        for (Shift s : branchShifts.values()) {
            if (s.getDate().equals(date)) {
                result.add(s);
            }
        }
        return result;
    }

    // For branch weekly schedules
    public List<Shift> getAllActiveShifts(int branchId) {
        return new ArrayList<>(activeShifts.getOrDefault(branchId, Collections.emptyMap()).values());
    }

    // Used by updateHistory loop to check across all system branches
    public List<Shift> getAllActiveShifts() {
        List<Shift> allActive = new ArrayList<>();
        for (Map<String, Shift> branchMap : activeShifts.values()) {
            allActive.addAll(branchMap.values());
        }
        return allActive;
    }

    // Used by verifyDelivery to match a slot globally across destination branches
    public List<Shift> getShiftsByDateAndType(LocalDate date, ShiftType type) {
        List<Shift> result = new ArrayList<>();
        String key = getShiftKey(date, type);
        for (Map<String, Shift> branchMap : activeShifts.values()) {
            if (branchMap.containsKey(key)) {
                result.add(branchMap.get(key));
            }
        }
        return result;
    }
}