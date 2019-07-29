package csp.hackathon.carparkspace.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import csp.hackathon.carparkspace.domain.BarrierEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VehicleParkingServiceImpl implements VehicleParkingService {
    @Autowired
    private VehicleParkingProducer vehicleParkingProducer;

    @Value("${car.park.barrier.event.topic}")
    private String rawSourceTopic;

    private ObjectMapper objectMapper = new ObjectMapper();

    public boolean ParkCar(String barrierType, boolean isEntry){
        try {
            String nextEvent = generateBarrierEvent(barrierType, getBarrierId(barrierType, isEntry), isEntry);
            vehicleParkingProducer.sendMessage(rawSourceTopic, nextEvent);
            System.out.println(nextEvent);
        }catch (JsonProcessingException ex){
            System.out.println(ex);
        }
        return true;
    }

    private String generateBarrierEvent(String barrierType, String barrierId, boolean isEntry) throws JsonProcessingException {
        BarrierEvent event = new BarrierEvent();
        event.setBarrierId(barrierId);
        event.setBarrierType(barrierType);
        event.setEntry(isEntry);
        event.setCarParkId("Sky-Parking");
        //event.setTimestamp(LocalDateTime.now());
        return objectMapper.writeValueAsString(event);
    }

    private String getBarrierId(String type, boolean isEntry){
        switch(type){
            case "General":
                if(isEntry)
                    return "General-Entry";
                else
                    return "General-Exit";
            case "Shift":
                if(isEntry)
                    return "Shift-Entry";
                else
                    return "Shift-Exit";
            case "Reserved":
                if(isEntry)
                    return "Reserved-Entry";
                else
                    return "Reserved-Exit";
        }

        return "Not-Identifiable";
    }
}
