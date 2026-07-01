package service;

import domain.transportation.DeliveryZone;

public class DeliveryZoneSL {
    private final int zoneId;
    private final String zoneName;

    public DeliveryZoneSL(DeliveryZone deliveryZone) {
        this.zoneId = deliveryZone.getZoneId();
        this.zoneName = deliveryZone.getZoneName();
    }

    public int getZoneId() { return zoneId; }
    public String getZoneName() { return zoneName; }

    public String shortString() {
        return zoneId + " | " + zoneName;
    }

    @Override
    public String toString() {
        return "Zone ID: " + zoneId +
                "\nZone Name: " + zoneName;
    }
}