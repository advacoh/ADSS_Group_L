package dev.domain;
public enum ShiftType {
    MORNING("M"),
    EVENING("E");

    private final String value;

    ShiftType(String value) {
        this.value = value;
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
}