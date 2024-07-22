package com.jonas.projetoSmilesSearch.model;

import lombok.Data;

@Data
public class Flight {
    private String departureAirportCode;
    private String departureTime;
    private String arrivalAirportCode;
    private String arrivalTime;
    private int miles;
    private double costTax;

}
