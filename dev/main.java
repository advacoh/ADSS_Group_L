//package dev.presentation;
//
//import dev.domain.*;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class Main {
//
//    private static final Scanner scanner = new Scanner(System.in);
//    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
//
//    private static UserController userController;
//    private static ShiftController shiftController;
//    private static EmployeeController employeeController;
//
//    private static int loggedInUserId = -1;
//    private static boolean isHR = false;
//
//    public static void main(String[] args) {
//        UserMemory userMemory = new UserMemory();
//        EmployeeMemory employeeMemory = new EmployeeMemory();
//        ShiftMemory shiftMemory = new ShiftMemory();
//        RequestMemory requestMemory = new RequestMemory();
//
//        userController = new UserController(userMemory);
//        employeeController = new EmployeeController(employeeMemory, userController); // TODO: adjust constructor if needed
//        shiftController = new ShiftController(shiftMemory, employeeMemory, userController, requestMemory);
//
//        DATE_FORMAT.setLenient(false);
//        mainMenu();
//    }
//
//    // =====================================================================
//    // MAIN MENU
//    // =====================================================================
//
//    private static void mainMenu() {
//        while (true) {
//            System.out.println("\n=== Main Menu ===");
//            System.out.println("1) Login");
//            System.out.println("2) Register as HR");
//            System.out.println("3) Exit");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> login();
//                case 2 -> registerHR();
//                case 3 -> {
//                    System.out.println("Goodbye!");
//                    System.exit(0);
//                }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private static void login() {
//        System.out.print("Enter ID: ");
//        int id = readInt();
//        System.out.print("Enter password: ");
//        String password = scanner.nextLine();
//
//        try {
//            userController.login(id, password);
//            loggedInUserId = id;
//            isHR = employeeController.isHR(id); // TODO: verify method name
//            System.out.println("Login successful. Welcome!");
//            if (isHR) hrMenu();
//            else employeeMenu();
//        } catch (Exception e) {
//            System.out.println("Login failed: " + e.getMessage());
//        }
//    }
//
//    private static void registerHR() {
//        System.out.print("Enter HR ID: ");
//        int id = readInt();
//        System.out.print("Enter name: ");
//        String name = scanner.nextLine();
//        System.out.print("Enter bank account: ");
//        int bankAccount = readInt();
//        Date startDate = readDate("Enter start date (dd/MM/yyyy): ");
//        if (startDate == null) return;
//        System.out.print("Enter salary: ");
//        double salary = readDouble();
//        System.out.print("Enter password (min 6 chars): ");
//        String password = scanner.nextLine();
//
//        try {
//            // TODO: adjust addEmployee signature to match your EmployeeController
//            employeeController.addHR(id, name, bankAccount, startDate, salary, password);
//            System.out.println("HR registered. You can now log in.");
//        } catch (Exception e) {
//            System.out.println("Registration failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // HR MENU
//    // =====================================================================
//
//    private static void hrMenu() {
//        while (true) {
//            System.out.println("\n=== HR Menu ===");
//            System.out.println("1) Shift Definition");
//            System.out.println("2) Shift Filling");
//            System.out.println("3) View Schedule & Preferences");
//            System.out.println("4) History");
//            System.out.println("5) Employee Management");
//            System.out.println("6) Request Handling");
//            System.out.println("7) Logout");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> shiftDefinitionMenu();
//                case 2 -> shiftFillingMenu();
//                case 3 -> scheduleAndPreferencesMenu();
//                case 4 -> historyMenu();
//                case 5 -> employeeManagementMenu();
//                case 6 -> hrRequestHandlingMenu();
//                case 7 -> { logout(); return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    // =====================================================================
//    // SHIFT DEFINITION
//    // =====================================================================
//
//    private static void shiftDefinitionMenu() {
//        while (true) {
//            System.out.println("\n=== Shift Definition ===");
//            System.out.println("1) Create Shift");
//            System.out.println("2) Set Role Requirement");
//            System.out.println("3) View Shift Requirements");
//            System.out.println("4) Back");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> createShift();
//                case 2 -> setRoleRequirement();
//                case 3 -> viewShiftRequirements();
//                case 4 -> { return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private static void createShift() {
//        Date date = readDate("Enter shift date (dd/MM/yyyy): ");
//        if (date == null) return;
//        ShiftType type = readShiftType();
//        if (type == null) return;
//
//        try {
//            shiftController.createShift(loggedInUserId, date, type);
//            System.out.println("Shift created. Note: Shift Manager (x1) is required by default.");
//            System.out.print("Would you like to set additional role requirements now? (y/n): ");
//            if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
//                setRoleRequirementForShift(date, type);
//            }
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void setRoleRequirement() {
//        Date date = readDate("Enter shift date (dd/MM/yyyy): ");
//        if (date == null) return;
//        ShiftType type = readShiftType();
//        if (type == null) return;
//        setRoleRequirementForShift(date, type);
//    }
//
//    private static void setRoleRequirementForShift(Date date, ShiftType type) {
//        while (true) {
//            try {
//                Shift shift = shiftController.getShift(loggedInUserId, date, type);
//                System.out.println("\nCurrent requirements:");
//                System.out.println("  SHIFT_MANAGER: 1 (locked)");
//                printShiftRequirements(shift);
//            } catch (Exception e) {
//                System.out.println("Could not load shift: " + e.getMessage());
//                return;
//            }
//
//            Certification role = readCertification();
//            if (role == null) return;
//            if (role == Certification.SHIFT_MANAGER) {
//                System.out.println("Shift Manager is locked at 1 and cannot be changed.");
//                continue;
//            }
//
//            System.out.print("Enter required count (0 to remove role): ");
//            int count = readInt();
//
//            try {
//                shiftController.setRequirement(loggedInUserId, date, type, role, count);
//                System.out.println("Requirement updated.");
//            } catch (Exception e) {
//                System.out.println("Failed: " + e.getMessage());
//            }
//
//            System.out.print("Set another role requirement? (y/n): ");
//            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) return;
//        }
//    }
//
//    private static void viewShiftRequirements() {
//        Date date = readDate("Enter shift date (dd/MM/yyyy): ");
//        if (date == null) return;
//        ShiftType type = readShiftType();
//        if (type == null) return;
//
//        try {
//            Shift shift = shiftController.getShift(loggedInUserId, date, type);
//            printShiftHeader(shift);
//            printShiftRequirements(shift);
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // SHIFT FILLING
//    // =====================================================================
//
//    private static void shiftFillingMenu() {
//        while (true) {
//            System.out.println("\n=== Shift Filling ===");
//            System.out.println("1) Assign Employee to Shift");
//            System.out.println("2) Remove Employee from Shift");
//            System.out.println("3) View Assigned Employees");
//            System.out.println("4) Back");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> assignEmployee();
//                case 2 -> removeEmployeeFromShift();
//                case 3 -> viewAssignedEmployees();
//                case 4 -> { return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private static void assignEmployee() {
//        Date date = readDate("Enter shift date (dd/MM/yyyy): ");
//        if (date == null) return;
//        ShiftType type = readShiftType();
//        if (type == null) return;
//        Certification role = readCertification();
//        if (role == null) return;
//
//        try {
//            List<Integer> candidates = employeeController.getAvailableByRoleAndShift(role, date, type); // TODO: verify signature
//
//            if (candidates.isEmpty()) {
//                System.out.println("No available certified employees for this role.");
//                System.out.print("Send an override request to an employee? (y/n): ");
//                if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
//                    sendOverrideRequest(date, type, role);
//                }
//                return;
//            }
//
//            System.out.println("\nAvailable employees:");
//            for (int empId : candidates) {
//                System.out.println("  - ID: " + empId); // TODO: print name if Employee.getName() exists
//            }
//
//            System.out.print("Enter employee ID to assign: ");
//            int empId = readInt();
//
//            System.out.print("Is this overtime? (y/n): ");
//            boolean isOvertime = scanner.nextLine().trim().equalsIgnoreCase("y");
//
//            shiftController.assignEmployee(loggedInUserId, empId, date, type, role, isOvertime);
//            System.out.println("Employee " + empId + " assigned successfully.");
//
//        } catch (Exception e) {
//            System.out.println("Assignment failed: " + e.getMessage());
//        }
//    }
//
//    private static void sendOverrideRequest(Date date, ShiftType type, Certification role) {
//        System.out.print("Enter employee ID to send override request to: ");
//        int empId = readInt();
//
//        try {
//            String requestId = shiftController.createOverrideRequest(loggedInUserId, empId, date, type, role);
//            System.out.println("Override request sent successfully. Request ID: " + requestId);
//            System.out.println("Track its status in Request Handling once the employee responds.");
//        } catch (Exception e) {
//            System.out.println("Failed to send request: " + e.getMessage());
//        }
//    }
//
//    private static void removeEmployeeFromShift() {
//        Date date = readDate("Enter shift date (dd/MM/yyyy): ");
//        if (date == null) return;
//        ShiftType type = readShiftType();
//        if (type == null) return;
//        Certification role = readCertification();
//        if (role == null) return;
//        System.out.print("Enter employee ID: ");
//        int empId = readInt();
//
//        try {
//            shiftController.removeEmployee(loggedInUserId, date, type, role, empId); // TODO: add this method to ShiftController
//            System.out.println("Employee removed from shift.");
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void viewAssignedEmployees() {
//        Date date = readDate("Enter shift date (dd/MM/yyyy): ");
//        if (date == null) return;
//        ShiftType type = readShiftType();
//        if (type == null) return;
//
//        try {
//            Shift shift = shiftController.getShift(loggedInUserId, date, type);
//            printShiftHeader(shift);
//            printShiftAssignments(shift);
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // SCHEDULE & PREFERENCES
//    // =====================================================================
//
//    private static void scheduleAndPreferencesMenu() {
//        while (true) {
//            System.out.println("\n=== Schedule & Preferences ===");
//            System.out.println("1) View Full Schedule");
//            System.out.println("2) View Employee Constraints");
//            System.out.println("3) View Employee Preferences");
//            System.out.println("4) View Shift Details");
//            System.out.println("5) Back");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> viewFullSchedule();
//                case 2 -> viewEmployeeConstraints();
//                case 3 -> viewEmployeePreferences();
//                case 4 -> viewShiftDetails();
//                case 5 -> { return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private static void viewFullSchedule() {
//        try {
//            List<Shift> shifts = shiftController.getActiveShifts(loggedInUserId);
//            if (shifts.isEmpty()) {
//                System.out.println("No shifts scheduled for this week.");
//                return;
//            }
//            System.out.println("\n=== Weekly Schedule ===");
//            for (Shift s : shifts) {
//                String status = s.isFullyStaffed() ? "[FULLY STAFFED]" : "[INCOMPLETE]";
//                System.out.println("  " + s.getDate() + " | " + s.getType() + " | " + status);
//            }
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void viewEmployeeConstraints() {
//        System.out.print("Enter employee ID: ");
//        int empId = readInt();
//
//        try {
//            Map<Date, Map<ShiftType, Boolean>> constraints = shiftController.getWeeklyConstraints(loggedInUserId, empId);
//            System.out.println("\nConstraints for employee " + empId + ":");
//            printWeeklySubmission(constraints);
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void viewEmployeePreferences() {
//        System.out.print("Enter employee ID: ");
//        int empId = readInt();
//
//        try {
//            Map<Date, Map<ShiftType, Boolean>> prefs = shiftController.getWeeklyPreferences(loggedInUserId, empId);
//            System.out.println("\nPreferences for employee " + empId + ":");
//            printWeeklySubmission(prefs);
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void viewShiftDetails() {
//        Date date = readDate("Enter shift date (dd/MM/yyyy): ");
//        if (date == null) return;
//        ShiftType type = readShiftType();
//        if (type == null) return;
//
//        try {
//            Shift shift = shiftController.getShift(loggedInUserId, date, type);
//            printShiftHeader(shift);
//            printShiftRequirements(shift);
//            printShiftAssignments(shift);
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // HISTORY
//    // =====================================================================
//
//    private static void historyMenu() {
//        while (true) {
//            System.out.println("\n=== History ===");
//            System.out.println("1) View Past Shift");
//            System.out.println("2) Back");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> viewPastShift();
//                case 2 -> { return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private static void viewPastShift() {
//        Date date = readDate("Enter shift date (dd/MM/yyyy): ");
//        if (date == null) return;
//        ShiftType type = readShiftType();
//        if (type == null) return;
//
//        try {
//            Shift shift = shiftController.getPastShift(loggedInUserId, date, type);
//            printShiftHeader(shift);
//            printShiftRequirements(shift);
//            printShiftAssignments(shift);
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // EMPLOYEE MANAGEMENT
//    // =====================================================================
//
//    private static void employeeManagementMenu() {
//        while (true) {
//            System.out.println("\n=== Employee Management ===");
//            System.out.println("1) Add Employee");
//            System.out.println("2) Edit Employee Profile");
//            System.out.println("3) Remove Employee");
//            System.out.println("4) View Employee Details");
//            System.out.println("5) Back");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> addEmployee();
//                case 2 -> editEmployee();
//                case 3 -> removeEmployee();
//                case 4 -> viewEmployeeDetails();
//                case 5 -> { return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private static void addEmployee() {
//        System.out.print("Enter employee ID: ");
//        int id = readInt();
//        System.out.print("Enter name: ");
//        String name = scanner.nextLine();
//        System.out.print("Enter bank account number: ");
//        int bankAccount = readInt();
//        Date startDate = readDate("Enter start date (dd/MM/yyyy): ");
//        if (startDate == null) return;
//
//        System.out.println("Employment type: 1) FULL_TIME  2) PART_TIME");
//        System.out.print("Choose: ");
//        EmpType empType = switch (readInt()) {
//            case 1 -> EmpType.FULL_TIME;
//            case 2 -> EmpType.PART_TIME;
//            default -> null;
//        };
//        if (empType == null) { System.out.println("Invalid type."); return; }
//
//        System.out.println("Salary type: 1) HOURLY  2) GLOBAL");
//        System.out.print("Choose: ");
//        SalType salType = switch (readInt()) {
//            case 1 -> SalType.HOURLY;
//            case 2 -> SalType.GLOBAL;
//            default -> null;
//        };
//        if (salType == null) { System.out.println("Invalid type."); return; }
//
//        System.out.print("Enter salary amount: ");
//        double salary = readDouble();
//        System.out.print("Enter vacation days: ");
//        int vacation = readInt();
//
//        System.out.println("Fixed day off:");
//        DayOfWeek dayOff = readDayOfWeek();
//        if (dayOff == null) return;
//
//        System.out.print("Willing to work double shifts? (y/n): ");
//        boolean willDouble = scanner.nextLine().trim().equalsIgnoreCase("y");
//        System.out.print("Willing to work overtime? (y/n): ");
//        boolean willOvertime = scanner.nextLine().trim().equalsIgnoreCase("y");
//        System.out.print("Enter password (min 6 chars): ");
//        String password = scanner.nextLine();
//
//        try {
//            // TODO: adjust to match your EmployeeController.addEmployee() signature
//            employeeController.addEmployee(loggedInUserId, id, name, bankAccount, startDate,
//                    empType, salType, salary, vacation, dayOff, willDouble, willOvertime, password);
//            System.out.println("Employee added successfully.");
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void editEmployee() {
//        System.out.print("Enter employee ID: ");
//        int empId = readInt();
//
//        System.out.println("\nEdit options:");
//        System.out.println("1) Add Certification");
//        System.out.println("2) Remove Certification");
//        System.out.println("3) Set Constraints on Behalf of Employee");
//        System.out.println("4) Back");
//        System.out.print("Choose: ");
//
//        switch (readInt()) {
//            case 1 -> addCertification(empId);
//            case 2 -> removeCertification(empId);
//            case 3 -> setConstraintsForEmployee(empId);
//            case 4 -> {}
//            default -> System.out.println("Invalid option.");
//        }
//    }
//
//    private static void addCertification(int empId) {
//        System.out.println("Select certification to add:");
//        Certification cert = readCertification();
//        if (cert == null) return;
//        try {
//            employeeController.addCertification(loggedInUserId, empId, cert); // TODO: verify method name
//            System.out.println("Certification added.");
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void removeCertification(int empId) {
//        System.out.println("Select certification to remove:");
//        Certification cert = readCertification();
//        if (cert == null) return;
//        try {
//            employeeController.removeCertification(loggedInUserId, empId, cert); // TODO: verify method name
//            System.out.println("Certification removed.");
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void setConstraintsForEmployee(int empId) {
//        System.out.println("Setting constraints for employee " + empId + ":");
//        Map<Date, Set<ShiftType>> constraints = readWeeklySubmission("unavailable");
//        try {
//            shiftController.setWeeklyConstraints(loggedInUserId, empId, constraints);
//            System.out.println("Constraints set.");
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void removeEmployee() {
//        System.out.print("Enter employee ID: ");
//        int empId = readInt();
//        System.out.print("Are you sure? (y/n): ");
//        if (!scanner.nextLine().trim().equalsIgnoreCase("y")) return;
//
//        try {
//            employeeController.dismissEmployee(loggedInUserId, empId); // TODO: verify method name
//            System.out.println("Employee removed.");
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void viewEmployeeDetails() {
//        System.out.print("Enter employee ID: ");
//        int empId = readInt();
//        try {
//            Employee emp = employeeController.getEmployee(loggedInUserId, empId); // TODO: verify method name
//            printEmployeeDetails(emp);
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // HR REQUEST HANDLING
//    // =====================================================================
//
//    private static void hrRequestHandlingMenu() {
//        while (true) {
//            System.out.println("\n=== Request Handling ===");
//            System.out.println("1) View Sent Requests");
//            System.out.println("2) Back");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> viewSentRequests();
//                case 2 -> { return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private static void viewSentRequests() {
//        try {
//            List<OverrideRequest> requests = shiftController.viewSentRequests(loggedInUserId);
//            if (requests.isEmpty()) {
//                System.out.println("You have no sent requests.");
//                return;
//            }
//
//            System.out.println("\nYour sent override requests:");
//            for (OverrideRequest r : requests) {
//                System.out.printf("  [%s] Employee: %d | %s %s | Role: %s | Status: %s%n",
//                        r.getId(), r.getEmpId(), r.getDate(), r.getShiftType(), r.getRole(), r.getStatus());
//            }
//
//            System.out.print("\nEnter request ID to act on (or press Enter to go back): ");
//            String input = scanner.nextLine().trim();
//            if (input.isEmpty()) return;
//
//            try {
//                OverrideRequest request = shiftController.viewRequest(loggedInUserId, input);
//                System.out.println("Status: " + request.getStatus());
//
//                if (request.getStatus() == RequestStatus.APPROVED) {
//                    System.out.print("Request is APPROVED. Assign employee with override? (y/n): ");
//                    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
//                        shiftController.assignWithOverride(loggedInUserId, input);
//                        System.out.println("Employee assigned with override.");
//                    }
//                } else if (request.getStatus() == RequestStatus.PENDING) {
//                    System.out.println("Request is still PENDING — waiting for employee response.");
//                } else {
//                    System.out.println("Request was REJECTED by the employee.");
//                }
//            } catch (Exception e) {
//                System.out.println("Failed: " + e.getMessage());
//            }
//
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // EMPLOYEE MENU
//    // =====================================================================
//
//    private static void employeeMenu() {
//        while (true) {
//            System.out.println("\n=== Employee Menu ===");
//            System.out.println("1) Set Constraints");
//            System.out.println("2) Set Preferences");
//            System.out.println("3) View Profile");
//            System.out.println("4) View Schedule");
//            System.out.println("5) Request Handling");
//            System.out.println("6) Logout");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> constraintsMenu();
//                case 2 -> preferencesMenu();
//                case 3 -> viewMyProfile();
//                case 4 -> viewMySchedule();
//                case 5 -> employeeRequestHandlingMenu();
//                case 6 -> { logout(); return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    // =====================================================================
//    // CONSTRAINTS
//    // =====================================================================
//
//    private static void constraintsMenu() {
//        while (true) {
//            System.out.println("\n=== Constraints ===");
//            System.out.println("1) Set Weekly Availability");
//            System.out.println("2) View My Constraints");
//            System.out.println("3) Back");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> setMyConstraints();
//                case 2 -> viewMyConstraints();
//                case 3 -> { return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private static void setMyConstraints() {
//        System.out.println("Mark shifts you CANNOT work:");
//        Map<Date, Set<ShiftType>> constraints = readWeeklySubmission("unavailable");
//        try {
//            shiftController.setWeeklyConstraints(loggedInUserId, loggedInUserId, constraints);
//            System.out.println("Constraints saved successfully.");
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void viewMyConstraints() {
//        try {
//            Map<Date, Map<ShiftType, Boolean>> constraints = shiftController.getWeeklyConstraints(loggedInUserId, loggedInUserId);
//            System.out.println("\nYour constraints this week:");
//            printWeeklySubmission(constraints);
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // PREFERENCES
//    // =====================================================================
//
//    private static void preferencesMenu() {
//        while (true) {
//            System.out.println("\n=== Preferences ===");
//            System.out.println("1) Set Weekly Preferences");
//            System.out.println("2) View My Preferences");
//            System.out.println("3) Back");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> setMyPreferences();
//                case 2 -> viewMyPreferences();
//                case 3 -> { return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private static void setMyPreferences() {
//        System.out.println("Mark shifts you PREFER to work:");
//        Map<Date, Set<ShiftType>> prefs = readWeeklySubmission("preferred");
//        try {
//            shiftController.setWeeklyPreferences(loggedInUserId, loggedInUserId, prefs);
//            System.out.println("Preferences saved successfully.");
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void viewMyPreferences() {
//        try {
//            Map<Date, Map<ShiftType, Boolean>> prefs = shiftController.getWeeklyPreferences(loggedInUserId, loggedInUserId);
//            System.out.println("\nYour preferences this week:");
//            printWeeklySubmission(prefs);
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // EMPLOYEE PROFILE & SCHEDULE
//    // =====================================================================
//
//    private static void viewMyProfile() {
//        try {
//            Employee emp = employeeController.getEmployee(loggedInUserId, loggedInUserId); // TODO: verify method name
//            printEmployeeDetails(emp);
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    private static void viewMySchedule() {
//        try {
//            List<Shift> shifts = shiftController.getActiveShifts(loggedInUserId);
//            System.out.println("\n=== Your Shifts This Week ===");
//            boolean found = false;
//            for (Shift s : shifts) {
//                if (s.isEmployeeAssigned(loggedInUserId)) {
//                    System.out.println("  " + s.getDate() + " | " + s.getType());
//                    found = true;
//                }
//            }
//            if (!found) System.out.println("You have no assigned shifts this week.");
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // EMPLOYEE REQUEST HANDLING
//    // =====================================================================
//
//    private static void employeeRequestHandlingMenu() {
//        while (true) {
//            System.out.println("\n=== Request Handling ===");
//            System.out.println("1) View Received Requests");
//            System.out.println("2) Back");
//            System.out.print("Choose: ");
//
//            switch (readInt()) {
//                case 1 -> viewReceivedRequests();
//                case 2 -> { return; }
//                default -> System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private static void viewReceivedRequests() {
//        try {
//            List<OverrideRequest> requests = shiftController.viewReceivedRequests(loggedInUserId);
//            if (requests.isEmpty()) {
//                System.out.println("You have no override requests.");
//                return;
//            }
//
//            System.out.println("\nOverride requests sent to you:");
//            for (OverrideRequest r : requests) {
//                System.out.printf("  [%s] Shift: %s %s | Role: %s | Status: %s%n",
//                        r.getId(), r.getDate(), r.getShiftType(), r.getRole(), r.getStatus());
//            }
//
//            System.out.print("\nEnter request ID to respond to (or press Enter to go back): ");
//            String input = scanner.nextLine().trim();
//            if (input.isEmpty()) return;
//
//            System.out.print("Approve this request? (y/n): ");
//            boolean approved = scanner.nextLine().trim().equalsIgnoreCase("y");
//
//            shiftController.respondToRequest(loggedInUserId, input, approved);
//            System.out.println(approved ? "Request approved." : "Request rejected.");
//
//        } catch (Exception e) {
//            System.out.println("Failed: " + e.getMessage());
//        }
//    }
//
//    // =====================================================================
//    // LOGOUT
//    // =====================================================================
//
//    private static void logout() {
//        try {
//            userController.logout(loggedInUserId);
//            System.out.println("Logged out successfully.");
//        } catch (Exception e) {
//            System.out.println("Logout error: " + e.getMessage());
//        } finally {
//            loggedInUserId = -1;
//            isHR = false;
//        }
//    }
//
//    // =====================================================================
//    // INPUT HELPERS
//    // =====================================================================
//
//    private static int readInt() {
//        while (true) {
//            try {
//                return Integer.parseInt(scanner.nextLine().trim());
//            } catch (NumberFormatException e) {
//                System.out.print("Invalid input. Enter a number: ");
//            }
//        }
//    }
//
//    private static double readDouble() {
//        while (true) {
//            try {
//                return Double.parseDouble(scanner.nextLine().trim());
//            } catch (NumberFormatException e) {
//                System.out.print("Invalid input. Enter a number: ");
//            }
//        }
//    }
//
//    private static Date readDate(String prompt) {
//        System.out.print(prompt);
//        while (true) {
//            try {
//                return DATE_FORMAT.parse(scanner.nextLine().trim());
//            } catch (ParseException e) {
//                System.out.print("Invalid format. Use dd/MM/yyyy: ");
//            }
//        }
//    }
//
//    private static ShiftType readShiftType() {
//        System.out.println("Shift type: 1) MORNING  2) EVENING");
//        System.out.print("Choose: ");
//        return switch (readInt()) {
//            case 1 -> ShiftType.MORNING;
//            case 2 -> ShiftType.EVENING;
//            default -> { System.out.println("Invalid shift type."); yield null; }
//        };
//    }
//
//    private static Certification readCertification() {
//        Certification[] certs = Certification.values();
//        System.out.println("Select role:");
//        for (int i = 0; i < certs.length; i++) {
//            System.out.println("  " + (i + 1) + ") " + certs[i]);
//        }
//        System.out.print("Choose (0 to cancel): ");
//        int choice = readInt();
//        if (choice == 0 || choice > certs.length) {
//            System.out.println("Cancelled.");
//            return null;
//        }
//        return certs[choice - 1];
//    }
//
//    private static DayOfWeek readDayOfWeek() {
//        DayOfWeek[] days = DayOfWeek.values();
//        for (int i = 0; i < days.length; i++) {
//            System.out.println("  " + (i + 1) + ") " + days[i]);
//        }
//        System.out.print("Choose: ");
//        int choice = readInt();
//        if (choice < 1 || choice > days.length) {
//            System.out.println("Invalid day.");
//            return null;
//        }
//        return days[choice - 1];
//    }
//
//    /**
//     * Reads a weekly shift submission from the user.
//     * @param label "unavailable" for constraints, "preferred" for preferences
//     */
//    private static Map<Date, Set<ShiftType>> readWeeklySubmission(String label) {
//        Map<Date, Set<ShiftType>> result = new HashMap<>();
//        System.out.println("Enter shifts you are " + label + ". Press Enter with no date when done.");
//
//        while (true) {
//            System.out.print("Enter date (dd/MM/yyyy) or press Enter to finish: ");
//            String input = scanner.nextLine().trim();
//            if (input.isEmpty()) break;
//
//            try {
//                Date date = DATE_FORMAT.parse(input);
//                ShiftType type = readShiftType();
//                if (type == null) continue;
//                result.computeIfAbsent(date, k -> new HashSet<>()).add(type);
//                System.out.println("  Added.");
//            } catch (ParseException e) {
//                System.out.println("Invalid date format. Use dd/MM/yyyy.");
//            }
//        }
//        return result;
//    }
//
//    // =====================================================================
//    // PRINT HELPERS
//    // =====================================================================
//
//    private static void printShiftHeader(Shift shift) {
//        System.out.println("\n=== Shift: " + shift.getDate() + " | " + shift.getType() + " ===");
//        System.out.println("Status: " + (shift.isFullyStaffed() ? "Fully Staffed" : "Incomplete"));
//    }
//
//    private static void printShiftRequirements(Shift shift) {
//        // TODO: add getRequiredRoles(): Map<Certification, Integer> to Shift if you want to print requirements here
//        System.out.println("(Add getRequiredRoles() to Shift to display requirements)");
//    }
//
//    private static void printShiftAssignments(Shift shift) {
//        Map<Certification, List<Integer>> assignments = shift.getAssignments();
//        if (assignments.isEmpty()) {
//            System.out.println("  No employees assigned yet.");
//            return;
//        }
//        System.out.println("Assigned employees:");
//        for (Map.Entry<Certification, List<Integer>> entry : assignments.entrySet()) {
//            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
//        }
//        List<Integer> overtime = shift.getOvertimeEmployees();
//        if (!overtime.isEmpty()) {
//            System.out.println("Overtime employees: " + overtime);
//        }
//    }
//
//    private static void printWeeklySubmission(Map<Date, Map<ShiftType, Boolean>> data) {
//        if (data == null || data.isEmpty()) {
//            System.out.println("  Nothing submitted.");
//            return;
//        }
//        for (Map.Entry<Date, Map<ShiftType, Boolean>> dateEntry : data.entrySet()) {
//            System.out.println("  " + dateEntry.getKey() + ":");
//            for (Map.Entry<ShiftType, Boolean> slotEntry : dateEntry.getValue().entrySet()) {
//                System.out.println("    " + slotEntry.getKey() + ": " + (slotEntry.getValue() ? "X" : "OK"));
//            }
//        }
//    }
//
//    private static void printEmployeeDetails(Employee emp) {
//        // TODO: print whichever fields Employee exposes via getters
//        System.out.println("\n=== Employee Profile ===");
//     //   System.out.println("ID: " + emp.getId());
//        // System.out.println("Name: " + emp.getName());
//        // System.out.println("Certifications: " + emp.getCertifications());
//        // etc.
//    }
//}