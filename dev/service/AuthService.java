package dev.service;

import dev.domain.Certification;
import dev.domain.EmployeeController;
import dev.domain.UserController;

public class AuthService {

    private final UserController userController;
    private final EmployeeController employeeController;

    public AuthService(UserController userController, EmployeeController employeeController) {
        this.userController = userController;
        this.employeeController = employeeController;
    }

    public Response<UserSL> login(int id, String pass) {
        try {
            userController.login(id, pass);
            boolean isHR = employeeController.isHR(id);
            return Response.success(new UserSL(id, isHR));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> logout(int id) {
        try {
            userController.logout(id);
            return Response.success();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }

    public Response<Void> registerHR(int id, String pass) {
        try {
            employeeController.registerHR(id, pass);
            return Response.success();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.failure(e.getMessage());
        }
    }
}