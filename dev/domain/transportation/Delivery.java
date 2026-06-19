package domain.transportation;

import enums.DeliveryStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class Delivery {
    private int id;
    private LocalDate date;
    private LocalTime departureTime;
    private double recordedWeight;
    private DeliveryStatus status;
    private Site source;
    private Truck truck;
    private Driver driver;
    private List<DeliveryDocument> documents;
    private int currentStep = 0; // Default starts at 0
    private String pendingReason;

    public Delivery(
            int id,
            LocalDate date,
            LocalTime departureTime,
            double recordedWeight,
            DeliveryStatus status,
            Site source,
            Truck truck,
            Driver driver,
            List<DeliveryDocument> documents
    ) {
        this.id = id;
        this.date = date;
        this.departureTime = departureTime;
        this.recordedWeight = recordedWeight;
        this.status = status;
        this.source = source;
        this.truck = truck;
        this.driver = driver;
        this.documents = documents;
    }

    public List<Integer> getBranches() {
        return documents.stream()
                .map(DeliveryDocument::getDestination)   // 1. Extract destination site from each document
                .filter(Objects::nonNull)
                .filter(Site::isBranch)                  // 2. Only keep sites that are branches
                .map(Site::getId)                        // 3. Extract the branch ID
                .distinct()                              // 5. Deduplicate if multiple documents go to the same branch
                .toList();
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public double getRecordedWeight() {
        return recordedWeight;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public Site getSource() {
        return source;
    }

    public Truck getTruck() {
        return truck;
    }

    public Driver getDriver() {
        return driver;
    }
    public List<DeliveryDocument> getDocuments() {
        return documents;
    }
    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public void setTruck(Truck truck) { 
        this.truck = truck; 
    }

    public void setRecordedWeight(double recordedWeight) {
        this.recordedWeight = recordedWeight;
    }

    public int getCurrentStep() { 
        return currentStep; 
    }

    public void incrementStep() { 
        this.currentStep++; 
    }

    public String getPendingReason() {
        return pendingReason;
    }

    public void setPendingReason(String pendingReason) {
        this.pendingReason = pendingReason;
    }

    public void setDate(LocalDate date) {
        this.date = date; 
    }

    public void setDepartureTime(LocalTime departureTime) { 
        this.departureTime = departureTime; 
    }

    public void setSource(Site source) { 
        this.source = source; 
    }

    public void setDriver(Driver driver) { 
        this.driver = driver; 
    }

    public void setDocuments(List<DeliveryDocument> documents) { 
        this.documents = documents; 
    }

}