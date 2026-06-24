package domain.hr;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


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
    }

    // setters

   public void assignEmployee(int userId, int branchId, int empId, LocalDate date, ShiftType type, Certification role, boolean isOvertime) {
        try {
            verifyHR(userId);
            updateHistory();
            
            Shift shift = shiftMemory.get(branchId, date, type);
            Employee emp = employeeMemory.get(empId);
            
            if (emp == null) throw new IllegalArgumentException("Employee " + empId + " not found");
            
            verifyEmployeeBranch(emp, branchId);
            
            if (!canAssign(emp, shift, role, date, type, isOvertime)) {
                throw new IllegalStateException("Employee " + empId + " cannot be assigned to this shift");
            }
            if (!shift.assignEmployee(role, empId)) {
                throw new IllegalStateException("Assignment failed: role full or not required");
            }
            if (isOvertime) {
                shift.addOvertimeEmployee(empId);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("assignEmployee failed: " + e.getMessage());
        }
    }
    public void removeEmployee(int userId, int branchId, LocalDate date, ShiftType type, Certification role, int employeeId) {
        try {
            verifyHR(userId);

            Employee emp = employeeMemory.get(employeeId);
            if (emp == null) throw new IllegalArgumentException("Employee " + employeeId + " not found");
            
            verifyEmployeeBranch(emp, branchId);

            Shift shift = shiftMemory.get(branchId, date, type);
            shift.removeEmployee(role, employeeId);

            updateHistory();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("removeEmployee failed: " + e.getMessage());
        }
    }

   public void createShift(int userId, int branchId, LocalDate day, ShiftType type) {
        try {
            verifyHR(userId);
            LocalDate today = LocalDate.now();

            LocalDate nextSunday = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
            LocalDate nextSaturday = nextSunday.plusDays(6);

            if (day.isBefore(nextSunday) || day.isAfter(nextSaturday)) {
                throw new IllegalArgumentException("Shift date must be within the next week (" + nextSunday + " to " + nextSaturday + ")");
            }

            String id = branchId + "_" + day.toString() + "_" + type.name();
            Shift newShift = new Shift(id, branchId, day, type);
            shiftMemory.save(newShift);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("createShift failed: " + e.getMessage());
        }
    }
    
    public void setRequirement(int userId, int branchId, LocalDate date, ShiftType type, Certification role, int count) {
        try {
            verifyHR(userId);
            updateHistory();
            Shift shift = shiftMemory.get(branchId, date, type);
            shift.setRequirement(role, count);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("setRequirement failed: " + e.getMessage());
        }
    }

    // getters

   public Shift getPastShift(int userId, int branchId, LocalDate day, ShiftType type) {
        try {
            verifyHR(userId);
            updateHistory();
            return shiftMemory.getPast(branchId, day, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getPastShift failed: " + e.getMessage());
        }
    }

    public List<Shift> getWeeklySchedule(int userId, int branchId) {
        try {
            verifyHR(userId);
            updateHistory();
            return shiftMemory.getAllActiveShifts(branchId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("getWeeklySchedule failed: " + e.getMessage());
        }
    }

    public Shift getShift(int userId, int branchId, LocalDate date, ShiftType type) {
        try {
            verifyHR(userId);
            return shiftMemory.get(branchId, date, type);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getShift failed: " + e.getMessage());
        }
    }

  public List<Employee> getAvailableForRole(int userId, int branchId, LocalDate date, ShiftType type, Certification role) {
        try {
            verifyHR(userId);
            Shift shift = shiftMemory.get(branchId, date, type);
            List<Employee> candidates = employeeMemory.getAllAvailableAndCertified(branchId, date, type, role);
            List<Employee> result = new ArrayList<>();
            for (Employee emp : candidates) {
                if (canAssign(emp, shift, role, date, type, false)) {
                    result.add(emp);
                }
            }
            return result;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("getAvailableForRole: " + e.getMessage());
        }
    }
    public List<Employee> getAllWithCertification(int userId, int branchId, Certification role) {
        try {
            verifyHR(userId);
            return employeeMemory.getByRole(branchId, role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("getAllWithCertification failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            throw new IllegalStateException("getAllWithCertification failed: " + e.getMessage());
        }
    }

    // constraints && prefrences logic

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
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("setDeadline failed: " + e.getMessage());
        }
    }

    public void setWeeklyConstraints(int userId, Map<LocalDate, Set<ShiftType>> cons) {
        try {
            verifyLogged(userId);
            Employee emp = employeeMemory.get(userId);
            if (emp == null) {
                throw new IllegalArgumentException("Employee " + userId + " not found");
            }
            checkDeadline();
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

    // Called by employee themselves
    public Map<LocalDate, Map<ShiftType, Boolean>> getWeeklyConstraints(int employeeId) {
        try {
            verifyLogged(employeeId);
            Employee emp = employeeMemory.get(employeeId);
            if (emp == null) throw new IllegalArgumentException("Employee " + employeeId + " not found");
            return emp.getWeeklyConstraints();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("getWeeklyConstraints failed: " + e.getMessage());
        }
    }

    // Called by HR for a branch employee
    public Map<LocalDate, Map<ShiftType, Boolean>> getWeeklyConstraints(int hrUserId, int branchId, int targetEmpId) {
        try {
            verifyHR(hrUserId);
            Employee emp = employeeMemory.get(targetEmpId);
            if (emp == null) throw new IllegalArgumentException("Employee " + targetEmpId + " not found");
            verifyEmployeeBranch(emp, branchId);
            return emp.getWeeklyConstraints();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("getWeeklyConstraints failed: " + e.getMessage());
        }
    }

    // Called by employee themselves
    public Map<LocalDate, Map<ShiftType, Boolean>> getWeeklyPreferences(int employeeId) {
        try {
            verifyLogged(employeeId);
            Employee emp = employeeMemory.get(employeeId);
            if (emp == null) throw new IllegalArgumentException("Employee " + employeeId + " not found");
            return emp.getWeeklyPreferences();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("getWeeklyPreferences failed: " + e.getMessage());
        }
    }

    // Called by HR for a branch employee
    public Map<LocalDate, Map<ShiftType, Boolean>> getWeeklyPreferences(int hrUserId, int branchId, int targetEmpId) {
        try {
            verifyHR(hrUserId);
            Employee emp = employeeMemory.get(targetEmpId);
            if (emp == null) throw new IllegalArgumentException("Employee " + targetEmpId + " not found");
            verifyEmployeeBranch(emp, branchId);
            return emp.getWeeklyPreferences();
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("getWeeklyPreferences failed: " + e.getMessage());
        }
    }


    // override logic

    public String createOverrideRequest(int hrUserId, int branchId, int targetEmpId, LocalDate date, ShiftType type, Certification role) {
        try {
            verifyHR(hrUserId);
            Employee emp = employeeMemory.get(targetEmpId);
            if (emp == null) throw new IllegalArgumentException("Employee " + targetEmpId + " not found");
            verifyEmployeeBranch(emp, branchId);

            shiftMemory.get(branchId, date, type); 
            if (emp.isAvailable(date, type)) {
                throw new IllegalStateException("Employee " + targetEmpId + " is already available for this shift, no override needed");
            }
            
            String requestId = requestMemory.generateId();
            OverrideRequest request = new OverrideRequest(requestId, hrUserId, targetEmpId, date, type, role);
            requestMemory.save(request);
            return requestId;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("createOverrideRequest failed: " + e.getMessage());
        }
    }

    public void assignWithOverride(int hrUserId, int branchId, String requestId) {
        try {
            verifyHR(hrUserId);
            OverrideRequest request = requestMemory.get(requestId);
            if (request.getStatus() != RequestStatus.APPROVED) {
                throw new IllegalStateException("Cannot override: request " + requestId + " has not been approved");
            }
            
            Employee emp = employeeMemory.get(request.getEmpId());
            if (emp == null) throw new IllegalArgumentException("Employee " + request.getEmpId() + " not found");
            verifyEmployeeBranch(emp, branchId);
            
            Shift shift = shiftMemory.get(branchId, request.getDate(), request.getShiftType());
            if (!emp.isCertified(request.getRole())) {
                throw new IllegalStateException("Employee " + request.getEmpId() + " is not certified for role " + request.getRole());
            }

            if (!shift.assignEmployee(request.getRole(), request.getEmpId())) {
                throw new IllegalStateException("Assignment failed: role full or not required");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("assignWithOverride failed: " + e.getMessage());
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

    public List<OverrideRequest> viewSentRequests(int hrUserId, int branchId) {
        try {
            verifyHR(hrUserId);
           
            List<OverrideRequest> hrRequests = requestMemory.getByHR(hrUserId);
            List<OverrideRequest> filteredRequests = hrRequests.stream()
            .filter(req -> {
                Employee emp = employeeMemory.get(req.getEmpId()); 
            return emp.getBranchId() == branchId;
            }).toList();
        return filteredRequests;

        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("viewSentRequests failed: " + e.getMessage());
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
            verifyLogged(userId);
            OverrideRequest request = requestMemory.get(requestId);
            if (request.getHrId() != userId && request.getEmpId() != userId) {
                throw new IllegalStateException("Access denied to request " + requestId);
            }
            return request;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new IllegalArgumentException("viewRequest failed: " + e.getMessage());
        }
    }

    // helpers

    private boolean canAssign(Employee emp, Shift shift, Certification role, LocalDate date, ShiftType type, boolean isOvertime) {
    if (!emp.isAvailable(date, type)) return false;
    if (!emp.isCertified(role)) return false;
    
    // Overtime capacity check is already localized to this specific shift/branch object
    if (isOvertime) {
        if (!shift.canAcceptOvertime() || !emp.willOvertime()) return false;
    }
    
    // FIXED: Only query shifts for the employee's specific branch
    if (!emp.willDouble()) {
        List<Shift> branchShiftsOnDate = shiftMemory.getByBranchAndDate(shift.getBranchId(), date);
        for (Shift s : branchShiftsOnDate) {
            if (!s.getID().equals(shift.getID()) && s.isEmployeeAssigned(emp.getID())) {
                return false; // Already working another shift at this branch today
            }
        }
    }
    
    if (shift.isEmployeeAssigned(emp.getID())) {
        boolean isShiftManager = shift.isAssignedAsRole(Certification.SHIFT_MANAGER, emp.getID());
        if (!isShiftManager) return false;
        if (shift.countRoles(emp.getID()) >= 2) return false;
    }
    return true;
}
    private void checkDeadline() {
        if (deadline != null && deadline.isBefore(LocalDate.now())) {
            throw new IllegalStateException("The submission deadline has passed");
        }
    }

    private void updateHistory() {
    try {
        LocalDate nextSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        for (Shift s : shiftMemory.getAllActiveShifts()) {
            if (s.getDate().isBefore(nextSunday)) {
                shiftMemory.archiveShift(s.getBranchId(), s.getDate(), s.getType());
            }
        }
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("updateHistory failed: " + e.getMessage());
    }
}

    private void verifyLogged(int userId) {
        if (!userController.isLogged(userId)) {
            throw new IllegalStateException("Access denied: User " + userId + " is not logged in");
        }
    }

    private void verifyHR(int userId) {
        verifyLogged(userId);
        Employee userEmp = employeeMemory.get(userId);
        if (userEmp == null || !userEmp.isHR()) {
            throw new IllegalStateException("Access denied: User " + userId + " is not an HR manager");
        }
    }

    private void verifyEmployeeBranch(Employee emp, int branchId) {
        if (emp.getBranchId() != branchId) {
            throw new IllegalStateException("Action denied: Employee " + emp.getID() + " belongs to branch " + emp.getBranchId() + ", not branch " + branchId);
        }
    }

    public void verifyDelivery(List<Integer> branchIds, LocalDate date, LocalTime time, int driverId) {
        ShiftType expectedShiftType;
        try {
            expectedShiftType = ShiftType.fromTime(time);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Delivery time is outside of operating shift hours.", e);
        }
        
        for (int branchId : branchIds) {
            Shift shift = shiftMemory.get(branchId, date, expectedShiftType); 
            if (shift == null) {
                throw new IllegalStateException("No shift scheduled for branch " + branchId + " on " + date);
            }
            
            List<Integer> warehouseStaff = shift.getAssignments().getOrDefault(Certification.WAREHOUSE, Collections.emptyList());
            if (warehouseStaff.isEmpty()) {
                throw new IllegalStateException("Delivery rejected: No warehouse employee assigned at branch " + branchId);
            }
        }

        boolean driverIsScheduledAnywhere = false;
        List<Shift> shiftsInTimeSlot = shiftMemory.getShiftsByDateAndType(date, expectedShiftType);
        
        for (Shift s : shiftsInTimeSlot) {
            if (s.isAssignedAsRole(Certification.DRIVER, driverId)) {
                driverIsScheduledAnywhere = true;
                break;
            }
        }
        
        if (!driverIsScheduledAnywhere) {
            throw new IllegalStateException("Delivery rejected: Driver ID " + driverId + " is not assigned in this shift window.");
        }
    }
  
}

