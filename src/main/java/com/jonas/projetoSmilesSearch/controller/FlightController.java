package com.jonas.projetoSmilesSearch.controller;

import com.jonas.projetoSmilesSearch.service.FlightService;
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
            @RequestParam String destinationAirportCode,
            @RequestParam String departureDate,
            @RequestParam int adults) {

        // Chama o serviço para buscar os voos e retorna como uma lista de mapas
        return flightService.searchFlights(originAirportCode, destinationAirportCode, departureDate, adults);
    }
}
