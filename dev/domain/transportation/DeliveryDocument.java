package domain.transportation;

import java.util.List;

public class DeliveryDocument {

    private int documentId;
    private Site destination;
    private List<TransportedItem> items;

    public DeliveryDocument(int documentId, Site destination, List<TransportedItem> items) {
        this.documentId = documentId;
        this.destination = destination;
        this.items = items;
    }

    public int getDocumentId() {
        return documentId;
    }

    public Site getDestination() {
        return destination;
    }

    public List<TransportedItem> getItems() {
        return items;
    }

    public void setDestination(Site destination) { this.destination = destination; }
}