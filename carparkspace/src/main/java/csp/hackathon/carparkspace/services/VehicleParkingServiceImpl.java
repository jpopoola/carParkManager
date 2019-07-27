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

    public boolean ParkCar(){
        try {
            String nextEvent = generateBarrierEvent();
            vehicleParkingProducer.sendMessage(rawSourceTopic, nextEvent);
            System.out.println(nextEvent);
        }catch (JsonProcessingException ex){
            System.out.println(ex);
        }
        return true;
    }

    private String generateBarrierEvent() throws JsonProcessingException {
        BarrierEvent event = new BarrierEvent();
        event.setBarrierId("1");
        event.setBarrierType("General");
        event.setEntry(true);
        event.setCarParkId("1");
        //event.setTimestamp(LocalDateTime.now());
        return objectMapper.writeValueAsString(event);
    }
}
