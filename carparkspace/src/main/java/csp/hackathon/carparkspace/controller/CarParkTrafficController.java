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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String helloWorld(){
        return "Hello World!";
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> sendMessage(@RequestBody String message){
        try {
            if(parkingService.ParkCar()) {
                return ResponseEntity.status(HttpStatus.OK).body(String.format("Message sent is %s", message));
            }
            return ResponseEntity.status(HttpStatus.INSUFFICIENT_SPACE_ON_RESOURCE).body(String.format("No space for the car and Message sent is %s", message));

        }
        catch( Exception ex)
        {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(ex.getMessage());
        }
    }
}
