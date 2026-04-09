package domain;

public class DeliveryZone {

    private int zoneId;
    private String zoneName;

    public DeliveryZone(int zoneId, String zoneName) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
    }

    public int getZoneId() {
        return zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }
}