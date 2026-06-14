package service;
import domain.hr.UserController;
import domain.hr.EmployeeController;
import domain.hr.Certification;
import domain.hr.EmpType;
import domain.hr.Employee;
import domain.hr.SalType;
import domain.hr.ShiftType;
import java.time.LocalDate;
import java.util.*;


public class WeeklyConstraintsSL {
    private Map<LocalDate, Map<ShiftType, Boolean>> shifts;

    public WeeklyConstraintsSL(Map<LocalDate, Map<ShiftType, Boolean>> shifts){
        this.shifts = shifts;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Weekly Constraints:\n");
        sb.append("=".repeat(40)).append("\n");

        if (shifts == null || shifts.isEmpty()) {
            sb.append("No constraints submitted.\n");
            return sb.toString();
        }

        // Sort dates so they display in chronological order
        List<LocalDate> sortedDates = new ArrayList<>(shifts.keySet());
        Collections.sort(sortedDates);

        for (LocalDate date : sortedDates) {
            sb.append(String.format("%-12s %s\n", "Date:", date));
            Map<ShiftType, Boolean> dailyShifts = shifts.get(date);
            for (ShiftType type : ShiftType.values()) {
                Boolean available = dailyShifts.get(type);
                String status = (available != null && available) ? "Available" : "Unavailable";
                sb.append(String.format("  %-10s %s\n", type + ":", status));
            }
            sb.append("-".repeat(40)).append("\n");
        }
        return sb.toString();
    }
}


