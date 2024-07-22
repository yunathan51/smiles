package com.jonas.projetoSmilesSearch;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class Main {
    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        // Não salva cookies
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return List.of(); // Não carrega cookies
                    }
                })
                .build();

        try {
            Request request = new Request.Builder()
                    .url("https://api-air-flightsearch-green.smiles.com.br/v1/airlines/search?cabin=ALL&originAirportCode=FLN&destinationAirportCode=GRU&departureDate=2024-08-29&adults=1")
                    .get()
                    .addHeader("User-Agent", "Mozilla/5.0")
                    .addHeader("X-Api-Key", "aJqPU7xNHl9qN3NVZnPaJ208aPo2Bh2p2ZV844tw")
                    .addHeader("Origin", "https://www.smiles.com.br")
                    .addHeader("Region", "BRASIL")
                    .addHeader("Referer", "https://www.smiles.com.br/")
                    .addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
                    .addHeader("Language", "pt-BR")
                    .addHeader("Channel", "Web")
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
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
                } else {
                    responseBody = response.body().string();
                }

                // Configurando JsonParser e Gson
                JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray flightSegmentList = jsonResponse.getAsJsonArray("requestedFlightSegmentList");

                Gson gson = new Gson();

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
                        String departureDateTime = departure.get("date").getAsString();
                        String arrivalDateTime = arrival.get("date").getAsString();

                        // Convertendo a data para LocalDateTime e formatando
                        LocalDateTime dateTime = LocalDateTime.parse(departureDateTime);
                        String departureTime = dateTime.format(formatter);
                        String arrivalTime = LocalDateTime.parse(arrivalDateTime).format(formatter);

                        // Verificar se há pelo menos duas entradas na fareList
                        if (fareList.size() > 1) {
                            JsonObject secondFare = fareList.get(1).getAsJsonObject();
                            int miles = secondFare.get("miles").getAsInt();
                            double costTax = secondFare.getAsJsonObject("g3").get("costTax").getAsDouble();

                            System.out.println(departureAirportCode + " " + departureTime + " -> " + arrivalTime + " " + arrivalAirportCode);
                            System.out.println("Milhas: " + miles);
                            System.out.println("Taxa: " + costTax);
                            System.out.println("POR PAX");
                            System.out.print("=====================================");
                        } else {
                            System.out.println("Não há uma segunda entrada na fareList para este voo.");
                        }
                        System.out.println();
                    }
                }
            } else {
                System.out.println("Request failed: " + response.code());
            }
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}