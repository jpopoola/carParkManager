package csp.hackathon.carparkspacemanager.streams;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import csp.hackathon.carparkspacemanager.config.TopicsConfiguration;
import csp.hackathon.carparkspacemanager.domian.BarrierEvent;
import csp.hackathon.carparkspacemanager.domian.CarParkCapacity;
import csp.hackathon.carparkspacemanager.domian.CarParkStatus;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class CarParkSpaceManagerStreams {

    @Autowired
    private TopicsConfiguration topicsConfiguration;

    @Value("${car.park.store}")
    private String carParkZoneStoreName;

    @Value("${car.park.zone.capacity.SHIFT}")
    private String shiftCapacity;

    @Value("${car.park.zone.capacity.RESERVED}")
    private String reservedCapacity;

    @Value("${car.park.zone.capacity.GENERAL}")
    private String generalCapacity;

    @Value("${kafka.bootstrap.servers}")
    private String brokerUrl;

    @Value("${kafka.application.id}")
    private String applicationId;

    @Value("${kafka.auto.offset.reset}")
    private String autoOffsetReset;

    private KafkaStreams streams;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, CarParkCapacity> currentCarParkStatus = new HashMap<>();
    @PostConstruct
    private void SetUp(){
         System.out.println("rawSourceTopic " + topicsConfiguration.getBarrierEventTopic());
         System.out.println("carParkZoneStoreName " + carParkZoneStoreName);
         System.out.println("shiftCapacity " + shiftCapacity);

        Topology topology = createTopology();
        streams = new KafkaStreams(topology, configure());
        streams.start();

        currentCarParkStatus.put("General", GetCarParkCapacity("General", generalCapacity));
        currentCarParkStatus.put("Shift", GetCarParkCapacity("Shift", shiftCapacity));
        currentCarParkStatus.put("Reserved", GetCarParkCapacity("Reserved", reservedCapacity));
    }

    public Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> epgStream = builder.stream(topicsConfiguration.getBarrierEventTopic(), Consumed.with(Serdes.String(), Serdes.String()));

        epgStream.map((key, value) -> new KeyValue<>(getKey(value), transform(value)))
                .to(topicsConfiguration.getAvailabilityOutputTopic(), Produced.with(Serdes.String(), Serdes.String()));

        return builder.build();
    }

    private Properties configure(){
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        Properties properties = new Properties();
        properties.putAll(props);

        return properties;
    }

    private String getKey(String value){
        try {
            BarrierEvent barrierEvent = objectMapper.readValue(value, BarrierEvent.class);
            switch(barrierEvent.getBarrierType()){
                case "General":
                    return "0";
                case "Shift":
                    return "1";
                case "Reserved":
                    return "2";
            }
        }catch(IOException ex){
            System.out.println(ex);
        }
        return "3";
    }

    private String transform(String value) {
        try {
            BarrierEvent barrierEvent = objectMapper.readValue(value, BarrierEvent.class);
            CarParkCapacity carParkCapacity = currentCarParkStatus.get(barrierEvent.getBarrierType());
            int currentStatus = carParkCapacity.getCurrentCapacity();
            if(barrierEvent.isEntry())
                currentStatus--;
            else
                currentStatus++;

            CarParkStatus status = new CarParkStatus();
            status.setCarParkTypeId(barrierEvent.getBarrierType());
            if(currentStatus <= 0){
                status.setCount("FULL");
                carParkCapacity.setCurrentCapacity(0);
            }
            else if(currentStatus >= carParkCapacity.getMaxCapacity()){
                status.setCount(Integer.toString(carParkCapacity.getMaxCapacity()));
                carParkCapacity.setCurrentCapacity(carParkCapacity.getMaxCapacity());
            }
            else{
                status.setCount(Integer.toString(currentStatus));
                carParkCapacity.setCurrentCapacity(currentStatus);
            }

            return objectMapper.writeValueAsString(status);

        }catch(IOException ex){
            System.out.println(ex);
        }

        return "";
    }

    private CarParkCapacity GetCarParkCapacity(String label, String maxCapacity){
        CarParkCapacity capacity = new CarParkCapacity();
        capacity.setCarParkTypeId(label);
        capacity.setMaxCapacity(Integer.parseInt(maxCapacity));
        capacity.setCurrentCapacity(Integer.parseInt(maxCapacity));

        return capacity;
    }

    @PreDestroy
    public void cleanUp(){
        streams.close();
        streams.cleanUp();
    }
}
