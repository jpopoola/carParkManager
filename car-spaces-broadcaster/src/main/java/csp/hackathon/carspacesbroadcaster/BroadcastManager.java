package csp.hackathon.carspacesbroadcaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class BroadcastManager {

    @Autowired
    private KafKaConfiguration kafKaConfiguration;

    @Value("${webPortal.endPoint}")
    private String webPortalEndpoint;
    private BroadcasterService service;

    @PostConstruct
    private void Initialise(){
        service = new BroadcasterService(kafKaConfiguration, webPortalEndpoint);
        service.Start();
    }

    @PreDestroy
    private void Stop(){
        service.Stop();
    }
}
