package dev.domain;
import java.util.List;
import java.util.Map;

public class Shift {
    public String ID; 
    private Date date;
    private String type;
    private Map<String, List<Integer>> assignments; // Map<RoleID, List<employeeID>>
    private boolean isAssigned;
}