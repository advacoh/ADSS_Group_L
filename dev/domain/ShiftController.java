package dev.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class ShiftController {
    private ShiftMemory shiftMemory;
    private EmployeeMemory employeeMemory;
    private UserController userController;
    private Date deadline;

    public ShiftController(ShiftMemory shiftMemory, EmployeeMemory employeeMemory, UserController userController) {
        this.shiftMemory = shiftMemory;
        this.employeeMemory = employeeMemory;
        this.userController = userController;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        this.deadline = cal.getTime();
    }

    public void assignEmployee(int userId, int empId, Date date, ShiftType type, Certification roleId) {
        try {
            verifyHR(userId);
            updateHistory();
            LocalDate localDate = toLocalDate(date);
            Shift shift = shiftMemory.get(localDate, type);
            Employee emp = employeeMemory.get(empId);
            // ensure available
            if (!emp.isAvailable(date, type)) {
                throw new IllegalStateException("Employee " + empId + " is unavailable for shift " + localDate + " " + type);
            }
            if (!emp.isCertified(roleId)) {
                throw new IllegalStateException("Employee " + empId + " is not certified for role " + roleId);
            }
            if (!emp.willDouble()) {
                List<Shift> shiftsOnDate = shiftMemory.getByDate(localDate);
                for (Shift s : shiftsOnDate) {
                    if (s.isEmployeeAssigned(empId)) {
                        throw new IllegalStateException("Employee " + empId + " does not allow double shifts");
                    }
                }
            }
            if (!shift.addEmployee(roleId, empId)) {
                throw new IllegalStateException("Assignment failed: role " + roleId + " is full or not required in this shift");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("assignEmployee failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("assignEmployee failed: " + e.getMessage());
        }

    }

    public void createShift(int userId, Date day, ShiftType type) {
        try {
            verifyHR(userId);
            LocalDate localDate = toLocalDate(day);
            LocalDate today = LocalDate.now();

            LocalDate nextSunday = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
            LocalDate nextSaturday = nextSunday.plusDays(6);

            if (localDate.isBefore(nextSunday) || localDate.isAfter(nextSaturday)) {
                throw new IllegalArgumentException("Shift date must be within the next week (" + nextSunday + " to " + nextSaturday + ")");
            }

            String id = localDate.toString() + "_" + type.name();
            Shift newShift = new Shift(id, localDate, type);
            shiftMemory.save(newShift);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("createShift failed: " + e.getMessage());
        }
    }

    public void setRequirement(int userId, Date date, ShiftType type, Certification role, int count) {
        try {
            verifyHR(userId);
            updateHistory();
            LocalDate localDate = toLocalDate(date);
            Shift shift = shiftMemory.get(localDate, type);
            shift.setRequirement(role, count);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("setRequirement failed: " + e.getMessage());
        }
    }

    public Shift getPastShift(int userId, Date day, ShiftType type) {
        try {
            verifyHR(userId);
            updateHistory();
            LocalDate localDate = toLocalDate(day);
            return shiftMemory.getPast(localDate, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getPastShift failed: " + e.getMessage());
        }
    }

    public List<Shift> getActiveShifts(int userId) {
        try {
            verifyLogged(userId);
            return shiftMemory.getAllActiveShifts();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getActiveShifts failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("getActiveShifts failed: " + e.getMessage());
        }
    }

    public void setDeadline(int userId, Date date) {
        try {
            verifyHR(userId);
            if (date == null) {
                throw new IllegalArgumentException("Deadline cannot be null");
            }
            if (date.before(new Date())) {
                throw new IllegalArgumentException("Deadline cannot be in the past");
            }
            this.deadline = date;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("setDeadline failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("setDeadline failed: " + e.getMessage());
        }
    }

    private void checkDeadline() {
        if (deadline.before(new Date())) {
            throw new IllegalStateException("The submission deadline has passed");
        }
    }

    private void updateHistory() {
        try {
            LocalDate nextSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
            for (Shift s : shiftMemory.getAllActiveShifts()) {
                if (s.getDate().isBefore(nextSunday)) {
                    shiftMemory.archiveShift(s.getDate(), s.getType());
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("updateHistory failed: " + e.getMessage());
        }
    }
    private LocalDate toLocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private void verifyLogged(int userId) {
        try {
            if (!userController.isLogged(userId)) {
                throw new IllegalStateException("Access denied: User " + userId + " is not logged in");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("verifyLogged failed: " + e.getMessage());
        }
    }

    private void verifyHR(int userId) {
        try {
            verifyLogged(userId);
            Employee userEmp = employeeMemory.get(userId);
            if (userEmp == null || !userEmp.isHR()) {
                throw new IllegalStateException("Access denied: User " + userId + " is not an HR manager");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("verifyHR failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("verifyHR failed: " + e.getMessage());
        }
    }

    public void setWeeklyConstraints(int userId, Map<Date, Set<ShiftType>> cons) {
        try {
            verifyLogged(userId);
            checkDeadline();
            Employee emp = employeeMemory.get(userId);
            if (emp == null) {
                throw new IllegalArgumentException("Employee " + userId + " not found");
            }
            emp.setWeeklyConstraints(cons);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("setWeeklyConstraints failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("setWeeklyConstraints failed: " + e.getMessage());
        }

    }

    public void setWeeklyPreferences(int userId, Map<Date, Set<ShiftType>> prefs) {
        try {
            verifyLogged(userId);
            checkDeadline();
            Employee emp = employeeMemory.get(userId);
            if (emp == null) {
                throw new IllegalArgumentException("Employee " + userId + " not found");
            }
            emp.setWeeklyPreferences(prefs);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("setWeeklyPreferences failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("setWeeklyPreferences failed: " + e.getMessage());
        }
    }

    public Map<Date, Map<ShiftType, Boolean>> getWeeklyConstraints(int userId) {
        try {
            verifyLogged(userId);
            Employee emp = employeeMemory.get(userId);
            if (emp == null) {
                throw new IllegalArgumentException("Employee " + userId + " not found");
            }
            return emp.getWeeklyConstraints();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getWeeklyConstraints failed: " + e.getMessage());
        }
    }
    public Map<Date, Map<ShiftType, Boolean>> getWeeklyPrefrences(int userId) {
        try {
            verifyLogged(userId);
            Employee emp = employeeMemory.get(userId);
            if (emp == null) {
                throw new IllegalArgumentException("Employee " + userId + " not found");
            }
            return emp.getWeeklyPreferences();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getWeeklyPreferences failed: " + e.getMessage());
        }
    }
}