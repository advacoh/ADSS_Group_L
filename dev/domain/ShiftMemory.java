package dev.domain;

import java.util.Map;
import java.util.List;

public class ShiftMemory {
    private Map<String, Shift> activeShifts;
    private Map<String, Shift> pastShifts;

    public boolean save(Shift shift) { return false; }
    public Shift get(String shiftID) { return null; }
    public boolean delete(String shiftID) { return false; }
    public List<Shift> getAllActiveShifts() { return null; }
    public boolean archiveShift(String shiftID) { return false; }
    public List<Shift> getHistory() { return null; }
}