package dataAccess.hr;

import java.time.LocalDate;

public class SlotDTO {
    private final LocalDate date;
    private final String shiftType;
    private final boolean constraint;
    private final boolean preference;

    public SlotDTO(LocalDate date, String shiftType, boolean constraint, boolean preference) {
        this.date = date;
        this.shiftType = shiftType;
        this.constraint = constraint;
        this.preference = preference;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getShiftType() {
        return shiftType;
    }

    // Standard naming convention for boolean getters uses 'is-'
    public boolean isConstraint() {
        return constraint;
    }

    public boolean isPreference() {
        return preference;
    }
}