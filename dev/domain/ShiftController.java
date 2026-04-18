package dev.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShiftController {
    private ShiftMemory shiftMemory;
    private EmployeeMemory employeeMemory;
    private UserController userController;
    private RequestMemory requestMemory;
    private LocalDate deadline;

    public ShiftController(ShiftMemory shiftMemory, EmployeeMemory employeeMemory, UserController userController, RequestMemory requestMemory) {
        this.shiftMemory = shiftMemory;
        this.employeeMemory = employeeMemory;
        this.userController = userController;
        this.requestMemory = requestMemory;
        this.deadline = LocalDate.now().plusYears(1);
    }

    public void assignEmployee(int userId, int empId, LocalDate date, ShiftType type, Certification roleId, boolean isOvertime) {
        try {
            verifyHR(userId);
            updateHistory();

            Shift shift = shiftMemory.get(date, type);
            Employee emp = employeeMemory.get(empId);
            if (emp == null) {
                throw new IllegalArgumentException("Employee " + empId + " not found");
            }
            if (isOvertime) {
                if (!shift.canAcceptOvertime()) {
                    throw new IllegalArgumentException("Overtime is only allowed for morning shifts");
                }
                if (!emp.willOvertime()) {
                    throw new IllegalStateException("Employee " + empId + " is not willing to do overtime");
                }
            }
            if (!emp.isAvailable(date, type)) {
                throw new IllegalStateException("Employee " + empId + " is unavailable for shift " + date + " " + type);
            }
            if (!emp.isCertified(roleId)) {
                throw new IllegalStateException("Employee " + empId + " is not certified for role " + roleId);
            }

            if (!emp.willDouble()) {
                List<Shift> shiftsOnDate = shiftMemory.getByDate(date);
                for (Shift s : shiftsOnDate) {
                    if (!s.getID().equals(shift.getID()) && s.isEmployeeAssigned(empId)) {
                        throw new IllegalStateException("Employee " + empId + " does not allow double shifts");
                    }
                }
            }
            if (!shift.assignEmployee(roleId, empId)) {
                throw new IllegalStateException("Assignment failed: role " + roleId + " is full or not required in this shift");
            }
            if (isOvertime) {
                shift.addOvertimeEmployee(empId);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("assignEmployee failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("assignEmployee failed: " + e.getMessage());
        }
    }

    public void removeEmployee(int userId, LocalDate date, ShiftType type, Certification role, int employeeId) {
        try {
            verifyHR(userId);

            Shift shift = shiftMemory.get(date, type);
            shift.removeEmployee(role, employeeId);

            updateHistory();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "removeEmployee failed: " + e.getMessage()
            );
        }
    }

    public void createShift(int userId, LocalDate day, ShiftType type) {
        try {
            verifyHR(userId);
            LocalDate today = LocalDate.now();

            LocalDate nextSunday = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
            LocalDate nextSaturday = nextSunday.plusDays(6);

            if (day.isBefore(nextSunday) || day.isAfter(nextSaturday)) {
                throw new IllegalArgumentException("Shift date must be within the next week (" + nextSunday + " to " + nextSaturday + ")");
            }

            String id = day.toString() + "_" + type.name();
            Shift newShift = new Shift(id, day, type);
            shiftMemory.save(newShift);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("createShift failed: " + e.getMessage());
        }
    }

    public void setRequirement(int userId, LocalDate date, ShiftType type, Certification role, int count) {
        try {
            verifyHR(userId);
            updateHistory();
            Shift shift = shiftMemory.get(date, type);
            shift.setRequirement(role, count);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("setRequirement failed: " + e.getMessage());
        }
    }

    public Shift getPastShift(int userId, LocalDate day, ShiftType type) {
        try {
            verifyHR(userId);
            updateHistory();
            return shiftMemory.getPast(day, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getPastShift failed: " + e.getMessage());
        }
    }

    public List<Shift> getWeeklySchedule(int userId) {
        try {
            verifyLogged(userId);
            return shiftMemory.getAllActiveShifts();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getActiveShifts failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("getActiveShifts failed: " + e.getMessage());
        }
    }

    public Shift getShift(int userId, LocalDate date, ShiftType type) {
        try {
            verifyLogged(userId);
            return shiftMemory.get(date, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getShift failed: " + e.getMessage());
        }
    }

    public void setDeadline(int userId, LocalDate date) {
        try {
            verifyHR(userId);
            if (date == null) {
                throw new IllegalArgumentException("Deadline cannot be null");
            }
            if (date.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Deadline cannot be in the past");
            }
            this.deadline = date;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("setDeadline failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("setDeadline failed: " + e.getMessage());
        }
    }

    public void setWeeklyConstraints(int userId, Map<LocalDate, Set<ShiftType>> cons) {
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

    public void setWeeklyPreferences(int userId, Map<LocalDate, Set<ShiftType>> prefs) {
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

    public Map<LocalDate, Map<ShiftType, Boolean>> getWeeklyConstraints(int userId, int empId) {
        try {
            verifyLogged(userId);
            verifyReadAccess(userId, empId);
            Employee emp = employeeMemory.get(empId);
            if (emp == null) {
                throw new IllegalArgumentException("Employee " + empId + " not found");
            }
            return emp.getWeeklyConstraints();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getWeeklyConstraints failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("getWeeklyConstraints failed: " + e.getMessage());
        }
    }

    public Map<LocalDate, Map<ShiftType, Boolean>> getWeeklyPreferences(int userId, int empId) {
        try {
            verifyLogged(userId);
            verifyReadAccess(userId, empId);
            Employee emp = employeeMemory.get(empId);
            if (emp == null) {
                throw new IllegalArgumentException("Employee " + empId + " not found");
            }
            return emp.getWeeklyPreferences();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getWeeklyPreferences failed: " + e.getMessage());
        }  catch (IllegalStateException e) {
            throw new IllegalStateException("getWeeklyPreferences failed: " + e.getMessage());
        }
    }

    public String createOverrideRequest(int userId, int empId, LocalDate date, ShiftType type, Certification role) {
        try {
            verifyHR(userId);
            shiftMemory.get(date, type);
            Employee emp = employeeMemory.get(empId);
            if (emp == null) {
                throw new IllegalArgumentException("Employee " + empId + " not found");
            }
            if (emp.isAvailable(date, type)) {
                throw new IllegalStateException("Employee " + empId + " is already available for this shift, no override needed");
            }
            String requestId = requestMemory.generateId();
            OverrideRequest request = new OverrideRequest(requestId, userId, empId, date, type, role);
            requestMemory.save(request);
            return requestId;

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("createOverrideRequest failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("createOverrideRequest failed: " + e.getMessage());
        }
    }

    public void assignWithOverride(int userId, String requestId) {
        try {
            verifyHR(userId);
            OverrideRequest request = requestMemory.get(requestId);
            if (request.getStatus() != RequestStatus.APPROVED) {
                throw new IllegalStateException("Cannot override: request " + requestId + " has not been approved by the employee");
            }
            Shift shift = shiftMemory.get(request.getDate(), request.getShiftType());
            Employee emp = employeeMemory.get(request.getEmpId());
            if (emp == null) {
                throw new IllegalArgumentException("Employee " + request.getEmpId() + " not found");
            }
            if (!emp.isCertified(request.getRole())) {
                throw new IllegalStateException("Employee " + request.getEmpId() + " is not certified for role " + request.getRole());
            }

            if (!shift.assignEmployee(request.getRole(), request.getEmpId())) {
                throw new IllegalStateException("Assignment failed: role " + request.getRole() + " is full or not required");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("assignWithOverride failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("assignWithOverride failed: " + e.getMessage());
        }
    }

    public void respondToRequest(int userId, String requestId, boolean approved) {
        try {
            verifyLogged(userId);
            OverrideRequest request = requestMemory.get(requestId);

            if (request.getEmpId() != userId) {
                throw new IllegalStateException("Employee " + userId + " is not the target of request " + requestId);
            }
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new IllegalStateException("Request " + requestId + " is no longer pending");
            }

            if (approved) request.approve();
            else          request.reject();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("respondToRequest failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("respondToRequest failed: " + e.getMessage());
        }
    }

    public List<OverrideRequest> viewSentRequests(int userId) {
        try {
            verifyHR(userId);
            return requestMemory.getByHR(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("viewSentRequests failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("viewSentRequests failed: " + e.getMessage());
        }
    }

    public List<OverrideRequest> viewReceivedRequests(int userId) {
        try {
            verifyLogged(userId);
            return requestMemory.getByEmployee(userId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("viewReceivedRequests failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("viewReceivedRequests failed: " + e.getMessage());
        }
    }

    public OverrideRequest viewRequest(int userId, String requestId) {
        try {
            verifyHR(userId);
            OverrideRequest request = requestMemory.get(requestId);
            if (request.getHrId() != userId) {
                throw new IllegalStateException("Request " + requestId + " does not belong to HR " + userId);
            }
            return request;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("viewRequest failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("viewRequest failed: " + e.getMessage());
        }
    }

    private void checkDeadline() {
        if (deadline.isBefore(LocalDate.now())) {
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

    private void verifyReadAccess(int userId, int empId) {
        Employee user = employeeMemory.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User " + userId + " not found");
        }
        if (!user.isHR() && userId != empId) {
            throw new IllegalStateException("Access denied: User " + userId + " can only access their own data");
        }
    }

}