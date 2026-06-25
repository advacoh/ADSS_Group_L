package repository;

import java.util.List;
import java.util.stream.Collectors;

import dataAccess.transportation.SiteDTO;
import dataAccess.transportation.SiteMapper;
import domain.transportation.DeliveryZone;
import domain.transportation.Site;

public class SiteRepository {

    private final SiteMapper siteMapper;

    public SiteRepository() {
        this.siteMapper = new SiteMapper();
    }

    public SiteRepository(String connectionString) {
        this.siteMapper = new SiteMapper(connectionString);
    }

    public void addSite(Site site) {
        if (siteExists(site.getId())) {
            return;
        }

        SiteDTO dto = new SiteDTO(
                site.getId(),
                site.getName(),
                site.getAddress(),
                site.getPhoneNumber(),
                site.getContactPerson(),
                site.getSiteType(),
                site.getDeliveryZone().getZoneId(),
                site.getDeliveryZone().getZoneName()
        );

        siteMapper.insert(dto);
    }

    public List<Site> getAllSites() {
        return siteMapper.selectAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public Site getSiteById(int id) {
        SiteDTO dto = siteMapper.selectById(id);
        if (dto == null) return null;
        return toDomain(dto);
    }

    public boolean siteExists(int id) {
        return siteMapper.selectById(id) != null;
    }

    private Site toDomain(SiteDTO dto) {
        DeliveryZone zone = new DeliveryZone(
                dto.getZoneId(),
                dto.getZoneName()
        );

        return new Site(
                dto.getId(),
                dto.getName(),
                dto.getAddress(),
                dto.getPhoneNumber(),
                dto.getContactPerson(),
                dto.getSiteType(),
                zone
        );
    }
}