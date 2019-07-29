package csp.hackathon.carspacesbroadcaster;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;

public class BroadcasterService implements Runnable {

    private Thread worker;

    private AtomicBoolean isThreadRunning;
    private KafKaConfiguration kafKaConfiguration;
    private String webPortalEndpoint;
    public BroadcasterService(KafKaConfiguration kafKaConfiguration, String webPortalEndpoint){
        this.kafKaConfiguration = kafKaConfiguration;
        this.webPortalEndpoint = webPortalEndpoint;
        this.isThreadRunning = new AtomicBoolean(true);
    }

    public void Start(){
        worker = new Thread(this);
        worker.start();
    }

    public void Stop(){
        isThreadRunning.set(false);
    }

    private Consumer<String, String> createConsumer(){
        Properties properties = new Properties();
        properties.put(BOOTSTRAP_SERVERS_CONFIG, kafKaConfiguration.getBrokerUrl());
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafKaConfiguration.getGroupId());
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafKaConfiguration.getResetConfig());
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        return consumer;
    }

    public void run() {
        Consumer<String, String> consumer = createConsumer();
        consumer.subscribe(Arrays.asList(new String[]{ kafKaConfiguration.getTopic() }));

        try {
            while (isThreadRunning.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(Integer.parseInt(kafKaConfiguration.getPollDuration())));
                int counter = 0;
                for(ConsumerRecord<String, String> record : records) {
                    String message = record.value();
                    System.out.println(message);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    System.out.println(dateFormat.format(date));

                    sendMessage(message);
                    counter++;
                }
                if(counter > 0)
                    consumer.commitAsync();
            }
        }catch (WakeupException e) {
            System.out.println(e.getMessage());
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        finally {
            if(consumer != null)
                consumer.close();
        }

        System.out.println(String.format("%s has exited",  kafKaConfiguration.getTopic()));
    }

    public void sendMessage(String message) {

        StringEntity entity = new StringEntity(message,
                ContentType.APPLICATION_JSON);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(this.webPortalEndpoint);
        request.setEntity(entity);

        try {
            HttpResponse response = httpClient.execute(request);
            System.out.println(response.getStatusLine().getStatusCode());
        }
        catch (IOException ex){
            System.out.println(ex);
        }
    }
}
