package dev.service;

import dev.domain.*;

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

    public Response<WeeklyConstraintsSL> getWeeklyConstraints(int userId, int targetEmpId) {
        try {
            Map<LocalDate, Map<ShiftType, Boolean>> consts = shiftController.getWeeklyConstraints(userId, targetEmpId);
            WeeklyConstraintsSL weeklyConstraintsSL = new WeeklyConstraintsSL(consts);
            return Response.success(weeklyConstraintsSL);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<WeeklyPreferenceSL> getWeeklyPreferences(int userId, int targetEmpId) {
        try {
            Map<LocalDate, Map<ShiftType, Boolean>> prefs = shiftController.getWeeklyPreferences(userId, targetEmpId);
            WeeklyPreferenceSL weeklyPreferenceSL = new WeeklyPreferenceSL(prefs);
            return Response.success(weeklyPreferenceSL);
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

    // SchedulingService
    public Response<List<EmployeeSL>> getAvailableForRole(int userId, LocalDate date, ShiftType type, Certification role) {
        try {
            List<EmployeeSL> employees = shiftController.getAvailableForRole(userId, date, type, role)
                    .stream().map(EmployeeSL::new).collect(Collectors.toList());
            return Response.success(employees);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<List<EmployeeSL>> getAllWithCertification(int userId, Certification role) {
        try {
            List<EmployeeSL> employees = shiftController.getAllWithCertification(userId, role)
                    .stream().map(EmployeeSL::new).collect(Collectors.toList());
            return Response.success(employees);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }
}