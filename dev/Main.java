import domain.*;
import enums.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        TransportController controller = new TransportController();

        DeliveryZone southZone = new DeliveryZone(1, "South");

        Site supplier1 = new Site(
                1,
                "Tnuva Supplier",
                "Industrial Area, Ashdod",
                "08-1234567",
                "Dana Levi",
                SiteType.SUPPLIER,
                southZone
        );

        Site branch1 = new Site(
                2,
                "Beer Sheva Branch",
                "Rager Blvd 10, Beer Sheva",
                "08-7654321",
                "Yossi Cohen",
                SiteType.BRANCH,
                southZone
        );

        Site branch2 = new Site(
                3,
                "Dimona Branch",
                "Herzl 5, Dimona",
                "08-1112222",
                "Maya David",
                SiteType.BRANCH,
                southZone
        );

        controller.addSite(supplier1);
        controller.addSite(branch1);
        controller.addSite(branch2);

        Truck truck1 = new Truck(
                "123-45-678",
                "Volvo FH",
                8000,
                15000,
                LicenseType.C
        );

        Driver driver1 = new Driver(
                "206000001",
                "David Levi",
                LicenseType.C
        );
        Driver driver2 = new Driver(
                "206000002",
                "Sarah Cohen",
                LicenseType.B
        );

        controller.addTruck(truck1);
        controller.addDriver(driver1);
        controller.addDriver(driver2);

        TransportedItem milk = new TransportedItem(1, "Milk 3%", 100);
        TransportedItem bread = new TransportedItem(2, "Bread", 50);
        TransportedItem cheese = new TransportedItem(3, "Cheese", 30);

        List<TransportedItem> itemsForBranch1 = new ArrayList<>();
        itemsForBranch1.add(milk);
        itemsForBranch1.add(bread);

        List<TransportedItem> itemsForBranch2 = new ArrayList<>();
        itemsForBranch2.add(cheese);

        DeliveryDocument document1 = new DeliveryDocument(1001, branch1, itemsForBranch1);
        DeliveryDocument document2 = new DeliveryDocument(1002, branch2, itemsForBranch2);

        List<DeliveryDocument> documents = new ArrayList<>();
        documents.add(document1);
        documents.add(document2);

        Delivery delivery1 = new Delivery(
                1,
                LocalDate.now(),
                LocalTime.of(8, 30),
                14000,
                DeliveryStatus.PLANNED,
                supplier1,
                truck1,
                driver1,
                documents
        );
        Delivery invalidDelivery = new Delivery(
                2,
                LocalDate.now(),
                LocalTime.of(10, 0),
                12000,
                DeliveryStatus.PLANNED,
                supplier1,
                truck1,
                driver2,
                documents
    );

        boolean created = controller.createDelivery(delivery1);
        boolean createdInvalid = controller.createDelivery(invalidDelivery);

        if (createdInvalid) {
            System.out.println("Invalid delivery created successfully.");
        } else {
            System.out.println("Invalid delivery creation failed as expected.");
        }
        if (created) {
            System.out.println("Delivery created successfully.");
        } else {
            System.out.println("Delivery creation failed.");
        }

        System.out.println();
        System.out.println("All deliveries in the system:");

        for (Delivery delivery : controller.getAllDeliveries()) {
            System.out.println("Delivery ID: " + delivery.getId());
            System.out.println("Date: " + delivery.getDate());
            System.out.println("Departure Time: " + delivery.getDepartureTime());
            System.out.println("Source: " + delivery.getSource().getName());
            System.out.println("Driver: " + delivery.getDriver().getName());
            System.out.println("Truck: " + delivery.getTruck().getLicenseNumber());
            System.out.println("Recorded Weight: " + delivery.getRecordedWeight());
            System.out.println("Status: " + delivery.getStatus());

            System.out.println("Destinations:");
            for (DeliveryDocument document : delivery.getDocuments()) {
                System.out.println("  Document ID: " + document.getDocumentId());
                System.out.println("  Destination: " + document.getDestination().getName());
                System.out.println("  Items:");

                for (TransportedItem item : document.getItems()) {
                    System.out.println("    - " + item.getItemName() + " | Quantity: " + item.getQuantity());
                }
            }

            System.out.println("---------------------------");
        }
    }
}