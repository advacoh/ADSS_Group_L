package service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import domain.transportation.Driver;
import enums.LicenseType;
import domain.transportation.TransportController;
import domain.hr.Certification;
import domain.hr.EmpType;
import domain.hr.Employee;
import domain.hr.EmployeeController;
import domain.hr.EmployeeMemory;
import domain.hr.OverrideRequest;
import domain.hr.RequestMemory;
import domain.hr.SalType;
import domain.hr.Shift;
import domain.hr.ShiftController;
import domain.hr.ShiftMemory;
import domain.hr.ShiftType;
import domain.hr.User;
import domain.hr.UserController;
import domain.hr.UserMemory;
import java.util.HashMap;
import java.util.HashSet;
import java.time.LocalTime;
import domain.transportation.Site;
import domain.transportation.DeliveryZone;
import domain.transportation.Truck;
import domain.transportation.TransportedItem;
import domain.transportation.DeliveryDocument;
import domain.transportation.Delivery;
import enums.SiteType;
import enums.DeliveryStatus;
import repository.DriverRepository;

public class ServiceFactory { 
   
    private final AuthService authService;
    private final SchedulingService schedulingService; 
    private final PersonnelService PersonnelService; 
    private final TransportService transportService;

    private LocalDate targetTuesday;
    private LocalDate targetWednesday;
    private LocalDate targetThursday;

   
    public ServiceFactory(boolean withData) {
        UserMemory userMemory = new UserMemory();
        EmployeeMemory employeeMemory = new EmployeeMemory();
        ShiftMemory shiftMemory = new ShiftMemory();
        RequestMemory requestMemory = new RequestMemory();
        DriverRepository driverMemory = new DriverRepository();
      
        calculateDynamicDates();

        if (withData) {
            populateTestData(userMemory, employeeMemory, shiftMemory, requestMemory, driverMemory);
        }

        UserController userController = new UserController(userMemory);
        EmployeeController employeeController = new EmployeeController(userController, employeeMemory, driverMemory);
        ShiftController shiftController = new ShiftController(shiftMemory, employeeMemory, userController, requestMemory);
        TransportController transportController = new TransportController(shiftController, driverMemory);

        this.authService = new AuthService(userController, employeeController);
        this.PersonnelService = new PersonnelService(userController, employeeController, transportController);
        this.schedulingService = new SchedulingService(shiftController);
        this.transportService = new TransportService(transportController);

        populateTransportData(transportController);
    }

    
    private void calculateDynamicDates() {
        LocalDate sunday = LocalDate.now();
        while (sunday.getDayOfWeek() != DayOfWeek.SUNDAY) {
            sunday = sunday.plusDays(1);
        }
        this.targetTuesday = sunday.plusDays(2);  
        this.targetWednesday = sunday.plusDays(3); 
        this.targetThursday = sunday.plusDays(4); 
    }

    public void populateEmployeeMemory(EmployeeMemory employeeMemory, DriverRepository driverMemory){ {
        LocalDate startDate1 = LocalDate.of(2025, 1, 15);
        LocalDate startDate2 = LocalDate.of(2024, 6, 1);
        LocalDate startDate3 = LocalDate.of(2021, 9, 10);
        LocalDate startDate4 = LocalDate.of(2025, 1, 15);

        Employee emp1 = new Employee(
                100000001, "Sarah Cohen", 100001,
                startDate1,
                EmpType.FULL_TIME, SalType.GLOBAL, 12000,
                20, true, 1, false,
                new HashSet<>(List.of(Certification.HR_MANAGER, Certification.SHIFT_MANAGER))
        );

        Employee emp2 = new Employee(
                100000002, "Yossi Levi", 100002,
                startDate2,
                EmpType.PART_TIME, SalType.HOURLY, 45,
                10, false, 7, false,
                new HashSet<>(List.of(Certification.CASHIER))
        );
        emp2.setBranchId(1); 
        

        // Submitting emp2 constraints for the following week
        LocalDate today = LocalDate.now();
        LocalDate sunday = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
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
        cons.put(this.targetTuesday, shifts1);
        cons.put(this.targetThursday, shifts2);
        emp2.setWeeklyConstraints(cons);

        Employee emp3 = new Employee(
                100000003, "Dana Mizrahi", 100003,
                startDate3,
                EmpType.FULL_TIME, SalType.HOURLY, 55,
                15, true, 6, true,
                new HashSet<>(List.of(Certification.WAREHOUSE, Certification.CASHIER))
        );

        emp3.setBranchId(1); 

        Employee emp4 = new Employee(
                100000004, "Ron Shapiro", 100004,
                startDate4,
                EmpType.FULL_TIME, SalType.GLOBAL, 10000,
                18, true, 2, true,
                new HashSet<>(List.of(Certification.SHIFT_MANAGER, Certification.CASHIER))
        );

        emp4.setBranchId(1);

        Employee emp5 = new Employee(
            100000005, "Avi Transport", 100005,
            LocalDate.of(2024, 1, 1),
            EmpType.FULL_TIME, SalType.GLOBAL, 12000,
            18, true, 1, false,
            new HashSet<>(List.of(Certification.DELIVERY_MANAGER))
        );
        emp5.setBranchId(1); 

        Driver driverEmp = new Driver(
            100000006,
            "David Driver",
            100006,
            LocalDate.of(2024, 1, 1),
            EmpType.FULL_TIME,
            SalType.GLOBAL,
            11000,
            18,
            true,
            1,
            false,
            new HashSet<>(List.of(Certification.DRIVER)),
            LicenseType.B
    );
    driverEmp.setBranchId(1); 

        employeeMemory.save(emp1);
        employeeMemory.save(emp2);
        employeeMemory.save(emp3);
        employeeMemory.save(emp4);
        employeeMemory.save(emp5);
        employeeMemory.save(driverEmp);
        driverMemory.addDriver(driverEmp);
     } }


