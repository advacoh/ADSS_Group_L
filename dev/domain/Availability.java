package dev.domain;
import java.util.List;
import java.util.Date;

public class Availability {
    private List<Constraint> constraints;

    public boolean addConstraint(Constraint c) { return false; }
    public boolean removeConstraint(Date date) { return false; }
    public boolean isAvailable(Date date, String shiftType) { return false; }
}