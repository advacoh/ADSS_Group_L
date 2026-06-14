package domain;

import java.time.LocalTime;

public enum ShiftType {
    MORNING("M", LocalTime.of(6, 0), LocalTime.of(14, 0)),
    EVENING("E", LocalTime.of(14, 0), LocalTime.of(22, 0));

    private final String value;
    private final LocalTime startTime;
    private final LocalTime endTime;

    ShiftType(String value, LocalTime startTime, LocalTime endTime) {
        this.value = value;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getValue() {
        return value;
    }

    public static ShiftType fromValue(String input) {
        for (ShiftType type : values()) {
            if (type.value.equalsIgnoreCase(input)) return type;
        }
        return null;
    }

    public boolean contains(LocalTime time) {
        return (!time.isBefore(startTime)) && time.isBefore(endTime);
    }

    public static ShiftType fromTime(LocalTime time) {
        for (ShiftType type : values()) {
            if (type.contains(time)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No shift assigned for the time: " + time);
    }
}