package domain.hr;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestMemory {
    private final Map<String, OverrideRequest> requests;
    private int counter;

    public RequestMemory(){
        this.requests = new HashMap<>();
        this.counter = 0;
    }

    public RequestMemory(Map<String, OverrideRequest> requests, int counter){
        this.requests = requests;
        this.counter = counter;
    }

    public void save(OverrideRequest request) {
        requests.put(request.getId(), request);
    }

    public OverrideRequest get(String requestId) {
        OverrideRequest request = requests.get(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Request " + requestId + " not found");
        }
        return request;
    }

    public List<OverrideRequest> getByEmployee(int empId) {
        List<OverrideRequest> result = new ArrayList<>();
        for (OverrideRequest r : requests.values()) {
            if (r.getEmpId() == empId) result.add(r);
        }
        return result;
    }

    public List<OverrideRequest> getByHR(int hrId) {
        List<OverrideRequest> result = new ArrayList<>();
        for (OverrideRequest r : requests.values()) {
            if (r.getHrId() == hrId) result.add(r);
        }
        return result;
    }

    public String generateId() {
        return "REQ_" + (++counter);
    }
}