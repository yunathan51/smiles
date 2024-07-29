package com.smiles.smilesFlightFinder.controller;


import com.smiles.smilesFlightFinder.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FlightController {


    @Autowired
    private FlightService flightService;

    @GetMapping("/search")
    public List<Map<String, Object>> searchFlights(
            @RequestParam String originAirportCode,
            @RequestParam String arrivalAirportCode,
            @RequestParam String departureDate,
            @RequestParam Integer adults) {

        return flightService.searchFlights(originAirportCode, arrivalAirportCode, departureDate, adults);
    }
}
