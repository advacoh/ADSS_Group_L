package dataAccess.transportation;

import enums.SiteType;

public class SiteDTO {
    private final int id;
    private final String name;
    private final String address;
    private final String phoneNumber;
    private final String contactPerson;
    private final SiteType siteType;
    private final int zoneId;
    private final String zoneName;

    public SiteDTO(int id, String name, String address, String phoneNumber,
                   String contactPerson, SiteType siteType, int zoneId, String zoneName) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactPerson = contactPerson;
        this.siteType = siteType;
        this.zoneId = zoneId;
        this.zoneName = zoneName;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getContactPerson() { return contactPerson; }
    public SiteType getSiteType() { return siteType; }
    public int getZoneId() { return zoneId; }
    public String getZoneName() { return zoneName; }
}