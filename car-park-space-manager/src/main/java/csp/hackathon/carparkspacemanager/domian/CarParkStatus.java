package csp.hackathon.carparkspacemanager.domian;

import lombok.Data;

@Data
public class CarParkStatus {
    /**
     Enumerated Type: General, Shift, Reserved
     */
    private String carParkTypeId;
    /**
     * count = FULL (if used == total) else count = used
     */
    private String count;
}
