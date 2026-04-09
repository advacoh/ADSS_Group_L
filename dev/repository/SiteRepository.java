package repository;

import domain.Site;

import java.util.ArrayList;
import java.util.List;

public class SiteRepository {

    private List<Site> sites;

    public SiteRepository() {
        this.sites = new ArrayList<>();
    }

    public void addSite(Site site) {
        sites.add(site);
    }

    public List<Site> getAllSites() {
        return sites;
    }
}