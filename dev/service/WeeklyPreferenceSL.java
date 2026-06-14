package service;

import java.time.LocalDate;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import domain.ShiftType;

public class WeeklyPreferenceSL {
        private Map<LocalDate, Map<ShiftType, Boolean>> shifts;

    public WeeklyPreferenceSL(Map<LocalDate, Map<ShiftType, Boolean>> shifts){
        this.shifts = shifts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Weekly Preferences:\n");
        sb.append("=".repeat(40)).append("\n");

        if (shifts == null || shifts.isEmpty()) {
            sb.append("No preferences submitted.\n");
            return sb.toString();
        }

        List<LocalDate> sortedDates = new ArrayList<>(shifts.keySet());
        Collections.sort(sortedDates);

        for (LocalDate date : sortedDates) {
            sb.append(String.format("%-12s %s\n", "Date:", date));
            Map<ShiftType, Boolean> dailyShifts = shifts.get(date);
            for (ShiftType type : ShiftType.values()) {
                Boolean preferred = dailyShifts.get(type);
                String status = (preferred != null && preferred) ? "Preferred" : "No Preference";
                sb.append(String.format("  %-10s %s\n", type + ":", status));
            }
            sb.append("-".repeat(40)).append("\n");
        }
        return sb.toString();
    }

}
