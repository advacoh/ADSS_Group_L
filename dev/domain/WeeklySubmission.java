package dev.domain;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.DayOfWeek;


public class WeeklySubmission {
    private LocalDate weekOf;
    private int dayOff;
    private boolean doubleShiftAllowed;
    private Map<LocalDate, Map<ShiftType, SlotSubmission>> slots; 

    public WeeklySubmission(int dayOff, boolean doubleShiftAllowed) {
        this.dayOff = dayOff;
        this.doubleShiftAllowed = doubleShiftAllowed;
        this.slots = new HashMap<>();

        LocalDate current = LocalDate.now();
        while (current.getDayOfWeek() != DayOfWeek.SUNDAY) {
            current = current.plusDays(1);
        }
        this.weekOf = current;

        for (int i = 0; i < 7; i++) {
            Map<ShiftType, SlotSubmission> dailyShifts = new HashMap<>();
            for (ShiftType shift : ShiftType.values()) {
                dailyShifts.put(shift, new SlotSubmission());
            }
            this.slots.put(current, dailyShifts);
            current = current.plusDays(1);
        }
    }

    // Getters
    public LocalDate getWeekOf() {
        return weekOf;
    }

    public int getDayOff() {
    return this.dayOff;
}

    public boolean willDouble(){
        return this.doubleShiftAllowed;
    }

    public Map<LocalDate, Map<ShiftType, SlotSubmission>> getSlots() {
        return slots;
    }

    public boolean isAvailable(LocalDate date, ShiftType shiftType){
        return getSlot(date, shiftType).isConstraint(); 
    }

    public boolean isPrefered(LocalDate date, ShiftType shiftType){
        return getSlot(date,shiftType).isPreference();
    }

    public LocalDate getDayOffDate() {
        LocalDate sunday = this.weekOf; // weekOf is already the Sunday of the week
        return sunday.plusDays(this.dayOff - 1);
    }

    // Setters

    public void setConstraint(LocalDate date, ShiftType shiftType, boolean value) {
        SlotSubmission slot = getSlot(date, shiftType);
        slot.setConstraint(value);
    }

    public void setPreference(LocalDate date, ShiftType shiftType, boolean value) {
        SlotSubmission slot = getSlot(date, shiftType);
        slot.setPreference(value);
    }

    public void setDoubleShiftsAllowed(boolean value){
        this.doubleShiftAllowed = value;
    }

    public void setAllConstraintsFalse() {
        for (Map<ShiftType, SlotSubmission> dailyShifts : slots.values()) {
            for (SlotSubmission slot : dailyShifts.values()) {
                slot.setConstraint(false);
            }
        }
    }

    public void setAllPreferencesFalse() {
        for (Map<ShiftType, SlotSubmission> dailyShifts : slots.values()) {
            for (SlotSubmission slot : dailyShifts.values()) {
                slot.setPreference(false);
            }
        }
    }

    public void setDayOff(int dayOff){
        if(dayOff < 1 || dayOff > 7){
            throw new IllegalArgumentException("The day off value that was submitted " + dayOff + " is invalid (1-7).");
        }
        this.dayOff = dayOff;
    }


// Helpers

private Map<ShiftType, SlotSubmission> getDailyShifts(LocalDate date){
    Map<ShiftType, SlotSubmission> dailyShifts = slots.get(date);
    if (dailyShifts == null) {
        throw new IllegalArgumentException("Date " + date + " is not within the current weekly submission range.");
    }
    return dailyShifts;
}

private SlotSubmission getSlot(LocalDate date, ShiftType shiftType){
    Map<ShiftType, SlotSubmission> dailyShifts = getDailyShifts(date);
    SlotSubmission slot = dailyShifts.get(shiftType);
    if (slot == null) {
        throw new IllegalArgumentException("Shift type " + shiftType + " is not defined for this date.");
    }
    return slot;
}
}
