package csp.hackathon.carparkspacemanager.domian;

import lombok.Data;

@Data
public class CarParkSpaces {
    private String cp1General;
    private String cp1Shift;
    private String cp1Reserved;

    private String cp2General;
    private String cp2Shift;
    private String cp2Reserved;

    private String generalBays;
    private String grasshoppers;
    private String goals;
    private String wkyeGreen;
    private String wycombeHouse;
    private String syonLane;
}
