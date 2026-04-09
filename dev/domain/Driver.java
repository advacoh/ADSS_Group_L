package domain;

import enums.LicenseType;

public class Driver {

    private String id;
    private String name;
    private LicenseType licenseType;

    public Driver(String id, String name, LicenseType licenseType) {
        this.id = id;
        this.name = name;
        this.licenseType = licenseType;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LicenseType getLicenseType() {
        return licenseType;
    }
}