package csp.hackathon.carparkspacemanager.domian;

import lombok.Data;

@Data
public class CarParkCapacity {
    /**
     Enumerated Type: General, Shift, Reserved
     */
    private String carParkTypeId;

    private int currentCapacity;

    private int maxCapacity;
}
