package dev.service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.domain.Certification;
import dev.domain.EmpType;
import dev.domain.Employee;
import dev.domain.EmployeeController;
import dev.domain.EmployeeMemory;
import dev.domain.OverrideRequest;
import dev.domain.RequestMemory;
import dev.domain.SalType;
import dev.domain.Shift;
import dev.domain.ShiftController;
import dev.domain.ShiftMemory;
import dev.domain.ShiftType;
import dev.domain.User;
import dev.domain.UserController;
import dev.domain.UserMemory;
import java.util.HashMap;
import java.util.HashSet;


public class ServiceFactory { 
   
    private final AuthService authService;
    private final SchedulingService schedulingService; 
    private final PersonalService personnelService; 

    // Default Constructor
    public ServiceFactory() {
        UserMemory userMemory = new UserMemory();
        EmployeeMemory employeeMemory = new EmployeeMemory();
        ShiftMemory shiftMemory = new ShiftMemory();
        RequestMemory requestMemory = new RequestMemory();

        UserController userController = new UserController(userMemory);
        EmployeeController employeeController = new EmployeeController(userController, employeeMemory);
        ShiftController shiftController = new ShiftController(shiftMemory, employeeMemory, userController, requestMemory);

        this.authService = new AuthService(userController);
        this.personnelService = new PersonalService(userController, employeeController);
        this.schedulingService = new SchedulingService(shiftController, employeeController);
    }

    // if withTestData variable is true - it will act as a simulated data constructor, if it's false - it will act as a default contractor.
    public ServiceFactory(boolean withTestData) {
        UserMemory userMemory = new UserMemory();
        EmployeeMemory employeeMemory = new EmployeeMemory();
        ShiftMemory shiftMemory = new ShiftMemory();
        RequestMemory requestMemory = new RequestMemory();

        if (withTestData) {
            populateTestData(userMemory, employeeMemory, shiftMemory, requestMemory);
        }

        UserController userController = new UserController(userMemory);
        EmployeeController employeeController = new EmployeeController(userController, employeeMemory);
        ShiftController shiftController = new ShiftController(shiftMemory, employeeMemory, userController, requestMemory);

        this.authService = new AuthService(userController);
        this.personnelService = new PersonalService(userController, employeeController);
        this.schedulingService = new SchedulingService(shiftController, employeeController);
    }

    public void populateEmployeeMemory(EmployeeMemory employeeMemory) {
        LocalDate startDate1 = LocalDate.of(2025, 1, 15);
        LocalDate startDate2 = LocalDate.of(2024, 6, 1);
        LocalDate startDate3 = LocalDate.of(2021, 9, 10);
        LocalDate startDate4 = LocalDate.of(2025, 1, 15);

        Employee emp1 = new Employee(
                100000001, "Sarah Cohen", 100001,
                startDate1,
                EmpType.FULL_TIME, SalType.GLOBAL, 12000,
                20, true, 1, false,
                new ArrayList<>(List.of(Certification.HR_MANAGER, Certification.SHIFT_MANAGER))
        );

        Employee emp2 = new Employee(
                100000002, "Yossi Levi", 100002,
                startDate2,
                EmpType.PART_TIME, SalType.HOURLY, 45,
                10, false, 7, false,
                new ArrayList<>(List.of(Certification.CASHIER))
        );

        // Submitting emp2 constraints for the following week
        LocalDate sunday = LocalDate.now();
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }

        LocalDate tuesday = sunday.plusDays(2);  // always a Tuesday
        LocalDate thursday = sunday.plusDays(4); // always a Thursday

        Map<LocalDate, Set<ShiftType>> cons = new HashMap<>();
        Set<ShiftType> shifts1 = new HashSet<>();
        shifts1.add(ShiftType.MORNING);
        Set<ShiftType> shifts2 = new HashSet<>();
        shifts2.add(ShiftType.MORNING);
        cons.put(tuesday, shifts1);
        cons.put(thursday, shifts2);
        emp2.setWeeklyConstraints(cons);

