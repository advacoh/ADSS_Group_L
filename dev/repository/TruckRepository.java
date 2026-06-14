package repository;

import domain.transportation.Truck;
import java.util.ArrayList;
import java.util.List;

public class TruckRepository {

    private List<Truck> trucks;

    public TruckRepository() {
        this.trucks = new ArrayList<>();
    }

    public void addTruck(Truck truck) {
        trucks.add(truck);
    }

    public List<Truck> getAllTrucks() {
        return trucks;
    }
    
    public Truck getTruckByLicenseNumber(String licenseNumber) {
        for (Truck truck : trucks) {
            if (truck.getLicenseNumber().equals(licenseNumber)) {
                return truck;
            }
        }
        return null;
    }
}