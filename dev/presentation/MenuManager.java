package presentation;

import presentation.hr.HRMenu;
import presentation.employee.EmployeeMenu;
import presentation.transport.TransportMenu;
import service.*;

public class MenuManager {

    private static final int NO_USER = -1;

    private final AuthService authService;
    private final HRMenu hrMenu;
    private final EmployeeMenu employeeMenu;
    private final TransportMenu transportMenu;

    private int loggedInUserId = NO_USER;

    public MenuManager(
            AuthService authService,
            SchedulingService schedulingService,
            PersonnelService personnelService,
            TransportService transportService
    ) {
        this.authService = authService;
        this.hrMenu = new HRMenu(this, schedulingService, personnelService, authService);
        this.employeeMenu = new EmployeeMenu(this, schedulingService, personnelService, authService);
        this.transportMenu = new TransportMenu(transportService);
    }

    public void start() {
        mainMenu();
    }

    private void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1) Login");
            System.out.println("2) Register as HR");
            System.out.println("3) Transport Menu");
            System.out.println("4) Exit");

            switch (InputUtil.readInt()) {
                case 1 -> login();
                case 2 -> registerHR();
                case 3 -> transportMenu.start();
                case 4 -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        System.out.println("Goodbye!");
    }

}