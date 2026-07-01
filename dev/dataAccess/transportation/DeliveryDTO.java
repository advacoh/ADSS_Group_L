package dataAccess.transportation;

import enums.DeliveryStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class DeliveryDTO {
    private final int id;
    private final LocalDate date;
    private final LocalTime departureTime;
    private final double recordedWeight;
    private final DeliveryStatus status;
    private final int sourceSiteId;
    private final String truckLicenseNumber;
    private final int driverId;
    private final int currentStep;
    private final String pendingReason;

    public DeliveryDTO(int id, LocalDate date, LocalTime departureTime,
                       double recordedWeight, DeliveryStatus status,
                       int sourceSiteId, String truckLicenseNumber,
                       int driverId, int currentStep, String pendingReason) {
        this.id = id;
        this.date = date;
        this.departureTime = departureTime;
        this.recordedWeight = recordedWeight;
        this.status = status;
        this.sourceSiteId = sourceSiteId;
        this.truckLicenseNumber = truckLicenseNumber;
        this.driverId = driverId;
        this.currentStep = currentStep;
        this.pendingReason = pendingReason;
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public LocalTime getDepartureTime() { return departureTime; }
    public double getRecordedWeight() { return recordedWeight; }
    public DeliveryStatus getStatus() { return status; }
    public int getSourceSiteId() { return sourceSiteId; }
    public String getTruckLicenseNumber() { return truckLicenseNumber; }
    public int getDriverId() { return driverId; }
    public int getCurrentStep() { return currentStep; }
    public String getPendingReason() { return pendingReason; }
}