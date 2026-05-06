package presentation.transport;

import service.TransportService;
import service.Response;
import service.DeliverySL;

import java.util.List;

public class DeliveryMenu {
    private final TransportService transportService;

    public DeliveryMenu(TransportService transportService) {
        this.transportService = transportService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Delivery Menu ===");
            System.out.println("1. Show all deliveries");
            System.out.println("0. Back");

            int choice = presentation.InputUtil.readInt("Choose option: ");

            switch (choice) {
                case 1:
                    showAllDeliveries();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void showAllDeliveries() {
        List<DeliverySL> deliveries = transportService.getAllDeliveries();

        if(deliveries.isEmpty()) {
            System.out.println("No deliveries found.");
            return;
        }
        for(DeliverySL delivery : deliveries) {
            System.out.println(delivery);
            System.out.println("--------------------");
        }
    }
}