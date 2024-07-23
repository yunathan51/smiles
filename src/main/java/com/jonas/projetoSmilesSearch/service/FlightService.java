package com.jonas.projetoSmilesSearch.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

@Service
public class FlightService {

    private static final String API_URL_TEMPLATE = "https://api-air-flightsearch-green.smiles.com.br/v1/airlines/search?cabin=ALL&originAirportCode=%s&destinationAirportCode=%s&departureDate=%s&adults=%d";
    private static final OkHttpClient client = new OkHttpClient();

    public List<Map<String, Object>> searchFlights(String originAirportCode, String destinationAirportCode, String departureDate, int adults) {
        String url = String.format(API_URL_TEMPLATE, originAirportCode, destinationAirportCode, departureDate, adults);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("X-Api-Key", "aJqPU7xNHl9qN3NVZnPaJ208aPo2Bh2p2ZV844tw")
                .addHeader("Origin", "https://www.smiles.com.br")
                .addHeader("Region", "BRASIL")
                .addHeader("Referer", "https://www.smiles.com.br/")
                .addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
                .addHeader("Language", "pt-BR")
                .addHeader("Channel", "Web")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody;
                if ("gzip".equalsIgnoreCase(response.header("Content-Encoding"))) {
                    GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().byteStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputStream));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    responseBody = sb.toString();
                    System.out.println("sucesso");
                } else {
                    responseBody = response.body().string();
                    System.out.println("falhou");
                }

                // Processa o JSON e retorna uma lista de mapas
                return parseJsonResponse(responseBody);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private List<Map<String, Object>> parseJsonResponse(String jsonResponse) {
        JsonObject json = new Gson().fromJson(jsonResponse, JsonObject.class);
        JsonArray flightSegmentList = json.getAsJsonArray("requestedFlightSegmentList");
        System.out.println(flightSegmentList.size());

        List<Map<String, Object>> flights = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (JsonElement segmentElement : flightSegmentList) {
            JsonObject segment = segmentElement.getAsJsonObject();
            JsonArray flightList = segment.getAsJsonArray("flightList");

            for (JsonElement flightElement : flightList) {
                JsonObject flight = flightElement.getAsJsonObject();
                JsonObject departure = flight.getAsJsonObject("departure");
                JsonObject arrival = flight.getAsJsonObject("arrival");
                JsonArray fareList = flight.getAsJsonArray("fareList");

                String departureAirportCode = departure.getAsJsonObject("airport").get("code").getAsString();
                String arrivalAirportCode = arrival.getAsJsonObject("airport").get("code").getAsString();
                String departureTime = LocalDateTime.parse(departure.get("date").getAsString()).format(formatter);
                String arrivalTime = LocalDateTime.parse(arrival.get("date").getAsString()).format(formatter);

                if (fareList.size() > 1) {
                    System.out.println(fareList);
                    JsonObject secondFare = fareList.get(1).getAsJsonObject();
                    int miles = secondFare.get("miles").getAsInt();
                    double costTax = secondFare.getAsJsonObject("g3").get("costTax").getAsDouble();

                    // Cria um mapa para cada voo
                    Map<String, Object> flightData = Map.of(
                            "departureAirportCode", departureAirportCode,
                            "arrivalAirportCode", arrivalAirportCode,
                            "departureTime", departureTime,
                            "arrivalTime", arrivalTime,
                            "miles", miles,
                            "costTax", costTax
                    );
                    flights.add(flightData);
                }
            }
        }

        return flights;
    }
}
