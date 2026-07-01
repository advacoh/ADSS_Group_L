package dataAccess.transportation;

import enums.LicenseType;

public class DriverDTO {

    private final int id;
    private final LicenseType licenseType;

    public DriverDTO(int id, LicenseType licenseType) {
        this.id = id;
        this.licenseType = licenseType;
    }

    public int getId() {
        return id;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }
}