    public void populateUserMmemory(UserMemory userMemory){
        User user1 = new User(100000001, "sarah123");   // Sarah Cohen - HR Manager
        User user2 = new User(100000002, "yossi123");   // Yossi Levi - Cashier
        User user3 = new User(100000003, "dana1234");   // Dana Mizrahi - Warehouse
        User user4 = new User(100000004, "ron12345");   // Ron Shapiro - Shift Manager
        User user5 = new User(100000005, "transport123");
 
        userMemory.save(user1);
        userMemory.save(user2);
        userMemory.save(user3);
        userMemory.save(user4);
        userMemory.save(user5);
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
        Shift shift1 = new Shift("SHIFT_001",1,this.targetTuesday , ShiftType.MORNING);
        shift1.setRequirement(Certification.CASHIER, 2);
        shift1.setRequirement(Certification.WAREHOUSE, 1);
        shift1.setRequirement(Certification.DRIVER, 1);

        shift1.assignEmployee(Certification.SHIFT_MANAGER, 100000004); 
        shift1.assignEmployee(Certification.CASHIER, 100000002);       
        shift1.assignEmployee(Certification.CASHIER, 100000004);       
        shift1.assignEmployee(Certification.WAREHOUSE, 100000003); 
        shift1.assignEmployee(Certification.DRIVER, 100000006); 
          
        shiftMemory.save(shift1);

        // first shift for branch 2
        Shift shift12 = new Shift("SHIFT_001_02",2,this.targetTuesday , ShiftType.MORNING);
        shift1.setRequirement(Certification.CASHIER, 2);
        shift1.setRequirement(Certification.WAREHOUSE, 1);
        shift1.setRequirement(Certification.DRIVER, 1);
        shiftMemory.save(shift1);

        // Shift 2 
        Shift shift2 = new Shift("SHIFT_002", 1, this.targetWednesday , ShiftType.MORNING);
        shift2.setRequirement(Certification.CASHIER, 2);
        shift2.assignEmployee(Certification.SHIFT_MANAGER, 100000001); 
        shift2.assignEmployee(Certification.CASHIER, 100000003);
        shiftMemory.save(shift2);       

        // Shift 3 
        Shift shift3 = new Shift("SHIFT_003", 1, this.targetThursday , ShiftType.MORNING);
        shift3.setRequirement(Certification.CASHIER, 1);
        shift3.setRequirement(Certification.WAREHOUSE, 1);
        shift3.assignEmployee(Certification.SHIFT_MANAGER, 100000004); 
        shift3.assignEmployee(Certification.CASHIER, 100000002);       
        shift3.assignEmployee(Certification.WAREHOUSE, 100000003);     
        shiftMemory.save(shift3);

        // Shift 4 - archived past shift for testing viewHistory
        Shift shift4 = new Shift("SHIFT_004", 1, LocalDate.of(2026, 1, 15), ShiftType.MORNING);
        shift4.setRequirement(Certification.CASHIER, 1);
        shift4.setRequirement(Certification.WAREHOUSE, 1);
        shift4.assignEmployee(Certification.SHIFT_MANAGER, 100000004);
        shift4.assignEmployee(Certification.CASHIER, 100000002);
        shift4.assignEmployee(Certification.WAREHOUSE, 100000003);
        shiftMemory.save(shift4);
        shiftMemory.archiveShift(1,LocalDate.of(2026, 1, 15), ShiftType.MORNING);
    }

  
    public void populateRequestMemory(RequestMemory requestMemory){

        String requestId = requestMemory.generateId(); 
        OverrideRequest request = new OverrideRequest(
        requestId,
        100000001,                                      // hrId    - Sarah Cohen
        100000002,                                     // empId   - Yossi Levi
        this.targetWednesday,        
        ShiftType.MORNING,                                    // shift - matches shift2
        Certification.CASHIER                                // role - Yossi is certified as cashier
        );
        requestMemory.save(request);
        
    }
    
