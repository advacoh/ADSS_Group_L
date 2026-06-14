package domain;

import enums.LicenseType;
import java.time.LocalDate;
import java.util.*;


public class Driver extends Employee {

    private LicenseType licenseType;

    public Driver(int ID, String name, int bankAccount, LocalDate startDate, 
                  EmpType employementType, SalType salaryType, int salary, 
                  int vacation, boolean willOvertime, int dayOff, boolean doubleShiftAllowed, 
                  Set<Certification> certifications, LicenseType licenseType) {
        
        super(ID, name, bankAccount, startDate, employementType, salaryType, salary, 
              vacation, willOvertime, dayOff, doubleShiftAllowed, certifications);
        
        this.licenseType = licenseType;
        this.addCertification(Certification.DRIVER);
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }

    public int getId() {
        return super.getID();
    }
}