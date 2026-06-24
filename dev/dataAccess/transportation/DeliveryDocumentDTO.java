package dataAccess.transportation;

public class DeliveryDocumentDTO {

    private final int documentId;
    private final int deliveryId;
    private final int destinationSiteId;

    public DeliveryDocumentDTO(
            int documentId,
            int deliveryId,
            int destinationSiteId
    ) {
        this.documentId = documentId;
        this.deliveryId = deliveryId;
        this.destinationSiteId = destinationSiteId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public int getDestinationSiteId() {
        return destinationSiteId;
    }
}