package repository;

import domain.Truck;

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
}