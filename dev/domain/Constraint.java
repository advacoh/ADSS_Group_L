package dev.domain;
public class Constraint {
    private String day;
    private String startTime;
    private String endTime;
    private boolean doubleShiftAllowed;

    public String getDay(){ return this.day;}
    public String getStartTime(){ return this.startTime;}
    public String getEndTime(){ return this.endTime;}

    public int getStartTimeInMinutes(){ return convertTimeToMinutes(this.startTime);}
    public int getEndTimeInMinutes(){ return convertTimeToMinutes(this.endTime);}
    public boolean getDoubleShiftAllowed(){ return this.doubleShiftAllowed;}

    public int convertTimeToMinutes(String timeStr) {
        if (timeStr == null || !timeStr.contains(":")) {
            return -1; 
        }
        try {
            String[] parts = timeStr.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return (hours * 60) + minutes;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public boolean isValidTime() { return false; }
    public boolean overlapsWith(string day, String start, String end) { return false; }
}