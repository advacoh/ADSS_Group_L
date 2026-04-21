package dev.domain;

import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class ShiftMemory {
    private final Map<String, Shift> activeShifts;
    private final Map<String, Shift> pastShifts;

    public ShiftMemory(){
        this.activeShifts = new HashMap<>();
        this.pastShifts = new HashMap<>();
    }

    public ShiftMemory(Map<String, Shift> activeShifts, Map<String, Shift> pastShifts){
        this.activeShifts = activeShifts;
        this.pastShifts = pastShifts;
    }


    private String getShiftKey(LocalDate date, ShiftType type) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE) + "_" + type.name();
    }

    public void save(Shift shift) {
        String key = getShiftKey(shift.getDate(), shift.getType());
        if (activeShifts.containsKey(key)) {
            throw new IllegalArgumentException("Shift already exists for " + shift.getDate() + " " + shift.getType());
        }
        activeShifts.put(key, shift);
    }

    public Shift get(LocalDate date, ShiftType type) {
        String key = getShiftKey(date, type);
        Shift shift = activeShifts.get(key);
        if (shift == null) {
            throw new IllegalArgumentException("Shift not found for " + date + " " + type);
        }
        return shift;
    }

    public List<Shift> getByDate(LocalDate date) {
        List<Shift> result = new ArrayList<>();
        for (Shift s : activeShifts.values()) {
            if (s.getDate().equals(date)) {
                result.add(s);
            }
        }
        return result;
    }

    public void archiveShift(LocalDate day, ShiftType type) {
        String key = getShiftKey(day, type);
        Shift shift = activeShifts.remove(key);
        if (shift == null) {
            throw new IllegalArgumentException("Cannot archive: Shift not found for " + day + " " + type);
        }
        pastShifts.put(key, shift);
    }

    public List<Shift> getAllActiveShifts() {
        return new ArrayList<>(activeShifts.values());
    }

    public Shift getPast(LocalDate date, ShiftType type) {
        String key = getShiftKey(date, type);
        Shift shift = pastShifts.get(key);
        if (shift == null) {
            throw new IllegalArgumentException("No archived shift found for " + date + " " + type);
        }
        return shift;
    }
}