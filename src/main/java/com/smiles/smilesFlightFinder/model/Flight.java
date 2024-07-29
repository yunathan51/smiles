package com.smiles.smilesFlightFinder.model;

import lombok.Data;

@Data
public class Flight {

    private String departureAirportCode;
    private String arrivalAirportCode;
    private String departureDate;
    private String arrivalDate;
    private Integer miles;
    private Double costTax;

}
