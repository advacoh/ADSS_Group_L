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
            System.out.println("\n=== Site Menu ===");
            System.out.println("1) Add Supplier Site");
            System.out.println("2) Add Branch Site");
            System.out.println("3) View all sites");
            System.out.println("4) View site details");
            System.out.println("5) Back");

            switch (InputUtil.readInt("Choose option: ")) {
                case 1 -> addSite(SiteType.SUPPLIER);
                case 2 -> addSite(SiteType.BRANCH);
                case 3 -> viewAllSites();
                case 4 -> viewSiteDetails();
                case 5 -> { return; }
                default -> System.out.println("Invalid option.");
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

    private void addSite(SiteType siteType) {
        System.out.println("\n--- Add " + (siteType == SiteType.SUPPLIER ? "Supplier" : "Branch") + " Site ---");

        int id = InputUtil.readInt("Enter site ID: ");
        String name = InputUtil.readString("Enter site name: ");
        String address = InputUtil.readString("Enter address: ");
        String phoneNumber = InputUtil.readString("Enter phone number: ");
        String contactPerson = InputUtil.readString("Enter contact person: ");
        
        int zoneId = InputUtil.readInt("Enter delivery zone ID: ");
        String zoneName = InputUtil.readString("Enter delivery zone name: ");

        boolean isAdded = transportService.addSite(id, name, address, phoneNumber, contactPerson, siteType, zoneId, zoneName);
        if (isAdded) {
            System.out.println("Site added successfully!");
        } else {
            System.out.println("Error: A site with this ID already exists.");
        }
    }
}