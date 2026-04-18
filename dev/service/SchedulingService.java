package dev.service;

import dev.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SchedulingService {

<<<<<<< HEAD
    public SchedulingService(ShiftController shiftController, EmployeeController employeeController){
        this.shiftController = shiftController;
        this.employeeController = employeeController;
    }

    public void submitConstraint(int activeUser, Date date, string startTime, string endTime) {} 
    public String getWeeklySchedule(int activeUser) { return ""; }
    public void createShift(int activeUser, Date date, string type) {} 
    public void setShiftRequirements(int activeUser, String shiftId, Map<String, Integer> requiredRoles) {}
    public boolean assignWorker(int activeUser, String shiftID, int targetEmpID, String role) { return false; }
    public void finalizeWeek(int activeUser, List<String> shiftIds) {}
    private boolean requireHRAuthorization(int activeUser) { return false; }
=======
    private final ShiftController shiftController;

    public SchedulingService(ShiftController shiftController) {
        this.shiftController = shiftController;
    }

    // --- Shift Management ---

    public Response<Void> createShift(int userId, LocalDate date, ShiftType type) {
        try {
            shiftController.createShift(userId, date, type);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> setRequirement(int userId, LocalDate date, ShiftType type, Certification role, int count) {
        try {
            shiftController.setRequirement(userId, date, type, role, count);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> assignEmployee(int userId, int targetEmpId, LocalDate date, ShiftType type, Certification role, boolean isOvertime) {
        try {
            shiftController.assignEmployee(userId, targetEmpId, date, type, role, isOvertime);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> removeEmployee(int userId, LocalDate date, ShiftType type, Certification role, int targetEmpId) {
        try {
            shiftController.removeEmployee(userId, date, type, role, targetEmpId);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    // --- Queries ---

    public Response<ShiftSL> getShift(int userId, LocalDate date, ShiftType type) {
        try {
            Shift shift = shiftController.getShift(userId, date, type);
            return Response.success(new ShiftSL(shift));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<List<ShiftSL>> getWeeklySchedule(int userId) {
        try {
            List<ShiftSL> shifts = shiftController.getWeeklySchedule(userId).stream()
                    .map(ShiftSL::new)
                    .collect(Collectors.toList());
            return Response.success(shifts);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<ShiftSL> getPastShift(int userId, LocalDate date, ShiftType type) {
        try {
            Shift shift = shiftController.getPastShift(userId, date, type);
            return Response.success(new ShiftSL(shift));
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

    public Response<Void> setWeeklyConstraints(int userId, Map<LocalDate, Set<ShiftType>> constraints) {
        try {
            shiftController.setWeeklyConstraints(userId, constraints);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> setWeeklyPreferences(int userId, Map<LocalDate, Set<ShiftType>> preferences) {
        try {
            shiftController.setWeeklyPreferences(userId, preferences);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Map<LocalDate, Map<ShiftType, Boolean>>> getWeeklyConstraints(int userId, int targetEmpId) {
        try {
            return Response.success(shiftController.getWeeklyConstraints(userId, targetEmpId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Map<LocalDate, Map<ShiftType, Boolean>>> getWeeklyPreferences(int userId, int targetEmpId) {
        try {
            return Response.success(shiftController.getWeeklyPreferences(userId, targetEmpId));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    // --- Override Requests ---

    public Response<String> createOverrideRequest(int userId, int targetEmpId, LocalDate date, ShiftType type, Certification role) {
        try {
            String requestId = shiftController.createOverrideRequest(userId, targetEmpId, date, type, role);
            return Response.success(requestId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> respondToRequest(int userId, String requestId, boolean approved) {
        try {
            shiftController.respondToRequest(userId, requestId, approved);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> assignWithOverride(int userId, String requestId) {
        try {
            shiftController.assignWithOverride(userId, requestId);
            return Response.success(null);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<List<OverrideRequestSL>> viewSentRequests(int userId) {
        try {
            List<OverrideRequestSL> requests = shiftController.viewSentRequests(userId).stream()
                    .map(OverrideRequestSL::new)
                    .collect(Collectors.toList());
            return Response.success(requests);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<List<OverrideRequestSL>> viewReceivedRequests(int userId) {
        try {
            List<OverrideRequestSL> requests = shiftController.viewReceivedRequests(userId).stream()
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
>>>>>>> HR_develop
}