package repository;

import dataAccess.hr.EmployeeDTO;
import dataAccess.hr.EmployeeMapper;
import dataAccess.hr.WeeklySubmissionDTO;
import dataAccess.transportation.DriverDTO;
import dataAccess.transportation.DriverMapper;
import domain.hr.Certification;
import domain.hr.EmpType;
import domain.hr.SalType;
import domain.transportation.Driver;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public class DriverRepository {

    private final DriverMapper driverMapper;
    private final EmployeeMapper employeeMapper;

    public DriverRepository() {
        String path = new File("supermarket.db").getAbsolutePath();
        String connectionString = "jdbc:sqlite:" + path;

        this.driverMapper = new DriverMapper();
        this.employeeMapper = new EmployeeMapper(connectionString);
    }

    public DriverRepository(String testConnectionString) {
        this.driverMapper = new DriverMapper(testConnectionString); 
        this.employeeMapper = new EmployeeMapper(testConnectionString);

    }



    public void addDriver(Driver driver) {
        if (driverMapper.selectById(driver.getId()) != null) {
            return;
        }

        DriverDTO dto = new DriverDTO(
                driver.getId(),
                driver.getLicenseType()
        );

        driverMapper.insert(dto);
    }

    public void removeDriver(Driver driver) {
        driverMapper.delete(driver.getId());
    }

    public Driver getDriverById(int id) {
        DriverDTO driverDTO = driverMapper.selectById(id);
        if (driverDTO == null) {
            return null;
        }

        return buildDriverFromDTO(driverDTO);
    }

    public java.util.List<Driver> getAllDrivers() {
        return driverMapper.selectAll()
                .stream()
                .map(this::buildDriverFromDTO)
                .filter(driver -> driver != null)
                .toList();
    }

    private Driver buildDriverFromDTO(DriverDTO driverDTO) {
        EmployeeDTO emp = employeeMapper.get(driverDTO.getId());

        if (emp == null) {
            return null;
        }

        WeeklySubmissionDTO weekly = emp.getWeeklySubmission();

        int dayOff = weekly != null ? weekly.getDayOff() : 7;
        boolean doubleShiftAllowed = weekly != null && weekly.isDoubleShiftAllowed();

        Set<Certification> certifications = emp.getCertifications()
                .stream()
                .map(Certification::valueOf)
                .collect(Collectors.toSet());

        Driver driver = new Driver(
                emp.getId(),
                emp.getName(),
                emp.getBankAccount(),
                emp.getStartDate(),
                EmpType.valueOf(emp.getEmploymentType()),
                SalType.valueOf(emp.getSalaryType()),
                emp.getSalary(),
                emp.getVacation(),
                emp.isWillOvertime(),
                dayOff,
                doubleShiftAllowed,
                certifications,
                driverDTO.getLicenseType()
        );

        driver.setBranchId(emp.getBranchId());

        return driver;
    }
}