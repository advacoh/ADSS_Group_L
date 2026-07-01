package domain.hr;

import dataAccess.hr.OverrideRequestDTO;
import dataAccess.hr.OverrideRequestMapper;

import java.util.ArrayList;
import java.util.List;

public class RequestMemory {
    private final OverrideRequestMapper mapper;
    private int counter;

    public RequestMemory(OverrideRequestMapper mapper) {
        this.mapper = mapper;
        this.counter = deriveCounter();
    }

    private int deriveCounter() {
        int max = 0;
        for (OverrideRequestDTO dto : mapper.getAll()) {
            String idPart = dto.getId().replace("REQ_", "");
            try {
                int value = Integer.parseInt(idPart);
                if (value > max) max = value;
            } catch (NumberFormatException e) {
                // skip malformed ids
            }
        }
        return max;
    }

    public boolean save(OverrideRequest request) {
        return mapper.save(toDTO(request));
    }

    public void update(OverrideRequest request) {
        mapper.update(toDTO(request));
    }

    public OverrideRequest get(String requestId) {
        OverrideRequestDTO dto = mapper.get(requestId);
        if (dto == null) {
            throw new IllegalArgumentException("Request " + requestId + " not found");
        }
        return toDomain(dto);
    }

    public List<OverrideRequest> getByEmployee(int empId) {
        List<OverrideRequest> result = new ArrayList<>();
        for (OverrideRequestDTO dto : mapper.getByEmployee(empId)) {
            result.add(toDomain(dto));
        }
        return result;
    }

    public List<OverrideRequest> getByHR(int hrId) {
        List<OverrideRequest> result = new ArrayList<>();
        for (OverrideRequestDTO dto : mapper.getByHR(hrId)) {
            result.add(toDomain(dto));
        }
        return result;
    }

    public String generateId() {
        return "REQ_" + (++counter);
    }

    private OverrideRequestDTO toDTO(OverrideRequest request) {
        return new OverrideRequestDTO(
                request.getId(),
                request.getHrId(),
                request.getEmpId(),
                request.getDate(),
                request.getShiftType().getValue(),
                request.getRole().name(),
                request.getStatus().name()
        );
    }

    private OverrideRequest toDomain(OverrideRequestDTO dto) {
        OverrideRequest request = new OverrideRequest(
                dto.getId(),
                dto.getHrId(),
                dto.getEmpId(),
                dto.getDate(),
                ShiftType.fromValue(dto.getShiftType()),
                Certification.valueOf(dto.getRole())
        );
        RequestStatus status = RequestStatus.valueOf(dto.getStatus());
        if (status == RequestStatus.APPROVED) {
            request.approve();
        } else if (status == RequestStatus.REJECTED) {
            request.reject();
        }
        return request;
    }
}