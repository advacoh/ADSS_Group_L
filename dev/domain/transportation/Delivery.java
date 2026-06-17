package domain.transportation;

import enums.DeliveryStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
}