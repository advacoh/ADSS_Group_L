package service;

import domain.transportation.Site;
import enums.SiteType;

public class SiteSL {
    private final int id;
    private final String name;
    private final String address;
    private final String phoneNumber;
    private final String contactPerson;
    private final SiteType siteType;
    private final DeliveryZoneSL deliveryZone;

    public SiteSL(Site site) {
        this.id = site.getId();
        this.name = site.getName();
        this.address = site.getAddress();
        this.phoneNumber = site.getPhoneNumber();
        this.contactPerson = site.getContactPerson();
        this.siteType = site.getSiteType();
        this.deliveryZone = new DeliveryZoneSL(site.getDeliveryZone());
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getContactPerson() { return contactPerson; }
    public SiteType getSiteType() { return siteType; }
    public DeliveryZoneSL getDeliveryZone() { return deliveryZone; }

    public String shortString() {
        return id + " | " + name + " | " + siteType;
    }

    @Override
    public String toString() {
        return "Site ID: " + id +
                "\nName: " + name +
                "\nAddress: " + address +
                "\nPhone Number: " + phoneNumber +
                "\nContact Person: " + contactPerson +
                "\nSite Type: " + siteType +
                "\nDelivery Zone: " + deliveryZone.shortString();
    }
}