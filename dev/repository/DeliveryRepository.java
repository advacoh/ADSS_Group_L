package repository;

import dataAccess.transportation.*;
import domain.transportation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeliveryRepository {

    private final DeliveryMapper deliveryMapper;
    private final DeliveryDocumentMapper documentMapper;
    private final TransportedItemMapper itemMapper;

    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final SiteRepository siteRepository;

    public DeliveryRepository(DriverRepository driverRepository,
                              TruckRepository truckRepository,
                              SiteRepository siteRepository) {
        this.deliveryMapper = new DeliveryMapper();
        this.documentMapper = new DeliveryDocumentMapper();
        this.itemMapper = new TransportedItemMapper();

        this.driverRepository = driverRepository;
        this.truckRepository = truckRepository;
        this.siteRepository = siteRepository;
    }

    public DeliveryRepository(DriverRepository driverRepository,
                              TruckRepository truckRepository,
                              SiteRepository siteRepository,
                              String connectionString) {
        this.deliveryMapper = new DeliveryMapper(connectionString);
        this.documentMapper = new DeliveryDocumentMapper(connectionString);
        this.itemMapper = new TransportedItemMapper(connectionString);

        this.driverRepository = driverRepository;
        this.truckRepository = truckRepository;
        this.siteRepository = siteRepository;
    }


    public void addDelivery(Delivery delivery) {
        DeliveryDTO dto = new DeliveryDTO(
                delivery.getId(),
                delivery.getDate(),
                delivery.getDepartureTime(),
                delivery.getRecordedWeight(),
                delivery.getStatus(),
                delivery.getSource().getId(),
                delivery.getTruck().getLicenseNumber(),
                delivery.getDriver().getId(),
                delivery.getCurrentStep(),
                delivery.getPendingReason()
        );

        deliveryMapper.insert(dto);

        for (DeliveryDocument doc : delivery.getDocuments()) {
            DeliveryDocumentDTO docDTO = new DeliveryDocumentDTO(
                    doc.getDocumentId(),
                    delivery.getId(),
                    doc.getDestination().getId()
            );

            documentMapper.insert(docDTO);

            for (TransportedItem item : doc.getItems()) {
                TransportedItemDTO itemDTO = new TransportedItemDTO(
                        item.getItemId(),
                        doc.getDocumentId(),
                        item.getItemName(),
                        item.getQuantity()
                );

                itemMapper.insert(itemDTO);
            }
        }
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryMapper.selectAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public Delivery getDeliveryById(int id) {
        DeliveryDTO dto = deliveryMapper.selectById(id);
        if (dto == null) return null;
        return toDomain(dto);
    }
    public void updateDelivery(Delivery delivery) {
        DeliveryDTO dto = new DeliveryDTO(
                delivery.getId(),
                delivery.getDate(),
                delivery.getDepartureTime(),
                delivery.getRecordedWeight(),
                delivery.getStatus(),
                delivery.getSource().getId(),
                delivery.getTruck().getLicenseNumber(),
                delivery.getDriver().getId(),
                delivery.getCurrentStep(),
                delivery.getPendingReason()
        );

        deliveryMapper.update(dto);

        documentMapper.deleteByDeliveryId(delivery.getId());

        for (DeliveryDocument doc : delivery.getDocuments()) {
            documentMapper.insert(new DeliveryDocumentDTO(
                    doc.getDocumentId(),
                    delivery.getId(),
                    doc.getDestination().getId()
            ));

            itemMapper.deleteByDocumentId(doc.getDocumentId());

            for (TransportedItem item : doc.getItems()) {
                itemMapper.insert(new TransportedItemDTO(
                        item.getItemId(),
                        doc.getDocumentId(),
                        item.getItemName(),
                        item.getQuantity()
                ));
            }
        }
    }

    private Delivery toDomain(DeliveryDTO dto) {
        Site source = siteRepository.getSiteById(dto.getSourceSiteId());
        Truck truck = truckRepository.getTruckByLicenseNumber(dto.getTruckLicenseNumber());
        Driver driver = driverRepository.getDriverById(dto.getDriverId());

        List<DeliveryDocument> documents = new ArrayList<>();

        List<DeliveryDocumentDTO> documentDTOs =
                documentMapper.selectByDeliveryId(dto.getId());

        for (DeliveryDocumentDTO docDTO : documentDTOs) {
            Site destination = siteRepository.getSiteById(docDTO.getDestinationSiteId());

            List<TransportedItem> items = itemMapper
                    .selectByDocumentId(docDTO.getDocumentId())
                    .stream()
                    .map(itemDTO -> new TransportedItem(
                            itemDTO.getItemId(),
                            itemDTO.getItemName(),
                            itemDTO.getQuantity()
                    ))
                    .collect(Collectors.toList());

            documents.add(new DeliveryDocument(
                    docDTO.getDocumentId(),
                    destination,
                    items
            ));
        }

        Delivery delivery = new Delivery(
                dto.getId(),
                dto.getDate(),
                dto.getDepartureTime(),
                dto.getRecordedWeight(),
                dto.getStatus(),
                source,
                truck,
                driver,
                documents
        );

        delivery.setPendingReason(dto.getPendingReason());

        return delivery;
    }
}