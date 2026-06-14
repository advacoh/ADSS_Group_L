package repository;

import domain.transportation.Delivery;
import java.util.ArrayList;
import java.util.List;

public class DeliveryRepository {

    private List<Delivery> deliveries;

    public DeliveryRepository() {
        this.deliveries = new ArrayList<>();
    }

    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
    }

    public List<Delivery> getAllDeliveries() {
        return deliveries;
    }
    
    public Delivery getDeliveryById(int id) {
        for (Delivery delivery : deliveries) {
            if (delivery.getId() == id) {
                return delivery;
            }
        }
        return null;
    }
}