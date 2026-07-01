package service;

import domain.hr.*;
import domain.transportation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SchedulingService {

    private final ShiftController shiftController;

    public SchedulingService(ShiftController shiftController) {
        this.shiftController = shiftController;
    }

    // --- Shift Management ---

    public Response<Void> createShift(int userId, int branchId, LocalDate date, ShiftType type) {
        try {
            shiftController.createShift(userId, branchId, date, type);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> setRequirement(int userId, int branchId, LocalDate date, ShiftType type, Certification role, int count) {
        try {
            shiftController.setRequirement(userId, branchId, date, type, role, count);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> assignEmployee(int userId, int branchId, int targetEmpId, LocalDate date, ShiftType type, Certification role, boolean isOvertime) {
        try {
            shiftController.assignEmployee(userId, branchId, targetEmpId, date, type, role, isOvertime);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> removeEmployee(int userId, int branchId, LocalDate date, ShiftType type, Certification role, int targetEmpId) {
        try {
            shiftController.removeEmployee(userId, branchId, date, type, role, targetEmpId);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    // --- Queries ---

    public Response<ShiftSL> getShift(int userId, int branchId, LocalDate date, ShiftType type) {
        try {
            Shift shift = shiftController.getShift(userId, branchId, date, type);
            return Response.success(new ShiftSL(shift));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<List<ShiftSL>> getWeeklySchedule(int userId, int branchId) {
        try {
            List<ShiftSL> shifts = shiftController.getWeeklySchedule(userId, branchId).stream()
                    .map(ShiftSL::new)
                    .collect(Collectors.toList());
            return Response.success(shifts);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<ShiftSL> getPastShift(int userId, int branchId, LocalDate date, ShiftType type) {
        try {
            Shift shift = shiftController.getPastShift(userId, branchId, date, type);
            return Response.success(new ShiftSL(shift));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<List<EmployeeSL>> getAvailableForRole(int userId, int branchId, LocalDate date, ShiftType type, Certification role) {
        try {
            List<EmployeeSL> employees = shiftController.getAvailableForRole(userId, branchId, date, type, role)
                    .stream().map(EmployeeSL::new).collect(Collectors.toList());
            return Response.success(employees);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<List<EmployeeSL>> getAllWithCertification(int userId, int branchId, Certification role) {
        try {
            List<EmployeeSL> employees = shiftController.getAllWithCertification(userId, branchId, role)
                    .stream().map(EmployeeSL::new).collect(Collectors.toList());
            return Response.success(employees);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }


    // --- Constraints & Preferences ---

    public Response<Void> setDeadline(int userId, LocalDate date) {
        try {
            shiftController.setDeadline(userId, date);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    //only by employee themselves
    public Response<Void> setWeeklyConstraints(int employeeId, Map<LocalDate, Set<ShiftType>> constraints) {
        try {
            shiftController.setWeeklyConstraints(employeeId, constraints);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> setWeeklyPreferences(int employeeId, Map<LocalDate, Set<ShiftType>> preferences) {
        try {
            shiftController.setWeeklyPreferences(employeeId, preferences);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    // --- Constraints ---
    
    // By employee themselves
    public Response<WeeklyConstraintsSL> getWeeklyConstraints(int employeeId) {
        try {
            Map<LocalDate, Map<ShiftType, Boolean>> consts = shiftController.getWeeklyConstraints(employeeId);
            return Response.success(new WeeklyConstraintsSL(consts));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    // by the HR
    public Response<WeeklyConstraintsSL> getWeeklyConstraints(int hrUserId, int branchId, int targetEmpId) {
        try {
            Map<LocalDate, Map<ShiftType, Boolean>> consts = shiftController.getWeeklyConstraints(hrUserId, branchId, targetEmpId);
            return Response.success(new WeeklyConstraintsSL(consts));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    // --- Preferences ---

    // By employee themselves
    public Response<WeeklyPreferenceSL> getWeeklyPreferences(int employeeId) {
        try {
            Map<LocalDate, Map<ShiftType, Boolean>> prefs = shiftController.getWeeklyPreferences(employeeId);
            return Response.success(new WeeklyPreferenceSL(prefs));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    // by the HR
    public Response<WeeklyPreferenceSL> getWeeklyPreferences(int hrUserId, int branchId, int targetEmpId) {
        try {
            Map<LocalDate, Map<ShiftType, Boolean>> prefs = shiftController.getWeeklyPreferences(hrUserId, branchId, targetEmpId);
            return Response.success(new WeeklyPreferenceSL(prefs));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    // --- Override Requests ---

   public Response<String> createOverrideRequest(int hrUserId, int branchId, int targetEmpId, LocalDate date, ShiftType type, Certification role) {
        try {
            String requestId = shiftController.createOverrideRequest(hrUserId, branchId, targetEmpId, date, type, role);
            return Response.success(requestId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }
    
    public Response<Void> respondToRequest(int employeeId, String requestId, boolean approved) {
        try {
            shiftController.respondToRequest(employeeId, requestId, approved);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> assignWithOverride(int hrUserId, int branchId, String requestId) {
        try {
            shiftController.assignWithOverride(hrUserId, branchId, requestId);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<List<OverrideRequestSL>> viewSentRequests(int hrUserId, int branchId) {
        try {
            List<OverrideRequestSL> requests = shiftController.viewSentRequests(hrUserId, branchId).stream()
                    .map(OverrideRequestSL::new)
                    .collect(Collectors.toList());
            return Response.success(requests);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<List<OverrideRequestSL>> viewReceivedRequests(int employeeId) {
        try {
            List<OverrideRequestSL> requests = shiftController.viewReceivedRequests(employeeId).stream()
                    .map(OverrideRequestSL::new)
                    .collect(Collectors.toList());
            return Response.success(requests);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<OverrideRequestSL> getRequest(int userId, String requestId) {
        try {
            OverrideRequest request = shiftController.viewRequest(userId, requestId);
            return Response.success(new OverrideRequestSL(request));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    
}