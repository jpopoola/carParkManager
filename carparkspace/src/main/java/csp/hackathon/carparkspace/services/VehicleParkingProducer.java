package csp.hackathon.carparkspace.services;

import csp.hackathon.carparkspace.domain.BarrierEvent;

public interface VehicleParkingProducer {
    void sendMessage(String topic, String message);
}
