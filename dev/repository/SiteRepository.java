package repository;

import domain.transportation.DeliveryZone;
import domain.transportation.Site;
import enums.SiteType;

import java.util.ArrayList;
import java.util.List;

public class SiteRepository {

    private List<Site> sites;

    public SiteRepository() {
        this.sites = new ArrayList<>();
        DeliveryZone southZone = new DeliveryZone(1, "South");
        Site branch = new Site(
                 1,
                "Beer Sheva Branch",
               "Rager Blvd 10, Beer Sheva",
                "08-7654321",
              "Yossi Cohen",
                 SiteType.BRANCH,
                southZone
            );
            Site branch2 = new Site(
                 2,
                "Beer Sheva Branch",
               "Rager Blvd 10, Beer Sheva",
                "08-7654321",
              "Yossi Cohen",
                 SiteType.BRANCH,
                southZone
            );
            sites.add(branch);
            sites.add(branch2);
        }
        

    public void addSite(Site site) {
        sites.add(site);
    }

    public List<Site> getAllSites() {
        return sites;
    }
    
    public Site getSiteById(int id) {
        for (Site site : sites) {
            if (site.getId() == id) {
                return site;
            }
        }
        return null;
    }

    public boolean siteExists(int id) {
        return getSiteById(id) != null;
    }
}