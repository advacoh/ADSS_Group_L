package repository;

import java.util.List;
import java.util.stream.Collectors;

import dataAccess.transportation.TruckDTO;
import dataAccess.transportation.TruckMapper;
import domain.transportation.Truck;

public class TruckRepository {
    private final TruckMapper truckMapper;

    public TruckRepository() {
        this.truckMapper = new TruckMapper();
    }

    public TruckRepository(String connectionString) {
        this.truckMapper = new TruckMapper(connectionString);
    }

    public void addTruck(Truck truck) {
        TruckDTO dto = new TruckDTO(
                truck.getLicenseNumber(),
                truck.getModel(),
                truck.getNetWeight(),
                truck.getMaxCapacityWeight(),
                truck.getRequiredLicenseType()
        );

        truckMapper.insert(dto);
    }

    public Truck getTruckByLicenseNumber(String licenseNumber) {
        TruckDTO dto = truckMapper.selectByLicenseNumber(licenseNumber);
        if (dto == null) return null;
        return toDomain(dto);
    }

    public List<Truck> getAllTrucks() {
        return truckMapper.selectAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public boolean deleteTruck(String licenseNumber) {
        return truckMapper.delete(licenseNumber);
    }

    private Truck toDomain(TruckDTO dto) {
        return new Truck(
                dto.getLicenseNumber(),
                dto.getModel(),
                dto.getNetWeight(),
                dto.getMaxCapacityWeight(),
                dto.getRequiredLicenseType()
        );
    }
}