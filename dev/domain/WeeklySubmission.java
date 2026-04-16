package dev.domain;

import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;


public class WeeklySubmission {
    private Date weekOf;
    private int dayOff;
    private boolean doubleShiftAllowed;
    private Map<Date, Map<ShiftType, SlotSubmission>> slots; 

    // Constructor
    public WeeklySubmission(int dayOff, boolean doubleShiftAllowed) {
        this.dayOff = dayOff;
        this.doubleShiftAllowed = doubleShiftAllowed;
        this.slots = new HashMap<>();
        Calendar calendar = Calendar.getInstance();

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.weekOf = calendar.getTime();

        for (int i = 0; i < 7; i++) {
            Date currentDay = calendar.getTime();
            Map<ShiftType, SlotSubmission> dailyShifts = new HashMap<>();
            for (ShiftType shift : ShiftType.values()) {
                dailyShifts.put(shift, new SlotSubmission());
            }
            this.slots.put(currentDay, dailyShifts);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    // Getters
    public Date getWeekOf() {
        return weekOf;
    }

    public int getDayOff() {
    return this.dayOff;
}

    public boolean willDouble(){
        return this.doubleShiftAllowed;
    }

    public Map<Date, Map<ShiftType, SlotSubmission>> getSlots() {
        return slots;
    }

    public boolean isAvailable(Date date, ShiftType shiftType){
        return getSlot(date,shiftType) != null;
    }

    public boolean isPrefered(Date date, ShiftType shiftType){
        return getSlot(date,shiftType).isPreference();
    }

    // Setters

    public void setConstraint(Date date, ShiftType shiftType, boolean value) {
        SlotSubmission slot = getSlot(date, shiftType);
        slot.setConstraint(value);
    }

    public void setPreference(Date date, ShiftType shiftType, boolean value) {
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


// Helpers

private Map<ShiftType, SlotSubmission> getDailyShifts(Date date){
    Map<ShiftType, SlotSubmission> dailyShifts = slots.get(date);
    if (dailyShifts == null) {
        throw new IllegalArgumentException("Date " + date + " is not within the current weekly submission range.");
    }
    return dailyShifts;
}

private SlotSubmission getSlot(Date date, ShiftType shiftType){
    Map<ShiftType, SlotSubmission> dailyShifts = getDailyShifts(date);
    SlotSubmission slot = dailyShifts.get(shiftType);
    if (slot == null) {
        throw new IllegalArgumentException("Shift type " + shiftType + " is not defined for this date.");
    }
    return slot;
}
}