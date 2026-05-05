package service;

import domain.TransportController;
import domain.Delivery;
import domain.Driver;
import domain.Truck;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TransportService {

    private TransportController transportController;

    public TransportService(TransportController transportController) {
        this.transportController = transportController;
    }


}