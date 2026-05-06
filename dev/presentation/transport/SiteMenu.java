package presentation.transport;

import service.TransportService;
import service.Response;
import service.SiteSL;

import java.util.List;

public class SiteMenu {
    private final TransportService transportService;

    public SiteMenu(TransportService transportService) {
        this.transportService = transportService;
    }

    public void start() {
        System.out.println("\n=== Sites ===");

        List<SiteSL> sites = transportService.getAllSites();

        if (sites.isEmpty()) {
            System.out.println("No sites found.");
            return;
        }

        for (SiteSL site : sites) {
            System.out.println(site);
            System.out.println("--------------------");
        }
    }
}