package service;

import domain.transportation.Delivery;
import enums.DeliveryStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public class DeliverySL {
    private final int id;
    private final LocalDate date;
    private final LocalTime departureTime;
    private final double recordedWeight;
    private final DeliveryStatus status;
    private final SiteSL source;
    private final TruckSL truck;
    private final DriverSL driver;
    private final int documentsCount;
    private final String pendingReason;

    public DeliverySL(Delivery delivery) {
        this.id = delivery.getId();
        this.date = delivery.getDate();
        this.departureTime = delivery.getDepartureTime();
        this.recordedWeight = delivery.getRecordedWeight();
        this.status = delivery.getStatus();
        this.source = new SiteSL(delivery.getSource());
        this.truck = new TruckSL(delivery.getTruck());
        this.driver = new DriverSL(delivery.getDriver());
        this.documentsCount = delivery.getDocuments() == null ? 0 : delivery.getDocuments().size();
        this.pendingReason = delivery.getPendingReason();
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public LocalTime getDepartureTime() { return departureTime; }
    public double getRecordedWeight() { return recordedWeight; }
    public DeliveryStatus getStatus() { return status; }
    public SiteSL getSource() { return source; }
    public TruckSL getTruck() { return truck; }
    public DriverSL getDriver() { return driver; }
    public int getDocumentsCount() { return documentsCount; }
    public String getPendingReason() { return pendingReason; }

    public String shortString() {
        return "Delivery #" + id + " | " + date + " " + departureTime +
                " | Truck: " + truck.getLicenseNumber() +
                " | Driver: " + driver.getId() +
                " | Status: " + status;
    }

    @Override
    public String toString() {
        String base = "Delivery ID: " + id +
                "\nDate: " + date +
                "\nDeparture Time: " + departureTime +
                "\nRecorded Weight: " + recordedWeight +
                "\nStatus: " + status;

        if (status == DeliveryStatus.PENDING && pendingReason != null) {
            base += "\nPending Reason: " + pendingReason;
        }

        base += "\nSource: " + source.shortString() +
                "\nTruck: " + truck.shortString() +
                "\nDriver: " + driver.shortString() +
                "\nDocuments Count: " + documentsCount;

        return base;
    }
}