    private void populateTestData(UserMemory u, EmployeeMemory e, ShiftMemory s, RequestMemory r, DriverRepository d) {
        populateUserMmemory(u);
        populateEmployeeMemory(e, d);
        populateShiftMemory(s);
        populateRequestMemory(r);
    }

    public AuthService getAuthService(){
        return this.authService;
    }

    public PersonnelService getPersonnelService(){
        return this.PersonnelService;
    }

    public SchedulingService getSchedulingService(){
        return this.schedulingService;
    }

    public TransportService getTransportService() {
        return this.transportService;
    }
    private void populateTransportData(TransportController transportController) {
        Driver driver = new Driver(
                100000006,
                "David Driver",
                100006,
                LocalDate.of(2024, 1, 1),
                EmpType.FULL_TIME,
                SalType.GLOBAL,
                11000,
                18,
                true,
                1,
                false,
                new HashSet<>(List.of(Certification.DRIVER)),
                LicenseType.B
        );
        transportController.addDriver(driver);

        Site site1 = new Site(
                1,
                "Beer Sheva Store Branch",
                "Bar Nisan 6",
                "0587243922",
                "Alex Roso",
                SiteType.BRANCH,
                new DeliveryZone(1, "South Zone 101")
        );

        Site site2 = new Site(
                2,
                "Dimona Store Branch",
                "Rager 6",
                "0587243922",
                "Bar Bussani",
                SiteType.BRANCH,
                new DeliveryZone(1, "South Zone 102")
        );

        Site site3 = new Site(
                3,
                "Tnuva Dairy Supplier",
                "Avraham Avinu 10",
                "0587243922",
                "Omer Biton",
                SiteType.SUPPLIER,
                new DeliveryZone(1, "South Zone 103")
        );

        transportController.addSite(site1);
        transportController.addSite(site2);
        transportController.addSite(site3);

        Truck truck = new Truck(
                "123-45-678",
                "Volvo FL Series", 
                3000.0,
                8000.0,
                LicenseType.B
        );
        transportController.addTruck(truck);

        List<TransportedItem> itemsForDoc1 = new ArrayList<>();
        itemsForDoc1.add(new TransportedItem(501, "Milk 3%", 100));
        itemsForDoc1.add(new TransportedItem(502, "White Bread", 50));

        List<TransportedItem> itemsForDoc2 = new ArrayList<>();
        itemsForDoc2.add(new TransportedItem(503, "Yellow Cheese", 30));

        List<TransportedItem> itemsForDoc3 = new ArrayList<>();
        itemsForDoc3.add(new TransportedItem(504, "Empty Crates", 80));

        DeliveryDocument dd1 = new DeliveryDocument(1001, site1, itemsForDoc1);
        DeliveryDocument dd2 = new DeliveryDocument(1002, site2, itemsForDoc2);
        DeliveryDocument dd3 = new DeliveryDocument(1003, site3, itemsForDoc3);

        List<DeliveryDocument> ddList = new ArrayList<>();
        ddList.add(dd1);
        ddList.add(dd2);
        //ddList.add(dd3);

        Delivery delivery = new Delivery(
                1,
                this.targetTuesday,   
                LocalTime.of(8, 30),        
                4000.0,                     
                DeliveryStatus.READY,
                site3,                      
                truck,
                driver,
                ddList
        );

        transportController.createDelivery(delivery);     
    }
}

