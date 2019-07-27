package csp.hackathon.carparkspace.services;

import csp.hackathon.carparkspace.configuration.KafkaConfiguration;
import csp.hackathon.carparkspace.domain.BarrierEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

import static csp.hackathon.carparkspace.serde.StreamSerdes.barrierEventSerde;
import static csp.hackathon.carparkspace.serde.StreamSerdes.stringSerde;

@Service
public class VehicleParkingProducerImpl implements VehicleParkingProducer {
    @Autowired
    private KafkaConfiguration kafkaConfiguration;

    private Producer<String, String> producer;

    @PostConstruct
    private void initialise(){
        producer = createProducer();
    }

    private Producer<String, String> createProducer(){
        return new KafkaProducer<>(getProperties());
    }

    private Properties getProperties(){
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.getBrokerUrl());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, kafkaConfiguration.getApplicationId());
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }

    public void sendMessage(String topic, String message){
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, "1", message);
        producer.send(record);
    }
}
