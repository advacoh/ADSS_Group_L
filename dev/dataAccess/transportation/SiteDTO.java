package dataAccess.transportation;

public class SiteDTO {
    private final int id;
    private final String name;
    private final String address;
    private final String phoneNumber;
    private final String contactPerson;
    private final String siteType; 
    private final String deliveryZone; 

    public SiteDTO(int id, String name, String address, String phoneNumber, 
                   String contactPerson, String siteType, String deliveryZone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactPerson = contactPerson;
        this.siteType = siteType;
        this.deliveryZone = deliveryZone;
    }

    // Getters
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

    public String getSiteType() {
        return siteType;
    }

    public String getDeliveryZone() {
        return deliveryZone;
    }
}