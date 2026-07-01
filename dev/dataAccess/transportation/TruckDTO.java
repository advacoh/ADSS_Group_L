package dataAccess.transportation;

import enums.LicenseType;

public class TruckDTO {

    private final String licenseNumber;
    private final String model;
    private final double netWeight;
    private final double maxCapacityWeight;
    private final LicenseType requiredLicenseType;

    public TruckDTO(
            String licenseNumber,
            String model,
            double netWeight,
            double maxCapacityWeight,
            LicenseType requiredLicenseType
    ) {
        this.licenseNumber = licenseNumber;
        this.model = model;
        this.netWeight = netWeight;
        this.maxCapacityWeight = maxCapacityWeight;
        this.requiredLicenseType = requiredLicenseType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getModel() {
        return model;
    }

    public double getNetWeight() {
        return netWeight;
    }

    public double getMaxCapacityWeight() {
        return maxCapacityWeight;
    }

    public LicenseType getRequiredLicenseType() {
        return requiredLicenseType;
    }
}