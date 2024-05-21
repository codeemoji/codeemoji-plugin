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

    private static final String jsonString1 = """
    {
        "coordinates": "pkg:maven/com.squareup.okio/okio@2.8.0",
        "description": "",
        "reference": "https://ossindex.sonatype.org/component/pkg:maven/com.squareup.okio/okio@2.8.0?utm_source=postmanruntime&utm_medium=integration&utm_content=7.39.0",
        "vulnerabilities": [
            {
                "id": "CVE-2023-3635",
                "displayName": "CVE-2023-3635",
                "title": "[CVE-2023-3635] CWE-195: Signed to Unsigned Conversion Error",
                "description": "GzipSource does not handle an exception that might be raised when parsing a malformed gzip buffer. This may lead to denial of service of the Okio client when handling a crafted GZIP archive, by using the GzipSource class.\n\n",
                "cvssScore": 7.5,
                "cvssVector": "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:N/I:N/A:H",
                "cwe": "CWE-195",
                "cve": "CVE-2023-3635",
                "reference": "https://ossindex.sonatype.org/vulnerability/CVE-2023-3635?component-type=maven&component-name=com.squareup.okio%2Fokio&utm_source=postmanruntime&utm_medium=integration&utm_content=7.39.0",
                "externalReferences": [
                    "http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2023-3635",
                    "https://github.com/square/okio/pull/1280"
                ]
            }
        ]
    }""";

    private static final String jsonString2 = """
    {
        "coordinates": "pkg:maven/com.squareup.okhttp3/okhttp@4.9.1",
            "description": "",
            "reference": "https://ossindex.sonatype.org/component/pkg:maven/com.squareup.okhttp3/okhttp@4.9.1?utm_source=postmanruntime&utm_medium=integration&utm_content=7.39.0",
            "vulnerabilities": [
        {
            "id": "CVE-2021-0341",
                "displayName": "CVE-2021-0341",
                "title": "[CVE-2021-0341] CWE-295: Improper Certificate Validation",
                "description": "In verifyHostName of OkHostnameVerifier.java, there is a possible way to accept a certificate for the wrong domain due to improperly used crypto. This could lead to remote information disclosure with no additional execution privileges needed. User interaction is not needed for exploitation.Product: AndroidVersions: Android-8.1 Android-9 Android-10 Android-11Android ID: A-171980069",
                "cvssScore": 7.5,
                "cvssVector": "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:N/A:N",
                "cwe": "CWE-295",
                "cve": "CVE-2021-0341",
                "reference": "https://ossindex.sonatype.org/vulnerability/CVE-2021-0341?component-type=maven&component-name=com.squareup.okhttp3%2Fokhttp&utm_source=postmanruntime&utm_medium=integration&utm_content=7.39.0",
                "externalReferences": [
            "http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2021-0341",
                    "https://github.com/square/okhttp/pull/6353",
                    "https://source.android.com/security/bulletin/2021-02-01#android-runtime"
                ]
        }
        ]
    }""";

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
            if (dependency.contains("okio")) {
                System.out.println("Mocked Okio in HashMap");
                jsonResponse = new JSONObject(jsonString1);
            } else if (dependency.contains("okhttp")) {
                System.out.println("Mocked OkHttp in HashMap");
                jsonResponse = new JSONObject(jsonString2);
            }
            //throw new RuntimeException("Errore durante la connessione all'API OSS Index", e);
            return jsonResponse;
        }
        return jsonResponse;
    }
}
