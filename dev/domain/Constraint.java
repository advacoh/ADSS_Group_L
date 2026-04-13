package dev.domain;

public class Constraint {
    private String day;
    private String startTime;
    private String endTime;
    private boolean doubleShiftAllowed;

    public boolean isValidTime() { return false; }
    public boolean overlapsWith(String day, String start, String end) { return false; }
}