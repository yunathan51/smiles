import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FlightService {

    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);
    private static final String API_URL_TEMPLATE = "https://api-air-flightsearch-green.smiles.com.br/v1/airlines/search?cabin=ALL&originAirportCode=%s&destinationAirportCode=%s&departureDate=%s&adults=%d";
    private static final OkHttpClient client = new OkHttpClient();

    public List<Map<String, Object>> searchFlights(String originAirportCode, String destinationAirportCode, String departureDate, int adults) {
        logger.info("searchFlights chamado com: origin={}, destination={}, date={}, adults={}", originAirportCode, destinationAirportCode, departureDate, adults);
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
                    logger.info("Resposta GZIP: " + responseBody);  // Adiciona log da resposta GZIP
                } else {
                    responseBody = response.body().string();
                    logger.info("Resposta: " + responseBody);  // Adiciona log da resposta
                }

                // Processa o JSON e retorna uma lista de mapas
                return parseJsonResponse(responseBody);

            } else {
                logger.error("Resposta não foi bem-sucedida: " + response.code());
            }
        } catch (Exception e) {
            logger.error("Erro ao chamar API", e);
        }

        return new ArrayList<>();
    }

    private List<Map<String, Object>> parseJsonResponse(String jsonResponse) {
        JsonObject json = new Gson().fromJson(jsonResponse, JsonObject.class);
        JsonArray flightSegmentList = json.getAsJsonArray("requestedFlightSegmentList");
        logger.info("Tamanho da lista de segmentos de voo: " + flightSegmentList.size());

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
