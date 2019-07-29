package csp.hackathon.carparkspace.controller;

import csp.hackathon.carparkspace.services.VehicleParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/traffic")
public class CarParkTrafficController {
    @Autowired
    private VehicleParkingService parkingService;

    private final String isEntryValue = "true";
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloWorld(){
        return "Hello World!";
    }

    @RequestMapping(value = "/{barrierType}/{isEntry}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> sendMessage(@PathVariable String barrierType, @PathVariable String isEntry){
        try {
            if(parkingService.ParkCar(barrierType, isEntryValue.equals(isEntry))) {
                return ResponseEntity.status(HttpStatus.OK).body(String.format("Event for %s with direction %s", barrierType, isEntry));
            }
            return ResponseEntity.status(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE).body(String.format("No space for the %s and %s", barrierType, isEntry));

        }
        catch( Exception ex)
        {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(ex.getMessage());
        }
    }
}
