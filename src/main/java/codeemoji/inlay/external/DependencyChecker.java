package codeemoji.inlay.external;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DependencyChecker {

    private static final String API_URL = "https://ossindex.sonatype.org/api/v3/component-report";
    private static final String API_TOKEN = "d8b039a32f7113c9d23e5baa798322a1e92c2202";

    public static JSONObject checkDependency(String dependency) {
        JSONObject jsonResponse = null;
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Crea il JSON per la richiesta
            JSONArray coordinates = new JSONArray();
            coordinates.put("pkg:maven/" + dependency);

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("coordinates", coordinates);

            // Scrivi il JSON nella richiesta
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonRequest.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Leggi la risposta
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
            } else {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                throw new RuntimeException("Errore nella richiesta: " + responseCode + " - " + response.toString());
            }

            connection.disconnect();

            // Determina se la risposta è un JSONArray o un JSONObject
            String responseString = response.toString();
            if (responseString.startsWith("[")) {
                String subString = responseString.substring(1, responseString.length() - 1);
                jsonResponse = new JSONObject(subString);
            }
            else {
                jsonResponse = new JSONObject(responseString);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la connessione all'API OSS Index", e);
        }
        return jsonResponse;
    }
}
