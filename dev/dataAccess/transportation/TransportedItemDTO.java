package dataAccess.transportation;

public class TransportedItemDTO {

    private final int itemId;
    private final int documentId;
    private final String itemName;
    private final int quantity;

    public TransportedItemDTO(
            int itemId,
            int documentId,
            String itemName,
            int quantity
    ) {
        this.itemId = itemId;
        this.documentId = documentId;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }
}