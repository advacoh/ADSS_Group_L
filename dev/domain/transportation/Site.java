package domain.transportation;

import enums.SiteType;

public class Site {

    private int id;
    private String name;
    private String address;
    private String phoneNumber;
    private String contactPerson;
    private SiteType siteType;
    private DeliveryZone deliveryZone;

    public Site(
            int id,
            String name,
            String address,
            String phoneNumber,
            String contactPerson,
            SiteType siteType,
            DeliveryZone deliveryZone
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactPerson = contactPerson;
        this.siteType = siteType;
        this.deliveryZone = deliveryZone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public SiteType getSiteType() {
        return siteType;
    }

    public DeliveryZone getDeliveryZone() {
        return deliveryZone;
    }
}