        Employee emp3 = new Employee(
                100000003, "Dana Mizrahi", 100003,
                startDate3,
                EmpType.FULL_TIME, SalType.HOURLY, 55,
                15, true, 6, true,
                new ArrayList<>(List.of(Certification.WAREHOUSE, Certification.CASHIER))
        );

        Employee emp4 = new Employee(
                100000004, "Ron Shapiro", 100004,
                startDate4,
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                18, true, 2, true,
                new ArrayList<>(List.of(Certification.SHIFT_MANAGER, Certification.CASHIER))
        );

        employeeMemory.save(emp1);
        employeeMemory.save(emp2);
        employeeMemory.save(emp3);
        employeeMemory.save(emp4);
        }

    public void populateUserMmemory(UserMemory userMemory){
        User user1 = new User(100000001, "sarah123");   // Sarah Cohen - HR Manager
        User user2 = new User(100000002, "yossi123");   // Yossi Levi - Cashier
        User user3 = new User(100000003, "dana1234");   // Dana Mizrahi - Warehouse
        User user4 = new User(100000004, "ron12345");   // Ron Shapiro - Shift Manager

        userMemory.save(user1);
        userMemory.save(user2);
        userMemory.save(user3);
        userMemory.save(user4);
    }


    public void populateShiftMemory(ShiftMemory shiftMemory){
        
        // Creating alligned shift dates
        LocalDate sunday = LocalDate.now();
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }

        LocalDate tuesday = sunday.plusDays(2);  // always a Tuesday
        LocalDate wednesday = sunday.plusDays(3); // always a Thursday
        LocalDate thursday = sunday.plusDays(4); // always a Thursday


        // Shift 1 
        Shift shift1 = new Shift("SHIFT_001",tuesday , ShiftType.MORNING);
        shift1.setRequirement(Certification.CASHIER, 2);
        shift1.setRequirement(Certification.WAREHOUSE, 1);
        shift1.assignEmployee(Certification.SHIFT_MANAGER, 100000004); 
        shift1.assignEmployee(Certification.CASHIER, 100000002);       
        shift1.assignEmployee(Certification.CASHIER, 100000004);       
        shift1.assignEmployee(Certification.WAREHOUSE, 100000003);     
        shiftMemory.save(shift1);

        // Shift 2 
        Shift shift2 = new Shift("SHIFT_002", wednesday , ShiftType.MORNING);
        shift2.setRequirement(Certification.CASHIER, 2);
        shift2.assignEmployee(Certification.SHIFT_MANAGER, 100000001); 
        shift2.assignEmployee(Certification.CASHIER, 100000003);
        shiftMemory.save(shift2);       

        // Shift 3 
        Shift shift3 = new Shift("SHIFT_003", thursday , ShiftType.MORNING);
        shift3.setRequirement(Certification.CASHIER, 1);
        shift3.setRequirement(Certification.WAREHOUSE, 1);
        shift3.assignEmployee(Certification.SHIFT_MANAGER, 100000004); 
        shift3.assignEmployee(Certification.CASHIER, 100000002);       
        shift3.assignEmployee(Certification.WAREHOUSE, 100000003);     
        shiftMemory.save(shift3);
    }

  
    public void populateRequestMemory(RequestMemory requestMemory){

        // Creating alligned shift dates
        LocalDate sunday = LocalDate.now();
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }

        LocalDate wendesday = sunday.plusDays(3);  // always a wednesday

        String requestId = requestMemory.generateId(); 
        OverrideRequest request = new OverrideRequest(
        requestId,
        100000001,                                      // hrId    - Sarah Cohen
        100000002,                                     // empId   - Yossi Levi
        wendesday,        
        ShiftType.MORNING,                                    // shift - matches shift2
        Certification.CASHIER                                // role - Yossi is certified as cashier
        );
        requestMemory.save(request);
    }
    
    private void populateTestData(UserMemory u, EmployeeMemory e, ShiftMemory s, RequestMemory r) {
        populateUserMmemory(u);
        populateEmployeeMemory(e);
        populateShiftMemory(s);
        populateRequestMemory(r);
    }

}

