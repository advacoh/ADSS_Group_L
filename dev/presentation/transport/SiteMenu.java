package presentation.transport;

import presentation.InputUtil;
import service.SiteSL;
import service.TransportService;
import domain.transportation.Site;
import domain.transportation.DeliveryZone;
import enums.SiteType;

import java.util.List;

public class SiteMenu {
    private final TransportService transportService;

    public SiteMenu(TransportService transportService) {
        this.transportService = transportService;
    }

    public void start() {
        while (true) {
            System.out.println("1) Add site");
            System.out.println("2) View all sites");
            System.out.println("3) View site details");
            System.out.println("4) Back");

            switch (InputUtil.readInt("Choose option: ")) {
                case 1 -> addSite();
                case 2 -> viewAllSites();
                case 3 -> viewSiteDetails();
                case 4 -> { return; }
            }
        }
    }

    private void viewAllSites() {
        List<SiteSL> sites = transportService.getAllSites();

        if (sites.isEmpty()) {
            System.out.println("No sites found.");
            return;
        }

        System.out.println("\n=== Sites ===");
        for (int i = 0; i < sites.size(); i++) {
            System.out.println((i + 1) + ") " + sites.get(i).shortString());
        }
    }

    private void viewSiteDetails() {
        List<SiteSL> sites = transportService.getAllSites();

        if (sites.isEmpty()) {
            System.out.println("No sites found.");
            return;
        }

        SiteSL selected = InputUtil.selectItem(sites);
        if (selected == null) return;

        System.out.println("\n--- Site Details ---");
        System.out.println(selected);
    }
    private void addSite() {
    System.out.println("\n--- Add Supplier Site ---");

    int id = InputUtil.readInt("Enter site ID: ");
    String name = InputUtil.readString("Enter site name: ");
    String address = InputUtil.readString("Enter address: ");
    String phoneNumber = InputUtil.readString("Enter phone number: ");
    String contactPerson = InputUtil.readString("Enter contact person: ");

    // Automatically assign the site type as SUPPLIER instead of asking the user
    SiteType siteType = SiteType.SUPPLIER; 

    int zoneId = InputUtil.readInt("Enter delivery zone ID: ");
    String zoneName = InputUtil.readString("Enter delivery zone name: ");

    DeliveryZone zone = new DeliveryZone(zoneId, zoneName);

    Site site = new Site(
            id,
            name,
            address,
            phoneNumber,
            contactPerson,
            siteType,
            zone
    );

    boolean isAdded = transportService.addSite(site);
    if (isAdded) {
        System.out.println("Supplier site added successfully!");
    } else {
        System.out.println("Error: A site with this ID already exists.");
    }
}
}