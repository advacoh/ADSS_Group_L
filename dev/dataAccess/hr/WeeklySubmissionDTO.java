package dataAccess.hr;

import java.util.Collections;
import java.util.List;

public class WeeklySubmissionDTO {
    private final int dayOff;
    private final boolean doubleShiftAllowed;
    private final List<SlotDTO> slots;

    public WeeklySubmissionDTO(int dayOff, boolean doubleShiftAllowed, List<SlotDTO> slots) {
        this.dayOff = dayOff;
        this.doubleShiftAllowed = doubleShiftAllowed;
        // Defensive copy to guarantee immutability of the List
        this.slots = slots != null ? List.copyOf(slots) : Collections.emptyList();
    }

    public int getDayOff() {
        return dayOff;
    }

    public boolean isDoubleShiftAllowed() {
        return doubleShiftAllowed;
    }

    public List<SlotDTO> getSlots() {
        return slots;
    }
}