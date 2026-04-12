package dev.domain;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;

public class Availability {
    public static final int MORNING_SHIFT_START = 480; // 08:00
    public static final int MORNING_SHIFT_END = 960; // 16:00
    public static final int EVENING_SHIFT_START = 960; // 16:00
    public static final int EVENING_SHIFT_END = 1440; // 00:00
    private List<Constraint> constraints;

    public Availability(){
        this.constraints = new ArrayList<>();
    }

    public boolean addConstraint(Constraint c) { 
        if (c == null) {
            return false;
        }
        return this.constraints.add(c);
    }

    public String safeFormatDate(Date date) {
        if (date == null) {
            return "";
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(date);
        } catch (Exception e) {
            return ""; // If the formatting fails return an empty string.
        }
    }

    public boolean removeConstraint(Date date) {
        String strDate = safeFormatDate(date);
        if (strDate == "") {
            return false;
        }

        Iterator<Constraint> iterator = constraints.iterator();
        while (iterator.hasNext()) {
            Constraint currentConstraint = iterator.next();
            if (currentConstraint.getDay().equals(strDate)) {
                iterator.remove();
                return true;
            }
        } 
        return false; // No constraints were found in the given date.
    }

    public boolean isAvailable(Date date, String shiftType) {
        String strDate = safeFormatDate(date);
        if (strDate.equals("")) {
            return false;
        }

        int targetStart;
        int targetEnd;

        if (shiftType.equals("MORNING")) {
            targetStart = MORNING_SHIFT_START;
            targetEnd = MORNING_SHIFT_END;
        } else if (shiftType.equals("EVENING")) {
            targetStart = EVENING_SHIFT_START;
            targetEnd = EVENING_SHIFT_END;
        } else {
            return false; // Invalid shift type
        }

        for (Constraint currentConstraint : constraints) {
            if (currentConstraint.getDay().equals(strDate)) {
                if (currentConstraint.getStartTimeInMinutes() < targetEnd 
                    && currentConstraint.getEndTimeInMinutes() > targetStart) {
                        return true; 
                }
            }
        }
        return false; 
    }